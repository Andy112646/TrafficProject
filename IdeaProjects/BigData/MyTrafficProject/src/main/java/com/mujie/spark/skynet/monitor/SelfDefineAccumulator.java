package com.mujie.spark.skynet.monitor;

import com.mujie.spark.constant.Constants;
import com.mujie.spark.util.StringUtils;
import org.apache.spark.util.AccumulatorV2;

/**
 * @Auther:wjx
 * @Date:2019/7/31
 * @Description:com.mujie.spark.skynet
 * @version:1.0
 */
public class SelfDefineAccumulator extends AccumulatorV2<String, String> {
    String returnResult = "";

    /**
     * 与 reset() 方法中保持一致
     *
     * @return
     */
    @Override
    public boolean isZero() {
        return "normalMonitorCount=0|normalCameraCount=0|abnormalMonitorCount=0|abnormalCameraCount=0|abnormalMonitorCameraInfos= ".equals(returnResult);
    }

    /**
     * 分区各自创建拷贝一个初始化的值
     *
     * @return
     */
    @Override
    public AccumulatorV2<String, String> copy() {
        SelfDefineAccumulator acc = new SelfDefineAccumulator();
        acc.returnResult = this.returnResult;
        return acc;
    }

    /**
     * 设置每个分区的初始值
     */
    @Override
    public void reset() {
        returnResult = Constants.FIELD_NORMAL_MONITOR_COUNT + "=0|"
                + Constants.FIELD_NORMAL_CAMERA_COUNT + "=0|"
                + Constants.FIELD_ABNORMAL_MONITOR_COUNT + "=0|"
                + Constants.FIELD_ABNORMAL_CAMERA_COUNT + "=0|"
                + Constants.FIELD_ABNORMAL_MONITOR_CAMERA_INFOS + "= ";
    }

    /**
     * 每个分区会拿着 reset 初始化的值 ，在各自的分区内相加
     *
     * @param v
     */
    @Override
    public void add(String v) {
        returnResult = myAdd(returnResult, v);
    }

    /**
     * 每个分区最终的结果和初始值 returnResult=""  做累加
     *
     * @param other
     */
    @Override
    public void merge(AccumulatorV2<String, String> other) {
        SelfDefineAccumulator accumulator = (SelfDefineAccumulator) other;
        returnResult = myAdd(returnResult, accumulator.returnResult);
    }

    @Override
    public String value() {
        return returnResult;
    }

    /**
     * 具体的累加方法
     *
     * v1 和 v2 累加，结果作为下一次的v1，一条一条继续累加
     *
     * @param v1
     * @param v2
     * @return
     */
    private String myAdd(String v1, String v2) {
        // 第一条数据因为前面没有返回值，所以 v1为空，将v2返回作为下一次计算的v1
        if (StringUtils.isEmpty(v1)) {
            return v2;
        }

        /**
         * v1: normalMonitorCount=0|normalCameraCount=0|abnormalMonitorCount=1|abnormalCameraCount=3|abnormalMonitorCameraInfos= ~"0001":07553,07554,07556~"0001":07553,07554,07556~"0001":07553,07554,07556~"0001":07553,07554,07556
         * 将每一条数据按 | 分割成多个字段
         * 每个字段再以 = 分割，获取field字段 和 对应的value
         */
        String[] valArr = v2.split("\\|");
        for (String string : valArr) {
            String[] fieldAndValArr = string.split("=");
            String field = fieldAndValArr[0]; // abnormalMonitorCameraInfos
            String value = fieldAndValArr[1]; // "0001":07553,07554,07556
            // getFieldFromConcatString方法传入三个参数（要分割的string、以什么符号分割、获取哪个field），返回要获取的field字段值
            String oldVal = StringUtils.getFieldFromConcatString(v1, "\\|", field);//" "
            if (oldVal != null) {
                // abnormalMonitorCameraInfos 字段是字符串，每一条中的值都是" ~"开头
                // 累加的时候只保留最前面的空格，后面的只以“~”分隔
                if(Constants.FIELD_ABNORMAL_MONITOR_CAMERA_INFOS.equals(field)){
                    if(value.startsWith(" ~")) {
                        value = value.substring(2);
                    }
                    v1 = StringUtils.setFieldInConcatString(v1, "\\|", field, oldVal + "~" + value);
                }else{
                    //其余都是int类型，直接加减就可以
                    int newVal = Integer.parseInt(oldVal)+Integer.parseInt(value);
                    v1 = StringUtils.setFieldInConcatString(v1, "\\|", field, String.valueOf(newVal));
                }
            }
        }
        return v1;
    }
}
