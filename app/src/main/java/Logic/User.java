package Logic;

import android.app.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import DB.DataRowBoard;
import UI.Board;
import activities.MainActivity;

/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * The User can be the actual user that plays the game or a dummy user that represents the rival during game play
 *
 */

public class User {
    public static final int DEFAULT_NUM_OF_COINS = 50;
    private String mId;
    private String mUserName;
    private ArrayList<Trap> mTraps;
    private int mCoins;
    private Board mBoard;
    private String mPassword;
    private List mTrapList;
    private boolean mIsRival;
    private ArrayList<DataRowBoard> mListDataBoardFromFireBase;

    public User(String id, String password, String userName, int coins, ArrayList<Trap> traps) {
        this.mId = id;
        this.mPassword = password;
        this.mUserName = userName;
        this.mCoins = coins;
        mIsRival = false;
        if (traps != null) {
            this.mTraps = new ArrayList<>(traps);
            mTrapList = traps.subList(0, traps.size() - 1);
        } else
            this.mTraps = new ArrayList<>();
    }

    public String GetPassword() {
        return mPassword;
    }

    public void SetPassword(String password) {
        this.mPassword = password;
    }

    public String GetUserName() {
        return mUserName;
    }

    public void SetUserName(String userName) {
        this.mUserName = userName;
    }

    public boolean IsRival(){
        return mIsRival;
    }

    public void SetIsRival(boolean isRival){
        mIsRival = isRival;
    }

    public ArrayList<Trap> GetTraps() {
        return mTraps;
    }

    public void AddToCoins(int num) {
        mCoins += num;
    }

    public void SetTraps(ArrayList<Trap> traps) {
        this.mTraps = traps;
    }

    public int GetCoins() {
        return mCoins;
    }

    public void SetCoins(int coins) {
        this.mCoins = coins;
    }

    public String GetId() {
        return mId;
    }

    public void SetId(String id) {
        this.mId = id;
    }

    public Board GetBoard() {
        return mBoard;
    }

    public void SetBoard(Board board) {
        this.mBoard = board;
    }

    public void SetBoard(MainActivity activity, int boardId) throws Exception{
        mBoard = new Board(activity,this, activity.GetDBOperator().GetBoardByUserId(mId), boardId, false);
    }


    public List<Trap> GetTrapList() {
        return mTrapList;
    }

    public void SetTrapList(List<Trap> traps) {
        mTrapList = traps;
    }

    public ArrayList<DataRowBoard> GetListDataBoardFromFireBase() {
        if (mListDataBoardFromFireBase == null) {
            return new ArrayList<DataRowBoard>();
        }
        return mListDataBoardFromFireBase;
    }

    public void SetListDataBoardFromFireBase(ArrayList<DataRowBoard> mListDataBoardFromFireBase) {
        this.mListDataBoardFromFireBase = mListDataBoardFromFireBase;
    }

    public ArrayList<Integer> GenerateListOfTrapIndex() {
        ArrayList<Integer> indexes = new ArrayList();
        for (Trap trap : mTraps) {
            indexes.add(trap.GetTrapIndex());
        }
        return indexes;
    }

    public void GenerateListOfTraps(HashMap<String, Long> list, String tag) {
        mTraps = new ArrayList<>();
        int counter = 1;
        if (list != null) {
            for (String str : list.keySet()) {
                long temp = list.get(str);
                mTraps.add(new Trap((int) temp));
            }
        }

    }
}
