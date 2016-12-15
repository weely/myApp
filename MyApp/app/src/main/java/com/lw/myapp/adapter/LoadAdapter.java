package com.lw.myapp.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Lw on 2016/12/6.
 */

public class LoadAdapter extends PagerAdapter {
    private ImageView[] mImageViews;
    //载入图片资源ID
    private int[] imgIdArray;

    public LoadAdapter(Context context, int[] arrays) {
        if (mImageViews == null) {
            this.imgIdArray = arrays;
            //将图片装载到数组中
            mImageViews = new ImageView[imgIdArray.length];
            for (int i = 0; i < mImageViews.length; i++) {
                ImageView imageView = new ImageView(context);
                mImageViews[i] = imageView;
                imageView.setBackgroundResource(imgIdArray[i]);
            }
        }
    }

    @Override
    public int getCount() {
        return imgIdArray.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //((ViewPager)container).removeView(mImageViews[position % mImageViews.length]);
        container.removeView((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        try {
            ((ViewPager) container).addView(mImageViews[position % mImageViews.length], 0);
        } catch (Exception e) {
        }
        return mImageViews[position % mImageViews.length];
    }
}
