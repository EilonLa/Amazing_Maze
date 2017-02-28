package activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.example.cdv.amazingmaze.R;

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
        MainActivity.mUser.SetBoard(this, R.id.activity_create_maze_board);

        this.mSetEntrance = findViewById(R.id.set_entrance);
        mSetEntrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.mUser.GetBoard().ismEntranceActivated()) {
                    MainActivity.mUser.GetBoard().setmEntranceActivated(false);
                } else {
                    MainActivity.mUser.GetBoard().setmEntranceActivated(true);
                    MainActivity.mUser.GetBoard().setmExitActivated(false);
                }
            }
        });
        this.mSetExit = findViewById(R.id.set_exit);
        mSetExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.mUser.GetBoard().ismExitActivated()) {
                    MainActivity.mUser.GetBoard().setmExitActivated(false);
                } else {
                    MainActivity.mUser.GetBoard().setmExitActivated(true);
                    MainActivity.mUser.GetBoard().setmEntranceActivated(false);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        MainActivity.mUser.GetBoard().SaveBoard();
        //finish();
    }
}




