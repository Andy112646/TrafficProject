package com.mujie.spark.dao.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.mujie.spark.dao.IAreaDao;
import com.mujie.spark.domain.Area;
import com.mujie.spark.jdbc.JDBCHelper;
import com.mujie.spark.jdbc.JDBCHelper.QueryCallback;

public class AreaDaoImpl implements IAreaDao {

	public List<Area> findAreaInfo() {
		final List<Area> areas = new ArrayList<Area>();
		
		String sql = "SELECT * FROM area_info";
		
		JDBCHelper jdbcHelper = JDBCHelper.getInstance();
		jdbcHelper.executeQuery(sql, null, new QueryCallback() {
			
			public void process(ResultSet rs) throws Exception {
				if(rs.next()) {
					String areaId = rs.getString(1);
					String areaName = rs.getString(2);
					areas.add(new Area(areaId, areaName));
				}
			}
		});
		return areas;
	}

}
