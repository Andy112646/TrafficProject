<%--
  Created by IntelliJ IDEA.
  User: LeoRio
  Date: 2019/5/7
  Time: 10:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>$Title$</title>
</head>
<body>
<form action="ServletRequest" method="get">
    用户名：<input type="text" name="uname" value="zhangsan"> <br>
    密码：<input type="password" name="pwd" value="123"> <br>
    性别：<input type="radio" name="sex" value="1" checked>男
    <input type="radio" name="sex" value="0">女 <br>
    爱好：<input type="checkbox" name="fav" value="a">吃饭
    <input type="checkbox" name="fav" value="b">睡觉
    <input type="checkbox" name="fav" value="c">打豆豆
    <input type="submit" value="提交">
</form>
</body>
</html>
