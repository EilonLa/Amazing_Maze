package Logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.example.cdv.amazingmaze.R;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import DB.DataRowBoard;
import DB.DataRowUser;
import UI.Fragments.LoggedInFragment;
import UI.Fragments.LoginFragment;
import activities.MainActivity;

/**
 * Created by אילון on 17/03/2017.
 */

public class GameController {
    private boolean mGameOn = false;
    private boolean mUpgrading = false;
    private boolean mCreating = false;
    private boolean mSerching = false;
    private boolean mIsSignIn = false;
    private boolean mIsSignUp = false;
    private boolean mListFlag = false;
    private boolean mDidUserWin = false;
    private boolean mIsGameFinished = false;
    private AtomicBoolean mIsWaitingForFireBase;
    private User mUser;
    private User mRival;
    private ArrayList<DataRowBoard> mListDataForRivalBoard;
    private ArrayList<String> mOptionalUsers;
    private SharedPreferences mSharedPref;
    private MainActivity mActivity;

    public GameController (MainActivity activity){
        mActivity = activity;
        mSharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
        mIsWaitingForFireBase = new AtomicBoolean(false);
        CreateUser();
    }

    public void CreateUser() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String lastId = mSharedPref.getString("lastId", "null");
                if (!lastId.equals("null")) {
                    synchronized (MainActivity.mLockObject) {
                        mIsWaitingForFireBase.set(true);
                        mActivity.GetFireBaseOperator().GetUserFromFireBase(lastId,null,null);
                    }
                    while (mIsWaitingForFireBase.get()){} //wait for fireBase to get the data
                    if (mUser == null) {//try to get the user from the db
                        synchronized (MainActivity.mLockObject) {
                            DataRowUser dataRowUser = mActivity.GetDBOperator().GetLastUserFromDB(lastId);
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
                                mUser.SetListDataBoardFromFireBase(mActivity.GetDBOperator().GetBoardByUserId(mUser.GetId()));
                            }
                        }
                    }
                }
                mActivity.SetUserFragment();
            }
        }).start();
    }


    public ArrayList<String> GetOptionalUsers(){
        if (mOptionalUsers == null){
            mOptionalUsers = new ArrayList<>();
        }
        return mOptionalUsers;
    }

    public boolean GetIsSearching(){return mSerching;}

    public boolean GetListFlag(){return mListFlag;}

    public void SetIsSearching(boolean isSearching){mSerching = isSearching;}

    public User GetUser() {
        return mUser;
    }

    public void SetUser(User user) {
        mUser = user;
    }

    public SharedPreferences GetSharedPref() {
        return mSharedPref;
    }

    public void SetRivalData(ArrayList<DataRowBoard> list){
        mListDataForRivalBoard = list;
    }

    public void SetOptionalUsers(ArrayList<String> list){
        this.mOptionalUsers = list;
    }

    public ArrayList<DataRowBoard> GetRivalData(){
        return mListDataForRivalBoard;
    }

    public User GetRival (){
        return mRival;
    }

    public void SetRival (User user){
        mRival = user;
    }

    public void SetIsUpgrading(boolean isUpgrading) {
        this.mUpgrading = isUpgrading;
    }

    public void SetIsSignIn(boolean isLoggin) {
        this.mIsSignIn = isLoggin;
    }

    public void SetIsSignUp(boolean isLoggin) {
        this.mIsSignUp = isLoggin;
    }

    public void SetListFlag(boolean flag){
        mListFlag = flag;
    }

    public boolean GetIsCreating() {
        return mCreating;
    }

    public void SetIsCreating(boolean isCreating){ mCreating = isCreating;}

    public boolean GetIsGameOn() {
        return mGameOn;
    }

    public boolean GetIsUpgrading() {
        return mUpgrading;
    }

    public void SetGameMode(boolean gameMode){
        this.mGameOn = gameMode;
    }

    public boolean DidUserWin(){
        return mDidUserWin;
    }

    public void SetWin(boolean flag){
        mDidUserWin = flag;
    }

    public boolean IsGameFinished(){
        return mIsGameFinished;
    }

    public void SetFinishedGame(){
        mIsGameFinished = true;
    }

    public void IsWaitingForFireBase(boolean isWaiting){
        mIsWaitingForFireBase.set(isWaiting);
    }

    public boolean IsWaitingForFireBase(){
        return mIsWaitingForFireBase.get();
    }

}
