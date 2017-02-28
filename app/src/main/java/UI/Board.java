package UI;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.cdv.amazingmaze.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import DB.DataRowBoard;
import DB.DataRowTile;
import DB.DataRowTileTrap;
import Logic.BFS;
import Logic.Tile;
import Logic.Trap;
import activities.GamePlay;
import activities.MainActivity;
import activities.PopItems;
import activities.ResultScreen;


/**
 * Created by אילון on 28/01/2017.
 */

public class Board extends ImageView {
    public static final String FINISH_THE_PATH_TAG = "Finish the path to the end before adding shields!";
    public static final String FROM_CREATE_TAG = "from_create";
    public static final String FROM_GAME_TAG = "from_game";
    public static final String CHOOSE_TAG = "Choose a tile for the start of the trap";
    public static final String ADD_SHIELD_TAG = "Would you like to add a shield before walking the path?";
    public static final String NO_PATH_TAG = "You do not have a path from start to end!";
    public static final String MAZE_SAVED_TAG = "Maze saved!";
    public final int TOP_BAR_HEIGHT = 220;
    public final int BOTTOM_BAR_HEIGHT = 280;
    public static final int NUM_OF_ROWS = 18;
    public static final int NUM_OF_COLS = 20;
    public static int PROTECTION = 0;
    private ArrayList<Thread> mAllThreads;
    private boolean mHasEntrance = false;
    private boolean mHasExit = false;
    private boolean mExitActivated = false;
    private boolean mEntranceActivated = false;
    private static boolean mIsWaitingForTrapToPick = false;
    private boolean mDIdPlayerWin = false;
    private boolean mGameMode;
    private static Tile[][] mTiles;
    private Activity mActivity;
    private LinearLayout mBoardLayout;
    public static Stack<Tile> mStack;
    private Button mUndo;
    public static Button mApprove;
    private Button mChest;
    private Button mRemoveTrap;
    private ImageView mTrapIcon;
    private Tile mEntranceTile;
    private Tile mExitTile;
    public ArrayList<Tile> mProtectedTiles = new ArrayList<>();
    private ArrayList<DataRowBoard> mDataForRow;
    private ObjectAnimator objectAnimator;
    private int mBoardId;
    private static LinkedList<Tile> mStepsTile;
    private ArrayList<Tile> mSwipedTiles;


    public Board(final Activity mActivity, ArrayList<DataRowBoard> mDataForRow, int mBoardId, final boolean mGameMode) {
        super(mActivity);
        this.mActivity = mActivity;
        this.mDataForRow = mDataForRow;
        this.mBoardId = mBoardId;
        this.mGameMode = mGameMode;
        mAllThreads = new ArrayList<>();
        SetButtons();
        mBoardLayout = SetMaze();
        SetGameMode(mGameMode);
        mStack = new Stack<>();
        mUndo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                PopFromStack();
            }
        });
        mApprove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mGameMode) {
                    SaveCreatedMaze();
                } else {//game mode
                    ApprovePath();
                }
            }
        });
        mChest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(mActivity, PopItems.class);
                        if (mGameMode) {
                            if (CheckPath()) {
                                mChest.setClickable(false);
                                SetLocationOnScreen();
                                OpenChest();
                            } else {
                                Toast.makeText(mActivity, FINISH_THE_PATH_TAG, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            i.putExtra(FROM_CREATE_TAG, true);
                            mActivity.startActivity(i);
                        }
                    }
                });
            }
        });
        mRemoveTrap.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mStack.isEmpty()) {
                    Tile tile = mStack.peek();
                    if (tile.GetTrap() != null) {
                        Trap trapToRemove = tile.GetTrap();
                        MainActivity.mUser.GetTraps().add(0, trapToRemove);
                        tile.SetTrap(null);
                        SetTrapIcon(0);
                    }
                }
            }
        });
        mStepsTile = new LinkedList<>();
    }


    public Tile[][] GetTiles() {
        return mTiles;
    }

    public void KillAllRunningThreads() {
        for (Thread thread : mAllThreads) {
            if (thread.isAlive())
                thread.interrupt();
        }
    }

    public void SetSwipedTiles(Tile start){
        if (mSwipedTiles == null){
            mSwipedTiles = new ArrayList<>();
            mSwipedTiles.add(start);
        }
        else{
            mSwipedTiles.clear();
            mSwipedTiles = null;
        }
    }

    public void FireClickByLocation(int x, int y) {
        for (int i = 0; i < mTiles.length; i++) {
            for (int j = 0; j < mTiles.length; j++) {
                if (mTiles[i][j].IsInBounds(x,y) && mSwipedTiles != null && !mSwipedTiles.contains(mTiles[i][j])) {
                    if (mTiles[i][j].isClickable()) {
                        mTiles[i][j].callOnClick();
                        mSwipedTiles.add(mTiles[i][j]);
                    }
                    return;
                }
            }
        }
    }

    public void AnimatePath() {
        SetLocationOnScreen();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView avatar = (ImageView) mActivity.findViewById(R.id.avatar);
                avatar.setBackgroundResource(R.mipmap.blue_ball);
                avatar.bringToFront();
                avatar.setVisibility(VISIBLE);
                Iterator<Tile> itr = mStepsTile.iterator();
                Tile from = null;
                Tile to = null;
                int delay = 0;
                if (itr.hasNext())
                    from = itr.next();
                while (itr.hasNext()) {
                    to = itr.next();
                    ObjectAnimator ox = ObjectAnimator.ofFloat(avatar, "x", (float) from.getLocationOnScreen()[0], (float) to.getLocationOnScreen()[0]);
                    ox.setDuration(500);
                    ObjectAnimator oy = ObjectAnimator.ofFloat(avatar, "y", (float) from.getLocationOnScreen()[1], (float) to.getLocationOnScreen()[1]);
                    oy.setDuration(500);
                    ox.setTarget(avatar);
                    oy.setTarget(avatar);
                    ox.setStartDelay(delay);
                    oy.setStartDelay(delay);
                    ox.start();
                    oy.start();
                    if (!to.IsProtected() && to.GetTrap() != null && to.GetTrap().IsAttack() && to.getCounterTrap() == null) {
                        SetTrapAnimationTile(to, delay + 400, avatar);
                        mDIdPlayerWin = false;
                        break;
                    }
                    if (to.GetTrap() != null)
                        if (to.IsProtected() && to.GetTrap() != null && to.GetTrap().IsAttack()) {
                            SetCounterTrapAnimation(to, delay + 400);
                        }
                    from = to;
                    delay += 400;
                }
                if (mDIdPlayerWin) {
                    final int finalDelay = delay;
                    mAllThreads.add(
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(finalDelay + 2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    MainActivity.mUser.AddToCoins(GamePlay.VALUE_FOR_WIN);
                                    Intent resultScreen = new Intent(mActivity.getApplicationContext(), ResultScreen.class);
                                    resultScreen.putExtra("player_win", true);
                                    mActivity.startActivity(resultScreen);
                                    KillAllRunningThreads();
                                    mActivity.finish();
                                }
                            }));
                    mAllThreads.get(mAllThreads.size() - 1).start();
                }
            }
        });

    }

    public void OpenChest() {
        SetTrapFlag(true);
        mAllThreads.add(
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mActivity, CHOOSE_TAG, Toast.LENGTH_LONG).show();
                            }
                        });

                        while (mIsWaitingForTrapToPick) {
                        }
                        mChest.setClickable(true);
                        Intent i = new Intent(getContext(), PopItems.class);
                        i.putExtra(FROM_GAME_TAG, true);
                        mActivity.startActivity(i);
                    }
                }));
        mAllThreads.get(mAllThreads.size() - 1).start();

    }



    public static void CompleteShieldLayout(Tile tile, Trap trap) {
        boolean reachedTile = false;;
        for (Tile tempTile : mStepsTile) {
            if (tempTile == tile) {
                reachedTile = true;
            }
            if (reachedTile && PROTECTION > 0) {
                tempTile.SetIcon(R.mipmap.protected_square);
                tempTile.SetTrapFlag(true);
                tempTile.SetIsProtected(true);
                tempTile.setCounterTrap(trap);
                PROTECTION--;
            }
        }
        PROTECTION = 0;
    }

    public boolean IsGameMode(){
        return mGameMode;
    }

    public void TrapDialog() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                SetTrapFlag(true);
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mActivity, CHOOSE_TAG, Toast.LENGTH_LONG).show();
                                    }
                                });
                                mAllThreads.add(
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                while (mIsWaitingForTrapToPick) {
                                                }
                                                CompleteShieldLayout(mStack.peek(), mStack.peek().getCounterTrap());
                                                Intent i = new Intent(getContext(), PopItems.class);
                                                i.putExtra(FROM_GAME_TAG, true);
                                                mActivity.startActivity(i);
                                            }
                                        }));
                                mAllThreads.get(mAllThreads.size() - 1).start();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                AnimatePath();
                                dialog.cancel();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setMessage(ADD_SHIELD_TAG).setPositiveButton("Protect me", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
    }

    public static void SetTrapFlag(boolean flag) {
        for (int i = 0; i < NUM_OF_ROWS; i++) {
            for (int j = 0; j < NUM_OF_COLS; j++) {
                mTiles[i][j].SetTrapFlag(flag);
            }
        }
        for (Tile tile : mStepsTile) {
            tile.setClickable(flag);
        }
        mIsWaitingForTrapToPick = flag;
    }

    public void AddStep(Tile tile) {
        mStepsTile.offer(tile);
    }

    public void SetLocationOnScreen() {
        for (Tile tile : mStepsTile) {
            int[] step = new int[2];
            tile.getLocationOnScreen(step);
            step[1] -= (mApprove.getHeight() + tile.getHeight());
            tile.SetLocationOnScreen(step);
        }
    }

    public void SetLocationOnScreenForAllTiles(){
        for (int i = 0; i < NUM_OF_ROWS; i++) {
            for (int j = 0; j < NUM_OF_COLS; j++) {
                int[] tempLocation = new int[2];
                mTiles[i][j].getLocationOnScreen(tempLocation);
                tempLocation[1] -= (mApprove.getHeight() + mTiles[i][j].getHeight())+10;
                mTiles[i][j].SetLocationOnScreen(tempLocation);
            }
        }
    }


    public void SetTrapAnimationTile(final Tile tile, final int delay, final ImageView avatar) {
        mAllThreads.add(
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                avatar.setVisibility(INVISIBLE);
                                tile.SetIcon(R.drawable.blast);
                                AnimationDrawable blast = (AnimationDrawable) tile.getBackground();
                                blast.start();
                            }
                        });
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent resultScreen = new Intent(mActivity.getApplicationContext(), ResultScreen.class);
                        resultScreen.putExtra("player_win", false);
                        //TODO: ADD COINS TO WINNER THROUGH FIREBASE
                        KillAllRunningThreads();
                        mActivity.startActivity(resultScreen);
                        mActivity.finish();

                    }
                }));
        mAllThreads.get(mAllThreads.size() - 1).start();
    }

    public void SetCounterTrapAnimation(final Tile tile, final int delay) {
        mAllThreads.add(
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tile.SetIcon(R.mipmap.star);
                                ObjectAnimator rotator = ObjectAnimator.ofFloat(tile, "rotationY", 360);
                                rotator.setDuration(1000);
                                rotator.setRepeatCount(ObjectAnimator.INFINITE);
                                rotator.setInterpolator(new LinearInterpolator());
                                rotator.start();
                            }
                        });
                    }
                }));
        mAllThreads.get(mAllThreads.size() - 1).start();
    }


    public void RemoveStep(Tile tile) {
        mStepsTile.remove(tile);
        NotifyStep(mStepsTile.peekLast());
    }

    public Tile LastStep() {
        return mStepsTile.peekLast();
    }

    public ArrayList<int[]> SetSteps() {
        ArrayList<int[]> steps = new ArrayList<>();
        int[] firstStep = new int[2];
        mEntranceTile.getLocationOnScreen(firstStep);
        steps.add(firstStep);
        return steps;
    }

    public void SetGameMode(boolean mGameMode) {
        for (int i = 0; i < NUM_OF_ROWS; i++) {
            for (int j = 0; j < NUM_OF_COLS; j++) {
                mTiles[i][j].SetGameMode(mGameMode);
            }
        }
        if (mGameMode)
            NotifyStep(mEntranceTile);
    }

    public void NotifyStep(Tile tile) {
        if (tile != null) {
            int row = tile.GetRow();
            int col = tile.GetCol();
            SetAllBoardNotClickable();

            mTiles[row][col].setClickable(true);
            if (row > 0 && !mTiles[row - 1][col].IsStepped()) {
                mTiles[row - 1][col].setClickable(true);
                Log.i("NotifyStep", "TILE CLICKABLE = " + mTiles[row - 1][col].toString());
            }
            if (col > 0 && !mTiles[row][col - 1].IsStepped()) {
                mTiles[row][col - 1].setClickable(true);
                Log.i("NotifyStep", "TILE CLICKABLE = " + mTiles[row][col - 1].toString());
            }
            if (row < NUM_OF_ROWS - 1 && !mTiles[row + 1][col].IsStepped()) {
                mTiles[row + 1][col].setClickable(true);
                Log.i("NotifyStep", "TILE CLICKABLE = " + mTiles[row + 1][col].toString());
            }
            if (col < NUM_OF_COLS - 1 && !mTiles[row][col + 1].IsStepped()) {
                mTiles[row][col + 1].setClickable(true);
                Log.i("NotifyStep", "TILE CLICKABLE = " + mTiles[row][col + 1].toString());
            }
        }
    }


    public boolean NeighboursClicked(Tile tile) {
        int row = tile.GetRow();
        int col = tile.GetCol();
        if (tile.IsEntrance())
            return true;

        if (row > 0 && mTiles[row - 1][col].IsStepped())
            return true;
        if (col > 0 && mTiles[row][col - 1].IsStepped())
            return true;
        if (row < NUM_OF_ROWS - 1 && mTiles[row + 1][col].IsStepped())
            return true;
        if (col < NUM_OF_COLS - 1 && mTiles[row][col + 1].IsStepped())
            return true;

        return false;
    }

    public void SetAllBoardNotClickable() {
        for (int i = 0; i < NUM_OF_ROWS; i++) {
            for (int j = 0; j < NUM_OF_COLS; j++) {
                mTiles[i][j].setClickable(false);
            }
        }
    }

    public void SetTrapIcon(final int iconId) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTrapIcon.setBackgroundResource(iconId);
            }
        });
    }

    public void InsertIntoStack(Tile tile) {
        mStack.push(tile);
    }

    public void PopFromStack() {
        if (!mStack.isEmpty()) {
            Tile temp = mStack.pop();
            if (temp != null) {
                temp.SetFromStack(true);
                temp.callOnClick();
                temp.SetFromStack(false);
            }
        }
    }

    public boolean DIdPlayerWin() {
        return mDIdPlayerWin;
    }

    public void DIdPlayerWin(boolean dIdPlayerWin) {
        this.mDIdPlayerWin = dIdPlayerWin;
    }

    public LinearLayout GetBoard() {
        return mBoardLayout;
    }

    public boolean HasEntrance() {
        return mHasEntrance;
    }

    public void SetHasEntrance(boolean mHasEntrance) {
        this.mHasEntrance = mHasEntrance;
    }

    public boolean CheckPath() {
        if (mHasEntrance && mHasExit) {
            BFS bfs = new BFS();
            clearVisited();
            ArrayList<Tile> path = bfs.Bfs(mTiles, mEntranceTile, mGameMode);
            return ContainsPath(path);
        }
        return false;
    }

    public boolean ContainsPath(ArrayList<Tile> set) {
        int counter = 0;
        for (Tile tile : set)
            if (tile.IsEntrance() || tile.IsExit())
                counter++;
        return counter == 2;
    }

    public LinearLayout SetMaze() {
        Log.i("SetMaze", "start");
        LinearLayout layout = (LinearLayout) mActivity.findViewById(mBoardId);
        ArrayList<LinearLayout> rows = new ArrayList<>();
        mTiles = new Tile[NUM_OF_ROWS][NUM_OF_COLS];
        if (mDataForRow.size() == 0) {//no board has been created
            for (int i = 0; i < NUM_OF_ROWS; i++) {
                rows.add(new LinearLayout(mActivity));
                for (int j = 0; j < NUM_OF_COLS; j++) {
                    mTiles[i][j] = new Tile(mActivity, i, j, this);
                    GridLayout.LayoutParams param = new GridLayout.LayoutParams(GridLayout.spec(i), GridLayout.spec(j));
                    Display display = mActivity.getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    param.width = size.x / NUM_OF_COLS;
                    param.height = (size.y - (TOP_BAR_HEIGHT + BOTTOM_BAR_HEIGHT)) / NUM_OF_ROWS;
                    param.setMargins(0, 0, 0, 0);
                    param.setGravity(0);
                    rows.get(i).addView(mTiles[i][j], param);
                }
            }
        } else {

            for (int i = 0; i < NUM_OF_ROWS; i++) {
                rows.add(new LinearLayout(mActivity));
                for (int j = 0; j < NUM_OF_COLS; j++) {
                    DataRowBoard data = getDataForCoord(i, j);
                    mTiles[data.GetRow()][data.GetCol()] = new Tile(mActivity, data.GetRow(), data.GetCol(), this);
                    DataRowTile dataRowTile = MainActivity.mDataBase.GetTileById(data.GetTileId());
                    if (dataRowTile.GetIsEntrance() == 1) {
                        mTiles[data.GetRow()][data.GetCol()].SetIsEntrance(true);
                        this.mEntranceTile = mTiles[data.GetRow()][data.GetCol()];
                        this.mHasEntrance = true;
                    }
                    if (dataRowTile.GetIsExit() == 1) {
                        mTiles[data.GetRow()][data.GetCol()].SetIsExit(true);
                        this.mExitTile = mTiles[data.GetRow()][data.GetCol()];
                        this.mHasExit = true;
                    }
                    if (dataRowTile.GetIsWall() == 0)
                        mTiles[data.GetRow()][data.GetCol()].SetIsWall(false);

                    DataRowTileTrap dataRowTileTrap = MainActivity.mDataBase.GetTrapByTileId(data.GetTileId());
                    if (dataRowTileTrap != null) {
                        Trap trap = new Trap(dataRowTileTrap.GetTrapId());
                        mTiles[data.GetRow()][data.GetCol()].SetTrap(trap);
                        trap.SetTile(mTiles[data.GetRow()][data.GetCol()]);
                    }
                    GridLayout.LayoutParams param = new GridLayout.LayoutParams(GridLayout.spec(data.GetRow()), GridLayout.spec(data.GetCol()));
                    Display display = mActivity.getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    param.width = size.x / NUM_OF_COLS;
                    param.height = (size.y - (TOP_BAR_HEIGHT + BOTTOM_BAR_HEIGHT)) / NUM_OF_ROWS;
                    param.setMargins(0, 0, 0, 0);
                    param.setGravity(0);
                    rows.get(data.GetRow()).addView(mTiles[data.GetRow()][data.GetCol()], param);
                }
            }
        }
        for (LinearLayout temp : rows)
            layout.addView(temp);
        return layout;
    }

    public DataRowBoard getDataForCoord(int row, int col) {
        for (DataRowBoard data : mDataForRow) {
            if (data.GetRow() == row && data.GetCol() == col)
                return data;
        }
        return null;
    }

    public void clearVisited() {
        for (int i = 0; i < NUM_OF_ROWS - 1; i++)
            for (int j = 0; j < NUM_OF_COLS - 1; j++)
                mTiles[i][j].SetVisited(false);

        if (mEntranceTile != null)
            mEntranceTile.SetVisited(false);
        if (mExitTile != null)
            mExitTile.SetVisited(false);
    }

    public Tile getmEntranceTile() {
        return mEntranceTile;
    }

    public void setmEntranceTile(Tile mEntranceTile) {
        this.mEntranceTile = mEntranceTile;
    }

    public Tile getmExitTile() {
        return mExitTile;
    }

    public void setmExitTile(Tile mExitTile) {
        this.mExitTile = mExitTile;
    }

    public LinearLayout getLayout() {
        return mBoardLayout;
    }

    public boolean ismHasExit() {
        return mHasExit;
    }

    public void setmHasExit(boolean mHasExit) {
        this.mHasExit = mHasExit;
    }

    public boolean ismExitActivated() {
        return mExitActivated;
    }

    public void setmExitActivated(boolean mExitActivated) {
        this.mExitActivated = mExitActivated;
    }

    public boolean ismEntranceActivated() {
        return mEntranceActivated;
    }

    public void setmEntranceActivated(boolean mEntranceActivated) {
        this.mEntranceActivated = mEntranceActivated;
    }

    public void ApprovePath() {
        clearVisited();
        if (CheckPath()) {
//            SetLocationOnScreen();
            if (MainActivity.mUser.GetTraps().size() > 0) {
                TrapDialog();
            } else {
                AnimatePath();
            }
        } else {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), NO_PATH_TAG, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void SaveCreatedMaze() {
        clearVisited();
        if (CheckPath()) {
            MainActivity.mDataBaseManager.addThread(
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SaveBoard();
                            //TODO: FIREBASE
                        }
                    }));
        } else
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), NO_PATH_TAG, Toast.LENGTH_LONG).show();
                }
            });
    }

    public void SaveBoard(){

       MainActivity.mDataBaseManager.addThread(
               new Thread(new Runnable() {
            @Override
            public void run() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, "Saving....", Toast.LENGTH_LONG).show();
                        mActivity.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                    }
                });
                ArrayList<DataRowBoard> rowsForBoard = new ArrayList<>();
                MainActivity.mDataBase.DeleteUserBoard(MainActivity.mUser.GetId());
                for (int i = 0; i < NUM_OF_ROWS; i++) {
                    for (int j = 0; j < NUM_OF_COLS; j++) {
                        Tile tile = mTiles[i][j];
                        int isWall = 0;
                        int isEntrance = 0;
                        int isExit = 0;
                        if (tile.IsWall())
                            isWall = 1;
                        if (tile.IsEntrance())
                            isEntrance = 1;
                        if (tile.IsExit())
                            isExit = 1;
                        MainActivity.mDataBase.AddRow_Tiles(new DataRowTile(i, j, isWall, isEntrance, isExit));
                        int lastTileId = MainActivity.mDataBase.GetLastTileIdFromDB();
                        if (tile.GetTrap() != null) {
                            MainActivity.mDataBase.AddRow_TileTrap(new DataRowTileTrap(tile.GetTrap().GetTrapIndex(), lastTileId));
                        }
                        rowsForBoard.add(new DataRowBoard(MainActivity.mUser.GetId(), i, j, lastTileId));
                    }
                }
                MainActivity.mDataBase.AddRows_Board(rowsForBoard);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mActivity.findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
                        Toast.makeText(mActivity,MAZE_SAVED_TAG , Toast.LENGTH_LONG).show();
                    }
                });
                mActivity.finish();
            }

        }));

    }

    public void SetButtons() {
        if (mGameMode) {
            mActivity.findViewById(R.id.loadingPanel_game).setVisibility(View.INVISIBLE);
            mTrapIcon = (ImageView) mActivity.findViewById(R.id.trap_game);
            mUndo = (Button) mActivity.findViewById(R.id.undo_create_game);
            mApprove = (Button) mActivity.findViewById(R.id.ok_create_game);
            mChest = (Button) mActivity.findViewById(R.id.place_trap_game);
            mRemoveTrap = (Button) mActivity.findViewById(R.id.remove_trap_game);
        } else {
            mActivity.findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
            mTrapIcon = (ImageView) mActivity.findViewById(R.id.trap_create);
            mUndo = (Button) mActivity.findViewById(R.id.undo_create);
            mApprove = (Button) mActivity.findViewById(R.id.ok_create);
            mChest = (Button) mActivity.findViewById(R.id.place_trap);
            mRemoveTrap = (Button) mActivity.findViewById(R.id.remove_trap);
        }
    }
}
