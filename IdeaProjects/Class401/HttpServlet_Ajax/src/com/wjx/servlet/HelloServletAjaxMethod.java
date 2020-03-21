package com.wjx.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Auther:wjx
 * @Date:2019/5/14
 * @Description:com.wjx.servlet
 * @version:1.0
 */
public class HelloServletAjaxMethod extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");

        System.out.println("HelloServletAjaxMethod.service");
        String method = req.getMethod();
        String uname = req.getParameter("uname");
        String pwd = req.getParameter("pwd");
        System.out.println("HelloServletAjaxMethod.service: "+uname+" : "+pwd);
        resp.getWriter().println("Hello ajax "+method);

    }
}
