package com.shaker.test1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

// 뮤직 어댑터 클래스입니다.

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    static ArrayList<Music_info> arrayList; // 전체일 때
    private static ArrayList<Music_info> arrayList_fav; // 즐겨찾기일 때
    int cnt = 0; // 몇번 눌렀는지 체크합니다.
    int position_check; // 방금 누른 포지션인지 체크합니다.
    int position;
    private Context mContext;

    //view 들을 setting 해줍니다.
    // ViwHolder 입니당.
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {


        // 뷰에 차곡차곡 넣어주는 작업
        ImageView it_picture; // 노래 사진
        TextView it_tite; // 제목
        TextView it_artist; // 아티스트
        TextView it_time; // 시간
        CheckBox it_star_box; // 별 상자

        MyViewHolder(View view){
            super(view);
            it_picture = view.findViewById(R.id.it_picture);
            it_tite = view.findViewById(R.id.it_tite);
            it_artist = view.findViewById(R.id.it_artist);
            it_time = view.findViewById(R.id.it_time);
            it_star_box = view.findViewById(R.id.it_star_box);

            view.setOnCreateContextMenuListener(this); //2. OnCreateContextMenuListener 리스너를 현재 클래스에서 구현한다고 설정해둡니다.
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {  // 컨텍스트 메뉴를 생성하고 메뉴 항목 선택시 호출되는 리스너를 등록해줍니다. ID 1001, 1002로 어떤 메뉴를 선택했는지 리스너에서 구분하게 됩니다.

                MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "수정하기");

                if(MusicChoAT.ALLFAV) // FAV에 있을 경우
                {
                    MenuItem Delete = menu.add(Menu.NONE, 1003, 3, "즐겨찾기에 삭제하기");
                    Delete.setOnMenuItemClickListener(onEditMenu);
                }
                else // ALL에 있을 경우
                {
                    if(MusicinfoArrayList.get(getAdapterPosition()).star_box) { // 즐겨찾기가 되어있을 때
                        MenuItem Delete = menu.add(Menu.NONE, 1003, 3, "즐겨찾기에 삭제하기");
                        Delete.setOnMenuItemClickListener(onEditMenu);
                    }
                    else { // 즐겨찾기가 안되어있을 때
                        MenuItem Add = menu.add(Menu.NONE, 1002, 2, "즐겨찾기에 추가하기");
                        Add.setOnMenuItemClickListener(onEditMenu);
                    }
                }
                Edit.setOnMenuItemClickListener(onEditMenu);
        }

        // 컨텍스트 메뉴에서 항목 클릭시 동작을 설정합니다.
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1001:  // 편집 항목을 선택시

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        // 다이얼로그를 보여주기 위해 edit_box.xml 파일을 사용합니다.

                        View view = LayoutInflater.from(mContext).inflate(R.layout.edit_box, null, false);
                        builder.setView(view);

                        final Button ButtonSubmit = (Button) view.findViewById(R.id.button_dialog_submit);
                        final EditText editTextTitle = (EditText) view.findViewById(R.id.edittext_dialog_title);
                        final EditText editTextArtist = (EditText) view.findViewById(R.id.edittext_dialog_artist);


                        final AlertDialog dialog = builder.create();
                        ButtonSubmit.setOnClickListener(new View.OnClickListener() {

                            // 수정 버튼을 클릭하면 현재 UI에 입력되어 있는 내용으로
                            public void onClick(View v) {
                                String strTitle = editTextTitle.getText().toString();
                                String strArtist = editTextArtist.getText().toString();
                                JSONArray jsonArray; // JSON 배열
                                String JSON_string; // JSON 배열형식으로 바꾸기
                                int draw_key = MusicinfoArrayList.get(getAdapterPosition()).drawableId; // 노래 키 값

                                // ListArray에 있는 데이터를 변경하고
                                if(MusicChoAT.ALLFAV) // FAV에 있을 경우
                                {
                                    MusicinfoArrayList.set(getAdapterPosition(), new Music_info(MusicinfoArrayList.get(getAdapterPosition()).drawableId, strTitle, strArtist, MusicinfoArrayList.get(getAdapterPosition()).time, MusicinfoArrayList.get(getAdapterPosition()).star_box));

                                    for(int i =0; i < arrayList_fav.size(); i++) {
                                        if(MusicinfoArrayList.get(getAdapterPosition()).drawableId == arrayList_fav.get(i).drawableId) // 기존게 저장되어있는 곳도 수정하기
                                        {
                                            arrayList_fav.set(i, new Music_info(MusicinfoArrayList.get(getAdapterPosition()).drawableId, strTitle, strArtist, MusicinfoArrayList.get(getAdapterPosition()).time, MusicinfoArrayList.get(getAdapterPosition()).star_box));
                                        }
                                    }

                                    for(int i =0; i < arrayList.size(); i++) {
                                        if(MusicinfoArrayList.get(getAdapterPosition()).drawableId == arrayList.get(i).drawableId) // 기존게 저장되어있는 곳도 수정하기
                                        {
                                            arrayList.set(i, new Music_info(MusicinfoArrayList.get(getAdapterPosition()).drawableId, strTitle, strArtist, MusicinfoArrayList.get(getAdapterPosition()).time, MusicinfoArrayList.get(getAdapterPosition()).star_box));
                                        }
                                    }
                                    String text = MusicChoAT.ser.getText().toString().toLowerCase(Locale.getDefault());
                                    fav_filter(text);
                                }
                                else // ALL에 있을 경우
                                {
                                    MusicinfoArrayList.set(getAdapterPosition(), new Music_info(MusicinfoArrayList.get(getAdapterPosition()).drawableId, strTitle, strArtist, MusicinfoArrayList.get(getAdapterPosition()).time, MusicinfoArrayList.get(getAdapterPosition()).star_box));
                                    for(int i =0; i < arrayList.size(); i++) {
                                        if(MusicinfoArrayList.get(getAdapterPosition()).drawableId == arrayList.get(i).drawableId) // 기존게 저장되어있는 곳도 수정하기
                                        {
                                            arrayList.set(i, new Music_info(MusicinfoArrayList.get(getAdapterPosition()).drawableId, strTitle, strArtist, MusicinfoArrayList.get(getAdapterPosition()).time, MusicinfoArrayList.get(getAdapterPosition()).star_box));
                                        }
                                    }
                                    String text = MusicChoAT.ser.getText().toString().toLowerCase(Locale.getDefault());
                                    all_filter(text);
                                }

                                // --------------------------------
                                //     회원 정보를 JSON으로 추출
                                // --------------------------------

                                boolean frist = true; // 처음
                                boolean frist1 = true; // 처음
                                boolean frist2 = true; // 처음

                                JSON_string = ""; // 저장 공간 비우기

                                Map<String, ?> totalValue = MusicChoAT.sharePref.getAll();// 저장소에 있는 정보를 다 넣기
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

                                    // ------------------------
                                    //     노래 저장하기
                                    // ------------------------

                                    jsonArray = new JSONArray(JSON_string);
                                    String music = ""; // 노래 저장하는 곳

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        if(MusicChoAT.id_text.equals(jsonObject1.getString("ID"))){
                                            // 노래 배열 가져오기
                                            JSONArray Music = jsonObject1.getJSONArray("MUSIC");
                                            if (frist1) {
                                                // 첫번째 키를 제외하고
                                                frist1 = false;
                                            } else // 그외에는 ,를 붙이기
                                            {
                                                music += ",";
                                            }
                                            for(int j = 0; j< Music.length(); j++){
                                                JSONObject jsonObject2= Music.getJSONObject(j);
                                                if(Integer.parseInt(jsonObject2.getString("MUSIC_ID")) != draw_key)
                                                {
                                                    if (frist2) {
                                                        // 첫번째 키를 제외하고
                                                        frist2 = false;
                                                    } else // 그외에는 ,를 붙이기
                                                    {
                                                        music += ",";
                                                    }
                                                    music += jsonObject2; // 기존 기록들 저장하기
                                                }
                                                if(Integer.parseInt(jsonObject2.getString("MUSIC_ID")) == draw_key)
                                                {
                                                    if (frist2) {
                                                        // 첫번째 키를 제외하고
                                                        frist2 = false;
                                                    } else // 그외에는 ,를 붙이기
                                                    {
                                                        music += ",";
                                                    }
                                                    // 기존 값 저장
                                                    music += "{\"MUSIC_ID\":\""+jsonObject2.getString("MUSIC_ID") +"\"," + "\"TITLE\":\""+strTitle+"\"," + "\"ARTIST\":\""+strArtist+"\"," + "\"TIME\":\""+jsonObject2.getString("TIME")+"\"," + "\"FAV\":\""+ jsonObject2.getString("FAV")+"\"}";
                                                }
                                            }
                                            break;
                                        }
                                    }

                                    // ------------------------
                                    //     노래 위치 검색
                                    // ------------------------

                                    int position = 0; // 포지션값 가져오기
                                    int cnt = 0; // 카운트 하기
                                    String result = ""; // 결과
                                    String KEY = ""; // 키 값

                                    // 해당 아이디가 있는 위치 검색
                                    jsonArray = new JSONArray(JSON_string);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        if(MusicChoAT.id_text.equals(jsonObject.getString("ID"))){
                                            position = i; // 포지션값 저장하기
                                            result = "{\"ID\": \""+ jsonObject.getString("ID") +"\"," + "\"PASSWORD\": \""+jsonObject.getString("PASSWORD")+"\","+ "\"MUSIC_V\": \""+jsonObject.getString("MUSIC_V")+"\"," + "\"EFFECT_V\": \""+jsonObject.getString("EFFECT_V")+"\"," + "\"EFFECT_N\": \""+jsonObject.getString("EFFECT_N")+"\","  + "\"RECORD\": "+jsonObject.getString("RECORD") + ",\"MUSIC\": "+"["+music+"]}"; // 최종 결과 값 가져오기
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
                                    //     노래 갱신하기
                                    // ---------------------

                                    MusicChoAT.editor.putString(KEY,result); // Update
                                    MusicChoAT.editor.apply(); // 저장소 갱신하기

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                // 어댑터에서 RecyclerView에 반영하도록 합니다.
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;

                    case 1002:  // 즐겨찾기 추가

                        JSONArray jsonArray; // JSON 배열
                        String JSON_string; // JSON 배열형식으로 바꾸기
                        int draw_key = MusicinfoArrayList.get(getAdapterPosition()).drawableId; // 노래 키 값

                        MusicinfoArrayList.set(getAdapterPosition(), new Music_info(MusicinfoArrayList.get(getAdapterPosition()).drawableId, MusicinfoArrayList.get(getAdapterPosition()).title, MusicinfoArrayList.get(getAdapterPosition()).artist, MusicinfoArrayList.get(getAdapterPosition()).time, true));
                        for(int i =0; i < arrayList.size(); i++) {
                            if(MusicinfoArrayList.get(getAdapterPosition()).drawableId == arrayList.get(i).drawableId) // 기존게 저장되어있는 곳도 수정하기
                            {
                                arrayList.set(i, new Music_info(MusicinfoArrayList.get(getAdapterPosition()).drawableId, MusicinfoArrayList.get(getAdapterPosition()).title, MusicinfoArrayList.get(getAdapterPosition()).artist, MusicinfoArrayList.get(getAdapterPosition()).time, true));
                            }
                        }
                        // --------------------------------
                        //     회원 정보를 JSON으로 추출
                        // --------------------------------

                        boolean frist = true; // 처음
                        boolean frist1 = true; // 처음
                        boolean frist2 = true; // 처음

                        JSON_string = ""; // 저장 공간 비우기

                        Map<String, ?> totalValue = MusicChoAT.sharePref.getAll();// 저장소에 있는 정보를 다 넣기
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

                            // ------------------------
                            //     노래 저장하기
                            // ------------------------

                            jsonArray = new JSONArray(JSON_string);
                            String music = ""; // 노래 저장하는 곳

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                if(MusicChoAT.id_text.equals(jsonObject1.getString("ID"))){
                                    // 노래 배열 가져오기
                                    JSONArray Music = jsonObject1.getJSONArray("MUSIC");
                                    if (frist1) {
                                        // 첫번째 키를 제외하고
                                        frist1 = false;
                                    } else // 그외에는 ,를 붙이기
                                    {
                                        music += ",";
                                    }
                                    for(int j = 0; j< Music.length(); j++){
                                        JSONObject jsonObject2= Music.getJSONObject(j);
                                        if(Integer.parseInt(jsonObject2.getString("MUSIC_ID")) != draw_key)
                                        {
                                            if (frist2) {
                                                // 첫번째 키를 제외하고
                                                frist2 = false;
                                            } else // 그외에는 ,를 붙이기
                                            {
                                                music += ",";
                                            }
                                            music += jsonObject2; // 기존 기록들 저장하기
                                        }
                                        if(Integer.parseInt(jsonObject2.getString("MUSIC_ID")) == draw_key)
                                        {
                                            if (frist2) {
                                                // 첫번째 키를 제외하고
                                                frist2 = false;
                                            } else // 그외에는 ,를 붙이기
                                            {
                                                music += ",";
                                            }
                                            // 기존 값 저장
                                            music += "{\"MUSIC_ID\":\""+jsonObject2.getString("MUSIC_ID") +"\"," + "\"TITLE\":\""+jsonObject2.getString("TITLE")+"\"," + "\"ARTIST\":\""+jsonObject2.getString("ARTIST")+"\"," + "\"TIME\":\""+jsonObject2.getString("TIME")+"\"," + "\"FAV\":\""+ "true"+"\"}";
                                        }
                                    }
                                    break;
                                }
                            }

                            // ------------------------
                            //     노래 위치 검색
                            // ------------------------

                            int position = 0; // 포지션값 가져오기
                            int cnt = 0; // 카운트 하기
                            String result = ""; // 결과
                            String KEY = ""; // 키 값

                            // 해당 아이디가 있는 위치 검색
                            jsonArray = new JSONArray(JSON_string);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if(MusicChoAT.id_text.equals(jsonObject.getString("ID"))){
                                    position = i; // 포지션값 저장하기
                                    result = "{\"ID\": \""+ jsonObject.getString("ID") +"\"," + "\"PASSWORD\": \""+jsonObject.getString("PASSWORD")+"\","+ "\"MUSIC_V\": \""+jsonObject.getString("MUSIC_V")+"\"," + "\"EFFECT_V\": \""+jsonObject.getString("EFFECT_V")+"\"," + "\"EFFECT_N\": \""+jsonObject.getString("EFFECT_N")+"\","  + "\"RECORD\": "+jsonObject.getString("RECORD") + ",\"MUSIC\": "+"["+music+"]}"; // 최종 결과 값 가져오기
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
                            //     노래 갱신하기
                            // ---------------------

                            MusicChoAT.editor.putString(KEY,result); // Update
                            MusicChoAT.editor.apply(); // 저장소 갱신하기

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // 갱신하기
                        notifyDataSetChanged();

                        break;

                    case 1003:  // 즐겨찾기 삭제

                        if(MusicChoAT.ALLFAV) // FAV에 있을 경우
                        {
                            int draw_key2 = MusicinfoArrayList.get(getAdapterPosition()).drawableId; // 노래 키 값

                            for(int i =0; i < arrayList_fav.size(); i++) {
                                if(MusicinfoArrayList.get(getAdapterPosition()).drawableId == arrayList_fav.get(i).drawableId) // 기존게 저장되어있는 곳도 수정하기
                                {
                                    arrayList_fav.remove(i);
                                }
                            }
                            for(int i =0; i < arrayList.size(); i++) {
                                if(MusicinfoArrayList.get(getAdapterPosition()).drawableId == arrayList.get(i).drawableId) // 기존게 저장되어있는 곳도 수정하기
                                {
                                    arrayList.set(i, new Music_info(MusicinfoArrayList.get(getAdapterPosition()).drawableId, MusicinfoArrayList.get(getAdapterPosition()).title, MusicinfoArrayList.get(getAdapterPosition()).artist, MusicinfoArrayList.get(getAdapterPosition()).time, false));
                                }
                            }
                            MusicinfoArrayList.remove(getAdapterPosition());

                            // --------------------------------
                            //     회원 정보를 JSON으로 추출
                            // --------------------------------

                            frist = true; // 처음
                            frist1 = true; // 처음
                            frist2 = true; // 처음

                            JSON_string = ""; // 저장 공간 비우기

                            totalValue = MusicChoAT.sharePref.getAll();// 저장소에 있는 정보를 다 넣기
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

                                // ------------------------
                                //     노래 저장하기
                                // ------------------------

                                jsonArray = new JSONArray(JSON_string);
                                String music = ""; // 노래 저장하는 곳

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    if(MusicChoAT.id_text.equals(jsonObject1.getString("ID"))){
                                        // 노래 배열 가져오기
                                        JSONArray Music = jsonObject1.getJSONArray("MUSIC");
                                        if (frist1) {
                                            // 첫번째 키를 제외하고
                                            frist1 = false;
                                        } else // 그외에는 ,를 붙이기
                                        {
                                            music += ",";
                                        }
                                        for(int j = 0; j< Music.length(); j++){
                                            JSONObject jsonObject2= Music.getJSONObject(j);
                                            if(Integer.parseInt(jsonObject2.getString("MUSIC_ID")) != draw_key2)
                                            {
                                                if (frist2) {
                                                    // 첫번째 키를 제외하고
                                                    frist2 = false;
                                                } else // 그외에는 ,를 붙이기
                                                {
                                                    music += ",";
                                                }
                                                music += jsonObject2; // 기존 기록들 저장하기
                                            }
                                            if(Integer.parseInt(jsonObject2.getString("MUSIC_ID")) == draw_key2)
                                            {
                                                if (frist2) {
                                                    // 첫번째 키를 제외하고
                                                    frist2 = false;
                                                } else // 그외에는 ,를 붙이기
                                                {
                                                    music += ",";
                                                }
                                                // 기존 값 저장
                                                music += "{\"MUSIC_ID\":\""+jsonObject2.getString("MUSIC_ID") +"\"," + "\"TITLE\":\""+jsonObject2.getString("TITLE")+"\"," + "\"ARTIST\":\""+jsonObject2.getString("ARTIST")+"\"," + "\"TIME\":\""+jsonObject2.getString("TIME")+"\"," + "\"FAV\":\""+ "false"+"\"}";
                                            }
                                        }
                                        break;
                                    }
                                }

                                // ------------------------
                                //     노래 위치 검색
                                // ------------------------

                                int position = 0; // 포지션값 가져오기
                                int cnt = 0; // 카운트 하기
                                String result = ""; // 결과
                                String KEY = ""; // 키 값

                                // 해당 아이디가 있는 위치 검색
                                jsonArray = new JSONArray(JSON_string);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if(MusicChoAT.id_text.equals(jsonObject.getString("ID"))){
                                        position = i; // 포지션값 저장하기
                                        result = "{\"ID\": \""+ jsonObject.getString("ID") +"\"," + "\"PASSWORD\": \""+jsonObject.getString("PASSWORD")+"\","+ "\"MUSIC_V\": \""+jsonObject.getString("MUSIC_V")+"\"," + "\"EFFECT_V\": \""+jsonObject.getString("EFFECT_V")+"\"," + "\"EFFECT_N\": \""+jsonObject.getString("EFFECT_N")+"\","  + "\"RECORD\": "+jsonObject.getString("RECORD") + ",\"MUSIC\": "+"["+music+"]}"; // 최종 결과 값 가져오기
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
                                //     노래 갱신하기
                                // ---------------------

                                MusicChoAT.editor.putString(KEY,result); // Update
                                MusicChoAT.editor.apply(); // 저장소 갱신하기

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        else // ALL에 있을 경우
                        {
                            MusicinfoArrayList.set(getAdapterPosition(), (new Music_info(MusicinfoArrayList.get(getAdapterPosition()).drawableId, MusicinfoArrayList.get(getAdapterPosition()).title, MusicinfoArrayList.get(getAdapterPosition()).artist, MusicinfoArrayList.get(getAdapterPosition()).time, false)));
                            for(int i =0; i < arrayList.size(); i++) {
                                if(MusicinfoArrayList.get(getAdapterPosition()).drawableId == arrayList.get(i).drawableId) // 기존게 저장되어있는 곳도 수정하기
                                {
                                    arrayList.set(i, new Music_info(MusicinfoArrayList.get(getAdapterPosition()).drawableId, MusicinfoArrayList.get(getAdapterPosition()).title, MusicinfoArrayList.get(getAdapterPosition()).artist, MusicinfoArrayList.get(getAdapterPosition()).time, false));
                                }
                            }
                        }
                        notifyDataSetChanged();

                        break;
                }
                return true;
            }
        };

    }

    private static ArrayList<Music_info> MusicinfoArrayList; // 노래 목록 받아오기
    private boolean check; // 넘어간건가?

    Adapter(Context context, ArrayList<Music_info> MusicinfoArrayList, boolean check){

        this.MusicinfoArrayList = MusicinfoArrayList;
        this.mContext = context;
        this.check = check;
        arrayList = new ArrayList<Music_info>();
        arrayList_fav = new ArrayList<Music_info>();
        arrayList.addAll(MusicinfoArrayList);
    }

    public boolean return_check(){ // 넘어간거야! 알려주기
        return check;
    }

    public void get_check(boolean check){ // 다시시작시에 초기화 할 수 있도록
        this.check = check;
    }

    public void get_cnt(int cnt){ // 다시 시작시 초기화 받아오기
        this.cnt = cnt;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item, parent, false);
        return new MyViewHolder(v);
    }

    // View 의 내용을 해당 포지션의 데이터로 바꿉니다.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        MyViewHolder myViewHolder = (MyViewHolder) holder;
        this.position = position;

        if(MusicinfoArrayList.get(position).time.equals("")){
            Bitmap myBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/SHAKER" + "/Music/" +MusicinfoArrayList.get(position).title);
            myViewHolder.it_picture.setImageBitmap(myBitmap);
            myViewHolder.it_time.setText("");

        }
        else{
            myViewHolder.it_picture.setImageResource(MusicinfoArrayList.get(position).drawableId);
            myViewHolder.it_time.setText("Time : " + MusicinfoArrayList.get(position).time);
        }
        myViewHolder.it_star_box.setChecked(MusicinfoArrayList.get(position).star_box);
        myViewHolder.it_tite.setText("Title : " + MusicinfoArrayList.get(position).title);
        myViewHolder.it_artist.setText("Artist : " + MusicinfoArrayList.get(position).artist);

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if( cnt == 0 ) // 처음 누를 경우
                {
                    position_check = position; // 포지션 값 저장
                    cnt++;

                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.giving_in)){
                        MusicChoAT.ms.setMusic("Giving_in",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.joystick)){
                        MusicChoAT.ms.setMusic("Joystick",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.kuyenda)){
                        MusicChoAT.ms.setMusic("Kuyenda",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.light_up_the_sky)){
                        MusicChoAT.ms.setMusic("Light_Up_The_Sky",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.hold_on)){
                        MusicChoAT.ms.setMusic("Hold_on",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.muffin)){
                        MusicChoAT.ms.setMusic("Muffin",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.sicc)){
                        MusicChoAT.ms.setMusic("SICC",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.hope)){
                        MusicChoAT.ms.setMusic("Hope",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.candyland)){
                        MusicChoAT.ms.setMusic("Candyland",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.sunburst)){
                        MusicChoAT.ms.setMusic("Sunburst",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.delicious)){
                        MusicChoAT.ms.setMusic("Delicious",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.together)){
                        MusicChoAT.ms.setMusic("Toghther",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.whole)){
                        MusicChoAT.ms.setMusic("Whole",true);
                    }
                    if(MusicinfoArrayList.get(position).time.equals("")){

                        MusicChoAT.ms.mp.stop();
                        MusicChoAT.ms.mp.release();
                        MusicChoAT.ms.mp = null;
                        MusicChoAT.ms.mp = new MediaPlayer();

                        Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ""+ MusicinfoArrayList.get(position).drawableId);

                        try {
                            MusicChoAT.ms.mp.setDataSource(mContext, musicURI);
                            MusicChoAT.ms.mp.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        MusicChoAT.ms.mp.setVolume(MusicChoAT.ms.change_volume,MusicChoAT.ms.change_volume); // 왼쪽과 오른쪽 넣기
                        MusicChoAT.ms.mp.setLooping(true);
                        MusicChoAT.ms.mp.start();
                    }
//                    MusicChoAT.createVisualizer();
                }
                else if (position_check != position) // 다른거 눌러서 포지션이 일치하지 않으면 초기화
                {
                    cnt = 1;
                    position_check = position;

                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.giving_in)){
                        MusicChoAT.ms.setMusic("Giving_in",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.joystick)){
                        MusicChoAT.ms.setMusic("Joystick",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.kuyenda)){
                        MusicChoAT.ms.setMusic("Kuyenda",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.light_up_the_sky)){
                        MusicChoAT.ms.setMusic("Light_Up_The_Sky",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.hold_on)){
                        MusicChoAT.ms.setMusic("Hold_on",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.muffin)){
                        MusicChoAT.ms.setMusic("Muffin",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.sicc)){
                        MusicChoAT.ms.setMusic("SICC",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.hope)){
                        MusicChoAT.ms.setMusic("Hope",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.candyland)){
                        MusicChoAT.ms.setMusic("Candyland",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.sunburst)){
                        MusicChoAT.ms.setMusic("Sunburst",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.delicious)){
                        MusicChoAT.ms.setMusic("Delicious",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.together)){
                        MusicChoAT.ms.setMusic("Toghther",true);
                    }
                    if(MusicinfoArrayList.get(position).drawableId == (R.drawable.whole)){
                        MusicChoAT.ms.setMusic("Whole",true);
                    }
                    if(MusicinfoArrayList.get(position).time.equals("")){

                        MusicChoAT.ms.mp.stop();
                        MusicChoAT.ms.mp.release();
                        MusicChoAT.ms.mp = null;
                        MusicChoAT.ms.mp = new MediaPlayer();

                        Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ""+ MusicinfoArrayList.get(position).drawableId);

                        try {
                            MusicChoAT.ms.mp.setDataSource(mContext, musicURI);
                            MusicChoAT.ms.mp.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        MusicChoAT.ms.mp.setVolume(MusicChoAT.ms.change_volume,MusicChoAT.ms.change_volume); // 왼쪽과 오른쪽 넣기
                        MusicChoAT.ms.mp.setLooping(true);
                        MusicChoAT.ms.mp.start();

                    }
//                    MusicChoAT.createVisualizer();
                }
                else if( cnt == 1 && position == position_check) // 두번 누를 경우 , 포지션이 일치한지 체크하기
                {
                    check = true;

                    Intent intent = new Intent(mContext , MusicCho2AT.class);
                    intent.putExtra("DRAW",MusicinfoArrayList.get(position).drawableId); // 절대 키
                    intent.putExtra("Title",MusicinfoArrayList.get(position).title); // 제목 주기
                    intent.putExtra("Artist",MusicinfoArrayList.get(position).artist); // 가수 주기
                    intent.putExtra("Time",MusicinfoArrayList.get(position).time); // 시간 주기
                    intent.putExtra("ID",MusicChoAT.id_text); // 아이디 주기
                    mContext.startActivity(intent);

                    cnt = 0;
                    position_check = 0;
                }

            }
        });
    }
    // 데이터 셋의 크기를 리턴해줍니다.
    @Override
    public int getItemCount() {
        return MusicinfoArrayList.size();
    }

    public void all_filter(String charText) { // 전체일 때 필터
        charText = charText.toLowerCase(Locale.getDefault());
        MusicinfoArrayList.clear();

        if (charText.length() == 0) {
            MusicinfoArrayList.addAll(arrayList);
        } else {
            for (Music_info music_info : arrayList) {
                String name = music_info.title;
                if (name.toLowerCase().contains(charText)) {
                    MusicinfoArrayList.add(music_info);
                }
            }
        }
        notifyDataSetChanged(); // 갱신
    }

    public void fav_filter(String charText) { // 즐겨찾기일 때 필터
        charText = charText.toLowerCase(Locale.getDefault());
        MusicinfoArrayList.clear();

        if (charText.length() == 0) {
            MusicinfoArrayList.addAll(arrayList_fav);
        } else {
            for (Music_info music_info : arrayList_fav) {
                String name = music_info.title;
                if (name.toLowerCase().contains(charText)) {
                    MusicinfoArrayList.add(music_info);
                }
            }
        }
        notifyDataSetChanged(); // 갱신
    }

    public static void ALL_ITEM(){ // 전체 아이템 가져오기

        MusicinfoArrayList.clear();
        arrayList_fav.clear(); // 전체로 가버렸으니 일단 초기화 시키기

        MusicinfoArrayList.addAll(arrayList);
    }
    public static void FAC_ITEM(){ // 즐겨찾기 아이템 가져오기

        MusicinfoArrayList.clear();
        for (Music_info music_info : arrayList) {
            boolean star_box = music_info.star_box;
            if (star_box) {
                MusicinfoArrayList.add(music_info);
            }
        }

        arrayList_fav.addAll(MusicinfoArrayList); // 검색을 위한 저장

    }
}