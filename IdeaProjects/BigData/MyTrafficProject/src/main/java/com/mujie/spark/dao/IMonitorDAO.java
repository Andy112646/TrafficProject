package com.mujie.spark.dao;

import com.mujie.spark.domain.MonitorState;
import com.mujie.spark.domain.TopNMonitor2CarCount;
import com.mujie.spark.domain.TopNMonitorDetailInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 卡口流量监控管理DAO接口
 * @Auther:wjx
 */
public interface IMonitorDAO {
    /**
     * 将卡口五种状态信息插入数据库
     * @param monitorState
     */
    void insertMonitorState(MonitorState monitorState);

    /**
     * 卡口流量topN批量插入到数据库
     * @param topNMonitor2CarCounts
     */
    void insertBatchTopN(ArrayList<TopNMonitor2CarCount> topNMonitor2CarCounts);

    /**
     * 卡口下车辆具体信息插入到数据库
     * @param monitorDetailInfos
     */
    void insertBatchMonitorDetails(List<TopNMonitorDetailInfo> monitorDetailInfos);

    /**
     * tonN卡口中每个卡口车速前10的车辆信息批量插入数据库
     * @param topNMonitorDetailInfos
     */
    void insertBatchTop10Details(List<TopNMonitorDetailInfo> topNMonitorDetailInfos);
}
