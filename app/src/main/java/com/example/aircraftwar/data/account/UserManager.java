package com.example.aircraftwar.data.account;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserManager
{
    private String file = "users.txt";
    private String root_path = "/data/user/0/com.example.aircraftwar/files/";

    //创建本地数据文件
    FileInputStream fis = null;
    FileOutputStream fos = null;
    File userFile;

    //创建一个List来缓存User信息
    List<User> userList = new ArrayList<>();

    public UserManager()
    {
        userFile = new File(root_path+file);

    }

    //保存文件
    public void save() throws Exception
    {
        fos = new FileOutputStream(userFile);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
        //每行存储一个用户的信息
        for (User u : userList)
        {
            String line = u.toString();
            writer.write(line);
            writer.newLine();
        }
        writer.close();
        fos.close();
    }

    //从文件加载
    public void load() throws Exception
    {
        fis = new FileInputStream(userFile);

        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

        while (true)
        {

            String line = reader.readLine();
            Log.i("00000000000000000000",line);//打印所有玩家信息
            if (line == null){
                break;
            }
            String[] cols = line.split("!");
            if (cols.length<2) continue;
            User user = new User();
            user.setUserName(cols[0].trim());
            user.setPassword(cols[1].trim());
            user.setToolPack(mapStringToMap(cols[2]));
            user.setScore(Integer.parseInt(cols[3]));
            userList.add( user );

        }
        reader.close();
    }

    //注册一个新用户
    public void add(User u)
    {
        userList.add(u);
    }

    // 按名称查找
    public User find(String username)
    {
        for (User u : userList)
        {
            if(u.getUserName().equals(username))
            {
                return u;
            }
        }
        return null;
    }

    /**将字符串转成map*/
    public  Map<String,Integer> mapStringToMap(String stringMap){
        Map<String,Integer> map = new HashMap<>();
        String[] strings = stringMap.split(",");
        for (String str : strings) {
            str = str.trim();
            String[] s = str.split("=");
            map.put(s[0],Integer.parseInt(s[1]));
        }
        return map;
    }

    public void updateList(User user){
        userList.remove(find(user.getUserName()));
        userList.add(user);
        try {
            save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}