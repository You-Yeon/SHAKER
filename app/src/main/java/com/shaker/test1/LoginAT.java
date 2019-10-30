package com.shaker.test1;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.IBinder;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

// 로그인 화면 클래스입니다.

public class LoginAT extends AppCompatActivity {

    String tag = "LoginAT"; // 태그

    MusicService ms; // 서비스 객체
    boolean isService = false; //서비스 여부
    boolean check; // 엑티비티가 넘어간걸 구별하기 위해서

    private DatabaseReference mDatabase;
    private FirebaseStorage storage;

    Boolean FB_CHECK; // 파이어베이스 회원정보 여부
    Boolean U_CHECK1;

    private JSONArray jsonArray; // JSON 배열
    private String JSON_string; // JSON 배열형식으로 바꾸기
    static SharedPreferences sharePref = null; // 쉐어드 프리페런스
    static SharedPreferences.Editor editor = null; // 저장소 조작기

    EditText ID; // 아이디 입력 창
    EditText PW; // 비밀번호 입력 창
    CheckBox cb; // 자동로그인

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

        FirebaseApp.initializeApp(LoginAT.this);
        setContentView(R.layout.login_at);
        check = false;

        tedPermission(); // 권한 받아오기 !

        ID = (EditText)findViewById(R.id.editID);
        ID.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(20)}); //영문,, 숫자, 특수기호, 글자수 제한
        PW = (EditText)findViewById(R.id.editPS);
        PW.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(20)}); //영문,, 숫자, 특수기호, 글자수 제한
        Button bon = (Button)findViewById(R.id.login);
        Button bon2 = (Button)findViewById(R.id.sign);

        cb = (CheckBox)findViewById(R.id.checkBox); // 자동로그인 체크박스

        sharePref = getSharedPreferences("DATA",MODE_PRIVATE);  // "DATA"에서 가져오기
        editor = sharePref.edit(); // "DATA" 조작기

        bindService(new Intent(LoginAT.this,MusicService.class), conn, Context.BIND_AUTO_CREATE);

        bon.setOnClickListener(new View.OnClickListener() { // 로그인
            @Override
            public void onClick(View view) {
                if(ID.getText().toString().length() == 0 ) // 아이디 확인
                {
                    Toast toast = Toast.makeText(LoginAT.this, "아이디를 적어주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                {
                    if(PW.getText().toString().length() == 0 ) // 비밀번호 확인
                    {
                        Toast toast = Toast.makeText(LoginAT.this, "비밀번호를 적어주세요.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else // 아이디와 비밀번호가 다 적혀져 있다면
                    {
                        // --------------------------------
                        //     회원 정보를 JSON으로 추출
                        // --------------------------------
                        boolean frist = true; // 처음

                        JSON_string =""; // 저장 공간 비우기
                        Map<String, ?> totalValue = sharePref.getAll(); // 저장소에 있는 정보를 다 넣기
                        for(Map.Entry<String, ?> entry : totalValue.entrySet()){
                            Log.e("share : ",entry.getKey() + ": "+ entry.getValue());
                            if( !entry.getKey().equals("Auto_Login") && !entry.getKey().equals("Auto_Login_ID")) { // 자동로그인 키와 아이디 값 제외

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

                            // --------------------------------
                            //     회원 정보가 있나요..?
                            // --------------------------------
                            U_CHECK1 = false; // 회원 여부 체크

                        try {
                                jsonArray = new JSONArray(JSON_string);
                                for(int i = 0 ; i<jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if((ID.getText().toString()).equals(jsonObject.getString("ID"))) // 입력한 아이디와 기존 아이디가 같을 경우에는
                                        {
                                            U_CHECK1 = true; // 회원 정보가 있네요 !
                                            break;
                                        }
                                }

                                // 로컬에 회원 정보가 없는 경우.

                                if(!U_CHECK1)
                                {
                                    mDatabase = FirebaseDatabase.getInstance().getReference("User");
                                    FB_CHECK = false;
                                    // Read from the database
                                    mDatabase.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                                                if((ID.getText().toString()).equals(fileSnapshot.getKey())){ // 파이어베이스에는 회원 정보가 있다.
                                                    FB_CHECK = true; // 일치합니다.
                                                    Toast toast = Toast.makeText(LoginAT.this, "회원 정보를 가져오는 중입니다.", Toast.LENGTH_SHORT);
                                                    toast.show();

                                                    break;
                                                }
                                            }

                                            // 파이어베이스에는 회원 정보가 있는 경우에는 로컬에 새로 추가를 해준다.
                                            if(FB_CHECK){
                                                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                                                    if((ID.getText().toString()).equals(fileSnapshot.getKey())) { // 파이어베이스에는 회원 정보가 있다.

                                                        //-------------------------------
                                                        //   로컬로 회원정보 저장하기
                                                        //-------------------------------
                                                        String record = ""; // 기록 저장하는 곳

                                                        try {

                                                            boolean frist = true; //처음

                                                            jsonArray = new JSONArray(JSON_string);

                                                            for (DataSnapshot fileSnapshot2 : dataSnapshot.child(fileSnapshot.getKey()).child("RECORD").getChildren()){
                                                                    if (frist) {
                                                                        // 첫번째 키를 제외하고
                                                                        frist = false;
                                                                    } else // 그외에는 ,를 붙이기
                                                                    {
                                                                        record += ",";
                                                                    }
                                                                    record += "{\"MUSIC_ID\": \""+fileSnapshot2.child("MUSIC_ID").getValue() +"\"," + "\"STAR\": \""+fileSnapshot2.child("STAR").getValue()+"\"," + "\"TIME\": \""+fileSnapshot2.child("TIME").getValue()+"\"," + "\"SCORE\": \""+fileSnapshot2.child("SCORE").getValue()+"\"," + "\"RANK\": \""+fileSnapshot2.child("RANK").getValue()+"\"}"; // 기존 기록들 저장하기
                                                            }
                                                        }catch (Exception e){}

                                                        String result; // 결과

                                                        result = "{\"ID\": \""+ String.valueOf(fileSnapshot.child("ID").getValue()) +"\"," + "\"PASSWORD\": \""+ String.valueOf(fileSnapshot.child("PASSWORD").getValue())+"\"," + "\"MUSIC_V\": \""+String.valueOf(fileSnapshot.child("MUSIC_V").getValue())+"\","+ "\"EFFECT_V\": \""+String.valueOf(fileSnapshot.child("EFFECT_V").getValue())+"\"," + "\"EFFECT_N\": \""+String.valueOf(fileSnapshot.child("EFFECT_N").getValue())+"\"," + "\"RECORD\": ["+record+"]," +
                                                                "\"MUSIC\": [{\"MUSIC_ID\":\""+ R.drawable.giving_in+"\",\"TITLE\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.giving_in)).child("TITLE").getValue())+"\",\"ARTIST\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.giving_in)).child("ARTIST").getValue())+"\",\"TIME\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.giving_in)).child("TIME").getValue())+"\",\"FAV\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.giving_in)).child("FAV").getValue())+"\"}," +
                                                                "{\"MUSIC_ID\":\""+ R.drawable.joystick+"\",\"TITLE\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.joystick)).child("TITLE").getValue())+"\",\"ARTIST\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.joystick)).child("ARTIST").getValue())+"\",\"TIME\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.joystick)).child("TIME").getValue())+"\",\"FAV\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.joystick)).child("FAV").getValue())+"\"}," +
                                                                "{\"MUSIC_ID\":\""+ R.drawable.kuyenda+"\",\"TITLE\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.kuyenda)).child("TITLE").getValue())+"\",\"ARTIST\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.kuyenda)).child("ARTIST").getValue())+"\",\"TIME\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.kuyenda)).child("TIME").getValue())+"\",\"FAV\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.kuyenda)).child("FAV").getValue())+"\"}," +
                                                                "{\"MUSIC_ID\":\""+ R.drawable.light_up_the_sky+"\",\"TITLE\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.light_up_the_sky)).child("TITLE").getValue())+"\",\"ARTIST\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.light_up_the_sky)).child("ARTIST").getValue())+"\",\"TIME\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.light_up_the_sky)).child("TIME").getValue())+"\",\"FAV\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.light_up_the_sky)).child("FAV").getValue())+"\"}," +
                                                                "{\"MUSIC_ID\":\""+ R.drawable.hold_on+"\",\"TITLE\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.hold_on)).child("TITLE").getValue())+"\",\"ARTIST\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.hold_on)).child("ARTIST").getValue())+"\",\"TIME\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.hold_on)).child("TIME").getValue())+"\",\"FAV\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.hold_on)).child("FAV").getValue())+"\"}," +
                                                                "{\"MUSIC_ID\":\""+ R.drawable.muffin+"\",\"TITLE\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.muffin)).child("TITLE").getValue())+"\",\"ARTIST\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.muffin)).child("ARTIST").getValue())+"\",\"TIME\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.muffin)).child("TIME").getValue())+"\",\"FAV\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.muffin)).child("FAV").getValue())+"\"}," +
                                                                "{\"MUSIC_ID\":\""+ R.drawable.sicc+"\",\"TITLE\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.sicc)).child("TITLE").getValue())+"\",\"ARTIST\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.sicc)).child("ARTIST").getValue())+"\",\"TIME\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.sicc)).child("TIME").getValue())+"\",\"FAV\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.sicc)).child("FAV").getValue())+"\"}," +
                                                                "{\"MUSIC_ID\":\""+ R.drawable.hope+"\",\"TITLE\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.hope)).child("TITLE").getValue())+"\",\"ARTIST\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.hope)).child("ARTIST").getValue())+"\",\"TIME\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.hope)).child("TIME").getValue())+"\",\"FAV\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.hope)).child("FAV").getValue())+"\"}," +
                                                                "{\"MUSIC_ID\":\""+ R.drawable.candyland+"\",\"TITLE\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.candyland)).child("TITLE").getValue())+"\",\"ARTIST\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.candyland)).child("ARTIST").getValue())+"\",\"TIME\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.candyland)).child("TIME").getValue())+"\",\"FAV\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.candyland)).child("FAV").getValue())+"\"}," +
                                                                "{\"MUSIC_ID\":\""+ R.drawable.sunburst+"\",\"TITLE\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.sunburst)).child("TITLE").getValue())+"\",\"ARTIST\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.sunburst)).child("ARTIST").getValue())+"\",\"TIME\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.sunburst)).child("TIME").getValue())+"\",\"FAV\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.sunburst)).child("FAV").getValue())+"\"}," +
                                                                "{\"MUSIC_ID\":\""+ R.drawable.delicious+"\",\"TITLE\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.delicious)).child("TITLE").getValue())+"\",\"ARTIST\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.delicious)).child("ARTIST").getValue())+"\",\"TIME\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.delicious)).child("TIME").getValue())+"\",\"FAV\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.delicious)).child("FAV").getValue())+"\"}," +
                                                                "{\"MUSIC_ID\":\""+ R.drawable.together+"\",\"TITLE\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.together)).child("TITLE").getValue())+"\",\"ARTIST\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.together)).child("ARTIST").getValue())+"\",\"TIME\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.together)).child("TIME").getValue())+"\",\"FAV\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.together)).child("FAV").getValue())+"\"}," +
                                                                "{\"MUSIC_ID\":\""+ R.drawable.whole+"\",\"TITLE\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.whole)).child("TITLE").getValue())+"\",\"ARTIST\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.whole)).child("ARTIST").getValue())+"\",\"TIME\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.whole)).child("TIME").getValue())+"\",\"FAV\":\""+String.valueOf(fileSnapshot.child("MUSIC").child(String.valueOf(R.drawable.whole)).child("FAV").getValue())+"\"}]}";

                                                        Map<String, ?> totalValue = sharePref.getAll(); // 저장소에 있는 모든 결과를 map형식으로
                                                        int temp = 0; // 임시 저장소, key값을 순서대로 하기 위해서!

                                                        for(Map.Entry<String, ?> entry : totalValue.entrySet()) {
                                                            if (temp <= Integer.parseInt(entry.getKey())){
                                                                temp = Integer.parseInt(entry.getKey()) +1 ; // 키에 1씩 더하기
                                                            }
                                                        }
                                                        Log.e(tag,String.valueOf(temp));

                                                        editor.putString(String.valueOf(temp),result); // temp번호로 등록하기
                                                        editor.commit(); // 저장하자!

                                                        //-----------------------------------------
                                                        //      프로필 파일 불러와서 저장하기
                                                        //-----------------------------------------

                                                        storage = FirebaseStorage.getInstance();
                                                        final String id_temp = String.valueOf(fileSnapshot.child("ID").getValue());
                                                        final String ps_temp = String.valueOf(fileSnapshot.child("PASSWORD").getValue());

                                                        final File fileCacheItem = new File( Environment.getExternalStorageDirectory().getAbsolutePath()+ "/SHAKER" + "/Profile/" + id_temp);
                                                        StorageReference storageRef = storage.getReferenceFromUrl("gs://shaker-45d31.appspot.com").child("profile");

                                                        //다운로드할 파일을 가르키는 참조 만들기
                                                        StorageReference pathReference = storageRef.child(id_temp);

                                                        //Url을 다운받기
                                                        pathReference.getDownloadUrl();

                                                        //휴대폰 로컬 영역에 저장하기
                                                        try {
                                                            pathReference.getFile(fileCacheItem).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                                    Toast.makeText(getApplicationContext(), "프로필 저장 성공", Toast.LENGTH_SHORT).show();

                                                                    // -----------------------------------------------
                                                                    //         아이디와 비밀번호가 일치하나요?
                                                                    // -----------------------------------------------

                                                                    if((ID.getText().toString()).equals(id_temp) && (PW.getText().toString()).equals(ps_temp))
                                                                    // 회원의 아이디와 비밀번호가 같을 경우에는
                                                                    {
                                                                        if(cb.isChecked()){ // 자동로그인 체크가 되어있다.

                                                                            editor.putBoolean("Auto_Login",true); // 자동 로그인입니다.
                                                                            editor.putString("Auto_Login_ID",ID.getText().toString()); // 자동로그인한 아이디 값 저장입니당
                                                                            editor.apply(); // 저장하자!
                                                                        } else{ // 자동로그인 체크가 되어있지 않다.
                                                                            // 그냥 텅텅
                                                                        }

                                                                        Intent intent = new Intent(getApplicationContext(),MusicChoAT.class);
                                                                        intent.putExtra("ID",ID.getText().toString()); // ID값 전달하기
                                                                        startActivity(intent); // 다음 화면으로 넘어간다
                                                                        check = true; // 화면넘어간거야 !
                                                                        finish();
                                                                    }

                                                                    else{ // 아이디랑 비밀번호가 일치하지 않대
                                                                        Toast toast = Toast.makeText(LoginAT.this, "아이디와 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT);
                                                                        toast.show();
                                                                    }
                                                                }
                                                            });
                                                        } catch (Exception e) { }

                                                        break;
                                                    }
                                                }
                                            }
                                            else{ // 회원정보가 없대
                                                Toast toast = Toast.makeText(LoginAT.this, "회원정보가 없습니다.", Toast.LENGTH_SHORT);
                                                toast.show();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }
                                else
                                {

                                    // -----------------------------------------------
                                    //         아이디와 비밀번호가 일치하나요?
                                    // -----------------------------------------------

                                    if(U_CHECK1){ // 회원정보가 있대

                                        Boolean U_CHECK2 = false; // 아이디와 비밀번호 일치 체크

                                        for(int i = 0 ; i<jsonArray.length(); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            if((ID.getText().toString()).equals(jsonObject.getString("ID")) && (PW.getText().toString()).equals(jsonObject.getString("PASSWORD")) )
                                            // 회원의 아이디와 비밀번호가 같을 경우에는
                                            {
                                                U_CHECK2 = true; // 아이디와 비밀번호가 같네요!
                                                break;
                                            }
                                        }

                                        if(U_CHECK2){ // 아이디랑 비밀번호가 일치하대

                                            if(cb.isChecked()){ // 자동로그인 체크가 되어있다.

                                                editor.putBoolean("Auto_Login",true); // 자동 로그인입니다.
                                                editor.putString("Auto_Login_ID",ID.getText().toString()); // 자동로그인한 아이디 값 저장입니당
                                                editor.apply(); // 저장하자!
                                            }

                                            else{ // 자동로그인 체크가 되어있지 않다.
                                                // 그냥 텅텅
                                            }
                                            Intent intent = new Intent(getApplicationContext(),MusicChoAT.class);
                                            intent.putExtra("ID",ID.getText().toString()); // ID값 전달하기
                                            startActivity(intent); // 다음 화면으로 넘어간다
                                            check = true; // 화면넘어간거야 !
                                            finish();

                                        } else{ // 아이디랑 비밀번호가 일치하지 않대
                                            Toast toast = Toast.makeText(LoginAT.this, "아이디와 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }

                                    }

                                }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast toast = Toast.makeText(LoginAT.this, "로그인을 실패했습니다.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
            }
        });

        bon2.setOnClickListener(new View.OnClickListener() { // 회원가입
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SignAT.class);
                startActivity(intent); // 다음 화면으로 넘어간다
                check = true; // 화면넘어간거야 !
            }
        });
    }

    private void tedPermission() {
        Log.e(tag,"Permission");

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setDeniedMessage("사진 및 파일을 저장하기 위하여 접근 권한이 필요합니다.")
                .setPermissions(Manifest.permission.INTERNET,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.MODIFY_AUDIO_SETTINGS,Manifest.permission.VIBRATE)
                .check();


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
        else {
            ID.getText().clear();
            PW.getText().clear();
            cb.setChecked(false);
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
    // 영문, 숫자, 특수기호 허용
    protected InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[~!@#$%^&*a-zA-Z0-9]+$");
            new InputFilter.LengthFilter(20);
            if (!ps.matcher(source).matches()){
                return "";
            }
            return null;
        }
    };
}
