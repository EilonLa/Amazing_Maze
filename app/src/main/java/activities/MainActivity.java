package activities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cdv.amazingmaze.R;

import java.util.ArrayList;

import DB.DBOperator;
import DB.DataBaseManager;
import DB.DataRowUser;
import DB.FireBaseOperator;
import Logic.Trap;
import Logic.User;
import UI.LoginFragment;
import UI.LoggedInFragment;


//TODO: FIREBASE
//TODO: FIND GAME


public class MainActivity extends FragmentActivity {
    public static ArrayList<Trap> mAvailableTraps;
    public static DBOperator mDataBase;
    public static DataBaseManager mDataBaseManager;
    private View mCreateNewMaze;
    private View mFindMaze;
    private View mUpgradeMaze;
    public static User mUser;
    private LoginFragment mLoginFragment;
    private LoggedInFragment mLoggedInFragment;
    public static TextView mLogOut;
    public static SharedPreferences mSharedPref;
    public static FireBaseOperator mFireBaseOperator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCreateNewMaze = findViewById(R.id.create_new);
        mDataBase = new DBOperator(this);
        mDataBaseManager = new DataBaseManager();
        mFireBaseOperator = new FireBaseOperator();
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
            mLoggedInFragment = new LoggedInFragment();
            getFragmentManager().beginTransaction().add(R.id.activity_main_login, mLoggedInFragment).commit();
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putString("lastId", mUser.GetId());
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
        mAvailableTraps = new ArrayList<>();
        mAvailableTraps.add(new Trap(Trap.FIRE));
        mAvailableTraps.add(new Trap(Trap.FIVE_STEPS ));
        mAvailableTraps.add(new Trap(Trap.TEN_STEPS ));
        mAvailableTraps.add(new Trap(Trap.TWENTY_STEPS ));
        mAvailableTraps.add(new Trap(Trap.FORTY_STEPS ));
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mFireBaseOperator.AddAuthListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFireBaseOperator.RemoveAuthListener();
    }

    public void CreateUser() {
        String lastId = mSharedPref.getString("lastId", "null");
        if (!lastId.equals("null")) {
            DataRowUser dataRowUser = mDataBase.GetLastUserFromDB(lastId);
            if (dataRowUser != null) {
                ArrayList<Trap> tempTraps = new ArrayList<>();
                String userId = dataRowUser.GetUserId();
                String password = dataRowUser.GetPassword();
                String userName = dataRowUser.GetUserName();
                int coins = dataRowUser.GetCoins();
                for (int trapIndex : dataRowUser.GetTrapIndexes()) {
                    tempTraps.add(new Trap(trapIndex));
                }
                mUser = new User(userId, password, userName, coins, tempTraps);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDataBase != null) {
            mDataBase.close();
        }
        if (mUser != null){
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putString("lastId", mUser.GetId());
            editor.commit();
            final DataRowUser dr = new DataRowUser(mUser.GetId(), mUser.GetUserName(), mUser.GetPassword(), mUser.GetCoins(), mUser.GenerateListOfTrapIndex());

        }
        finish();

    }
}

