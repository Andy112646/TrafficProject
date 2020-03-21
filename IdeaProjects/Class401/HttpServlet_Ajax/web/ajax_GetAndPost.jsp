<%--
  Created by IntelliJ IDEA.
  User: LeoRio
  Date: 2019/5/14
  Time: 9:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script type="text/javascript">
        // 创建ajax对象
        function createAjax() {
            var xmlrequest = null;
            if(window.XMLHttpRequest){
                xmlrequest = new XMLHttpRequest();
            }else if (window.ActiveXObject){
                xmlrequest = new ActiveXObject("Msxml2.XMLHTTP");
            } else {
                console.log("当前浏览器不支持ajax");
            }
            return xmlrequest;
        }
        // ajax发送get方式请求并携带请求参数
        function AjaxGet() {
            // 1、创建ajax对象
            var xmlrequest = createAjax();
            // 2、声明请求参数
            xmlrequest.open("get", "HelloServletAjaxMethod?uname="+encodeURIComponent("张三","utf-8")+"&pwd=abc&random="+Math.random(),true);
            // 3、写回调参数接收服务器响应的数据
            xmlrequest.onreadystatechange = function () {
                if(xmlrequest.readyState==4 && xmlrequest.status ==200){
                    alert(xmlrequest.responseText)
                }
            }
            // 4、发送请求
            xmlrequest.send(null);
        }

        // ajax发送post方式请求并携带请求参数
        function AjaxPost() {
            // 1、创建ajax对象
            var xmlrequest = createAjax();
            // 2、声明请求参数
            xmlrequest.open("post", "HelloServletAjaxMethod", true);
            // 3、写回调参数接收服务器响应的数据
            xmlrequest.onreadystatechange = function () {
                if(xmlrequest.readyState==4 && xmlrequest.status ==200){
                    alert(xmlrequest.responseText)
                }
            }
            // post请求必须添加一个请求头，否则服务器接收不到数据
            // Content-Type: application/x-www-form-urlencoded
            xmlrequest.setRequestHeader("Content-Type","application/x-www-form-urlencoded")
            // 4、发送请求
            xmlrequest.send("uname=李四&pwd=abc");
        }
    </script>
</head>
<body>
<input type="button" value="ajax的get请求" onclick="AjaxGet()"/>
<input type="button" value="ajax的post请求" onclick="AjaxPost()"/>
<hr>
<form action="HelloServletAjaxMethod" method="post">
    用户名： <input type="text" name="uname" value="李四"> <br>
    密码： <input type="password" name="pwd" value="abc"> <br>
    <input type="submit" value="提交">
</form>
<hr>
<h3>ajax的get方式请求</h3>
<ol>
    <li>请求参数必须要放在url地址后面用?隔开</li>
    <li>针对IE浏览器中文乱码，必须要给中进行utf-8编码 encodeURIComponent("张三","utf-8")</li>
    <li>get可能会走缓存（服务器检测到两次访问的请求是同一个url），但是可以让它不走缓存（改变url地址）</li>
    <li>get方式请求只能携带少量数据2-4K</li>
    <li>get请求方式一般做查询</li>
</ol>

<h3>ajax的post方式请求</h3>
<ol>
    <li>请求参数必须要放在send方法里面</li>
    <li>post请求方式不会走缓存</li>
    <li>post方式请求携带的数据理论上没有限制，但是实际上服务器会做限制</li>
    <li>一般做DML操作时用post请求</li>
</ol>
</body>
</html>
