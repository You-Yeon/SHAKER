package com.shaker.test1;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Binder;
import android.util.Log;

// 음악 서비스 클래스입니다.

public class MusicService extends Service {

    public MediaPlayer mp;
    public int mp_volume = 50; // 최초 볼륨
    float change_volume = 0.5f; // 볼륨 바꾸기
    String tag = "MusicService";

    IBinder mBinder = (IBinder) new MyBinder();

    class MyBinder extends Binder {
        MusicService getService() { // 서비스 객체를 리턴
            return MusicService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // 액티비티에서 bindService() 를 실행하면 호출됨
        // 리턴한 IBinder 객체는 서비스와 클라이언트 사이의 인터페이스 정의한다
        return mBinder; // 서비스 객체를 리턴
    }

    @Override
    public void onCreate() { // 노래 만들기
        Log.e(tag,"onCreate");
        mp = MediaPlayer.create(this, R.raw.made_of_something);
        mp.setVolume(change_volume,change_volume);
        mp.setLooping(true);
        mp.start();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() { // 멈춰
        super.onDestroy();
        Log.e(tag,"onDestroy");
        if (mp != null)
        {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    public void setMusic(String name, boolean loop) {

        mp.stop();
        mp.release();
        mp = null;

        if (name == "Made_of_something")
            mp = MediaPlayer.create(this, R.raw.made_of_something);
        if (name == "Candyland")
            mp = MediaPlayer.create(this, R.raw.candyland);
        if (name == "Hope")
            mp = MediaPlayer.create(this, R.raw.hope);
        if (name == "Sunburst")
            mp = MediaPlayer.create(this, R.raw.sunburst);
        if (name == "Giving_in")
            mp = MediaPlayer.create(this, R.raw.giving_in);
        if (name == "Joystick")
            mp = MediaPlayer.create(this, R.raw.joystick);
        if (name == "Light_Up_The_Sky")
            mp = MediaPlayer.create(this, R.raw.light_up_the_sky);
        if (name == "Muffin")
            mp = MediaPlayer.create(this, R.raw.muffin);
        if (name == "SICC")
            mp = MediaPlayer.create(this, R.raw.sicc);
        if (name == "Kuyenda")
            mp = MediaPlayer.create(this, R.raw.kuyenda);
        if (name == "Hold_on")
            mp = MediaPlayer.create(this, R.raw.hold_on);
        if (name == "Delicious")
            mp = MediaPlayer.create(this, R.raw.delicious);
        if (name == "Toghther")
            mp = MediaPlayer.create(this, R.raw.together);
        if (name == "Whole")
            mp = MediaPlayer.create(this, R.raw.whole);

        mp.setVolume(change_volume,change_volume); // 왼쪽과 오른쪽 넣기
        mp.setLooping(loop);
        mp.start();
    }

    public void Music_on(){
        mp.start();
    }

    public void Music_pause(){
        mp.pause();
    }

    public void Music_Volume(int progress){ // 음악 볼륨 설정

        mp_volume =  progress; // 초기화 시키기

        change_volume = (float)progress * 1/100;
        Log.e("Volume",String.valueOf(change_volume));

        mp.setVolume(change_volume,change_volume); // 왼쪽과 오른쪽 넣기

    }

    public int Music_Current_Time(){
        int sec = mp.getCurrentPosition()/100;
        return sec;
    }

    public int Music_Total_Time(){
        int sec = mp.getDuration()/1000;
        return sec;
    }
}
