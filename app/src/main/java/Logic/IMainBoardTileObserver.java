package Logic;

import android.app.Activity;

/**
 * Created by אילון on 02/03/2017.
 */

public interface IMainBoardTileObserver {

    void SetSwipedTiles(Tile start);

    void FireClickByLocation(int x, int y);

    void SetTrapFlag(boolean flag);

    void AddStep(Tile tile);

    void SetLocationOnScreenForAllTiles();

    void RemoveStep(Tile tile);

    Tile LastStep();

    void SetGameMode(boolean mGameMode);

    void NotifyStep(Tile tile);

    boolean NeighboursClicked(Tile tile);

    void SetAllBoardNotClickable();

    void InsertIntoStack(Tile tile);

    void PopFromStack();

    boolean HasEntrance();

    void SetHasEntrance(boolean mHasEntrance);

    void clearVisited();

    Activity GetActivity();

    void MakeToast (String text);

    void SetTrapIcon(final int iconId);

    boolean IsEntranceActivated();

    boolean IsExitActivated();

    void SetExitActivated(boolean mExitActivated);

    void SetEntranceActivated(boolean mEntranceActivated);

    void SetExitTile(Tile mExitTile);

    void SetEntranceTile(Tile mEntranceTile);

    Tile GetExitTile();

    boolean IsHasExit();

    void SetHasExit(boolean mHasExit);

    Tile GetEntranceTile();

    boolean IsGameMode ();


}
