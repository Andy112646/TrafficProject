package com.wjx.area.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @Auther:wjx
 * @Date:2019/4/23
 * @Description:com.bjsxt.util JDBC操作工具类
 * @version:1.0
 */
public class DBUtil {
    private static String url;
    private static String user;
    private static String password;
    private static String driver;

    static {
        try {
            // 创建Properties对象
            Properties prop = new Properties();
            // 加载配置文件
            prop.load(DBUtil.class.getClassLoader().getResourceAsStream("db.properties"));
            // 从配置文件中读取数据并为属性赋值
            driver = prop.getProperty("mysql.driver").trim();
            url = prop.getProperty("mysql.url").trim();
            user = prop.getProperty("mysql.user").trim();
            password = prop.getProperty("mysql.password").trim();
            // 加载驱动
            Class.forName(driver);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     *
     * @return
     */
    public static Connection getConn() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("连接获取失败, 请检查[url: " + url + "], [user: " + user + "], [password: " + password + "]");
        }
        return conn;
    }

    /**
     * 获取发送器
     *
     * @param conn
     * @return
     */
    public static Statement getStmt(Connection conn) {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stmt;
    }

    /**
     * 获取预处理发送器
     *
     * @param conn
     * @param sql
     * @return
     */
    public static PreparedStatement getPstmt(Connection conn, String sql) {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pstmt;
    }

    /**
     * 动态绑定参数
     *
     * @param pstmt
     * @param params
     */
    public static void bindParam(PreparedStatement pstmt, Object... params) {
        try {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 封装DML操作，返回int类型结果
     *
     * @param sql
     * @param params
     * @return
     */
    public static int executeDML(String sql, Object... params) {
        // 声明int
        int n = 0;
        // 创建连接
        Connection conn = getConn();
        // 创建发送器
        PreparedStatement pstmt = getPstmt(conn, sql);
        // 给sql中的?赋值
        try {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            // 发送sql获取结果
            n = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(null, pstmt, conn);
        }
        return n;
    }

    /**
     * 支持多表联查，返回的是双重List集合
     *
     * @param sql
     * @param params
     * @return
     */
    public static List executeQueryMultiple(String sql, Object... params) {
        // 声明返回结果
        List list = new ArrayList();
        // 连接对象
        Connection conn = getConn();
        // 发送器
        PreparedStatement pstmt = getPstmt(conn, sql);
        // 动态绑定参数
        bindParam(pstmt, params);
        // 声明结果集
        ResultSet res = null;
        try {
            // 执行得到结果集
            res = pstmt.executeQuery();
            // 获取元数据
            ResultSetMetaData metaData = res.getMetaData();
            // 处理结果集
            while (res.next()) {
                ArrayList arrayList = new ArrayList();
                // 为对象的属性赋值， 属性未知, set方法未知
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    // 通过属性名来获取属性值
                    Object objValue = res.getObject(i);
                    arrayList.add(objValue);
                }
                // 加入list集合
                list.add(arrayList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.closeAll(res, pstmt, conn);
        }
        return list;
    }

    /**
     * 查询集合对象的方法
     *
     * @param clazz
     * @param sql
     * @param params
     * @param <T>
     * @return
     */
    public static <T> List<T> executeQuery(Class<T> clazz, String sql, Object... params) {
        // 声明返回结果
        List<T> list = new ArrayList<>();
        // 连接对象
        Connection conn = getConn();
        // 发送器
        PreparedStatement pstmt = getPstmt(conn, sql);
        // 动态绑定参数
        bindParam(pstmt, params);
        // 声明结果集
        ResultSet res = null;
        try {
            // 执行得到结果集
            res = pstmt.executeQuery();
            // 获取元数据
            ResultSetMetaData metaData = res.getMetaData();
            // 处理结果集
            while (res.next()) {
                // 为每条数据创建一个对象存储数据
                T t = clazz.newInstance();
                // 为对象的属性赋值， 属性未知, set方法未知
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String column = metaData.getColumnLabel(i).toLowerCase();
                    // 通过属性名来获取属性值
                    Object objValue = res.getObject(column);
                    // 对每一个值进行空值判断，在使用反射调用set方法为属性赋值时，若为null则会报错
                    if (objValue != null) {
                        // 反射获取属性
                        Class<?> type = clazz.getDeclaredField(column).getType();
                        // 拼接对应该列的set方法
                        String setMethod = "set" + column.substring(0, 1).toUpperCase() + column.substring(1);
                        // 反射获取set方法
                        Method method = clazz.getMethod(setMethod, type);
                        // 反射调用set方法为属性赋值
                        method.invoke(t, objValue);
                    }
                }
                // 加入list集合
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(res, pstmt, conn);
        }
        return list;
    }

    /**
     * 统一关闭资源
     *
     * @param rs
     * @param pstmt
     * @param conn
     */
    public static void closeAll(ResultSet rs, Statement pstmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
