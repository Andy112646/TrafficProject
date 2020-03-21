package com.mujie.spark.dao.factory;

import com.mujie.spark.dao.ICarTrackDAO;
import com.mujie.spark.dao.IMonitorDAO;
import com.mujie.spark.dao.IRandomExtractDAO;
import com.mujie.spark.dao.ITaskDAO;
import com.mujie.spark.dao.impl.CarTrackDAOImpl;
import com.mujie.spark.dao.impl.MonitorDAOImpl;
import com.mujie.spark.dao.impl.RandomExtractDAOImpl;
import com.mujie.spark.dao.impl.TaskDaoImpl;

/**
 * @Auther:wjx
 * @Date:2019/7/31
 *
 * DAO 工厂类
 */
public class DAOFactory {
    public static ITaskDAO getTaskDAO() {
        return new TaskDaoImpl();
    }

    public static IMonitorDAO getMonitorDAO() {
        return new MonitorDAOImpl();
    }

    public static IRandomExtractDAO getRandomExtractDAO(){
        return new RandomExtractDAOImpl();
    }

    public static ICarTrackDAO getCarTrackDAO() {
        return new CarTrackDAOImpl();

    }
}
