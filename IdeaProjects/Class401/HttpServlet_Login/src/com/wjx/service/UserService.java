package com.wjx.service;

import com.wjx.pojo.User;

/**
 * @Auther:wjx
 * @Date:2019/5/6
 * @Description:com.wjx.service
 * @version:1.0
 */
public interface UserService {
    /**
     * 根据用户名和密码查询用户信息
     *
     * @param user
     * @return
     */
    boolean query4Login(User user);
}
