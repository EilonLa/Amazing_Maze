package UI.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cdv.amazingmaze.R;

import UI.Board;
import UI.FontCreator_Logo;
import activities.MainActivity;

/**
 * Created by אילון on 26/01/2017.
 */

public class GamePlay extends Fragment {
    public static final int VALUE_FOR_WIN = 10;
    private Board mBoard;
    private int mSeconds;
    private FontCreator_Logo mGameTimer;
    private FontCreator_Logo mStartGame;
    private Thread mTimerHandler;
    private MainActivity mActivity;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity)getActivity();
        mBoard = new Board(mActivity, mActivity.GetController().GetRival(), mActivity.GetController().GetRivalData(), R.id.activity_game_maze_board,true);
        mBoard.SetGameMode(true);
        mGameTimer = (FontCreator_Logo) getActivity().findViewById(R.id.timer_game_seconds);
        mSeconds = Integer.parseInt(mGameTimer.getText().toString());
        mStartGame = (FontCreator_Logo) getActivity().findViewById(R.id.start_game);
        mStartGame.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                startGame();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gameplay, container, false);
        SetTimer();
        return view;
    }


    public void SetTimer(){
        if (mTimerHandler ==null ) {
            mTimerHandler = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    while (mSeconds > 0) {
                        mSeconds--;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mGameTimer.setText("" + mSeconds);
                            }
                        });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    TimeUp();
                }
            });
            mTimerHandler.start();
        }
    }

    public void StopTimer (){
        if (mTimerHandler.isAlive())
            mTimerHandler.interrupt();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startGame(){
        Log.i("GamePlay",""+" mStartGame");
    }

    public void TimeUp() {
        if (!mActivity.GetController().IsGameFinished()) {
            mBoard.SetAllBoardNotClickable();
            if (mBoard.CheckPath()) {
                mBoard.AnimatePath();
            } else {//player lose
                mBoard.KillAllRunningThreads();
                mActivity.GetController().SetWin(false);
                mActivity.getFragmentManager().beginTransaction().remove(mActivity.getFragmentManager().findFragmentById(R.id.container_board)).addToBackStack(null).commit();
                mActivity.getFragmentManager().beginTransaction().add(R.id.container_Result_Screen, new ResultScreen()).addToBackStack(null).commit();
            }
        }
        else{
            StopTimer();
        }
    }

}
