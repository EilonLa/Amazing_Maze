package DB;

/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * Class represents the tile in the DB
 *
 */
public class DataRowTile {
    private String mId;
    private int mRow;
    private int mCol;
    private int mIsWall;
    private int mIsEntrance;
    private int mIsExit;

    public DataRowTile (String id,int row, int col,int isWall, int isEntrance, int isExit){
        this.mId = id;
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
}
