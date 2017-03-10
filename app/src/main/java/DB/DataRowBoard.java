package DB;

/**
 * Created by אילון on 31/01/2017.
 */

public class DataRowBoard {
    private String mId;
    private int mRow;
    private int mCol;
    private String mTileId;

    public DataRowBoard (String userId, int row,int col, String tileId){
        this.mId = userId;
        this.mRow = row;
        this.mCol = col;
        this.mTileId = tileId;
    }

    public String GetId() {
        return mId;
    }

    public void SetId(String id) {
        this.mId = id;
    }

    public int GetRow() {
        return mRow;
    }

    public void SetRow(int row) {
        this.mRow = row;
    }

    public int GetCol() {
        return mCol;
    }

    public void SetCol(int col) {
        this.mCol = col;
    }

    public String GetTileId() {
        return mTileId;
    }

    public void SetTileId(String tileId) {
        this.mTileId = tileId;
    }
}
