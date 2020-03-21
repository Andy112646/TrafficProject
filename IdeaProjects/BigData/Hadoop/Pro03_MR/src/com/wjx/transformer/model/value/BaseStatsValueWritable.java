package com.wjx.transformer.model.value;

import com.wjx.common.KpiType;
import org.apache.hadoop.io.Writable;

/**
 * 自定义顶级的输出value父类
 * 
 * @author root
 *
 */
public abstract class BaseStatsValueWritable implements Writable {
    /**
     * 获取当前value对应的kpi值
     * 
     * @return
     */
    public abstract KpiType getKpi();
}
