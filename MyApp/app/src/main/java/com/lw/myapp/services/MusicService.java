package com.lw.myapp.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.lw.myapp.activity.MusicActivity;
import com.lw.myapp.model.LrcInfo;
import com.lw.myapp.model.MusicInfo;
import com.lw.myapp.util.LrcUtil;
import com.lw.myapp.util.MusicUtil;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Lw on 2016/12/8.
 */

public class MusicService extends Service {
    private MediaPlayer player;
    private String musicUrl;
    private int currentTime;
    private int currentItem;
    private List<MusicInfo> musicInfoLists;
    private List<LrcInfo> lrcInfos;
    private int index = 0;

    private int playType = 2;       //播放模式; 1、单曲循环 2、循序播放 3、循环播放 4、随机播放
    private boolean isPlay = false;
    private PlayTypeReceiver receiver;
    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (player != null) {
                    currentTime = player.getCurrentPosition();
                    Intent sendCurrentTimeBrostcast = new Intent("CURRENT_TIME");
                    sendCurrentTimeBrostcast.putExtra("CURRENT_TIME", currentTime);
                    sendBroadcast(sendCurrentTimeBrostcast);
                    handler.sendEmptyMessageDelayed(1, 100);
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

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                switch (playType) {
                    case 1:
                        player.start();
                        return;
                    case 2:
                        currentItem++;
                        if (currentItem >= musicInfoLists.size()) {
                            isPlay = false;
                            return;
                        }
                        break;
                    case 3:
                        currentItem++;
                        currentItem = currentItem >= musicInfoLists.size() ? 0 : currentItem;
                        break;
                    case 4:
                        currentItem = (int) (Math.random() * (musicInfoLists.size() - 1));
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
        });

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
                    isPlay = false;
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
    public void onDestroy() {
        if (player != null) {
            player.stop();
            player.release();
            isPlay = false;
            player = null;
        }
        unregisterReceiver(receiver);
    }

    private void play(final int currentTime) {
        try {
            player.reset();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
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
                        initLrc();
                        isPlay = true;
                    }
                }
            });
            handler.sendEmptyMessage(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initLrc(){
        File f = new File(musicUrl.replace(".mp3", ".lrc"));
        if (!f.exists()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InputStream is = LrcUtil.getLrcFromNet(musicInfoLists.get(currentItem).getArtist(),
                            musicInfoLists.get(currentItem).getTitle());
                    LrcUtil.downLrc(musicUrl, is);
                    lrcInfos = LrcUtil.readLRC(musicUrl);
                }
            }).start();
        }
        lrcInfos = LrcUtil.readLRC(musicUrl);
        MusicActivity.lrcView.setLrcInfos(lrcInfos);
        handler.post(mRunnable);
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            if (lrcInfos == null || lrcInfos.isEmpty()) {
                return;
            } else {
                MusicActivity.lrcView.setIndex(lrcIndex());
                MusicActivity.lrcView.invalidate();
                handler.postDelayed(mRunnable, 100);
            }
        }
    };

    public int lrcIndex() {
        long duration = 0;
        if(player.isPlaying()) {
            currentTime = player.getCurrentPosition();
            duration = player.getDuration();
        }
        if (currentTime < duration) {
            for (int i = 0; i < lrcInfos.size(); i++) {
                if (i < lrcInfos.size() - 1) {
                    if ((currentTime < lrcInfos.get(i).getLrcTime() && i == 0) ||
                            (currentTime > lrcInfos.get(i).getLrcTime() && currentTime < lrcInfos.get(i + 1).getLrcTime())) {
                        index = i;
                    }
                }
                if (i == lrcInfos.size() - 1 && currentTime > lrcInfos.get(i).getLrcTime()) {
                    index = i;
                }
            }
        }
        return index;
    }

    public class MusicBinder extends Binder {
        /*private List<Object> list;
        public List<Object> getInfos() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                }
            }).start();
            return list;
        }*/

        public int getCurrentItem() {
            return currentItem;
        }
        public boolean getPlayStatus () {
            return isPlay;
        }
        public int getCurrentTime() {
            return currentTime;
        }
    }

    private class PlayTypeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("UPDATE_PLAY_TYPE".equals(action)) {
                playType = intent.getIntExtra("PLAY_TYPE", -1);
            }
        }
    }
}
