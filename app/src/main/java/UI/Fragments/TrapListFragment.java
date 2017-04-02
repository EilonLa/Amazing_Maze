package UI.Fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.cdv.amazingmaze.R;

import java.util.ArrayList;

import Logic.ExceptionHandler;
import Logic.Tile;
import Logic.Trap;
import UI.Board;
import UI.FontCreator_Buttons;
import UI.FontCreator_Logo;
import UI.TrapViewAdapter;
import activities.MainActivity;


/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * The TrapListFragment fragment is a list view that shows all the traps
 *
 */

public class TrapListFragment extends ListFragment implements AdapterView.OnItemClickListener {
    public static final String SELECT_TILE_TAG = "Select a tile first!";
    public static final String CHOOSE_TILE_TAG = "Choose the right kind of trap!";
    public static final String NOT_ENOUGH_COINS_TAG = "Not enough coins!";
    private TrapViewAdapter mAdapter;
    private MainActivity mActivity;
    private boolean mIsFinished = false;
    private FontCreator_Buttons mNumOfCoinsView;
    private boolean mItemClicked = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.items_list, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity)getActivity();
        mActivity.GetController().SetListFlag(true);
        SetListOfItems();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            if (!mItemClicked) {
                mItemClicked = true;
                if (mActivity.GetController().GetIsCreating() || mActivity.GetController().GetIsGameOn()) {
                    Object[] tempObj = (Object[]) getListView().getAdapter().getItem(i);
                    boolean isAttack = (boolean) tempObj[4];
                    if (mActivity.GetController().GetIsGameOn() && !isAttack || mActivity.GetController().GetIsCreating() && isAttack) {
                        int tempIndex = Trap.MatchIconToIndex((int) tempObj[0]);
                        Trap tempTrap = new Trap(tempIndex);
                        if (!Board.mStack.isEmpty()) {
                            Tile tempTile = Board.mStack.peek();
                            if (tempTile.GetTrap() != null && !mActivity.GetController().GetIsGameOn()) {
                                Trap trapToReplace = tempTile.GetTrap();
                                mActivity.GetController().GetUser().GetTraps().add(0, trapToReplace);
                                tempTile.SetTrap(tempTrap);
                            }
                            if (!tempTile.IsWall()) {
                                if (mActivity.GetController().GetIsGameOn())
                                    tempTile.setCounterTrap(tempTrap);
                                else if (mActivity.GetController().GetIsCreating()) {
                                    tempTile.SetTrap(tempTrap);
                                    tempTile.SetIcon(tempTrap.GetIconId());
                                }
                                mActivity.removeTraps(tempTrap);
                                mActivity.GetFireBaseOperator().UpdateTraps(mActivity.GetController().GetUser());
                                mAdapter.RemoveView(i);
                                if (mActivity.GetController().GetIsGameOn()) {
                                    Board.PROTECTION = Trap.GetCosts()[tempTrap.GetTrapIndex()];
                                    Board.CompleteShieldLayout(Board.mStack.peek(), tempTrap);
                                }
                                mIsFinished = true;
                                mActivity.GetController().SetIsUpgrading(false);
                                mActivity.GetController().SetListFlag(false);
                                mActivity.GetController().GetActiveBoard().SetAllBoardClickable(true);
                                getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                            }
                        } else {
                            Toast.makeText(getActivity(), SELECT_TILE_TAG, Toast.LENGTH_LONG).show();
                            mItemClicked = false;
                        }
                    } else {
                        Toast.makeText(getActivity(), CHOOSE_TILE_TAG, Toast.LENGTH_LONG).show();
                        mItemClicked = false;
                    }
                } else if (mActivity.GetController().GetIsUpgrading()) {
                    Object[] tempObj = (Object[]) getListView().getAdapter().getItem(i);
                    int tempIndex = Trap.MatchIconToIndex((int) tempObj[0]);
                    Trap tempTrap = new Trap(tempIndex);
                    if (mActivity.GetController().GetUser().GetCoins() >= tempTrap.GetPrice()) {
                        mActivity.GetController().GetUser().GetTraps().add(0, tempTrap);
                        mActivity.GetController().GetUser().AddToCoins(-1 * tempTrap.GetPrice());
                        mActivity.GetFireBaseOperator().UpdateTraps(mActivity.GetController().GetUser());
                        mActivity.GetFireBaseOperator().UpdateCoins(mActivity.GetController().GetUser());
                    } else {
                        Toast.makeText(getActivity(), NOT_ENOUGH_COINS_TAG, Toast.LENGTH_LONG).show();
                        mItemClicked = false;
                    }
                    mActivity.GetController().SetIsUpgrading(false);
                    mActivity.GetController().SetListFlag(false);
                    getActivity().getFragmentManager().beginTransaction().remove(mActivity.getFragmentManager().findFragmentById(R.id.container_list)).commit();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            new ExceptionHandler( e, mActivity.GetFireBaseOperator());
        }
    }

    public void SetListOfItems() {
        ArrayList<Object[]> data = new ArrayList<>();
        if (mActivity.GetController().GetIsCreating() || mActivity.GetController().GetIsGameOn()) {
            for (Trap trap : mActivity.GetController().GetUser().GetTraps()) {
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
        if (mActivity.GetController().GetIsUpgrading()) {
            mNumOfCoinsView = (FontCreator_Buttons) getActivity().findViewById(R.id.x_for_coins);
            mNumOfCoinsView.setText(mActivity.GetController().GetUser().GetCoins() + "X");
            for (Trap trap : mActivity.GetAvailableTraps()) {
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
        mAdapter = new TrapViewAdapter(getActivity(),R.layout.items_list, data);
        setListAdapter(mAdapter);
        getListView().setOnItemClickListener(this);
    }

    public Object[] CheckViewExists(ArrayList<Object[]> viewList, Trap trap) {
        if (trap != null) {
            for (Object[] obj : viewList) {
                if (obj != null && obj.length > 0 && obj[1] != null && obj[1].toString().compareToIgnoreCase(trap.GetName()) == 0) {
                    return obj;
                }
            }
        }
        return null;
    }
}
