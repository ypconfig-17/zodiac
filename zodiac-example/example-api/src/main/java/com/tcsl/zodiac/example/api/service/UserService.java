package com.tcsl.zodiac.example.api.service;

import com.tcsl.zodiac.example.api.model.User;

/**
 * @className: UserService
 * @description:
 * @author: yang pan
 */
public interface UserService {


   void addUser(User user);


   User getUser(String username);
}
