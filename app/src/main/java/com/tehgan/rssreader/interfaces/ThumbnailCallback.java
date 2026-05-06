package com.tehgan.rssreader.interfaces;

import android.graphics.Bitmap;

public interface ThumbnailCallback {
    /**
     * @param bmp The downloaded image file
     */
    void onThumbnailDownloaded(Bitmap bmp);
}
