package com.shaker.test1;

// 음악 목록의 기본 정보를 선언하는 클래스입니다.

public class Music_info {
    public int drawableId;
    public String title;
    public String artist;
    public String time;
    public boolean star_box;

    public Music_info(int drawableId, String title, String artist, String time, boolean star_box){

        this.drawableId = drawableId;
        this.title = title;
        this.artist = artist;
        this.time = time;
        this.star_box = star_box;

    }
}
