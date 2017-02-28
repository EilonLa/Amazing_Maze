package Logic;

import android.app.Activity;

import java.util.ArrayList;

import UI.Board;
import activities.MainActivity;

/**
 * Created by אילון on 28/01/2017.
 */

public class User {
    public static final int DEFAULT_NUM_OF_COINS = 50;
    private int mId;
    private String mUserName;
    private ArrayList<Trap> mTraps;
    private int mCoins;
    private Board mBoard;
    private String mPassword;


    public User(int id, String password, String userName, int coins, ArrayList<Trap> traps) {
        this.mId = id;
        this.mPassword = password;
        this.mUserName = userName;
        this.mCoins = coins;
        this.mTraps = traps;
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

    public int GetId() {
        return mId;
    }

    public void SetId(int id) {
        this.mId = id;
    }

    public Board GetBoard() {
        return mBoard;
    }

    public void SetBoard(Board board) {
        this.mBoard = board;
    }

    public void SetBoard(Activity activity, int boardId) {
        mBoard = new Board(activity, MainActivity.mDataBase.GetBoardByUserId(MainActivity.mUser.mId), boardId, false);
    }

    public ArrayList<Integer> GenerateListOfTrapIndex() {
        ArrayList<Integer> indexes = new ArrayList();
        for (Trap trap : mTraps) {
            if (trap.IsAttack())
                indexes.add(trap.GetTrapIndex());
            else
                indexes.add(trap.GetTrapIndex() * -1);
        }
        return indexes;
    }
}
