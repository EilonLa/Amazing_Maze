package DB;

/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * Class represents the Trap in the DB
 *
 */
public class DataRowTrap {
    private int mType;
    private int mId;

    public DataRowTrap (int index,int type){
        this.mId = index;
        this.mType = type;
    }

    public int GetType() {
        return mType;
    }

    public void SetType(int type) {
        this.mType = type;
    }

    public int GetId() {
        return mId;
    }

    public void SetId(int id) {
        this.mId = id;
    }

}
