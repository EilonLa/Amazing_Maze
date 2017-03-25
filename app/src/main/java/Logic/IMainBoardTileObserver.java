package Logic;

import android.app.Activity;

import activities.MainActivity;

/**
 * Created by אילון on 02/03/2017.
 */

public interface IMainBoardTileObserver {

    void SetSwipedTiles(Tile start) throws Exception ;

    void FireClickByLocation(int x, int y) throws Exception ;

    void SetTrapFlag(boolean flag) throws Exception ;

    void AddStep(Tile tile) throws Exception ;

    void SetLocationOnScreenForAllTiles() throws Exception ;

    void RemoveStep(Tile tile) throws Exception ;

    Tile LastStep() throws Exception ;

    void SetGameMode(boolean mGameMode) throws Exception ;

    void NotifyStep(Tile tile) throws Exception ;

    boolean NeighboursClicked(Tile tile) throws Exception ;

    void SetAllBoardClickable(boolean clickable) throws Exception ;

    void InsertIntoStack(Tile tile) throws Exception ;

    void PopFromStack() throws Exception ;

    boolean HasEntrance() throws Exception ;

    void SetHasEntrance(boolean mHasEntrance) throws Exception ;

    Activity GetActivity() throws Exception ;

    void MakeToast (String text) throws Exception ;

    void SetTrapIcon(final int iconId) throws Exception ;

    boolean IsEntranceActivated() throws Exception ;

    boolean IsExitActivated() throws Exception ;

    void SetExitActivated(boolean mExitActivated) throws Exception ;

    void SetEntranceActivated(boolean mEntranceActivated) throws Exception ;

    void SetExitTile(Tile mExitTile) throws Exception ;

    void SetEntranceTile(Tile mEntranceTile) throws Exception ;

    Tile GetExitTile() throws Exception ;

    boolean IsHasExit() throws Exception ;

    void SetHasExit(boolean mHasExit) throws Exception ;

    Tile GetEntranceTile() throws Exception ;

    boolean IsGameMode () throws Exception ;



}
