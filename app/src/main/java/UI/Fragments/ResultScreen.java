package UI.Fragments;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.app.Fragment;


import com.example.cdv.amazingmaze.R;

import UI.Board;
import UI.FontCreator_Buttons;
import UI.FontCreator_Logo;
import activities.MainActivity;

/**
 * Created by אילון on 26/01/2017.
 */

public class ResultScreen extends Fragment {
    private ImageView mImageView, mFireworks1, mFireworks2, mFireworks3, mBlast;
    private AnimationDrawable mAnimator;
    private FontCreator_Buttons mPlayAgain;
    private MainActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.result_screen, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity )getActivity();
        mImageView = (ImageView) getActivity().findViewById(R.id.endImageView);
        mPlayAgain = (FontCreator_Buttons) getActivity().findViewById(R.id.btn_play_again);
        mPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
            }
        });
        boolean isWin = mActivity.GetController().DidUserWin();
        if (isWin){
            mActivity.GetController().GetUser().AddToCoins(GamePlay.VALUE_FOR_WIN);
            mActivity.GetFireBaseOperator().UpdateCoins(mActivity.GetController().GetUser());
            winAnimation();
        }else{
            mActivity.GetFireBaseOperator().UpdateCoins(mActivity.GetController().GetRival());
            loseAnimation();
        }
    }

    

    public void winAnimation() {
        mImageView.setBackgroundResource(R.mipmap.winner);
        mFireworks1 = (ImageView) getActivity().findViewById(R.id.fireworks1);
        mFireworks2 = (ImageView) getActivity().findViewById(R.id.fireworks2);
        mFireworks3 = (ImageView) getActivity().findViewById(R.id.fireworks3);
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
        mBlast = (ImageView) getActivity().findViewById(R.id.blast);
        mBlast.setBackgroundResource(R.drawable.blast);
        mAnimator = (AnimationDrawable) mBlast.getBackground();
        mAnimator.start();
    }
}
