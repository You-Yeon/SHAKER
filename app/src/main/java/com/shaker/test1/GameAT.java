package com.shaker.test1;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

// 인 게임 클래스입니다.

public class GameAT extends AppCompatActivity {

    //추가 기능

//    private Visualizer audioOutput = null;
//    public float intensity = 0; //intensity is a value between 0 and 1. The intensity in this case is the system output volume
//    Equalizer mEqualizer;
//    Button getmusic;

    //


    boolean thread_start;
//    boolean setting;

    public SoundPool ef_sp; // 효과음 들려주기
    public SoundPool ov_sp; // 효과음 들려주기
    int sound_beep_alert = 0; // 파일 로드
    int sound_beep_alert2 = 0; // 파일 로드
    int sound_beep_alert3 = 0; // 파일 로드
    String ef_name; // 효과음 이름
    float change_volume; // 효과음 볼륨
    boolean SOUND; // 소리
    boolean SOUND2; // 콤보 스킬 소리

    private JSONArray jsonArray; // JSON 배열
    private String JSON_string; // JSON 배열형식으로 바꾸기

    static SharedPreferences sharePref = null; // 쉐어드 프리페런스
    static SharedPreferences.Editor editor = null; // 저장소 조작기

    //Using the Accelometer & Gyroscoper
    private SensorManager mSensorManager = null;

    //Using the Accelometer
    private SensorEventListener mAccLis;
    private Sensor mAccelometerSensor = null;

    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float accX, accY, accZ;

    private static final int SHAKE_THRESHOLD = 900; // 흔들림 감지 임계값
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    String tag = "GameAT"; // 태그

    boolean check; // 엑티비티가 넘어간걸 구별하기 위해서
    String id_text; // 아이디 값
    TextView Score; // 점수 뷰
    ImageView Back_art; // 배경 아트
    int draw_key;
    int star; //난이도 받아오기
    int rank; // 랭크
    String time;
    String title;

    TextView combo; // 콤보
    int COMBO; // 콤보 세기
    int MAX_COMBO; // 최대 콤보
    ImageView combo_img; // 콤보 이미지

    TextView SCORE; // 점수
    int score; // 점수

    TextView ACCURACY; // 정확도
    float accuracy; // 정확도
    float accuracy_cnt = 0; // 정확도 계산하기 위해서
    float All_accuracy = 0; // 정확도 계산하기 위해서

    ProgressBar HP; // 체력 프로그레스 바
    int a = 100;
    int SPEED; // 노트 속도

    ImageButton button1; // 버튼 1
    ImageButton button2; // 버튼 2
    ImageButton button3; // 버튼 3
    ImageButton button4; // 버튼 4
    ImageButton button5; // 버튼 5

    ImageView button_effect1; // 버튼 1 이펙트
    ImageView button_effect2; // 버튼 2 이펙트
    ImageView button_effect3; // 버튼 3 이펙트
    ImageView button_effect4; // 버튼 4 이펙트
    ImageView button_effect5; // 버튼 5 이펙트

    //  ----------------------------------
    //    시각적으로 보이는 노트 데이터
    //  ----------------------------------

    ArrayList<Integer> BOT1; // 버튼 1 y의 노트 데이터 저장소
    ArrayList<Integer> BOT2; // 버튼 2 y의 노트 데이터 저장소
    ArrayList<Integer> BOT3; // 버튼 3 y의 노트 데이터 저장소
    ArrayList<Integer> BOT4; // 버튼 4 y의 노트 데이터 저장소
    ArrayList<Integer> BOT5; // 버튼 5 y의 노트 데이터 저장소

    //  ----------------------------------
    //    보이지 않는 노트 데이터
    //  ----------------------------------

    ArrayList<Integer> BOT1_note_data; // 버튼 1 time의 노트 데이터 저장소
    ArrayList<Integer> BOT2_note_data; // 버튼 2 time의 노트 데이터 저장소
    ArrayList<Integer> BOT3_note_data; // 버튼 3 time의 노트 데이터 저장소
    ArrayList<Integer> BOT4_note_data; // 버튼 4 time의 노트 데이터 저장소
    ArrayList<Integer> BOT5_note_data; // 버튼 5 time의 노트 데이터 저장소

    //  ---------------------------
    //       노트 데이터 길이
    //  --------------------------

    int bot1_cnt = 1; // 버튼 1 데이터 카운트
    int bot2_cnt = 1; // 버튼 2 데이터 카운트
    int bot3_cnt = 1; // 버튼 3 데이터 카운트
    int bot4_cnt = 1; // 버튼 4 데이터 카운트
    int bot5_cnt = 1; // 버튼 5 데이터 카운트
    String line; // 한줄씩 읽기
    String data[];
    String music_path; // 노래 경로
    String star_path; // 난이도 경로

    boolean bot1_done = false; // 버튼1 끝났니
    boolean bot2_done = false; // 버튼2 끝났니
    boolean bot3_done = false; // 버튼3 끝났니
    boolean bot4_done = false; // 버튼4 끝났니
    boolean bot5_done = false; // 버튼5 끝났니

    boolean SCREEN; // 스크린 값이 true 이면 기본 false 이면 180도 회전
    MusicService ms; // 서비스 객체
    boolean isService = false; //서비스 여부

    Handler Han; // 핸들러
    FrameLayout canvas; // 캔버스
    DrawView mview; // 노트
    Thread th; // 스레드
    Thread th2; // 효과음스레드

    boolean th_check = true; // 나간 여부
    boolean th_loop = true;  // 종료
    Bitmap myBitmap01, myBitmap02, myBitmap03; // 노트 사진
    int P ; // 여분 공간
    int th_cnt =0; // 카운트
    int fn_cnt =0; // 끝내는 카운트
    boolean TH_CNT; // 카운트
    boolean  FN_CNT; // 끝내는 카운트
    boolean miss = false; // 미스

    int PF = 0; // 퍼펙트
    int GT = 0; // 그레이트으
    int GD = 0; // 구웃
    int CL = 0; // 쿠울
    int BD = 0; // 배애드
    int MI = 0; // 미수


    //--------------------------
    //    콤보 게이지 만들기
    //--------------------------

    int combo_value = 0 ; //  프로그레스 값
    ProgressBar combo_progress;
    ImageView shake; // 뒤에 쉐이킹 그림
    ImageView score_combo; // 점수 배수 표시
    int combo_state = 1; // 콤보 스텟
    boolean sensor = false;  // 센서
//    AnimationDrawable animationDrawable;
    boolean sensor_check = false; // 센서 체킹
    boolean cb_check = true;

//    boolean cheat = false; // 치트
//    int cheat_cnt = 0; // 동시 횟수

    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            // 서비스와 연결되었을 때 호출되는 메서드
            // 서비스 객체를 전역변수로 저장
            MusicService.MyBinder mb = (MusicService.MyBinder) service;
            ms = mb.getService(); // 서비스가 제공하는 메소드 호출하여
            // 서비스쪽 객체를 전달받을수 있슴
            ms.Music_pause();
            Log.e(tag,"onService");
            isService = true;
        }
        public void onServiceDisconnected(ComponentName name) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
            Log.e(tag,"offService");
            isService = false;
        }
    };

    public static Activity GameAT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(tag, "onCreate");
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.game_at);

        //가속도 센서
        //Using the Gyroscope & Accelometer
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Using the Accelometer
        mAccelometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccLis = new GameAT.AccelometerListener();

        check = false; //초기화

        MusicLodAT.th = null;

        Score = (TextView) findViewById(R.id.Score); // 점수
        combo = (TextView)findViewById(R.id.combo); // 콤보
        combo_img = (ImageView)findViewById(R.id.combo_img); // 콤보 이미지
        SCORE = (TextView)findViewById(R.id.Score); // 점수
        ACCURACY = (TextView)findViewById(R.id.accuracy); // 정확도
        Back_art = (ImageView)findViewById(R.id.back_art); // 배경 이미지
        score_combo = (ImageView)findViewById(R.id.combo_score); // 점수 배수 보여주기
        shake = (ImageView)findViewById(R.id.shake); // 뒤에 쉐이킹 보여주기

        button1 = (ImageButton) findViewById(R.id.Button1); // 버튼 1
        button2 = (ImageButton) findViewById(R.id.Button2); // 버튼 2
        button3 = (ImageButton) findViewById(R.id.Button3); // 버튼 3
        button4 = (ImageButton) findViewById(R.id.Button4); // 버튼 4
        button5 = (ImageButton) findViewById(R.id.Button5); // 버튼 5

        button_effect1 = (ImageView) findViewById(R.id.Button1_effect); // 버튼 1 이펙트
        button_effect2 = (ImageView) findViewById(R.id.Button2_effect); // 버튼 2 이펙트
        button_effect3 = (ImageView) findViewById(R.id.Button3_effect); // 버튼 3 이펙트
        button_effect4 = (ImageView) findViewById(R.id.Button4_effect); // 버튼 4 이펙트
        button_effect5 = (ImageView) findViewById(R.id.Button5_effect); // 버튼 5 이펙트

        BOT1 = new ArrayList<>(); // 버튼 1 위치의 데이터 공간
        BOT2 = new ArrayList<>(); // 버튼 2 위치의 데이터 공간
        BOT3 = new ArrayList<>(); // 버튼 3 위치의 데이터 공간
        BOT4 = new ArrayList<>(); // 버튼 4 위치의 데이터 공간
        BOT5 = new ArrayList<>(); // 버튼 5 위치의 데이터 공간

        BOT1_note_data = new ArrayList<>(); // 버튼 1 위치의 데이터 공간
        BOT2_note_data = new ArrayList<>(); // 버튼 2 위치의 데이터 공간
        BOT3_note_data = new ArrayList<>(); // 버튼 3 위치의 데이터 공간
        BOT4_note_data = new ArrayList<>(); // 버튼 4 위치의 데이터 공간
        BOT5_note_data = new ArrayList<>(); // 버튼 5 위치의 데이터 공간

        HP = (ProgressBar) findViewById(R.id.hp); // 체력 프로그래스 바
        HP.setProgress(a);

        // --- 콤보 게이지

        combo_progress = (ProgressBar)findViewById(R.id.Combo_progressBar); // 콤보 프로그레스 바
        combo_progress.setProgress(combo_value);

        sharePref = getSharedPreferences("DATA",MODE_PRIVATE);  // "DATA"에서 가져오기
        editor = sharePref.edit(); // "DATA" 조작기

        ef_sp = new SoundPool(20, AudioManager.STREAM_MUSIC,0); // 효과음 soundpool
        ov_sp = new SoundPool(5, AudioManager.STREAM_MUSIC,0); // 게임 오버 효과음

        bindService(new Intent(GameAT.this, MusicService.class), conn, Context.BIND_AUTO_CREATE);

        GameAT = GameAT.this; // finish를 할 수 있도록

        Intent intent = getIntent();
//        time = intent.getExtras().getString("Time");
        title = intent.getExtras().getString("Title");
//        if(time.equals("")){
//            Bitmap myBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/SHAKER" + "/Music/" +title);
//            draw_key = intent.getExtras().getInt("DRAW");
//            Back_art.setImageBitmap(myBitmap);
//        }
//        else
//        {
            draw_key = intent.getExtras().getInt("DRAW");
            Back_art.setImageResource(draw_key);
//        }
        star = intent.getExtras().getInt("STAR");
        SCREEN = intent.getExtras().getBoolean("SCREEN");
        id_text = intent.getExtras().getString("ID");


        // 화면 고정
        if (SCREEN) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        canvas = (FrameLayout) findViewById(R.id.canvas); // 캔버스
        mview= new DrawView(canvas.getContext()); // 뷰 생성
        canvas.addView(mview); // 캔버스 그리기

        // --------------------------------
        //     회원 정보를 JSON으로 추출
        // --------------------------------

        boolean frist = true; // 처음

        JSON_string = ""; // 저장 공간 비우기
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

        // ------------------------
        //     효과음 검색
        // ------------------------

        change_volume = 0.0f; // 단위 바꾸기

        // 해당 아이디가 있는 위치 검색
        try {

            jsonArray = new JSONArray(JSON_string);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(id_text.equals(jsonObject.getString("ID"))){

                    //볼륨 설정하기
                    change_volume = Float.parseFloat(jsonObject.getString("EFFECT_V")) * 1/100;

                    // 디폴트 값 설정하기
                    if(jsonObject.getString("EFFECT_N").equals("kick")){
                        ef_name = "kick"; }
                    if(jsonObject.getString("EFFECT_N").equals("snare")){
                        ef_name = "snare"; }
                    if(jsonObject.getString("EFFECT_N").equals("tom")){
                        ef_name = "tom"; }
                    if(jsonObject.getString("EFFECT_N").equals("bubble")){
                        ef_name = "bubble"; }

                    break;
                }
            }

        } catch (JSONException e) { e.printStackTrace(); }

        //---------------------------------------------------------------------------------

        // ---------------------
        //   효과음 종류 선택
        // ---------------------

        // 파일 로드

        if(ef_name.equals("kick")){ // kick
            sound_beep_alert = ef_sp.load(GameAT.this, R.raw.kick,1);
        }
        else if(ef_name.equals("snare")){ // snare
            sound_beep_alert = ef_sp.load(GameAT.this, R.raw.snare,1);
        }
        else if(ef_name.equals("tom")){ // tom
            sound_beep_alert = ef_sp.load(GameAT.this, R.raw.tom,1);
        }
        else{ // bubble
            sound_beep_alert = ef_sp.load(GameAT.this, R.raw.bubble,1);
        }

        sound_beep_alert2 = ov_sp.load(GameAT.this,R.raw.over,1);
        sound_beep_alert3 =ef_sp.load(GameAT.this, R.raw.combo_skill,1);

        //---------------------------------------------------------------------------------

        // 노트 데이터 불러오기

        Note_Data(draw_key, star);

        //핸들러와 스레드

        Han = new Handler(){
            @Override
            public void handleMessage(Message msg) { // 핸들러
                super.handleMessage(msg);

                if(msg.arg2 == 200 && combo_value == 100){ // 콤보 게이지

                    if(cb_check && combo_state != 4){
                        shake.setImageResource(R.drawable.shaking1); // 이미지 설정
//                        animationDrawable = (AnimationDrawable)shake.getBackground();
//                        animationDrawable.start();
                        sensor = true; // 센서 키기
                        cb_check =false;
                    }

                    if(combo_state == 1 && !cb_check) { // 아무 효과 없을 경우
                        if (sensor_check) { // 흔들림이 감지가 되었을 때

                            SOUND2 = true;
                            shake.setImageResource(0);
                            score_combo.setImageResource(R.drawable.scorex2);
                            combo_state = 2; // 2로 바꾸기
                            combo_progress.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY); // 초록색
                            combo_value = 0; //초기화
                            combo_progress.setProgress(combo_value);
                            sensor_check = false;
                            cb_check = true;

//                            animationDrawable.stop();
//                            shake.setBackgroundResource(0);
                            Log.e(tag,"1");
                        }
                    } else if(combo_state == 2 && !cb_check){ // 2배 효과일 경우
                        if(sensor_check){ // 흔들림이 감지가 되었을 때

                            SOUND2 = true;
                            shake.setImageResource(0);
                            score_combo.setImageResource(R.drawable.scorex3);
                            combo_state = 3; // 3으로 바꾸기
                            combo_progress.getProgressDrawable().setColorFilter(Color.MAGENTA, PorterDuff.Mode.MULTIPLY); // 파란색
                            combo_value = 0; //초기화
                            combo_progress.setProgress(combo_value);
                            sensor_check = false;
                            cb_check = true;

//                            animationDrawable.stop();
//                            shake.setBackgroundResource(0);
                            Log.e(tag,"2");
                        }
                    } else if(combo_state == 3 && !cb_check){ // 3배 효과일 경우
                        if(sensor_check && combo_value == 100){ // 흔들림이 감지가 되었을 때

                            SOUND2 = true;
                            shake.setImageResource(0);
                            score_combo.setImageResource(R.drawable.scorex4);
                            combo_state = 4; // 4로 바꾸기
                            sensor_check = false;

//                            animationDrawable.stop();
//                            shake.setBackgroundResource(0);
                            Log.e(tag,"3");

                        }
                    }

                }

                if(msg.arg1 == 100) // 지우기
                {
                    combo_img.setImageResource(0);
                }
                if(msg.arg1 == 101) // 미스
                {
                    score_combo.setImageResource(0);
                    combo_img.setImageResource(R.drawable.miss); // 미스 그림 :)
                    combo_state = 1; // 1로 바꾸기
                    combo_value = 0; // 콤보 게이지 초기화
                    shake.setImageResource(0);
                    sensor = false; // 센서 키기
                    sensor_check = false;
                    cb_check = true;

                    combo_progress.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY); // 주황색

                    a -=5; // 체력 5씩 깎기
                    COMBO = 0; // 콤보 초기화
                    combo_value = 0; // 콤보 게이지 초기화
                    combo_progress.setProgress(combo_value);
                    combo.setText(null); // 콤보 초기화

                    accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                    All_accuracy = All_accuracy + 0; // 미스니까 0을 더한다. :)
                    accuracy = All_accuracy / accuracy_cnt;
                    ACCURACY.setText(String.format("%02.2f",accuracy) + " %");
                }

                if(msg.arg1 == 102) // 클리어
                {

                    th = null;
                    th2 = null;
                    th_loop = false;

                    long now = System.currentTimeMillis(); // 현재 시간
                    Date date = new Date(now); // 현재 날짜
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss"); // 시간 가져오기
                    String getTime = sdf.format(date); // 최종 시간 값

                    if(accuracy == 100f){ // 랭크 정해주기
                        rank = R.drawable.ss;
                    }
                    else if(accuracy < 100f && accuracy >= 95f){
                        rank = R.drawable.s;
                    }
                    else if(accuracy < 95f && accuracy >= 90f){
                        rank = R.drawable.a;
                    }
                    else if(accuracy < 90f && accuracy >= 80f){
                        rank = R.drawable.b;
                    }
                    else if(accuracy < 80f && accuracy >= 70f){
                        rank = R.drawable.c;
                    }
                    else if(accuracy < 70f){
                        rank = R.drawable.d;
                    }

                    Intent intent = new Intent(getApplicationContext(),ScoreAT.class);
                    intent.putExtra("Draw_key",draw_key); // 키 값
                    intent.putExtra("Star",star); // 난이도
                    intent.putExtra("ID",id_text); // 아이디
                    intent.putExtra("Time",getTime); // 시간
                    intent.putExtra("Score",score); // 점수 주기
                    intent.putExtra("Rank",rank); // 점수 주기
                    intent.putExtra("Accuracy",accuracy); // 정확도 주기
                    intent.putExtra("Max_combo",MAX_COMBO); // 최대 콤보

                    intent.putExtra("SCREEN",SCREEN); // 화면 정보 주기

                    intent.putExtra("Perfect",PF); // 퍼펙트
                    intent.putExtra("Great",GT); // 그레이트으
                    intent.putExtra("Good",GD); // 굿드으
                    intent.putExtra("Cool",CL); // 쿠울
                    intent.putExtra("Bad",BD); // 배드
                    intent.putExtra("Miss",MI); // 미수우

                    startActivity(intent); // 다음 화면으로 넘어간다
                    check = true; // 화면넘어간거야 !

                }

//                if(msg.arg1 == 103 ){ // 치트키
//
//                    PF++;
//                    TH_CNT = true;
//                    th_cnt = 0; // 초기화
//                    combo_img.setImageResource(R.drawable.perfect); // 퍼펙트 이미지 나오게
//
//                    COMBO++; // 콤보 올리기
//                    if(combo_value < 100) { combo_value+=5; shake.setImageResource(0);} // 콤보 게이지 올리기
//                    combo_progress.setProgress(combo_value);
//
//                    if(COMBO >= MAX_COMBO){
//                        MAX_COMBO = COMBO;
//                    }
//
//                    combo.setText(String.valueOf(COMBO));
//
//                    score += 400; // 점수 더하기
//                    Score.setText(String.format("%08d",score)); // 점수 올려버리기
//
//                    accuracy_cnt++ ; // 정확도 계산을 위해서 세기
//                    All_accuracy += 100; // perfect니까 100 더하기
//                    accuracy = All_accuracy / accuracy_cnt;
//                    if (accuracy >= 100){ // 100 일때는 그냥 100
//                        ACCURACY.setText("100.00 %");
//                    }
//                    else {
//                        ACCURACY.setText(String.format("%02.2f",accuracy) + " %");
//                    }
//
//                    if(a < 100) {a++;} // 체력 증가
//
//                    if(cheat_cnt > 1){
//                        PF++;
//                        TH_CNT = true;
//                        th_cnt = 0; // 초기화
//                        combo_img.setImageResource(R.drawable.perfect); // 퍼펙트 이미지 나오게
//
//                        COMBO++; // 콤보 올리기
//                        if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
//                        combo_progress.setProgress(combo_value);
//                        if(COMBO >= MAX_COMBO){
//                            MAX_COMBO = COMBO;
//                        }
//
//                        combo.setText(String.valueOf(COMBO));
//
//                        score += 400*combo_state; // 점수 더하기
//                        Score.setText(String.format("%08d",score)); // 점수 올려버리기
//
//                        accuracy_cnt++ ; // 정확도 계산을 위해서 세기
//                        All_accuracy += 100; // perfect니까 100 더하기
//                        accuracy = All_accuracy / accuracy_cnt;
//                        if (accuracy >= 100){ // 100 일때는 그냥 100
//                            ACCURACY.setText("100.00 %");
//                        }
//                        else {
//                            ACCURACY.setText(String.format("%02.2f",accuracy) + " %");
//                        }
//
//                        if(a < 100) {a++;} // 체력 증가
//                    }
//
//                    cheat_cnt = 0;
//                }

                // 그림 갱신
                canvas.removeView(mview);
                canvas.addView(mview);

            }
        };

        th = new Thread(new Runnable() {
            @Override
            public void run() { // 스레드

                // 초반 몇 초 쉬고
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e){}

                // 노래 설정

                if(draw_key == (R.drawable.giving_in)){
                    ms.setMusic("Giving_in",false);
                }
                if(draw_key == (R.drawable.joystick)){
                    ms.setMusic("Joystick",false);
                }
                if(draw_key == (R.drawable.kuyenda)){
                    ms.setMusic("Kuyenda",false);
                }
                if(draw_key == (R.drawable.light_up_the_sky)){
                    ms.setMusic("Light_Up_The_Sky",false);
                }
                if(draw_key == (R.drawable.hold_on)){
                    ms.setMusic("Hold_on",false);
                }
                if(draw_key == (R.drawable.muffin)){
                    ms.setMusic("Muffin",false);
                }
                if(draw_key == (R.drawable.sicc)){
                    ms.setMusic("SICC",false);
                }
                if(draw_key == (R.drawable.hope)){
                    ms.setMusic("Hope",false);
                }
                if(draw_key == (R.drawable.candyland)){
                    ms.setMusic("Candyland",false);
                }
                if(draw_key == (R.drawable.sunburst)){
                    ms.setMusic("Sunburst",false);
                }
                if(draw_key == (R.drawable.delicious)){
                    ms.setMusic("Delicious",false);
                }
                if(draw_key == (R.drawable.together)){
                    ms.setMusic("Toghther",false);
                }
                if(draw_key == (R.drawable.whole)){
                    ms.setMusic("Whole",false);
                }
//                if(time.equals("")){
//
//                    ms.mp.stop();
//                    ms.mp.release();
//                    ms.mp = null;
//                    ms.mp = new MediaPlayer();
//
//                    Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ""+ draw_key);
//
//                    try {
//                        ms.mp.setDataSource(GameAT.this, musicURI);
//                        ms.mp.prepare();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    ms.mp.setVolume(MusicChoAT.ms.change_volume,MusicChoAT.ms.change_volume); // 왼쪽과 오른쪽 넣기
//                    ms.mp.setLooping(false);
//                    ms.mp.start();

//                }

                thread_start = true;

                while (th_loop) {
                    if(th_check)
                    {
                        Message msg = Han.obtainMessage(); // 핸들러 정보 받아오기

                        MainGame(); // 게임 생성

                        if(miss)// 미스일때
                        {
                            MI++;
                            TH_CNT = true; // 카운트 시작
                            msg.arg1 = 101;
                            miss = false; // 미스 출력
                        }

                        if(TH_CNT) // 콤보 이미지 카운터로 세기
                        {
                            if(th_cnt == 50)
                            {
                                th_cnt = 0; // 초기화
                                TH_CNT = false; // 초기화
                                msg.arg1 = 100; // 이미지를 지워줄래?
                            }
                            th_cnt++;
                        }

                        if(FN_CNT) // 끝났네
                        {
                            fn_cnt ++;
                            if(fn_cnt == 200){ // 끝내기
                                msg.arg1 = 102;
                            }
                        }

//                        if(cheat){ // 치트키
//                            msg.arg1 = 103;
//                            cheat = false;
//                        }

                        if(combo_value == 100 && combo_state != 4){// 콤보 게이지
                            msg.arg2 = 200;
                        }

                        if(a <= 0){ // 게임 오버
                            ov_sp.play(sound_beep_alert2, ms.change_volume, ms.change_volume, 0, 0, 1f); // 효과음

                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            Intent intent = new Intent(getApplicationContext(),OverAT.class);
                            intent.putExtra("SCREEN",SCREEN); // 화면 정보 주기

                            startActivity(intent); // 다음 화면으로 넘어간다
                            check = true; // 화면넘어간거야 !

                            th = null;
                            th2 = null;
                            th_loop = false;
                        }

                        // 체력 바 갱신
                        HP.setProgress(a);

                        Han.sendMessage(msg);

                        // 딜레이

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else{}
                }
            }
        });
        th.start();

        th2 = new Thread(new Runnable() {
            @Override
            public void run() { // 스레드

                while (th_loop){
                    if(SOUND) {
                        ef_sp.play(sound_beep_alert, change_volume, change_volume, 0, 0, 1f); // 효과음
                        SOUND = false;
                    }
                    if(SOUND2) {
                        ef_sp.play(sound_beep_alert3, change_volume, change_volume, 0, 0, 1f); // 효과음
                        SOUND2 = false;
                    }
                    get_Data(); // 노트 데이터 긁어오기
                }
            }
        });
        th2.start();

        // 버튼들

        button1.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        button1.setImageResource(R.drawable.off_button2); // 버튼 이미지
                        button_effect1.setImageResource(R.drawable.flash_effect); // 클릭 효과
                        SOUND = true;

                        if(BOT1.size() > 0)
                        {
                            // Perfect

                            if (BOT1.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/4 &&
                                    BOT1.get(0) + myBitmap01.getHeight() <= canvas.getHeight() + myBitmap01.getHeight()/4){

                                BOT1.remove(0); // 지우기

                                PF++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.perfect); // 퍼펙트 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 400*combo_state; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 100; // perfect니까 100 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                if (accuracy >= 100){ // 100 일때는 그냥 100
                                    ACCURACY.setText("100.00 %");
                                }
                                else {
                                    ACCURACY.setText(String.format("%02.2f",accuracy) + " %");
                                }

                                if(a < 100) {a++;} // 체력 증가
                            }
                            // Great

                            else if (BOT1.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/3 &&
                                    BOT1.get(0) + myBitmap01.getHeight() <= canvas.getHeight() + myBitmap01.getHeight()/3)
                            {
                                BOT1.remove(0); // 지우기

                                GT++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.great); // 그레이트 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 300*combo_state; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 90; // great니까 90 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }
                            // Good

                            else if(BOT1.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/2 &&
                                    BOT1.get(0) + myBitmap01.getHeight() <= canvas.getHeight() + myBitmap01.getHeight()/2)
                            {
                                BOT1.remove(0); // 지우기

                                GD++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.good); // 굿 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 200*combo_state; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 80; // good니까 80 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }
                            // Cool

                            else if(BOT1.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight() &&
                                    BOT1.get(0) + myBitmap01.getHeight() < canvas.getHeight() + myBitmap01.getHeight())
                            {
                                BOT1.remove(0); // 지우기

                                CL++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.cool); // 쿨 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 100*combo_state; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 70; // good니까 70 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }
                            // Bad

                            else if(BOT1.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/2 &&
                                    BOT1.get(0) + myBitmap01.getHeight() < canvas.getHeight() + myBitmap01.getHeight())
                            {

                                BOT1.remove(0); // 지우기

                                BD++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.bad); // 배드 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 50*combo_state; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 50; // bad니까 50 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }
//                            miss = true;
                        }
                        break;

                    case MotionEvent.ACTION_UP:

                        button1.setImageResource(R.drawable.button2);
                        button_effect1.setImageResource(0);
                        break;
                }
                return true;
            }
        });

        button2.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        button2.setImageResource(R.drawable.off_button1); // 버튼 이미지
                        button_effect2.setImageResource(R.drawable.flash_effect); // 클릭 효과
                        SOUND = true;

                        if(BOT2.size() > 0)
                        {
                            // Perfect

                            if (BOT2.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/4 &&
                                    BOT2.get(0) + myBitmap01.getHeight() <= canvas.getHeight() + myBitmap01.getHeight()/4){

                                BOT2.remove(0); // 지우기

                                PF++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.perfect); // 퍼펙트 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 400*combo_state; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 100; // perfect니까 100 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                if (accuracy >= 100){ // 100 일때는 그냥 100
                                    ACCURACY.setText("100.00 %");
                                }
                                else {
                                    ACCURACY.setText(String.format("%02.2f",accuracy) + " %");
                                }

                                if(a < 100) {a++;} // 체력 증가
                            }
                            // Great

                            else if (BOT2.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/3 &&
                                    BOT2.get(0) + myBitmap01.getHeight() <= canvas.getHeight() + myBitmap01.getHeight()/3)
                            {
                                BOT2.remove(0); // 지우기

                                GT++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.great); // 그레이트 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 300*combo_state; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 90; // great니까 90 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }
                            // Good

                            else if(BOT2.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/2 &&
                                    BOT2.get(0) + myBitmap01.getHeight() <= canvas.getHeight() + myBitmap01.getHeight()/2)
                            {
                                BOT2.remove(0); // 지우기

                                GD++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.good); // 굿 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 200*combo_state; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 80; // good니까 80 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }
                            // Cool

                            else if(BOT2.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight() &&
                                    BOT2.get(0) + myBitmap01.getHeight() < canvas.getHeight() + myBitmap01.getHeight())
                            {
                                BOT2.remove(0); // 지우기

                                CL++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.cool); // 쿨 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 100*combo_state; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 70; // good니까 70 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }
                            // Bad

                            else if(BOT2.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/2 &&
                                    BOT2.get(0) + myBitmap01.getHeight() < canvas.getHeight() + myBitmap01.getHeight())
                            {

                                BOT2.remove(0); // 지우기

                                BD++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.bad); // 배드 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 50*combo_state; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 50; // bad니까 50 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:

                        button2.setImageResource(R.drawable.button1);
                        button_effect2.setImageResource(0);
                        break;
                }
                return true;
            }
        });

        button3.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        button3.setImageResource(R.drawable.off_button3); // 버튼 이미지
                        button_effect3.setImageResource(R.drawable.flash_effect); // 클릭 효과
                        SOUND = true;

                        if(BOT3.size() > 0)
                        {
                            // Perfect

                            if (BOT3.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/4 &&
                                    BOT3.get(0) + myBitmap01.getHeight() <= canvas.getHeight() + myBitmap01.getHeight()/4){

                                BOT3.remove(0); // 지우기

                                PF++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.perfect); // 퍼펙트 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 400*combo_state; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 100; // perfect니까 100 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                if (accuracy >= 100){ // 100 일때는 그냥 100
                                    ACCURACY.setText("100.00 %");
                                }
                                else {
                                    ACCURACY.setText(String.format("%02.2f",accuracy) + " %");
                                }

                                if(a < 100) {a++;} // 체력 증가
                            }
                            // Great

                            else if (BOT3.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/3 &&
                                    BOT3.get(0) + myBitmap01.getHeight() <= canvas.getHeight() + myBitmap01.getHeight()/3)
                            {
                                BOT3.remove(0); // 지우기

                                GT++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.great); // 그레이트 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 300*combo_state; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 90; // great니까 90 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }
                            // Good

                            else if(BOT3.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/2 &&
                                    BOT3.get(0) + myBitmap01.getHeight() <= canvas.getHeight() + myBitmap01.getHeight()/2)
                            {
                                BOT3.remove(0); // 지우기

                                GD++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.good); // 굿 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 200*combo_state; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 80; // good니까 80 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }
                            // Cool

                            else if(BOT3.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight() &&
                                    BOT3.get(0) + myBitmap01.getHeight() < canvas.getHeight() + myBitmap01.getHeight())
                            {
                                BOT3.remove(0); // 지우기

                                CL++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.cool); // 쿨 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 100*combo_state; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 70; // good니까 70 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }
                            // Bad

                            else if(BOT3.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/2 &&
                                    BOT3.get(0) + myBitmap01.getHeight() < canvas.getHeight() + myBitmap01.getHeight())
                            {
                                BOT3.remove(0); // 지우기

                                BD++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.bad); // 배드 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 50*combo_state; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 50; // bad니까 50 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:

                        button3.setImageResource(R.drawable.button3);
                        button_effect3.setImageResource(0);
                        break;

                }
                return true;
            }
        });

        button4.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        button4.setImageResource(R.drawable.off_button1); // 버튼 이미지
                        button_effect4.setImageResource(R.drawable.flash_effect); // 클릭 효과
                        SOUND = true;

                        if(BOT4.size() > 0)
                        {
                            // Perfect

                            if (BOT4.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/4 &&
                                    BOT4.get(0) + myBitmap01.getHeight() <= canvas.getHeight() + myBitmap01.getHeight()/4){

                                BOT4.remove(0); // 지우기

                                PF++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.perfect); // 퍼펙트 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 400; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 100; // perfect니까 100 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                if (accuracy >= 100){ // 100 일때는 그냥 100
                                    ACCURACY.setText("100.00 %");
                                }
                                else {
                                    ACCURACY.setText(String.format("%02.2f",accuracy) + " %");
                                }

                                if(a < 100) {a++;} // 체력 증가
                            }
                            // Great

                            else if (BOT4.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/3 &&
                                    BOT4.get(0) + myBitmap01.getHeight() <= canvas.getHeight() + myBitmap01.getHeight()/3)
                            {
                                BOT4.remove(0); // 지우기

                                GT++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.great); // 그레이트 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 300; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 90; // great니까 90 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }
                            // Good

                            else if(BOT4.get(0)>= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/2 &&
                                    BOT4.get(0)+ myBitmap01.getHeight() <= canvas.getHeight() + myBitmap01.getHeight()/2)
                            {
                                BOT4.remove(0); // 지우기

                                GD++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.good); // 굿 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 100; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 80; // good니까 80 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }
                            // Cool

                            else if(BOT4.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight() &&
                                    BOT4.get(0) + myBitmap01.getHeight() < canvas.getHeight() + myBitmap01.getHeight())
                            {
                                BOT4.remove(0); // 지우기

                                CL++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.cool); // 쿨 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 100; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 70; // good니까 70 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }
                            // Bad

                            else if(BOT4.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/2 &&
                                    BOT4.get(0) + myBitmap01.getHeight() < canvas.getHeight() + myBitmap01.getHeight())
                            {
                                BOT4.remove(0); // 지우기

                                BD++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.bad); // 배드 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 50; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 50; // bad니까 50 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:

                        button4.setImageResource(R.drawable.button1);
                        button_effect4.setImageResource(0);
                        break;
                }
                return true;
            }
        });

        button5.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        button5.setImageResource(R.drawable.off_button2); // 버튼 이미지
                        button_effect5.setImageResource(R.drawable.flash_effect); // 클릭 효과
                        SOUND = true;

                        if(BOT5.size() > 0)
                        {
                            // Perfect

                            if (BOT5.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/4 &&
                                    BOT5.get(0) + myBitmap01.getHeight() <= canvas.getHeight() + myBitmap01.getHeight()/4){

                                BOT5.remove(0); // 지우기

                                PF++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.perfect); // 퍼펙트 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 400; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 100; // perfect니까 100 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                if (accuracy >= 100){ // 100 일때는 그냥 100
                                    ACCURACY.setText("100.00 %");
                                }
                                else {
                                    ACCURACY.setText(String.format("%02.2f",accuracy) + " %");
                                }

                                if(a < 100) {a++;} // 체력 증가
                            }
                            // Great

                            else if (BOT5.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/3 &&
                                    BOT5.get(0) + myBitmap01.getHeight() <= canvas.getHeight() + myBitmap01.getHeight()/3)
                            {
                                BOT5.remove(0); // 지우기

                                GT++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.great); // 그레이트 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 300; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 90; // great니까 90 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }
                            // Good

                            else if(BOT5.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/2 &&
                                    BOT5.get(0) + myBitmap01.getHeight() <= canvas.getHeight() + myBitmap01.getHeight()/2)
                            {

                                BOT5.remove(0); // 지우기

                                GD++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.good); // 굿 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 100; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 80; // good니까 80 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }

                            // Cool

                            else if(BOT5.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight() &&
                                    BOT5.get(0) + myBitmap01.getHeight() < canvas.getHeight() + myBitmap01.getHeight())
                            {

                                BOT5.remove(0); // 지우기

                                CL++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.cool); // 쿨 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 100; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 70; // good니까 70 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }
                            // Bad

                            else if(BOT5.get(0) >= canvas.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight() - myBitmap01.getHeight()/2 &&
                                    BOT5.get(0) + myBitmap01.getHeight() < canvas.getHeight() + myBitmap01.getHeight())
                            {

                                BOT5.remove(0); // 지우기

                                BD++;
                                TH_CNT = true;
                                th_cnt = 0; // 초기화
                                combo_img.setImageResource(R.drawable.bad); // 배드 이미지 나오게

                                COMBO++; // 콤보 올리기
                                if(combo_value < 100) { combo_value+=5; } // 콤보 게이지 올리기
                                combo_progress.setProgress(combo_value);
                                if(COMBO >= MAX_COMBO){
                                    MAX_COMBO = COMBO;
                                }

                                combo.setText(String.valueOf(COMBO));

                                score += 50; // 점수 더하기
                                Score.setText(String.format("%08d",score)); // 점수 올려버리기

                                accuracy_cnt++ ; // 정확도 계산을 위해서 세기
                                All_accuracy += 50; // bad니까 50 더하기
                                accuracy = All_accuracy / accuracy_cnt;
                                ACCURACY.setText(String.format("%02.2f",accuracy) + " %");

                                if(a < 100) {a++;} // 체력 증가
                            }
                        }

                        break;

                    case MotionEvent.ACTION_UP:

                        button5.setImageResource(R.drawable.button2);
                        button_effect5.setImageResource(0);
                        break;

                }

                return true;
            }
        });

    }

    public void MainGame(){ // 메인 노트 생성하는 메소드

        // ------------------------
        //      노트 y 값 갱신
        // ------------------------

        for ( int i=0; i < BOT1.size(); i++ ) // 하얀색
        {
            BOT1.set(i,BOT1.get(i) + SPEED);
        }

        for ( int i=0; i < BOT2.size(); i++ ) // 노란색
        {
            BOT2.set(i,BOT2.get(i) + SPEED);
        }

        for ( int i=0; i < BOT3.size(); i++ ) // 주황색
        {
            BOT3.set(i,BOT3.get(i) + SPEED);
        }

        for ( int i=0; i < BOT4.size(); i++ ) // 노란색
        {
            BOT4.set(i,BOT4.get(i) + SPEED);
        }

        for ( int i=0; i < BOT5.size(); i++ ) // 하얀색
        {
            BOT5.set(i,BOT5.get(i) + SPEED);
        }

        // ------------------------
        //  아래로 떨어지는 거 지우기
        // ------------------------

        if(BOT1.size() > 0){
            if(BOT1.get(0) > canvas.getHeight()){
                BOT1.remove(0);
                miss = true;
                th_cnt = 0; // 초기화
            }
        }

        if(BOT2.size() > 0) {
            if(BOT2.get(0) > canvas.getHeight()){
                BOT2.remove(0);
                miss = true;
                th_cnt = 0; // 초기화
            }
        }

        if(BOT3.size() > 0){
            if(BOT3.get(0) > canvas.getHeight()){
                BOT3.remove(0);
                miss = true;
                th_cnt = 0; // 초기화

            }
        }

        if(BOT4.size() > 0){
            if(BOT4.get(0) > canvas.getHeight()){
                BOT4.remove(0);
                miss = true;
                th_cnt = 0; // 초기화
            }
        }

        if(BOT5.size() > 0){
            if(BOT5.get(0) > canvas.getHeight()){
                BOT5.remove(0);
                miss = true;
                th_cnt = 0; // 초기화
            }
        }


        // ------------------------
        //          치트
        // ------------------------

//        if(BOT1.size() > 0){
//            if(BOT1.get(0) >= canvas.getHeight() - myBitmap01.getHeight()){
//                BOT1.remove(0); // 지우기
//                cheat = true;
//                cheat_cnt++;
//            }
//        }
//
//        if(BOT2.size() > 0) {
//            if(BOT2.get(0) >= canvas.getHeight() - myBitmap01.getHeight()){
//                BOT2.remove(0); // 지우기
//                cheat = true;
//                cheat_cnt++;
//            }
//        }
//
//        if(BOT3.size() > 0){
//            if(BOT3.get(0) >= canvas.getHeight() - myBitmap01.getHeight()){
//                BOT3.remove(0); // 지우기
//                cheat = true;
//                cheat_cnt++;
//            }
//        }
//
//        if(BOT4.size() > 0){
//            if(BOT4.get(0) >= canvas.getHeight() - myBitmap01.getHeight()){
//                BOT4.remove(0); // 지우기
//                cheat = true;
//                cheat_cnt++;
//            }
//        }
//
//        if(BOT5.size() > 0){
//            if(BOT5.get(0) >= canvas.getHeight() - myBitmap01.getHeight()){
//                BOT5.remove(0); // 지우기
//                cheat = true;
//                cheat_cnt++;
//            }
//        }

        // ------------------------
        //        노트 생성
        // ------------------------

        if(BOT1_note_data.size() > 0){
            if(BOT1_note_data.get(0) <= ms.Music_Current_Time()){
                BOT1.add(-myBitmap01.getHeight()); // 시각적으로 보이는 부분으로 보내고
                BOT1_note_data.remove(0); // 보이지 않는 공간은 지운다.
            }
        }

        if(BOT2_note_data.size() > 0){
            if(BOT2_note_data.get(0) <= ms.Music_Current_Time()){
                BOT2.add(-myBitmap01.getHeight()); // 시각적으로 보이는 부분으로 보내고
                BOT2_note_data.remove(0); // 보이지 않는 공간은 지운다.
            }
        }

        if(BOT3_note_data.size() > 0){
            if(BOT3_note_data.get(0) <= ms.Music_Current_Time()){
                BOT3.add(-myBitmap01.getHeight()); // 시각적으로 보이는 부분으로 보내고
                BOT3_note_data.remove(0); // 보이지 않는 공간은 지운다.
            }
        }

        if(BOT4_note_data.size() > 0){
            if(BOT4_note_data.get(0) <= ms.Music_Current_Time()){
                BOT4.add(-myBitmap01.getHeight()); // 시각적으로 보이는 부분으로 보내고
                BOT4_note_data.remove(0); // 보이지 않는 공간은 지운다.
            }
        }

        if(BOT5_note_data.size() > 0){
            if(BOT5_note_data.get(0) <= ms.Music_Current_Time()){
                BOT5.add(-myBitmap01.getHeight()); // 시각적으로 보이는 부분으로 보내고
                BOT5_note_data.remove(0); // 보이지 않는 공간은 지운다.
            }
        }

        // ------------------------
        //        게임 종료
        // ------------------------

//        if( BOT1_note_data.size() == 0 && BOT2_note_data.size() == 0 && BOT3_note_data.size() == 0 && BOT4_note_data.size() == 0 && BOT5_note_data.size() == 0 &&
//        BOT1.size() == 0 && BOT2.size() == 0 && BOT3.size() == 0 && BOT4.size() == 0 && BOT5.size() == 0){
//            // 다 비워져있을 때
//
//            FN_CNT = true; // 게임 끝내러 가자
//        }

        // 원래 이게 맞는건데 ㅋ
        if(ms.mp.getCurrentPosition()/1000 == ms.Music_Total_Time()){
            FN_CNT = true; // 게임 끝내러 가자
        }

    }

    public void Note_Data(int draw_key, int star){ // 노트 데이터 전달

        music_path = ""; // 노래 경로
        star_path = ""; // 난이도 경로

        // 노래 경로 설정

        switch (draw_key)
        {
            case R.drawable.giving_in: // Giving_in

                SPEED = 15;

                music_path = "/Giving_in";
                break;

            case R.drawable.joystick: // Joystick

                SPEED = 20;

                music_path = "/Joystick";
                break;

            case R.drawable.kuyenda: // Kuyenda

                SPEED = 18;

                music_path = "/Kuyenda";
                break;

            case R.drawable.light_up_the_sky: //Light_Up_The_Sky

                SPEED = 20;

                music_path = "/Light_up_the_sky";
                break;

            case R.drawable.hold_on: // Hold_On

                SPEED = 8;

                music_path = "/Hold_on";
                break;

            case R.drawable.muffin: // Muffin

                SPEED = 15;

                music_path = "/Muffin";
                break;

            case R.drawable.sicc: // SICC

                SPEED = 28;

                music_path = "/SICC";
                break;

            case R.drawable.hope: // Hope

                SPEED = 20;

                music_path = "/Hope";
                break;

            case R.drawable.candyland: // Candyland

                SPEED = 20;

                music_path = "/Candyland";
                break;

            case R.drawable.sunburst: // Sunburst

                SPEED = 20;

                music_path = "/Sunburst";
                break;

            case R.drawable.delicious: // Delicious

                SPEED = 10;

                music_path = "/Delicious";
                break;

            case R.drawable.together: // Together

                SPEED = 18;

                music_path = "/Together";
                break;

            case R.drawable.whole: // Whole

                SPEED = 10;

                music_path = "/Whole";
                break;

            default:
                SPEED = 15;
        }


//        if(!time.equals("")){
//            // 난이도 경로 설정

            if(star == R.drawable.star1){
                star_path = "/star1.txt";
            }
            if(star == R.drawable.star2){
                star_path = "/star2.txt";
            }
            if(star == R.drawable.star3){
                star_path = "/star3.txt";
            }

            int total = 0; // 총 개수
            try {
                BufferedReader buf = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SHAKER" + "/Note_data" + music_path + star_path));

                // 첫번째 줄
                line = buf.readLine();
                data = line.split(","); // ,으로 잘라오기
                total += data.length;
                for (int i =0; i < 5; i++){
                    BOT1_note_data.add(Integer.parseInt(data[i]));
                }

                // 두번째 줄
                line = buf.readLine();
                data = line.split(","); // ,으로 잘라오기
                total += data.length;
                for (int i =0; i < 5; i++){
                    BOT2_note_data.add(Integer.parseInt(data[i]));
                }

                // 세번째 줄
                line = buf.readLine();
                data = line.split(","); // ,으로 잘라오기
                total += data.length;
                for (int i =0; i < 5; i++){
                    BOT3_note_data.add(Integer.parseInt(data[i]));
                }

                // 네번째 줄
                line = buf.readLine();
                data = line.split(","); // ,으로 잘라오기
                total += data.length;
                for (int i =0; i < 5; i++){
                    BOT4_note_data.add(Integer.parseInt(data[i]));
                }

                // 다섯번째 줄
                line = buf.readLine();
                data = line.split(","); // ,으로 잘라오기
                total += data.length;
                for (int i =0; i < 5; i++){
                    BOT5_note_data.add(Integer.parseInt(data[i]));
                }

                buf.close();

            }catch (Exception e){ }

            bot1_cnt = 1;
            bot2_cnt = 1;
            bot3_cnt = 1;
            bot4_cnt = 1;
            bot5_cnt = 1;

            Log.e("노트 총 개수",String.valueOf(total));

            Log.e("bot1",BOT1_note_data.toString());
            Log.e("bot2",BOT2_note_data.toString());
            Log.e("bot3",BOT3_note_data.toString());
            Log.e("bot4",BOT4_note_data.toString());
            Log.e("bot5",BOT5_note_data.toString());
//        }
    }

    public void get_Data(){

        // ------------------------
        //      노트 불러오기
        // ------------------------

//        if(!time.equals("")){
            if(BOT1_note_data.size() < 5){ // 5보다 적을 겨우에 더하기
                if(!bot1_done){
                    try {
                        BufferedReader buf = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SHAKER" + "/Note_data" + music_path + star_path));
                        line = buf.readLine();
                        data = line.split(","); // ,으로 잘라오기

                        if(bot1_cnt == data.length/5){ // 마지막 까지 왔다면.. 나머지 전부 더하기
                            for (int i = bot1_cnt*5; i < data.length; i++) {
                                BOT1_note_data.add(Integer.parseInt(data[i]));
                            }
                            bot1_done = true;
                        }
                        if(bot1_cnt < data.length/5){ // 그외의 경우
                            for (int i = bot1_cnt*5; i < (bot1_cnt+1)*5; i++) {
                                BOT1_note_data.add(Integer.parseInt(data[i]));
                            }
                            bot1_cnt++;
                        }
                    }catch (Exception c){}
                }
            }

            if(BOT2_note_data.size() < 5){ // 5보다 적을 겨우에 더하기
                if(!bot2_done){
                    try {
                        BufferedReader buf = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SHAKER" + "/Note_data" + music_path + star_path));
                        line = buf.readLine();
                        line = buf.readLine();
                        data = line.split(","); // ,으로 잘라오기

                        if(bot2_cnt == data.length/5){ // 마지막 까지 왔다면.. 나머지 전부 더하기
                            for (int i = bot2_cnt*5; i < data.length; i++) {
                                BOT2_note_data.add(Integer.parseInt(data[i]));
                            }
                            bot2_done = true;
                        }
                        if(bot2_cnt < data.length/5){ // 그외의 경우
                            for (int i = bot2_cnt*5; i < (bot2_cnt+1)*5; i++) {
                                BOT2_note_data.add(Integer.parseInt(data[i]));
                            }
                            bot2_cnt++;
                        }
                    }catch (Exception c){}
                }
            }

            if(BOT3_note_data.size() < 5){ // 5보다 적을 겨우에 더하기
                if(!bot3_done){
                    try {
                        BufferedReader buf = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SHAKER" + "/Note_data" + music_path + star_path));
                        line = buf.readLine();
                        line = buf.readLine();
                        line = buf.readLine();
                        data = line.split(","); // ,으로 잘라오기

                        if(bot3_cnt == data.length/5){ // 마지막 까지 왔다면.. 나머지 전부 더하기
                            for (int i = bot3_cnt*5; i < data.length; i++) {
                                BOT3_note_data.add(Integer.parseInt(data[i]));
                            }
                            bot3_done = true;
                        }
                        if(bot3_cnt < data.length/5){ // 그외의 경우
                            for (int i = i = bot3_cnt*5; i < (bot3_cnt+1)*5; i++) {
                                BOT3_note_data.add(Integer.parseInt(data[i]));
                            }
                            bot3_cnt++;
                        }
                    }catch (Exception c){}
                }
            }

            if(BOT4_note_data.size() < 5){ // 5보다 적을 겨우에 더하기
                if(!bot4_done){
                    try {
                        BufferedReader buf = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SHAKER" + "/Note_data" + music_path + star_path));
                        line = buf.readLine();
                        line = buf.readLine();
                        line = buf.readLine();
                        line = buf.readLine();
                        data = line.split(","); // ,으로 잘라오기

                        if(bot4_cnt == data.length/5){ // 마지막 까지 왔다면.. 나머지 전부 더하기
                            for (int i = bot4_cnt*5; i < data.length; i++) {
                                BOT4_note_data.add(Integer.parseInt(data[i]));
                            }
                            bot4_done = true;
                        }
                        if(bot4_cnt < data.length/5){ // 그외의 경우
                            for (int i = bot4_cnt*5; i < (bot4_cnt+1)*5; i++) {
                                BOT4_note_data.add(Integer.parseInt(data[i]));
                            }
                            bot4_cnt++;
                        }

                    }catch (Exception c){}
                }
            }

            if(BOT5_note_data.size() < 5){ // 5보다 적을 겨우에 더하기
                if(!bot5_done){
                    try {
                        BufferedReader buf = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SHAKER" + "/Note_data" + music_path + star_path));
                        line = buf.readLine();
                        line = buf.readLine();
                        line = buf.readLine();
                        line = buf.readLine();
                        line = buf.readLine();
                        data = line.split(","); // ,으로 잘라오기

                        if(bot5_cnt == data.length/5){ // 마지막 까지 왔다면.. 나머지 전부 더하기
                            for (int i = bot5_cnt*5; i < data.length; i++) {
                                BOT5_note_data.add(Integer.parseInt(data[i]));
                            }
                            bot5_done = true;
                        }
                        if(bot5_cnt < data.length/5){ // 그외의 경우
                            for (int i = bot5_cnt*5; i < (bot5_cnt+1)*5; i++) {
                                BOT5_note_data.add(Integer.parseInt(data[i]));
                            }
                            bot5_cnt++;
                        }
                    }catch (Exception c){}
                }
            }
//        }
//        else{ // 가져온 노래일 경우
//
//            if(thread_start) { // 서브 스레드 1 시작
//                if(!setting){
//                    // Setting 시작
//
//                    AudioManager audio = (AudioManager) getSystemService(this.AUDIO_SERVICE); // 디바이스 소리 서비스 접근.
//                    audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL); //소리, 무음, 진동 중에 소리로 설정.
//                    audio.setStreamVolume(AudioManager.STREAM_MUSIC, audio.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_PLAY_SOUND); // 소리 크기 설정.
//
//                    mEqualizer = new Equalizer(0, ms.mp.getAudioSessionId());
//                    mEqualizer.setEnabled(true);
//
//
//                    setting = true;
//                }
//
//
//                short numberFrequencyBands = mEqualizer.getNumberOfBands();
//
//                final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];
//                final short upperEqualizerBandLevel = mEqualizer.getBandLevelRange()[1];
//
//                for(short i = 0; i < numberFrequencyBands; i++) {
//
//                    final short equalizerBandIndex = i;
//
//                    for(short j = 0; j < numberFrequencyBands; j++) { // Gian값 조절
//                        if(j == equalizerBandIndex){
//                            mEqualizer.setBandLevel(j, (short) (upperEqualizerBandLevel)); // 해당 인덱스만 최대 설정.
//                        }
//                        mEqualizer.setBandLevel(j, (short) (lowerEqualizerBandLevel)); // 그외는 최소 설정.
//                    }
//
//
//                }
//
//            }
//        }

    }

    private class DrawView extends View {

        public DrawView(Context context) {
            super(context);
            // 노트 그림 설정
            myBitmap01 = BitmapFactory.decodeResource(getResources(), R.drawable.note2); // 노트 1
            myBitmap02 = BitmapFactory.decodeResource(getResources(), R.drawable.note1); // 노트 2
            myBitmap03 = BitmapFactory.decodeResource(getResources(), R.drawable.note3); // 노트 3
        }

        protected void onDraw(Canvas canvas) {

            for ( int i= 0; i < BOT1.size(); i++ ) // 하얀색
            {
                canvas.drawBitmap(myBitmap01, 0, BOT1.get(i), null);
            }

            for ( int i= 0; i < BOT2.size(); i++ ) // 노란색
            {
                canvas.drawBitmap(myBitmap02, myBitmap01.getWidth() + P, BOT2.get(i), null);
            }

            for ( int i= 0; i < BOT3.size(); i++ ) // 주황색
            {
                canvas.drawBitmap(myBitmap03, myBitmap01.getWidth()*2 + P*2, BOT3.get(i), null);
            }

            for ( int i= 0; i < BOT4.size(); i++ ) // 노란색
            {
                canvas.drawBitmap(myBitmap02, myBitmap01.getWidth()*3 + P*3, BOT4.get(i), null);
            }

            for ( int i= 0; i < BOT5.size(); i++ ) // 하얀색
            {
                canvas.drawBitmap(myBitmap01, myBitmap01.getWidth()*4 + P*4, BOT5.get(i), null);
            }

//            Log.e(tag,"onDraw");

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus == true) {
            P = ( canvas.getWidth() - (myBitmap01.getWidth()*5) ) / 4 ; // 여분 값 계산
            combo_progress.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY); // 주황색
        }
    }

    protected void onStart(){
        super.onStart();
        Log.e(tag,"onStart");
        th_check = true;  // 스레드 시작
        mSensorManager.registerListener(mAccLis, mAccelometerSensor, SensorManager.SENSOR_DELAY_UI); // 센서키기
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

    protected void onStop() { // 나갈 때
        super.onStop();
        Log.e(tag,"onStop");
        mSensorManager.unregisterListener(mAccLis); // 배터리 소모 없애기 위해 센서 끄기
        if(!check) {
            if(!FN_CNT){
                ms.Music_pause();
                onBackPressed();
            }
        }
    }

    protected void onDestroy() { // 꺼질 때
        super.onDestroy();
        Log.e(tag,"onDestroy");
        th_loop = false; // 스레드 종료
        th = null; // 스레드 비우기
        th2 = null;
        unbindService(conn); // 서비스 종료
    }

    protected void onRestart() { // 다시 들어올 때
        super.onRestart();
        check = false;
        Log.e(tag,"onRestart");
        if(!FN_CNT){
            ms.Music_on();
        }
    }

    private class AccelometerListener implements SensorEventListener { // 센서

        // 센서에 변화를 감지하기 위해 계속 호출되고 있는 함수
        @Override
        public void onSensorChanged(SensorEvent event) {

            if(sensor){
                //Log.e(tag,"onSensor");
                long currentTime = System.currentTimeMillis();
                long gabOfTime = (currentTime - lastTime); // 측정한 시간과 현재 시간의 차가 0.1초 이상일 때 (0.1초 이상 움직)

                if (gabOfTime > 100) {
                    lastTime = currentTime;
                    Log.e(tag,"sensor");
                    accX = event.values[SensorManager.DATA_X];
                    accY = event.values[SensorManager.DATA_Y];
                    accZ = event.values[SensorManager.DATA_Z];

                    speed = Math.abs(accX + accY + accZ - lastX - lastY - lastZ) / gabOfTime * 10000;

                    if (speed > SHAKE_THRESHOLD && !sensor_check) {
                        sensor_check = true; // 흔들렸어요.
                        sensor = false;
                    }

                    lastX = event.values[DATA_X];
                    lastY = event.values[DATA_Y];
                    lastZ = event.values[DATA_Z];
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    public void onBackPressed() {

        Log.e(tag,"onBackPressed");
        Intent intent = new Intent(getApplicationContext(),Game_PauseAT.class);
        intent.putExtra("SCREEN",SCREEN); // 화면 정보 주기
        startActivity(intent); // 다음 화면으로 넘어간다
    }

    public void set_SCREEN(boolean SCREEN) { // 일시정지에서 받아온 화면을 바꾸기
        this.SCREEN = SCREEN;
        if(SCREEN){ setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);}
        else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
    }

}
