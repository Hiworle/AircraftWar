package com.example.aircraftwar.tool;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;

public class IOUtil {
    /**
     * 获取string输入
     * @param is 输入流
     * @return 输入的string
     */
    public static String readString(InputStream is) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));//包装为缓冲字符流
        String info = null;
        try {
            info = bufferedReader.readLine();

        } catch (SocketException e){
            return null;
        } catch (Exception e1) {
            Log.e("IOUtil ERROR", "cant read line");
        }

        return info;
    }

    /**
     * 发送string
     * @param os 输出流
     * @param s 要发送的字符串
     */
    public static void writeString(OutputStream os,String s) {
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os, "UTF-8")), true);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        printWriter.println(s);
        printWriter.flush();
    }
}

