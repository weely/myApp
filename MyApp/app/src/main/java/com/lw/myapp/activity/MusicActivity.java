package com.lw.myapp.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lw.myapp.R;
import com.lw.myapp.adapter.MusicInfoAdapter;
import com.lw.myapp.adapter.ViewPagerAdapter;
import com.lw.myapp.model.LrcInfo;
import com.lw.myapp.model.MusicInfo;
import com.lw.myapp.services.MusicService;
import com.lw.myapp.util.MusicUtil;
import com.lw.myapp.view.LrcView;
import com.lw.myapp.view.MyDrawable;
import com.lw.myapp.view.RoundImageDrawable;
import com.lw.myapp.view.RoundProgressBar;

import java.util.ArrayList;
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
    private List<LrcInfo> lrcInfos;
    private ListView musicListView;
    private MusicBroatcastReceiver musicBroadReceiver;
    private Intent musicService;

    private TextView tvSongName, tvCurrentTime, tvResTime;
    private ImageButton btnPrevious, btnNext, btnPlay, btnReback, btnlist, btnPlayType;
    private SeekBar seekBar;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private MusicInfoAdapter musicAdapter;
    public static LrcView lrcView;

    private FrameLayout mainMusicView;
    private RoundProgressBar progressBar;

    private boolean isPlay = false;
    private int currentItem, currentTime;
    private int playType = 2;
    private Message message = new Message();
    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            if (musicInfoLists != null) {
                switch (msg.what) {
                    case 1:         //play
                        tvSongName.setText(musicInfoLists.get(currentItem).getTitle()
                                + "-" + musicInfoLists.get(currentItem).getArtist());
                        int duration = (int) musicInfoLists.get(currentItem).getDuration();
                        seekBar.setMax(duration);
                        progressBar.setMax(duration);
                        if (isPlay) {
                            btnPlay.setImageResource(R.drawable.pause_selector);
                        } else {
                            btnPlay.setImageResource(R.drawable.play_selector);
                        }
                        musicListView.setSelection(currentItem);
                        break;
                    case 2:
                        tvCurrentTime.setText(MusicUtil.formatTime(currentTime));
                        long res = musicInfoLists.get(currentItem).getDuration() - currentTime;
                        tvResTime.setText(MusicUtil.formatTime(res));
                        seekBar.setProgress(currentTime);
                        progressBar.setProgress(currentTime);
                        break;
                    case 3:
                        tvSongName.setText(musicInfoLists.get(currentItem).getTitle()
                                + "-" + musicInfoLists.get(currentItem).getArtist());
                        int duration2 = (int) musicInfoLists.get(currentItem).getDuration();
                        seekBar.setMax(duration2);
                        progressBar.setMax(duration2);
                        if (isPlay) {
                            btnPlay.setImageResource(R.drawable.pause_selector);
                        } else {
                            btnPlay.setImageResource(R.drawable.play_selector);
                        }
                        musicListView.setSelection(currentItem);
                        tvCurrentTime.setText(MusicUtil.formatTime(currentTime));
                        long res2 = musicInfoLists.get(currentItem).getDuration() - currentTime;
                        tvResTime.setText(MusicUtil.formatTime(res2));
                        seekBar.setProgress(currentTime);
                        progressBar.setProgress(currentTime);
                        break;
                    case 4:
                        drawerLayout.closeDrawer(musicListView);
                        //musicAdapter.setSelectItem(currentItem);
                        //musicAdapter.notifyDataSetInvalidated();
                    default:
                        break;
                }
                //progressBar.invalidate();
            }
            super.handleMessage(msg);
        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, final IBinder service) {
            currentItem = ((MusicService.MusicBinder) service).getCurrentItem();
            isPlay = ((MusicService.MusicBinder) service).getPlayStatus();
            currentTime = ((MusicService.MusicBinder) service).getCurrentTime();
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

        musicInfoLists = MusicUtil.getMusicLists(MusicActivity.this);

        addChildView();
    }

    public void addChildView() {
        List<View> viewLists = new ArrayList<View>();
        //MyHorizontalScrollView mainMusicView = new MyHorizontalScrollView(MusicActivity.this);
        mainMusicView = (FrameLayout) getLayoutInflater().
                inflate(R.layout.music_main_layout, (ViewGroup) findViewById(R.id.main_music_viewGroup));
        progressBar = (RoundProgressBar) mainMusicView.findViewById(R.id.music_progressBar);
        progressBar.postInvalidate();

        ImageView imageView = (ImageView) mainMusicView.findViewById(R.id.main_music_view);
        Bitmap bitmap = BitmapFactory.decodeResource(MusicActivity.this.getResources(), R.mipmap.item01);
        imageView.setImageDrawable(new RoundImageDrawable(bitmap));
        //imageView.setAlpha((float)0.8);
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        drawerLayout.setBackground(new MyDrawable(bitmap,height,width));

        lrcView = new LrcView(MusicActivity.this);
        viewLists.add(mainMusicView);
        viewLists.add(lrcView);
        viewPagerAdapter = new ViewPagerAdapter(viewLists);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    private void addListener() {
        btnlist.setOnClickListener(this);
        btnReback.setOnClickListener(this);
        btnPlayType.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        musicAdapter = new MusicInfoAdapter(musicInfoLists, MusicActivity.this);
        musicListView.setAdapter(musicAdapter);
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentItem = position;
                startMusicService(PLAY_FLAG, musicInfoLists.get(currentItem).getUrl(), currentItem, 0);
                handler.sendEmptyMessageDelayed(4, 100);
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

    private void startMusicService(int flag, String url, int position, int time) {
        isPlay = flag == PAUSE_FLAG ? false : true;
        Intent service = new Intent(MusicActivity.this, MusicService.class);
        service.putExtra("PLAY_MSG", flag);
        service.putExtra("MUSIC_URL", url);
        service.putExtra("CURRENT_TIME", time);
        service.putExtra("CURRENT_ITEM", position);
        startService(service);
        handler.sendEmptyMessage(1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_list:
                drawerLayout.openDrawer(musicListView);
                break;
            case R.id.btn_reback:
                onBackPressed();
                break;
            case R.id.btn_previous:
                if (musicInfoLists == null || musicInfoLists.isEmpty()) return;

                currentItem = currentItem <= 0 ? (musicInfoLists.size() - 1) : --currentItem;
                startMusicService(PREVIOUS_FLAG, musicInfoLists.get(currentItem).getUrl(), currentItem, 0);
                break;
            case R.id.btn_play:
                if (musicInfoLists == null || musicInfoLists.isEmpty()) return;

                int playFlag = isPlay ? PAUSE_FLAG : PLAY_FLAG;
                startMusicService(playFlag, musicInfoLists.get(currentItem).getUrl(), currentItem, currentTime);
                break;
            case R.id.btn_next:
                if (musicInfoLists == null || musicInfoLists.isEmpty()) return;

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
}
