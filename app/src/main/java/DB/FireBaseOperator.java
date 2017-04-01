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

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import Logic.ExceptionHandler;
import Logic.Tile;
import Logic.Trap;
import Logic.User;
import UI.Board;
import activities.MainActivity;
/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * Class represents the operator of FireBase
 *
 */

public class FireBaseOperator {
    public static final String USER_TAG = "user";
    public static final String USER_NAME_TAG = "user_name";
    public static final String USER_PASSWORD_TAG = "user_password";
    public static final String USER_COINS_TAG = "user_coins";
    public static final String USER_TRAPS_TAG = "Traps";
    public static final String BOARD_TAG = "board";
    public static final String TILE_ID_TAG = "Tile List";
    public static final String LOG_TAG = "Error Log";
    private DatabaseReference mRootReference;
    private MainActivity mActivity;

    public FireBaseOperator(MainActivity mainActivity) {
        mActivity = mainActivity;
        mRootReference = FirebaseDatabase.getInstance().getReference();
        mRootReference.getDatabase().goOnline();
    }

    public MainActivity GetActivity(){
        return mActivity;
    }

    /**
     *
     * inserts the updated traps to the firebase
     * @param user that owns the traps
     */
    public void UpdateTraps(User user) {
        try {
            int counter = 1;
            HashMap<String, Integer> map = new HashMap<>();
            for (int index : user.GenerateListOfTrapIndex()) {
                map.put(USER_TRAPS_TAG + " " + counter, index);
                counter++;
            }
            mRootReference.child(USER_TAG).child(user.GetId()).child(USER_TRAPS_TAG).setValue(map);
        }catch (Exception e){
            new ExceptionHandler( e,this);
        }
    }

    /**
     *
     * inserts the updated coins to the firebase
     * @param user that owns the coins
     */
    public void UpdateCoins(User user) {
        try {
            mRootReference.child(USER_TAG).child(user.GetId()).child(USER_COINS_TAG).setValue(user.GetCoins());
        }catch (Exception e){
            new ExceptionHandler( e,this);
        }
    }

    /**
     * @param userName
     * @param password
     * @param coins
     * @param traps
     * @return the auto generated id value from the fire base
     */
    public String SaveUserToFireBase(final String userName, final String password, final int coins, ArrayList<Trap> traps) {
        try {
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
        }catch (Exception e){
            new ExceptionHandler( e,this);
        }
        return "";
    }


    /**
     * gets the raw data for the board's creation
     * @param user
     */
    public void GetDataForBoard(final User user) {
        try {
            if (user.GetId() != null) {
                if (mRootReference.child(BOARD_TAG).child(user.GetId()).child(TILE_ID_TAG) == null) {
                    mActivity.GetController().IsWaitingForFireBase(false);
                }
                mRootReference.child(BOARD_TAG).child(user.GetId()).child(TILE_ID_TAG).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (user.IsRival() && dataSnapshot.getValue() != null) {
                            /*we need this because if rival do not have a board we dont want
                             the user to play an empty game*/
                            mActivity.GetController().SetDoesRivalHasBoard(true);
                        }
                        else if (user.IsRival() && dataSnapshot.getValue() == null){
                            mActivity.GetController().SetDoesRivalHasBoard(false);
                        }
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
                        //if we are on create mode
                        if (user.GetPassword() != null) {
                            mActivity.GetController().GetUser().SetListDataBoardFromFireBase(dataRowBoard);
                        }
                        //if we are not on create mode and getting rival data
                        else {
                            mActivity.GetController().SetRivalData(dataRowBoard);
                        }
                        mActivity.GetController().IsWaitingForFireBase(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mActivity.GetController().IsWaitingForFireBase(false);
                    }
                });
            } else {

            }
        }catch (Exception e){
            new ExceptionHandler( e,this);
        }
    }

    /**
     * saves the board data to the firebase
     * @param board
     * @param user
     * @return The auto generated board id from the firebase
     */
    public String SaveBoardToFireBase(Board board, User user) {
        try {
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
        }catch (Exception e){
            new ExceptionHandler( e,this);
        }
        return "";
    }


    /**
     * @param userId - if null, it means we creating by user name
     * @param userName will always be available
     * @param password - only after login in the device. Rival's password is not visible
     */
    public void GetUserFromFireBase(final String userId, final String userName, final String password) {
        try {
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
        }catch (Exception e){
            new ExceptionHandler( e,this);
        }
    }

    /**
     * gets user by user name
     * @param userName
     */
    public void GetRivalUser(final String userName) {
        if (userName.equals(mActivity.GetController().GetUser().GetUserName())) {
            return;
        }
        mRootReference.child(USER_TAG).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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

    /**
     * finds matching user names containing string
     * @param query
     */
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

    /**
     * get list of user traps from firebase
     * @param user
     */
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

    /**
     * Records All Exceptions to the fireBase
     * @param errorText
     */
    public void InsertErrorLog(String errorText){
        DatabaseReference ref = mRootReference.child(LOG_TAG).push();
        ref.setValue(errorText);

    }
}
