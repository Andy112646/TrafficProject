package com.wjx.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Auther:wjx
 * @Date:2019/5/7
 * @Description:${PACKAGE_NAME}
 * @version:1.0
 */
public class Demo03 extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String apple = req.getParameter("apple");
        System.out.println("Demo03.service:"+apple);
        // 请求转发之后不能再次请求转发，会报500错误，通常在后面加return来避免500错误
        // req.getRequestDispatcher("Demo03").forward(req,resp);
    }
}
