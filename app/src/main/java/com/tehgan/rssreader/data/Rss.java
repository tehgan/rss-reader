package com.tehgan.rssreader.data;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

// Functional for our use-case (Navigation argument) without implementing the CREATOR field
@SuppressLint("ParcelCreator")
public class Rss implements Parcelable {
    private final String title;
    private final String description;
    private final String link;
    private final String pubDate;
    private final String thumbnailRawUrl;

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLink() { return link; }
    public String getPubDate() { return pubDate; }
    public String getThumbnailRawUrl() { return thumbnailRawUrl; }

    public Rss(
            String title,
            String description,
            String link,
            String pubDate,
            String thumbnailRawUrl
    ) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubDate = pubDate;
        this.thumbnailRawUrl = thumbnailRawUrl;
    }

    protected Rss(Parcel p) {
        title = p.readString();
        description = p.readString();
        link = p.readString();
        pubDate = p.readString();
        thumbnailRawUrl = p.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(link);
        dest.writeString(pubDate);
        dest.writeString(thumbnailRawUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
