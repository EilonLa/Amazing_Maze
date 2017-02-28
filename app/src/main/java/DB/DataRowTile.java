package DB;

/**
 * Created by אילון on 31/01/2017.
 */

public class DataRowTile {
    private int mId;
    private int mRow;
    private int mCol;
    private int mIsWall;
    private int mIsEntrance;
    private int mIsExit;

    public DataRowTile (int row, int col,int isWall, int isEntrance, int isExit){
        this.mRow = row;
        this.mCol = col;
        this.mIsWall = isWall;
        this.mIsEntrance = isEntrance;
        this.mIsExit = isExit;
    }

    public int GetIsWall() {
        return mIsWall;
    }

    public int GetIsEntrance() {
        return mIsEntrance;
    }

    public void SetIsEntrance(int isEntrance) {
        this.mIsEntrance = isEntrance;
    }

    public int GetIsExit() {
        return mIsExit;
    }

    public void SetIsExit(int isExit) {
        this.mIsExit = isExit;
    }

    public void SetIsWall(int isWall) {
        this.mIsWall = isWall;
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
}
