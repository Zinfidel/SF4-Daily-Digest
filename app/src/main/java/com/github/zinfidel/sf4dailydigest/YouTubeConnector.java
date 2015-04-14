package com.github.zinfidel.sf4dailydigest;

import android.content.Context;
import android.util.DisplayMetrics;

import com.google.api.client.util.DateTime;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.ThumbnailDetails;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Helper class that creates a YouTube instance with easy-to-use interface for queries.
 * This class is based on the tutorial found here:
 * http://code.tutsplus.com/tutorials/create-a-youtube-client-on-android--cms-22858
 */
public class YouTubeConnector {

    private static String apiKey = null;

    public static Calendar lastSearch = null;
    private static final long HOUR_IN_MIILIS = 60L * 60L * 1000L;

    private static final String KEY_FILE = "youtube_api_key.txt";
    private static final String THUMB_RES_DEF = "default";
    private static final String THUMB_RES_MED = "medium";

    private static final String PARTS = "id, snippet";
    private static final String QUERY_TYPE = "video";
    private static final String ORDER = "date";
    private static final long MAX_RESULTS = 10;
    private static final String FIELDS =
            "items(id/videoId,snippet(title,publishedAt,channelTitle,thumbnails/%s/url))";
    private static final String PREFIX = "USF4 ";

    private YouTube.Search.List query;

    public YouTubeConnector(Context context) {
        apiKey = getKey(context);
        String thumbRes = getThumbRes(context);
        YouTube youTube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), null)
                .setApplicationName(context.getResources().getString(R.string.app_name))
                .build();

        try {
            query = youTube.search().list(PARTS);
            query.setKey(apiKey);
            query.setType(QUERY_TYPE);
            query.setOrder(ORDER);
            query.setMaxResults(MAX_RESULTS);
            lastSearch = Calendar.getInstance();
            query.setPublishedAfter(getYesterday());
            query.setFields(String.format(FIELDS, thumbRes));
        } catch (IOException ex) {
            // TODO: display error message
            System.exit(-1);
        }
    }

    /**
     * Performs a search for the given keywords. "USF4" is automatically prepended to the terms.
     * @param keywords The keywords to search for.
     * @return A list of VideoItem results, or null if YouTube can't be contacted.
     */
    public List<VideoItem> search(String keywords) {
        query.setQ(PREFIX + keywords);
        try {
            SearchListResponse response = query.execute();
            List<SearchResult> results = response.getItems();

            List<VideoItem> items = new ArrayList<>();
            for (SearchResult result : results) {
                VideoItem item = new VideoItem(result);
                items.add(item);
            }
            return items;
        } catch (IOException e) {
            // TODO: display error message about not contacting youtube
            return null;
        }
}

    /**
     * Retrieves the YouTube API developer key from the text file located at
     * srs/main/assets/youtube_api_key.txt.
     * @return YouTube API key.
     */
    private static String getKey(Context context) {
        if (apiKey == null) {
            String key = null;
            try {
                key = IOUtils.toString(context.getAssets().open(KEY_FILE));
            } catch (IOException ex) {
                // Unrecoverable - either the key was not copied to assets, or something went very wrong
                // while accessing the file system. In any case, the application can not continue.
                // TODO: display error message
                System.exit(-1);
            }
            apiKey = key;
        }

        return apiKey;
    }

    /**
     * Determines the size of thumbnail to request from YouTube based on screen pixel density.
     * @param context Application context.
     * @return The query string for the thumbnail size.
     */
    private static String getThumbRes(Context context) {
        int density = context.getResources().getDisplayMetrics().densityDpi;
        return density <= DisplayMetrics.DENSITY_HIGH ? THUMB_RES_DEF : THUMB_RES_MED;
    }

    /**
     * @return Yesterday's date (24 hours ago) in RFC 3339 format.
     */
    private static DateTime getYesterday() {
        Calendar c = Calendar.getInstance();
        c.roll(Calendar.DAY_OF_YEAR, -1);
        return new DateTime(c.getTime());
    }

    /**
     * Returns the difference, in hours, between the supplied publish date and the last search.
     * @param publish The publish date of the video.
     * @return The delta in ours between the publish date and the last search.
     */
    private static int getSearchDelta(long publish) {
        long delta = lastSearch.getTimeInMillis() - publish;
        return (int) (delta / HOUR_IN_MIILIS);
    }


    /** Struct-like class that stores search results in an easy-to-use format. */
    public class VideoItem {

        public final String id;
        public final String title;
        public final String channel;
        public final int published;
        public final String thumbUrl;

        public VideoItem(SearchResult sr) {
            id = sr.getId().getVideoId();
            title = sr.getSnippet().getTitle();
            channel = sr.getSnippet().getChannelTitle();
            published = getSearchDelta(sr.getSnippet().getPublishedAt().getValue());

            ThumbnailDetails thumbs = sr.getSnippet().getThumbnails();
            if (thumbs.getDefault() != null) {
                thumbUrl = thumbs.getDefault().getUrl();
            } else {
                thumbUrl = thumbs.getMedium().getUrl();
            }
        }
    }

}
