package com.tehgan.rssreader.ui.basic.feed;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.tehgan.rssreader.data.Rss;

import java.util.List;

public class FeedViewModel extends ViewModel {
    @Nullable
    private List<Rss> items;

    @Nullable
    public List<Rss> getItems() {
        return items;
    }

    public void setItems(List<Rss> items) {
        this.items = items;
    }
}
