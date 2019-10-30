package com.shaker.test1;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;


// 게임 일시정지 화면 클래스입니다.

public class Game_PauseAT extends AppCompatActivity{

    String tag = "Game_PauseAT"; // 태그

    //Using the Accelometer & Gyroscoper
    private SensorManager mSensorManager = null;

    //Using the Accelometer
    private SensorEventListener mAccLis;
    private Sensor mAccelometerSensor = null;


    boolean check; // 엑티비티가 넘어간걸 구별하기 위해서
    boolean SCREEN; // 스크린 값이 true 이면 기본 false 이면 180도 회전

    MusicCho2AT MusicCho2AT;
    GameAT GameAT;
    MusicLodAT MusicLodAT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.game_pause_at);

        //Using the Gyroscope & Accelometer
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Using the Accelometer
        mAccelometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccLis = new Game_PauseAT.AccelometerListener();

        Log.e(tag,"onCreate");

        Button bon = (Button)findViewById(R.id.Continue); // 계속하기
        Button bon1 = (Button)findViewById(R.id.Retry); // 다시하기
        Button bon2 = (Button)findViewById(R.id.Back); // 뒤로가기

        MusicCho2AT = (MusicCho2AT) com.shaker.test1.MusicCho2AT.MusicCho2AT; // MusicCho2AT의 객체
        GameAT = (GameAT) com.shaker.test1.GameAT.GameAT; // GameAT의 객체
        MusicLodAT = (MusicLodAT) com.shaker.test1.MusicLodAT.MusicLodAT; // MusicLodAT의 객체

        Intent intent = getIntent(); // GameAT에서 받아오기
        SCREEN = intent.getExtras().getBoolean("SCREEN");

        // 화면 초기화
        if(SCREEN){ setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);}
        else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        bon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 계속하기
                GameAT.set_SCREEN(SCREEN);
                finish();
            }
        });

        bon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 다시 하기 게임 죽여

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

        bon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 뒤로 가기 게임 죽여

                GameAT.th = null;
                GameAT.th2 = null;
                GameAT.finish(); // 게임 죽여
                MusicLodAT.finish(); // 게임 로딩 죽여
                finish(); // 나도 죽여

            }
        });
    }
    protected void onStart(){
        super.onStart();
        Log.e(tag,"onStart");
        mSensorManager.registerListener(mAccLis, mAccelometerSensor, SensorManager.SENSOR_DELAY_UI); // 센서키기
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
        mSensorManager.unregisterListener(mAccLis); // 배터리 소모 없애기 위해 센서 끄기
    }

    protected void onDestroy() { // 꺼질 때
        super.onDestroy();
        Log.e(tag,"onDestroy");
    }

    protected void onRestart() { // 다시 들어올 때
        super.onRestart();
        Log.e(tag,"onRestart");

    }

    public void onBackPressed() { // 뒤로가기 키

        super.onBackPressed();
        GameAT.finish(); // 게임 죽여
        MusicLodAT.finish(); // 게임 로딩 죽여
        MusicCho2AT.finish(); // 게임 선택2 죽여
    }

    private class AccelometerListener implements SensorEventListener { // 센서

        // 센서에 변화를 감지하기 위해 계속 호출되고 있는 함수
        @Override
        public void onSensorChanged(SensorEvent event) {

            Log.e(tag,"onSensor");

            double accX = event.values[0];
            double accZ = event.values[2];

            double angleXZ = Math.atan2(accX,  accZ) * 180/Math.PI;

            Log.e("LOG", "ACCELOMETER           [X]:" + String.format("%.4f", event.values[0])
                    + "           [Z]:" + String.format("%.4f", event.values[2])
                    + "           [angleXZ]: " + String.format("%.4f", angleXZ));

            if(angleXZ > 45 ){ // 45도 이상이면 기본 가로 화면
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                SCREEN = true;
            }
            if(angleXZ < -45 ){ // -45도 이하면 180도 회전 가로 화면
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                SCREEN = false;
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
