package com.lw.myapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.lw.myapp.R;

/**
 * Created by Lw on 2016/12/6.
 */

public class StartActivity extends android.app.Activity {
    private SharedPreferences sp;
    private boolean flag = false;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Intent intent1 = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent1);
                    break;
                case 2:
                    Intent intent2 = new Intent(StartActivity.this, GuideActivity.class);
                    startActivity(intent2);
                default:
                    break;
            }
            StartActivity.this.finish();
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.start_layout);

        sp = getSharedPreferences("LEADING_RECORD", this.MODE_PRIVATE);
        flag = sp.getBoolean("is_first", false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (flag) {
                    handler.sendEmptyMessageDelayed(1, 2000);
                } else {
                    handler.sendEmptyMessage(2);
                }
            }
        }).start();

        /*if (flag) {
            handler.sendEmptyMessageDelayed(1, 2000);
        } else {
            handler.sendEmptyMessage(2);
        }*/
    }
}
