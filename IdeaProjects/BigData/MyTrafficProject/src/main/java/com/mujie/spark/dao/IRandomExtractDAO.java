package com.mujie.spark.dao;

import com.mujie.spark.domain.RandomExtractCar;
import com.mujie.spark.domain.RandomExtractMonitorDetail;

import java.util.List;

/**
 * 随机抽取car信息管理DAO类
 *
 * @Auther:wjx
 * @Date:2019/8/4
 * @Description:com.mujie.spark.dao
 * @version:1.0
 */
public interface IRandomExtractDAO {
    void insertBatchRandomExtractCar(List<RandomExtractCar> carRandomExtracts);

    void insertBatchRandomExtractDetails(List<RandomExtractMonitorDetail> r);

}
