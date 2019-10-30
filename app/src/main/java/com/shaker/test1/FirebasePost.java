package com.shaker.test1;

import java.util.HashMap;
import java.util.Map;

// Firebase에 회원 정보를 Post하는 클래스입니다.

public class FirebasePost {
    public String ID;
    public String PASSWORD;
    public int MUSIC_V;
    public int EFFECT_V;
    public String EFFECT_N;


    public FirebasePost(String ID, String PASSWORD,int MUSIC_V, int EFFECT_V, String EFFECT_N) {
        this.ID = ID;
        this.PASSWORD = PASSWORD;
        this.MUSIC_V = MUSIC_V;
        this.EFFECT_V = EFFECT_V;
        this.EFFECT_N = EFFECT_N;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("ID", ID);
        result.put("PASSWORD", PASSWORD);
        result.put("MUSIC_V", MUSIC_V);
        result.put("EFFECT_V", EFFECT_V);
        result.put("EFFECT_N", EFFECT_N);
        return result;
    }
}
