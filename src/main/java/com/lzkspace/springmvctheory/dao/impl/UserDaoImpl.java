package com.lzkspace.springmvctheory.dao.impl;

import com.lzkspace.springmvctheory.dao.UserDao;
import com.lzkspace.springmvctheory.annotation.Repository;

/**
 * @author : liaozikai
 * file : UserDaoImpl.java
 */
@Repository("userDao")
public class UserDaoImpl implements UserDao {
    @Override
    public void insert() {
        System.out.println("execut UserDao.insert()");
    }
}
