<%--
  Created by IntelliJ IDEA.
  User: LeoRio
  Date: 2019/5/7
  Time: 14:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>$Title$</title>
  </head>
  <body>
  <form action="ServletCharset" method="get">
    用户名：<input type="text" name="uname" value="张三"> <br>
    密码：<input type="password" name="pwd" value="123"> <br>
    <input type="submit" value="提交">
  </form>
  <hr>
  <h3>post请求方式的乱码处理</h3>
  <ol>
    <li>页面的编码格式是utf-8,contentType="text/html;charset=UTF-8"</li>
    <li>设置请求信息的解码格式,req.setCharacterEncoding("utf-8");</li>
    <li>设置响应信息的编码格式, resp.setCharacterEncoding("utf-8");</li>
    <li>设置浏览器的解码格式,resp.setContentType("text/html;charset=utf-8");</li>
  </ol>

  <h3>get请求方式的乱码处理</h3>
  <ol>
    <li>页面的编码格式是utf-8,contentType="text/html;charset=UTF-8"</li>
    <li>设置请求信息的解码格式,req.setCharacterEncoding("utf-8");</li>
    <li>设置响应信息的编码格式, resp.setCharacterEncoding("utf-8");</li>
    <li>设置浏览器的解码格式,resp.setContentType("text/html;charset=utf-8");</li>
    <li>需要在tomcat的servlet.xml里面配置上useBodyEncodingForURI="true"（在配置文件的第71行）</li>
  </ol>
  </body>
</html>
