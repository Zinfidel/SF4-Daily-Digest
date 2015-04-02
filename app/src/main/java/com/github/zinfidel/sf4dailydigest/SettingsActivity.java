package com.github.zinfidel.sf4dailydigest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


/** The settings activity, complete with action bar. */
public class SettingsActivity extends ActionBarActivity {

    /** See getCharPrefMap(). */
    private static Map<String, String> charPrefMap = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsFragment()).commit();
    }

    // TODO: DEBUGGING REMOVE ME IMMEDIATELY
    @Override
    protected void onResume() {
        super.onResume();
        MainActivity.prefsChanged = true;
    }

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

    /** Settings fragment for embedding in the Settings Activity. */
    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);

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

            // Add click listeners to the reset buttons.
            Preference turnOff = (Preference) findPreference("turn_off_all_chars_key");
            turnOff.setOnPreferenceClickListener(new PrefCharToggleListener(getActivity(), false));
            Preference turnOn = (Preference) findPreference("turn_on_all_chars_key");
            turnOn.setOnPreferenceClickListener(new PrefCharToggleListener(getActivity(), true));
        }

        /** Listener for the special toggle button preferences. */
        private class PrefCharToggleListener implements Preference.OnPreferenceClickListener {

            private final Context context;
            private final boolean enabled;

            public PrefCharToggleListener(Context context, boolean enabled) {
                this.context = context;
                this.enabled = enabled;
            }

            /** Toggles all character preferences at once. Emits a toast afterwards. */
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor ed = sp.edit();

                // Update the preference values AND the checkbox visual states.
                for (String id : Character.allChars) {
                    String key = getCharPrefMap().get(id);

                    ed.putBoolean(key, enabled);

                    CheckBoxPreference cbp = (CheckBoxPreference) findPreference(key);
                    cbp.setChecked(enabled);
                }
                ed.apply();

                // Display a toast indicating that the operation executed.
                Resources res = context.getResources();
                CharSequence toastText = enabled ? res.getText(R.string.pref_all_chars_on_toast)
                                           : res.getText(R.string.pref_all_chars_off_toast);
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                return true;
            }
        }
    }


}
