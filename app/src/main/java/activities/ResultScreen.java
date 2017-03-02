package activities;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.cdv.amazingmaze.R;

import UI.FontCreator_Buttons;
import UI.FontCreator_Logo;

/**
 * Created by אילון on 26/01/2017.
 */

public class ResultScreen extends Activity {
    private ImageView mImageView, mFireworks1, mFireworks2, mFireworks3, mBlast;
    private AnimationDrawable mAnimator;
    private FontCreator_Buttons mPlayAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_screen);
        mImageView = (ImageView) findViewById(R.id.endImageView);
        mPlayAgain = (FontCreator_Buttons) findViewById(R.id.btn_play_again);
        mPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        boolean isWin = getIntent().getBooleanExtra("player_win",false);
        if (isWin){
            winAnimation();
        }else{
            loseAnimation();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onDestroy();
    }

    public void winAnimation() {
        mImageView.setBackgroundResource(R.mipmap.winner);
        mFireworks1 = (ImageView) findViewById(R.id.fireworks1);
        mFireworks2 = (ImageView) findViewById(R.id.fireworks2);
        mFireworks3 = (ImageView) findViewById(R.id.fireworks3);
        mFireworks1.setBackgroundResource(R.drawable.blue_fire);
        mFireworks2.setBackgroundResource(R.drawable.red_fire);
        mFireworks3.setBackgroundResource(R.drawable.green_fire);
        mAnimator = (AnimationDrawable) mFireworks1.getBackground();
        mAnimator.start();
        mAnimator = (AnimationDrawable) mFireworks2.getBackground();
        mAnimator.start();
        mAnimator = (AnimationDrawable) mFireworks3.getBackground();
        mAnimator.start();
    }

    public void loseAnimation() {
        mImageView.setBackgroundResource(R.mipmap.loser);
        mBlast = (ImageView) findViewById(R.id.blast);
        mBlast.setBackgroundResource(R.drawable.blast);
        mAnimator = (AnimationDrawable) mBlast.getBackground();
        mAnimator.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.mDataBase.SaveCurrentState(MainActivity.mUser);
    }
}
