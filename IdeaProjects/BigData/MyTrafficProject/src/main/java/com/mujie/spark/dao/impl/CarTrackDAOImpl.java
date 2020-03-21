package com.mujie.spark.dao.impl;

import com.mujie.spark.dao.ICarTrackDAO;
import com.mujie.spark.domain.CarTrack;
import com.mujie.spark.jdbc.JDBCHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther:wjx
 * @Date:2019/8/4
 * @Description:com.mujie.spark.dao.impl
 * @version:1.0
 */
public class CarTrackDAOImpl implements ICarTrackDAO {
    @Override
    public void insertBatchCarTrack(List<CarTrack> carTracks) {
        JDBCHelper jdbcHelper = JDBCHelper.getInstance();
        String sql = "INSERT INTO car_track VALUES(?,?,?,?)";
        List<Object[]> params = new ArrayList<>();
        for(CarTrack c : carTracks){
            params.add(new Object[]{c.getTaskId(),c.getDate(),c.getCar(),c.getTrack()});
        }
        jdbcHelper.executeBatch(sql, params);
    }
}
