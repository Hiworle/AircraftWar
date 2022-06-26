package com.example.aircraftwar.data.rank;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Record implements Serializable {


    /**用户名从登录界面获取*/
    private int ranking = 0;
    private String name;
    private int score;
    private String date;
    /**设置序列号*/
    private static final long serialVersionUID = 11289300;

    /**对应属性的getter和setter*/
    public String getName() {
        return name;
    }
    /**摒弃*/
    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public Record() {
    }
    public Record(String name, int score, String date) {
        this.name = name;
        this.score = score;
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        return
                "用户:'" + name + '\'' +
                ", 此次得分:" + score +
                "    " + date + '\'' ;
    }
}
