package com.lw.myapp.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.lw.myapp.model.MusicInfo;
import com.lw.myapp.util.MusicUtil;

import java.util.List;

/**
 * Created by Lw on 2016/12/8.
 */

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    private MediaPlayer player;
    private String musicUrl;
    private int currentTime;
    private int currentItem;
    private List<MusicInfo> musicInfoLists;
    private int playType = 2;       //播放模式; 1、单曲循环 2、循序播放 3、循环播放 4、随机播放
    //private boolean isPlay = false;
    private PlayTypeReceiver receiver;
    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (player != null) {
                    currentTime = player.getCurrentPosition();
                    Intent sendCurrentTimeBrostcast = new Intent("CURRENT_TIME");
                    sendCurrentTimeBrostcast.putExtra("CURRENT_TIME", currentTime);
                    sendBroadcast(sendCurrentTimeBrostcast);
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        IBinder binder = new MusicBinder();
        return binder;
    }

    @Override
    public void onCreate() {
        player = new MediaPlayer();
        musicInfoLists = MusicUtil.getMusicLists(getApplicationContext());

        receiver = new PlayTypeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("UPDATE_PLAY_TYPE");
        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int msg = intent.getIntExtra("PLAY_MSG", -1);
        switch(msg) {
            case 1:             //next
            case 2:             //previous
            case 3:             //progress
            case 4:             //play
                musicUrl = intent.getStringExtra("MUSIC_URL");
                currentTime = intent.getIntExtra("CURRENT_TIME", -1);
                currentItem = intent.getIntExtra("CURRENT_ITEM", -1);
                play(currentTime);
                break;
            case 5:             //pause
                if (player != null && player.isPlaying()) {
                    player.pause();
                    //isPlay = false;
                }
                break;
            default:
                break;
        }
        /*Bundle bundle = intent.getBundleExtra("MUSIC_BUNDLE");
        musicInfoLists = (List<MusicInfo>) bundle.getSerializable("MUSIC_LISTS");*/

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        switch (playType) {
            case 1:
                player.isLooping();
                return;
            case 2:
                currentItem++;
                if (currentItem >= musicInfoLists.size()) {
                    return;
                }
                break;
            case 3:
                currentItem++;
                currentItem = currentItem >= musicInfoLists.size() ? 0 : currentItem;
                break;
            case 4:
                currentItem = (int) Math.random() * (musicInfoLists.size() - 1);
                break;
            default:
                break;
        }
        Intent sendBroadcast = new Intent("UPDATE_ACTION");
        sendBroadcast.putExtra("CURRENT_ITEM", currentItem);
        sendBroadcast(sendBroadcast);
        musicUrl = musicInfoLists.get(currentItem).getUrl();
        play(0);
    }

    @Override
    public void onDestroy() {
        Log.i("weely", "---4--Destroy-");
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        unregisterReceiver(receiver);
    }

    private void play(final int currentTime) {
        try {
            player.reset();
            player.setDataSource(musicUrl);
            player.prepare();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (mp == player) {
                        player.start();
                        if (currentTime > 0) {
                            player.seekTo(currentTime);
                        }
                    }
                }
            });
            handler.sendEmptyMessage(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MusicBinder extends Binder {
        public int getCurrentItem() {
            return currentItem;
        }
    }

    private class PlayTypeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("UPDATE_PLAY_TYPE".equals(action)) {
                playType = intent.getIntExtra("PLAY_TYPE", -1);
                Log.i("weely", "2---" + playType);
            }
        }
    }
}
