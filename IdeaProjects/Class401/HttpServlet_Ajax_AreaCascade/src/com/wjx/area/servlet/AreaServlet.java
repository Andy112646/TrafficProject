package com.wjx.area.servlet;

import com.google.gson.Gson;
import com.wjx.area.pojo.Area;
import com.wjx.area.service.AreaService;
import com.wjx.area.service.impl.AreaServletImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Auther:wjx
 * @Date:2019/5/14
 * @Description:com.wjx.area.servlet
 * @version:1.0
 */
public class AreaServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");

        AreaService areaService = new AreaServletImpl();
        String parentid = req.getParameter("parentid");

        List<Area> areaList = areaService.queryByParentid(parentid);
        resp.getWriter().print(new Gson().toJson(areaList));
    }
}
