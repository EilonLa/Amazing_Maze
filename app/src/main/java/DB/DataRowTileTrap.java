package DB;
/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * Class represents the connection between the tile and the Trap in the DB
 *
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
