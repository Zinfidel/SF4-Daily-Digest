package com.github.zinfidel.sf4dailydigest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class CharListViewAdapter extends BaseAdapter {

    private List<YouTubeConnector.VideoItem> items;

    public CharListViewAdapter(List<YouTubeConnector.VideoItem> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();
        YouTubeConnector.VideoItem item = items.get(position);

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
        Picasso.with(context)
                .load(item.thumbUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(img);

        // Populate the title.
        TextView title = (TextView) listItem.findViewById(R.id.listitem_title);
        title.setText(item.title);

        // Populate the channel ID.
        TextView channel = (TextView) listItem.findViewById(R.id.listitem_channel);
        channel.setText(String.format("by %s", item.channel));

        // Populate the "hours ago" timestamp.
        TextView published = (TextView) listItem.findViewById(R.id.listitem_published);
        published.setText(String.format("%d hours ago", item.published));

        // Set the onclick listener.
        final String id = item.id;
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
}
