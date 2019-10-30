package com.shaker.test1;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

// 환경 설정 화면 클래스입니다.

public class SetAT extends AppCompatActivity {

    String tag = "SetAT"; // 태그

    public SoundPool ef_sp; // 효과음 들려주기
    int sound_beep_alert = 0; // 파일 로드
    String ef_name; // 효과음 이름
    float change_volume; // 효과음 볼륨

    MusicService ms; // 서비스 객체
    boolean isService = false; //서비스 여부

    private JSONArray jsonArray; // JSON 배열
    private String JSON_string; // JSON 배열형식으로 바꾸기

    static SharedPreferences sharePref = null; // 쉐어드 프리페런스
    static SharedPreferences.Editor editor = null; // 저장소 조작기

    boolean check; // 엑티비티가 넘어간걸 구별하기 위해서
    private Uri mImageCaptureUri;
    private ImageView mPhotoImageView;
    private Button mButton;
    MusicChoAT MusicChoAT;
    SeekBar sb1; // 노래 볼륨
    SeekBar sb2; // 효과음 볼륨
    Spinner spr; // 효과음 스피너

    TextView ID_text; // 아이디 값
    String id_text; // 아이디 값

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            // 서비스와 연결되었을 때 호출되는 메서드
            // 서비스 객체를 전역변수로 저장
            Log.e(tag,"onService");
            MusicService.MyBinder mb = (MusicService.MyBinder) service;
            ms = mb.getService(); // 서비스가 제공하는 메소드 호출하여
            // 서비스쪽 객체를 전달받을수 있슴
            isService = true;
            sb1.setProgress(ms.mp_volume);
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
        Log.e(tag,"onCreate");
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.set_at);

        MusicChoAT = (MusicChoAT) com.shaker.test1.MusicChoAT.MusicChoAT; // MusicChoAT의 객체
        bindService(new Intent(SetAT.this,MusicService.class), conn, Context.BIND_AUTO_CREATE);

        sharePref = getSharedPreferences("DATA",MODE_PRIVATE);  // "DATA"에서 가져오기
        editor = sharePref.edit(); // "DATA" 조작기

        check = false;
        ef_sp = new SoundPool(10, AudioManager.STREAM_MUSIC,0); // 효과음 soundpool

        Button bon = (Button)findViewById(R.id.Logout); // 로그아웃
        Button bon3 = (Button)findViewById(R.id.Closure); // 회원 탈퇴
        ImageView bon2 = (ImageView)findViewById(R.id.BACK); // 뒤로가기
        mButton = (Button) findViewById(R.id.CH_PRO); // 사진 고르기 버튼
        mPhotoImageView = (ImageView) findViewById(R.id.PRofile); //사진
        sb1  = (SeekBar) findViewById(R.id.music_seekbar); // 노래 볼륨 설정
        sb2  = (SeekBar) findViewById(R.id.touch_seekbar); // 효과음 볼륨 설정
        spr = (Spinner) findViewById(R.id.touch_spinner); //  효과음 스피너
        ID_text = (TextView) findViewById(R.id.User_id); // 유저 아이디 값

        // 인텐트 받아오기

        Intent intent = getIntent(); // LoginAT이나 Main에서 받아오기
        id_text = intent.getExtras().getString("ID");
        // 전달 받은 값 넣어주깅
        ID_text.setText("ID : " + id_text);
        ID_text.setTextSize(18);

        //프로필 설정하기

        Bitmap myBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/SHAKER" + "/Profile" + "/" +id_text);
        mPhotoImageView.setImageBitmap(myBitmap);

        //스피너 설정하기

        final String[] effect_data = getResources().getStringArray(R.array.sound_effect); // 데이터 받아오기
        ArrayAdapter<String> spr_adapter = new ArrayAdapter<String>(SetAT.this,R.layout.support_simple_spinner_dropdown_item, effect_data); // 데이터와 화면에 연결될 어댑터 생성
        spr.setAdapter(spr_adapter); // 어댑터에 연결


        // 스피너 디폴트 값 설정

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

        // 해당 아이디가 있는 위치 검색
        try {

            jsonArray = new JSONArray(JSON_string);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(id_text.equals(jsonObject.getString("ID"))){

                    // 디폴트 값 설정하기
                    if(jsonObject.getString("EFFECT_N").equals("kick")){
                        spr.setSelection(0); ef_name = "kick"; }
                    if(jsonObject.getString("EFFECT_N").equals("snare")){
                        spr.setSelection(1); ef_name = "snare"; }
                    if(jsonObject.getString("EFFECT_N").equals("tom")){
                        spr.setSelection(2); ef_name = "tom"; }
                    if(jsonObject.getString("EFFECT_N").equals("bubble")){
                        spr.setSelection(3); ef_name = "bubble"; }

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
            sound_beep_alert = ef_sp.load(SetAT.this, R.raw.kick,1);
        }
        else if(ef_name.equals("snare")){ // snare
            sound_beep_alert = ef_sp.load(SetAT.this, R.raw.snare,1);
        }
        else if(ef_name.equals("tom")){ // tom
            sound_beep_alert = ef_sp.load(SetAT.this, R.raw.tom,1);
        }
        else { // bubble
            sound_beep_alert = ef_sp.load(SetAT.this, R.raw.bubble, 1);
        }

        // ------------------------
        //     효과음 볼륨 추출
        // ------------------------

        change_volume = 0.0f; // 단위 바꾸기
        int effet_v_temp= 0;

        // 해당 아이디가 있는 위치 검색
        try {

            jsonArray = new JSONArray(JSON_string);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(id_text.equals(jsonObject.getString("ID"))){
                    effet_v_temp = Integer.parseInt(jsonObject.getString("EFFECT_V")); // 초기 설정
                    change_volume = Float.parseFloat(jsonObject.getString("EFFECT_V")) * 1/100;
                    break;
                }
            }

        } catch (JSONException e) { e.printStackTrace(); }

        //---------------------------------------------------------------------------------

        sb2.setProgress(effet_v_temp); // 원래 값 가져오기

        bon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 로그아웃
                Intent intent = new Intent(getApplicationContext(),LoginAT.class);

                if(sharePref.getBoolean("Auto_Login",false)) // 자동로그인 여부가 있다면
                {
                    editor.remove("Auto_Login");
                    editor.remove("Auto_Login_ID");
                    editor.commit();
                    // 자동로그인과 아이디 값 지우기
                }

                startActivity(intent); // 다음 화면으로 넘어간다
                check = true; // 화면넘어간거야 !
                MusicChoAT.finish(); //MusicChoAT 끝내기
                finish(); // SetAT 끝내기
            }
        });

        bon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 뒤로가기
                onBackPressed();
                check = true; // 화면넘어간거야 !
            }
        });

        bon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 회원탈퇴

                Intent intent = new Intent(getApplicationContext(),LoginAT.class);

                if(sharePref.getBoolean("Auto_Login",false)) // 자동로그인 여부가 있다면
                {
                    editor.remove("Auto_Login");
                    editor.remove("Auto_Login_ID");
                    editor.commit();
                    // 자동로그인과 아이디 값 지우기
                }

                // --------------------------------
                //     회원 정보를 JSON으로 추출
                // --------------------------------

                boolean frist = true; // 처음

                JSON_string =""; // 저장 공간 비우기
                Map<String, ?> totalValue = sharePref.getAll(); // 저장소에 있는 정보를 다 넣기
                for(Map.Entry<String, ?> entry : totalValue.entrySet()){
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

                // -----------------------
                //      회원 탈퇴하기
                // -----------------------

                int position = 0; // 포지션값 가져오기
                int cnt = 0; // 카운트 하기

                try {

                    // --------------------------------
                    //   회원 정보의 포지션값 가져오기
                    // --------------------------------

                    jsonArray = new JSONArray(JSON_string);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if(id_text.equals(jsonObject.getString("ID"))){
                            position = i; // 포지션값 저장하기
                            break;
                        }
                    }

                    // --------------------------------
                    //   해당 포지션 값을 지우기
                    // --------------------------------

                    for(Map.Entry<String, ?> entry : totalValue.entrySet()){
                        if( !entry.getKey().equals("Auto_Login") && !entry.getKey().equals("Auto_Login_ID")) { // 자동로그인 키와 아이디 값 제외
                            if(cnt == position){
                                editor.remove(entry.getKey());
                                editor.commit();
                                break;
                            }
                            cnt++;
                        }
                    }
                    // 파이어베이스 데이터 삭제
                    Query applesQuery = FirebaseDatabase.getInstance().getReference("User").orderByChild("ID").equalTo(id_text);
                    applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                appleSnapshot.getRef().removeValue();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    // ------------------
                    //   프로필 지우기
                    // ------------------

                    File fileCacheItem = new File( Environment.getExternalStorageDirectory().getAbsolutePath()+ "/SHAKER" + "/Profile" + "/" + id_text);
                    fileCacheItem.delete();

                    //storage 주소와 폴더 파일명을 지정해 준다.
                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://shaker-45d31.appspot.com").child("profile/" + id_text);

                    //삭제
                    storageRef.delete();

                    //------------------

                    startActivity(intent); // 다음 화면으로 넘어간다
                    check = true; // 화면넘어간거야 !
                    MusicChoAT.finish(); //MusicChoAT 끝내기
                    finish(); // SetAT 끝내기

                }catch(JSONException e){
                    e.printStackTrace();
                    Toast toast = Toast.makeText(SetAT.this, "회원 탈퇴를 실패했습니다.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        doTakePhotoAction();
                    }
                };

                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        doTakeAlbumAction();
                    }
                };

                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(SetAT.this)
                        .setTitle("업로드할 이미지 선택")
                        .setPositiveButton("사진촬영", cameraListener)
                        .setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();
            }
        });

        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // 볼륨 설정 하는 곳
            public void onStartTrackingTouch(SeekBar seekBar) { // 누른 순간
            }

            public void onStopTrackingTouch(SeekBar seekBar) { // 멈춘 순간

            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { // 드래그 하는 동안
                ms.Music_Volume(progress); // 음악 볼륨 설정해버리깅

                // 사운드 크기 설정

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
                //     볼륨 위치 검색
                // ------------------------

                int position = 0; // 포지션값 가져오기
                int cnt = 0; // 카운트 하기
                String result = ""; // 결과
                String KEY = ""; // 키 값

                // 해당 아이디가 있는 위치 검색
                try {

                    jsonArray = new JSONArray(JSON_string);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if(id_text.equals(jsonObject.getString("ID"))){
                            position = i; // 포지션값 저장하기
                            result = "{\"ID\": \""+ jsonObject.getString("ID") +"\"," + "\"PASSWORD\": \""+jsonObject.getString("PASSWORD")+"\","+ "\"MUSIC_V\": \""+String.valueOf(progress)+"\"," + "\"EFFECT_V\": \""+jsonObject.getString("EFFECT_V")+"\"," + "\"EFFECT_N\": \""+jsonObject.getString("EFFECT_N")+"\","  + "\"RECORD\": "+jsonObject.getString("RECORD")+ ",\"MUSIC\": "+jsonObject.getString("MUSIC")+"}"; // 최종 결과 값 가져오기
                            break;
                        }
                    }

                } catch (JSONException e) { e.printStackTrace(); }

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
                //     볼륨 갱신하기
                // ---------------------

                editor.putString(KEY,result); // Update
                editor.apply(); // 저장소 갱신하기
                Log.e("share : ",result);

                //---------------------------------------------------------------------------------
            }
        });

        sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // 볼륨 설정 하는 곳
            public void onStartTrackingTouch(SeekBar seekBar) { // 누른 순간
            }

            public void onStopTrackingTouch(SeekBar seekBar) { // 멈춘 순간

                //효과음 플레이
                ef_sp.play(sound_beep_alert,change_volume,change_volume,0,0,1f);

            }
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { // 드래그 하는 동안

                // 사운드 크기 설정

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
                //     볼륨 위치 검색
                // ------------------------

                int position = 0; // 포지션값 가져오기
                int cnt = 0; // 카운트 하기
                String result = ""; // 결과
                String KEY = ""; // 키 값

                // 해당 아이디가 있는 위치 검색
                try {

                    jsonArray = new JSONArray(JSON_string);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if(id_text.equals(jsonObject.getString("ID"))){
                            position = i; // 포지션값 저장하기
                            change_volume = (float)progress * 1/100; // 효과음 음량바꾸기
                            result = "{\"ID\": \""+ jsonObject.getString("ID") +"\"," + "\"PASSWORD\": \""+jsonObject.getString("PASSWORD")+"\","+ "\"MUSIC_V\": \""+jsonObject.getString("MUSIC_V")+"\"," + "\"EFFECT_V\": \""+String.valueOf(progress)+"\"," + "\"EFFECT_N\": \""+jsonObject.getString("EFFECT_N")+"\","  + "\"RECORD\": "+jsonObject.getString("RECORD")+ ",\"MUSIC\": "+jsonObject.getString("MUSIC")+"}"; // 최종 결과 값 가져오기
                            break;
                        }
                    }

                } catch (JSONException e) { e.printStackTrace(); }

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
                //     볼륨 갱신하기
                // ---------------------

                editor.putString(KEY,result); // Update
                editor.apply(); // 저장소 갱신하기
                Log.e("share : ",result);

                //---------------------------------------------------------------------------------
            }
        });

        spr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // 사운드 종류 설정

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
                //     효과음 위치 검색
                // ------------------------

                int position2 = 0; // 포지션값 가져오기
                int cnt = 0; // 카운트 하기
                String result = ""; // 결과
                String KEY = ""; // 키 값

                // 해당 아이디가 있는 위치 검색
                try {

                    jsonArray = new JSONArray(JSON_string);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if(id_text.equals(jsonObject.getString("ID"))){
                            position2 = i; // 포지션값 저장하기
                            ef_name = String.valueOf(spr.getItemAtPosition(position)); // 효과음 출력도 바꾸기
                            result = "{\"ID\": \""+ jsonObject.getString("ID") +"\"," + "\"PASSWORD\": \""+jsonObject.getString("PASSWORD")+"\","+ "\"MUSIC_V\": \""+jsonObject.getString("MUSIC_V")+"\"," + "\"EFFECT_V\": \""+jsonObject.getString("EFFECT_V")+"\"," + "\"EFFECT_N\": \""+String.valueOf(spr.getItemAtPosition(position))+"\","  + "\"RECORD\": "+jsonObject.getString("RECORD")+ ",\"MUSIC\": "+jsonObject.getString("MUSIC")+"}"; // 최종 결과 값 가져오기
                            break;
                        }
                    }

                } catch (JSONException e) { e.printStackTrace(); }

                // 해당위치의 키 값 검색
                for(Map.Entry<String, ?> entry : totalValue.entrySet()) {
                    if( !entry.getKey().equals("Auto_Login") && !entry.getKey().equals("Auto_Login_ID")) { // 자동로그인 키와 아이디 값 제외

                        if (cnt == position2) {
                            KEY = entry.getKey(); // 키 값 저장하기
                            break;
                        }
                        cnt++;
                    }
                }

                // ---------------------
                //     효과음 갱신하기
                // ---------------------

                editor.putString(KEY,result); // Update
                editor.apply(); // 저장소 갱신하기
                Log.e("share : ",result);


                // ---------------------
                //   효과음 종류 선택
                // ---------------------

                // 파일 재 로드

                if(ef_name.equals("kick")){ // kick
                    sound_beep_alert = ef_sp.load(SetAT.this, R.raw.kick,1);
                }
                else if(ef_name.equals("snare")){ // snare
                    sound_beep_alert = ef_sp.load(SetAT.this, R.raw.snare,1);
                }
                else if(ef_name.equals("tom")){ // tom
                    sound_beep_alert = ef_sp.load(SetAT.this, R.raw.tom,1);
                }
                else{ // bubble
                    sound_beep_alert = ef_sp.load(SetAT.this, R.raw.bubble,1);
                }

                //---------------------------------------------------------------------------------
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }
    /**
     * 카메라에서 이미지 가져오기
     */
    private void doTakePhotoAction()
    {
        Log.e(tag,"doTakePhotoAction");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }


    /**
     * 앨범에서 이미지 가져오기
     */

    private void doTakeAlbumAction()
    {
        // 앨범 호출
        Log.e(tag,"doTakeAlbumAction");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.e(tag,"onActivityResult");
        if(resultCode != RESULT_OK)
        {
            return;
        }
        switch(requestCode)
        {
            case CROP_FROM_CAMERA:
            {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.
                final Bundle extras = data.getExtras();

                if(extras != null)
                {
                    Bitmap photo = extras.getParcelable("data");

                    // ---------------------------
                    //    프로필 이미지 저장하기
                    // ---------------------------

                    //storage 주소와 폴더 파일명을 지정해 준다.
                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://shaker-45d31.appspot.com").child("profile/" + id_text);

                    File fileCacheItem = new File( Environment.getExternalStorageDirectory().getAbsolutePath()+ "/SHAKER" + "/Profile" + "/" + id_text);
                    OutputStream out;

                    try {

                        fileCacheItem.createNewFile();
                        out = new FileOutputStream(fileCacheItem);
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.close();

                        // 업로드
                        storageRef.putFile(Uri.fromFile(fileCacheItem));

                    } catch (Exception e) { e.printStackTrace(); }

                    MusicChoAT.Update_profile(); // 프로필 업데이트
                    mPhotoImageView.setImageBitmap(photo);
                }
                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if(f.exists())
                {
                    f.delete();
                }

                break;
            }

            case PICK_FROM_ALBUM:
            {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.
                mImageCaptureUri = data.getData();
            }

            case PICK_FROM_CAMERA:
            {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");
                intent.putExtra("outputX", 130);
                intent.putExtra("outputY", 130);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);

                break;
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

        //    파이어 베이스 갱신

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
            //     볼륨 이동하기
            // ----------------------

            jsonArray = new JSONArray(JSON_string);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (id_text.equals(jsonObject.getString("ID"))) { // 아이디에서 검색
                    // 기록 배열 가져오기
                    FirebaseDatabase.getInstance().getReference("User").child(id_text).child("MUSIC_V").setValue(jsonObject.getString("MUSIC_V"));
                    FirebaseDatabase.getInstance().getReference("User").child(id_text).child("EFFECT_V").setValue(jsonObject.getString("EFFECT_V"));
                    FirebaseDatabase.getInstance().getReference("User").child(id_text).child("EFFECT_N").setValue(jsonObject.getString("EFFECT_N"));
                    break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    public void onBackPressed() {
        super.onBackPressed();
        check = true;
    }

}
