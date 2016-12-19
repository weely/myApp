package com.lw.myapp.model;

/**
 * Created by Lw on 2016/12/16.
 */

public class LrcInfo {
    private String lrcContent;
    private int lrcTime;

    public LrcInfo() {
    }

    public LrcInfo(String lrcContent, int lrcTime) {
        this.lrcContent = lrcContent;
        this.lrcTime = lrcTime;
    }

    public String getLrcContent() {

        return lrcContent;
    }

    public void setLrcContent(String lrcContent) {
        this.lrcContent = lrcContent;
    }

    public int getLrcTime() {
        return lrcTime;
    }

    public void setLrcTime(int lrcTime) {
        this.lrcTime = lrcTime;
    }
}
