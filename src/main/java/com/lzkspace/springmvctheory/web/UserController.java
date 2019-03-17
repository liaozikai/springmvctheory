package com.lzkspace.springmvctheory.web;

import com.lzkspace.springmvctheory.dao.UserDao;
import com.lzkspace.springmvctheory.service.UserService;
import com.lzkspace.springmvctheory.annotation.Qualifier;
import com.lzkspace.springmvctheory.annotation.Controller;
import com.lzkspace.springmvctheory.annotation.RequestMapping;

/**
 * @author : liaozikai
 * file : UserController.java
 */
@Controller("userController")
@RequestMapping("/user")
public class UserController {
    
    @Qualifier("userService")
    private UserService userService;
    
    @Qualifier("userDao")
    private UserDao userDao;
    
    @RequestMapping("/insert")
    public void insert() {
        userService.insert();
        userDao.insert();
    }
    
}
