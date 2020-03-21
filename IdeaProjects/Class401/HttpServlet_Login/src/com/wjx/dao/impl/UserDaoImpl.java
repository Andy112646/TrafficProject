package com.wjx.dao.impl;

import com.wjx.dao.UserDao;
import com.wjx.pojo.User;
import com.wjx.util.DBUtil;

/**
 * @Auther:wjx
 * @Date:2019/5/6
 * @Description:com.wjx.dao.impl
 * @version:1.0
 */
public class UserDaoImpl implements UserDao {
    @Override
    public boolean select4Login(User user) {
        String sql = "select * from tb_user where username=? and password=?";
        return DBUtil.executeQuery(User.class, sql, user.getUsername(), user.getPassword()).size()> 0 ? true:false;
    }
}
