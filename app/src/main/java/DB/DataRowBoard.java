package DB;

/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * Class represents the connection between the tile and the board in the DB
 *
 */
public class DataRowBoard {
    private String mId;
    private int mRow;
    private int mCol;
    private String mTileId;
    private boolean mIsEntrance;
    private boolean mIsExit;
    private boolean mIsWall;
    private int mTrapIndex;
    private boolean mIsFromFireBase;

    public DataRowBoard (String userId, int row,int col, String tileId){
        this.mId = userId;
        this.mRow = row;
        this.mCol = col;
        this.mTileId = tileId;
        mIsFromFireBase = false;
    }

    public DataRowBoard (String userId, int row,int col, String tileId, boolean isEntrance, boolean isExit,boolean isWall, int trapIndex){
        this.mId = userId;
        this.mRow = row;
        this.mCol = col;
        this.mTileId = tileId;
        this.mIsEntrance = isEntrance;
        this.mIsWall = isWall;
        this.mIsExit = isExit;
        this.mTrapIndex = trapIndex;
        mIsFromFireBase = true;
    }

    public boolean IsFromFireBase(){
        return mIsFromFireBase;
    }

    public int GetTrapIndex() {
        return mTrapIndex;
    }

    public void SetTrapIndex(int trapIndex) {
        this.mTrapIndex = trapIndex;
    }

    public boolean IsWall() {
        return mIsWall;
    }

    public void SetWall(boolean wall) {
        mIsWall = wall;
    }

    public boolean IsEntrance() {
        return mIsEntrance;
    }

    public void SetEntrance(boolean entrance) {
        mIsEntrance = entrance;
    }

    public boolean isExit() {
        return mIsExit;
    }

    public void setExit(boolean exit) {
        mIsExit = exit;
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
