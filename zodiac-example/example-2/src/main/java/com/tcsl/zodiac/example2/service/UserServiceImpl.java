package com.tcsl.zodiac.example2.service;

import com.tcsl.zodiac.example.api.model.User;
import com.tcsl.zodiac.example.api.service.UserService;
import com.tcsl.zodiac.rpc.spring.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * @className: UserServiceImpl
 * @description:
 * @author: yang pan
 */

@RpcService
@Slf4j
public class UserServiceImpl implements UserService , EnvironmentAware {

    private static final Map<String, User> userRepository = new HashMap<>();

    @Autowired
    RoleService roleService;

    @Override
    public void addUser(User user) {
        log.info("用户保存入参user:{}", user);
        userRepository.put(user.getUsername(), user);

    }

    @Override
    public User getUser(String username) {
        log.info("查询用户入参user:{}", username);
        User user = new User();
        user.setUsername("杨攀");
        user.setPassword("123");
        return user;

    }
    Environment environment;
    @Override
    public void setEnvironment(Environment environment) {
       this. environment = environment;
    }
}
