package com.tehgan.rssreader.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.tehgan.rssreader.data.entity.RssEntity;

import java.util.List;

@Dao
public interface RssDao {
    @Query("SELECT * FROM favourites")
    LiveData<List<RssEntity>> getAll();

    /**
     * Room's standard 'delete' method doesn't work as data-to-entity conversion doesn't take into account IDs;<br>
     * this method looks up a link (as it's the second-most unique identifier) and deletes the associated record from the table.
     * @param link The Rss object's link (retrieved via getLink())
     */
    @Query("DELETE FROM favourites WHERE link = :link")
    void deleteViaLink(String link);

    @Insert
    void insert(RssEntity item);
}
