package com.wjx.servlet;

import com.wjx.pojo.User;
import com.wjx.service.UserService;
import com.wjx.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Auther:wjx
 * @Date:2019/5/6
 * @Description:com.wjx.servlet
 * @version:1.0
 */
public class Login extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = new User();
        user.setUsername(req.getParameter("uname"));
        user.setPassword(req.getParameter("pwd"));
        UserService userService = new UserServiceImpl();
        if (userService.query4Login(user)){
            resp.getWriter().print("Login successful!");
        }else{
            resp.getWriter().print("Login failed!");
        }
    }
}
