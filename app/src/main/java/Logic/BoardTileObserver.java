package Logic;

import android.app.Activity;
import android.widget.Toast;

import java.util.ArrayList;

import UI.Board;
import activities.MainActivity;

/**
 * Created by אילון on 02/03/2017.
 */

public class BoardTileObserver implements IMainBoardTileObserver {
    private Board mBoard;


    public BoardTileObserver (Board board){
        this.mBoard = board;
    }

    @Override
    public void SetSwipedTiles(Tile start) {
        if (mBoard.GetSwipedTiles() == null){
            mBoard.SetSwipedTiles(new ArrayList<Tile>());
            mBoard.GetSwipedTiles().add(start);
        }
        else{
            mBoard.GetSwipedTiles().clear();
            mBoard.SetSwipedTiles(null);
        }
    }

    @Override
    public void FireClickByLocation(int x, int y) {
        for (int i = 0; i < mBoard.GetTiles().length; i++) {
            for (int j = 0; j < mBoard.GetTiles().length; j++) {
                if (mBoard.GetTiles()[i][j].IsInBounds(x,y) && mBoard.GetSwipedTiles() != null && !mBoard.GetSwipedTiles().contains(mBoard.GetTiles()[i][j])) {
                    if (mBoard.GetTiles()[i][j].isClickable()) {
                        mBoard.GetTiles()[i][j].callOnClick();
                        mBoard.GetSwipedTiles().add(mBoard.GetTiles()[i][j]);
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void SetTrapFlag(boolean flag) {
        for (int i = 0; i < Board.NUM_OF_ROWS; i++) {
            for (int j = 0; j < Board.NUM_OF_COLS; j++) {
                mBoard.GetTiles()[i][j].SetTrapFlag(flag);
            }
        }
        for (Tile tile : mBoard.GetStepsTile()) {
            tile.setClickable(flag);
        }
        mBoard.SetIsWaitingForTrapToPick(flag);
    }

    @Override
    public void AddStep(Tile tile) {
        mBoard.GetStepsTile().offer(tile);
    }

    @Override
    public void SetLocationOnScreenForAllTiles() {
        for (int i = 0; i < Board.NUM_OF_ROWS; i++) {
            for (int j = 0; j < Board.NUM_OF_COLS; j++) {
                int[] tempLocation = new int[2];
                mBoard.GetTiles()[i][j].getLocationOnScreen(tempLocation);
                tempLocation[1] -= (mBoard.GetApproveBtn().getHeight() + mBoard.GetTiles()[i][j].getHeight())+10;
                mBoard.GetTiles()[i][j].SetLocationOnScreen(tempLocation);
            }
        }
    }

    @Override
    public void RemoveStep(Tile tile) {
        mBoard.GetStepsTile().remove(tile);
        NotifyStep(mBoard.GetStepsTile().peekLast());
    }

    @Override
    public Tile LastStep() {
        return mBoard.GetStepsTile().peekLast();
    }

    @Override
    public void SetGameMode(boolean mGameMode) {
        for (int i = 0; i < Board.NUM_OF_ROWS; i++) {
            for (int j = 0; j < Board.NUM_OF_COLS; j++) {
                mBoard.GetTiles()[i][j].SetGameMode(mGameMode);
            }
        }
        if (mGameMode)
            NotifyStep(GetEntranceTile());
    }

    @Override
    public void NotifyStep(Tile tile) {
        mBoard.NotifyStep(tile);
    }

    @Override
    public boolean NeighboursClicked(Tile tile) {
        int row = tile.GetRow();
        int col = tile.GetCol();
        if (tile.IsEntrance())
            return true;

        if (row > 0 && mBoard.GetTiles()[row - 1][col].IsStepped())
            return true;
        if (col > 0 && mBoard.GetTiles()[row][col - 1].IsStepped())
            return true;
        if (row < Board.NUM_OF_ROWS - 1 && mBoard.GetTiles()[row + 1][col].IsStepped())
            return true;
        if (col < Board.NUM_OF_COLS - 1 && mBoard.GetTiles()[row][col + 1].IsStepped())
            return true;

        return false;    }

    @Override
    public void SetAllBoardNotClickable() {
        mBoard.SetAllBoardNotClickable();
    }

    @Override
    public void InsertIntoStack(Tile tile) {
        mBoard.GetStack().push(tile);
    }

    @Override
    public void PopFromStack() {
        mBoard.PopFromStack();
    }

    @Override
    public boolean HasEntrance() {
        return mBoard.HasEntrance();
    }

    @Override
    public void SetHasEntrance(boolean hasEntrance) {
        mBoard.SetHasEntrance(hasEntrance);
    }

    @Override
    public void clearVisited() {
        for (int i = 0; i < Board.NUM_OF_ROWS - 1; i++)
            for (int j = 0; j < Board.NUM_OF_COLS - 1; j++)
                mBoard.GetTiles()[i][j].SetVisited(false);

        if (GetEntranceTile() != null)
            GetEntranceTile().SetVisited(false);
        if (GetExitTile() != null)
            GetExitTile().SetVisited(false);
    }

    @Override
    public MainActivity GetActivity() {
        return mBoard.GetActivity();
    }

    @Override
    public void MakeToast(final String text) {
        mBoard.GetActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mBoard.GetActivity(),text,Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void SetTrapIcon(final int iconId) {
        mBoard.SetTrapIcon(iconId);
    }

    @Override
    public boolean IsEntranceActivated() {
        return mBoard.IsEntranceActivated();
    }

    @Override
    public boolean IsExitActivated() {
       return mBoard.IsExitActivated();
    }

    @Override
    public void SetExitActivated(boolean exitActivated) {
        mBoard.SetExitActivated( exitActivated);
    }

    @Override
    public void SetEntranceActivated(boolean entranceActivated) {
        mBoard.SetEntranceActivated(entranceActivated);
    }

    @Override
    public void SetExitTile(Tile exitTile) {
        mBoard.SetExitTile(exitTile);
    }

    @Override
    public void SetEntranceTile(Tile entranceTile) {
        mBoard.SetEntranceTile(entranceTile);
    }

    @Override
    public Tile GetExitTile() {
        return mBoard.GetExitTile();
    }

    @Override
    public boolean IsHasExit() {
        return mBoard.IsHasExit();
    }

    @Override
    public void SetHasExit(boolean mHasExit) {
        mBoard.SetHasExit(mHasExit);
    }

    @Override
    public Tile GetEntranceTile() {
        return mBoard.GetEntranceTile();
    }

    @Override
    public boolean IsGameMode() {
        return mBoard.IsGameMode();
    }
}
