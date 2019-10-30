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
import android.widget.ImageView;
import android.widget.TextView;

// 게임 점수 화면 클래스입니다.

public class ScoreAT extends AppCompatActivity {

    String tag = "ScoreAT"; // 태그

    boolean check; // 엑티비티가 넘어간걸 구별하기 위해서
    int SCORE; // 점수
    int RANK; // 랭크
    String TIME; // 시간
    String id_text; // 시간
    int STAR; // 난이도
    int DRAW_KEY; // 키 값
    int MAX_COMBO; // 최대 콤보
    float ACCURACY; // 정확도

    int PF; // 퍼펙트
    int GT; // 그레이트으
    int GD; // 구웃
    int CL; // 쿠울
    int BD; // 배애드
    int MI; // 미수


    boolean SCREEN; // 스크린 값이 true 이면 기본 false 이면 180도 회전

    MusicCho2AT MusicCho2AT;
    GameAT GameAT;
    MusicLodAT MusicLodAT;

    MusicService ms; // 서비스 객체
    boolean isService = false; //서비스 여부

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
        setContentView(R.layout.score_at);

        check = false;
        Log.e(tag,"onCreate");
        bindService(new Intent(ScoreAT.this,MusicService.class), conn, Context.BIND_AUTO_CREATE);

        Button bon = (Button)findViewById(R.id.BACK); // 뒤로가기
        Button bon2 = (Button)findViewById(R.id.RETRY); // 다시시작

        TextView Score = (TextView)findViewById(R.id.Score_2); // 점수
        TextView Perfect = (TextView)findViewById(R.id.Perfect_2); // 퍼펙트
        TextView Great = (TextView)findViewById(R.id.Great_2); // 그레이트으
        TextView Good = (TextView)findViewById(R.id.Good_2); // 구웃
        TextView Cool = (TextView)findViewById(R.id.Cool_2); // 쿠울
        TextView Bad = (TextView)findViewById(R.id.Bad_2); // 배드으
        TextView Miss = (TextView)findViewById(R.id.Miss_2); // 미스으

        TextView Max_combo = (TextView)findViewById(R.id.Combo_2); // 콤보오
        TextView Accuaracy = (TextView)findViewById(R.id.Accuracy_2); // 정확도오
        ImageView Rank = (ImageView)findViewById(R.id.Ranking_2); // 랭크으

        MusicCho2AT = (MusicCho2AT) com.shaker.test1.MusicCho2AT.MusicCho2AT; // MusicCho2AT의 객체
        GameAT = (GameAT) com.shaker.test1.GameAT.GameAT; // GameAT의 객체
        MusicLodAT = (MusicLodAT) com.shaker.test1.MusicLodAT.MusicLodAT; // MusicLodAT의 객체

        Intent intent = getIntent(); // GameAT에서 받아오기

        SCORE = intent.getExtras().getInt("Score");
        RANK =  intent.getExtras().getInt("Rank");
        STAR =  intent.getExtras().getInt("Star");
        TIME = intent.getExtras().getString("Time");
        DRAW_KEY =  intent.getExtras().getInt("Draw_key");
        ACCURACY = intent.getExtras().getFloat("Accuracy");
        MAX_COMBO = intent.getExtras().getInt("Max_combo");
        SCREEN = intent.getExtras().getBoolean("SCREEN");
        id_text = intent.getExtras().getString("ID");

        PF =  intent.getExtras().getInt("Perfect");
        GT =  intent.getExtras().getInt("Great");
        GD =  intent.getExtras().getInt("Good");
        CL =  intent.getExtras().getInt("Cool");
        BD =  intent.getExtras().getInt("Bad");
        MI =  intent.getExtras().getInt("Miss");

        GameAT.th_loop = false;
        GameAT.th = null;
        GameAT.th2 = null;

        // 화면 고정
        if(SCREEN){ setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);}
        else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        Score.setText(String.format("%08d",SCORE)); // 점수 올리기

        Perfect.setText(String.format("%03d",PF) + "x"); // 보여주기
        Great.setText(String.format("%03d",GT) + "x");
        Good.setText(String.format("%03d",GD) + "x");
        Cool.setText(String.format("%03d",CL) + "x");
        Bad.setText(String.format("%03d",BD) + "x");
        Miss.setText(String.format("%03d",MI) + "x");

        Max_combo.setText(String.format("%03d",MAX_COMBO) + "x"); // 최대 콤보
        Accuaracy.setText(String.format("%02.2f",ACCURACY) + " %"); // 정확도
        Rank.setImageResource(RANK); // 랭크 설정

        //기록 넘기기
        MusicCho2AT.get_Record(DRAW_KEY, STAR, TIME, SCORE , RANK);

        bon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 뒤로가기

                check = true; // 화면 넘긴거임 !
                GameAT.finish(); // 게임 죽여
                MusicLodAT.finish(); // 게임 로딩 죽여
                finish(); // 나도 죽여
            }
        });

        bon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 다시시작

                GameAT.finish(); // 게임 죽여
                check = true; // 화면 넘긴거임 !
                finish(); // 나도 죽여

                Intent intent = new Intent(getApplicationContext(),GameAT.class);
                intent.putExtra("DRAW",DRAW_KEY); // 절대 키
                intent.putExtra("STAR",STAR);
                intent.putExtra("SCREEN",SCREEN);
                intent.putExtra("ID",id_text);

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
        Log.e(tag,"onRestart");
        check = false;
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
