package com.tehgan.rssreader.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.tehgan.rssreader.data.db.dao.RssDao;
import com.tehgan.rssreader.data.entity.RssEntity;

@Database(entities = {RssEntity.class}, version = 2, exportSchema = false)
public abstract class RssDatabase extends RoomDatabase {
    public abstract RssDao rssDao();

    /* Singleton code is employed to improve application performance;
     *  creating a new database whenever one of the Favourites fragments are opened
     *   would be horribly expensive! */
    // Adapted from https://developer.android.com/codelabs/android-room-with-a-view#7
    public static volatile RssDatabase INSTANCE;

    /**
     * Returns a singleton instance of the RssDatabase (favourites), creates it if it doesn't already exist.
     * @param context Required to initialize the database in case it hasn't already been initialized.
     * @return The RssDatabase
     */
    public static RssDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RssDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            RssDatabase.class,
                            "rss_database"
                    ).fallbackToDestructiveMigration(true).build();
                    // A proper migration strategy would be best in a real-world application
                }
            }
        }
        return INSTANCE;
    }
}
