package com.imooc.controller;

import com.imooc.domain.MiaoshaUser;
import com.imooc.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    private static Logger log = LoggerFactory.getLogger(GoodsController.class);

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> userInfo(Model model, MiaoshaUser miaoshaUser) {
        return Result.success(miaoshaUser);
    }
}
