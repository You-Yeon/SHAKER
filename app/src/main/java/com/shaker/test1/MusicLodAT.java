package com.shaker.test1;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.os.IBinder;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

// 게임 시작 후 로딩 프로그래스 화면 클래스입니다.

public class MusicLodAT extends AppCompatActivity  {

    String tag = "MusicLodAT"; // 태그

    //Using the Accelometer & Gyroscoper
    private SensorManager mSensorManager = null;

    //Using the Accelometer
    private SensorEventListener mAccLis;
    private Sensor mAccelometerSensor = null;

    boolean SCREEN; // 스크린 값이 true 이면 기본 false 이면 180도 회전

    String title;
    String artist;
    String time;
    String id_text;
    int draw_key;
    int star; // 난이도도 받기

    MusicService ms; // 서비스 객체
    boolean isService = false; //서비스 여부
    boolean check; // 엑티비티가 넘어간걸 구별하기 위해서

    boolean th_loop = true; // 스레드 종료 여부
    boolean th_check = true; // 스레드 사용 여부
    ProgressBar pb; // 로딩 바
    int cnt = 0; // 카운트

    Handler Han; // 핸들러
    static Thread th; // 스레드

    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            Log.e(tag,"onService");
            MusicService.MyBinder mb = (MusicService.MyBinder) service;
            ms = mb.getService(); // 서비스가 제공하는 메소드 호출하여
            // 서비스쪽 객체를 전달받을수 있슴
            isService = true;
        }
        public void onServiceDisconnected(ComponentName name) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
            Log.e(tag,"offService");
            isService = false;
        }
    };

    public static Activity MusicLodAT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(tag,"onCreate");
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.music_loading_at);

        //Using the Gyroscope & Accelometer
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Using the Accelometer
        mAccelometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccLis = new AccelometerListener();

        bindService(new Intent(MusicLodAT.this,MusicService.class), conn, Context.BIND_AUTO_CREATE);

        check = false;

        pb = (ProgressBar) findViewById(R.id.progressBar); // 로딩바

        ImageView Music = (ImageView)findViewById(R.id.music_pro); // 노래 사진
        TextView Title = (TextView)findViewById(R.id.Title); // 제목
        TextView Singer = (TextView)findViewById(R.id.Singer); // 가수
        TextView Time = (TextView)findViewById(R.id.Time); // 시간

        MusicLodAT = MusicLodAT.this; // finish를 할 수 있도록

        Intent intent = getIntent(); // MusicCho2AT에서 받아오기

        time = intent.getExtras().getString("Time");
        title = intent.getExtras().getString("Title");
        Title.setText("Title : "+ title);
//        if(time.equals("")){
//            Bitmap myBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/SHAKER" + "/Music/" +title);
//            draw_key = intent.getExtras().getInt("DRAW");
//            Music.setImageBitmap(myBitmap);
//            Time.setText("");
//        }
//        else
//        {
            draw_key = intent.getExtras().getInt("DRAW");
            Music.setImageResource(draw_key);
            Time.setText("Time : "+time);
//        }

        star = intent.getExtras().getInt("STAR"); // 기록을 위해 전달을 받아 두기
        artist = intent.getExtras().getString("Artist");
        Singer.setText("Artist : "+artist);
        id_text = intent.getExtras().getString("ID");

        Han = new Handler(){
            @Override
            public void handleMessage(Message msg) { // 핸들러
                super.handleMessage(msg);
                pb.setProgress(cnt);
            }
        };

        th = new Thread(new Runnable() {
            @Override
            public void run() { // 스레드
                // 초반 몇 초 쉬고
                try {
                    while (th_loop){

                        if(th_check) {

                            if(cnt == 5000){

                                Intent intent = new Intent(getApplicationContext(),GameAT.class);
                                intent.putExtra("DRAW",draw_key); // 절대 키
                                intent.putExtra("STAR",star);
                                intent.putExtra("Title",title);
                                intent.putExtra("SCREEN",SCREEN);
                                intent.putExtra("ID",id_text);
                                intent.putExtra("Time",time); // 시간 주기

                                startActivity(intent); // 다음 화면으로 넘어간다
                                check = true; // 화면넘어간거야 !

                                th_loop =false; // 스레드는 종료하기
                            }
                            Message msg = Han.obtainMessage(); // 핸들러 정보 받아오기
                            cnt +=10;
                            Han.sendMessage(msg);
                            Thread.sleep(10);

                        }
                        else {}
                    }
                } catch (InterruptedException e){}
            }
        });
        th.start();

    }

    protected void onResume(){
        super.onResume();
        Log.e(tag,"onResume");
    }

    protected void onPause(){
        super.onPause();
        th_check = false; // 스레드 쉬어라
        Log.e(tag,"onPause");

    }

    protected void onStart(){
        super.onStart();
        th_check = true;  // 스레드 시작
        Log.e(tag,"onStart");
        mSensorManager.registerListener(mAccLis, mAccelometerSensor, SensorManager.SENSOR_DELAY_UI); // 센서키기
    }

    protected void onStop() { // 나갈 때
        super.onStop();
        Log.e(tag,"onStop");
        mSensorManager.unregisterListener(mAccLis); // 배터리 소모 없애기 위해 센서 끄기
        if(!check) {
            ms.Music_pause();
        }
    }

    protected void onDestroy() { // 꺼질 때
        super.onDestroy();
        Log.e(tag,"onDestroy");
        th_loop = false; // 스레드 종료
        th = null; // 스레드 비우기
        unbindService(conn); // 서비스 종료
    }

    protected void onRestart() { // 다시 들어올 때
        super.onRestart();
        check = false;
        Log.e(tag,"onRestart");
        ms.Music_on();

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

    public void onBackPressed() {
        super.onBackPressed();
        check = true;
    }

}
