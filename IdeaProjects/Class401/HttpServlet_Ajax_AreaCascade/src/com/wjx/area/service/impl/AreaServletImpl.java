package com.wjx.area.service.impl;

import com.wjx.area.dao.AreaDao;
import com.wjx.area.dao.impl.AreaDaoImpl;
import com.wjx.area.pojo.Area;
import com.wjx.area.service.AreaService;

import java.util.List;

/**
 * @Auther:wjx
 * @Date:2019/5/14
 * @Description:com.wjx.area.service.impl
 * @version:1.0
 */
public class AreaServletImpl implements AreaService {
    @Override
    public List<Area> queryByParentid(String parentid) {
        AreaDao areaDao = new AreaDaoImpl();
        return areaDao.selectByParentid(parentid);
    }
}
