package com.wjx.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Auther:wjx
 * @Date:2019/5/7
 * @Description:com.wjx.servlet
 * @version:1.0
 *
 * HttpServletResponse:响应对象也是由servlet容器创建，这个对象包含了所有的http协议的响应信息
 *  http协议的响应信息
 *      响应行 ：协议和版本号，状态码（200,404,405,500），状态码描述（ok）
 *      响应头 ：若干响应头
 *      响应实体内容 ：由程序员来写（html,text）
 */
public class ServletResponse extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 响应行 ：协议和版本号，状态码（200,404,405,500），状态码描述（ok）--不需要程序员来负责，服务会自动加上
        // 响应头 ：若干响应头-不需要程序员来负责，服务会自动加上
        // setHeader设置的响应头，同名的响应头会被覆盖
        resp.setHeader("Apple", "iphoneX");
        resp.setHeader("Apple", "iphoneXR");

        // addHeader添加响应头，同名的响应头不会被覆盖
        resp.addHeader("HuaWei", "Mate30");
        resp.addHeader("HuaWei", "P30pro");

        // 设置响应头处理乱码,告诉浏览器要用utf-8解码
        // 两种写法
        //resp.setHeader("content-type","text/html;charset=utf-8");
        resp.setContentType("text/html;charset=utf-8");
        // 响应实体内容 ：由程序员来写（html,text）
        resp.getWriter().print("星期天学习!");
        resp.getWriter().print("<html>");
        resp.getWriter().print("<head>");
        resp.getWriter().print("</head>");
        resp.getWriter().print("<body>");
        resp.getWriter().print("<font style='color:red'>");
        resp.getWriter().print("星期天学习!");
        resp.getWriter().print("</font>");
        resp.getWriter().print("</body>");
        resp.getWriter().print("</html>");
    }
}
