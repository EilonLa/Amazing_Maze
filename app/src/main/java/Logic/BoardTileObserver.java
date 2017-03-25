package Logic;

import android.widget.Toast;

import java.util.ArrayList;

import UI.Board;
import activities.MainActivity;

/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * The BoardTileObserver is in charge of all communication between tiles and board
 *
 */
public class BoardTileObserver implements IMainBoardTileObserver {
    private Board mBoard;


    public BoardTileObserver (Board board){
        this.mBoard = board;
    }

    /**
     * For us to be able to swipe across the tiles without multiple clickes, meaning per swipe tile will be pressed once
     * @param start
     */
    @Override
    public void SetSwipedTiles(Tile start)throws Exception {
        if (mBoard.GetSwipedTiles() == null){
            mBoard.SetSwipedTiles(new ArrayList<Tile>());
            mBoard.GetSwipedTiles().add(start);
        }
        else{
            mBoard.GetSwipedTiles().clear();
            mBoard.SetSwipedTiles(null);
        }
    }

    /**
     * Gets the location coordinate on screen, searches for thee tile in that location and calling onClick
     * @param x
     * @param y
     */
    @Override
    public void FireClickByLocation(int x, int y)throws Exception {
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

    /**
     * When choosing a tile to place a trap the onClick is different therefor the tile needs to be notified
     * @param flag
     */
    @Override
    public void SetTrapFlag(boolean flag)throws Exception {
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


    /**
     * When playing the game, each step is saved
     * @param tile
     */
    @Override
    public void AddStep(Tile tile)throws Exception {
        mBoard.GetStepsTile().offer(tile);
    }

    /**
     * Iterates over the list of tiles and sets the tile's location on screen
     */
    @Override
    public void SetLocationOnScreenForAllTiles()throws Exception {
        for (int i = 0; i < Board.NUM_OF_ROWS; i++) {
            for (int j = 0; j < Board.NUM_OF_COLS; j++) {
                int[] tempLocation = new int[2];
                mBoard.GetTiles()[i][j].getLocationOnScreen(tempLocation);
                tempLocation[1] -= (mBoard.GetApproveBtn().getHeight() + mBoard.GetTiles()[i][j].getHeight())+10;
                mBoard.GetTiles()[i][j].SetLocationOnScreen(tempLocation);
            }
        }
    }

    /**
     * When we are setting the Entrance or Exit tiles the background color of the ImageView is different.
     * Because' after setting it we need to set the background back
     */
    public void ResetView()throws Exception{
        mBoard.GetActivity().GetController().ResetView();
    }

    /**
     * Called when a step is reverted
     * @param tile
     */
    @Override
    public void RemoveStep(Tile tile)throws Exception {
        mBoard.GetStepsTile().remove(tile);
        NotifyStep(mBoard.GetStepsTile().peekLast());
    }

    /**
     * @return the last step that was performed
     */
    @Override
    public Tile LastStep()throws Exception {
        return mBoard.GetStepsTile().peekLast();
    }

    /**
     * set's the game mode (yes or no) for all the tiles.
     * @param mGameMode
     */
    @Override
    public void SetGameMode(boolean mGameMode)throws Exception {
        for (int i = 0; i < Board.NUM_OF_ROWS; i++) {
            for (int j = 0; j < Board.NUM_OF_COLS; j++) {
                mBoard.GetTiles()[i][j].SetGameMode(mGameMode);
            }
        }
        if (mGameMode)
            NotifyStep(GetEntranceTile());
    }

    /**
     *
     * When playing the game each step opens the tiles neighbours for clicking and set's the rest of the board not clickable
     * @param tile
     */
    @Override
    public void NotifyStep(Tile tile)throws Exception {
        mBoard.NotifyStep(tile);
    }

    /**
     * @param tile
     * @return true if neighbours are clicked and false if not
     */
    @Override
    public boolean NeighboursClicked(Tile tile)throws Exception {
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
    public void SetAllBoardClickable(boolean clickable)throws Exception {
        mBoard.SetAllBoardClickable(clickable);
    }

    /**
     * Inserts the tile into the board's stack of clicked tiles
     * @param tile
     */
    @Override
    public void InsertIntoStack(Tile tile) {
        mBoard.GetStack().push(tile);
    }

    /**
     *
     */
    @Override
    public void PopFromStack()throws Exception {
        mBoard.PopFromStack();
    }

    @Override
    public boolean HasEntrance()throws Exception {
        return mBoard.HasEntrance();
    }

    @Override
    public void SetHasEntrance(boolean hasEntrance)throws Exception {
        mBoard.SetHasEntrance(hasEntrance);
    }

    @Override
    public MainActivity GetActivity()throws Exception {
        return mBoard.GetActivity();
    }

    @Override
    public void MakeToast(final String text)throws Exception {
        mBoard.GetActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mBoard.GetActivity(),text,Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * sets  the trap icon at the screen when clicked
     * @param iconId
     */
    @Override
    public void SetTrapIcon(final int iconId)throws Exception {
        mBoard.SetTrapIcon(iconId);
    }

    @Override
    public boolean IsEntranceActivated()throws Exception {
        return mBoard.IsEntranceActivated();
    }

    @Override
    public boolean IsExitActivated()throws Exception {
       return mBoard.IsExitActivated();
    }

    @Override
    public void SetExitActivated(boolean exitActivated)throws Exception {
        mBoard.SetExitActivated( exitActivated);
    }

    @Override
    public void SetEntranceActivated(boolean entranceActivated)throws Exception {
        mBoard.SetEntranceActivated(entranceActivated);
    }

    @Override
    public void SetExitTile(Tile exitTile)throws Exception {
        mBoard.SetExitTile(exitTile);
    }

    @Override
    public void SetEntranceTile(Tile entranceTile)throws Exception {
        mBoard.SetEntranceTile(entranceTile);
    }

    @Override
    public Tile GetExitTile()throws Exception {
        return mBoard.GetExitTile();
    }

    @Override
    public boolean IsHasExit()throws Exception {
        return mBoard.IsHasExit();
    }

    @Override
    public void SetHasExit(boolean mHasExit)throws Exception {
        mBoard.SetHasExit(mHasExit);
    }

    @Override
    public Tile GetEntranceTile()throws Exception {
        return mBoard.GetEntranceTile();
    }

    @Override
    public boolean IsGameMode()throws Exception {
        return mBoard.IsGameMode();
    }
}
