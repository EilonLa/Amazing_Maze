package DB;

/**
 * Created by אילון on 31/01/2017.
 */

public class DataRowTileTrap {
    private String mTileId;
    private int mTrapId;

    public DataRowTileTrap (int trapId,String tileId){
        this.mTileId = tileId;
        this.mTrapId = trapId;
    }

    public String GetTileId() {
        return mTileId;
    }

    public void SetTileId(String tileId) {
        this.mTileId = tileId;
    }

    public int GetTrapId() {
        return mTrapId;
    }

    public void SetTrapId(int trapId) {
        this.mTrapId = trapId;
    }
}
