package DB;

/**
 * Created by אילון on 31/01/2017.
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
