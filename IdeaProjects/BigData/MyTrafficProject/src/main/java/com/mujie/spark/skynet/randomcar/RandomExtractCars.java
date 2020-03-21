package com.mujie.spark.skynet.randomcar;

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

/**
 * 随机抽取N辆的信息
 */
public class RandomExtractCars {
    public static void main(String[] args) {
        JavaSparkContext sc = null;
        SparkSession spark = null;
        Boolean onLocal = ConfigurationManager.getBoolean(Constants.SPARK_LOCAL);

        if(onLocal){
            SparkConf conf = new SparkConf().setAppName(Constants.SPARK_APP_NAME);
            conf.setMaster("local");
            sc = new JavaSparkContext(conf);
            spark = SparkSession.builder().getOrCreate();
            MockData.mock(sc, spark);
        }else{
            spark = SparkSession.builder().enableHiveSupport().getOrCreate();
            spark.sql("use traffic");
        }
        // 读取配置文件中的taskId
        long taskId = ParamUtils.getTaskIdFromArgs(args, Constants.SPARK_LOCAL_TASKID_EXTRACT_CAR);
        // 通过taskId获取运行参数
        ITaskDAO taskDAO = DAOFactory.getTaskDAO();
        Task task = taskDAO.findTaskById(taskId);
        if(task == null){
            System.out.println("没有找打对应的taskID参数");
            System.exit(1);
        }
        /**
         * 将 task 的 parm字段的字符串转换成json格式数据。
         */
        JSONObject taskParamsJsonObject = JSONObject.parseObject(task.getTaskParams());

        /**
         * 获取时间段内所有的卡口信息
         */
        JavaRDD<Row> cameraRDD = SparkUtils.getCameraRDDByDateRange(spark, taskParamsJsonObject);

        /**
         * 随机抽取车辆信息并插入到 random_extract_car 表
         * 详细信息插入到 random_extract_car_detail_info 表
         */
        JavaPairRDD<String, Row> randomExtractCar2DetailRDD = FunToTran.randomExtractCarInfo(sc,taskId,taskParamsJsonObject,cameraRDD);

        /**
         * 获取车辆的轨迹
         */
        JavaPairRDD<String, String> carTrackRDD = FunToTran.getCarTrack(taskId,randomExtractCar2DetailRDD);

        /**
         * 将车辆的轨迹信息写入到数据库表car_track中
         */
        FunToTran.saveCarTrack2DB(taskId,carTrackRDD);

        sc.close();
    }
}
