<%--
  Created by IntelliJ IDEA.
  User: LeoRio
  Date: 2019/5/14
  Time: 14:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>客户端接收服务器响应的数据</title>
    <script type="text/javascript">
        // 1、创建ajax对象
        function createAjax() {
            var xmlrequest = null;
            if(window.XMLHttpRequest){ //谷歌，火狐
                xmlrequest = new XMLHttpRequest();
            }else if(window.ActiveXObject){//IE老版本
                xmlrequest = new ActiveXObject("Msxml2.XMLHTTP");
            }else{
                alert("你当前的浏览器不支持ajax")
            }
            return xmlrequest;
        }

        // ajax发送post请求并携带请求参数
        function AjaxResponseTest() {
            // 1、创建ajax
            var xmlrequest = createAjax();
            // 2、声明请求参数
            xmlrequest.open("post","HelloServletAjaxResponse",true);
            // 3、写回调函数接收服务器响应的数据
            xmlrequest.onreadystatechange = function () {
                if(xmlrequest.readyState==4 && xmlrequest.status==200){
                    // (1) 接收服务器响应的text文本信息
                    console.info(xmlrequest.responseText);
                }
            }
            xmlrequest.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
            xmlrequest.send("uname=李四&pwd=abc");
        }
    </script>
</head>
<body>
    <input type="button" value="ajax接收服务器响应的数据" onclick="AjaxResponseTest()"/>
</body>
</html>
