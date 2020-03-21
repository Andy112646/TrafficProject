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
public class ServletCharset extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 设置请求信息的解码格式
        req.setCharacterEncoding("utf-8");
        // 设置响应信息的编码方式
        resp.setCharacterEncoding("utf-8");
        // 设置浏览器的解码格式
        resp.setContentType("text/html;charset=utf-8");
        String uname = req.getParameter("uname");
        String pwd = req.getParameter("pwd");
        System.out.println(uname);
        System.out.println(pwd);
        // 服务器响应给客户端
        resp.getWriter().print(uname);
    }
}
