package com.tcsl.zodiac.example1.controller;

import com.tcsl.zodiac.example.api.model.User;
import com.tcsl.zodiac.example.api.service.UserService;
import com.tcsl.zodiac.rpc.spring.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @className: ExampleController
 * @description:
 * @author: yang pan
 */


@RestController
@Slf4j
public class ExampleController {

    @RpcReference(serverUrl = "example2")
    UserService  userService;

    @RequestMapping("/getUser")
    public User  getUser(){
        User user = userService.getUser("yangpan");
        log.info("rpc 返回结果 user:{}",user);
        return user;
    }

 }
