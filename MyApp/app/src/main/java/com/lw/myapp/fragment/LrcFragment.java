package com.lw.myapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lw.myapp.R;
import com.lw.myapp.view.LrcView;

/**
 * Created by Lw on 2016/12/16.
 */

public class LrcFragment extends Fragment {
    private View view;
    private LrcView lrcView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.lrc_layout, container, false);
        lrcView = (LrcView) view.findViewById(R.id.lrc_view);

        return view;
    }
}
