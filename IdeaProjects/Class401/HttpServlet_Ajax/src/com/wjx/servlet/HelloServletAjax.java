package com.wjx.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Auther:wjx
 * @Date:2019/5/13
 * @Description:com.wjx.servlet
 * @version:1.0
 */
public class HelloServletAjax extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("HelloServletAjax.service");
        // 响应
        resp.getWriter().println("Hello Ajax!");
    }
}
