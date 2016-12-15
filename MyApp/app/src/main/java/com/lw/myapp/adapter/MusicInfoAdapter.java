package com.lw.myapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lw.myapp.R;
import com.lw.myapp.model.MusicInfo;
import com.lw.myapp.util.MusicUtil;

import java.util.List;

/**
 * Created by Lw on 2016/12/8.
 */

public class MusicInfoAdapter extends BaseAdapter {
    private List<MusicInfo> lists;
    private Context context;
    private int selectItem = -1;

    public MusicInfoAdapter(List<MusicInfo> lists, Context context) {
        this.lists = lists;
        this.context = context;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.music_itemview, null);
            holder.img = (ImageView) convertView.findViewById(R.id.image);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.artist = (TextView) convertView.findViewById(R.id.singer);
            holder.duration = (TextView) convertView.findViewById(R.id.duration);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MusicInfo musicInfo = lists.get(position);
        holder.title.setText(musicInfo.getTitle());
        holder.artist.setText(musicInfo.getArtist());
        holder.duration.setText(MusicUtil.formatTime(musicInfo.getDuration()));

        /*long songId = musicInfo.getId();
        long albumId = Long.parseLong(musicInfo.getAlbumId());
        holder.img.setImageBitmap(MusicUtil.getArtwork(context, songId, albumId, true, true));*/

        Bitmap bm = MusicUtil.getAlbumArt(musicInfo.getAlbumId(), context);
        if (bm == null) {
            holder.img.setImageResource(R.mipmap.ic_launcher);
        } else {
            BitmapDrawable bmpDraw = new BitmapDrawable(null, bm);
            holder.img.setImageDrawable(bmpDraw);
        }
        if (position == selectItem) {
            convertView.setBackgroundColor(Color.argb(66, 66, 66, 66));
        } else {
            convertView.setBackgroundColor(Color.argb(55, 00, 250, 250));
        }

        return convertView;
    }

    private void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    class ViewHolder {
        protected ImageView img;
        protected TextView title;
        protected TextView artist;
        protected TextView duration;
    }
}
