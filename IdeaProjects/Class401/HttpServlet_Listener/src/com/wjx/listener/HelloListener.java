package com.wjx.listener;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * @Auther:wjx
 * @Date:2019/5/13
 * @Description:com.wjx.listener
 * @version:1.0
 */
public class HelloListener implements HttpSessionListener, HttpSessionAttributeListener,
        ServletRequestListener, ServletRequestAttributeListener,
        ServletContextListener, ServletContextAttributeListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("HelloListener.sessionCreated session被创建了");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("HelloListener.sessionDestroyed session被销毁了");
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        HttpSession session = se.getSession();
        System.out.println("HelloListener.attributeAdded session作用域添加信息:"+session.getAttribute("name"));
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        HttpSession session = se.getSession();
        System.out.println("HelloListener.attributeReplaced session作用域属性被替换:" +session.getAttribute("name"));
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        System.out.println("HelloListener.attributeRemoved session作用域移除");
    }

    @Override
    public void attributeAdded(ServletContextAttributeEvent scae) {

    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent scae) {

    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent scae) {

    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    @Override
    public void attributeAdded(ServletRequestAttributeEvent srae) {

    }

    @Override
    public void attributeRemoved(ServletRequestAttributeEvent srae) {

    }

    @Override
    public void attributeReplaced(ServletRequestAttributeEvent srae) {

    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {

    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {

    }


}
