package UI;

import android.app.Activity;
import android.content.ClipData;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.cdv.amazingmaze.R;

/**
 * Created by אילון on 28/01/2017.
 */
public class ToolbarActivity extends AppCompatActivity {

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LayoutInflater inflater = LayoutInflater.from(this);
        View contentView = inflater.inflate(layoutResID, null);

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        layout.addView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ImageView options = new ImageView(this);
        options.setImageResource(R.mipmap.options);
        layout.addView(options);
    }
}
