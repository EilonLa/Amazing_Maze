package DB;


import java.util.ArrayList;

/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * Class represents a user in the DB
 *
 */
public class DataRowUser {
    private String mUserName;
    private String mUserId;
    private String mPassword;
    private int mCoins;
    private ArrayList<Integer> mTrapIndexes;

    public DataRowUser(String userId, String userName, String password, int coins, ArrayList<Integer> trapIndexes) {
        this.mUserId = userId;
        this.mUserName = userName;
        this.mPassword = password;
        this.mCoins = coins;
        this.mTrapIndexes = trapIndexes;
    }
    public DataRowUser(String userName, String password, int coins, ArrayList<Integer> trapIndexes) {
        this.mUserName = userName;
        this.mPassword = password;
        this.mCoins = coins;
        this.mTrapIndexes = trapIndexes;

    }

    public ArrayList<Integer> GetTrapIndexes() {
        return mTrapIndexes;
    }

    public void SetTrapIndexes(ArrayList<Integer> trapIndexes) {
        this.mTrapIndexes = trapIndexes;
    }

    public String GetUserName() {
        return mUserName;
    }

    public void SetUserName(String userName) {
        this.mUserName = userName;
    }

    public String GetUserId() {
        return mUserId;
    }

    public void SetUserId(String userId) {
        this.mUserId = userId;
    }

    public String GetPassword() {
        return mPassword;
    }

    public void SetPassword(String password) {
        this.mPassword = password;
    }

    public int GetCoins() {
        return mCoins;
    }

    public void SetCoins(int coins) {
        this.mCoins = coins;
    }
}
