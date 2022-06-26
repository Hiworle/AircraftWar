package com.example.aircraftwar.data.account;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String userName;
    private String password;

    private int score;
    private Map<String, Integer> toolPack ;


    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.score = 500;
        this.toolPack = new HashMap<>();
        toolPack.put("fireTool", 0);
        toolPack.put("HpTool", 0);
        toolPack.put("MissileTool", 0);
        toolPack.put("shieldTool", 0);
    }

    public User() {
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int score){
        this.score = this.score + score;
    }

    public void decreaseScore(int score){
        this.score = this.score - score;
    }

    public Map<String, Integer> getToolPack() {
        return toolPack;
    }

    public void setToolPack(Map<String, Integer> toolPack) {
        this.toolPack = toolPack;
    }
    public void setToolPack(String name, int num) {
        int num_ = toolPack.get(name) + num;
        toolPack.put(name, num_);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString(){


        return this.getUserName() + "!" + this.getPassword() + "!"
                + this.getToolPack().toString().replaceAll("[{]","").replaceAll("[}]","")
                + "!" + this.getScore() + "!";
    }
}
