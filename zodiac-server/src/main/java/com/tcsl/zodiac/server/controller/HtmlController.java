package com.tcsl.zodiac.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @className: HtmlController
 * @description:
 * @author: yang pan
 */
@Controller
public class HtmlController {


    @GetMapping({"/"})
    public String home() {
        return "home";
    }


}
