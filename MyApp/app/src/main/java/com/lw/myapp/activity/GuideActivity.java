package com.lw.myapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lw.myapp.R;
import com.lw.myapp.adapter.LoadAdapter;

public class GuideActivity extends AppCompatActivity {
    private ImageButton btnLeading;
    private ViewPager vPLeading;
    private ViewGroup vGLeading;
    private ImageView[] tips;
    private ImageView[] mImageViews;
    private int[] imgIdArray;


    private Handler h = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                vPLeading.setCurrentItem(vPLeading.getCurrentItem() + 1);
                h.sendEmptyMessageDelayed(0, 2000);
            } else if (msg.what == 1) {
                Intent intent = new Intent();
                intent.setClass(GuideActivity.this, MainActivity.class);
                startActivity(intent);
                GuideActivity.this.finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_layout);

        init();
        addListener();
        h.sendEmptyMessageDelayed(0, 2000);
    }

    private void init() {
        vPLeading = (ViewPager) findViewById(R.id.viewPagerLeading);
        btnLeading = (ImageButton) findViewById(R.id.btnLeading);
        vGLeading = (ViewGroup) findViewById(R.id.viewGroupLeading);

        //载入图片资源ID
        imgIdArray = new int[]{R.mipmap.item01, R.mipmap.item02, R.mipmap.item03, R.mipmap.item04};
        //将点点加入到ViewGroup中
        tips = new ImageView[imgIdArray.length];
        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(10, 10));
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                    ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            vGLeading.addView(imageView, layoutParams);
        }
    }

    private void addListener() {
        //设置Adapter
        vPLeading.setAdapter(new LoadAdapter(GuideActivity.this, imgIdArray));
        vPLeading.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < tips.length; i++) {
                    if (i == position % imgIdArray.length) {
                        tips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
                    } else {
                        tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
                    }
                }
                if (position == imgIdArray.length - 1) {
                    btnLeading.setVisibility(View.VISIBLE);
                } else {
                    btnLeading.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnLeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("LEADING_RECORD", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("is_first", true);
                editor.commit();

                h.sendEmptyMessageDelayed(1, 1500);
            }
        });
    }
}
