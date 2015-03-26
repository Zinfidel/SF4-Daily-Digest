package com.github.zinfidel.sf4dailydigest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;


/**
 * TODO: document your custom view class.
 */
public class ButtonBarView extends HorizontalScrollView {
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

    private void init(Context context) {
        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.view_buttonbar, this, true);

        LinearLayout buttons_container = (LinearLayout) findViewById(R.id.buttons_container);

        //TODO: Make map that maps strings to R ints instead of reflection to get file names.
        int[] res = new int[] {R.drawable.abel, R.drawable.chunli, R.drawable.dhalsim};
        for (int id : res) {
            ImageButton cb = (ImageButton)
                    inflater.inflate(R.layout.button_character, buttons_container, false);

            cb.setImageResource(id);
            buttons_container.addView(cb);
        }
    }
}

//    /**
//     * Gets the example string attribute value.
//     *
//     * @return The example string attribute value.
//     */
//    public String getExampleString() {
//        return mExampleString;
//    }
//
//    /**
//     * Sets the view's example string attribute value. In the example view, this string
//     * is the text to draw.
//     *
//     * @param exampleString The example string attribute value to use.
//     */
//    public void setExampleString(String exampleString) {
//        mExampleString = exampleString;
//        invalidateTextPaintAndMeasurements();
