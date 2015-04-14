package com.github.zinfidel.sf4dailydigest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;


/** The settings activity, complete with action bar. */
public class SettingsActivity extends PreferenceActivity {

    /** See getCharPrefMap(). */
    private static Map<String, String> charPrefMap = null;

    /** Returns a mapping of character ids to preference keys.*/
    public static Map<String, String> getCharPrefMap() {
        // Initialize the singleton map if it doesn't exist yet.
        if (charPrefMap == null) {
            charPrefMap = new HashMap<>();
            for (String id : Character.allChars) {
                String key = id + "_enabled_key";
                charPrefMap.put(id, key);
            }
        }

        return charPrefMap;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    public void onHeaderClick(@Nonnull Header header, int position) {
        super.onHeaderClick(header, position);

        if (header.id == R.id.turn_on_all_chars) {
            ToggleChars(getApplicationContext(), true);
        }
        else if (header.id == R.id.turn_off_all_chars) {
            ToggleChars(getApplicationContext(), false);
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return CharSelectFragment.class.getName().equals(fragmentName);
    }

    /** Toggles all character preference_char_select at once. Emits a toast afterwards. */
    private boolean ToggleChars(Context context, boolean enabled) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();

        // Update the preference values.
        for (String id : Character.allChars) {
            String key = getCharPrefMap().get(id);
            ed.putBoolean(key, enabled);
        }
        ed.apply();

        // Display a toast indicating that the operation executed.
        Resources res = context.getResources();
        CharSequence toastText = enabled ? res.getText(R.string.pref_all_chars_on_toast)
                : res.getText(R.string.pref_all_chars_off_toast);
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        return true;
    }

    /** Settings fragment for that holds the character select checkboxes. */
    public static class CharSelectFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_char_select);

            // Dynamically add checkboxes with character names and icon.
            PreferenceScreen prefChars = (PreferenceScreen) findPreference("button_characters_key");
            for (String id : Character.allChars) {
                Character c = Character.get(id);
                CheckBoxPreference cbp = new CheckBoxPreference(getActivity());
                cbp.setKey(getCharPrefMap().get(id));
                cbp.setTitle(c.name);
                cbp.setIcon(c.icon);
                cbp.setDefaultValue(true);
                prefChars.addPreference(cbp);
            }
        }
    }
}
