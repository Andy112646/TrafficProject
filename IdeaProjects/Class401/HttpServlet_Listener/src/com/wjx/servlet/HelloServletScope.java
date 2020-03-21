package com.wjx.servlet;

/**
 * @Auther:wjx
 * @Date:2019/5/13
 * @Description:com.wjx.servlet
 * @version:1.0
 */

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 监听器主要是监听三个对象的创建和销毁，还有它的作用域变化
 * 三个对象（ServletRequest，HttpSession,ServletContext）
 * 每个对象都有一个创建的监听方法和一个销毁的监听方法(create,destory)
 * 每个对象都有三个作用域的监听方法(add,remove,replace)
 * 3*5=15个方法
 */
public class HelloServletScope extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.setAttribute("name", "zhangsan");
        session.setAttribute("name", "lisi");
        session.removeAttribute("name");
        // 清除作用域会调用remove方法
        session.invalidate();
    }
}
