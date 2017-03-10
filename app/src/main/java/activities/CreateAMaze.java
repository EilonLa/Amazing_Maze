package activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.example.cdv.amazingmaze.R;

import UI.Board;

/**
 * Created by אילון on 26/01/2017.
 */

public class CreateAMaze extends FragmentActivity {
    private View mSetEntrance;
    private View mSetExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_maze);
        MainActivity.mUser.SetBoard(new Board(this,MainActivity.mUser.GetListDataBoardFromFireBase(),R.id.activity_create_maze_board,false));

        this.mSetEntrance = findViewById(R.id.set_entrance);
        mSetEntrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.mUser.GetBoard().IsEntranceActivated()) {
                    MainActivity.mUser.GetBoard().SetEntranceActivated(false);
                } else {
                    MainActivity.mUser.GetBoard().SetEntranceActivated(true);
                    MainActivity.mUser.GetBoard().SetExitActivated(false);
                }
            }
        });
        this.mSetExit = findViewById(R.id.set_exit);
        mSetExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.mUser.GetBoard().IsExitActivated()) {
                    MainActivity.mUser.GetBoard().SetExitActivated(false);
                } else {
                    MainActivity.mUser.GetBoard().SetExitActivated(true);
                    MainActivity.mUser.GetBoard().SetEntranceActivated(false);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        MainActivity.mUser.GetBoard().SaveBoard(MainActivity.mUser);
        //finish();
    }
}




