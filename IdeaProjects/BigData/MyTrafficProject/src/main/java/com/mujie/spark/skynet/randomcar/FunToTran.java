package com.mujie.spark.skynet.randomcar;

import com.alibaba.fastjson.JSONObject;
import com.mujie.spark.constant.Constants;
import com.mujie.spark.dao.ICarTrackDAO;
import com.mujie.spark.dao.IRandomExtractDAO;
import com.mujie.spark.dao.factory.DAOFactory;
import com.mujie.spark.domain.CarTrack;
import com.mujie.spark.domain.RandomExtractCar;
import com.mujie.spark.domain.RandomExtractMonitorDetail;
import com.mujie.spark.util.DateUtils;
import com.mujie.spark.util.ParamUtils;
import com.mujie.spark.util.StringUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Row;
import scala.Tuple2;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther:wjx
 * @Date:2019/8/4
 * @Description:com.mujie.spark.skynet.randomcar
 * @version:1.0
 */
public class FunToTran {
    /**
     * @param sc
     * @param taskId
     * @param params
     * @param cameraRDD
     * @return
     */
    public static JavaPairRDD<String, Row> randomExtractCarInfo(JavaSparkContext sc, long taskId, JSONObject params, JavaRDD<Row> cameraRDD) {
        /**
         * 将原始的卡口信息提取转化成需要的格式
         * 返回格式：("dateHour"="2019-07-30_08","car"="京A63967")
         */
        JavaPairRDD<String, String> dateHourCar2DetailRDD = cameraRDD.mapToPair(new PairFunction<Row, String, String>() {
            @Override
            public Tuple2<String, String> call(Row row) throws Exception {
                String actionTime = row.getAs("action_time");
                String dateHour = DateUtils.getDateHour(actionTime); // 2019-07-30_08
                String car = row.getAs("car");
                String key = Constants.FIELD_DATE_HOUR + "=" + dateHour;
                String value = Constants.FIELD_CAR + "=" + car;
                return new Tuple2<>(key, value);
            }
        });

        /**
         * 返回格式：("京A63967",Row)
         *
         */
        JavaPairRDD<String, Row> car2DetailRDD = cameraRDD.mapToPair(new PairFunction<Row, String, Row>() {
            @Override
            public Tuple2<String, Row> call(Row row) throws Exception {
                String car = row.getAs("car");
                return new Tuple2<>(car, row);
            }
        });

        /**
         * 对同一时间段内的数据做去重处理
         * 也就是规定每个时间段同一车辆只出现一次
         */
        JavaPairRDD<String, String> dateHour2DetailRDD = dateHourCar2DetailRDD.distinct();

        /**
         * 去重后的各个小时段的总的车流量
         * 返回格式：("dateHour"="2019-07-30_08", 车辆数)
         */
        Map<String, Long> countByKey = dateHour2DetailRDD.countByKey();

        Map<String, Map<String, Long>> dateHourCountMap = new HashMap<>();
        for (Map.Entry<String, Long> entry : countByKey.entrySet()) {
            String dateHour = entry.getKey();
            String[] dateHourSplit = dateHour.split("_");
            String date = dateHourSplit[0];
            String hour = dateHourSplit[1];
            Long count = Long.parseLong(String.valueOf(entry.getValue()));
            Map<String, Long> hourCountMap = dateHourCountMap.get(date);
            if (hourCountMap == null) {
                hourCountMap = new HashMap<String, Long>();
                // key是按天区分
                dateHourCountMap.put(date, hourCountMap);
            }
            hourCountMap.put(hour, count);
        }
        // 获取要抽取的车辆数
        int extractNums = Integer.parseInt(ParamUtils.getParam(params, Constants.FIELD_EXTRACT_NUM));

        /**
         * 每天应抽取的车辆数：
         * 抽取总数 / 天数
         */
        int extractNumPerDay = extractNums / dateHourCountMap.size();

        /**
         * 记录每天每小时抽取索引的集合
         * Map<"日期"，Map<"小时段"，List<Integer>(抽取数据索引)>>
         */
        Map<String, Map<String, List<Integer>>> dateHourExtractMap = new HashMap<>();
        Random random = new Random();
        for (Map.Entry<String, Map<String, Long>> entry : dateHourCountMap.entrySet()) {
            String date = entry.getKey();
            Map<String, Long> hourCountMap = entry.getValue();
            // 计算出这一天总的车流量
            long dateCarCount = 0L;
            Collection<Long> values = hourCountMap.values();
            for (long tmpHourCount : values) {
                dateCarCount += tmpHourCount;
            }
            /**
             * 小时段对应的应该抽取车辆的索引集合
             * hourExtractMap ---- Map<小时，List<>>
             */
            Map<String, List<Integer>> hourExtractMap = dateHourExtractMap.get(date);
            if (hourExtractMap == null) {
                hourExtractMap = new HashMap<String, List<Integer>>();
                dateHourExtractMap.put(date, hourExtractMap);
            }

            /**
             * 遍历的是每个小时对应的车流量总数信息
             * hourCountMap  key:hour  value:carCount
             */
            for (Map.Entry<String, Long> hourCountEntry : hourCountMap.entrySet()) {
                // 当前小时段
                String hour = hourCountEntry.getKey();
                // 当前小时段对应的真实的车辆数
                long hourCarCount = hourCountEntry.getValue();
                // 计算出这个小时的车流量占总车流量的百分比,然后计算出在这个时间段内应该抽取出来的车辆信息的数量
                int hourExtractNum = (int) (((double) hourCarCount / (double) dateCarCount) * extractNumPerDay);

                // 由于存在小数问题，所以当要抽取的数量大于当前时段总车流量，则取当前时段总车流量
                if (hourExtractNum >= hourCarCount) {
                    hourExtractNum = (int) hourCarCount;
                }
                List<Integer> extractIndexs = hourExtractMap.get(hour);
                if (extractIndexs == null) {
                    extractIndexs = new ArrayList<Integer>();
                    hourExtractMap.put(hour, extractIndexs);
                }

                /**
                 * 根据每个小时段需要抽取的车辆数作为循环次数，
                 * 该时段总车流量数作为随机数范围，生成一系列的随机数，作为抽取的车辆信息的下标
                 */
                for (int i = 0; i < hourExtractNum; i++) {
                    int index = random.nextInt((int) hourCarCount);
                    while (extractIndexs.contains(index)) {
                        index = random.nextInt((int) hourCarCount);
                    }
                    extractIndexs.add(index);
                }
            }
        }
        Broadcast<Map<String, Map<String, List<Integer>>>> dateHourExtractBroadcast = sc.broadcast(dateHourExtractMap);
        /**
         * 在dateHour2DetailRDD中进行随机抽取车辆信息：
         * 1、按照date_hour进行分组
         * 2、然后对组内的信息按照 dateHourExtractBroadcast 参数抽取相应的车辆信息
         * 3、抽取出来的结果直接放入到MySQL数据库中。
         *
         * extractCarRDD 就是抽取出来的所有车辆
         */
        JavaPairRDD<String, String> extractCarRDD = dateHour2DetailRDD.groupByKey().flatMapToPair(
                new PairFlatMapFunction<Tuple2<String, Iterable<String>>, String, String>() {

                    @Override
                    public Iterator<Tuple2<String, String>> call(Tuple2<String, Iterable<String>> t) throws Exception {
                        List<Tuple2<String, String>> list = new ArrayList<>();
                        List<RandomExtractCar> carRandomExtracts = new ArrayList<>();
                        String dateHour = t._1;
                        Iterator<String> iterator = t._2.iterator();
                        String date = dateHour.split("_")[0];
                        String hour = dateHour.split("_")[1];

                        Map<String, Map<String, List<Integer>>> dateHourExtractMap = dateHourExtractBroadcast.value();
                        List<Integer> indexList = dateHourExtractMap.get(date).get(hour);
                        int index = 0;
                        while (iterator.hasNext()) {
                            String car = iterator.next().split("=")[1];
                            if (indexList.contains(index)) {
                                RandomExtractCar carRandomExtract = new RandomExtractCar(taskId, car, date, dateHour);
                                carRandomExtracts.add(carRandomExtract);
                                list.add(new Tuple2<>(car, car));
                            }
                            index++;
                        }
                        /**
                         * 将抽取出来的车辆信息插入到random_extract_car表中
                         */
                        IRandomExtractDAO randomExtractDAO = DAOFactory.getRandomExtractDAO();
                        randomExtractDAO.insertBatchRandomExtractCar(carRandomExtracts);
                        return list.iterator();
                    }
                });

        /**
         * 由抽取的car获取相应的车辆的详细信息
         */
        JavaPairRDD<String, Row> randomExtractCar2DetailRDD =
                extractCarRDD.distinct().join(car2DetailRDD).mapPartitionsToPair(
                        new PairFlatMapFunction<Iterator<Tuple2<String, Tuple2<String, Row>>>, String, Row>() {
                            @Override
                            public Iterator<Tuple2<String, Row>> call(Iterator<Tuple2<String, Tuple2<String, Row>>> iterator) throws Exception {
                                List<RandomExtractMonitorDetail> randomExtractMonitorDetails = new ArrayList<>();
                                List<Tuple2<String, Row>> list = new ArrayList<>();
                                while (iterator.hasNext()) {
                                    Tuple2<String, Tuple2<String, Row>> tuple = iterator.next();
                                    Row row = tuple._2._2;
                                    String car = tuple._1;
                                    RandomExtractMonitorDetail m = new RandomExtractMonitorDetail(taskId, row.getString(0), row.getString(1), row.getString(2), row.getString(3), row.getString(4), row.getString(5), row.getString(6));
                                    randomExtractMonitorDetails.add(m);
                                    list.add(new Tuple2<>(car, row));
                                }
                                // 将车辆详细信息插入random_extract_car_detail_info表中。
                                IRandomExtractDAO randomExtractDAO = DAOFactory.getRandomExtractDAO();
                                randomExtractDAO.insertBatchRandomExtractDetails(randomExtractMonitorDetails);
                                return list.iterator();
                            }
                        });
        return randomExtractCar2DetailRDD;
    }

    /**
     * 获取车辆的行驶轨迹
     *
     * @param taskId
     * @param randomExtractCar2DetailRDD
     * @return (car, " dateHour = 2019 - 07 - 01 | carTrack = monitor_id, monitor_id, monitor_id... ")
     */
    public static JavaPairRDD<String, String> getCarTrack(long taskId, JavaPairRDD<String, Row> randomExtractCar2DetailRDD) {
        JavaPairRDD<String, Iterable<Row>> groupByCar = randomExtractCar2DetailRDD.groupByKey();
        JavaPairRDD<String, String> carTrackRDD =
                groupByCar.mapToPair(new PairFunction<Tuple2<String, Iterable<Row>>, String, String>() {

                    @Override
                    public Tuple2<String, String> call(Tuple2<String, Iterable<Row>> tuple) throws Exception {
                        String car = tuple._1;
                        Iterator<Row> carMetailsIterator = tuple._2.iterator();
                        List<Row> rows = IteratorUtils.toList(carMetailsIterator);
                        Collections.sort(rows, new Comparator<Row>() {
                            @Override
                            public int compare(Row r1, Row r2) {
                                if (DateUtils.after(r1.getAs("action_time"), r2.getAs("action_time"))) {
                                    return 1;
                                }
                                return -1;
                            }
                        });

                        StringBuilder carTrack = new StringBuilder();
                        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        for (Row row : rows) {
                            carTrack.append("," + row.getAs("monitor_id"));
                        }
                        return new Tuple2<>(car, Constants.FIELD_DATE + "=" + date + "|" + Constants.FIELD_CAR_TRACK + "=" + carTrack.substring(1));
                    }
                });
        return carTrackRDD;
    }

    public static void saveCarTrack2DB(long taskId, JavaPairRDD<String, String> carTrackRDD) {
        carTrackRDD.foreachPartition(new VoidFunction<Iterator<Tuple2<String,String>>>() {

            @Override
            public void call(Iterator<Tuple2<String, String>> iterator) throws Exception {
                List<CarTrack> carTracks = new ArrayList<>();
                while (iterator.hasNext()) {
                    Tuple2<String, String> tuple = iterator.next();
                    String car = tuple._1;
                    String dateAndCarTrack = tuple._2;
                    String date = StringUtils.getFieldFromConcatString(dateAndCarTrack, "\\|", Constants.FIELD_DATE);
                    String track = StringUtils.getFieldFromConcatString(dateAndCarTrack, "\\|",Constants.FIELD_CAR_TRACK);
                    CarTrack carTrack = new CarTrack(taskId, date,car, track);
                    carTracks.add(carTrack);
                }
                //将车辆的轨迹存入数据库表car_track中
                ICarTrackDAO carTrackDAO = DAOFactory.getCarTrackDAO();
                carTrackDAO.insertBatchCarTrack(carTracks);
            }
        });
    }
}
