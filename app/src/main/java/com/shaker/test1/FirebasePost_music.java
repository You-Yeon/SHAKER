package com.shaker.test1;

import java.util.HashMap;
import java.util.Map;

// Firebase에 음악 정보를 Post하는 클래스입니다.

public class FirebasePost_music {
    public int MUSIC_ID;
    public String TITLE;
    public String ARTIST;
    public String TIME;
    public String FAV;


    public FirebasePost_music(int MUSIC_ID, String TITLE,String ARTIST, String TIME, String FAV) {
        this.MUSIC_ID = MUSIC_ID;
        this.TITLE = TITLE;
        this.ARTIST = ARTIST;
        this.TIME = TIME;
        this.FAV = FAV;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("MUSIC_ID", MUSIC_ID);
        result.put("TITLE", TITLE);
        result.put("ARTIST", ARTIST);
        result.put("TIME", TIME);
        result.put("FAV", FAV);
        return result;
    }
}
