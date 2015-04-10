package com.github.zinfidel.sf4dailydigest;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private Handler handler;
    private ButtonBarView bar;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the character objects. This method can be called multiple times safely.
        try {
            Character.LoadCharacters(getApplication().getResources());
        } catch (Exception e) {
            //TODO: Actual handling
            System.exit(-1);
        }

        setContentView(R.layout.activity_main);

        handler = new Handler();
        bar = (ButtonBarView) findViewById(R.id.button_bar);
        list = (ListView) findViewById(R.id.list_view);

        // Attach a handler to the button bar character changed event that automatically searches
        // YouTube and updates the ListView adapter when it gets results.
        bar.setOnCharacterChangedListener(new ButtonBarView.CharacterChangedListener() {
            @Override
            public void onCharacterChanged(Character c) {
                // Send the first search term in the characters search field.
                Character character = bar.getSelectedChar();
                searchYouTube(character.search.get(0));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The only activity it could be is settings. Regardless of the return code, rebuild
        // the button bar to ensure it is synced with the user settings.
        ButtonBarView bb = (ButtonBarView) findViewById(R.id.button_bar);
        bb.PopulateBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a thread and executes a YouTube search for the given keywords. The GUI-thread
     * handler receives a post to update the listview when the search returns.
     * @param keywords The keywords to search (character search term).
     */
    private void searchYouTube(final String keywords) {
        new Thread() {
            public void run() {
                Context context = MainActivity.this.getApplicationContext();
                YouTubeConnector yc = new YouTubeConnector(context);
                final List<YouTubeConnector.VideoItem> results = yc.search(keywords);

                handler.post(new Runnable() {
                    public void run() {
                        list.setAdapter(new CharListViewAdapter(results));
                    }
                });
            }
        }.start();
    }
}
