package com.shaker.test1;

// 기록의 기본 정보를 선언하는 클래스입니다.

public class Record_info {

    public int drawableId;
    public int difficulty;
    public String time;
    public int score;
    public int rank;

    public Record_info(int drawableId, int difficulty, String time, int score, int rank){

        this.drawableId = drawableId;
        this.difficulty = difficulty;
        this.time = time;
        this.score = score;
        this.rank = rank;

    }
}
