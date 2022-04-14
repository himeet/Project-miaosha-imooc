package com.imooc.controller;

import com.imooc.result.Result;
import com.imooc.service.MiaoshaUserService;
import com.imooc.vo.LoginVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid  LoginVO loginVO) {

        log.info(loginVO.toString());

        // 登录
        miaoshaUserService.login(response, loginVO);
        return Result.success(true);
    }
}
