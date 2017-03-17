package Logic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.cdv.amazingmaze.R;

import UI.Board;
import activities.MainActivity;

/**
 * Created by אילון on 26/01/2017.
 */

public class Tile extends ImageView {
    private final String ONE_STEP_TAG = "Take one step back at a time!";
    private final int mBrickIconId = R.mipmap.brick_tile;
    private final int mFloorIconId = R.mipmap.floor_tile;
    private final int mProtectedIcon = R.mipmap.protected_square;
    private final int mRoadTile = R.mipmap.yellow_squares;
    private boolean mIsWall = true;
    private boolean mIsEntrance = false;
    private boolean mIsExit = false;
    private boolean mFromStack = false;
    private boolean mVisited = false;
    private boolean mProtected = false;
    private boolean mGameMode;
    private boolean mStepped = false;
    private boolean mTrapFlag = false;
    private int mRow;
    private int mCol;
    private Tile mSelf;
    private Trap mTrap;
    private Trap mCounterTrap;
    private int[] mLocationOnScreen;
    private BoardTileObserver mObserver;

    private String mTileId;

    public Tile(int row, int col, BoardTileObserver observer) {
        super(observer.GetActivity());
        mObserver = observer;
        SetIcon(mBrickIconId);
        this.mRow = row;
        this.mCol = col;
        this.mSelf = this;
        mLocationOnScreen = new int[2];
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int x = (int) motionEvent.getRawX();
                int y = (int) motionEvent.getRawY();
                y -= (Board.mApprove.getHeight() + getHeight());

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isClickable()) {
                        callOnClick();
                        mObserver.SetLocationOnScreenForAllTiles();
                        mObserver.SetSwipedTiles(mSelf);
                    }
                    return true;
                }

                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE ) {
                        mObserver.FireClickByLocation(x, y);
                        return true;
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP ) {
                        mObserver.FireClickByLocation(x, y);
                    if (isClickable()) {
                        mObserver.SetSwipedTiles(mSelf);
                    }
                        return true;
                }

                return false;
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mFromStack) {//tile is not called from the stack.pop() call
                    mObserver.InsertIntoStack(mSelf);
                }
                if (mGameMode) {//the tile is clicked in game mode
                    if (mTrapFlag && mStepped) {//if choosing to place a trap and the tile is part of the path
                        if (mProtected) {//if the tile already has a defence trap
                            SetIcon(mRoadTile);
                            mProtected = false;
                        } else {
                            SetIcon(mProtectedIcon);
                            //ApproveTrapDialog();
                            mObserver.SetTrapFlag(false);
                            mProtected = true;
                        }

                    } else {//if game mode is on and walking the path
                        if (!mIsWall) {//road tile
                            if (!mStepped) {//tile is not stepped
                                if (mObserver.NeighboursClicked(mSelf)) {//if the tile's neighbours are clicked
                                    SetIcon(mRoadTile);
                                    mStepped = true;
                                    mObserver.AddStep(mSelf);//add the step to the path list in the board
                                    mObserver.NotifyStep(mSelf);//set the next tiles clickable
                                }
                            } else {//cancle last step
                                if (mObserver.LastStep() == mSelf) {//pnly last step is possible
                                    if (!mIsEntrance && !mIsExit)
                                        SetIcon(mFloorIconId);
                                    else if (mIsEntrance)
                                        SetIcon(R.mipmap.entrance_icon);
                                    else if (mIsExit)
                                        SetIcon(R.mipmap.exit_icon);
                                    mStepped = false;
                                    mObserver.RemoveStep(mSelf);
                                } else {
                                    mObserver.MakeToast(ONE_STEP_TAG);
                                }
                            }
                        }
                    }
                } else {//create game mode
                    if (mIsWall) {//tile is a wall
                        if (mTrap != null) {//if tile has a trap, show it
                            mObserver.SetTrapIcon(mTrap.GetIconId());
                            SetIcon(mTrap.GetIconId());
                        }
                        else {
                            mObserver.SetTrapIcon(0);
                            SetIcon(mFloorIconId);
                        }
                        mIsWall = false;
                        if (IsBoundary()) {//if tile is on one of the edges
                            if (!mObserver.HasEntrance() && mObserver.IsEntranceActivated()) {//board has no entrance and setEntrance is clicked
                                mIsEntrance = true;
                                mIsExit = false;
                                SetIcon(R.mipmap.entrance_icon);
                                mObserver.SetEntranceActivated(false);
                                mObserver.SetHasEntrance(true);
                                mObserver.SetEntranceTile(mSelf);
                                if (mObserver.GetExitTile() == mSelf) {
                                    mObserver.SetExitTile(null);
                                }
                            }
                            if (!mObserver.IsHasExit() && mObserver.IsExitActivated()) {//board has no exit and setExit is activated
                                mIsExit = true;
                                mIsEntrance = false;
                                mObserver.SetExitActivated(false);
                                mObserver.SetHasExit(true);
                                SetIcon(R.mipmap.exit_icon);
                                mObserver.SetExitTile(mSelf);
                                if (mObserver.GetEntranceTile() == mSelf) {
                                    mObserver.SetEntranceTile(null);
                                }
                            }
                        }
                    } else {//if tile is not a wall
                        if (mTrap != null){
                            mObserver.GetActivity().GetController().GetUser().AddToCoins(mTrap.GetPrice());
                            mObserver.GetActivity().GetController().GetUser().GetTraps().add(mTrap);
                            SetTrap(null);
                        }
                        if (mIsEntrance) {
                            mObserver.SetHasEntrance(false);
                            mIsEntrance = false;
                            mObserver.SetEntranceTile(null);
                        }
                        if (mIsExit) {
                            mObserver.SetHasExit(false);
                            mIsExit = false;
                            mObserver.SetExitTile(null);
                        }
                        mObserver.SetTrapIcon(0);
                        SetIcon(mBrickIconId);
                        mIsWall = true;
                    }
                }
            }
        });
    }

    public String GetTileId() {
        return mTileId;
    }

    public void SetTileId(String tileId) {
        this.mTileId = tileId;

    }

    public void SetLocationOnScreen(int[] location){
        mLocationOnScreen = location;
    }

    public Trap getCounterTrap() {
        return mCounterTrap;
    }

    public void setCounterTrap(Trap counterTrap) {
        this.mCounterTrap = counterTrap;
    }

    public boolean IsProtected() {
        return mProtected;
    }

    public boolean IsStepped() {
        return mStepped;
    }

    public void SetStepped(boolean mStepped) {
        this.mStepped = mStepped;
    }

    public boolean IsBoundary() {
        return mRow == Board.NUM_OF_ROWS - 1 || mRow == 0 || mCol == Board.NUM_OF_COLS - 1 || mCol == 0;
    }

    public Trap GetTrap() {
        return mTrap;
    }

    public int GetTrapIndex(){
        if (mTrap != null)
            return mTrap.GetTrapIndex();
        else{
            return -1;
        }
    }

    public void SetTrap(Trap trap) {
        if (!mObserver.IsGameMode()) {
            this.mTrap = trap;
            if (mTrap != null) {
                mTrap.SetTile(mSelf);
                SetIcon(mTrap.GetIconId());
            }else{
                SetIcon(mFloorIconId);
            }
        } else {//gameMode
            if (mTrap == null) {
                this.mTrap = trap;
            } else {
                if (mTrap.IsAttack() && !trap.IsAttack()) {
                    setCounterTrap(trap);
                    trap.SetTile(mSelf);
                } else {
                    this.mTrap = trap;
                    if (mTrap != null) {
                        mTrap.SetTile(mSelf);
                    }
                }
            }
        }
    }

    public void SetGameMode(boolean mGameMode) {
        this.mGameMode = mGameMode;
        if (mGameMode)
            this.setClickable(false);
    }

    public int[] getLocationOnScreen() {
        return mLocationOnScreen;
    }

    public boolean IsInBounds(int x, int y) {
        int a = mLocationOnScreen[0];
        int b = mLocationOnScreen[1];
        return x < a + getWidth() && x >=a && y < b + getHeight() && y >= b;
    }

    public void SetFromStack(boolean isFromStack) {
        this.mFromStack = isFromStack;
    }

    public boolean IsExit() {
        return mIsExit;
    }

    public void SetIsExit(boolean mIsExit) {
        this.mIsExit = mIsExit;
        if (mIsExit == true)
            SetIcon(R.mipmap.exit_icon);
    }

    public boolean IsEntrance() {
        return mIsEntrance;
    }

    public void SetIsEntrance(boolean mIsEntrance) {
        this.mIsEntrance = mIsEntrance;
        if (mIsEntrance == true)
            SetIcon(R.mipmap.entrance_icon);
    }

    public void SetTrapFlag(boolean flag) {
        this.mTrapFlag = flag;
    }

    public boolean IsWall() {
        return mIsWall;
    }

    public void SetIsWall(boolean isWall) {
        this.mIsWall = isWall;
        if (!isWall && !IsExit() && !IsEntrance())
            SetIcon(mFloorIconId);
    }

    public int GetRow() {
        return mRow;
    }

    public void SetRow(int mRow) {
        this.mRow = mRow;
    }

    public boolean IsVisited() {
        return mVisited;
    }

    public void SetVisited(boolean mVisited) {
        this.mVisited = mVisited;
    }

    public String toString() {
        return "[" + mRow + "]" + "[" + mCol + "]";
    }

    public int GetCol() {
        return mCol;
    }

    public void SetCol(int mCol) {
        this.mCol = mCol;
    }

    public void SetIsProtected(boolean isProtected) {
        this.mProtected = isProtected;
    }

    public void SetIcon(final int iconId) {
        mObserver.GetActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setBackgroundResource(iconId);
            }
        });
    }
}
