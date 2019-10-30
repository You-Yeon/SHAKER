package com.shaker.test1;

import java.util.HashMap;
import java.util.Map;

// Firebase에 게임 기록을 Post하는 클래스입니다.

public class FirebasePost_record {
    public int MUSIC_ID;
    public int STAR;
    public String TIME;
    public int SCORE;
    public int RANK;


    public FirebasePost_record(int MUSIC_ID, int STAR,String TIME, int SCORE, int RANK) {
        this.MUSIC_ID = MUSIC_ID;
        this.STAR = STAR;
        this.TIME = TIME;
        this.SCORE = SCORE;
        this.RANK = RANK;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("MUSIC_ID", MUSIC_ID);
        result.put("STAR", STAR);
        result.put("TIME", TIME);
        result.put("SCORE", SCORE);
        result.put("RANK", RANK);
        return result;
    }
}
