package com.shaker.test1;

// 난이도의 기본 정보를 선언하는 클래스입니다.

public class Difficulty_info {

    public int drawableId;
    public int star;
    public String note_num;

    public Difficulty_info(int drawableId, int star, String note_num){

        this.drawableId = drawableId;
        this.star = star;
        this.note_num  = note_num;

    }
}
