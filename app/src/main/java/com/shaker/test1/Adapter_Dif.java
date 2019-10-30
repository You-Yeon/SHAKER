package com.shaker.test1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

// 게임 난이도 어댑터 클래스입니다.

public class Adapter_Dif extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int cnt = 1; // 몇번 눌렀는지 체크합니다.
    int position_check =0; // 방금 누른 포지션인지 체크합니다.
    int position;
    private Context mContext;

    String title;
    String artist;
    String time;

    //view 들을 setting 해줍니다.
    // ViwHolder 입니당.
    public class MyViewHolder extends RecyclerView.ViewHolder {


        // 뷰에 차곡차곡 넣어주는 작업
        ImageView it_star; // 별
        TextView it_note_num; // 노트 개수

        MyViewHolder(View view){
            super(view);
            it_star = view.findViewById(R.id.it_star);
            it_note_num = view.findViewById(R.id.it_note_num);

        }

    }

    private ArrayList<Difficulty_info> DCinfoArrayList; // 난이도 목록 받아오기
    private boolean check; // 넘어간건가?

    Adapter_Dif(Context context, ArrayList<Difficulty_info> DCinfoArrayList, boolean check){
        this.DCinfoArrayList = DCinfoArrayList;
        this.mContext = context;
        this.check = check;
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

    // 제목, 아티스트, 시간 가져오기
    public void get_title(String tilte) {
        this.title = tilte;
    }

    public void get_artist(String artist) {
        this.artist = artist;
    }

    public void get_time(String time) {
        this.time = time;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.difficulty_item, parent, false);
        return new MyViewHolder(v);
    }

    // View 의 내용을 해당 포지션의 데이터로 바꿉니다.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        MyViewHolder myViewHolder = (MyViewHolder) holder;
        this.position = position;

        myViewHolder.it_star.setImageResource(DCinfoArrayList.get(position).star);
        myViewHolder.it_note_num.setText(DCinfoArrayList.get(position).note_num);

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                if( cnt == 0 ) // 처음 누를 경우
//                {
//                    MusicCho2AT.v = (CardView)MusicCho2AT.mRecyclerView.getLayoutManager().findViewByPosition(position_check);
//                    MusicCho2AT.v.setCardBackgroundColor(Color.parseColor("#AAA3FF86")); // 풀리게
//
//
//                    MusicCho2AT.v = (CardView)MusicCho2AT.mRecyclerView.getLayoutManager().findViewByPosition(position);
//                    MusicCho2AT.v.setCardBackgroundColor(Color.parseColor("#AA5AD188")); // 눌리게
//
//                    MusicCho2AT.show_Record(DCinfoArrayList.get(position).star);
//                    position_check = position; // 포지션 값 저장
//                    cnt++;
//
//                }
                if (position_check != position) // 다른거 눌러서 포지션이 일치하지 않으면 초기화
                {
                    MusicCho2AT.v = (CardView)MusicCho2AT.mRecyclerView.getLayoutManager().findViewByPosition(position);
                    MusicCho2AT.v.setCardBackgroundColor(Color.parseColor("#AA5AD188")); // 눌리게

                    MusicCho2AT.v = (CardView)MusicCho2AT.mRecyclerView.getLayoutManager().findViewByPosition(position_check);
                    MusicCho2AT.v.setCardBackgroundColor(Color.parseColor("#AAA3FF86")); // 풀리게

                    MusicCho2AT.show_Record(DCinfoArrayList.get(position).star);
                    cnt = 1;
                    position_check = position;
                }
                else if( cnt == 1 && position == position_check) // 두번 누를 경우 , 포지션이 일치한지 체크하기
                {
                    check = true;
//                    Toast.makeText(mContext, position +"", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(v.getContext(),MusicLodAT.class);
                    intent.putExtra("DRAW",DCinfoArrayList.get(position).drawableId); // 절대 키
                    intent.putExtra("STAR",DCinfoArrayList.get(position).star); // 난이도 전달하기
                    intent.putExtra("Title",title); // 제목 주기
                    intent.putExtra("Artist",artist); // 가수 주기
                    intent.putExtra("Time",time); // 시간 주기
                    intent.putExtra("ID",MusicCho2AT.id_text); // 아이디 주기
                    mContext.startActivity(intent);

                    cnt = 1;
                    position_check = position;

                }

            }
        });
    }

    // 데이터 셋의 크기를 리턴해줍니다.
    @Override
    public int getItemCount() {
        return DCinfoArrayList.size();
    }
}