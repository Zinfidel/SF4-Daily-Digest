package com.github.zinfidel.sf4dailydigest;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CharListViewAdapter extends BaseAdapter {

    private JSONArray items;

    public CharListViewAdapter(JSONArray items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.length();
    }

    @Override
    public Object getItem(int position) {
        JSONObject item = null;
        try {
            item = items.getJSONObject(position);
        } catch (JSONException ex) {
            System.exit(-1);
        }

        return item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();
        JSONObject item = null;
        try {
            item = items.getJSONObject(position);
        } catch (JSONException ex) {
            System.exit(-1);
        }

        // If convertView is not null, then it means we can reuse it to display this item and we
        // don't need to create a new view. Otherwise inflate a new video_listitem.
        View listItem = null;
        if (convertView != null) {
            listItem = convertView;
        } else {
            LayoutInflater inflater =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItem = inflater.inflate(R.layout.video_listitem, parent, false);
        }

        // Populate the thumbnail asynchronously using the Picasso library. The placeholder image
        // should provide the correct relative layout size for the listview view to use.
        ImageView img = (ImageView) listItem.findViewById(R.id.listitem_thumbnail);

        // TODO: select correct resolution for screen size.
//        Picasso.with(context).load(getThumbURL(position)).into(img);
        Picasso.with(context)
                .load(R.drawable.test_thumbnail)
                .placeholder(R.drawable.placeholder)
                .into(img);
//        Picasso.with(context).load("http://i.imgur.com/UnYXFSM.png").into(img);

        // Populate the textview with the video's title.
        TextView tv = (TextView) listItem.findViewById(R.id.listitem_title);
        tv.setText(getTitle(position));

        // Set the onclick listener.
        final String id = getId(position);
        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send an intent to open a youtube weblink.
                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(v.getResources().getString(R.string.youtube_search) + id));
                Activity a = (Activity) v.getContext();
                a.startActivity(intent);
            }
        });

        return listItem;
    }

    // TODO: DEBUG DELETE THIS - HELPER FUNC
    private String getThumbURL(int position) {
        String url = null;
        try {
            JSONObject item = items.getJSONObject(position);
            JSONObject snippet = item.getJSONObject("snippet");
            JSONObject thumbs = snippet.getJSONObject("thumbnails");
            JSONObject def = thumbs.getJSONObject("default");
            url = def.getString("url");

        } catch (JSONException ex) {
            System.exit(-1);
        }

        return url;
    }

    // TODO: DEBUG DELETE THIS - HELPER FUNC
    private String getId(int position) {
        String vidId = null;
        try {
            JSONObject item = items.getJSONObject(position);
            JSONObject id = item.getJSONObject("id");
            vidId = id.getString("videoId");

        } catch (JSONException ex) {
            System.exit(-1);
        }

        return vidId;
    }

    // TODO: DEBUG DELETE THIS - HELPER FUNC
    private String getTitle(int position) {
        String title = null;
        try {
            JSONObject item = items.getJSONObject(position);
            JSONObject snippet = item.getJSONObject("snippet");
            title = snippet.getString("title");

        } catch (JSONException ex) {
            System.exit(-1);
        }

        return title;
    }
}
