package com.tehgan.rssreader.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// Index ensures all links must be unique, meaning no duplicate items/articles can be added to favourites.
@Entity(tableName = "favourites", indices = {@Index(value = {"link"}, unique = true)})
public class RssEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "link")
    public String link;

    @ColumnInfo(name = "pub_date")
    public String pubDate;

    @ColumnInfo(name = "thumbnail_url")
    public String thumbnailRawUrl;

    public RssEntity(String title, String description, String link, String pubDate, String thumbnailRawUrl) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubDate = pubDate;
        this.thumbnailRawUrl = thumbnailRawUrl;
    }
}
