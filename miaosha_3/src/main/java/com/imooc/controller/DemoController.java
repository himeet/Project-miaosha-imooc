package com.imooc.controller;

import com.imooc.domain.User;
import com.imooc.redis.RedisService;
import com.imooc.redis.UserKey;
import com.imooc.result.CodeMsg;
import com.imooc.result.Result;
import com.imooc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    // 1.rest接口
    @GetMapping(value = "/success")
    @ResponseBody
    public Result<String> success() {
        return Result.success("hello success");
    }

    @GetMapping(value = "/error")
    @ResponseBody
    public Result<String> error() {
        return Result.error(CodeMsg.SERVER_ERROR);
    }


    // 2.页面
    @GetMapping(value = "/thymeleaf")
    public String thymeleaf(Model model) {  // 当使用thymeleaf的时候，返回的结果是String类型，代表模板的名称
        model.addAttribute("name", "Andy");
        return "hello";  // hello代表模板的名称
    }

    @GetMapping(value = "/db/get")
    @ResponseBody
    public Result<User> dbGet() {  // 当使用thymeleaf的时候，返回的结果是String类型，代表模板的名称

        User user = userService.getUserById(1);

        return Result.success(user);
    }

    /**
     * 测试数据库事务
     * @return
     */
    @GetMapping(value = "/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx() {  // 当使用thymeleaf的时候，返回的结果是String类型，代表模板的名称
        userService.tx();
        return Result.success(true);
    }

    @GetMapping(value = "/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
        User user = redisService.get(UserKey.getById, "" + 1, User.class);
        return Result.success(user);
    }

    @GetMapping(value = "/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {

        User user = new User();
        user.setId(1);
        user.setName("Mike");
        boolean ret = redisService.set(UserKey.getById, "" + 1, user);

        return Result.success(ret);
    }

}
