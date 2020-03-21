package com.bjsxt.jdbc;

import java.sql.*;
import java.util.Scanner;

/**
 * @Auther:wjx
 * @Date:2019/4/22
 * @Description:com.bjsxt.jdbc
 * @version:1.0
 */
public class HelloTest05_login {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入用户名：");
        String username = scanner.next();
        System.out.println("请输入密码：");
        String pwd = scanner.next();

        String url = "jdbc:mysql://localhost:3306/db_scott?useSSL=false";
        String user = "root";
        String password = "admin";
        String sql = "select name from tb_user where username = '" +username+ "' and pwd = '" +pwd+ "'";
        System.out.println(sql);

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            // 加载驱动
            Class.forName("com.mysql.jdbc.Driver");
            // 获取数据库连接
            connection = DriverManager.getConnection(url, user, password);
            // 获取发送器
            statement = connection.createStatement();
            // 发送sql获取结果
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()){
                System.out.println("登录成功！");
            }else{
                System.out.println("登录失败!");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            // 关闭资源
            if (resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (statement != null){
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
