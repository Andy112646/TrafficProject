package com.wjx.listener;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Auther:wjx
 * @Date:2019/5/13
 * @Description:com.wjx.listener
 * @version:1.0
 */
public class RequestListener implements ServletRequestListener {
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        ServletRequest servletRequest = sre.getServletRequest();
        HttpServletRequest req = (HttpServletRequest) servletRequest;

        String sysdate = new Date().toLocaleString();
        Object username = req.getSession().getAttribute("username");
        if (username == null) {
            username = "用户未登录";
        }

        String url = req.getRequestURL().toString();
        String method = req.getMethod();
        String remoteUser = req.getRemoteAddr();
        int remotePort = req.getRemotePort();
        String message = sysdate + "  " +remoteUser+":"+remotePort+"  "+ username + "，访问路径：" + url + "，请求方式：" + method;
        System.out.println(message);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String nowdate = sdf.format(new Date());

        File file = new File("D:/" + nowdate + ".txt");
        PrintWriter pw = null;
        try {
            FileWriter fw = new FileWriter(file, true);
            pw = new PrintWriter(fw);
            pw.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }


    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {

    }
}
