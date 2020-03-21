<%--
  Created by IntelliJ IDEA.
  User: LeoRio
  Date: 2019/5/7
  Time: 15:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>$Title$</title>
  </head>
  <body>
  <h4>请响应定向</h4>
  <a href="Demo01?apple=iphoneX">响应重定向</a>
  <ol>
    <li>响应重定向整个过程只发生了两次次请求，地址栏会发生改变</li>
    <li>响应重定向不能携带参数，但是可以把参数拼接到地址后面（不建议）</li>
    <li>响应重定向不能再次重定向,会报500错误，通常在后面加return来避免500错误</li>
    <li>效率相对较低</li>
    <li>当资源在服务器外部时，只能用响应重定向</li>
  </ol>
  当做退出登录时，用请求转发还是用重定向？
  建议用重定向
  </body>
</html>
