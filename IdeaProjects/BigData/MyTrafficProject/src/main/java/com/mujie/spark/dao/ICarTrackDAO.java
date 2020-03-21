package com.mujie.spark.dao;

import com.mujie.spark.domain.CarTrack;

import java.util.List;

/**
 * @Auther:wjx
 * @Date:2019/8/4
 * @Description:com.mujie.spark.dao
 * @version:1.0
 */
public interface ICarTrackDAO {
    /**
     * 批量插入车辆轨迹信息
     * @param carTracks
     */
    void insertBatchCarTrack(List<CarTrack> carTracks);
}
