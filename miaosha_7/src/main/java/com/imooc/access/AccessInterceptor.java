package com.imooc.access;

import com.alibaba.fastjson.JSON;
import com.imooc.domain.MiaoshaUser;
import com.imooc.redis.AccessKey;
import com.imooc.redis.RedisService;
import com.imooc.result.CodeMsg;
import com.imooc.result.Result;
import com.imooc.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.thymeleaf.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Autowired
    RedisService redisService;

    /**
     * 在方法执行之前执行拦截
     * 注：拦截器比com.imooc.config.MiaoshaUserArgumentResolver先执行
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {

            MiaoshaUser miaoshaUser = getMiaoshaUser(request, response);
            UserContext.setUser(miaoshaUser);  // 将当前用户存入上下文中（上下文是通过ThreadLocal实现的，ThreadLocal是线程安全的）

            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }

            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            String key = request.getRequestURI();

            if (needLogin) {
                if (miaoshaUser == null) {
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key += "_" + miaoshaUser.getId();
            } else {
                // do nothing
            }

            AccessKey accessKey = AccessKey.withExpire(seconds);
            Integer count = redisService.get(accessKey, key, Integer.class);
            if (count == null) {  // 该用户第一次访问该接口
                redisService.set(accessKey, key, 1);
            } else if (count < maxCount){  // 该用户不是第一次访问该接口，但是没超过访问次数，不需要限流
                redisService.incr(accessKey, key);
            } else {  // 该用户不是第一次访问该接口，已经超过访问次数，需要限流
                render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }

        }
        return true;
    }

    private void render(HttpServletResponse response ,CodeMsg codeMsg) throws Exception{
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(codeMsg));
        out.write(str.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }

    private MiaoshaUser getMiaoshaUser(HttpServletRequest request, HttpServletResponse response) {
        String paramToken = request.getParameter(MiaoshaUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, MiaoshaUserService.COOKIE_NAME_TOKEN);

        if (StringUtils.isEmpty(paramToken) && StringUtils.isEmpty(cookieToken)) {
            return null;
        }

        String token = !StringUtils.isEmpty(paramToken)  ? paramToken : cookieToken;

        return miaoshaUserService.getByToken(response, token);
    }

    private String getCookieValue(HttpServletRequest request, String cookieNameToken) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieNameToken)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
