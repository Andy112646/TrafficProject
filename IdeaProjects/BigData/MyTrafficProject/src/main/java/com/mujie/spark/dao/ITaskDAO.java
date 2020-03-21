package com.mujie.spark.dao;

import com.mujie.spark.domain.Task;

/**
 * @Auther:wjx
 * @Date:2019/7/31
 * @Description:com.mujie.spark.dao
 * @version:1.0
 *
 * 任务管理 DAO 接口
 */
public interface ITaskDAO {
    /**
     * 根据taskId查询指定任务
     * @param taskId
     * @return
     */
    Task findTaskById(long taskId);
}
