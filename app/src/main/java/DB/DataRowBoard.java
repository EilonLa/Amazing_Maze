package DB;

/**
 * Created by אילון on 31/01/2017.
 */

public class DataRowBoard {
    private int mId;
    private int mRow;
    private int mCol;
    private int mTileId;

    public DataRowBoard (int userId, int row,int col, int tileId){
        this.mId = userId;
        this.mRow = row;
        this.mCol = col;
        this.mTileId = tileId;
    }

    public int GetId() {
        return mId;
    }

    public void SetId(int id) {
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

    public int GetTileId() {
        return mTileId;
    }

    public void SetTileId(int tileId) {
        this.mTileId = tileId;
    }
}
