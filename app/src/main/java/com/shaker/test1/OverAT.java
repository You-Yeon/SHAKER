package com.shaker.test1;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

// 게임 오버 화면 클래스입니다.

public class OverAT extends AppCompatActivity{

    String tag = "OverAT"; // 태그

    boolean check; // 엑티비티가 넘어간걸 구별하기 위해서
    boolean SCREEN; // 스크린 값이 true 이면 기본 false 이면 180도 회전

    MusicService ms; // 서비스 객체
    boolean isService = false; //서비스 여부

    MusicCho2AT MusicCho2AT;
    GameAT GameAT;
    MusicLodAT MusicLodAT;

    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            // 서비스와 연결되었을 때 호출되는 메서드
            // 서비스 객체를 전역변수로 저장
            MusicService.MyBinder mb = (MusicService.MyBinder) service;
            ms = mb.getService(); // 서비스가 제공하는 메소드 호출하여
            // 서비스쪽 객체를 전달받을수 있슴
            Log.e(tag,"onService");

            isService = true;

            ms.setMusic("Made_of_something",true); // 처음 로그인 노래로 변경
        }
        public void onServiceDisconnected(ComponentName name) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
            Log.e(tag,"offService");
            isService = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.over_at);

        check = false;
        Log.e(tag,"onCreate");
        bindService(new Intent(OverAT.this,MusicService.class), conn, Context.BIND_AUTO_CREATE);

        Button bon = (Button)findViewById(R.id.back_button); // 뒤로가기
        Button bon2 = (Button)findViewById(R.id.retry_button); // 다시시작

        MusicCho2AT = (MusicCho2AT) com.shaker.test1.MusicCho2AT.MusicCho2AT; // MusicCho2AT의 객체
        GameAT = (GameAT) com.shaker.test1.GameAT.GameAT; // GameAT의 객체
        MusicLodAT = (MusicLodAT) com.shaker.test1.MusicLodAT.MusicLodAT; // MusicLodAT의 객체

        Intent intent = getIntent(); // GameAT에서 받아오기
        SCREEN = intent.getExtras().getBoolean("SCREEN");

        // 화면 고정
        if(SCREEN){ setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);}
        else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        bon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 뒤로 가기

                GameAT.th = null;
                GameAT.th2 = null;
                check = true; // 화면 넘긴거임 !
                GameAT.finish(); // 게임 죽여
                MusicLodAT.finish(); // 게임 로딩 죽여
                finish(); // 나도 죽여
            }
        });

        bon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 다시 시작

                GameAT.th = null;
                GameAT.th2 = null;
                GameAT.finish(); // 게임 죽여
                finish(); // 나도 죽여

                Intent intent = new Intent(getApplicationContext(),GameAT.class);
                intent.putExtra("DRAW",GameAT.draw_key); // 절대 키
                intent.putExtra("STAR",GameAT.star);
                intent.putExtra("SCREEN",SCREEN);
                intent.putExtra("ID",GameAT.id_text);

                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });
    }

    protected void onStart(){
        super.onStart();
        Log.e(tag,"onStart");
    }

    protected void onResume(){
        super.onResume();
        Log.e(tag,"onResume");
    }

    protected void onPause(){
        super.onPause();
        Log.e(tag,"onPause");
    }

    protected void onStop() { // 나갈 때
        super.onStop();
        Log.e(tag,"onStop");
        if(!check) {
            ms.Music_pause();
        }
    }

    protected void onDestroy() { // 꺼질 때
        super.onDestroy();
        Log.e(tag,"onDestroy");
        unbindService(conn); // 서비스 종료
    }

    protected void onRestart() { // 다시 들어올 때
        super.onRestart();
        check = false;
        Log.e(tag,"onRestart");
        ms.Music_on();

    }

    public void onBackPressed() { // 뒤로가기 키

        super.onBackPressed();
        check = true; // 화면 넘긴거임 !
        GameAT.finish(); // 게임 죽여
        MusicLodAT.finish(); // 게임 로딩 죽여
        MusicCho2AT.finish(); // 게임 선택2 죽여
    }
}
