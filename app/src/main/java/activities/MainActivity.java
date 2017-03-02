package activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cdv.amazingmaze.R;

import java.util.ArrayList;

import DB.DBOperator;
import DB.DataBaseManager;
import DB.DataRowTrapUser;
import DB.DataRowUser;
import Logic.Trap;
import Logic.User;
import UI.LoginFragment;
import UI.SignedInFragment;


//TODO: FIREBASE
//TODO: FIND GAME
//TODO PLAY MAZE
//TODO: SAVE RESULTS


public class MainActivity extends FragmentActivity {
    public static boolean mNeedToRefresh = false;
    public static ArrayList<Trap> mAvailbleTraps;
    public static DBOperator mDataBase;
    public static DataBaseManager mDataBaseManager;
    public static SharedPreferences mLastUserId;
    private final DataRowUser mDummyUser = new DataRowUser("null", "", 0, null);
    private View mCreateNewMaze;
    private View mFindMaze;
    private View mUpgradeMaze;
    public static User mUser;
    private LoginFragment mLoginFragment;
    private SignedInFragment mSignedInFragment;
    private TextView mLogOut;
    public static SharedPreferences mSharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mNeedToRefresh){
            Intent i = getIntent();
            finish();
            mNeedToRefresh = false;
            startActivity(i);
        }
        setContentView(R.layout.activity_main);
        mCreateNewMaze = findViewById(R.id.create_new);
        mDataBase = new DBOperator(this);
        mDataBaseManager = new DataBaseManager();
        mDataBaseManager.start();
        mSharedPref = getPreferences(Context.MODE_PRIVATE);
        SetAvailableTraps();
        CreateUser();
        mLogOut = (TextView) findViewById(R.id.logout);
        if (mUser == null) {
            mLoginFragment = new LoginFragment();
            getFragmentManager().beginTransaction().add(R.id.activity_main_login, mLoginFragment).commit();
            mLogOut.setVisibility(View.INVISIBLE);
        } else {
            mSignedInFragment = new SignedInFragment();
            getFragmentManager().beginTransaction().add(R.id.activity_main_login, mSignedInFragment).commit();
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putInt("lastId", mUser.GetId());
            editor.commit();
            mLogOut.setVisibility(View.VISIBLE);
        }
        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUser != null) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    //mDataBase.SaveCurrentState(mUser);
                                    mUser = null;
                                    mLoginFragment = new LoginFragment();
                                    getFragmentManager().beginTransaction().replace(R.id.activity_main_login, mLoginFragment).commit();
                                    mLogOut.setVisibility(View.INVISIBLE);
                                    SharedPreferences.Editor editor = mSharedPref.edit();
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
                if (mUser != null) {
                    Intent intent = new Intent(getApplicationContext(), CreateAMaze.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "You need to log in first", Toast.LENGTH_LONG).show();
                }
            }
        });
        mFindMaze = findViewById(R.id.Find_maze);
        mFindMaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (mUser != null) {
//                    Intent intent = new Intent(getApplicationContext(), FindAMaze.class);
//                    startActivity(intent);
//                }
                if (mUser != null) {
                    Intent intent = new Intent(getApplicationContext(), GamePlay.class);
                    startActivity(intent);
                }

            }
        });
        mUpgradeMaze = findViewById(R.id.Upgrade_maze);
        mUpgradeMaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity: ", "mUpgradeMaze clicked");
                if (mUser != null) {
                    Intent intent = new Intent(getApplicationContext(), PopItems.class);
                    intent.putExtra("upgrade", true);
                    startActivity(intent);
                }
            }
        });

    }

    public static void removeTraps(Trap trap) {
        for (Trap tempTrap : mUser.GetTraps()) {
            if (trap.GetName().compareToIgnoreCase(tempTrap.GetName()) == 0) {
                Log.i("Removing", trap.GetDescription());
                mUser.GetTraps().remove(tempTrap);
                break;
            }
        }
    }

    public void SetAvailableTraps() {
        mAvailbleTraps = new ArrayList<>();
        mAvailbleTraps.add(new Trap(Trap.FIRE));
        mAvailbleTraps.add(new Trap(Trap.FIVE_STEPS ));
        mAvailbleTraps.add(new Trap(Trap.TEN_STEPS ));
        mAvailbleTraps.add(new Trap(Trap.TWENTY_STEPS ));
        mAvailbleTraps.add(new Trap(Trap.FORTY_STEPS ));
    }

    public void CreateUser() {
        int lastId = mSharedPref.getInt("lastId", -1);
        if (lastId != -1) {
            DataRowUser dataRowUser = mDataBase.GetLastUserFromDB(lastId);
            if (dataRowUser != null) {
                ArrayList<Trap> tempTraps = new ArrayList<>();
                int userId = dataRowUser.GetUserId();
                String password = dataRowUser.GetPassword();
                String userName = dataRowUser.GetUserName();
                int coins = dataRowUser.GetCoins();
                for (int trapIndex : dataRowUser.GetTrapIndexes()) {
                    tempTraps.add(new Trap(trapIndex));
                }
                mUser = new User(userId, password, userName, coins, tempTraps);
            }
        }
//        int lastId = mSharedPref.getInt("lastId", -1);
//        Log.i("id", " " + lastId);
//        if (lastId != mDummyUser.GetUserId() && lastId != -1) {
//            DataRowUser dataRowUser = mDataBase.GetLastUserFromDB();
//            if (dataRowUser != null) {
//                ArrayList<Trap> tempTraps = new ArrayList<>();
//                int userId = dataRowUser.GetUserId();
//                String password = dataRowUser.GetPassword();
//                String userName = dataRowUser.GetUserName();
//                int coins = dataRowUser.GetCoins();
//                for (int trapIndex : dataRowUser.GetTrapIndexes()) {
//                    tempTraps.add(new Trap(trapIndex));
//                }
//                mUser = new User(userId, password, userName, coins, tempTraps);
//                SharedPreferences.Editor editor = mSharedPref.edit();
//                editor.putInt("lastId", userId);
//                editor.commit();
//            }
//        }
    }


    //int userId, String userName, String password, int coins, ArrayList<Integer> trapIndexes
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDataBase != null) {
            //mDataBase.closeConnection();
            mDataBase.close();
        }
        if (mUser != null){
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putInt("lastId", mUser.GetId());
            editor.commit();
            //mUser.GetBoard().SaveBoard();
            final DataRowUser dr = new DataRowUser(mUser.GetId(), mUser.GetUserName(), mUser.GetPassword(), mUser.GetCoins(), mUser.GenerateListOfTrapIndex());
            //mDataBase.AddRow_User(dr);

            //mDataBase.UpdateCoins(mUser.GetCoins(), mUser.GetId());
        }
//        final DataRowUser dr = new DataRowUser(mUser.GetId(), mUser.GetUserName(), mUser.GetPassword(), mUser.GetCoins(), mUser.GenerateListOfTrapIndex());
//        mDataBase.AddRow_User(dr);
//        mUser.GetBoard().SaveBoard();
//        SharedPreferences.Editor editor = mSharedPref.edit();
//        editor.putInt("lastId", mUser.GetId());
//        editor.commit();
        //mDataBase.UpdateCoins(mUser.GetCoins(), mUser.GetId());
        finish();

    }
}
