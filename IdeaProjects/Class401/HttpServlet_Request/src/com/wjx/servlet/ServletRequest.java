package com.wjx.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * @Auther:wjx
 * @Date:2019/5/7
 * @Description:com.wjx.servlet
 * @version:1.0
 *
 * HttpServletRequest:由servlet容器创建这个对象，该对象包含所有的http协议的请求信息
 * http协议的请求信息
 *      请求行 ：请求方式，资源路径，协议和版本号
 *      请求头 ：若干请求头
 *      请求实体内容 ：发送过来的内容（用户名，密码）
 */
public class ServletRequest extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取请求行信息
        //getReqLine(req, resp);
        
        // 请求头里面的信息
        //getReqHeader(req, resp);
        
        // 请求的实体内容
        getContext(req, resp);
    }

    // 请求的实体内容
    private void getContext(HttpServletRequest req, HttpServletResponse resp) {
        // 获取用户和密码
        // 用户名什么都不写获取的是空字符串""
        // 如果servlet获取的参数的名称和jsp页面的name的属性值不一致，获取的结果也是null
        String uname = req.getParameter("uname");
        String pwd = req.getParameter("pwd");
        System.out.println("uname:"+uname);

        // 获取性别
        String sex = req.getParameter("sex");
        System.out.println("sex:"+sex);

        // 获取爱好
        String[] favs = req.getParameterValues("fav");
        System.out.println("fav:"+ Arrays.toString(favs));

        // 遍历数组前要判断是否为空，否则容易报错
        if (favs!=null && favs.length>0){
            for (int i = 0; i < favs.length; i++) {
                System.out.println(favs[i]);
            }
        }
    }

    // 请求头里面的信息
    private void getReqHeader(HttpServletRequest req, HttpServletResponse resp) {
        // 获取单个请求头信息
        String header = req.getHeader("User-Agent");
        System.out.println(header);

        // 获取多个请求头，是枚举类型的请求头名称
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String s = headerNames.nextElement();
            System.out.println(s+":"+req.getHeader(s));
        }

    }

    // 获取请求行的信息
    private void getReqLine(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("请求方式："+req.getMethod());
        System.out.println("URI："+req.getRequestURI());
        System.out.println("URL："+req.getRequestURL());
        System.out.println("协议："+req.getScheme());
        System.out.println("版本号："+req.getProtocol());
        System.out.println("获取项目的访问路径："+req.getContextPath());
    }
}
