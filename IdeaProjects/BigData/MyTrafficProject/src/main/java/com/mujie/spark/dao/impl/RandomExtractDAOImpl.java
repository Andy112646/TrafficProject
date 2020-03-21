package com.mujie.spark.dao.impl;

import com.mujie.spark.dao.IRandomExtractDAO;
import com.mujie.spark.domain.RandomExtractCar;
import com.mujie.spark.domain.RandomExtractMonitorDetail;
import com.mujie.spark.jdbc.JDBCHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 随机抽取car信息管理DAO实现类
 * @Auther:wjx
 * @Date:2019/8/4
 * @Description:com.mujie.spark.dao.impl
 * @version:1.0
 */
public class RandomExtractDAOImpl implements IRandomExtractDAO {
    @Override
    public void insertBatchRandomExtractCar(List<RandomExtractCar> carRandomExtracts) {
        JDBCHelper jdbcHelper = JDBCHelper.getInstance();
        String sql = "INSERT INTO random_extract_car VALUES(?,?,?,?)";
        List<Object[]> params = new ArrayList<>();
        for (RandomExtractCar carRandomExtract : carRandomExtracts) {
            params.add(new Object[]{carRandomExtract.getTaskId(),carRandomExtract.getCar(),carRandomExtract.getDate(),carRandomExtract.getDateHour()});
        }
        jdbcHelper.executeBatch(sql , params);
    }

    @Override
    public void insertBatchRandomExtractDetails(List<RandomExtractMonitorDetail> randomExtractMonitorDetails) {
        JDBCHelper jdbcHelper = JDBCHelper.getInstance();
        String sql = "INSERT INTO random_extract_car_detail_info VALUES(?,?,?,?,?,?,?,?)";
        List<Object[]> params = new ArrayList<>();
        for(RandomExtractMonitorDetail r : randomExtractMonitorDetails){
            params.add(new Object[]{r.getTaskId(),r.getDate(),r.getMonitorId(),r.getCameraId(),r.getCar(),r.getActionTime(),r.getSpeed(),r.getRoadId()});
        }
        jdbcHelper.executeBatch(sql, params);
    }
}
