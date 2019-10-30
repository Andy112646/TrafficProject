package com.producedate2hive;

import org.apache.spark.sql.SparkSession;

/**
 * 1.代码方式向hive中创建数据
 * 2.后面可以有sql文件执行直接在hive中创建数据库表
 *
 */
public class Data2Hive {
	public static void main(String[] args) {
		SparkSession spark = SparkSession.builder().appName("traffic2hive").enableHiveSupport().getOrCreate();
		spark.sql("USE traffic");
		spark.sql("DROP TABLE IF EXISTS monitor_flow_action");
		//在hive中创建monitor_flow_action表
		spark.sql("CREATE TABLE IF NOT EXISTS monitor_flow_action "
				+ "(date STRING,monitor_id STRING,camera_id STRING,car STRING,action_time STRING,speed STRING,road_id STRING,area_id STRING) "
				+ "row format delimited fields terminated by '\t' ");
		spark.sql("load data local inpath '/root/test/monitor_flow_action' into table monitor_flow_action");
		
		//在hive中创建monitor_camera_info表
		spark.sql("DROP TABLE IF EXISTS monitor_camera_info");
		spark.sql("CREATE TABLE IF NOT EXISTS monitor_camera_info (monitor_id STRING, camera_id STRING) row format delimited fields terminated by '\t'");
		spark.sql("LOAD DATA "
				+ "LOCAL INPATH '/root/test/monitor_camera_info'"
				+ "INTO TABLE monitor_camera_info");
		
		System.out.println("========data2hive finish========");
		spark.stop();
	}
}
