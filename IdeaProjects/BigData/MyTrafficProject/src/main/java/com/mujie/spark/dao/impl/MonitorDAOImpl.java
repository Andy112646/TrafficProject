package com.mujie.spark.dao.impl;

import com.mujie.spark.dao.IMonitorDAO;
import com.mujie.spark.domain.MonitorState;
import com.mujie.spark.domain.TopNMonitor2CarCount;
import com.mujie.spark.domain.TopNMonitorDetailInfo;
import com.mujie.spark.jdbc.JDBCHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther:wjx
 * @Date:2019/7/31
 * @Description:com.mujie.spark.dao.impl
 * @version:1.0
 */
public class MonitorDAOImpl implements IMonitorDAO {
    /**
     * 向数据库表monitor_state中添加由累加器累加的卡口的五种状态数据
     *
     * @param monitorState
     */
    @Override
    public void insertMonitorState(MonitorState monitorState) {
        JDBCHelper jdbcHelper = JDBCHelper.getInstance();
        String sql = "INSERT INTO monitor_state VALUES(?,?,?,?,?,?)";
        Object[] param = new Object[]{
                monitorState.getTaskId(),
                monitorState.getNormalMonitorCount(),
                monitorState.getNormalCameraCount(),
                monitorState.getAbnormalMonitorCount(),
                monitorState.getAbnormalCameraCount(),
                monitorState.getAbnormalMonitorCameraInfos()};
        List<Object[]> params = new ArrayList<>();
        params.add(param);
        jdbcHelper.executeBatch(sql, params);
    }

    /**
     * 向数据库表 topn_monitor_car_count 中插入车流量最多的TopN数据
     *
     * @param topNMonitor2CarCounts
     */
    @Override
    public void insertBatchTopN(ArrayList<TopNMonitor2CarCount> topNMonitor2CarCounts) {
        JDBCHelper instance = JDBCHelper.getInstance();
        String sql = "insert into topn_monitor_car_count values(?,?,?)";
        List<Object[]> params = new ArrayList<>();
        for (TopNMonitor2CarCount topNMonitor2CarCount : topNMonitor2CarCounts) {
            params.add(new Object[]{topNMonitor2CarCount.getTaskId(),topNMonitor2CarCount.getMonitorId(),topNMonitor2CarCount.getCarCount()});
        }
        instance.executeBatch(sql , params);
    }

    /**
     * 	将topN的卡扣车流量明细数据 存入topn_monitor_detail_info 表中
     * @param monitorDetailInfos
     */
    @Override
    public void insertBatchMonitorDetails(List<TopNMonitorDetailInfo> monitorDetailInfos) {
        JDBCHelper jdbcHelper = JDBCHelper.getInstance();
        String sql = "INSERT INTO topn_monitor_detail_info VALUES(?,?,?,?,?,?,?,?)";
        List<Object[]> params = new ArrayList<>();
        for(TopNMonitorDetailInfo m : monitorDetailInfos){
            params.add(new Object[]{m.getTaskId(),m.getDate(),m.getMonitorId(),m.getCameraId(),m.getCar(),m.getActionTime(),m.getSpeed(),m.getRoadId()});
        }
        jdbcHelper.executeBatch(sql, params);
    }

    /**
     * tonN卡口中每个卡口车速前10的车辆信息批量插入数据库
     * @param topNMonitorDetailInfos
     */
    @Override
    public void insertBatchTop10Details(List<TopNMonitorDetailInfo> topNMonitorDetailInfos) {
        JDBCHelper instance = JDBCHelper.getInstance();
        String sql = "insert into top10_speed_detail values(?,?,?,?,?,?,?,?)";
        List<Object[]> params = new ArrayList<>();
        for (TopNMonitorDetailInfo m : topNMonitorDetailInfos) {
            params.add(new Object[]{m.getTaskId(),m.getDate(),m.getMonitorId(),m.getCameraId(),m.getCar(),m.getActionTime(),m.getSpeed(),m.getRoadId()});
        }
        instance.executeBatch(sql, params);

    }


}
