package com.lzkspace.springmvctheory.service.impl;

import com.lzkspace.springmvctheory.dao.UserDao;
import com.lzkspace.springmvctheory.service.UserService;
import com.lzkspace.springmvctheory.annotation.Qualifier;
import com.lzkspace.springmvctheory.annotation.Service;

/**
 * @author : liaozikai
 * file : UserServiceImpl.java
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Qualifier("userDao")
    UserDao userDao;
    @Override
    public void insert() {
        System.out.println("userService.insert() start");
        userDao.insert();
        System.out.println("userService.insert() end");
    }
}
