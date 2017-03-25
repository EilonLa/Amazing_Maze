package UI;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * The FontCreator_Buttons is a factory for fonts
 *
 */
public class FontCreator_Buttons extends TextView {

    public FontCreator_Buttons(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FontCreator_Buttons(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontCreator_Buttons(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "button_font5.ttf");
        setTypeface(tf ,1);
    }
}
