package com.github.zinfidel.sf4dailydigest;

import android.content.Context;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class that creates a YouTube instance with easy-to-use interface for queries.
 * This class is based on the tutorial found here:
 * http://code.tutsplus.com/tutorials/create-a-youtube-client-on-android--cms-22858
 */
public class YouTubeConnector {

    private static YouTube instance = null;

    private static final String PARTS = "id, snippet";
    private static final String QUERY_TYPE = "video";
    private static final long MAX_RESULTS = 5;
    private static final String FIELDS = "items(id/videoId,snippet(title,thumbnails(default(url))))";
    private static final String PREFIX = "USF4";

    private static String apiKey = null;
    private final YouTube youTube;
    private YouTube.Search.List query;

    public YouTubeConnector(Context context) {
        apiKey = getKey(context);
        youTube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), null)
                .setApplicationName(context.getResources().getString(R.string.app_name))
                .build();

        try {
            query = youTube.search().list(PARTS);
            query.setKey(apiKey);
            query.setType(QUERY_TYPE);
            query.setMaxResults(MAX_RESULTS);
            query.setFields(FIELDS);
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
    private static final String getKey(Context context) {
        if (apiKey == null) {
            String key = null;
            try {
                key = IOUtils.toString(context.getAssets().open("youtube_api_key.txt"));
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

    /** Struct-like class that stores search results in an easy-to-use format. */
    public class VideoItem {
        public final String id;
        public final String title;
        public final String thumbUrl;

        public VideoItem(SearchResult sr) {
            id = sr.getId().getVideoId();
            title = sr.getSnippet().getTitle();
            thumbUrl = sr.getSnippet().getThumbnails().getDefault().getUrl();
        }
    }

}
