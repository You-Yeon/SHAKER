package com.shaker.test1;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

// 노래를 선택하는 화면 클래스입니다.

public class MusicChoAT extends AppCompatActivity {

//    static private Visualizer audioOutput = null;
//    static public float intensity = 0; //intensity is a value between 0 and 1. The intensity in this case is the system output volume
//    static ProgressBar mu_pb;

    String tag = "MusicCHoAT"; // 태그
    int cnt = 0; //몇번 눌렀는지 체크하기.

    static CardView v;

    boolean check; // 엑티비티가 넘어간걸 구별하기 위해서
    static EditText ser; // 검색하는 창
    Button ALL_bon; // 전체 노래 버튼
    Button FAV_bon; // 즐겨찾기 노래 버튼
//    Button GET_bon; // 음악 가져오는 버튼

    private static JSONArray jsonArray; // JSON 배열
    private static String JSON_string; // JSON 배열형식으로 바꾸기

    static SharedPreferences sharePref = null; // 쉐어드 프리페런스
    static SharedPreferences.Editor editor = null; // 저장소 조작기

    static ArrayList<Difficulty_info> Music_DCArrayList;

    static boolean ALLFAV =false; // false 일때는 ALL, true 일때는 FAV
    static ArrayList<Music_info> MusicinfoArrayList;
    static ArrayList<Music_info> temp; // 전달 수단

    static MusicService ms; // 서비스 객체
    boolean isService = false; //서비스 여부
    static Adapter Adapter; //Adapter!

    static TextView ID_text; // id 값
    static String id_text; // id 값
    static ImageView profile; // 프로필

    static RecyclerView mRecyclerView; // 리사이클러뷰
    RecyclerView.LayoutManager mLayoutManager; // 모양 잡기

    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            // 서비스와 연결되었을 때 호출되는 메서드
            // 서비스 객체를 전역변수로 저장
            Log.e(tag,"onService");
            MusicService.MyBinder mb = (MusicService.MyBinder) service;
            ms = mb.getService(); // 서비스가 제공하는 메소드 호출하여
            // 서비스쪽 객체를 전달받을수 있슴

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
                    if (id_text.equals(jsonObject.getString("ID"))) // 아이디 일치시에
                    {
                        ms.Music_Volume(Integer.parseInt(jsonObject.getString("MUSIC_V"))); // 볼륨 설정
                        break;
                    }
                }
            } catch (JSONException e) { e.printStackTrace(); }

            //---------------------------------------------------------------------------------

            isService = true;
//            createVisualizer();
        }
        public void onServiceDisconnected(ComponentName name) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
            Log.e(tag,"offService");
            isService = false;
        }
    };

    public static Activity MusicChoAT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.e(tag, "onCreate");
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sharePref = getSharedPreferences("DATA",MODE_PRIVATE);  // "DATA"에서 가져오기
        editor = sharePref.edit(); // "DATA" 조작기

        bindService(new Intent(MusicChoAT.this, MusicService.class), conn, Context.BIND_AUTO_CREATE);
        setContentView(R.layout.music_choice_at);

//        mu_pb = (ProgressBar)findViewById(R.id.mu_bar);
        ALL_bon = (Button) findViewById(R.id.all); // 전체 노래 버튼
        FAV_bon = (Button) findViewById(R.id.fav); // 즐겨찾기 노래 버튼
//        GET_bon = (Button) findViewById(R.id.get_music); // 음악 가져오는 버튼
        profile = (ImageView) findViewById(R.id.profile); // 설정
        ImageView bon = (ImageView) findViewById(R.id.setting); // 설정
        ser = (EditText) findViewById(R.id.Searching); // 검색 창

        mRecyclerView = findViewById(R.id.recycler_view); // 리사이클러뷰
        mRecyclerView.setHasFixedSize(true); // 사이즈 안바꾸니까 true
        mLayoutManager = new LinearLayoutManager(this); // 리니어 아래로
        mRecyclerView.setLayoutManager(mLayoutManager); // 만들기

        MusicinfoArrayList = new ArrayList<>(); // adapter에 들어갈 list
        temp = new ArrayList<>(); // 전달 list

        ID_text =(TextView)findViewById(R.id.user_id); // 아이디 값
        Intent intent = getIntent(); // LoginAT이나 Main에서 받아오기

        // 전달 받은 값 넣어주깅
        id_text = intent.getExtras().getString("ID");
        ID_text.setText("ID : "+ id_text);
        ID_text.setTextSize(18);

        // 음악 아이템 추가하기
        Alladd_Music();

        Adapter = new Adapter(MusicChoAT.this, MusicinfoArrayList, check);
        mRecyclerView.setAdapter(Adapter); // mRecyclerView 객체에 myAdapter 객체를 연결합니다.

        MusicChoAT = MusicChoAT.this; // set에서 finish를 할 수 있도록

        // 프로필 설정하기
        Update_profile(); // 업데이트

        Music_DCArrayList = new ArrayList<Difficulty_info>();

        bon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SetAT.class);
                intent.putExtra("ID",id_text); // 아이디 값 넘겨주기
                startActivity(intent); // 다음 화면으로 넘어간다

                check = true; // 화면넘어간거야 !
            }
        });

        // 눌리지 않는 상태는 off 눌린 상태는 on
        // off일 때 FFFFEDD2  (color ID) 4654
        // on일 때 FFFFDAAA (color ID) 9558

        ALL_bon.setOnClickListener(new View.OnClickListener() { // 전체 노래 버튼
            @Override
            public void onClick(View view) {

                ALLFAV = false ; // ALL에 있어요

                ser.setText("");
                ALL_bon.setBackgroundColor(0xFFFFDAAA); // ALL 버튼은 on으로 상태 변환
                FAV_bon.setBackgroundColor(0xFFFFEDD2); // FAV 버튼은 off로 상태 변환

                // 전체 노래 목록 가져오기

                Adapter.ALL_ITEM();
                Adapter.notifyDataSetChanged();


            }
        });

        FAV_bon.setOnClickListener(new View.OnClickListener() { // 즐겨찾기 버튼
            @Override
            public void onClick(View view) {

                ALLFAV = true ; // FAV에 있어요

                ser.setText("");
                ALL_bon.setBackgroundColor(0xFFFFEDD2); // ALL 버튼은 off으로 상태 변환
                FAV_bon.setBackgroundColor(0xFFFFDAAA); // FAV 버튼은 on로 상태 변환

                // 즐겨찾기 목록 가져오기

                Adapter.FAC_ITEM();
                Adapter.notifyDataSetChanged();


            }
        });

//        GET_bon.setOnClickListener(new View.OnClickListener() { // 음악 가져오기 버튼
//            @Override
//            public void onClick(View view) { // 음악 가져오기
//
//                Intent intent_upload = new Intent();
//                intent_upload.setType("audio/*");
//                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent_upload,1);
//
//            }
//        });

        ser.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

                ColorDrawable ALL_buttonColor = (ColorDrawable) ALL_bon.getBackground();
                int ALL_colorId = ALL_buttonColor.getColor(); // 배경 컬러 아이디 값 가져오기

                ColorDrawable FAV_buttonColor = (ColorDrawable) FAV_bon.getBackground();
                int FAV_colorId = FAV_buttonColor.getColor(); // 배경 컬러 아이디 값 가져오기

                if(ALL_colorId == -9558) // 전체일 때
                {
                    String text = ser.getText().toString().toLowerCase(Locale.getDefault());
                    Adapter.all_filter(text);
                }

                if(FAV_colorId == -9558) // 즐겨찾기일 때
                {
                    String text = ser.getText().toString().toLowerCase(Locale.getDefault());
                    Adapter.fav_filter(text);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

    }

    public static void Alladd_Music(){

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
                    JSONArray Record = jsonObject1.getJSONArray("MUSIC");
                    for (int j = 0; j < Record.length(); j++) { // 기록 중에서
                        JSONObject jsonObject2 = Record.getJSONObject(j);
                        MusicinfoArrayList.add(new Music_info(Integer.parseInt(jsonObject2.getString("MUSIC_ID")), jsonObject2.getString("TITLE"), jsonObject2.getString("ARTIST"), jsonObject2.getString("TIME"), Boolean.parseBoolean(jsonObject2.getString("FAV"))));
                    }
                    break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    static void change_color(int position){
//        v = (CardView)mRecyclerView.getLayoutManager().findViewByPosition(position);
//        v.setCardBackgroundColor(Color.parseColor("#AA9CB3FF")); // 파란색
//    }

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
            //     기록 이동하기
            // ----------------------

            jsonArray = new JSONArray(JSON_string);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                if (id_text.equals(jsonObject1.getString("ID"))) { // 아이디에서 검색
                    // 기록 배열 가져오기
                    JSONArray Record = jsonObject1.getJSONArray("MUSIC");
                    for (int j = 0; j < Record.length(); j++) { // 기록 중에서
                        JSONObject jsonObject2 = Record.getJSONObject(j);
                        FirebaseDatabase.getInstance().getReference("User").child(id_text).child("MUSIC").child(String.valueOf(jsonObject2.getString("MUSIC_ID"))).child("TITLE").setValue(jsonObject2.getString("TITLE"));
                        FirebaseDatabase.getInstance().getReference("User").child(id_text).child("MUSIC").child(String.valueOf(jsonObject2.getString("MUSIC_ID"))).child("ARTIST").setValue(jsonObject2.getString("ARTIST"));
                        FirebaseDatabase.getInstance().getReference("User").child(id_text).child("MUSIC").child(String.valueOf(jsonObject2.getString("MUSIC_ID"))).child("TIME").setValue(jsonObject2.getString("TIME"));
                        FirebaseDatabase.getInstance().getReference("User").child(id_text).child("MUSIC").child(String.valueOf(jsonObject2.getString("MUSIC_ID"))).child("FAV").setValue(jsonObject2.getString("FAV"));
                    }
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
        if(!check && !Adapter.return_check()) {
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
        cnt = 0; // 초기화
        Adapter.get_check(check);
        Adapter.get_cnt(cnt);
        Log.e(tag,"onRestart");
        ms.Music_on();

    }

    public static void Update_profile(){
        Bitmap myBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/SHAKER" + "/Profile" + "/" +id_text);
        profile.setImageBitmap(myBitmap);
    }

//    static void createVisualizer(){
//
//        int rate = Visualizer.getMaxCaptureRate();
//
//        if(audioOutput != null){
//            audioOutput.release();
//            audioOutput = null;
//        }
//
//        mu_pb.setMax(45000);
//
//        audioOutput = new Visualizer(ms.mp.getAudioSessionId()); // get output audio stream
//        audioOutput.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
//            @Override
//            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
//                intensity = ((float) waveform[0] + 128f) / 256;
//
//                Log.d("ddddd",String.valueOf(100000f*intensity));
//
//                if(30000 > Integer.parseInt(String.format("%.0f",100000f*intensity)) && 0 < Integer.parseInt(String.format("%.0f",100000f*intensity)))
//                {
//                    mu_pb.setProgress(Integer.parseInt(String.format("%.0f",100000f*intensity)));
//                    Log.e("ddddd",String.valueOf(Integer.parseInt(String.format("%.0f",100000f*intensity))));
//                }
//            }
//
//            @Override
//            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
//
//            }
//
//        },rate , true, false); // waveform not freq data
//
//        audioOutput.setEnabled(true);
//    }


//    @Override
//    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
//
//        if (requestCode == 1) { // 음악 데이터 끌어오기
//            if (resultCode == RESULT_OK) {
//                //the selected audio.
//                Uri uri = data.getData();
//
//                String[] projection = {MediaStore.Audio.Media._ID,
//                        MediaStore.Audio.Media._ID,
//                        MediaStore.Audio.Media.ALBUM_ID,
//                        MediaStore.Audio.Media.TITLE,
//                        MediaStore.Audio.Media.ALBUM,
//                        MediaStore.Audio.Media.ARTIST,
//                        MediaStore.Audio.Media.DATA
//                };
//
//                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//
//                while (cursor.moveToNext()) {
//
//                    Log.e(tag, "path : " + cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
//                    Log.e(tag, "id : " + cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
//                    Log.e(tag, "album : " + cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
//                    Log.e(tag, "album_id : " + cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
//                    Log.e(tag, "title : " + cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
//                    Log.e(tag, "artist : " + cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
//
//                    Bitmap albumImage = getAlbumImage(this, Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))), 170);
//                    File fileCacheItem = new File( Environment.getExternalStorageDirectory().getAbsolutePath()+ "/SHAKER" + "/Music/" + cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
//                    OutputStream out;
//
//                    try {
//                        fileCacheItem.createNewFile();
//                        out = new FileOutputStream(fileCacheItem);
//                        if(albumImage == null){
//                            albumImage = ((BitmapDrawable)getResources().getDrawable(R.drawable.music)).getBitmap(); // 기본 프로필로
//                            albumImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                        }
//                        else
//                        {
//                            albumImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                        }
//                        out.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    MusicinfoArrayList.add(new Music_info(Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))), cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)), cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)), "", false));
//                    Adapter.arrayList.add(new Music_info(Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))), cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)), cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)), "", false));
//                    Music_DCArrayList.add(new Difficulty_info(Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))),  R.drawable.star3, ""));
//                    Adapter.notifyDataSetChanged();
//
//                }
//                cursor.close();
//            }
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//
//    }
//
//    private static final BitmapFactory.Options options = new BitmapFactory.Options();
//
//    private static Bitmap getAlbumImage(Context context, int album_id, int MAX_IMAGE_SIZE) {
//        // NOTE: There is in fact a 1 pixel frame in the ImageView used to
//        // display this drawable. Take it into account now, so we don't have to
//        // scale later.
//        ContentResolver res = context.getContentResolver();
//        Uri uri = Uri.parse("content://media/external/audio/albumart/" + album_id);
//        if (uri != null) {
//            ParcelFileDescriptor fd = null;
//            try {
//                fd = res.openFileDescriptor(uri, "r");
//
//
//                // Compute the closest power-of-two scale factor
//                // and pass that to sBitmapOptionsCache.inSampleSize, which will
//                // result in faster decoding and better quality
//
//                //크기를 얻어오기 위한옵션 ,
//                //inJustDecodeBounds값이 true로 설정되면 decoder가 bitmap object에 대해 메모리를 할당하지 않고, 따라서 bitmap을 반환하지도 않는다.
//                // 다만 options fields는 값이 채워지기 때문에 Load 하려는 이미지의 크기를 포함한 정보들을 얻어올 수 있다.
//
//                options.inJustDecodeBounds = true;
//                BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);
//                int scale = 0;
//                if (options.outHeight > MAX_IMAGE_SIZE || options.outWidth > MAX_IMAGE_SIZE) {
//                    scale = (int) Math.pow(2, (int) Math.round(Math.log(MAX_IMAGE_SIZE / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
//                }
//                options.inJustDecodeBounds = false;
//                options.inSampleSize = scale;
//
//
//                Bitmap b = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);
//
//                if (b != null) {
//                    // finally rescale to exactly the size we need
//                    if (options.outWidth != MAX_IMAGE_SIZE || options.outHeight != MAX_IMAGE_SIZE) {
//                        Bitmap tmp = Bitmap.createScaledBitmap(b, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE, true);
//                        b.recycle();
//                        b = tmp;
//                    }
//                }
//
//                return b;
//
//            } catch (FileNotFoundException e) {
//            } finally {
//                try {
//                    if (fd != null)
//                        fd.close();
//                } catch (IOException e) { }
//            }
//        }
//        return null;
//    }

}
