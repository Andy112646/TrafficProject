package com.wjx.dao;

import com.wjx.pojo.User;

/**
 * @Auther:wjx
 * @Date:2019/5/6
 * @Description:com.wjx.dao
 * @version:1.0
 */
public interface UserDao {
    /**
     * 根据用户名和密码查询用户信息
     *
     * @param user
     * @return
     */
    boolean select4Login(User user);
}
