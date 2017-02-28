package DB;

/**
 * Created by אילון on 31/01/2017.
 */

public class DataRowTrapUser {
    private int mUserId;
    private int mTrapId;
    public DataRowTrapUser (int userId,int trapId){
        this.mUserId = userId;
        this.mTrapId = trapId;
    }

    public int GetTrapId() {
        return mTrapId;
    }

    public void SetTrapId(int trapId) {
        this.mTrapId = trapId;
    }

    public int GetUserId() {
        return mUserId;
    }

    public void SetUserId(int userId) {
        this.mUserId = userId;
    }
}
