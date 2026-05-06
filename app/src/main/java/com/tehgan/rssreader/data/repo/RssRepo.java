package com.tehgan.rssreader.data.repo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.tehgan.rssreader.data.Rss;
import com.tehgan.rssreader.data.db.RssDatabase;
import com.tehgan.rssreader.data.db.dao.RssDao;
import com.tehgan.rssreader.data.entity.RssEntity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RssRepo {
    private final RssDao rssDao;
    private final LiveData<List<RssEntity>> items;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public RssRepo(Application application) {
        RssDatabase db = RssDatabase.getDatabase(application.getApplicationContext());
        rssDao = db.rssDao();
        items = rssDao.getAll();
    }

    public void insert(RssEntity entity) {
        executor.execute(() -> rssDao.insert(entity));
    }

    public void deleteViaLink(String link) {
        executor.execute(() -> rssDao.deleteViaLink(link));
    }

    public LiveData<List<RssEntity>> getAll() {
        return items;
    }

    public Rss entityToData(RssEntity entity) {
        return new Rss(
                entity.title,
                entity.description,
                entity.link,
                entity.pubDate,
                entity.thumbnailRawUrl
        );
    }

    public RssEntity dataToEntity(Rss data) {
        return new RssEntity(
                data.getTitle(),
                data.getDescription(),
                data.getLink(),
                data.getPubDate(),
                data.getThumbnailRawUrl()
        );
    }
}
