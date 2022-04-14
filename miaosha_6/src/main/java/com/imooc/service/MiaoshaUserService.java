package com.imooc.service;

import com.imooc.dao.MiaoshaUserDAO;
import com.imooc.domain.MiaoshaUser;
import com.imooc.exception.GlobalException;
import com.imooc.redis.MiaoshaUserKey;
import com.imooc.redis.RedisService;
import com.imooc.result.CodeMsg;
import com.imooc.utils.MD5Util;
import com.imooc.utils.UUIDUtil;
import com.imooc.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDAO miaoshaUserDAO;

    @Autowired
    RedisService redisService;

    /**
     * 通过主键id获取用户
     * @param id
     * @return
     */
    public MiaoshaUser getById(long id) {

        // 取缓存
        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
        if (miaoshaUser != null) {
            return miaoshaUser;
        }

        // 查询数据库
        miaoshaUser = miaoshaUserDAO.getById(id);

        // 写缓存
        if (miaoshaUser != null) {
            redisService.set(MiaoshaUserKey.getById, "" + id, miaoshaUser);
        }

        return miaoshaUser;
    }

    /**
     * 更新用户密码
     * @param token
     * @param id
     * @param newPassword
     * @return
     */
    public Boolean updatePassword(String token, Long id, String newPassword) {

        // 查询出用户
        MiaoshaUser miaoshaUser = getById(id);
        if (miaoshaUser == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        // 更新数据库中用户密码
        MiaoshaUser toBeUpdateMiaoshaUser = new MiaoshaUser();
        toBeUpdateMiaoshaUser.setId(id);
        toBeUpdateMiaoshaUser.setPassword(MD5Util.formPass2DBPass(newPassword, miaoshaUser.getSalt()));
        miaoshaUserDAO.update(toBeUpdateMiaoshaUser);

        // 修改缓存
        redisService.del(MiaoshaUserKey.getById, "" + id);  // 删除对象缓存
        miaoshaUser.setPassword(newPassword);
        redisService.set(MiaoshaUserKey.token, token, miaoshaUser);  // 更新token缓存

        return true;
    }

    public MiaoshaUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        if (miaoshaUser != null) {
            // 延长token的有效期
            addCookie(response, token, miaoshaUser);
        }

        return miaoshaUser;
    }

    public void addCookie(HttpServletResponse response, String token, MiaoshaUser miaoshaUser) {
        redisService.set(MiaoshaUserKey.token, token, miaoshaUser);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());  // 设置cookie的过期时间
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public String login(HttpServletResponse response, LoginVO loginVO) {  // 传入HttpServletResponse的目的是将cookie返回
        // 参数校验
        if (loginVO == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }

        String mobile = loginVO.getMobile();
        String formPass = loginVO.getPassword();

        // 判断手机号是否存在
        MiaoshaUser miaoshaUser = getById(Long.parseLong(mobile));
        if (miaoshaUser == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        // 验证密码
        String dbPass = miaoshaUser.getPassword();
        String dbSalt = miaoshaUser.getSalt();
        String calcPass = MD5Util.formPass2DBPass(formPass, dbSalt);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        // 生成cookie
        String token = UUIDUtil.uuid();
        addCookie(response, token, miaoshaUser);

        return token;
    }

}
