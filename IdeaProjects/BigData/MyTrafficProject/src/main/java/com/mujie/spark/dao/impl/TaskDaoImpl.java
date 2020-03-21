package com.mujie.spark.dao.impl;

import com.mujie.spark.dao.ITaskDAO;
import com.mujie.spark.domain.Task;
import com.mujie.spark.jdbc.JDBCHelper;
import com.mujie.spark.jdbc.JDBCHelper.QueryCallback;

import java.sql.ResultSet;

/**
 * @Auther:wjx
 * @Date:2019/7/31
 * @Description:com.mujie.spark.dao.impl
 * @version:1.0
 *
 * 任务管理DAO 实现类
 */
public class TaskDaoImpl implements ITaskDAO {
    @Override
    public Task findTaskById(long taskId) {
        final Task task = new Task();

        String sql = "select * from task where task_id = ?";

        Object[] params = new Object[]{taskId};

        JDBCHelper jdbcHelper = JDBCHelper.getInstance();
        jdbcHelper.executeQuery(sql, params, new QueryCallback() {

            @Override
            public void process(ResultSet rs) throws Exception {
                if(rs.next()) {
                    long taskid = rs.getLong(1);
                    String taskName = rs.getString(2);
                    String createTime = rs.getString(3);
                    String startTime = rs.getString(4);
                    String finishTime = rs.getString(5);
                    String taskType = rs.getString(6);
                    String taskStatus = rs.getString(7);
                    String taskParam = rs.getString(8);

                    task.setTaskId(taskid);
                    task.setTaskName(taskName);
                    task.setCreateTime(createTime);
                    task.setStartTime(startTime);
                    task.setFinishTime(finishTime);
                    task.setTaskType(taskType);
                    task.setTaskStatus(taskStatus);
                    task.setTaskParams(taskParam);
                }
            }
        });
        return task;
    }

}