<%--
  Created by IntelliJ IDEA.
  User: LeoRio
  Date: 2019/5/7
  Time: 15:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>$Title$</title>
  </head>
  <body>
  <h3>服务器内部资源跳转的两种方式</h3>
  <h4>请求转发</h4>
  <a href="Demo01?apple=iphoneX">请求转发</a>
  <ol>
    <li>请求转发整个过程只发生了一次请求，地址栏不会发生改变</li>
    <li>请求转发可以携带请求参数</li>
    <li>请求转发后不能再次请求转发，会报500错误，通常在后面加return来避免500错误</li>
    <li>效率相对较高</li>
    <li>当资源在本服务内部时建议使用请求转发</li>
  </ol>
  </body>
</html>
