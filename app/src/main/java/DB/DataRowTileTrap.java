package DB;

/**
 * Created by אילון on 31/01/2017.
 */

public class DataRowTileTrap {
    private int mTileId;
    private int mTrapId;

    public DataRowTileTrap (int trapId,int tileId){
        this.mTileId = tileId;
        this.mTrapId = trapId;
    }

    public int GetTileId() {
        return mTileId;
    }

    public void SetTileId(int tileId) {
        this.mTileId = tileId;
    }

    public int GetTrapId() {
        return mTrapId;
    }

    public void SetTrapId(int trapId) {
        this.mTrapId = trapId;
    }
}
