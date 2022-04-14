package com.imooc.controller;

import com.imooc.domain.MiaoshaUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/goods")
public class GoodsController {

    private static Logger log = LoggerFactory.getLogger(GoodsController.class);

    @RequestMapping("/to_list")
    public String toList(Model model, MiaoshaUser miaoshaUser) {
        model.addAttribute("user", miaoshaUser);
        return "goods_list";
    }
}
