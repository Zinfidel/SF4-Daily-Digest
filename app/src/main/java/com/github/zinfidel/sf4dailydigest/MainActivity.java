package com.github.zinfidel.sf4dailydigest;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;


public class MainActivity extends Activity {

    private Handler handler;
    private ButtonBarView bar;
    private ListView list;
    private ProgressBar spinner;

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
        spinner = (ProgressBar) findViewById(R.id.spinner);

        // Attach a handler to the button bar character changed event that automatically searches
        // YouTube and updates the ListView adapter when it gets results.
        bar.setOnCharacterChangedListener(new ButtonBarView.CharacterChangedListener() {
            @Override
            public void onCharacterChanged(Character c) {
                // Send the first search term in the characters search field.
                Character character = bar.getSelectedChar();
                searchYouTube(character);
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
     * @param c The character object to use in the search.
     */
    private void searchYouTube(final Character c) {
        new Thread() {
            public void run() {
                Context context = MainActivity.this.getApplicationContext();
                final ActionBar actionBar = getActionBar();
                final String update = context.getResources().getString(R.string.status_searching);
                final String done = context.getResources().getString(R.string.status_done);
                final String keywords = c.search.get(0);
                final String name = c.name;

                // Turn the content area into a plain gray box with a progress spinner by doing:
                //   + Set the action bar title to a search message.
                //   + Turn off the listview background tutorial image
                //   + Remove the current listview adapter (removes list items)
                //   + Set the progress spinner visibility to visible
                if (actionBar != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            actionBar.setTitle(String.format(update, c.name));
                            list.setBackgroundResource(0);
                            list.setAdapter(null);
                            spinner.setVisibility(View.VISIBLE);
                        }
                    });
                }

                // Perform the search.
                YouTubeConnector yc = new YouTubeConnector(context);
                final List<YouTubeConnector.VideoItem> results = yc.search(keywords);


                // Post a search query that updates the listview and its adapter when it returns.
                handler.post(new Runnable() {
                    public void run() {
                        // Post the results to the content area. Do this by:
                        //   + Setting the list adapter to the results returned.
                        //   + Updating the background. It will be blank if there are results,
                        //     otherwise it will display the "no videos" tutorial image.
                        //   + Turn off the progress spinner visibility.
                        //   + Set the action bar title to a search post-mortem status.
                        CharListViewAdapter adapter = new CharListViewAdapter(results);
                        list.setAdapter(adapter);
                        updateBackground(adapter);
                        spinner.setVisibility(View.GONE);
                        if (actionBar != null) {
                            actionBar.setTitle(String.format(done, c.name));
                        }
                    }
                });
            }
        }.start();
    }

    /**
     * Display a message via background if no search results were returned, otherwise
     * remove the background image if there is one.
     * @param adapter The listview adapter generated from a search query.
     */
    private void updateBackground(CharListViewAdapter adapter) {
        if (adapter.isEmpty()) {
            list.setBackgroundResource(R.drawable.none_found_bg);
        } else {
            list.setBackgroundResource(0);
        }
    }
}
