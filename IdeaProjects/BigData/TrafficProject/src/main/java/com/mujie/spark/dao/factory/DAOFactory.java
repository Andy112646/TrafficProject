package com.mujie.spark.dao.factory;

import com.mujie.spark.dao.IAreaDao;
import com.mujie.spark.dao.ICarTrackDAO;
import com.mujie.spark.dao.IMonitorDAO;
import com.mujie.spark.dao.IRandomExtractDAO;
import com.mujie.spark.dao.ITaskDAO;
import com.mujie.spark.dao.IWithTheCarDAO;
import com.mujie.spark.dao.impl.AreaDaoImpl;
import com.mujie.spark.dao.impl.CarTrackDAOImpl;
import com.mujie.spark.dao.impl.MonitorDAOImpl;
import com.mujie.spark.dao.impl.RandomExtractDAOImpl;
import com.mujie.spark.dao.impl.TaskDAOImpl;
import com.mujie.spark.dao.impl.WithTheCarDAOImpl;

/**
 * DAO工厂类
 * @author root
 *
 */
public class DAOFactory {
	
	
	public static ITaskDAO getTaskDAO(){
		return new TaskDAOImpl();
	}
	
	public static IMonitorDAO getMonitorDAO(){
		return new MonitorDAOImpl();
	}
	
	public static IRandomExtractDAO getRandomExtractDAO(){
		return new RandomExtractDAOImpl();
	}
	
	public static ICarTrackDAO getCarTrackDAO(){
		return new CarTrackDAOImpl();
	}
	
	public static IWithTheCarDAO getWithTheCarDAO(){
		return new WithTheCarDAOImpl();
	}

	public static IAreaDao getAreaDao() {
		return  new AreaDaoImpl();
		
	}
}
