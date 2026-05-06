package com.tehgan.rssreader.interfaces;

public interface TitleCallback {
    /**
     *  @param title The RSS feed's channel value (e.g. "BBC News")
     */
    void onTitleUpdate(String title);
}
