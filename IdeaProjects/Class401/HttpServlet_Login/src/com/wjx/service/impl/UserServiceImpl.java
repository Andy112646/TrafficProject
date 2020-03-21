package com.wjx.service.impl;

import com.wjx.dao.UserDao;
import com.wjx.dao.impl.UserDaoImpl;
import com.wjx.pojo.User;
import com.wjx.service.UserService;

/**
 * @Auther:wjx
 * @Date:2019/5/6
 * @Description:com.wjx.service.impl
 * @version:1.0
 */
public class UserServiceImpl implements UserService {
    UserDao userDao = new UserDaoImpl();
    @Override
    public boolean query4Login(User user) {
        return userDao.select4Login(user);
    }
}
