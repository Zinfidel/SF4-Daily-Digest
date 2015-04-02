package com.github.zinfidel.sf4dailydigest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;


/** A vertical bar populated by CharacterImageButtons. The buttons are generated via the
 * characters.xml file and a preference list. */
public class ButtonBarView extends ScrollView {

    /** Currently selected character. */
    private Character selectedChar = null;

    /** Singular listener for character changed events. */
    private CharacterChangedListener listener = null;

    /**
     * Stores all of the buttons so that they can be inserted and removed from the button bar
     * easily. This is needed becaues preference changes will cause the entire
     */
    private Map<String, CharacterImageButton> charButtons = new HashMap<>();

    private Handler handler = new Handler();


    public ButtonBarView(Context context) {
        super(context);
        init(context);
    }

    public ButtonBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ButtonBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    /**
     * Initialize the button bar by creating and adding the character buttons.
     * @param context The activity context this bar belongs to.
     */
    private void init(Context context) {
        charButtons = new HashMap<>();
        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_buttonbar, this, true);
        LinearLayout buttonsContainer = (LinearLayout) findViewById(R.id.buttons_container);

        // Create all of the character buttons and store them. We don't add them to the button
        // bar yet because which ones are added is based on preferences.
        for (String id : Character.allChars) {
            CharacterImageButton cb = (CharacterImageButton)
                    inflater.inflate(R.layout.button_character, buttonsContainer, false);

            // Set the button's character, the icon (image), and the click event.
            cb.character = Character.get(id);
            cb.setImageResource(cb.character.icon);
            cb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Sets this button bar's character field to the field of the button clicked.
                    CharacterImageButton cib = (CharacterImageButton) v;
                    setSelectedChar(cib.character);
                }
            });

            charButtons.put(id, cb);
        }

        // Now add buttons to the bar based on preferences. Do so on the GUI thread!
        handler.post(new Runnable() {
            @Override
            public void run() {
                PopulateBar();
            }
        });
    }

    /**
     * Populates the button bar with buttons based on preferences.
     * WARNING: This MUST be called on the GUI thread!
     */
    public void PopulateBar() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        LinearLayout buttonsContainer = (LinearLayout) findViewById(R.id.buttons_container);

        // Add buttons that are enabled in preferences.
        for (String id : Character.allChars) {
            String key = SettingsActivity.getCharPrefMap().get(id);
            boolean isEnabled = sp.getBoolean(key, true);
            if (isEnabled) {
                CharacterImageButton cib = charButtons.get(id);

                // Completely clear out any button's parent to prevent old button bars from causing
                // exceptions by holding onto the buttons.
                if (cib.getParent() != null) {
                    ViewGroup parent = (ViewGroup) cib.getParent();
                    parent.removeAllViews();
                }
                buttonsContainer.addView(charButtons.get(id));
            }
        }

        // Invalidate the view to ensure the GUI responds to the new set of buttons.
        invalidate();
    }

    /**
     * Sets the listener for the character changed events. Events are fired in the
     * selected character setter method.
     * @param l The listener to register.
     */
    public void setOnCharacterChangedListener(CharacterChangedListener l) {
        listener = l;
    }

    /** Get the currently selected character of this bar. */
    public Character getSelectedChar() {
        return selectedChar;
    }

    /**
     * Set the currently selected character of this bar.
     * @param c The character to set.
     */
    private void setSelectedChar(Character c) {
        selectedChar = c;
        listener.onCharacterChanged(c);
    }

    /** Listener interface for detecting when the selected character changes. */
    public interface CharacterChangedListener extends EventListener {
        public void onCharacterChanged(Character c);
    }
}
