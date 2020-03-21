package com.wjx.area.dao;

import com.wjx.area.pojo.Area;

import java.util.List;

/**
 * @Auther:wjx
 * @Date:2019/5/14
 * @Description:com.wjx.area.dao.impl
 * @version:1.0
 */
public interface AreaDao {
    /**
     * 根据上级城市编号查询城市信息
     *
     * @param parentid
     * @return
     */
    List<Area> selectByParentid(String parentid);
}
