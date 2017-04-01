package Logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import DB.DataRowBoard;
import DB.DataRowUser;
import UI.Board;
import activities.MainActivity;

/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * The GameController is a manager for every logic in the game and all of the views and other params are communicating with this
 * class in order to get data on the system's current state and mode
 *
 */
public class GameController {
    private boolean mGameOn = false;
    private boolean mUpgrading = false;
    private boolean mCreating = false;
    private boolean mSearching = false;
    private boolean mIsSignIn = false;
    private boolean mIsSignUp = false;
    private boolean mListFlag = false;
    private boolean mDidUserWin = false;
    private boolean mRivalHasBoard = false;
    private boolean mIsGameFinished = false;
    private AtomicBoolean mIsWaitingForFireBase;
    private User mUser;
    private User mRival;
    private ArrayList<DataRowBoard> mListDataForRivalBoard;
    private ArrayList<String> mOptionalUsers;
    private SharedPreferences mSharedPref;
    private MainActivity mActivity;
    private Thread mGameTimerThread;
    private Board mActiveBoard;
    private View mViewToReset;

    public GameController(MainActivity activity) {
        mActivity = activity;
        mSharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
        mIsWaitingForFireBase = new AtomicBoolean(false);
        CreateUser();
    }

    /**
     * Creates the user by the following rules:
     * 1. Get's the last user Id that was logged in to the system
     * 2. Get's the User from the Server
     * 3. If not found (can happen if user was created on a stand alone environment), searches the user on the data base.
     */
    public void CreateUser() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String lastId = mSharedPref.getString("lastId", "null");
                if (!lastId.equals("null")) {
                    synchronized (MainActivity.mLockObject) {
                        mIsWaitingForFireBase.set(true);
                        mActivity.GetFireBaseOperator().GetUserFromFireBase(lastId, null, null);
                    }
                    while (mIsWaitingForFireBase.get()) {
                    } //wait for fireBase to get the data
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


    /**
     * WCalled when server is creating the list of users that match query when searching for a game to play
     *
     * @return List of users that match query
     */
    public ArrayList<String> GetOptionalUsers() {
        if (mOptionalUsers == null) {
            mOptionalUsers = new ArrayList<>();
        }
        return mOptionalUsers;
    }

    /**
     * When we are setting the Entrance or Exit tiles the background color of the ImageView is different.
     * Because' after setting it we need to set the background back

     * @param view
     */
    public void ViewToReset(View view) {
        mViewToReset = view;
    }

    /**
     * When we are setting the Entrance or Exit tiles the background color of the ImageView is different.
     * Because' after setting it we need to set the background back
     */

    public void ResetView() {
        if (mViewToReset != null) {
            mViewToReset.setBackgroundColor(Color.BLACK);
        }
    }


    /**
     * when inflating a board it can be the user's or the rival's
     * @param board
     */
    public void SetActiveBoard(Board board) {
        mActiveBoard = board;
        mDidUserWin = false;
    }

    public Board GetActiveBoard() {
        return mActiveBoard;
    }


    public void KillBoardThreads() throws Exception{
        if (mActiveBoard != null) {
            mActiveBoard.KillAllRunningThreads();
        }
    }

    /**
     * Set's the game timer
     * @param thread
     */
    public void SetTimer(Thread thread) {
        mGameTimerThread = thread;
    }

    /**
     * Stops the game timer
     */
    public void StopTimer() {
        if (mGameTimerThread != null && mGameTimerThread.isAlive()) {
            mGameTimerThread.interrupt();
        }
    }

    public void ResetFlags() {
        mGameOn = false;
        mUpgrading = false;
        mCreating = false;
        mSearching = false;
        mIsSignIn = false;
        mIsSignUp = false;
        mListFlag = false;
        mIsGameFinished = false;
    }

    public boolean GetIsSignIn() {
        return mIsSignIn;
    }

    public boolean GetIsSignUp() {
        return mIsSignUp;
    }

    public boolean GetIsSearching() {
        return mSearching;
    }

    public boolean GetListFlag() {
        return mListFlag;
    }

    public void SetIsSearching(boolean isSearching) {
        mSearching = isSearching;
    }

    public User GetUser() {
        return mUser;
    }

    public void SetUser(User user) {
        mUser = user;
    }

    public SharedPreferences GetSharedPref() {
        return mSharedPref;
    }

    public void SetRivalData(ArrayList<DataRowBoard> list) {
        mListDataForRivalBoard = list;
    }

    public void SetOptionalUsers(ArrayList<String> list) {
        this.mOptionalUsers = list;
    }

    public ArrayList<DataRowBoard> GetRivalData() {
        return mListDataForRivalBoard;
    }

    public User GetRival() {
        return mRival;
    }

    public void SetDoesRivalHasBoard(boolean data) {
        mRivalHasBoard = data;
    }

    public boolean GetDoesRivalHasBoard(){
        return mRivalHasBoard;
    }

    public void SetRival(User user) {
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

    public void SetListFlag(boolean flag) {
        mListFlag = flag;
        try {
            mActiveBoard.SetAllBoardClickable(!flag);
        }catch (Exception e){
        }
    }

    public boolean GetIsCreating() {
        return mCreating;
    }

    public void SetIsCreating(boolean isCreating) {
        mCreating = isCreating;
    }

    public boolean GetIsGameOn() {
        return mGameOn;
    }

    public boolean GetIsUpgrading() {
        return mUpgrading;
    }

    public void SetGameMode(boolean gameMode) {
        this.mGameOn = gameMode;
    }

    public boolean DidUserWin() {
        return mDidUserWin;
    }

    public void SetWin(boolean flag) {
        mDidUserWin = flag;
    }

    public boolean IsGameFinished() {
        return mIsGameFinished;
    }

    public void SetFinishedGame() {
        mIsGameFinished = true;
    }

    public void IsWaitingForFireBase(boolean isWaiting) {
        mIsWaitingForFireBase.set(isWaiting);
    }

    public boolean IsWaitingForFireBase() {
        return mIsWaitingForFireBase.get();
    }

}
