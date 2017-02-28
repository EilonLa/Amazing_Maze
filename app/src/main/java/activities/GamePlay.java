package activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;

import com.example.cdv.amazingmaze.R;

import UI.Board;
import UI.FontCreator_Logo;

/**
 * Created by אילון on 26/01/2017.
 */

public class GamePlay extends Activity {
    public static final int VALUE_FOR_WIN = 10;
    private Board mBoard;
    private int mSeconds;
    private FontCreator_Logo mGameTimer;
    private FontCreator_Logo mStartGame;
    private Thread mTimerHandler;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameplay);
        mBoard = new Board(this, MainActivity.mDataBase.GetBoardByUserId(MainActivity.mUser.GetId()), R.id.activity_game_maze_board,true);
        mBoard.SetGameMode(true);
        mGameTimer = (FontCreator_Logo) findViewById(R.id.timer_game_seconds);
        mSeconds = Integer.parseInt(mGameTimer.getText().toString());
        mStartGame = (FontCreator_Logo) findViewById(R.id.start_game);
        mStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SetTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StopTimer();
    }

    public void SetTimer(){
        if (mTimerHandler ==null) {
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
                        runOnUiThread(new Runnable() {
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

    public void TimeUp(){
        mBoard.SetAllBoardNotClickable();
        if (mBoard.CheckPath()){
            mBoard.AnimatePath();
        }else{
            mBoard.KillAllRunningThreads();
            Intent resultScreen = new Intent(getApplicationContext(),ResultScreen.class);
            //TODO: add to maze creator coins via fire base
            resultScreen.putExtra("player_win",false);
            startActivity(resultScreen);
            finish();
        }
    }

}
