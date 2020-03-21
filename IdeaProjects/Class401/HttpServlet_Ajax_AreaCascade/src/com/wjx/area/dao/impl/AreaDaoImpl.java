package com.wjx.area.dao.impl;

import com.wjx.area.dao.AreaDao;
import com.wjx.area.pojo.Area;
import com.wjx.area.util.DBUtil;

import java.util.List;

/**
 * @Auther:wjx
 * @Date:2019/5/14
 * @Description:com.wjx.area.dao.impl.impl
 * @version:1.0
 */
public class AreaDaoImpl implements AreaDao {
    @Override
    public List<Area> selectByParentid(String parentid) {
        String sql = "select * from area where parentid = ?";
        return DBUtil.executeQuery(Area.class, sql, parentid);
    }
}
