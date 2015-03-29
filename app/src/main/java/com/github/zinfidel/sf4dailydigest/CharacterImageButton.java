package com.github.zinfidel.sf4dailydigest;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class CharacterImageButton extends ImageButton {

    /** Character that this button represents. */
    public Character character = null;

    public CharacterImageButton(Context context) {
        super(context);
    }

    public CharacterImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CharacterImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
