<%--
  Created by IntelliJ IDEA.
  User: LeoRio
  Date: 2019/5/13
  Time: 16:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>$Title$</title>
    <script type="text/javascript">
      function createAjax() {
        // 1、创建ajax对象
        var xmlrequest = null;
        if (window.XMLHttpRequest){ // 火狐，谷歌-网景内核
          xmlrequest = new XMLHttpRequest();
        } else if(window.ActiveXObject){ // IE老版本
          xmlrequest = new ActiveXObject("Msxml2.XMLHTTP");
        }else {
          console("你当前的浏览器不支持ajax");
        }

        // 2、声明请求参数： 请求方式(GET/POST)、url、是否异步请求(默认是异步请求true)
        xmlrequest.open("get", "HelloServletAjax", false);

        // 3、声明一个回调函数来接收服务器响应的数据(防止数据丢失)
        xmlrequest.onreadystatechange = function () {
          // 获取服务器响应的数据
          alert(xmlrequest.responseText);
        }

        // 4、发送ajax给服务器,null为了浏览器的兼容性
        xmlrequest.send(null);

      }
    </script>
  </head>
  <body>
  <input type="button" value="测试ajax" onclick="createAjax()"><br><br>
  <a href="./ajax_GetAndPost.jsp">ajax的GET请求和POST请求</a><br><br>
  <a href="./ajax_respnoseData.jsp">ajax接收服务器响应的数据</a><br><br>
  </body>
</html>
