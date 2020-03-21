<%--
  Created by IntelliJ IDEA.
  User: LeoRio
  Date: 2019/5/14
  Time: 19:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>省市县Ajax三级联动</title>
    <style type="text/css">
        select {
            width: 150px;
            font-size: 16px;
        }
    </style>
    <script type="text/javascript" src="static/js/jquery-3.4.0.js"></script>
    <script type="text/javascript">
        $(function () {
            $.getJSON("AreaServlet",{"parentid":0},function (res) {
                var varsel = $("#provinceid");
                for (var i = 0; i < res.length; i++) {
                    var varoption = $("<option value=" + res[i].areaid + ">" + res[i].areaname + "</option>");
                    varsel.append(varoption);
                }
                queryAllCity();
            })
        })
        
        // 查询当前省份的所有市
        function queryAllCity() {
            var parentid = $("#provinceid").val();
            $("#cityid").empty();
            $.getJSON("AreaServlet",{"parentid":parentid},function (res) {
                var varsel = $("#cityid");
                for (var i = 0; i < res.length; i++) {
                    var varoption = $("<option value=" + res[i].areaid + ">" + res[i].areaname + "</option>");
                    varsel.append(varoption);
                }
                queryAllTown();
            })
        }

        // 查询当前市的所有县
        function queryAllTown() {
            var parentid = $("#cityid").val();
            $("#townid").empty();
            $.getJSON("AreaServlet",{"parentid":parentid},function (res) {
                var varsel = $("#townid");
                for (var i = 0; i < res.length; i++) {
                    var varoption = $("<option value=" + res[i].areaid + ">" + res[i].areaname + "</option>");
                    varsel.append(varoption);
                }
            })
        }
    </script>

</head>
<body style="font-size: 16px">
    省：<select name="province" id="provinceid" onchange="queryAllCity()"></select>
    市：<select name="city" id="cityid" onchange="queryAllTown()"></select>
    县：<select name="town" id="townid"></select>
</body>
</html>
