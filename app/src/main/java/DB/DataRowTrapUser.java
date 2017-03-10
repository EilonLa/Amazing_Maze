package DB;

/**
 * Created by אילון on 31/01/2017.
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
