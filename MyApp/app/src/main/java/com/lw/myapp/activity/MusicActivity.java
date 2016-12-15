package com.lw.myapp.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lw.myapp.R;
import com.lw.myapp.adapter.MusicInfoAdapter;
import com.lw.myapp.model.MusicInfo;
import com.lw.myapp.services.MusicService;
import com.lw.myapp.util.MusicUtil;

import java.util.List;

/**
 * Created by Lw on 2016/12/8.
 */

public class MusicActivity extends Activity implements View.OnClickListener {

    public final static int PLAY_FLAG = 1;
    public final static int PREVIOUS_FLAG = 2;
    public final static int NEXT_FLAG = 3;
    public final static int PROGRESS_FLAG = 4;
    public final static int PAUSE_FLAG = 5;
    private int[] playTypeImgs = {R.mipmap.icon_playing_mode_repeat_cur, R.mipmap.icon_playing_mode_normal,
            R.mipmap.icon_playing_mode_repeat_all, R.mipmap.icon_playing_mode_shuffle};

    private List<MusicInfo> musicInfoLists;
    private ListView musicListView;
    private MusicBroatcastReceiver musicBroadReceiver;
    private Intent musicService;

    private TextView tvSongName, tvCurrentTime, tvResTime;
    private ImageButton btnPrevious, btnNext, btnPlay, btnReback, btnlist, btnPlayType;
    private SeekBar seekBar;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;

    private boolean isPlay = false;
    private int currentItem, currentTime;
    private int playType = 2;
    private Message message = new Message();
    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            if (musicInfoLists != null) {
                switch (msg.what) {
                    case 1:
                        tvSongName.setText(musicInfoLists.get(currentItem).getTitle());
                        seekBar.setMax((int) musicInfoLists.get(currentItem).getDuration());
                        if (isPlay) {
                            btnPlay.setImageResource(R.drawable.pause_selector);
                        } else {
                            btnPlay.setImageResource(R.drawable.play_selector);
                        }
                        break;
                    case 2:
                        //tvSongName.setText(musicInfoLists.get(currentItem).getTitle());
                        tvCurrentTime.setText(MusicUtil.formatTime(currentTime));
                        long res = musicInfoLists.get(currentItem).getDuration() - currentTime;
                        tvResTime.setText(MusicUtil.formatTime(res));
                        seekBar.setProgress(currentTime);
                        break;
                    case 3:
                        tvSongName.setText(musicInfoLists.get(currentItem).getTitle());
                        if (isPlay) {
                            btnPlay.setImageResource(R.drawable.pause_selector);
                        } else {
                            btnPlay.setImageResource(R.drawable.play_selector);
                        }
                        tvCurrentTime.setText(MusicUtil.formatTime(currentTime));
                        seekBar.setMax((int) musicInfoLists.get(currentItem).getDuration());
                        long res2 = musicInfoLists.get(currentItem).getDuration() - currentTime;
                        tvResTime.setText(MusicUtil.formatTime(res2));
                        seekBar.setProgress(currentTime);
                        break;
                    default:
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, final IBinder service) {
            currentItem = ((MusicService.MusicBinder)service).getCurrentItem();
            isPlay = ((MusicService.MusicBinder)service).getPlayStatus();
            currentTime = ((MusicService.MusicBinder)service).getCurrentTime();
            handler.sendEmptyMessage(3);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_layout);

        initView();
        addListener();
        musicService = new Intent(MusicActivity.this, MusicService.class);
        bindService(musicService, conn, Context.BIND_AUTO_CREATE);

        musicBroadReceiver = new MusicBroatcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("UPDATE_ACTION");
        filter.addAction("CURRENT_TIME");
        registerReceiver(musicBroadReceiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        musicListView = (ListView) findViewById(R.id.musicListView);
        musicInfoLists = MusicUtil.getMusicLists(MusicActivity.this);
        tvSongName = (TextView) findViewById(R.id.tv_song_name);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        tvResTime = (TextView) findViewById(R.id.tv_res_time);
        btnlist = (ImageButton) findViewById(R.id.btn_list);
        btnPrevious = (ImageButton) findViewById(R.id.btn_previous);
        btnPlay = (ImageButton) findViewById(R.id.btn_play);
        btnNext = (ImageButton) findViewById(R.id.btn_next);
        btnReback = (ImageButton) findViewById(R.id.btn_reback);
        btnPlayType = (ImageButton) findViewById(R.id.btn_play_type);
        seekBar = (SeekBar) findViewById(R.id.sbar_music);
        viewPager = (ViewPager) findViewById(R.id.viewpager_music);
    }

    private void addListener() {
        btnlist.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnReback.setOnClickListener(this);
        btnPlayType.setOnClickListener(this);

        final MusicInfoAdapter musicAdapter = new MusicInfoAdapter(musicInfoLists, MusicActivity.this);

        musicListView.setAdapter(musicAdapter);
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentItem = position;
                message.what = 1;
                handler.sendEmptyMessage(message.what);
                startMusicService(PLAY_FLAG, musicInfoLists.get(currentItem).getUrl(), currentItem, 0);
                drawerLayout.closeDrawer(musicListView);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (musicInfoLists == null || musicInfoLists.isEmpty()) return;
                int progress = seekBar.getProgress();
                startMusicService(PROGRESS_FLAG, musicInfoLists.get(currentItem).getUrl(), currentItem, progress);
            }
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void startMusicService(int play_flag, String url, int position, int time) {
        isPlay = play_flag == PAUSE_FLAG ? false : true;
        Intent service = new Intent(MusicActivity.this, MusicService.class);
        service.putExtra("PLAY_MSG", play_flag);
        service.putExtra("MUSIC_URL", url);
        service.putExtra("CURRENT_TIME", time);
        service.putExtra("CURRENT_ITEM", position);
        startService(service);
        handler.sendEmptyMessage(1);
    }

    @Override
    public void onClick(View v) {
        if (musicInfoLists == null || musicInfoLists.isEmpty()) return;

        switch (v.getId()) {
            case R.id.btn_list:

                break;
            case R.id.btn_reback:
                onBackPressed();
                break;
            case R.id.btn_previous:
                currentItem = currentItem <= 0 ? (musicInfoLists.size() - 1) : --currentItem;
                startMusicService(PREVIOUS_FLAG, musicInfoLists.get(currentItem).getUrl(), currentItem, 0);
                break;
            case R.id.btn_play:
                int playFlag = isPlay ? PAUSE_FLAG : PLAY_FLAG;
                startMusicService(playFlag, musicInfoLists.get(currentItem).getUrl(), currentItem, currentTime);
                break;
            case R.id.btn_next:
                currentItem = currentItem >= (musicInfoLists.size() - 1) ? 0 : ++currentItem;
                startMusicService(NEXT_FLAG, musicInfoLists.get(currentItem).getUrl(), currentItem, 0);
                break;
            case R.id.btn_play_type:
                playType = playType >= 4 ? 1 : ++playType;
                btnPlayType.setImageResource(playTypeImgs[playType - 1]);
                Intent sendPlayTypeBroadcast = new Intent("UPDATE_PLAY_TYPE");
                sendPlayTypeBroadcast.putExtra("PLAY_TYPE", playType);
                sendBroadcast(sendPlayTypeBroadcast);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        unregisterReceiver(musicBroadReceiver);
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private class MusicBroatcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("UPDATE_ACTION".equals(action)) {
                currentItem = intent.getIntExtra("CURRENT_ITEM", -1);
                message.what = 1;
                isPlay = true;
                handler.sendEmptyMessage(message.what);
            } else if ("CURRENT_TIME".equals(action)) {
                currentTime = intent.getIntExtra("CURRENT_TIME", -1);
                message.what = 2;
                handler.sendEmptyMessage(message.what);
            }
        }
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

        }

        return super.onKeyDown(keyCode, event);
    }*/
}
