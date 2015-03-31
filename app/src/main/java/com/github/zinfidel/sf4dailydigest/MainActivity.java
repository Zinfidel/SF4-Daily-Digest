package com.github.zinfidel.sf4dailydigest;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: DEBUGGING CRAP
        try {
            Character.LoadCharacters(getApplication().getResources());
        } catch (Exception e) {
            //TODO: Actual handling
            e.printStackTrace();
            System.exit(-1);
        }
        Exception ex = null;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButtonBarView bar = (ButtonBarView) findViewById(R.id.button_bar);
        bar.setOnCharacterChangedListener(new ButtonBarView.CharacterChangedListener() {
            @Override
            public void onCharacterChanged(Character c) {
                // TODO: Actual handler
                System.out.println(c.name);
            }
        });

        JSONArray test = getTestJSON();
        ListView lv = (ListView) findViewById(R.id.list_view);
        lv.setAdapter(new CharListViewAdapter(test));
    }

    //TODO: JSON DEBUGGING TEST CRAP
    private JSONArray getTestJSON() {
        String content = null;
        InputStream file = getResources().openRawResource(R.raw.test);
        try {
            content = IOUtils.toString(file);
        } catch (IOException e) {
            System.out.println("Error loading JSON file.");
        }

        JSONArray test = null;
        try  {
            JSONObject root = new JSONObject(content);
            test = root.getJSONArray("items");
        } catch (JSONException e) {
            System.out.println("Error parsing JSON file.");
        }

        return test;
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
