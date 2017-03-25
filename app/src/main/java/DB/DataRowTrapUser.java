package DB;

/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * Class represents the connection between the Trap and the User in the DB
 *
 */

public class DataRowTrapUser {
    private String mUserId;
    private int mTrapId;
    public DataRowTrapUser (String userId,int trapId){
        this.mUserId = userId;
        this.mTrapId = trapId;
    }

    public int GetTrapId() {
        return mTrapId;
    }

    public void SetTrapId(int trapId) {
        this.mTrapId = trapId;
    }

    public String GetUserId() {
        return mUserId;
    }

    public void SetUserId(String userId) {
        this.mUserId = userId;
    }
}
