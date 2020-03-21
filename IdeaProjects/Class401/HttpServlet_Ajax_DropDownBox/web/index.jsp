<%--
  Created by IntelliJ IDEA.
  User: LeoRio
  Date: 2019/5/15
  Time: 11:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>$Title$</title>
    <style type="text/css">
        #divinput {
            border: solid 2px darkseagreen;
            height: 30px;
            width: 260px;
        }

        #inputid {
            border: none;
            outline: none;
            height: 30px;
            font-size: 20px;
        }

        #divcontent {
            width: 260px;
            border: solid 2px mediumturquoise;
            border-top: none;
            border-bottom: none;
        }

        #divcontent div {
            border-bottom: solid 1px lightgrey;
        }

        #divcontent div:last-child {
            border-bottom: solid 2px mediumturquoise;
        }

        .bgcolor{
            color: white;
            background-color: lightblue;
        }
    </style>
    <script type="text/javascript" src="js/jquery-3.4.0.js"></script>
    <script type="text/javascript">

        // 给页面绑定键盘按下触发的事件
        $(document).keydown(function () {
            // 判断下拉框是否有内容
            if($("#divcontent").html().trim()==""){
                // 没有内容则隐藏并且清空
                $("#divcontent").hide().html("");
                return;
            }else{
                // 监控用户键盘按下事件
                var eve = window.event || event;
                if(eve.keyCode==38){ // 背景颜色向上移动
                    // 让input输入框失去焦点
                    $("#inputid").blur();
                    // 判断背景颜色的同级哥哥元素(当前元素以前的元素)有多少个
                    var varlength = $(".bgcolor").prevAll().length;
                    if(varlength==0){ // 一开始时，没有一个div有背景色
                        // 添加背景颜色时先移除背景色
                        $(".divclass").removeClass("bgcolor");
                        // 给最后一项添加背景色
                        $(".divclass").eq($(".divclass").length-1).addClass("bgcolor");
                    }else{
                        // 清除当前div背景色，给前面一个div添加背景色
                        $(".divclass").removeClass("bgcolor");
                        $(".divclass").eq(varlength-1).addClass("bgcolor");
                    }
                }else if(eve.keyCode==40){ // 背景色向下移动
                    // 让inut输入框框失去焦点
                    $("#inputid").blur();
                    // 判断带divclass的div是否带背景色
                    if($(".divclass").hasClass("bgcolor")){
                        // 判断背景颜色的同级哥哥元素(当前元素以前的元素)有多少个
                        var varlength = $(".bgcolor").prevAll().length;
                        // 判断带背景色的元素是否是最后一个
                        if(varlength==($(".divclass").length-1)){
                            // 是最后一个元素,先移除背景颜色再给下一个（第一个元素）添加背景颜色
                            $(".divclass").removeClass("bgcolor").eq(0).addClass("bgcolor");
                        }else{
                            $(".divclass").removeClass("bgcolor").eq(varlength+1).addClass("bgcolor");
                        }
                    }else{
                        // div都没有带背景色
                        $(".divclass").removeClass("bgcolor").eq(0).addClass("bgcolor");
                    }
                }else if (eve.keyCode==13){ // 回车键
                    // 获取带背景色的文本内容，放到input输入框里面，清空并隐藏下拉列表
                    if($(".divclass").hasClass("bgcolor")){
                        var vartext = $(".bgcolor").text();
                        $("#inputid").val(vartext);
                        // 隐藏并清除下拉列表
                        $("#divcontent").hide().html("")
                    }
                }
            }
        })

        // 鼠标移动添加背景颜色
        function addbgcolor(obj) {
            // 先移除背景颜色
            $(".divclass").removeClass("bgcolor");
            // 再添加背景色
            $(obj).addClass("bgcolor");
            $("#inputid").val($(obj).text());
        }

        // 键盘按下触发的事件
        function baidusuggest() {
            var varinputval = $("#inputid").val().trim();

            if (varinputval.length == 0){
                // 隐藏下拉框并清除内容
                $("#divcontent").hide().html("");
                return;
            } else {
                // 把关键字发送给服务器做查询操作
                $.getJSON("HelloBaidu",{"keyword":varinputval},function (res) {
                    var varhtml = "";
                    // 只显示5条数据
                    for (var i = 0; i < res.length; i++) {
                        if(i<5){
                            varhtml += "<div class='divclass' onmousemove='addbgcolor(this)'>"+res[i]+"</div>";
                        }
                    }

                    if (varhtml==""){
                        $("#divcontent").hide().html("");
                    } else {
                        // 添加到divcontent
                        $("#divcontent").show().html(varhtml);
                    }
                });
            }
        }
    </script>

</head>
<body>
<div id="divinput"><input type="text" id="inputid" name="key" value="" onkeyup="baidusuggest()"></div>
<div id="divcontent">
</div>
</body>
</html>
