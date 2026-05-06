package com.tehgan.rssreader.ui.basic.favourites;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.tehgan.rssreader.data.Rss;
import com.tehgan.rssreader.data.repo.RssRepo;

import java.util.ArrayList;
import java.util.List;

/* ViewModel is required to map RssEntity elements to Rss (data),
 *  while still maintaining the auto-update benefit of LiveData. */
public class FavouritesViewModel extends AndroidViewModel {
    private final RssRepo repo;

    private final LiveData<List<Rss>> items;

    public FavouritesViewModel(@NonNull Application application) {
        super(application);
        repo = new RssRepo(getApplication());
        items = Transformations.map(repo.getAll(), rssEntities -> {
            List<Rss> list = new ArrayList<>(rssEntities.size());
            for (int i = 0; i < rssEntities.size(); i++) {
                list.add(repo.entityToData(rssEntities.get(i)));
            }
            return list;
        });
    }

    public LiveData<List<Rss>> getAll() {
        return items;
    }
}
