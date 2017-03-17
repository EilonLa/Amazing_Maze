package DB;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import Logic.Tile;
import Logic.Trap;
import Logic.User;
import UI.Board;
import activities.MainActivity;

/**
 * Created by אילון on 02/03/2017.
 */

public class FireBaseOperator {
    public static final String USER_TAG = "user";
    public static final String USER_NAME_TAG = "user_name";
    public static final String USER_PASSWORD_TAG = "user_password";
    public static final String USER_COINS_TAG = "user_coins";
    public static final String USER_TRAPS_TAG = "Traps";

    public static final String BOARD_TAG = "board";

    public static final String TILE_TAG = "Tile";
    public static final String TILE_ID_TAG = "Tile List";
    public static final String TILE_ROW_TAG = "row";
    public static final String TILE_COL_TAG = "col";
    public static final String TILE_IS_ENTRANCE_TAG = "is_entrance";
    public static final String TILE_IS_EXIT_TAG = "is_exit";
    public static final String TILE_IS_WALL_TAG = "is_wall";
    public static final String TILE_TRAP_ID_TAG = "trap_id";
    public static final String USER_TRAP_TAG = "user_trap";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRootReference;
    private FirebaseAuth mAuth;
    private MainActivity mActivity;

    public FireBaseOperator(MainActivity mainActivity) {
        mActivity = mainActivity;
        mRootReference = FirebaseDatabase.getInstance().getReference();
        mRootReference.getDatabase().goOnline();
        mAuth = FirebaseAuth.getInstance();
    }

    public void UpdateTraps(User user) {
        int counter = 1;
        HashMap<String, Integer> map = new HashMap<>();
        for (int index : user.GenerateListOfTrapIndex()) {
            map.put(USER_TRAPS_TAG + " " + counter, index);
            counter++;
        }
        mRootReference.child(USER_TAG).child(user.GetId()).child(USER_TRAPS_TAG).setValue(map);
    }

    public void UpdateCoins(User user) {
        mRootReference.child(USER_TAG).child(user.GetId()).child(USER_COINS_TAG).setValue(user.GetCoins());
    }

    public String SaveUserToFireBaseFirstTime(final String userName, final String password, final int coins, ArrayList<Trap> traps) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(USER_NAME_TAG, userName);
        map.put(USER_PASSWORD_TAG, password);
        map.put(USER_COINS_TAG, User.DEFAULT_NUM_OF_COINS);
        int counter = 1;
        if (traps != null) {
            for (Trap trap : traps) {
                map.put(USER_TRAPS_TAG + " " + counter, trap);
                counter++;
            }
        }
        DatabaseReference ref = mRootReference.child(USER_TAG).push();
        ref.setValue(map);
        return ref.getKey();
    }

    public void GetDataForBoard(final User user) {
        if (user.GetId() != null) {
            if (mRootReference.child(BOARD_TAG).child(user.GetId()).child(TILE_ID_TAG) == null) {
                mActivity.GetController().IsWaitingForFireBase(false);
            }
            mRootReference.child(BOARD_TAG).child(user.GetId()).child(TILE_ID_TAG).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<DataRowBoard> dataRowBoard = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        ArrayList<Object> tempList = (ArrayList<Object>) data.getValue();
                        if (tempList != null) {
                            long tempRow = (long) tempList.get(0);
                            int row = (int) tempRow;
                            long tempCol = (long) tempList.get(1);
                            int col = (int) tempCol;
                            String tileId = dataSnapshot.getKey();
                            boolean isEntrance = (boolean) tempList.get(2);
                            boolean isExit = (boolean) tempList.get(3);
                            boolean isWall = (boolean) tempList.get(4);
                            long tempIndex = (long) tempList.get(5);
                            int trapIndex = (int) tempIndex;
                            dataRowBoard.add(new DataRowBoard(user.GetId(), row, col, tileId, isEntrance, isExit, isWall, trapIndex));
                        }
                    }
                    if (user.GetPassword() != null) {
                        mActivity.GetController().GetUser().SetListDataBoardFromFireBase(dataRowBoard);
                    } else {
                        mActivity.GetController().SetRivalData(dataRowBoard);
                    }
                    mActivity.GetController().IsWaitingForFireBase(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {

        }
    }

    public String SaveBoardToFireBase(Board board, User user) {
        synchronized (MainActivity.mLockObject) {
            mRootReference.child(BOARD_TAG).child(user.GetId()).child(TILE_ID_TAG).removeValue();
            DatabaseReference ref = mRootReference.child(BOARD_TAG).child(user.GetId()).child(TILE_ID_TAG);
            ArrayList<ArrayList<Object>> data = new ArrayList<>();
            for (int i = 0; i < Board.NUM_OF_ROWS; i++) {
                for (int j = 0; j < Board.NUM_OF_COLS; j++) {
                    Tile tile = board.GetTiles()[i][j];
                    DatabaseReference tempRef;
                    tempRef = ref.push();
                    tile.SetTileId(tempRef.getKey());
                    ArrayList<Object> tempList = new ArrayList<>();
                    tempList.add(i);
                    tempList.add(j);
                    tempList.add(tile.IsEntrance());
                    tempList.add(tile.IsExit());
                    tempList.add(tile.IsWall());
                    tempList.add(tile.GetTrapIndex());
                    data.add(tempList);
                    tempRef.setValue(tempList);
                }
            }

            return ref.getKey();
        }
        //ref.setValue(data);
    }

    public void GetUserFromFireBase(final String userId, final String userName, final String password) {
        if (userId != null && mRootReference.child(USER_TAG).child(userId) != null) {
            mRootReference.child(USER_TAG).child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String userName = dataSnapshot.child(USER_NAME_TAG).getValue(String.class);
                        String password = dataSnapshot.child(USER_PASSWORD_TAG).getValue(String.class);
                        long coins = dataSnapshot.child(USER_COINS_TAG).getValue(Long.class);
                        if (userName != null) {
                            mActivity.GetController().SetUser(new User(userId, password, userName, (int) coins, null));
                            GetUserTrapsFromFireBase(mActivity.GetController().GetUser());

                        }
                    } else {
                        mActivity.GetController().IsWaitingForFireBase(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else if (userId == null && userName != null) {
            mRootReference.child(USER_TAG).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean userFound = false;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        HashMap<String, Object> dataMap = (HashMap<String, Object>) data.getValue();
                        String tempUserName = dataMap.get(USER_NAME_TAG).toString();
                        String tempPassword = dataMap.get(USER_PASSWORD_TAG).toString();
                        if (tempUserName.equals(userName) && tempPassword.equals(password)) {
                            String userId = data.getKey().toString();
                            long coins = (long) dataMap.get(USER_COINS_TAG);
                            mActivity.GetController().SetUser(new User(userId, password, userName, (int) coins, null));
                            userFound = true;
                            GetUserTrapsFromFireBase(mActivity.GetController().GetUser());
                        }
                    }
                    if (!userFound) {
                        Toast.makeText(mActivity, "Server error, no such User or Password", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            mActivity.GetController().IsWaitingForFireBase(false);
        }

    }

    public void GetRivalUser(final String userName) {
        if (userName.equals(mActivity.GetController().GetUser().GetUserName())) {
            return;
        }
        mRootReference.child(USER_TAG).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("FireBase", "GetRivalUser");
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.child(USER_NAME_TAG).getValue().toString().equals(userName)) {
                        long coins = data.child(USER_COINS_TAG).getValue(Long.class);
                        mActivity.GetController().SetRival(new User(data.getKey(), null, userName,(int)coins , null));
                        mActivity.GetController().GetRival().SetIsRival(true);
                        GetDataForBoard(mActivity.GetController().GetRival());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void GetOptionalUsersForGame(final String query) {
        mRootReference.child(USER_TAG).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.child(USER_NAME_TAG).getValue().toString().contains(query)) {
                        mActivity.GetController().GetOptionalUsers().add(data.child(USER_NAME_TAG).getValue().toString());
                    }
                }
                mActivity.GetController().IsWaitingForFireBase(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void GetUserTrapsFromFireBase(final User user) {
        if (mRootReference.child(USER_TAG).child(user.GetId()).child(USER_TRAPS_TAG) != null) {
            mRootReference.child(USER_TAG).child(user.GetId()).child(USER_TRAPS_TAG).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mActivity.GetController().GetUser().GenerateListOfTraps((HashMap<String, Long>) dataSnapshot.getValue(), USER_TRAPS_TAG);
                    GetDataForBoard(mActivity.GetController().GetUser());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            GetDataForBoard(mActivity.GetController().GetUser());
        }
    }
}
