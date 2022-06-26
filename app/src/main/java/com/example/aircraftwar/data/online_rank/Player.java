package com.example.aircraftwar.data.online_rank;

import java.io.Serializable;

public class Player implements Serializable {

    private int ranking = 0;
    private String yourName;
    private String otherName;
    private int yourScore;
    private int otherScore;
    private int score;

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public String getYourName() {
        return yourName;
    }

    public void setYourName(String yourName) {
        this.yourName = yourName;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public int getYourScore() {
        return yourScore;
    }

    public void setYourScore(int yourScore) {
        this.yourScore = yourScore;
    }

    public int getOtherScore() {
        return otherScore;
    }

    public void setOtherScore(int otherScore) {
        this.otherScore = otherScore;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Player(String yourName, String otherName, int yourScore, int otherScore) {
        this.yourName = yourName;
        this.otherName = otherName;
        this.yourScore = yourScore;
        this.otherScore = otherScore;
        this.score = yourScore+otherScore;
    }

    public Player() {
    }

    @Override
    public String toString() {
        return
               "你: " + yourName + " 和队友: " + otherName
               + " , 共同得分:" + score;
    }
}
