package com.example.aircraftwar.handle;


import com.example.aircraftwar.model.Login;
import com.example.aircraftwar.tool.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HandleLogin {
   Connection con;
   PreparedStatement preSql;
   ResultSet rs;  
   public HandleLogin(){
       try {
           con = DBUtils.getConnect();
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
   public Login loginVerify(Login loginModel) {
      String id = loginModel.getID();
      String pw = loginModel.getPassword();
      String sqlStr ="select id,password from user where "+
                      "id = ? and password = ?";
      try { 
          preSql = con.prepareStatement(sqlStr);
          preSql.setString(1,id);
          preSql.setString(2,pw);  
          rs = preSql.executeQuery(); 
          if(rs.next()==true) {
             loginModel.setLoginSuccess(true);
              System.out.println("登陆成功");
          }
          else {
             loginModel.setLoginSuccess(false);
              System.out.println("登陆失败");
          }
          con.close();
      }
      catch(SQLException e) {
          System.out.println(e);
      }
      return loginModel;
   }
}
