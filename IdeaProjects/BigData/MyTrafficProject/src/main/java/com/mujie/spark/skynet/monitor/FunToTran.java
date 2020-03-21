package com.mujie.spark.skynet.monitor;

import com.alibaba.fastjson.JSONObject;
import com.mujie.spark.constant.Constants;
import com.mujie.spark.dao.IMonitorDAO;
import com.mujie.spark.dao.factory.DAOFactory;
import com.mujie.spark.domain.MonitorState;
import com.mujie.spark.domain.TopNMonitor2CarCount;
import com.mujie.spark.domain.TopNMonitorDetailInfo;
import com.mujie.spark.util.ParamUtils;
import com.mujie.spark.util.SparkUtils;
import com.mujie.spark.util.StringUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.Optional;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @Auther:wjx
 * @Date:2019/8/1
 * @Description:com.mujie.spark.skynet.monitor
 * @version:1.0
 */
public class FunToTran {
    /**
     * 将row类型的RDD转换成k-v格式的RDD
     * k:monitor_id  v:row
     *
     * @param cameraRDD
     * @return (0000, 2019 - 07 - 30 0000 48564 京P51361 2019 - 07 - 30 01 : 31 : 42 123 7 01)
     * (0001, 2019-07-30	0001	26317	京P51361	2019-07-30 01:08:25	92	24	08)
     */
    public static JavaPairRDD<String, Row> getMonitor2DetailRDD(JavaRDD<Row> cameraRDD) {
        JavaPairRDD<String, Row> monitorId2Detail = cameraRDD.mapToPair(new PairFunction<Row, String, Row>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Tuple2<String, Row> call(Row row) {
                return new Tuple2<>(row.getAs("monitor_id"), row);
            }
        });
        return monitorId2Detail;
    }


    /**
     * 遍历分组后的RDD，将value中的每一条row进行聚合：
     * 1、获取当前卡口所有正常的camearIds
     * 3、统计当前卡口camearIds正常的个数
     * 3、统计通过该卡口的车流量（即row的条数）
     * <p>
     * 数据中一共就有9个monitorId信息，那么聚合之后的信息也是9条
     * monitor_id=|cameraIds=|area_id=|camera_count=|carCount=
     * 例如:
     * ("0005","monitorId=0005|camearIds=09200,03243,02435,03232|cameraCount=4|carCount=100")
     */
    public static JavaPairRDD<String, String> aggreagteByMonitor(JavaPairRDD<String, Iterable<Row>> monitorId2RowRDD) {
        JavaPairRDD<String, String> monitorId2CameraCountRDD = monitorId2RowRDD.mapToPair(new PairFunction<Tuple2<String, Iterable<Row>>, String, String>() {
            private static final long serialVersionUID = 1l;

            @Override
            public Tuple2<String, String> call(Tuple2<String, Iterable<Row>> tuple) throws Exception {
                String monitorId = tuple._1;
                Iterator<Row> rowIterator = tuple._2.iterator();
                ArrayList<Object> list = new ArrayList<>();
                StringBuilder tmpInfos = new StringBuilder();
                int count = 0; // 统计车辆数的count
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    String cameraId = row.getAs("camera_id");
                    if (!list.contains(cameraId)) {
                        list.add(cameraId);
                    }
                    if (!tmpInfos.toString().contains(cameraId)) {
                        tmpInfos.append("," + cameraId);
                    }
                    count++;
                }
                /**
                 * camera_count
                 */
                int cameraCount = list.size();
                String infos = Constants.FIELD_MONITOR_ID + "=" + monitorId + "|"
                        + Constants.FIELD_CAMERA_IDS + "=" + tmpInfos.toString().substring(1) + "|"
                        + Constants.FIELD_CAMERA_COUNT + "=" + cameraCount + "|"
                        + Constants.FIELD_CAR_COUNT + "=" + count;
                return new Tuple2<>(monitorId, infos);
            }
        });
        return monitorId2CameraCountRDD;
    }


    /**
     * 检测卡口状态
     *
     * @param sc
     * @return RDD(实际卡扣对应车流量总数, 对应的卡扣号)
     */
    public static JavaPairRDD<Integer, String> checkMonitorState(
            JavaSparkContext sc,
            SparkSession spark,
            JavaPairRDD<String, String> monitorId2CameraCountRDD,
            final long taskId, JSONObject taskParamsJsonObject,
            SelfDefineAccumulator monitorAndCameraStateAccumulator
            /*final Accumulator<String> monitorAndCameraStateAccumulator*/) {
        /**
         * 从monitor_camera_info标准表中查询出来每一个卡口对应的camera的数量
         */
        String sqlText = "SELECT * FROM monitor_camera_info";
        Dataset<Row> standardDF = spark.sql(sqlText);
        JavaRDD<Row> standardRDD = standardDF.javaRDD();
        /**
         * 使用mapToPair算子将standardRDD变成KV格式的RDD
         * monitorId2CameraId   :
         * (K:monitor_id  v:camera_id)
         */
        JavaPairRDD<String, String> monitorId2CameraId = standardRDD.mapToPair(
                new PairFunction<Row, String, String>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Tuple2<String, String> call(Row row) {

                        return new Tuple2<>(row.getAs("monitor_id"), row.getAs("camera_id"));
                    }
                });

        /**
         * 对每一个卡扣下面的信息进行统计，统计出来camera_count（这个卡扣下一共有多少个摄像头）,camera_ids(这个卡扣下，所有的摄像头编号拼接成的字符串)
         * 返回：
         * 	("monitorId","cameraIds=xxx|cameraCount=xxx")
         * 例如：
         * 	("0008","cameraIds=02322,01213,03442|cameraCount=3")
         * 如何来统计？
         * 	1、按照monitor_id分组
         * 	2、使用mapToPair遍历，遍历的过程可以统计
         */
        JavaPairRDD<String, String> standardMonitor2CameraInfos = monitorId2CameraId.groupByKey()
                .mapToPair(new PairFunction<Tuple2<String, Iterable<String>>, String, String>() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public Tuple2<String, String> call(Tuple2<String, Iterable<String>> tuple) {
                        String monitorId = tuple._1;
                        Iterator<String> cameraIterator = tuple._2.iterator();
                        int count = 0;//标准表中当前卡扣对应的摄像头个数
                        StringBuilder cameraIds = new StringBuilder();
                        while (cameraIterator.hasNext()) {
                            cameraIds.append("," + cameraIterator.next());
                            count++;
                        }
                        //cameraIds=00001,00002,00003,00004|cameraCount=4
                        String cameraInfos = Constants.FIELD_CAMERA_IDS + "=" + cameraIds.toString().substring(1) + "|"
                                + Constants.FIELD_CAMERA_COUNT + "=" + count;
                        return new Tuple2<>(monitorId, cameraInfos);
                    }
                });


        /**
         * 将两个RDD进行比较，join  leftOuterJoin
         * 为什么使用左外连接？ 左：标准表里面的信息  右：实际信息
         */
        JavaPairRDD<String, Tuple2<String, Optional<String>>> joinResultRDD =
                standardMonitor2CameraInfos.leftOuterJoin(monitorId2CameraCountRDD);


        /**
         * carCount2MonitorId 最终返回的K,V格式的数据
         * K：实际监测数据中某个卡扣对应的总车流量
         * V：实际监测数据中这个卡扣 monitorId
         *
         * mapPartitionsToPair 一个个分区处理，而不是一条条数据处理
         */
        JavaPairRDD<Integer, String> carCount2MonitorId = joinResultRDD.mapPartitionsToPair(
                new PairFlatMapFunction<
                        Iterator<Tuple2<String, Tuple2<String, Optional<String>>>>, Integer, String>() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public Iterator<Tuple2<Integer, String>> call(
                            Iterator<Tuple2<String, Tuple2<String, Optional<String>>>> iterator) {

                        List<Tuple2<Integer, String>> list = new ArrayList<>();
                        // 遍历处理分区中的每一条数据
                        while (iterator.hasNext()) {
                            // 储藏返回值
                            Tuple2<String, Tuple2<String, Optional<String>>> tuple = iterator.next(); //
                            String monitorId = tuple._1;
                            String standardCameraInfos = tuple._2._1; // 当前卡口标准的摄像头信息
                            Optional<String> factCameraInfosOptional = tuple._2._2; // 当前卡口实际的摄像头信息
                            String factCameraInfos = "";

                            if (factCameraInfosOptional.isPresent()) { // factCameraInfosOptional 有值
                                //这里面是实际检测数据中有标准卡扣信息
                                factCameraInfos = factCameraInfosOptional.get();
                            } else { // factCameraInfosOptional 没有值
                                String standardCameraIds =
                                        StringUtils.getFieldFromConcatString(standardCameraInfos, "\\|", Constants.FIELD_CAMERA_IDS);
                                String abnoramlCameraCount =
                                        StringUtils.getFieldFromConcatString(standardCameraInfos, "\\|", Constants.FIELD_CAMERA_COUNT);

                                //abnormalMonitorCount=1|abnormalCameraCount=3|abnormalMonitorCameraInfos="0001":07553,07554,07556
                                monitorAndCameraStateAccumulator.add(
                                        Constants.FIELD_ABNORMAL_MONITOR_COUNT + "=1|"
                                                + Constants.FIELD_ABNORMAL_CAMERA_COUNT + "=" + abnoramlCameraCount + "|"
                                                + Constants.FIELD_ABNORMAL_MONITOR_CAMERA_INFOS + "=" + monitorId + ":" + standardCameraIds);
                                //跳出了本次while
                                continue;
                            }
                            /**
                             * 从实际数据拼接的字符串中获取摄像头数
                             */
                            int factCameraCount = Integer.parseInt(StringUtils.getFieldFromConcatString(factCameraInfos, "\\|", Constants.FIELD_CAMERA_COUNT));
                            /**
                             * 从标准数据拼接的字符串中获取摄像头数
                             */
                            int standardCameraCount = Integer.parseInt(StringUtils.getFieldFromConcatString(standardCameraInfos, "\\|", Constants.FIELD_CAMERA_COUNT));
                            if (factCameraCount == standardCameraCount) {
                                /*
                                 * 	1、正常卡口数量
                                 * 	2、异常卡口数量
                                 * 	3、正常通道（此通道的摄像头运行正常）数，通道就是摄像头
                                 * 	4、异常卡口数量中哪些摄像头异常，需要保存摄像头的编号
                                 */
                                monitorAndCameraStateAccumulator.add(Constants.FIELD_NORMAL_MONITOR_COUNT + "=1|" + Constants.FIELD_NORMAL_CAMERA_COUNT + "=" + factCameraCount);
                            } else {
                                /**
                                 * 从实际数据拼接的字符串中获取摄像编号集合
                                 */
                                String factCameraIds = StringUtils.getFieldFromConcatString(factCameraInfos, "\\|", Constants.FIELD_CAMERA_IDS);

                                /**
                                 * 从标准数据拼接的字符串中获取摄像头编号集合
                                 */
                                String standardCameraIds = StringUtils.getFieldFromConcatString(standardCameraInfos, "\\|", Constants.FIELD_CAMERA_IDS);

                                List<String> factCameraIdList = Arrays.asList(factCameraIds.split(","));
                                List<String> standardCameraIdList = Arrays.asList(standardCameraIds.split(","));
                                StringBuilder abnormalCameraInfos = new StringBuilder();
                                int abnormalCameraCount = 0;//不正常摄像头数
                                int normalCameraCount = 0;//正常摄像头数
                                for (String cameraId : standardCameraIdList) {
                                    if (!factCameraIdList.contains(cameraId)) {
                                        abnormalCameraCount++;
                                        abnormalCameraInfos.append("," + cameraId);
                                    }
                                }
                                normalCameraCount = standardCameraIdList.size() - abnormalCameraCount;
                                //往累加器中更新状态
                                monitorAndCameraStateAccumulator.add(
                                        Constants.FIELD_ABNORMAL_MONITOR_COUNT + "=1|"
                                                + Constants.FIELD_NORMAL_CAMERA_COUNT + "=" + normalCameraCount + "|"
                                                + Constants.FIELD_ABNORMAL_CAMERA_COUNT + "=" + abnormalCameraCount + "|"
                                                + Constants.FIELD_ABNORMAL_MONITOR_CAMERA_INFOS + "=" + monitorId + ":" + abnormalCameraInfos.toString().substring(1));
                            }
                            //从实际数据拼接到字符串中获取车流量
                            int carCount = Integer.parseInt(StringUtils.getFieldFromConcatString(factCameraInfos, "\\|", Constants.FIELD_CAR_COUNT));
                            list.add(new Tuple2<>(carCount, monitorId));
                        }
                        //最后返回的list是实际监测到的数据中，list[(卡扣对应车流量总数,对应的卡扣号),... ...]
                        return list.iterator();
                    }
                });
        return carCount2MonitorId;
    }


    /**
     * 往数据库中保存 累加器累加的五个状态
     *
     * @param taskId
     * @param monitorAndCameraStateAccumulator
     */
    public static void saveMonitorState(Long taskId, SelfDefineAccumulator monitorAndCameraStateAccumulator) {
        /**
         * 累加器的值不能在Executor端读取
         * 而是在Driver端中读取的
         */
        String accumulatorVal = monitorAndCameraStateAccumulator.value();
        String normalMonitorCount = StringUtils.getFieldFromConcatString(accumulatorVal, "\\|", Constants.FIELD_NORMAL_MONITOR_COUNT);
        String normalCameraCount = StringUtils.getFieldFromConcatString(accumulatorVal, "\\|", Constants.FIELD_NORMAL_CAMERA_COUNT);
        String abnormalMonitorCount = StringUtils.getFieldFromConcatString(accumulatorVal, "\\|", Constants.FIELD_ABNORMAL_MONITOR_COUNT);
        String abnormalCameraCount = StringUtils.getFieldFromConcatString(accumulatorVal, "\\|", Constants.FIELD_ABNORMAL_CAMERA_COUNT);
        String abnormalMonitorCameraInfos = StringUtils.getFieldFromConcatString(accumulatorVal, "\\|", Constants.FIELD_ABNORMAL_MONITOR_CAMERA_INFOS);

        /**
         * 这里面只有一条记录
         */
        MonitorState monitorState = new MonitorState(taskId, normalMonitorCount, normalCameraCount, abnormalMonitorCount, abnormalCameraCount, abnormalMonitorCameraInfos);

        /**
         * 向数据库表monitor_state中添加累加器累计的各个值
         */
        IMonitorDAO monitorDAO = DAOFactory.getMonitorDAO();
        monitorDAO.insertMonitorState(monitorState);
    }

    /**
     * 1、获取车流量前N的卡口号，持久化到数据库
     * （作用： 当某一个卡口的流量这几天突然暴增和往常的流量不相符，交管部门应该找一下原因，是什么问题导致的，应该到现场去疏导车辆。）
     * <p>
     * 2、获取topN卡口的id并返回RDD
     *
     * @param sc
     * @param taskId
     * @param taskParamsJsonObject 任务参数，拿到其中的topNum
     * @param carCount2MonitorRDD  RDD：(car_count, monitor_id)
     */
    public static JavaPairRDD<String, String> getTopNMonitorCarFlow(JavaSparkContext sc, Long taskId, JSONObject taskParamsJsonObject, JavaPairRDD<Integer, String> carCount2MonitorRDD) {
        /**
         * 获取车流量前N的卡口号
         */
        // 获取参数topNum
        int topNum = Integer.parseInt(ParamUtils.getParam(taskParamsJsonObject, Constants.FIELD_TOP_NUM));
        // 获取topN的 carCount2MonitorId
        List<Tuple2<Integer, String>> topNCarCount = carCount2MonitorRDD.sortByKey().take(topNum);
        // 将结果封装到对象中
        ArrayList<TopNMonitor2CarCount> topNMonitor2CarCounts = new ArrayList<>();
        for (Tuple2<Integer, String> tuple : topNCarCount) {
            TopNMonitor2CarCount topNMonitor2CarCount = new TopNMonitor2CarCount(taskId, tuple._2, tuple._1);
            topNMonitor2CarCounts.add(topNMonitor2CarCount);
        }
        // 插入数据库
        IMonitorDAO monitorDAO = DAOFactory.getMonitorDAO();
        monitorDAO.insertBatchTopN(topNMonitor2CarCounts);

        /**
         * 获取topN卡口的id 并返回RDD
         *
         */
        List<Tuple2<String, String>> topNMonitorId = new ArrayList<>();
        for (Tuple2<Integer, String> t : topNCarCount) {
            topNMonitorId.add(new Tuple2<>(t._2, t._2));
        }
        // 将tuple格式的list转成RDD
        JavaPairRDD<String, String> topNMonitorIdRDD = sc.parallelizePairs(topNMonitorId);
        return topNMonitorIdRDD;
    }

    /**
     * 获取车流量 topN 卡口的详细信息
     * 存入 topn_monitor_detail_info 表
     *
     * @param taskId
     * @param topNMonitor2CarFlow
     * @param monitor2DetailRDD
     */
    public static void getTopNDetails(Long taskId, JavaPairRDD<String, String> topNMonitor2CarFlow, JavaPairRDD<String, Row> monitor2DetailRDD) {
        /**
         * 方法1 join：topNMonitor2CarFlow join monitor2DetailRDD
         */
//        JavaPairRDD<String, Row> monitorDetailRDD = topNMonitor2CarFlow.join(monitor2DetailRDD).mapToPair(new PairFunction<Tuple2<String, Tuple2<String, Row>>, String, Row>() {
//
//            private static final long serialVersionUID = 1L;
//
//            @Override
//            public Tuple2<String, Row> call(Tuple2<String, Tuple2<String, Row>> t) throws Exception {
//                return new Tuple2<>(t._1, t._2._2);
//            }
//        });
//
//        // 将详细信息取出封装到list集合中，然后插入数据库
//        monitorDetailRDD.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Row>>>() {
//
//            private static final long serialVersionUID = 1L;
//
//            @Override
//            public void call(Iterator<Tuple2<String, Row>> t) throws Exception {
//                List<TopNMonitorDetailInfo> monitorDetailInfos = new ArrayList<>();
//                while (t.hasNext()){
//                    Tuple2<String, Row> tuple = t.next();
//                    Row row = tuple._2;
//                    TopNMonitorDetailInfo m = new TopNMonitorDetailInfo(taskId, row.getString(0), row.getString(1), row.getString(2), row.getString(3), row.getString(4), row.getString(5), row.getString(6));
//                    monitorDetailInfos.add(m);
//                }
//                // 存入topn_monitor_detail_info 表中
//                IMonitorDAO monitorDAO = DAOFactory.getMonitorDAO();
//                monitorDAO.insertBatchMonitorDetails(monitorDetailInfos);
//            }
//        });

        /**
         * 方法2：广播变量：将topN的卡口id广播出去，然后filter
         */
        // 获取topN 的monitorId
        List<String> topNMonitorId = topNMonitor2CarFlow.map(new Function<Tuple2<String, String>, String>() {
            @Override
            public String call(Tuple2<String, String> v1) throws Exception {
                return v1._1;
            }
        }).collect();

        // 获取SparkContext，任意一个RDD就可以获取
        JavaSparkContext sc = new JavaSparkContext(monitor2DetailRDD.context());
        // 将topN 的monitorId 广播出去
        Broadcast<List<String>> broadcast = sc.broadcast(topNMonitorId);
        monitor2DetailRDD.filter(new Function<Tuple2<String, Row>, Boolean>() {
            @Override
            public Boolean call(Tuple2<String, Row> tuple) throws Exception {
                return broadcast.value().contains(tuple._1);
            }
        }).foreachPartition(new VoidFunction<Iterator<Tuple2<String, Row>>>() {

            private static final long serialVersionUID = 1L;

            @Override
            public void call(Iterator<Tuple2<String, Row>> t) throws Exception {
                List<TopNMonitorDetailInfo> monitorDetailInfos = new ArrayList<>();
                while (t.hasNext()) {
                    Tuple2<String, Row> tuple = t.next();
                    Row row = tuple._2;
                    TopNMonitorDetailInfo m = new TopNMonitorDetailInfo(taskId, row.getString(0), row.getString(1), row.getString(2), row.getString(3), row.getString(4), row.getString(5), row.getString(6));
                    monitorDetailInfos.add(m);
                }
                // 存入topn_monitor_detail_info 表中
                IMonitorDAO monitorDAO = DAOFactory.getMonitorDAO();
                monitorDAO.insertBatchMonitorDetails(monitorDetailInfos);
            }
        });
    }

    /**
     * 获取经常高速通过的TOPN卡扣 , 返回车辆经常高速通过的卡扣List
     * 思路：
     * 1、对每个卡口进行聚合：高速通过的车辆数、中速通过的数量、低速通过的数量
     * 2、然后对卡口进行二次排序，高速数量多的排前面，如果相同则比较中速的数量。。。。。。
     *
     * @param monitorId2RowsRDD (monitorId ,Iterable[Row])
     * @return List<MonitorId> 返回车辆经常高速通过的卡扣List
     */
    public static List<String> speedTopNMonitor(JavaPairRDD<String, Iterable<Row>> monitorId2RowsRDD) {
        JavaPairRDD<SpeedSortKey, String> speedSortKey2MonitorId = monitorId2RowsRDD.mapToPair(new PairFunction<Tuple2<String, Iterable<Row>>, SpeedSortKey, String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Tuple2<SpeedSortKey, String> call(Tuple2<String, Iterable<Row>> tuple) throws Exception {
                String monitorId = tuple._1;
                Iterator<Row> speedIterator = tuple._2.iterator();

                // 统计卡口下高、中、正常、低速的车辆数
                long lowSpeed = 0;
                long normalSpeed = 0;
                long mediumSpeed = 0;
                long highSpeed = 0;
                while (speedIterator.hasNext()) {
                    int speed = StringUtils.convertStringtoInt(speedIterator.next().getAs("speed"));
                    if (speed >= 120) {
                        highSpeed++;
                    } else if (speed >= 90) {
                        mediumSpeed++;
                    } else if (speed >= 60) {
                        normalSpeed++;
                    } else if (speed >= 0) {
                        lowSpeed++;
                    }
                }
                SpeedSortKey speedSortKey = new SpeedSortKey(lowSpeed, normalSpeed, mediumSpeed, highSpeed);
                return new Tuple2<>(speedSortKey, monitorId);
            }
        });

        // 按自定义类SpeedSortKey排序
        JavaPairRDD<SpeedSortKey, String> sortBySpeedCount = speedSortKey2MonitorId.sortByKey(false);
        // 取出top5 经常高速的卡口id
        List<Tuple2<SpeedSortKey, String>> take = sortBySpeedCount.take(5);
        List<String> monitorIds = new ArrayList<>();

        for (Tuple2<SpeedSortKey, String> tuple : take) {
            monitorIds.add(tuple._2);
            System.out.println("monitor_id = " + tuple._2 + "-----" + tuple._1);
        }
        return monitorIds;
    }

    /**
     * 获取 车流量topN卡口中，每个卡口车速前10的车辆信息，同时存入数据库表 top10_speed_detail 中
     * <p>
     * 分组取tonN：两种方式
     * 1、原生集合排序，将每组数据存入一个list中，然后调用sort(可自定义)排序，再遍历输出前10条数据
     * 2、定长数组排序，每组数据的每条数据和数组中的每个元素对比，替换，到最后，数组中的就是topN的数据
     *
     * @param sc
     * @param taskId
     * @param top5MonitorIds
     * @param monitor2DetailRDD
     */
    public static void getMonitorDetails(JavaSparkContext sc, Long taskId, List<String> top5MonitorIds, JavaPairRDD<String, Row> monitor2DetailRDD) {

        // 广播变量 topN的monitorId
        Broadcast<List<String>> broadcast = sc.broadcast(top5MonitorIds);
        // 找出在topN中的所有卡口车流量数据
        // 然后按卡口id分组
        // 然后将遍历没组数据
        // 然后将这组数据的每条数据与定长数组中的元素对比
        monitor2DetailRDD.filter(new Function<Tuple2<String, Row>, Boolean>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Boolean call(Tuple2<String, Row> tuple) throws Exception {
                return broadcast.value().contains(tuple._1);
            }
        }).groupByKey().foreach(new VoidFunction<Tuple2<String, Iterable<Row>>>() {
            @Override
            public void call(Tuple2<String, Iterable<Row>> tuple) throws Exception {
                String monitorId = tuple._1;
                Iterator<Row> rowIterator = tuple._2.iterator();
                Row[] top10Cars = new Row[10];
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    Long speed = Long.valueOf(row.getAs("speed"));
                    // 对于没条row 的speed都要和数组中的每个元素一一比对
                    for (int i = 0; i < top10Cars.length; i++) {
                        if (top10Cars[i] == null) {
                            top10Cars[i] = row;
                            break;
                        } else {
                            long _speed = Long.valueOf(top10Cars[i].getAs("speed"));
                            // 速度大的row存在数组的前面
                            if (speed > _speed) {
                                for (int j = 9; j > i; j--) {
                                    top10Cars[j] = top10Cars[j - 1];
                                }
                                top10Cars[i] = row;
                                break;
                            }
                        }
                    }
                }

                /**
                 * 将tonN卡口中每个卡口车速前10的车辆信息存入数据库
                 */
                List<TopNMonitorDetailInfo> topNMonitorDetailInfos = new ArrayList<>();
                for (Row row : top10Cars) {
                    topNMonitorDetailInfos.add(new TopNMonitorDetailInfo(taskId, row.getString(0), row.getString(1), row.getString(2), row.getString(3), row.getString(4), row.getString(5), row.getString(6)));
                }
                IMonitorDAO monitorDAO = DAOFactory.getMonitorDAO();
                monitorDAO.insertBatchTop10Details(topNMonitorDetailInfos);
            }
        });
    }


    /**
     * 区域碰撞分析
     * <p>
     * 两个区域共同出现的车辆
     *
     * @param spark
     * @param taskParamsJsonObject
     * @param area1
     * @param area2
     */
    public static void areaCarPeng(SparkSession spark, JSONObject taskParamsJsonObject, String area1, String area2) {
        // 区域01的数据
        JavaRDD<Row> cameraRDD01 = SparkUtils.getCameraRDDByDateRangeAndArea(spark, taskParamsJsonObject, area1);
        // 区域02的数据
        JavaRDD<Row> cameraRDD02 = SparkUtils.getCameraRDDByDateRangeAndArea(spark, taskParamsJsonObject, area2);

        // 输出区域01中的车牌信息并去重
        JavaRDD<String> distinct1 = cameraRDD01.map(new Function<Row, String>() {
            @Override
            public String call(Row v1) {
                return v1.getAs("car");
            }
        }).distinct();

        // 输出区域02中的车牌信息并去重
        JavaRDD<String> distinct2 = cameraRDD02.map(new Function<Row, String>() {
            @Override
            public String call(Row v1) {
                return v1.getAs("car");
            }
        }).distinct();

        // 求交集，得出出现在两个区域的车辆信息
        distinct1.intersection(distinct2).foreach(new VoidFunction<String>() {
            @Override
            public void call(String car) {
                System.out.println("01 ，02 区域同时出现的car ***** " + car);
            }
        });
    }



}
