package UI;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by אילון on 28/01/2017.
 */
public class FontCreator_Logo extends TextView {

    public FontCreator_Logo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FontCreator_Logo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontCreator_Logo(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "logofont.ttf");
        setTypeface(tf ,1);

    }
}
