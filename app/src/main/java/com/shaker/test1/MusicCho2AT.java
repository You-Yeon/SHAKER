package com.shaker.test1;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.audiofx.Equalizer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.os.IBinder;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// 해당 노래의 난이도를 선택하고 기록을 확인하는 화면 클래스입니다.

public class MusicCho2AT extends AppCompatActivity{

    String tag = "MusicCHo2AT"; // 태그

    Equalizer mEqualizer;

    static CardView v; // 카드 뷰 배경 설정
    boolean STAR = true; // 시작이에요

    MusicService ms; // 서비스 객체
    boolean isService = false; //서비스 여부
    boolean check; // 엑티비티가 넘어간걸 구별하기 위해서
    int cnt = 0; //몇번 눌렀는지 체크하기.

    ArrayList<Difficulty_info> DCinfoArrayList; // 난이도 리스트
    ArrayList<Difficulty_info> DCinfoArrayList_filter; // 필터링한 난이도 리스트
    static ArrayList<Record_info> RCinfoArrayList; // 음.. 기록 리스트
    static ArrayList<Record_info> RCinfoArrayList_filter; // 음.. 필터링한 기록 리스트
    static int Difficulty; // 난이도를 전달해줄꼐..

    Adapter_Dif Adapter_Dif; // 난이도 Adapter!
    static Adapter_Rec Adapter_Rec; // 점수 Adapter!

    private static JSONArray jsonArray; // JSON 배열
    private static String JSON_string; // JSON 배열형식으로 바꾸기

    static DatabaseReference mDatabase;

    static SharedPreferences sharePref = null; // 쉐어드 프리페런스
    static SharedPreferences.Editor editor = null; // 저장소 조작기

    static RecyclerView mRecyclerView; // 리사이클러뷰 난이도
    RecyclerView mRecyclerView2; // 리사이클러뷰 기록
    RecyclerView.LayoutManager mLayoutManager; // 모양 잡기 난이도
    RecyclerView.LayoutManager mLayoutManager2; // 모양 잡기 기록
    String title;
    String artist;
    String time;
    int draw_key;

    static TextView ID_text; // 아이디 값
    static String id_text; // 아이디 값

    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            // 서비스와 연결되었을 때 호출되는 메서드
            // 서비스 객체를 전역변수로 저장
            Log.e(tag,"onService");
            MusicService.MyBinder mb = (MusicService.MyBinder) service;
            ms = mb.getService(); // 서비스가 제공하는 메소드 호출하여
            // 서비스쪽 객체를 전달받을수 있슴
            equalizer();
            isService = true;
        }
        public void onServiceDisconnected(ComponentName name) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
            Log.e(tag,"offService");
            isService = false;
        }
    };

    public static Activity MusicCho2AT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.e(tag,"onCreate");
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.music_choice2_at);

        bindService(new Intent(MusicCho2AT.this,MusicService.class), conn, Context.BIND_AUTO_CREATE);

        check = false;

        ImageView profile = (ImageView)findViewById(R.id.profile); // 노래 사진

        ImageView Music = (ImageView)findViewById(R.id.musicimg); // 노래 사진
        TextView Title = (TextView)findViewById(R.id.titletext); // 제목
        TextView Singer = (TextView)findViewById(R.id.artisttext); // 가수
        TextView Time = (TextView)findViewById(R.id.timetext); // 시간
        ImageView bon = (ImageView) findViewById(R.id.back); // 뒤로가기
        ID_text = (TextView) findViewById(R.id.user_id); // 아이디 값

        Intent intent = getIntent(); // MusicChoAT에서 받아오기

        // 전달 받은 값들 다 넣어주깅

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
        artist = intent.getExtras().getString("Artist");
        Singer.setText(artist);
        id_text = intent.getExtras().getString("ID");
        ID_text.setText("ID : " + id_text);
        ID_text.setTextSize(18);

        //프로필 설정하기

        Bitmap myBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/SHAKER" + "/Profile" + "/" +id_text);
        profile.setImageBitmap(myBitmap);

        //난이도
        mRecyclerView = findViewById(R.id.DC_recycler_view); // 리사이클러뷰
        mRecyclerView.setHasFixedSize(true); // 사이즈 안바꾸니까 true
        mLayoutManager = new LinearLayoutManager(this); // 리니어 아래로
        mRecyclerView.setLayoutManager(mLayoutManager); // 만들기

        //기록
        mRecyclerView2 = findViewById(R.id.RC_recycler_view); // 리사이클러뷰
        mRecyclerView2.setHasFixedSize(true); // 사이즈 안바꾸니까 true
        mLayoutManager2 = new LinearLayoutManager(this); // 리니어 아래로
        mRecyclerView2.setLayoutManager(mLayoutManager2); // 만들기

        //어레이 리스트 선언
        DCinfoArrayList = new ArrayList<>(); // 난이도 list
        DCinfoArrayList_filter = new ArrayList<>(); // adapter에 들어갈 난이도 list
        RCinfoArrayList = new ArrayList<>(); // 기록 list
        RCinfoArrayList_filter = new ArrayList<>(); // adapter에 들어갈 기록 list

        // 난이도 추가하기............ :(
        // 이건 너무 하드 코딩 아닌가 싶다.

        DCinfoArrayList.add(new Difficulty_info(R.drawable.giving_in,  R.drawable.star1, "Note : 75"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.giving_in,  R.drawable.star2, "Note : 111"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.giving_in,  R.drawable.star3, "Note : 189"));

        DCinfoArrayList.add(new Difficulty_info(R.drawable.joystick,  R.drawable.star1, "Note : 93"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.joystick,  R.drawable.star2, "Note : 191"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.joystick,  R.drawable.star3, "Note : 281"));

        DCinfoArrayList.add(new Difficulty_info(R.drawable.kuyenda,  R.drawable.star1, "Note : 141"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.kuyenda,  R.drawable.star2, "Note : 181"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.kuyenda,  R.drawable.star3, "Note : 250"));

        DCinfoArrayList.add(new Difficulty_info(R.drawable.light_up_the_sky,  R.drawable.star1, "Note : 116"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.light_up_the_sky,  R.drawable.star2, "Note : 162"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.light_up_the_sky,  R.drawable.star3, "Note : 230"));

        DCinfoArrayList.add(new Difficulty_info(R.drawable.hold_on,  R.drawable.star1, "Note : 93"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.hold_on,  R.drawable.star2, "Note : 123"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.hold_on,  R.drawable.star3, "Note : 146"));

        DCinfoArrayList.add(new Difficulty_info(R.drawable.muffin,  R.drawable.star1, "Note : 86"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.muffin,  R.drawable.star2, "Note : 120"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.muffin,  R.drawable.star3, "Note : 183"));

        DCinfoArrayList.add(new Difficulty_info(R.drawable.sicc,  R.drawable.star1, "Note : 126"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.sicc,  R.drawable.star2, "Note : 194"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.sicc,  R.drawable.star3, "Note : 250"));

        DCinfoArrayList.add(new Difficulty_info(R.drawable.hope,  R.drawable.star1, "Note : 129"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.hope,  R.drawable.star2, "Note : 169"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.hope,  R.drawable.star3, "Note : 213"));

        DCinfoArrayList.add(new Difficulty_info(R.drawable.candyland,  R.drawable.star1, "Note : 91"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.candyland,  R.drawable.star2, "Note : 109"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.candyland,  R.drawable.star3, "Note : 155"));

        DCinfoArrayList.add(new Difficulty_info(R.drawable.sunburst,  R.drawable.star1, "Note : 127"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.sunburst,  R.drawable.star2, "Note : 171"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.sunburst,  R.drawable.star3, "Note : 236"));

        DCinfoArrayList.add(new Difficulty_info(R.drawable.delicious,  R.drawable.star1, "Note : 100"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.delicious,  R.drawable.star2, "Note : 124"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.delicious,  R.drawable.star3, "Note : 154"));

        DCinfoArrayList.add(new Difficulty_info(R.drawable.together,  R.drawable.star1, "Note : 100"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.together,  R.drawable.star2, "Note : 144"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.together,  R.drawable.star3, "Note : 205"));

        DCinfoArrayList.add(new Difficulty_info(R.drawable.whole,  R.drawable.star1, "Note : 108"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.whole,  R.drawable.star2, "Note : 132"));
        DCinfoArrayList.add(new Difficulty_info(R.drawable.whole,  R.drawable.star3, "Note : 152"));

        if(MusicChoAT.Music_DCArrayList.size() > 0){
            DCinfoArrayList.addAll(MusicChoAT.Music_DCArrayList); // 새로 넣은 노래들 넣기.
        }

        for(int i = 0; i < DCinfoArrayList.size(); i ++){ // 필터링
            if(DCinfoArrayList.get(i).drawableId == draw_key){
                Log.e("asdasd","Asdasd");

                DCinfoArrayList_filter.add(new Difficulty_info(DCinfoArrayList.get(i).drawableId,DCinfoArrayList.get(i).star,DCinfoArrayList.get(i).note_num ));
            }
        }

        //난이도 어뎁터
        Adapter_Dif = new Adapter_Dif(MusicCho2AT.this,DCinfoArrayList_filter,check);
        Adapter_Dif.get_title(title); // 제목 전달해주기
        Adapter_Dif.get_artist(artist); // 아티스트 전달해주기
        Adapter_Dif.get_time(time); // 시간 전달해주기

        mRecyclerView.setAdapter(Adapter_Dif); // mRecyclerView 객체에 myAdapter 객체를 연결합니다.

        //기록 어뎁터
        Adapter_Rec = new Adapter_Rec(MusicCho2AT.this,RCinfoArrayList_filter);
        mRecyclerView2.setAdapter(Adapter_Rec); // mRecyclerView 객체에 myAdapter 객체를 연결합니다.

        MusicCho2AT = MusicCho2AT.this; // finish를 할 수 있도록

        sharePref = getSharedPreferences("DATA",MODE_PRIVATE);  // "DATA"에서 가져오기
        editor = sharePref.edit(); // "DATA" 조작기

        Alladd_Record(draw_key); // shared에 있는 기록들 옮기기

        bon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 뒤로가기
                onBackPressed(); // 뒤로가기
                check = true; // 넘어가는거야 !
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus == true && STAR) {

            STAR = false; // 한번만
            if (DCinfoArrayList_filter.size() >0  ){

            show_Record(DCinfoArrayList_filter.get(0).star); // 처음 난이도 기록 보여주고
            v = (CardView)mRecyclerView.getLayoutManager().findViewByPosition(0);
            v.setCardBackgroundColor(Color.parseColor("#AA5AD188")); // 눌리게

            }
        }
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
        if(mEqualizer != null){
            mEqualizer.setEnabled(false);
            mEqualizer.release();
            mEqualizer = null;
        }
        Log.e(tag,"onPause");
    }

    protected void onStop() { // 나갈 때
        super.onStop();
        Log.e(tag,"onStop");
        if(!check && !Adapter_Dif.return_check()) {
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
        check = false; // 초기화
        cnt = 1; // 초기화
        Adapter_Dif.get_check(check);
        Adapter_Dif.get_cnt(cnt);
        Log.e(tag,"onRestart");
        ms.Music_on();
    }

    public void onBackPressed() {
        super.onBackPressed();
        check = true;
    }

    public static void Alladd_Record(int drawableId){

        //          로컬 저장하기

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

        try {

            // ----------------------
            //     기록 이동하기
            // ----------------------

            jsonArray = new JSONArray(JSON_string);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                if (id_text.equals(jsonObject1.getString("ID"))) { // 아이디에서 검색
                    // 기록 배열 가져오기
                    JSONArray Record = jsonObject1.getJSONArray("RECORD");
                    for (int j = 0; j < Record.length(); j++) { // 기록 중에서
                        JSONObject jsonObject2 = Record.getJSONObject(j);
                        if (String.valueOf(drawableId).equals(jsonObject2.getString("MUSIC_ID"))){ // 동일한 노래 id 값일 때
                            // 데이터 저장소에 옮기기..
                            RCinfoArrayList.add(new Record_info(drawableId, Integer.parseInt(jsonObject2.getString("STAR")), jsonObject2.getString("TIME"), Integer.parseInt(jsonObject2.getString("SCORE")), Integer.parseInt(jsonObject2.getString("RANK"))));
                        }
                    }
                    break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void get_Record(int drawableId, int difficulty, String time, int score, int rank) {

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

        try {

            frist = true; // 처음

            jsonArray = new JSONArray(JSON_string);
            String record = ""; // 기록 저장하는 곳

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                if(id_text.equals(jsonObject1.getString("ID"))){
                    // 기록 배열 가져오기
                    JSONArray Record = jsonObject1.getJSONArray("RECORD");
                    for(int j = 0; j< Record.length(); j++){
                        JSONObject jsonObject2= Record.getJSONObject(j);
                        if (frist) {
                            // 첫번째 키를 제외하고
                            frist = false;
                        } else // 그외에는 ,를 붙이기
                        {
                            record += ",";
                        }
                        record += jsonObject2; // 기존 기록들 저장하기
                    }
                    if(record.equals("")){ // 아무것도 없으면
                        //그냥 패스
                    }
                    else{ // 뭔가 있다면
                        record += ","; // 마지막 반점 , 신규 기록 있으니까
                    }
                    break;
                }
            }

            // 신규 기록 저장하기
            record += "{\"MUSIC_ID\": \""+drawableId +"\"," + "\"STAR\": \""+difficulty+"\"," + "\"TIME\": \""+time+"\"," + "\"SCORE\": \""+score+"\"," + "\"RANK\": \""+rank+"\"}";
            mDatabase = FirebaseDatabase.getInstance().getReference();

            //  파이어베이스 갱신

            Map<String, Object> childUpdates = new HashMap<>();
            // 값 넣어주기
            FirebasePost_record post_record = new FirebasePost_record(drawableId, difficulty, time, score,rank);
            childUpdates.put("/User/" + id_text + "/RECORD/" + time +"/",post_record.toMap());

            mDatabase.updateChildren(childUpdates);

            // ------------------------
            //     기록 위치 검색
            // ------------------------

            int position = 0; // 포지션값 가져오기
            int cnt = 0; // 카운트 하기
            String result = ""; // 결과
            String KEY = ""; // 키 값

            // 해당 아이디가 있는 위치 검색
            jsonArray = new JSONArray(JSON_string);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(id_text.equals(jsonObject.getString("ID"))){
                    position = i; // 포지션값 저장하기
                    result = "{\"ID\": \""+ jsonObject.getString("ID") +"\"," + "\"PASSWORD\": \""+jsonObject.getString("PASSWORD")+"\","+ "\"MUSIC_V\": \""+jsonObject.getString("MUSIC_V")+"\"," + "\"EFFECT_V\": \""+jsonObject.getString("EFFECT_V")+"\"," + "\"EFFECT_N\": \""+jsonObject.getString("EFFECT_N")+"\","  + "\"RECORD\": ["+record+"]" + ",\"MUSIC\": "+jsonObject.getString("MUSIC") +"}"; // 최종 결과 값 가져오기
                    break;
                }
            }

            // 해당위치의 키 값 검색
            for(Map.Entry<String, ?> entry : totalValue.entrySet()) {
                if( !entry.getKey().equals("Auto_Login") && !entry.getKey().equals("Auto_Login_ID")) { // 자동로그인 키와 아이디 값 제외
                    if (cnt == position) {
                        KEY = entry.getKey(); // 키 값 저장하기
                        break;
                    }
                    cnt++;
                }
            }

            // ---------------------
            //     기록 갱신하기
            // ---------------------

            editor.putString(KEY,result); // Update
            editor.apply(); // 저장소 갱신하기



        } catch (JSONException e) {
            e.printStackTrace();
        }

        RCinfoArrayList.add(new Record_info(drawableId, difficulty, time, score, rank));
        show_Record(difficulty);
    }

    public static void show_Record(int difficulty){
        ArrayList<Record_info> temp = new ArrayList<>();
        Difficulty = difficulty;
        RCinfoArrayList_filter.clear(); // 일단 청소좀 해주고

        for(int i = 0; i < RCinfoArrayList.size(); i ++){ // 필터링
            if(RCinfoArrayList.get(i).difficulty == difficulty){
                temp.add(new Record_info(RCinfoArrayList.get(i).drawableId, RCinfoArrayList.get(i).difficulty ,RCinfoArrayList.get(i).time ,RCinfoArrayList.get(i).score ,RCinfoArrayList.get(i).rank ));
            }
        }
        Log.e("야야", "여기 왔냐 ? ");

        RCinfoArrayList_filter.addAll(sort(temp)); // 정렬해서 보내기
        Adapter_Rec.notifyDataSetChanged();// 갱신
    }

    public static ArrayList sort(ArrayList<Record_info> RCinfoArrayList_filter){ // 버블.. 정렬을 사용해 볼까요?? :)

        int cnt = 0;
        int temp ; // 임시 저장 공간
        String temp2; // 임시 저장 공간

        for(int i = RCinfoArrayList_filter.size(); i > 0; i --){
            for(int j = 0; j<i-1; j++){
                cnt++;
                if( RCinfoArrayList_filter.get(j).score < RCinfoArrayList_filter.get(j+1).score){

                    //점수
                    temp = RCinfoArrayList_filter.get(j).score;
                    RCinfoArrayList_filter.get(j).score = RCinfoArrayList_filter.get(j+1).score;
                    RCinfoArrayList_filter.get(j+1).score = temp;

                    //랭크
                    temp = RCinfoArrayList_filter.get(j).rank;
                    RCinfoArrayList_filter.get(j).rank = RCinfoArrayList_filter.get(j+1).rank;
                    RCinfoArrayList_filter.get(j+1).rank = temp;

                    //시간
                    temp2 = RCinfoArrayList_filter.get(j).time;
                    RCinfoArrayList_filter.get(j).time = RCinfoArrayList_filter.get(j+1).time;
                    RCinfoArrayList_filter.get(j+1).time = temp2;
                }
            }
        }

        return RCinfoArrayList_filter;
    }

    public static void del_Record(int drawableId, int difficulty, String time){

        for(int i = 0; i < RCinfoArrayList.size(); i ++){ // 필터링
            if(RCinfoArrayList.get(i).drawableId == drawableId && RCinfoArrayList.get(i).difficulty == difficulty && RCinfoArrayList.get(i).time.equals(time)){
                    RCinfoArrayList.remove(i);
                    Adapter_Rec.notifyItemRemoved(i);
            }
        }
        Adapter_Rec.notifyDataSetChanged();// 갱신
    }

    public void equalizer(){

        mEqualizer = new Equalizer(0, ms.mp.getAudioSessionId());
        mEqualizer.setEnabled(true);

        final short upperEqualizerBandLevel = mEqualizer.getBandLevelRange()[1];

        mEqualizer.setBandLevel((short)0, (short) (upperEqualizerBandLevel));
        mEqualizer.setBandLevel((short)1, (short) (upperEqualizerBandLevel));
        mEqualizer.setBandLevel((short)2, (short) (upperEqualizerBandLevel/2));


    }

}
