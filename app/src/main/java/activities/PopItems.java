package activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.cdv.amazingmaze.R;

import java.util.ArrayList;

import DB.DataRowTrapUser;
import DB.DataRowUser;
import UI.Board;
import Logic.Tile;
import Logic.Trap;
import UI.FontCreator_Logo;
import UI.PopItemView;

/**
 * Created by אילון on 30/01/2017.
 */

public class PopItems extends Activity {
    public static final String SELECT_TILE_TAG = "Select a tile first!";
    public static final String CHOOSE_TILE_TAG = "Choose the right kind of trap!";
    public static final String NOT_ENOUGH_COINS_TAG = "Not enough coins!";
    public static boolean mFromApprove = false;
    private ListView mListView;
    private boolean mFromCreate = false;
    private boolean mUpgrade = false;
    private PopItems mSelf;
    private PopItemView mPopItemView;
    private FontCreator_Logo mNumOfCoinsView;
    private boolean mFromGameMode;
    private boolean mIsFinished = false;
    private Board mBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_list);
        mSelf = this;
        mFromCreate = getIntent().getBooleanExtra("from_create", false);
        mUpgrade = getIntent().getBooleanExtra("upgrade", false);
        mFromGameMode = getIntent().getBooleanExtra("from_game", false);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        getWindow().setLayout((int) (dm.widthPixels * .8), (int) (dm.heightPixels * .7));
        mListView = (ListView) findViewById(R.id.list_items_id);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                if (mFromCreate || mFromGameMode) {
                    Object[] tempObj = (Object[]) mListView.getAdapter().getItem(index);
                    boolean isAttack = (boolean) tempObj[4];
                    if (mFromGameMode && !isAttack || mFromCreate && isAttack) {
                        int tempIndex = Trap.MatchIconToIndex((int) tempObj[0]);
                        Trap tempTrap = new Trap(tempIndex);
                        if (!Board.mStack.isEmpty()) {
                            Tile tempTile = Board.mStack.peek();
                            if (tempTile.GetTrap() != null && !mFromGameMode) {
                                Trap trapToReplace = tempTile.GetTrap();
                                MainActivity.mUser.GetTraps().add(0, trapToReplace);
                                tempTile.SetTrap(tempTrap);
                            }
                            if (!tempTile.IsWall()) {
                                if (mFromGameMode)
                                    tempTile.setCounterTrap(tempTrap);
                                else if (mFromCreate) {
                                    tempTile.SetTrap(tempTrap);
                                    tempTile.SetIcon(tempTrap.GetIconId());
                                }
                                Log.i("Removing in POP",tempTrap.GetDescription());
                                MainActivity.removeTraps(tempTrap);
                                //TODO: update data base for amount of mUser traps
                                mPopItemView.RemoveView(index);
                                if (mFromGameMode ) {
                                    Board.PROTECTION = Trap.GetCosts()[tempTrap.GetTrapIndex()];
                                    Board.CompleteShieldLayout(Board.mStack.peek(), tempTrap);
                                }
                                mIsFinished = true;
                                mSelf.finish();
                            }
                        } else {
                            Toast.makeText(mSelf, SELECT_TILE_TAG, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(mSelf,CHOOSE_TILE_TAG , Toast.LENGTH_LONG).show();
                    }
                } else if (mUpgrade) {
                    Object[] tempObj = (Object[]) mListView.getAdapter().getItem(index);
                    int tempIndex = Trap.MatchIconToIndex((int) tempObj[0]);
                    Trap tempTrap = new Trap(tempIndex);
                    if (MainActivity.mUser.GetCoins() >= tempTrap.GetPrice()) {
                        MainActivity.mUser.GetTraps().add(0, tempTrap);
                        MainActivity.mUser.AddToCoins(-1 * tempTrap.GetPrice());
                    } else {
                        Toast.makeText(mSelf,NOT_ENOUGH_COINS_TAG , Toast.LENGTH_LONG).show();
                    }
                    //MainActivity.mDataBase.UpdateCoins(MainActivity.mUser.GetCoins(),MainActivity.mUser.GetId());
                    //final DataRowUser dr = new DataRowUser(MainActivity.mUser.GetId(), MainActivity.mUser.GetUserName(),MainActivity.mUser.GetPassword(), MainActivity.mUser.GetCoins(), null);
                    //MainActivity.mDataBase.AddRow_User(dr);
                    MainActivity.mDataBase.SaveCurrentState(MainActivity.mUser);
                    mSelf.finish();
                }
            }

        });
        SetListOfItems();
    }

    public void SetListOfItems() {
        ArrayList<Object[]> data = new ArrayList<>();
        if (mFromCreate || mFromGameMode) {
            for (Trap trap : MainActivity.mUser.GetTraps()) {
                if (trap != null) {
                    Object[] tempObjArr = CheckViewExists(data, trap);
                    if (tempObjArr == null) {
                        Object[] temp = {trap.GetIconId(), trap.GetName(), trap.GetDescription(), 1, trap.IsAttack()};
                        data.add(temp);
                    } else {
                        int tempCount = Integer.parseInt(tempObjArr[3].toString());
                        tempCount++;
                        data.remove(tempObjArr);
                        tempObjArr[3] = tempCount;
                        data.add(tempObjArr);
                    }
                }
            }
        }
        if (mUpgrade) {
            mNumOfCoinsView = (FontCreator_Logo) findViewById(R.id.x_for_coins);
            mNumOfCoinsView.setText(MainActivity.mUser.GetCoins() + "X");
            for (Trap trap : MainActivity.mAvailbleTraps) {
                Object[] tempObjArr = CheckViewExists(data, trap);
                if (tempObjArr == null) {
                    Object[] temp = {trap.GetIconId(), trap.GetName(), trap.GetDescription(), 1, trap.IsAttack()};
                    data.add(temp);
                } else {
                    int tempCount = Integer.parseInt(tempObjArr[3].toString());
                    tempCount++;
                    data.remove(tempObjArr);
                    tempObjArr[3] = tempCount;
                    data.add(tempObjArr);
                }
            }

        }
        mPopItemView = new PopItemView(this, R.layout.items_list, data);
        mListView.setAdapter(mPopItemView);
    }

    public Object[] CheckViewExists(ArrayList<Object[]> viewList, Trap trap) {
//       try {
           if (trap != null) {
               for (Object[] obj : viewList) {
                   if (obj != null && obj.length > 0 && obj[1] != null && obj[1].toString().compareToIgnoreCase(trap.GetName()) == 0) {
                       return obj;
                   }
               }
           }
           return null;
//       }catch (Exception e){
//           return null;
//       }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mIsFinished && mFromGameMode){
            Tile tempTile = Board.mStack.peek();
            if (tempTile.IsProtected())
                tempTile.SetIsProtected(false);
            if (tempTile.IsStepped())
                tempTile.SetIcon(R.mipmap.yellow_squares);

        }
    }
    @Override
    public void onBackPressed() {
        if (mFromGameMode) {
            Board.SetTrapFlag(false);
        } else
            super.onBackPressed();
    }
}
