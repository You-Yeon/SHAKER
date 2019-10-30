package com.shaker.test1;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

// 스플레쉬 화면 클래스입니다.

public class SplashAT extends AppCompatActivity {

    static SharedPreferences sharePref = null; // 쉐어드 프리페런스
    static SharedPreferences.Editor editor = null; // 저장소 조작기

    public MediaPlayer mp;
    private static JSONArray jsonArray; // JSON 배열
    private static String JSON_string; // JSON 배열형식으로 바꾸기

    float change_volume = 0.8f; // 볼륨 설정

    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharePref = getSharedPreferences("DATA",MODE_PRIVATE);  // "DATA"에서 가져오기
        editor = sharePref.edit(); // "DATA" 조작기

        mp = MediaPlayer.create(SplashAT.this, R.raw.intro); // 인트로 소리 나게 하기
        mp.setLooping(false); // 한번만

        if(sharePref.getBoolean("Auto_Login",false)){ // 자동로그인인 경우

            // 사운드 크기 설정

            // --------------------------------
            //     회원 정보를 JSON으로 추출
            // --------------------------------
            boolean frist = true; // 처음

            JSON_string =""; // 저장 공간 비우기
            Map<String, ?> totalValue = sharePref.getAll();// 저장소에 있는 정보를 다 넣기
            for (Map.Entry<String, ?> entry : totalValue.entrySet()) {
                if( !entry.getKey().equals("Auto_Login") && !entry.getKey().equals("Auto_Login_ID")) { // 자동로그인 키와 아이디 값 제외

                    Log.e("share : ", entry.getKey() + ": " + entry.getValue());
                    if (frist) {
                        // 첫번째 키를 제외하고
                        frist = false;
                    } else // 그외에는 ,를 붙이기
                    {
                        JSON_string += ",";
                    }
                    JSON_string += entry.getValue();
                }
            }
            JSON_string = "[" + JSON_string + "]"; // 배열로 바꾸기

            try {
                jsonArray = new JSONArray(JSON_string);
                for(int i = 0 ; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (sharePref.getString("Auto_Login_ID","").equals(jsonObject.getString("ID"))) // 아이디 일치시에
                    {
                        change_volume = Float.parseFloat(jsonObject.getString("MUSIC_V")) * 1/100; // 효과음 음량바꾸기
                        break;
                    }
                }
            } catch (JSONException e) { e.printStackTrace(); }

            //---------------------------------------------------------------------------------
        }

       try{
           mp.setVolume(change_volume,change_volume); // 볼륨 설정
           mp.start();
           Thread.sleep(2000);
           Intent mainIntent = new Intent(SplashAT.this, MainActivity.class);
           startActivity(mainIntent);
       } catch (InterruptedException e) {
           e.printStackTrace();
       }

    }

    protected  void onStop(){
        super.onStop();
        finish();
    }
}
