package com.tehgan.rssreader.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.tehgan.rssreader.interfaces.ThumbnailCallback;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

public class ThumbnailDownloader extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ThumbnailCallback> callback;

    public ThumbnailDownloader(ThumbnailCallback callback) {
        this.callback = new WeakReference<>(callback);
    }

    @Override
    @Nullable
    protected Bitmap doInBackground(String... strings) {
        try {
            String rawUrl = strings[0];
            if (rawUrl.isEmpty()) {
                return null;
            }
            URL url = new URL(rawUrl);
            URLConnection connection = url.openConnection();
            // Wait 3 seconds to establish a connection.
            /* if a connection cannot be established,
             *  either the user's internet is down or the thumbnail URL has been removed. */
            connection.setConnectTimeout(3000);
            return BitmapFactory.decodeStream(connection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        ThumbnailCallback tc = callback.get();
        if (tc != null) {
            tc.onThumbnailDownloaded(bitmap);
        }
    }
}
