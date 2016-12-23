package com.lw.myapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.lw.myapp.R;
import com.lw.myapp.view.RoundImageDrawable;

/**
 * Created by Lw on 2016/12/7.
 */

public class MainActivity extends android.app.Activity {
    private ImageButton btnMusic, btnMovie, btnPicture, btnFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        init();
    }

    private void init() {
        btnMusic = (ImageButton) findViewById(R.id.btnMusic);
        btnMovie = (ImageButton) findViewById(R.id.btnMovie);
        btnPicture = (ImageButton) findViewById(R.id.btnPicture);
        btnFile = (ImageButton) findViewById(R.id.btnFile);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.saber);
        RoundImageDrawable drawable = new RoundImageDrawable(bitmap);
        /*btnMusic.setImageDrawable(drawable);
        btnMusic.setScaleType(ImageView.ScaleType.CENTER_INSIDE);*/
        btnMovie.setImageDrawable(drawable);
        btnPicture.setImageDrawable(drawable);
        btnFile.setImageDrawable(drawable);

        btnMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicActivity.class);
                startActivity(intent);
            }
        });
    }
}
