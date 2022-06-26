package com.example.aircraftwar.handle;


import com.example.aircraftwar.model.Register;
import com.example.aircraftwar.tool.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HandleRegister {
   Connection con;
   PreparedStatement preSql;  
   public HandleRegister(){
       try {
           con = DBUtils.getConnect();
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

    /**
     * 尝试注册用户到数据库
     * @param register 用户信息
     * @return 返回为0，id重复；否则注册成功
     */
   public int tryRegister(Register register) {
      String sqlStr ="insert into user values(?,?)";
      int ok = 0;
      try { 
          preSql = con.prepareStatement(sqlStr);
          preSql.setString(1,register.getID());
          preSql.setString(2,register.getPassword());
          ok = preSql.executeUpdate();
          con.close();
      }
      catch(SQLException e) {
          System.out.println("id不能重复！");
      }
      if(ok!=0) {
          System.out.println("注册成功");
      }

      return ok;
   }
}
