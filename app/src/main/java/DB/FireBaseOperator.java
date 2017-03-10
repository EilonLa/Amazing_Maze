package DB;

import android.support.annotation.NonNull;
import android.util.Log;

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
import UI.SignInFragment;
import activities.MainActivity;

/**
 * Created by אילון on 02/03/2017.
 */

public class FireBaseOperator  {
    public static final String USER_TAG = "user";
    public static final String USER_ID_TAG = "user_id";
    public static final String USER_NAME_TAG = "user_name";
    public static final String USER_PASSWORD_TAG = "user_password";
    public static final String USER_COINS_TAG = "user_coins";

    public static final String BOARD_TAG = "board";
    public static final String BOARD_USER_ID_TAG = "user_id";
    public static final String BOARD_ROW_TAG = "row";
    public static final String BOARD_COL_TAG = "col";
    public static final String BOARD_TILE_ID_TAG = "tile_id";

    public static final String TILE_TAG = "Tile";
    public static final String TILE_ID_TAG = "tile_id";
    public static final String TILE_ROW_TAG = "row";
    public static final String TILE_COL_TAG = "col";
    public static final String TILE_IS_ENTRANCE_TAG = "is_entrance";
    public static final String TILE_IS_EXIT_TAG = "is_exit";
    public static final String TILE_IS_WALL_TAG = "is_wall";
    public static final String TILE_TRAP_ID_TAG = "trap_id";

    public static final String TILE_TRAP_TRAP_ID_TAG = "trap_id";
    public static final String TILE_TRAP_TILE_ID_TAG = "tile_id";

    public static final String USER_TRAP_TAG = "user_trap";
    public static final String USER_TRAP_TRAP_ID_TAG = "trap_id";
    public static final String USER_TRAP_USER_ID_TAG = "user_id";

    private String mReferenceToken;
    private static User mUserFromFireBase;
    private static ArrayList<Trap> mListDataFromFireBase;
    private ArrayList<DataRowBoard> mListDataBoardFromFireBase;
    private static Tile mTile;
    private static int mBoardId;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRootReference;
    private DatabaseReference mUserReference;
    private FirebaseAuth mAuth;

    public static AtomicBoolean mIsRunning = new AtomicBoolean(false);


    public FireBaseOperator() {
        mRootReference = FirebaseDatabase.getInstance().getReference();
        mUserReference = mRootReference.child(USER_TAG);
        mRootReference.getDatabase().goOnline();


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("FireBaseOperator", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("FireBaseOperator", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

    }

    public String SaveUserToFireBaseFirstTime(final String userName, final String password, final int coins) {
        HashMap<String, String> map = new HashMap<>();
        map.put(USER_NAME_TAG, userName);
        map.put(USER_PASSWORD_TAG, password);
        map.put(USER_COINS_TAG, String.valueOf(User.DEFAULT_NUM_OF_COINS));
        DatabaseReference ref = mRootReference.child(USER_TAG).push();
        ref.setValue(map);
        return ref.getKey();
    }


    public void SaveUserToFireBase(User user){
        HashMap<String, String> map = new HashMap<>();
        map.put(USER_NAME_TAG, user.GetUserName());
        map.put(USER_PASSWORD_TAG, user.GetPassword());
        map.put(USER_COINS_TAG, String.valueOf(User.DEFAULT_NUM_OF_COINS));
        mRootReference.child(USER_TRAP_TAG).child(user.GetId()).setValue(map);
        SaveUserTraps(user);
    }

    public void SaveUserTraps(final User user) {
        mRootReference.child(USER_TRAP_TAG).child(user.GetId()).setValue(user.GenerateListOfTrapIndex());
//        for (Trap trap : user.GetTraps()) {
//            SaveUserTrapToFireBase(user, trap);
//        }
    }

    public void GetDataForBoard(final User user, final SignInFragment signInFragment) {
        mListDataBoardFromFireBase = new ArrayList<>();
        mRootReference.child(BOARD_TAG).child(user.GetId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<DataRowBoard> dataRowBoard = new ArrayList<>();
                ArrayList<ArrayList<Object>> data = ( ArrayList<ArrayList<Object>>)dataSnapshot.getValue();
                if (data != null) {
                    for (ArrayList<Object> tempList : data) {
                        int row = (int) tempList.get(0);
                        int col = (int) tempList.get(1);
                        String tileId = tempList.get(2).toString();
                        dataRowBoard.add(new DataRowBoard(user.GetId(), row, col, tileId));
                    }
                    user.SetListDataBoardFromFireBase(dataRowBoard);
                }
                signInFragment.ContinueFireBaseActions();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public String SaveBoardToFireBase(Board board, User user) {
        DatabaseReference ref = mRootReference.child(BOARD_TAG).child(user.GetId());
        ArrayList<ArrayList<Object>> data = new ArrayList<>();
        for (int i = 0; i < Board.NUM_OF_ROWS; i++) {
            for (int j = 0; j < Board.NUM_OF_COLS; j++) {
                DatabaseReference tempRef = ref.child(TILE_ID_TAG).push();
                Tile tile = board.GetTiles()[i][j];
                tile.SetTileId(tempRef.getKey());
                ArrayList<Object> tempList = new ArrayList<>();
                tempList.add(i);
                tempList.add(j);
                tempList.add(tile.IsEntrance());
                tempList.add(tile.IsExit());
                tempList.add(tile.IsWall());
                tempList.add(tile.GetTrapIndex());
                data.add(tempList);
//                mRootReference.child(BOARD_TAG).child(BOARD_ROW_TAG).setValue(i);
//                mRootReference.child(BOARD_TAG).child(BOARD_COL_TAG).setValue(j);
//                mRootReference.child(BOARD_TAG).child(BOARD_TILE_ID_TAG).setValue(board.GetTiles()[i][j].GetTileId());
            }
        }
        ref.setValue(data);
        return ref.getKey();
    }

    public String SaveTileToFireBase(Tile tile) {
        HashMap<String, Object> dataMap = new HashMap<>();
        DatabaseReference ref = mRootReference.child(TILE_TAG).child(TILE_ID_TAG).push();
        dataMap.put(TILE_ROW_TAG,tile.GetRow());
        dataMap.put(TILE_COL_TAG,tile.GetCol());
        dataMap.put(TILE_IS_ENTRANCE_TAG,tile.IsEntrance());
        dataMap.put(TILE_IS_EXIT_TAG,tile.IsExit());
        dataMap.put(TILE_IS_WALL_TAG,tile.IsWall());
        dataMap.put(TILE_TRAP_ID_TAG,tile.GetTrapIndex());
        ref.setValue(dataMap);
        return ref.getKey();
    }

    public void GetUserFromFireBase(final String userName, final String password, final SignInFragment signInFragment) {
        mRootReference.child(USER_TAG).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    HashMap<String,String> dataMap = (HashMap<String,String>) data.getValue();
                    String tempUserName = dataMap.get(USER_NAME_TAG);
                    String tempPassword = dataMap.get(USER_PASSWORD_TAG);
                    if (tempUserName.equals(userName) && tempPassword.equals(password)) {
                        String userId = data.getKey().toString();
                        int coins = Integer.parseInt(  dataMap.get(USER_COINS_TAG));
                        MainActivity.mUser = new User(userId,password, userName, coins, null);
                        GetUserTrapsFromFireBase(MainActivity.mUser,signInFragment);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void GetUserTrapsFromFireBase(final User user, final SignInFragment signInFragment) {
        mRootReference.child(USER_TRAP_TAG).child(user.GetId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user.GenerateListOfTraps((ArrayList<Integer>) dataSnapshot.getValue());
                GetDataForBoard(user,signInFragment);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


//    public void AddAuthListener() {
//        mAuth.addAuthStateListener(mAuthListener);
//        mUserReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    public void RemoveAuthListener() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
