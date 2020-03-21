package com.mujie.spark.jdbc;

import com.mujie.spark.conf.ConfigurationManager;
import com.mujie.spark.constant.Constants;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @Auther:wjx
 * @Date:2019/7/31
 * @Description:com.mujie.spark.jdbc
 * @version:1.0
 */
public class JDBCHelper {
    /**
     * 静态代码块加载数据库驱动
     */
    static {
        String driver = ConfigurationManager.getProperty(Constants.JDBC_DRIVER);
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取单例对象
     *
     * @return
     */
    private static JDBCHelper instance = null;

    public static JDBCHelper getInstance() {
        if (instance == null) {
            synchronized (JDBCHelper.class) {
                if (instance == null) {
                    instance = new JDBCHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 准备数据库连接池,创建连接对象
     */
    private LinkedList<Connection> datasource = new LinkedList<Connection>();

    private JDBCHelper() {
        // 从配置文件中获取连接池大小
        int datasourceSize = ConfigurationManager.getInteger(Constants.JDBC_DATASOURCE_SIZE);
        // 创建指定数量的数据库连接，然后放入连接池中
        for (int i = 0; i < datasourceSize; i++) {
            Boolean local = ConfigurationManager.getBoolean(Constants.SPARK_LOCAL);
            String url = null;
            String user = null;
            String password = null;

            // 根据运行环境连接不同的数据库
            if (local) {
                url = ConfigurationManager.getProperty(Constants.JDBC_URL);
                user = ConfigurationManager.getProperty(Constants.JDBC_USER);
                password = ConfigurationManager.getProperty(Constants.JDBC_PASSWORD);
            } else {
                url = ConfigurationManager.getProperty(Constants.JDBC_URL_PROD);
                user = ConfigurationManager.getProperty(Constants.JDBC_USER_PROD);
                password = ConfigurationManager.getProperty(Constants.JDBC_PASSWORD_PROD);
            }

            try {
                Connection conn = DriverManager.getConnection(url, user, password);
                datasource.push(conn); // 向连接池放入一个创建好的连接
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取数据库连接对象的方法
     * <p>
     * 实现了一个简单的等待机制，避免短暂没有连接对象造成获取失败的问题
     *
     * @return
     */
    public synchronized Connection getConnection() {
        while (datasource.size() == 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 返回一个数据库连接对象
        return datasource.poll();
    }


    /**
     * 执行 增、删、改 SQL语句，返回受影响的行数
     *
     * @param sql
     * @param params
     * @return 受影响的行数
     */
    public int executeUpdate(String sql, Object[] params) {
        int rtn = 0; // 初始化受影响的行数
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // 关闭自动提交
            pstmt = conn.prepareStatement(sql);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            rtn = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                datasource.push(conn);
            }
        }
        return rtn;
    }

    /**
     * 执行查询SQL语句
     *
     * @param sql
     * @param params
     * @param callback
     */
    public void executeQuery(String sql, Object[] params,
                             QueryCallback callback) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            rs = pstmt.executeQuery();
            callback.process(rs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                datasource.push(conn);
            }
        }
    }

    /**
     * 批量执行SQL的方法
     *
     * @param sql
     * @param paramsList
     * @return
     */
    public int[] executeBatch(String sql, List<Object[]> paramsList) {
        int[] rtn = null;
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            // 第一步：取消自动提交
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);

            if(paramsList != null && paramsList.size() > 0) {
                for(Object[] params : paramsList) {
                    for(int i = 0; i < params.length; i++) {
                        pstmt.setObject(i + 1, params[i]);
                    }
                    // 第二步：使用PreparedStatement.addBatch()方法加入批量的SQL参数
                    pstmt.addBatch();
                }
            }

            // 第三步：executeBatch()方法，执行批量的SQL语句
            rtn = pstmt.executeBatch();
            // 最后一步：提交事务
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(conn != null) {
                datasource.push(conn);
            }
        }
        return rtn;
    }


    /**
     * 静态内部类：查询回调接口
     *
     * @author Administrator
     */
    public static interface QueryCallback {

        /**
         * 处理查询结果
         *
         * @param rs
         * @throws Exception
         */
        void process(ResultSet rs) throws Exception;
    }

}
