package com.tehgan.rssreader.interfaces;

import com.tehgan.rssreader.data.Rss;

import java.util.List;

public interface RssCallback {
    /**
     * @param progress An integer ranging from 0-100
     */
    void onProgressUpdate(int progress);

    /**
     * @param channel Name of RSS channel (e.g. 'BBC News')
     * @param items The retrieved RSS items (articles)
     */
    void onRssFetched(String channel, List<Rss> items);

    /**
     * @param error One of the following exceptions:<br>
     *  <code>XmlPullParserException</code>: URL exists but isn't a valid RSS feed.<br>
     * <code>UnknownHostException</code>: Either the user has no internet connection, or the URL doesn't exist.<br>
     * <code>MalformedURLException</code>: Provided RSS URL is invalid.<br>
     * <code>SSLHandshakeException</code>: User's date/time is out-of-sync<br>
     * <code>SocketTimeoutException</code>: No response from RSS feed within RssParser-specified timeout limit<br>
     * <code>IOException</code>: General I/O exception.
     */
    void onError(String error);
}
