package UI;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
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

import DB.DataBaseManager;
import DB.DataRowBoard;
import DB.DataRowTile;
import DB.DataRowTileTrap;
import Logic.BFS;
import Logic.BoardTileObserver;
import Logic.ExceptionHandler;
import Logic.Tile;
import Logic.Trap;
import Logic.User;
import UI.Fragments.GamePlay;
import UI.Fragments.ResultScreen;
import UI.Fragments.TrapListFragment;
import activities.MainActivity;


/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * The Board is an ImageView that contains all the tiles
 *
 */

public class Board extends ImageView {
    public static final String FINISH_THE_PATH_TAG = "Finish the path to the end before adding shields!";
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
    private boolean mFromCopyConstructor;
    private static Tile[][] mTiles;
    private MainActivity mActivity;
    private LinearLayout mBoardLayout;
    public static Stack<Tile> mStack;
    private Button mUndo;
    public static Button mApprove;
    private Button mChest;
    private ImageView mTrapIcon;
    private Tile mEntranceTile;
    private Tile mExitTile;
    private ArrayList<DataRowBoard> mDataForRow;
    private int mBoardId;
    private static LinkedList<Tile> mStepsTile;
    private ArrayList<Tile> mSwipedTiles;
    private User mUser;

    public Board(final MainActivity activity, User user, ArrayList<DataRowBoard> dataForRow, int boardId, final boolean gameMode) throws Exception {
        super(activity);
        this.mActivity = activity;
        mFromCopyConstructor = false;
        mUser = user;
        if (mUser != null && mUser.IsRival()) {
            mUser.SetBoard(this);
        }
        mActivity.GetController().SetActiveBoard(this);
        this.mDataForRow = dataForRow;
        this.mBoardId = boardId;
        this.mGameMode = gameMode;
        mAllThreads = new ArrayList<>();
        SetButtons();
        mBoardLayout = SetMaze(mDataForRow);
        mActivity.GetController().SetGameMode(mGameMode);
        SetGameMode(mGameMode);
        mStack = new Stack<>();
        mUndo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PopFromStack();
                } catch (Exception e) {
                    e.printStackTrace();
                    new ExceptionHandler( e.getStackTrace()[0].getClassName()+"/"+e.getStackTrace()[0].getMethodName()+" : "+e.getStackTrace()[0].getLineNumber(), mActivity.GetFireBaseOperator());
                }
            }
        });
        mApprove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mGameMode) {
                    try {
                        SaveCreatedMaze();
                    } catch (Exception e) {
                        e.printStackTrace();
                        new ExceptionHandler( e.getStackTrace()[0].getClassName()+"/"+e.getStackTrace()[0].getMethodName()+" : "+e.getStackTrace()[0].getLineNumber(), mActivity.GetFireBaseOperator());
                    }
                } else {//game mode
                    try {
                        ApprovePath();
                    } catch (Exception e) {
                        e.printStackTrace();
                        new ExceptionHandler( e.getStackTrace()[0].getClassName()+"/"+e.getStackTrace()[0].getMethodName()+" : "+e.getStackTrace()[0].getLineNumber(), mActivity.GetFireBaseOperator());

                    }
                }
            }
        });
        mChest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!mActivity.GetController().GetListFlag()) {
                            mActivity.GetController().SetListFlag(true);
                            try {
                                SetAllBoardClickable(false);
                            } catch (Exception e) {
                                e.printStackTrace();
                                new ExceptionHandler( e.getStackTrace()[0].getClassName()+"/"+e.getStackTrace()[0].getMethodName()+" : "+e.getStackTrace()[0].getLineNumber(), mActivity.GetFireBaseOperator());
                            }
                            try {

                                if (mGameMode) {
                                    if (CheckPath()) {
                                        SetLocationOnScreen();
                                        OpenChest();
                                    } else {
                                        Toast.makeText(mActivity, FINISH_THE_PATH_TAG, Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    TrapListFragment listFragment = new TrapListFragment();
                                    mActivity.getFragmentManager().beginTransaction().add(R.id.container_list_board, listFragment).addToBackStack(null).commit();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                new ExceptionHandler( e.getStackTrace()[0].getClassName()+"/"+e.getStackTrace()[0].getMethodName()+" : "+e.getStackTrace()[0].getLineNumber(), mActivity.GetFireBaseOperator());
                            }
                        }
                    }
                });
            }
        });
        mStepsTile = new LinkedList<>();
    }


    public Board(Board other) throws Exception{
        super(other.GetActivity());
        mFromCopyConstructor = true;
        this.mActivity = other.GetActivity();
        mUser = mActivity.GetController().GetUser();
        if (mUser != null && mUser.IsRival()) {
            mUser.SetBoard(this);
        }
        mActivity.GetController().SetActiveBoard(this);
        this.mDataForRow = other.GetDataForRow();
        this.mBoardId = other.GetBoardLayoutId();
        this.mGameMode = other.IsGameMode();
        mAllThreads = new ArrayList<>();
        other.KillAllRunningThreads();
        SetButtons();
        mBoardLayout = SetMaze(mDataForRow);
        mActivity.GetController().SetGameMode(mGameMode);
        SetGameMode(mGameMode);
        mStack = new Stack<>();
        mUndo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PopFromStack();
                } catch (Exception e) {
                    e.printStackTrace();
                    new ExceptionHandler( e.getStackTrace()[0].getClassName()+"/"+e.getStackTrace()[0].getMethodName()+" : "+e.getStackTrace()[0].getLineNumber(), mActivity.GetFireBaseOperator());
                }
            }
        });
        mApprove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!mGameMode) {
                        SaveCreatedMaze();
                    } else {//game mode
                        ApprovePath();
                    }
                }catch (Exception e ){
                    e.printStackTrace();
                    new ExceptionHandler( e.getStackTrace()[0].getClassName()+"/"+e.getStackTrace()[0].getMethodName()+" : "+e.getStackTrace()[0].getLineNumber(), mActivity.GetFireBaseOperator());
                }
            }
        });
        mChest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!mActivity.GetController().GetListFlag()) {
                            mActivity.GetController().SetListFlag(true);
                            try {
                                SetAllBoardClickable(false);
                            } catch (Exception e) {
                                e.printStackTrace();
                                new ExceptionHandler( e.getStackTrace()[0].getClassName()+"/"+e.getStackTrace()[0].getMethodName()+" : "+e.getStackTrace()[0].getLineNumber(), mActivity.GetFireBaseOperator());
                            }
                            try {
                                if (mGameMode) {
                                    if (CheckPath()) {
                                        SetLocationOnScreen();
                                        OpenChest();
                                    } else {
                                        Toast.makeText(mActivity, FINISH_THE_PATH_TAG, Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    TrapListFragment listFragment = new TrapListFragment();
                                    mActivity.getFragmentManager().beginTransaction().add(R.id.container_list_board, listFragment).addToBackStack(null).commit();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                new ExceptionHandler( e.getStackTrace()[0].getClassName()+"/"+e.getStackTrace()[0].getMethodName()+" : "+e.getStackTrace()[0].getLineNumber(), mActivity.GetFireBaseOperator());
                            }
                        }
                    }
                });
            }
        });
        mStepsTile = new LinkedList<>();

    }

    public int GetBoardLayoutId (){
        return mBoardId;
    }

    public ArrayList<DataRowBoard> GetDataForRow() {
        return mDataForRow;
    }


    public MainActivity GetActivity() {
        return mActivity;
    }

    public Tile[][] GetTiles() {
        return mTiles;
    }

    public void KillAllRunningThreads() throws Exception {
        for (Thread thread : mAllThreads) {
            if (thread.isAlive())
                thread.interrupt();
        }
    }

    public void SetSwipedTiles(ArrayList<Tile> list) {
        mSwipedTiles = list;
    }

    public ArrayList<Tile> GetSwipedTiles() {
        return mSwipedTiles;
    }

    public static Stack<Tile> GetStack() {
        return mStack;
    }

    public static LinkedList<Tile> GetStepsTile() {
        return mStepsTile;
    }

    public void AnimatePath() throws Exception {
        mActivity.GetController().SetFinishedGame();
        mActivity.GetController().StopTimer();
        SetLocationOnScreen();
        mDIdPlayerWin = true;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView avatar = (ImageView) mActivity.findViewById(R.id.avatar);
                avatar.setBackgroundResource(R.drawable.running_man);
                AnimationDrawable animator = (AnimationDrawable) avatar.getBackground();
                animator.start();
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
                        try {
                            SetTrapAnimationTile(to, delay + 400, avatar);
                        } catch (Exception e) {
                            e.printStackTrace();
                            new ExceptionHandler( e.getStackTrace()[0].getClassName()+"/"+e.getStackTrace()[0].getMethodName()+" : "+e.getStackTrace()[0].getLineNumber(), mActivity.GetFireBaseOperator());
                        }
                        mDIdPlayerWin = false;
                        break;
                    }
                    if (to.GetTrap() != null)
                        if (to.IsProtected() && to.GetTrap() != null && to.GetTrap().IsAttack()) {
                            try {
                                SetCounterTrapAnimation(to, delay + 400);
                            } catch (Exception e) {
                                e.printStackTrace();
                                new ExceptionHandler( e.getStackTrace()[0].getClassName()+"/"+e.getStackTrace()[0].getMethodName()+" : "+e.getStackTrace()[0].getLineNumber(), mActivity.GetFireBaseOperator());
                            }
                        }
                    from = to;
                    delay += 400;
                }
                if (mDIdPlayerWin) {
                    final int finalDelay = delay;
                    mUser.AddToCoins(GamePlay.VALUE_FOR_WIN);
                    mActivity.GetController().SetWin(true);
                    mAllThreads.add(
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(finalDelay);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        new ExceptionHandler( e.getStackTrace()[0].getClassName()+"/"+e.getStackTrace()[0].getMethodName()+" : "+e.getStackTrace()[0].getLineNumber(), mActivity.GetFireBaseOperator());
                                    }
                                    mActivity.GetController().SetWin(true);
                                    mActivity.getFragmentManager().beginTransaction().remove(mActivity.getFragmentManager().findFragmentById(R.id.container_board)).addToBackStack(null).commit();
                                    mActivity.getFragmentManager().beginTransaction().add(R.id.container_Result_Screen, new ResultScreen()).addToBackStack(null).commit();
                                    try {
                                        KillAllRunningThreads();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        new ExceptionHandler( e.getStackTrace()[0].getClassName()+"/"+e.getStackTrace()[0].getMethodName()+" : "+e.getStackTrace()[0].getLineNumber(), mActivity.GetFireBaseOperator());
                                    }
                                }
                            }));
                    mAllThreads.get(mAllThreads.size() - 1).start();
                }
            }
        });

    }

    public void OpenChest()throws Exception {
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
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TrapListFragment listFragment = new TrapListFragment();
                                if (!mGameMode) {
                                    mActivity.getFragmentManager().beginTransaction().add(R.id.container_list_board, listFragment).addToBackStack(null).commit();
                                } else {
                                    mActivity.getFragmentManager().beginTransaction().add(R.id.container_list_board_gameplay, listFragment).addToBackStack(null).commit();
                                }
                            }
                        });
                    }
                }));
        mAllThreads.get(mAllThreads.size() - 1).start();

    }

    public static void CompleteShieldLayout(Tile tile, Trap trap)throws Exception {
        boolean reachedTile = false;
        ;
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

    public LinearLayout GetBoardLayout() {
        return mBoardLayout;
    }

    public boolean IsGameMode() {
        return mGameMode;
    }

    public void TrapDialog()throws Exception {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                try {
                                    SetTrapFlag(true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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
                                                try {
                                                    CompleteShieldLayout(mStack.peek(), mStack.peek().getCounterTrap());
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                TrapListFragment mListFragment = new TrapListFragment();
                                                mActivity.getFragmentManager().beginTransaction().add(R.id.container_list, mListFragment).commit();
                                            }
                                        }));
                                mAllThreads.get(mAllThreads.size() - 1).start();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                try {
                                    AnimatePath();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    new ExceptionHandler( e.getStackTrace()[0].getClassName()+"/"+e.getStackTrace()[0].getMethodName()+" : "+e.getStackTrace()[0].getLineNumber(), mActivity.GetFireBaseOperator());
                                }
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

    public static void SetTrapFlag(boolean flag)throws Exception {
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

    public void SetLocationOnScreen()throws Exception {
        for (Tile tile : mStepsTile) {
            int[] step = new int[2];
            tile.getLocationOnScreen(step);
            step[1] -= (mApprove.getHeight() + tile.getHeight());
            tile.SetLocationOnScreen(step);
        }
    }

    public void SetTrapAnimationTile(final Tile tile, final int delay, final ImageView avatar)throws Exception {
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
                                try {
                                    tile.SetIcon(R.drawable.blast);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    new ExceptionHandler( e.getStackTrace()[0].getClassName()+"/"+e.getStackTrace()[0].getMethodName()+" : "+e.getStackTrace()[0].getLineNumber(), mActivity.GetFireBaseOperator());
                                }
                                AnimationDrawable blast = (AnimationDrawable) tile.getBackground();
                                blast.start();
                            }
                        });
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mActivity.getFragmentManager().beginTransaction().remove(mActivity.getFragmentManager().findFragmentById(R.id.container_board)).addToBackStack(null).commit();
                        mActivity.getFragmentManager().beginTransaction().add(R.id.container_Result_Screen, new ResultScreen()).addToBackStack(null).commit();

                    }
                }));
        mAllThreads.get(mAllThreads.size() - 1).start();
    }

    public void SetCounterTrapAnimation(final Tile tile, final int delay)throws Exception {
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
                                try {
                                    tile.SetIcon(R.mipmap.star);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    new ExceptionHandler( e.getStackTrace()[0].getClassName()+"/"+e.getStackTrace()[0].getMethodName()+" : "+e.getStackTrace()[0].getLineNumber(), mActivity.GetFireBaseOperator());
                                }
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

    public void SetGameMode(boolean mGameMode)throws Exception {
        for (int i = 0; i < NUM_OF_ROWS; i++) {
            for (int j = 0; j < NUM_OF_COLS; j++) {
                mTiles[i][j].SetGameMode(mGameMode);
            }
        }
        if (mGameMode)
            NotifyStep(mEntranceTile);
    }

    public void NotifyStep(Tile tile)throws Exception {
        if (tile != null) {
            int row = tile.GetRow();
            int col = tile.GetCol();
            SetAllBoardClickable(false);

            mTiles[row][col].setClickable(true);
            if (row > 0 && !mTiles[row - 1][col].IsStepped()) {
                mTiles[row - 1][col].setClickable(true);
            }
            if (col > 0 && !mTiles[row][col - 1].IsStepped()) {
                mTiles[row][col - 1].setClickable(true);
            }
            if (row < NUM_OF_ROWS - 1 && !mTiles[row + 1][col].IsStepped()) {
                mTiles[row + 1][col].setClickable(true);
            }
            if (col < NUM_OF_COLS - 1 && !mTiles[row][col + 1].IsStepped()) {
                mTiles[row][col + 1].setClickable(true);
            }
        }
    }

    public void SetAllBoardClickable(boolean clickable)throws Exception {
        for (int i = 0; i < NUM_OF_ROWS; i++) {
            for (int j = 0; j < NUM_OF_COLS; j++) {
                mTiles[i][j].setClickable(clickable);
            }
        }
        mApprove.setClickable(clickable);
       // mChest.setClickable(clickable);
        mUndo.setClickable(clickable);
    }

    public void SetTrapIcon(final int iconId) throws Exception{
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTrapIcon.setBackgroundResource(iconId);
            }
        });
    }

    public boolean PopFromStack()throws Exception {
        if (!mStack.isEmpty()) {
            Tile temp = mStack.pop();
            if (temp != null) {
                temp.SetFromStack(true);
                temp.callOnClick();
                temp.SetFromStack(false);
            }
            return true;
        }
        return false;
    }

    public void DrainStack() {
        mStack.clear();
    }

    public boolean HasEntrance() {
        return mHasEntrance;
    }

    public void SetHasEntrance(boolean mHasEntrance) {
        this.mHasEntrance = mHasEntrance;
    }

    public boolean CheckPath()throws Exception {
        if (mHasEntrance && mHasExit) {
            BFS bfs = new BFS();
            clearVisited();
            ArrayList<Tile> path = bfs.Bfs(mTiles, mEntranceTile, mGameMode);
            return ContainsPath(path);
        }
        return false;
    }

    public int GetBoardDataSize() {
        return mDataForRow.size();
    }

    public boolean ContainsPath(ArrayList<Tile> set)throws Exception {
        int counter = 0;
        for (Tile tile : set)
            if (tile.IsEntrance() || tile.IsExit())
                counter++;
        return counter == 2;
    }

    public LinearLayout SetMaze(ArrayList<DataRowBoard> dataForRow)throws Exception {
        BoardTileObserver observer = new BoardTileObserver(this);
        LinearLayout layout = (LinearLayout) mActivity.findViewById(mBoardId);
        ArrayList<LinearLayout> rows = new ArrayList<>();
        mTiles = new Tile[NUM_OF_ROWS][NUM_OF_COLS];
        if (dataForRow.size() == 0) {//no board has been created
            for (int i = 0; i < NUM_OF_ROWS; i++) {
                rows.add(new LinearLayout(mActivity));
                for (int j = 0; j < NUM_OF_COLS; j++) {
                    mTiles[i][j] = new Tile(i, j, observer);
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
                    mTiles[data.GetRow()][data.GetCol()] = new Tile(data.GetRow(), data.GetCol(), observer);
                    synchronized (MainActivity.mLockObject) {
                        DataRowTile dataRowTile = null;
                        if (!data.IsFromFireBase()) {
                            dataRowTile = mActivity.GetDBOperator().GetTileById(data.GetTileId());
                        }
                        if ((dataRowTile != null && dataRowTile.GetIsEntrance() == 1) || data.IsEntrance()) {
                            mTiles[data.GetRow()][data.GetCol()].SetIsEntrance(true);
                            this.mEntranceTile = mTiles[data.GetRow()][data.GetCol()];
                            this.mHasEntrance = true;
                        }
                        if ((dataRowTile != null && dataRowTile.GetIsExit() == 1) || data.isExit()) {
                            mTiles[data.GetRow()][data.GetCol()].SetIsExit(true);
                            this.mExitTile = mTiles[data.GetRow()][data.GetCol()];
                            this.mHasExit = true;
                        }
                        if ((dataRowTile != null && dataRowTile.GetIsWall() == 0) || !data.IsWall())
                            mTiles[data.GetRow()][data.GetCol()].SetIsWall(false);

                        if (data.IsFromFireBase() && data.GetTrapIndex() != -1){
                            Trap trap = new Trap(data.GetTrapIndex());
                            mTiles[data.GetRow()][data.GetCol()].SetTrap(trap);
                            trap.SetTile(mTiles[data.GetRow()][data.GetCol()]);
                        } else{
                            DataRowTileTrap dataRowTileTrap = mActivity.GetDBOperator().GetTrapByTileId(data.GetTileId());
                            if (dataRowTileTrap != null ) {
                                Trap trap = new Trap(dataRowTileTrap.GetTrapId());
                                mTiles[data.GetRow()][data.GetCol()].SetTrap(trap);
                                trap.SetTile(mTiles[data.GetRow()][data.GetCol()]);
                            }
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
        }
        for (LinearLayout temp : rows)
            layout.addView(temp);
        return layout;
    }

    public void SetIsWaitingForTrapToPick(boolean isWaitingForTrapToPick) throws Exception{
        mIsWaitingForTrapToPick = isWaitingForTrapToPick;
    }

    public DataRowBoard getDataForCoord(int row, int col)throws Exception {
        for (DataRowBoard data : mDataForRow) {
            if (data.GetRow() == row && data.GetCol() == col)
                return data;
        }
        return null;
    }

    public void clearVisited() throws Exception{
        for (int i = 0; i < NUM_OF_ROWS - 1; i++)
            for (int j = 0; j < NUM_OF_COLS - 1; j++)
                mTiles[i][j].SetVisited(false);

        if (mEntranceTile != null)
            mEntranceTile.SetVisited(false);
        if (mExitTile != null)
            mExitTile.SetVisited(false);
    }

    public Tile GetEntranceTile() {
        return mEntranceTile;
    }

    public void SetEntranceTile(Tile mEntranceTile) {
        this.mEntranceTile = mEntranceTile;
    }

    public Tile GetExitTile() {
        return mExitTile;
    }

    public void SetExitTile(Tile mExitTile) {
        this.mExitTile = mExitTile;
    }

    public LinearLayout getLayout() {
        return mBoardLayout;
    }

    public boolean IsHasExit() {
        return mHasExit;
    }

    public void SetHasExit(boolean mHasExit) {
        this.mHasExit = mHasExit;
    }

    public boolean IsExitActivated() {
        return mExitActivated;
    }

    public void SetExitActivated(boolean mExitActivated) {
        this.mExitActivated = mExitActivated;
    }

    public boolean IsEntranceActivated() {
        return mEntranceActivated;
    }


    public Button GetApproveBtn() {
        return mApprove;
    }

    public void SetEntranceActivated(boolean mEntranceActivated) {
        this.mEntranceActivated = mEntranceActivated;
    }

    public void ApprovePath()throws Exception {
        mActivity.GetController().SetFinishedGame();
        clearVisited();
        if (CheckPath()) {
            if (mUser.GetTraps().size() > 0) {
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

    public void SaveCreatedMaze()throws Exception {
        clearVisited();
        if (CheckPath()) {
            SaveBoard(mUser);
            //DrainStack();
           // mActivity.onBackPressed();
        } else
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), NO_PATH_TAG, Toast.LENGTH_LONG).show();
                }
            });
    }

    public void SaveBoard(final User user)throws Exception {
        if (mActivity.GetDBOperator().GetDBManager() == null) {
            mActivity.GetDBOperator().SetDBManager(new DataBaseManager());
            mActivity.GetDBOperator().GetDBManager().start();
        }
        mActivity.GetDBOperator().GetDBManager().addThread(new Thread(new Runnable() {
            @Override
            public void run() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, "Saving....", Toast.LENGTH_LONG).show();
                        //mActivity.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                    }
                });
                try {
                    if (CheckPath()) {
                        mActivity.GetFireBaseOperator().SaveBoardToFireBase(Board.this, mUser);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mActivity.GetFireBaseOperator().UpdateCoins(mUser);
                mActivity.GetFireBaseOperator().UpdateTraps(mUser);
                ArrayList<DataRowBoard> rowsForBoard = new ArrayList<>();
                mActivity.GetDBOperator().DeleteUserBoard(user.GetId());
                for (int i = 0; i < NUM_OF_ROWS; i++) {
                    for (int j = 0; j < NUM_OF_COLS; j++) {
                        Tile tile = mTiles[i][j];
                        if (tile != null) {
                            int isWall = 0;
                            int isEntrance = 0;
                            int isExit = 0;
                            if (tile.IsWall())
                                isWall = 1;
                            if (tile.IsEntrance())
                                isEntrance = 1;
                            if (tile.IsExit())
                                isExit = 1;
                            String tileId = tile.GetTileId();
                            if (!mFromCopyConstructor) {
                                mActivity.GetDBOperator().AddRow_Tiles(new DataRowTile(tileId, i, j, isWall, isEntrance, isExit));//tile

                                if (tile.GetTrap() != null) {
                                    mActivity.GetDBOperator().AddRow_TileTrap(new DataRowTileTrap(tile.GetTrap().GetTrapIndex(), tileId));//save trap_tile
                                }
                            }
                            rowsForBoard.add(new DataRowBoard(user.GetId(), i, j, tileId));//save board
                        }
                    }
                }
                if (rowsForBoard.size() > 0) {
                    mActivity.GetDBOperator().AddRows_Board(rowsForBoard);
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mActivity.findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
                        Toast.makeText(mActivity, MAZE_SAVED_TAG, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }));

    }

    public void SetButtons() throws Exception{
        if (mGameMode) {
            mActivity.findViewById(R.id.loadingPanel_game).setVisibility(View.INVISIBLE);
            mTrapIcon = (ImageView) mActivity.findViewById(R.id.trap_game);
            mUndo = (Button) mActivity.findViewById(R.id.undo_create_game);
            mApprove = (Button) mActivity.findViewById(R.id.ok_create_game);
            mChest = (Button) mActivity.findViewById(R.id.place_trap_game);
        } else {
            mActivity.findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
            mTrapIcon = (ImageView) mActivity.findViewById(R.id.trap_create);
            mUndo = (Button) mActivity.findViewById(R.id.undo_create);
            mApprove = (Button) mActivity.findViewById(R.id.ok_create);
            mChest = (Button) mActivity.findViewById(R.id.place_trap);
        }
    }
}
