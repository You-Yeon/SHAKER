package com.shaker.test1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

// 게임 기록 어댑터 클래스입니다.

public class Adapter_Rec extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    int position;
    int difficulty;

    //view 들을 setting 해줍니다.
    // ViwHolder 입니당.
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        // 뷰에 차곡차곡 넣어주는 작업
        ImageView it_rank; // 랭크
        TextView it_time; // 시간
        TextView it_score; // 점수

        MyViewHolder(View view){
            super(view);
            it_rank = view.findViewById(R.id.it_rank);
            it_time = view.findViewById(R.id.it_time);
            it_score = view.findViewById(R.id.it_score);

            view.setOnCreateContextMenuListener(this); //2. OnCreateContextMenuListener 리스너를 현재 클래스에서 구현한다고 설정해둡니다.
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {  // 컨텍스트 메뉴를 생성하고 메뉴 항목 선택시 호출되는 리스너를 등록해줍니다. ID 1001, 1002로 어떤 메뉴를 선택했는지 리스너에서 구분하게 됩니다.

            MenuItem Delete = menu.add(Menu.NONE, 1002, 1, "삭제");
            Delete.setOnMenuItemClickListener(onEditMenu);

        }

        // 컨텍스트 메뉴에서 항목 클릭시 동작을 설정합니다.
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1002:

                        JSONArray jsonArray; // JSON 배열
                        String JSON_string; // JSON 배열형식으로 바꾸기
                        String time = RCinfoArrayList.get(getAdapterPosition()).time; // 노래 키 값

                        difficulty = RCinfoArrayList.get(getAdapterPosition()).difficulty; // 임시 저장하고
                        MusicCho2AT.del_Record(RCinfoArrayList.get(getAdapterPosition()).drawableId ,RCinfoArrayList.get(getAdapterPosition()).difficulty , RCinfoArrayList.get(getAdapterPosition()).time );
                        MusicCho2AT.show_Record(difficulty); // 해당 난이도를 다시 보여주자 !

                        // --------------------------------
                        //     회원 정보를 JSON으로 추출
                        // --------------------------------

                        boolean frist = true; // 처음
                        boolean frist1 = true; // 처음
                        boolean frist2 = true; // 처음

                        JSON_string = ""; // 저장 공간 비우기

                        Map<String, ?> totalValue = MusicCho2AT.sharePref.getAll();// 저장소에 있는 정보를 다 넣기
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
                            //     기존 기록 저장하기
                            // ------------------------

                            jsonArray = new JSONArray(JSON_string);
                            String record = ""; // 노래 저장하는 곳

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                if(MusicCho2AT.id_text.equals(jsonObject1.getString("ID"))){
                                    // 노래 배열 가져오기
                                    JSONArray Record = jsonObject1.getJSONArray("RECORD");
                                    if (frist1) {
                                        // 첫번째 키를 제외하고
                                        frist1 = false;
                                    } else // 그외에는 ,를 붙이기
                                    {
                                        record += ",";
                                    }
                                    for(int j = 0; j< Record.length(); j++){
                                        JSONObject jsonObject2= Record.getJSONObject(j);
                                        if(!jsonObject2.getString("TIME").equals(time))
                                        {
                                            if (frist2) {
                                                // 첫번째 키를 제외하고
                                                frist2 = false;
                                            } else // 그외에는 ,를 붙이기
                                            {
                                                record += ",";
                                            }
                                            record += jsonObject2; // 기존 기록들 저장하기
                                        }
                                        else
                                        {
                                            // 파이어베이스 데이터 삭제
                                            Query applesQuery = FirebaseDatabase.getInstance().getReference("User").child(jsonObject1.getString("ID")).child("RECORD").orderByChild("TIME").equalTo(jsonObject2.getString("TIME"));
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

                                        }
                                    }
                                    break;
                                }
                            }

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
                                if(MusicCho2AT.id_text.equals(jsonObject.getString("ID"))){
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

                            MusicCho2AT.editor.putString(KEY,result); // Update
                            MusicCho2AT.editor.apply(); // 저장소 갱신하기

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                }
                return true;
            }
        };
    }
    private ArrayList<Record_info> RCinfoArrayList; // 기록 목록 받아오기

    Adapter_Rec(Context context, ArrayList<Record_info> RCinfoArrayList){
        this.RCinfoArrayList = RCinfoArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item, parent, false);
        return new MyViewHolder(v);
    }

    // View 의 내용을 해당 포지션의 데이터로 바꿉니다.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        MyViewHolder myViewHolder = (MyViewHolder) holder;
        this.position = position;

        myViewHolder.it_rank.setImageResource(RCinfoArrayList.get(position).rank);
        myViewHolder.it_time.setText(RCinfoArrayList.get(position).time);
        myViewHolder.it_score.setText("Score : " + String.format("%08d",RCinfoArrayList.get(position).score));
    }

    // 데이터 셋의 크기를 리턴해줍니다.
    @Override
    public int getItemCount() {
        return RCinfoArrayList.size();
    }
}