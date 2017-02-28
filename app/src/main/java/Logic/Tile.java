package Logic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
    private Board mBoard;
    private Tile mSelf;
    private Activity mActivity;
    private Trap mTrap;
    private Trap mCounterTrap;
    private int[] mLocationOnScreen;

    public Tile(final Activity activity, int row, int col, final Board board) {
        super(activity);
        this.mActivity = activity;
        SetIcon(mBrickIconId);
        this.mBoard = board;
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
                        mBoard.SetLocationOnScreenForAllTiles();
                        mBoard.SetSwipedTiles(mSelf);
                    }
                    return true;
                }

                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE ) {
                        mBoard.FireClickByLocation(x, y);
                        return true;
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP ) {
                        mBoard.FireClickByLocation(x, y);
                    if (isClickable()) {
                        mBoard.SetSwipedTiles(mSelf);
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
                    mBoard.InsertIntoStack(mSelf);
                }
                if (mGameMode) {//the tile is clicked in game mode
                    if (mTrapFlag && mStepped) {//if choosing to place a trap and the tile is part of the path
                        if (mProtected) {//if the tile already has a defence trap
                            SetIcon(mRoadTile);
                            mProtected = false;
                        } else {
                            SetIcon(mProtectedIcon);
                            //ApproveTrapDialog();
                            mBoard.SetTrapFlag(false);
                            mProtected = true;
                        }

                    } else {//if game mode is on and walking the path
                        if (!mIsWall) {//road tile
                            if (!mStepped) {//tile is not stepped
                                if (mBoard.NeighboursClicked(mSelf)) {//if the tile's neighbours are clicked
                                    SetIcon(mRoadTile);
                                    mStepped = true;
                                    mBoard.AddStep(mSelf);//add the step to the path list in the board
                                    mBoard.NotifyStep(mSelf);//set the next tiles clickable
                                }
                            } else {//cancle last step
                                if (mBoard.LastStep() == mSelf) {//pnly last step is possible
                                    if (!mIsEntrance && !mIsExit)
                                        SetIcon(mFloorIconId);
                                    else if (mIsEntrance)
                                        SetIcon(R.mipmap.entrance_icon);
                                    else if (mIsExit)
                                        SetIcon(R.mipmap.exit_icon);
                                    mStepped = false;
                                    mBoard.RemoveStep(mSelf);
                                } else {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(),ONE_STEP_TAG , Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }
                    }
                } else {//create game mode
                    if (mIsWall) {//tile is a wall
                        if (mTrap != null) {//if tile has a trap, show it
                            mBoard.SetTrapIcon(mTrap.GetIconId());
                            SetIcon(mTrap.GetIconId());
                        }
                        else {
                            mBoard.SetTrapIcon(0);
                            SetIcon(mFloorIconId);
                        }
                        mIsWall = false;
                        if (IsBoundary()) {//if tile is on one of the edges
                            if (!mBoard.HasEntrance() && mBoard.ismEntranceActivated()) {//board has no entrance and setEntrance is clicked
                                mIsEntrance = true;
                                mIsExit = false;
                                SetIcon(R.mipmap.entrance_icon);
                                mBoard.setmEntranceActivated(false);
                                mBoard.SetHasEntrance(true);
                                mBoard.setmEntranceTile(mSelf);
                                if (mBoard.getmExitTile() == mSelf) {
                                    mBoard.setmExitTile(null);
                                }
                            }
                            if (!mBoard.ismHasExit() && mBoard.ismExitActivated()) {//board has no exit and setExit is activated
                                mIsExit = true;
                                mIsEntrance = false;
                                mBoard.setmExitActivated(false);
                                mBoard.setmHasExit(true);
                                SetIcon(R.mipmap.exit_icon);
                                mBoard.setmExitTile(mSelf);
                                if (mBoard.getmEntranceTile() == mSelf) {
                                    mBoard.setmEntranceTile(null);
                                }
                            }
                        }
                    } else {//if tile is not a wall
                        if (mTrap != null){
                            MainActivity.mUser.AddToCoins(mTrap.GetPrice());
                            MainActivity.mUser.GetTraps().add(mTrap);
                            SetTrap(null);
                        }
                        if (mIsEntrance) {
                            mBoard.SetHasEntrance(false);
                            mIsEntrance = false;
                            mBoard.setmEntranceTile(null);
                        }
                        if (mIsExit) {
                            mBoard.setmHasExit(false);
                            mIsExit = false;
                            mBoard.setmExitTile(null);
                        }
                        mBoard.SetTrapIcon(0);
                        SetIcon(mBrickIconId);
                        mIsWall = true;
                    }
                }
            }
        });
    }



    public void ApproveTrapDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        mBoard.SetTrapFlag(false);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        if (!mSelf.GetTrap().IsAttack())
                            mSelf.SetTrap(null);
                        dialog.cancel();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage("Are you sure you want to add a trap at [" + mRow + "]" + "[" + mCol + "]?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

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

    public void SetTrap(Trap trap) {
        if (!mBoard.IsGameMode()) {
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

    public boolean IsGameMode() {
        return mGameMode;
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
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setBackgroundResource(iconId);
            }
        });
    }
}
