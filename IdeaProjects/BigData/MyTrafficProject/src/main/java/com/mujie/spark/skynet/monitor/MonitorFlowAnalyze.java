package com.mujie.spark.skynet.monitor;

import com.alibaba.fastjson.JSONObject;
import com.mujie.spark.conf.ConfigurationManager;
import com.mujie.spark.constant.Constants;
import com.mujie.spark.dao.ITaskDAO;
import com.mujie.spark.dao.factory.DAOFactory;
import com.mujie.spark.domain.Task;
import com.mujie.spark.util.ParamUtils;
import com.mujie.spark.util.SparkUtils;
import com.spark.spark.test.MockData;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.List;

/**
 * 卡口流量监控模块
 */
public class MonitorFlowAnalyze {
    public static void main(String[] args) {
        JavaSparkContext sc = null;
        SparkSession spark = null;
        // 获取应用程序的执行环境（本地还是集群）
        Boolean onLocal = ConfigurationManager.getBoolean(Constants.SPARK_LOCAL);

        if (onLocal) {
            // 本地运行
            SparkConf conf = new SparkConf().setAppName(Constants.SPARK_APP_NAME);
            conf.setMaster("local");
            sc = new JavaSparkContext(conf);
            spark = SparkSession.builder().getOrCreate();
            /**
             * 基于本地测试生成模拟测试数据然后注册成临时表：
             * monitor_flow_action	数据表：监控车流量所有数据
             * monitor_camera_info	标准表：卡扣对应摄像头标准表
             */
            MockData.mock(sc, spark);
        } else {
            // 集群中运行，数据源为Hive，所以开启Hive支持
            spark = SparkSession.builder().appName(Constants.SPARK_APP_NAME).enableHiveSupport().getOrCreate();
            sc = new JavaSparkContext(spark.sparkContext());
            spark.sql("use traffic");
        }
        // 从配置文件my.properties中获取spark.local.taskId.monitorFlow的taskId
        Long taskId = ParamUtils.getTaskIdFromArgs(args, Constants.SPARK_LOCAL_TASKID_MONITOR);
        if (taskId == 0L) {
            System.out.println("参数为空");
            System.exit(1);
        }

        /**
         * 通过taskId 获取数据库中对应的数据并封装到Task对象中
         * task_param字段是形如json格式的字符串：
         * {"startDate":["2019-07-30"],"endDate":["2019-07-30"],"topNum":["5"],"areaName":["海淀区"]}
         */
        ITaskDAO taskDao = DAOFactory.getTaskDAO();
        Task task = taskDao.findTaskById(taskId);

        if (task == null) {
            System.out.println("没有该taskId");
            System.exit(1); // 终止JVM进程（表示非正常退出程序）
        }
        System.out.println(task.getTaskParams());

        /**
         * 将 task 的 parm字段的字符串转换成json格式数据。
         */
        JSONObject taskParamsJsonObject = JSONObject.parseObject(task.getTaskParams());

        /**
         * 通过params（json字符串）的startDate和endDate查询monitor_flow_action
         *
         * 获取指定时间段内检测的monitor_flow_action中车流量数据，返回JavaRDD<Row>
         * 2019-07-30	0000	48564	京P51361	2019-07-30 01:31:42	123	7	01
         * 2019-07-30	0001	26317	京P51361	2019-07-30 01:08:25	92	24	08
         */
        JavaRDD<Row> cameraRDD = SparkUtils.getCameraRDDByDateRange(spark, taskParamsJsonObject);


        /**
         * 将row类型的RDD转换成k-v格式的RDD
         * k:monitor_id  v:row
         *
         * (0000, 2019-07-30	0000	48564	京P51361	2019-07-30 01:31:42	123	7	01)
         * (0001, 2019-07-30	0001	26317	京P51361	2019-07-30 01:08:25	92	24	08)
         */
        JavaPairRDD<String, Row> monitor2DetailRDD = FunToTran.getMonitor2DetailRDD(cameraRDD);
        // 持久化
        monitor2DetailRDD = monitor2DetailRDD.cache();


        /**
         * 按照卡口号分组，将相同monitor_id 的row聚合在一个list中
         * 由于一共有9个卡扣号，这里groupByKey后一共有9组数据。
         * (monitor_id, [row,row...])
         * (monitor_id, [row,row...])
         * (monitor_id, [row,row...])
         *   ......
         *
         */
        JavaPairRDD<String, Iterable<Row>> monitorId2RowsRDD = monitor2DetailRDD.groupByKey();

        /**
         * 遍历分组后的RDD，将value中的每一条row进行聚合：
         * 1、获取当前卡口所有正常的camearIds
         * 3、统计当前卡口camearIds正常的个数
         * 3、统计通过该卡口的车流量（即row的条数）
         *
         * 数据中一共就有9个monitorId信息，那么聚合之后的信息也是9条
         * monitor_id=|cameraIds=|area_id=|camera_count=|carCount=
         * 例如:
         * ("0005","monitorId=0005|camearIds=09200,03243,02435,03232|cameraCount=4|carCount=100")
         *
         */
        JavaPairRDD<String, String> aggregateMonitorId2DetailRDD = FunToTran.aggreagteByMonitor(monitorId2RowsRDD);

        /**
         * 创建一个累加器（自定义）
         */
        SelfDefineAccumulator monitorAndCameraStateAccumulator = new SelfDefineAccumulator();
        spark.sparkContext().register(monitorAndCameraStateAccumulator, "SelfAccumulator");
        /**
         * 检测卡扣状态
         * carCount2MonitorRDD
         * K:car_count V:monitor_id
         * RDD(卡扣对应车流量总数,对应的卡扣号)
         */
        JavaPairRDD<Integer, String> carCount2MonitorRDD =
                FunToTran.checkMonitorState(sc, spark, aggregateMonitorId2DetailRDD, taskId, taskParamsJsonObject, monitorAndCameraStateAccumulator);

        carCount2MonitorRDD.count();

        /**
         * 往数据库表  monitor_state 中保存 累加器累加的五个状态
         */
        FunToTran.saveMonitorState(taskId, monitorAndCameraStateAccumulator);


        /**
         * 获取车流量排名前N的卡口号和车流量
         * 数据存入 topn_monitor_car_count 中
         *
         * 然后返回topN的RDD，便于后面业务的使用
         * （monitor_id, monitor_id）
         */
        JavaPairRDD<String, String> topNMonitor2CarFlow = FunToTran.getTopNMonitorCarFlow(sc, taskId, taskParamsJsonObject, carCount2MonitorRDD);

        /**
         * 获取车流量 topN 卡口的详细信息
         * 存入 topn_monitor_detail_info 表
         */
        FunToTran.getTopNDetails(taskId, topNMonitor2CarFlow, monitor2DetailRDD);

        /**
         * 获取车辆高速通过的top5卡口
         *
         * 使用二次排序
         */
        List<String> top5MonitorIds = FunToTran.speedTopNMonitor(monitorId2RowsRDD);

        /**
         * 获取 车流量topN卡口中，每个卡口车速前10的车辆信息，同时存入数据库表 top10_speed_detail 中
         *
         * 分组取tonN 问题
         */
        FunToTran.getMonitorDetails(sc, taskId, top5MonitorIds, monitor2DetailRDD);

        /**
         * 区域碰撞分析
         *
         * 计算出两个区域共同出现的车辆
         */
        FunToTran.areaCarPeng(spark,taskParamsJsonObject,"01","02");

    }

}
