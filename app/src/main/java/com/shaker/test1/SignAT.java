package com.shaker.test1;

import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
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
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

// 회원가입 클래스 입니다.

public class SignAT extends AppCompatActivity {

    String tag = "SignAT"; // 태그

    MusicService ms; // 서비스 객체
    boolean isService = false; //서비스 여부
    boolean check; // 엑티비티가 넘어간걸 구별하기 위해서
    boolean ID_check; // 아이디 중복 체크를 했나요?
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    private DatabaseReference mDatabase;
    private FirebaseStorage storage;

    private JSONArray jsonArray; // JSON 배열
    private String JSON_string; // JSON 배열형식으로 바꾸기
    private String result; // 결과

    static SharedPreferences sharePref = null; // 쉐어드 프리페런스
    static SharedPreferences.Editor editor = null; // 저장소 조작기

    private Uri mImageCaptureUri;
    private ImageView mPhotoImageView;
    private Button mButton;

    EditText editText1; // 아이디
    EditText editText2; // 비번
    EditText editText3; // 비번 확인

    String ID_CHECK; // 중복체크하고 아이디값을 변경했는지 아닌지 여부를 확인하기 위해서

    Bitmap TEMP; // 임시 프로필 저장소

    Boolean DD_CHECK; // 중복체크
    Boolean MAKE =false; // 회원가입 만드는중

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
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.e(tag,"onCreate");
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.sign_at);

        FirebaseApp.initializeApp(SignAT.this);

        check = false; //초기화

        editText1 = (EditText)findViewById(R.id.IDText); // ID
        editText2 = (EditText)findViewById(R.id.Pw); // PW
        editText3 = (EditText)findViewById(R.id.PW_check); // PW-confirm

        editText1.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(20)}); //영문,, 숫자, 특수기호, 글자수 제한
        editText2.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(20)}); //영문,, 숫자, 특수기호, 글자수 제한
        editText3.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(20)}); //영문,, 숫자, 특수기호, 글자수 제한

        final ImageView bon = (ImageView) findViewById(R.id.bACK); // 뒤로가기
        Button bon2 = (Button) findViewById(R.id.Join); // 가입하기
        Button bon3 = (Button) findViewById(R.id.Check_B); // 중복체크하기
        mButton = (Button) findViewById(R.id.CH_Pro); // 사진 고르기 버튼
        mPhotoImageView = (ImageView) findViewById(R.id.PROFILE); // 사진

        sharePref = getSharedPreferences("DATA",MODE_PRIVATE);  // "DATA"에서 가져오기
        editor = sharePref.edit(); // "DATA" 조작기


        bindService(new Intent(SignAT.this,MusicService.class), conn, Context.BIND_AUTO_CREATE);

        bon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 뒤로 가기
//                editor.clear();
//                editor.commit();

//                Toast toast = Toast.makeText(SignAT.this, "로컬 기록 초기화", Toast.LENGTH_SHORT);
//                toast.show();

                onBackPressed(); // 뒤로 가기
                check = true; // 화면넘어간거야 !
            }
        });

        bon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 가입하기
                if(editText1.getText().toString().length() == 0 ) // 아이디 확인
                {
                    Toast toast = Toast.makeText(SignAT.this, "아이디를 적어주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else // 아이디 있넹
                {
                    if(!ID_check){ // 중복체크를 하지 않았다면
                        Toast toast = Toast.makeText(SignAT.this, "아이디 중복체크를 해주세요.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else if(!ID_CHECK.equals(editText1.getText().toString())){ // 아이디 중복체크한 후에 바뀌어있거나
                        Toast toast = Toast.makeText(SignAT.this, "아이디 중복체크를 해주세요.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else if(editText2.getText().toString().length() == 0) // 비밀번호 확인
                    {
                        Toast toast = Toast.makeText(SignAT.this, "비밀번호를 적어주세요.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else // 비밀번호도 적었넹
                    {
                        if(editText3.getText().toString().length() == 0) // 비밀번호 확인을 확인 (?)
                        {
                            Toast toast = Toast.makeText(SignAT.this, "비밀번호 확인을 적어주세요.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else // 비밀번호 확인도 적었넹
                        {
                            if(editText2.getText().toString().equals(editText3.getText().toString())) // 같은지 확인
                            {
                                MAKE = true;

                                //        로컬 저장

                                // -------------------------
                                //    회원 정보 등록하기
                                // -------------------------
                                result = "{\"ID\": \""+ editText1.getText().toString() +"\"," + "\"PASSWORD\": \""+ editText2.getText().toString()+"\"," + "\"MUSIC_V\": \""+"50"+"\","+ "\"EFFECT_V\": \""+"50"+"\"," + "\"EFFECT_N\": \""+"kick"+"\"," + "\"RECORD\": []," +
                                        "\"MUSIC\": [{\"MUSIC_ID\":\""+ R.drawable.giving_in+"\",\"TITLE\":\"Giving_in\",\"ARTIST\":\"Elementd\",\"TIME\":\"01:01\",\"FAV\":\"false\"}," +
                                        "{\"MUSIC_ID\":\""+ R.drawable.joystick+"\",\"TITLE\":\"Joystick\",\"ARTIST\":\"Jensation\",\"TIME\":\"01:15\",\"FAV\":\"false\"}," +
                                        "{\"MUSIC_ID\":\""+ R.drawable.kuyenda+"\",\"TITLE\":\"Kuyenda\",\"ARTIST\":\"Lennart_Schroot\",\"TIME\":\"01:01\",\"FAV\":\"false\"}," +
                                        "{\"MUSIC_ID\":\""+ R.drawable.light_up_the_sky+"\",\"TITLE\":\"Light_Up_The_Sky\",\"ARTIST\":\"Axtasia\",\"TIME\":\"01:09\",\"FAV\":\"false\"}," +
                                        "{\"MUSIC_ID\":\""+ R.drawable.hold_on+"\",\"TITLE\":\"Hold_on\",\"ARTIST\":\"Prismo\",\"TIME\":\"01:11\",\"FAV\":\"false\"}," +
                                        "{\"MUSIC_ID\":\""+ R.drawable.muffin+"\",\"TITLE\":\"Muffin\",\"ARTIST\":\"Raven, Kreyn\",\"TIME\":\"01:08\",\"FAV\":\"false\"}," +
                                        "{\"MUSIC_ID\":\""+ R.drawable.sicc+"\",\"TITLE\":\"SICC\",\"ARTIST\":\"Retrovision_Domastic\",\"TIME\":\"01:05\",\"FAV\":\"false\"}," +
                                        "{\"MUSIC_ID\":\""+ R.drawable.hope+"\",\"TITLE\":\"Hope\",\"ARTIST\":\"Tobu\",\"TIME\":\"01:08\",\"FAV\":\"false\"}," +
                                        "{\"MUSIC_ID\":\""+ R.drawable.candyland+"\",\"TITLE\":\"Candyland\",\"ARTIST\":\"Tobu\",\"TIME\":\"01:09\",\"FAV\":\"false\"}," +
                                        "{\"MUSIC_ID\":\""+ R.drawable.sunburst+"\",\"TITLE\":\"Sunburst\",\"ARTIST\":\"Tobu, Itro\",\"TIME\":\"01:01\",\"FAV\":\"false\"}," +
                                        "{\"MUSIC_ID\":\""+ R.drawable.delicious+"\",\"TITLE\":\"Delicious\",\"ARTIST\":\"Jensation\",\"TIME\":\"01:13\",\"FAV\":\"false\"}," +
                                        "{\"MUSIC_ID\":\""+ R.drawable.together+"\",\"TITLE\":\"Together\",\"ARTIST\":\"3rd_Prototype\",\"TIME\":\"01:01\",\"FAV\":\"false\"}," +
                                        "{\"MUSIC_ID\":\""+ R.drawable.whole+"\",\"TITLE\":\"Whole\",\"ARTIST\":\"Rob_Gasser\",\"TIME\":\"01:06\",\"FAV\":\"false\"}]}";

                                Map<String, ?> totalValue = sharePref.getAll(); // 저장소에 있는 모든 결과를 map형식으로
                                int temp = 0; // 임시 저장소, key값을 순서대로 하기 위해서!

                                for(Map.Entry<String, ?> entry : totalValue.entrySet()) {
                                    if (temp <= Integer.parseInt(entry.getKey())){
                                        temp = Integer.parseInt(entry.getKey()) +1 ; // 키에 1씩 더하기
                                    }
                                }
                                Log.e(tag,String.valueOf(temp));

                                editor.putString(String.valueOf(temp),result); // temp번호로 등록하기
                                editor.apply(); // 저장하자!

                                //     파이어베이스 저장
                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                Map<String, Object> childUpdates = new HashMap<>();
                                Map<String, Object> postValues = null;

                                FirebasePost post = new FirebasePost(editText1.getText().toString(), editText2.getText().toString(), 50, 50,"kick");
                                FirebasePost_music post_music1 = new FirebasePost_music(R.drawable.giving_in, "Giving_in", "Elementd", "01:01","false");
                                FirebasePost_music post_music2 = new FirebasePost_music(R.drawable.joystick, "Joystick", "Jensation", "01:15","false");
                                FirebasePost_music post_music3 = new FirebasePost_music(R.drawable.kuyenda, "Kuyenda", "Lennart_Schroot", "01:01","false");
                                FirebasePost_music post_music4 = new FirebasePost_music(R.drawable.light_up_the_sky, "Light_Up_The_Sky", "Axtasia", "01:09","false");
                                FirebasePost_music post_music5 = new FirebasePost_music(R.drawable.hold_on, "Hold_on", "Prismo", "01:11","false");
                                FirebasePost_music post_music6 = new FirebasePost_music(R.drawable.muffin, "Muffin", "Raven, Kreyn", "01:08","false");
                                FirebasePost_music post_music7 = new FirebasePost_music(R.drawable.sicc, "SICC", "Retrovision_Domastic", "01:05","false");
                                FirebasePost_music post_music8 = new FirebasePost_music(R.drawable.hope, "Hope", "Tobu", "01:08","false");
                                FirebasePost_music post_music9 = new FirebasePost_music(R.drawable.candyland, "Candyland", "Tobu", "01:09","false");
                                FirebasePost_music post_music10 = new FirebasePost_music(R.drawable.sunburst, "Sunburst", "Tobu, Itro", "01:01","false");
                                FirebasePost_music post_music11 = new FirebasePost_music(R.drawable.delicious, "Delicious", "Jensation", "01:13","false");
                                FirebasePost_music post_music12 = new FirebasePost_music(R.drawable.together, "Together", "3rd_Prototype", "01:01","false");
                                FirebasePost_music post_music13 = new FirebasePost_music(R.drawable.whole, "Whole", "Rob_Gasser", "01:06","false");

                                postValues = post.toMap();
                                childUpdates.put("/User/" + editText1.getText().toString(), postValues);
                                mDatabase.updateChildren(childUpdates);
                                childUpdates = new HashMap<>();

                                childUpdates.put("/User/" + editText1.getText().toString() + "/RECORD/","");
                                childUpdates.put("/User/" + editText1.getText().toString() + "/MUSIC/","");
                                mDatabase.updateChildren(childUpdates);
                                childUpdates = new HashMap<>();

                                postValues = post_music1.toMap();
                                childUpdates.put("/User/" + editText1.getText().toString() + "/MUSIC/" + R.drawable.giving_in +"/",postValues);
                                postValues = post_music2.toMap();
                                childUpdates.put("/User/" + editText1.getText().toString() + "/MUSIC/"+ R.drawable.joystick +"/",postValues);
                                postValues = post_music3.toMap();
                                childUpdates.put("/User/" + editText1.getText().toString() + "/MUSIC/"+ R.drawable.kuyenda +"/",postValues);
                                postValues = post_music4.toMap();
                                childUpdates.put("/User/" + editText1.getText().toString() + "/MUSIC/"+ R.drawable.light_up_the_sky +"/",postValues);
                                postValues = post_music5.toMap();
                                childUpdates.put("/User/" + editText1.getText().toString() + "/MUSIC/"+ R.drawable.hold_on +"/",postValues);
                                postValues = post_music6.toMap();
                                childUpdates.put("/User/" + editText1.getText().toString() + "/MUSIC/"+ R.drawable.muffin +"/",postValues);
                                postValues = post_music7.toMap();
                                childUpdates.put("/User/" + editText1.getText().toString() + "/MUSIC/"+ R.drawable.sicc +"/",postValues);
                                postValues = post_music8.toMap();
                                childUpdates.put("/User/" + editText1.getText().toString() + "/MUSIC/"+ R.drawable.hope +"/",postValues);
                                postValues = post_music9.toMap();
                                childUpdates.put("/User/" + editText1.getText().toString() + "/MUSIC/"+ R.drawable.candyland +"/",postValues);
                                postValues = post_music10.toMap();
                                childUpdates.put("/User/" + editText1.getText().toString() + "/MUSIC/"+ R.drawable.sunburst +"/",postValues);
                                postValues = post_music11.toMap();
                                childUpdates.put("/User/" + editText1.getText().toString() + "/MUSIC/"+ R.drawable.delicious +"/",postValues);
                                postValues = post_music12.toMap();
                                childUpdates.put("/User/" + editText1.getText().toString() + "/MUSIC/"+ R.drawable.together +"/",postValues);
                                postValues = post_music13.toMap();
                                childUpdates.put("/User/" + editText1.getText().toString() + "/MUSIC/"+ R.drawable.whole +"/",postValues);

                                mDatabase.updateChildren(childUpdates);

                                // ---------------------------
                                //    프로필 이미지 저장하기
                                // ---------------------------

                                storage = FirebaseStorage.getInstance();

                                //storage 주소와 폴더 파일명을 지정해 준다.
                                StorageReference storageRef = storage.getReferenceFromUrl("gs://shaker-45d31.appspot.com").child("profile/" + editText1.getText().toString());
                                File fileCacheItem = new File( Environment.getExternalStorageDirectory().getAbsolutePath()+ "/SHAKER" + "/Profile" + "/" + editText1.getText().toString());
                                OutputStream out;

                                if(TEMP != null){ // 프로필 설정한 경우에
                                    try {

                                        fileCacheItem.createNewFile();
                                        out = new FileOutputStream(fileCacheItem);
                                        TEMP.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                        out.close();

                                        // 업로드
                                        storageRef.putFile(Uri.fromFile(fileCacheItem));

                                    } catch (Exception e) { e.printStackTrace(); }
                                }
                                else{ // 설정하지 않는 경우에
                                    try {

                                        fileCacheItem.createNewFile();
                                        out = new FileOutputStream(fileCacheItem);
                                        TEMP = ((BitmapDrawable)getResources().getDrawable(R.drawable.profile)).getBitmap(); // 기본 프로필로
                                        TEMP.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                        out.close();

                                        // 업로드
                                        storageRef.putFile(Uri.fromFile(fileCacheItem));

                                    } catch (Exception e) { e.printStackTrace(); }
                                }

                                //-----------------------------

                                Toast.makeText(SignAT.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                                finish();
                                check = true; // 화면넘어간거야 !
                            }
                            else
                            {
                                Toast toast = Toast.makeText(SignAT.this, "비밀번호가 같지 않습니다.", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }
                }
            }
        });

        bon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 아이디 중복 체크

                if (editText1.getText().toString().length() == 0) // 아이디 확인
                {
                    Toast toast = Toast.makeText(SignAT.this, "아이디를 적어주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else // 아이디가 있다면 아이디 중복체크를 해보자!
                {

                    //       파이어베이스로 검색

                    // --------------------------------
                    //         아이디 중복 체크
                    // --------------------------------

                    DD_CHECK = false; // 일치하냐 안하냐
                    mDatabase = FirebaseDatabase.getInstance().getReference("User");

                    // Read from the database
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(!MAKE){
                                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                                    if((editText1.getText().toString()).equals(fileSnapshot.getKey())){

                                        Toast.makeText(SignAT.this, "일치하는 아이디가 있습니다.", Toast.LENGTH_SHORT).show(); // 일치하는 아이디가 있다고 합니다.
                                        ID_check = false; // 아이디 체크 안됨.
                                        DD_CHECK = true; // 일치합니다.
                                        break;
                                    }
                                }

                                if(!DD_CHECK){ // 일치 안합니다.

                                    Toast.makeText(SignAT.this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                                    ID_check = true; // 아이디 체크 완료
                                    ID_CHECK = editText1.getText().toString(); // 추후 바뀔수도 있으니까
                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });


                    // 로컬로 회원 정보 찾는 방법

//                    // --------------------------------
//                    //     회원 정보를 JSON으로 추출
//                    // --------------------------------
//
//                    boolean frist = true; // 처음
//
//                    JSON_string =""; // 저장 공간 비우기
//                    Map<String, ?> totalValue = sharePref.getAll();// 저장소에 있는 정보를 다 넣기
//                    for(Map.Entry<String, ?> entry : totalValue.entrySet()){
//                        Log.e("share : ",entry.getKey() + ": "+ entry.getValue());
//                        if( !entry.getKey().equals("Auto_Login") && !entry.getKey().equals("Auto_Login_ID")) { // 자동로그인 키와 아이디 값 제외
//
//                            if (frist) {
//                                // 첫번째 키를 제외하고
//                                frist = false;
//                            } else // 그외에는 ,를 붙이기
//                            {
//                                JSON_string += ",";
//                            }
//                            JSON_string += entry.getValue();
//                        }
//                    }
//                    JSON_string = "[" + JSON_string + "]"; // 배열로 바꾸기
//
//                    // --------------------------------
//                    //         아이디 중복 체크
//                    // --------------------------------
//
//                    Boolean DD_CHECK = false; // 일치하냐 안하냐
//                    try {
//                        jsonArray = new JSONArray(JSON_string);
//                        for(int i = 0 ; i<jsonArray.length(); i++) {
//                            JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                            if((editText1.getText().toString()).equals(jsonObject.getString("ID"))) // 입력한 아이디와 기존 아이디가 같을 경우에는
//                            {
//                                Toast toast = Toast.makeText(SignAT.this, "일치하는 아이디가 있습니다.", Toast.LENGTH_SHORT); // 일치하는 아이디가 있다고 합니다.
//                                toast.show();
//                                DD_CHECK = true; // 일치합니다.
//                                break;
//                            }
//
//                        }
//
//                        if(!DD_CHECK){ // 일치 안합니다.
//
//                            Toast toast = Toast.makeText(SignAT.this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT);
//                            toast.show();
//                            ID_check = true; // 아이디 체크 완료
//                            ID_CHECK = editText1.getText().toString(); // 추후 바뀔수도 있으니까
//                        }
//                        else{ // 일치합니다.
//
//                            ID_check = false; // 아이디 체크 안됨.
//                        }
//
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Toast toast = Toast.makeText(SignAT.this, "아이디 중복체크를 실패했습니다.", Toast.LENGTH_SHORT);
//                        toast.show();
//                    }
                }
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 사진
                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() //사진촬영
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        doTakePhotoAction();
                    }
                };

                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() // 앨범선택
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        doTakeAlbumAction();
                    }
                };

                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() //취소
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                };
                // 기본 틀
                new AlertDialog.Builder(SignAT.this)
                        .setTitle("업로드할 이미지 선택")
                        .setPositiveButton("사진촬영", cameraListener)
                        .setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();
            }
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
        startActivityForResult(intent,PICK_FROM_CAMERA);
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
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(tag,"onActivityResult");
        if(resultCode != RESULT_OK)
        {
            return;
        }
        switch(requestCode)
        {
            case PICK_FROM_ALBUM:
            {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                mImageCaptureUri = data.getData();
            }

            case PICK_FROM_CAMERA:
            {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.
                Intent intent = new Intent("com.android.camera.action.CROP");

                intent.setDataAndType(mImageCaptureUri, "image/*");// crop한 이미지를 저장할때 130x130 크기로 저장
                intent.putExtra("outputX", 130); // crop한 이미지의 x축 크기
                intent.putExtra("outputY", 130); // crop한 이미지의 y축 크기
                intent.putExtra("aspectX", 1); // crop 박스의 x축 비율
                intent.putExtra("aspectY", 1); // crop 박스의 y축 비율
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);

                startActivityForResult(intent, CROP_FROM_CAMERA);

                break;
            }

            case CROP_FROM_CAMERA:
            {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.
                final Bundle extras = data.getExtras();

                if(extras != null)
                {
                    Bitmap photo = extras.getParcelable("data");
                    TEMP = photo; // 임시 저장

                    //이미지 출력해서 보여주기
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

    // 영문, 숫자, 특수기호 허용
    protected InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {


            Pattern ps = Pattern.compile("^[~!@#$%^&*a-zA-Z0-9]+$");
            if (!ps.matcher(source).matches()){
                return "";
            }
            return null;
        }
    };

//    public void postFirebaseDatabase(boolean add){
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        Map<String, Object> childUpdates = new HashMap<>();
//        Map<String, Object> postValues = null;
//        if(add){
//            FirebasePost post = new FirebasePost(ID, name, age, gender);
//            postValues = post.toMap();
//        }
//        childUpdates.put("/User/" + ID, postValues);
//        mDatabase.updateChildren(childUpdates);
//    }
}