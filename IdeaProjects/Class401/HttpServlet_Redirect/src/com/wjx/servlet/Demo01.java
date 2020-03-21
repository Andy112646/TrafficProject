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
public class Demo01 extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String apple = req.getParameter("apple");
        System.out.println("Demo01.service:"+apple);
        //请求重定向不能携带参数,但可以将值拼接地址里（不建议这样写）
        resp.sendRedirect("Demo02?apple="+apple);
    }
}
