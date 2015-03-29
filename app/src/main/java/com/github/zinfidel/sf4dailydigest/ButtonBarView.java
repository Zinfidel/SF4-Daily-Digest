package com.github.zinfidel.sf4dailydigest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.EventListener;


/**
 * TODO: document your custom view class.
 */
public class ButtonBarView extends HorizontalScrollView {

    /** Currently selected character. */
    private Character selectedChar = null;

    /** Singular listener for character changed events. */
    private CharacterChangedListener listener = null;


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
        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_buttonbar, this, true);

        // TODO: MAKE THIS BASED ON PREFERENCES
        // Create and add the character buttons to the button bar's linear layout..
        LinearLayout buttons_container = (LinearLayout) findViewById(R.id.buttons_container);
        for (String name : Character.defaultCharList) {
            CharacterImageButton cb = (CharacterImageButton)
                    inflater.inflate(R.layout.button_character, buttons_container, false);

            // Set the button's character, the icon (image), and the click event.
            cb.character = Character.get(name);
            cb.setImageResource(cb.character.icon);
            cb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Sets this button bar's character field to the field of the button clicked.
                    CharacterImageButton cib = (CharacterImageButton) v;
                    setSelectedChar(cib.character);
                }
            });

            buttons_container.addView(cb);
        }
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
