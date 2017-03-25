package DB;

/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * Class represents the connection between the user and the board in the DB
 *
 */

public class DataRowUserBoard {
    private int mUserId;
    private int mBoardId;

    public DataRowUserBoard(int userId,int boardId){
        this.mUserId = userId;
        this.mBoardId = boardId;
    }

    public int GetUserId() {
        return mUserId;
    }

    public void SetUserId(int userId) {
        this.mUserId = userId;
    }

    public int GetBoardId() {
        return mBoardId;
    }

    public void SetBoardId(int boardId) {
        this.mBoardId = boardId;
    }
}
