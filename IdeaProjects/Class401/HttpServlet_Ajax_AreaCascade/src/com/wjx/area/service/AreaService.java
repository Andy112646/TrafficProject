package com.wjx.area.service;

import com.wjx.area.pojo.Area;

import java.util.List;

/**
 * @Auther:wjx
 * @Date:2019/5/14
 * @Description:com.wjx.area.service
 * @version:1.0
 */
public interface AreaService {
    /**
     * 根据上级城市编号查询城市信息
     *
     * @return
     */
    List<Area> queryByParentid(String parentid);
}
