package com.example.aircraftwar.tool;

import java.sql.*;
public class GetDBConnection {
    public static Connection connectDB(String DBName,String id,String password) {
        try{  //加载JDBC-MySQL5.1连接器:
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch(Exception e){
            System.out.println("加载连接器失败！");
        }
        Connection con = null;
        String uri = "jdbc:mysql://127.0.0.1:3306/"+DBName;
//                +"?"+ "useSSL=false&serverTimezone=CST&characterEncoding=utf-8";
        try{
            con = DriverManager.getConnection(uri,id,password); //连接
        }
        catch(SQLException e){
            System.out.println("连接失败！");
            System.out.println(e);
        }
        return con;
    }
}
