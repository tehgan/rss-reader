package com.tehgan.rssreader.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.tehgan.rssreader.interfaces.RssCallback;
import com.tehgan.rssreader.data.Rss;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Contains functions for downloading and parsing the RSS feeds, tailored towards the BBC's US/CA feed.
// Adapted from https://developer.android.com/develop/connectivity/network-ops/xml
public class RssParser extends AsyncTask<String, Integer, List<Rss>> {
    private final String TITLE_TAG = "title";
    private final String DESC_TAG = "description";
    private final String LINK_TAG = "link";
    private final String DATE_TAG = "pubDate";
    // Specified in media RSS specification
    // https://www.rssboard.org/media-rss#media-thumbnails
    private final String THUMBNAIL_TAG = "media:thumbnail";


    private final String rssRawUrl;
    private final WeakReference<RssCallback> callback;

    private int progress = 0;

    // If result is empty this indicates a success; otherwise, an exception has occurred.
    private String result = "";
    private String channel = "";

    public RssParser(String rssRawUrl, RssCallback callback) {
        this.rssRawUrl = rssRawUrl;
        this.callback = new WeakReference<>(callback);
    }

    @Override
    protected List<Rss> doInBackground(String... strings) {
        List<Rss> feed = getFeed();
        // Feed's been retrieved, set progress to 100%
        progress = 100;
        publishProgress(progress);
        return feed;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        RssCallback rc = callback.get();
        if (rc != null) {
            rc.onProgressUpdate(values[0]);
        }
    }

    @Override
    protected void onPostExecute(List<Rss> rssData) {
        RssCallback rc = callback.get();
        if (rc != null) {
            if (result.isEmpty()) {
                rc.onRssFetched(channel, rssData);
            } else {
                rc.onError(result);
            }
        }
        result = "";
        progress = 0;
    }

    private List<Rss> getFeed() {
        try {
            URL url = new URL(rssRawUrl);
            URLConnection urlConnection = url.openConnection();
            // Set a first-connection timeout of 3 seconds to prevent this task from running forever
            urlConnection.setConnectTimeout(3000);
            InputStream in = urlConnection.getInputStream();
            progress = 10;
            publishProgress(progress);

            // Create and initialize XML parser
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            // Skip over XML versioning tag
            parser.nextTag();
            // Skip over RSS metadata tag
            parser.nextTag();
            return parseFeed(parser);
        } catch (Exception e) {
            e.printStackTrace();
            // Set result to exception type; "XmlPullParserException", "UnknownHostException", etc.
            result = e.getClass().getSimpleName();
            return Collections.emptyList();
        }
    }

    private List<Rss> parseFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Rss> items = new ArrayList<>();
        progress = 20;
        publishProgress(progress);

        parser.require(XmlPullParser.START_TAG, null, "channel");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                // Channel title
                channel = parseTag(parser, "title");
            } else if (name.equals("item")) {
                // Item tag denotes an RSS entry (e.g. an article)
                if (progress < 80) {
                    progress += 5;
                    publishProgress(progress);
                }
                Rss rss = parseItem(parser);
                if (isPopulated(rss)) {
                    items.add(rss);
                }
            } else {
                skip(parser);
            }
        }
        return items;
    }

    private Rss parseItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "item");

        String title = "";
        String description = "";
        String link = "";
        String pubDate = "";
        String thumbnailRawUrl = "";

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case TITLE_TAG:
                    title = parseTag(parser, TITLE_TAG);
                    break;
                case DESC_TAG:
                    description = parseTag(parser, DESC_TAG);
                    break;
                case LINK_TAG:
                    link = parseTag(parser, LINK_TAG);
                    break;
                case DATE_TAG:
                    pubDate = parseTag(parser, DATE_TAG);
                    break;
                case THUMBNAIL_TAG:
                    thumbnailRawUrl = parseThumbnail(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new Rss(title, description, link, pubDate, thumbnailRawUrl);
    }

    /**
     * Parses and returns the value of <code>tag</code>.
     * @param parser The XmlPullParser (no namespace processing, RSS feed InputStream)
     * @param tag The name of the tag/element to parse ("title", "link", etc.)
     * @return The value of the tag
     */
    private String parseTag(XmlPullParser parser, String tag) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, tag);
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, null, tag);
        return result;
    }

    /**
     * Whereas most tags only contain one attribute, the media:thumbnail tag contains several:<br>
     * * height<br>
     * * width<br>
     * * url (what we need)
     * @param parser The XmlPullParser (no namespace processing, RSS feed InputStream)
     * @return The thumbnail's URL
     */
    private String parseThumbnail(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, THUMBNAIL_TAG);
        String url = parser.getAttributeValue(null, "url");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, null, THUMBNAIL_TAG);
        return url;
    }

    // Skips over irrelevant tags.
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            Log.w("skip", "getEventType != START_TAG");
            Log.w("skip", parser.getEventType() + ", " + XmlPullParser.START_TAG);
            Log.d("skip", "parser name == " + parser.getName());
            IllegalStateException ils = new IllegalStateException();
            ils.printStackTrace();
            throw ils;
        }
        int depth = 1;
        while (depth != 0) {
            int nextRes = parser.next();
            if (nextRes == XmlPullParser.END_TAG) {
                depth--;
            } else if (nextRes == XmlPullParser.START_TAG) {
                depth++;
            }
        }
    }

    /**
     * Checks whether an RSS object is empty or not.
     * @param rss The RSS object to check
     * @return <code>true</code> if the RSS object contains any populated fields,
     * <code>false</code> otherwise.
     */
    private boolean isPopulated(Rss rss) {
        return !rss.getLink().isBlank()
                || !rss.getTitle().isBlank()
                || !rss.getDescription().isBlank()
                || !rss.getPubDate().isBlank()
                || !rss.getThumbnailRawUrl().isBlank();
    }
}
