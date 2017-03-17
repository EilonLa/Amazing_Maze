package activities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cdv.amazingmaze.R;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import DB.DBOperator;
import DB.DataRowBoard;
import DB.DataRowUser;
import DB.FireBaseOperator;
import Logic.GameController;
import Logic.Trap;
import Logic.User;
import UI.Fragments.CreateAMaze;
import UI.Fragments.FindAMaze;
import UI.Fragments.GamePlay;
import UI.Fragments.LoginFragment;
import UI.Fragments.LoggedInFragment;
import UI.Fragments.TrapListFragment;

//TODO: change icon when returning from signUp
//TODO: BACKGROUND FOR GAMEPLAY
//TODO: FIND A MAZE


public class MainActivity extends FragmentActivity {
    public static final Object mLockObject = new Object();
    private CreateAMaze mCreateMaze;
    private View mCreateNewMaze;
    private View mFindMaze;
    private View mUpgradeMaze;
    private LoginFragment mLoginFragment;
    private LoggedInFragment mLoggedInFragment;
    private TextView mLogOut;
    private ArrayList<Trap> mAvailableTraps;
    private DBOperator mDBOperator;
    private FireBaseOperator mFireBaseOperator;
    private GameController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDBOperator = new DBOperator(this);
        mCreateNewMaze = findViewById(R.id.create_new);
        mFireBaseOperator = new FireBaseOperator(this);
        SetAvailableTraps();
        mController = new GameController(this);
        mLogOut = (TextView) findViewById(R.id.logout);
        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mController.GetUser() != null) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    //mDataBase.SaveCurrentState(mUser);
                                    mController.SetUser(null);
                                    mLoginFragment = new LoginFragment();
                                    getFragmentManager().beginTransaction().replace(R.id.activity_main_login, mLoginFragment).addToBackStack(null).commit();
                                    mLogOut.setVisibility(View.INVISIBLE);
                                    SharedPreferences.Editor editor = mController.GetSharedPref().edit();
                                    editor.putInt("lastId", -1);
                                    editor.commit();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialog.cancel();
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Are you sure you want to log out?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            }
        });
        mCreateNewMaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mController.GetUser() != null) {
                    mCreateMaze = new CreateAMaze();
                    mController.SetIsCreating(true);
                    getFragmentManager().beginTransaction().add(R.id.container_board, mCreateMaze).addToBackStack(null).commit();

                } else {
                    Toast.makeText(MainActivity.this, "You need to log in first", Toast.LENGTH_LONG).show();
                }
            }
        });
        mFindMaze = findViewById(R.id.Find_maze);
        mFindMaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mController.GetUser() != null) {
                    mController.SetIsSearching(true);
                    FindAMaze findFragment = new FindAMaze();
                    getFragmentManager().beginTransaction().add(R.id.container_find,findFragment).addToBackStack(null).commit();
                }

            }
        });
        mUpgradeMaze = findViewById(R.id.Upgrade_maze);
        mUpgradeMaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity: ", "mUpgradeMaze clicked");
                if (mController.GetUser() != null && !mController.GetIsUpgrading()) {
                    TrapListFragment listFragment = new TrapListFragment();
                    getFragmentManager().beginTransaction().add(R.id.container_list, listFragment).addToBackStack(null).commit();
                    mController.SetIsUpgrading(true);
                }
            }
        });

    }

    public TextView GetLogOut() {
        return mLogOut;
    }

    public ArrayList<Trap> GetAvailableTraps() {
        return mAvailableTraps;
    }

    public void removeTraps(Trap trap) {
        for (Trap tempTrap : mController.GetUser().GetTraps()) {
            if (trap.GetName().compareToIgnoreCase(tempTrap.GetName()) == 0) {
                Log.i("Removing", trap.GetDescription());
                mController.GetUser().GetTraps().remove(tempTrap);
                break;
            }
        }
    }

    public FireBaseOperator GetFireBaseOperator(){return mFireBaseOperator;}

    public DBOperator GetDBOperator() {
        return mDBOperator;
    }

    public GameController GetController(){return mController;}

    public void SetAvailableTraps() {
        mAvailableTraps = new ArrayList<>();
        mAvailableTraps.add(new Trap(Trap.FIRE));
        mAvailableTraps.add(new Trap(Trap.FIVE_STEPS));
        mAvailableTraps.add(new Trap(Trap.TEN_STEPS));
        mAvailableTraps.add(new Trap(Trap.TWENTY_STEPS));
        mAvailableTraps.add(new Trap(Trap.FORTY_STEPS));
    }


    public void SetUserFragment(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mController.GetUser() == null) {
                    mLoginFragment = new LoginFragment();
                    getFragmentManager().beginTransaction().add(R.id.activity_main_login, mLoginFragment).addToBackStack(null).commit();
                    mLogOut.setVisibility(View.INVISIBLE);
                } else {
                    mLoggedInFragment = new LoggedInFragment();
                    getFragmentManager().beginTransaction().add(R.id.activity_main_login, mLoggedInFragment).addToBackStack(null).commit();
                    SharedPreferences.Editor editor = mController.GetSharedPref().edit();
                    editor.putString("lastId", mController.GetUser().GetId());
                    editor.commit();
                    mLogOut.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mFireBaseOperator.AddAuthListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (mController.GetIsCreating() && !mController.GetListFlag()){
            mController.GetUser().GetBoard().SaveBoard(mController.GetUser());
            mController.SetIsCreating(false);
        }
        if (mController.GetIsUpgrading()){
            mController.SetIsUpgrading(false);
        }
//        if (mController.GetI){
//            mIsSignIn = false;
//        }
//        if (mIsSignUp){
//            mIsSignUp = false;
//        }
        if (mController.GetIsGameOn()&& mController.GetListFlag()){
            mController.GetRival().GetBoard().PopFromStack();
            mController.SetListFlag( false);
        }
        if (mController.GetListFlag()){
            mController.SetListFlag( false);
        }

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }else{
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mUser != null) {
//            SharedPreferences.Editor editor = mSharedPref.edit();
//            editor.putString("lastId", mUser.GetId());
//            editor.commit();
//        }
//
//        if (mController.GetIsGameOn()) {
//            mGamePlay.StopTimer();
//            mGameOn = false;
//        }
        finish();
    }
}

