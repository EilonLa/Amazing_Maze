package UI.Fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cdv.amazingmaze.R;

import Logic.ExceptionHandler;
import UI.Board;
import UI.FontCreator_Buttons;
import activities.MainActivity;

/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * The GamePlay fragment is inflated when the user found a game and now we need to setup the board
 *
 */
public class GamePlay extends Fragment {
    public static final int VALUE_FOR_WIN = 10;
    private Board mBoard;
    private int mSeconds;
    private FontCreator_Buttons mGameTimer;
    private FontCreator_Buttons mStartGame;
    private Thread mTimerHandler;
    private MainActivity mActivity;
    private Button mBackBtn;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        try {
            mBoard = new Board(mActivity, mActivity.GetController().GetRival(), mActivity.GetController().GetRivalData(), R.id.activity_game_maze_board, true);
            mBoard.SetGameMode(true);
            mGameTimer = (FontCreator_Buttons) getActivity().findViewById(R.id.timer_game_seconds);
            mBackBtn = (Button) getActivity().findViewById(R.id.back_create_game);
            mBackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActivity.GetController().GetActiveBoard().DrainStack();
                    mActivity.onBackPressed();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            new ExceptionHandler(e.toString(), mActivity.GetFireBaseOperator());
        }
        mSeconds = Integer.parseInt(mGameTimer.getText().toString());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gameplay, container, false);
        try {
            SetTimer();
        }catch (Exception e){
            e.printStackTrace();
            new ExceptionHandler(e.toString(), mActivity.GetFireBaseOperator());
        }
        return view;
    }


    public void SetTimer()throws Exception {
        if (mTimerHandler == null) {
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
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mGameTimer.setText("" + mSeconds);
                                }
                            });
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    try {
                        TimeUp();
                    }catch (Exception e){
                        e.printStackTrace();
                        new ExceptionHandler(e.toString(), mActivity.GetFireBaseOperator());
                    }
                }
            });
            mTimerHandler.start();
            if (mActivity == null){
                mActivity = (MainActivity)getActivity();
            }
            mActivity.GetController().SetTimer(mTimerHandler);
        }
    }

    public void StopTimer() {
        if (mTimerHandler.isAlive())
            mTimerHandler.interrupt();
    }

    public void TimeUp()throws Exception {
        if (mBoard != null) {
            mBoard.SetAllBoardClickable(false);
            if (mBoard.CheckPath()) {
                mBoard.AnimatePath();
            } else {//player lose
                mBoard.KillAllRunningThreads();
                mActivity.GetController().SetWin(false);
                mActivity.getFragmentManager().beginTransaction().remove(mActivity.getFragmentManager().findFragmentById(R.id.container_board)).addToBackStack(null).commit();
                mActivity.getFragmentManager().beginTransaction().add(R.id.container_Result_Screen, new ResultScreen()).addToBackStack(null).commit();
            }
        }
    }
}
