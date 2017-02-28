package DB;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


/**
 * Created by eilon & dvir on 29/11/2016.
 */

public class DBOperator extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "AmazingMaze";

    public Activity mActivity;

    public DBOperator(Activity context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mActivity = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(FeedReaderContract.CREATE_TABLE_USER);
            db.execSQL(FeedReaderContract.CREATE_TABLE_TRAP);
            db.execSQL(FeedReaderContract.CREATE_TABLE_TILE);
            db.execSQL(FeedReaderContract.CREATE_TABLE_BOARD);
            db.execSQL(FeedReaderContract.CREATE_TABLE_TRAP_TILE);
            db.execSQL(FeedReaderContract.CREATE_TABLE_BOARD_USER);
            db.execSQL(FeedReaderContract.CREATE_TABLE_TRAP_USER);

        } catch (Exception e) {
            onUpgrade(db, DB_VERSION, 0);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(FeedReaderContract.DELETE_USER_TABLE);
        db.execSQL(FeedReaderContract.DELETE_TRAP_TABLE);
        db.execSQL(FeedReaderContract.DELETE_TILE_TABLE);
        db.execSQL(FeedReaderContract.DELETE_BOARD_TABLE);
        db.execSQL(FeedReaderContract.DELETE_TRAP_TILE_TABLE);
        db.execSQL(FeedReaderContract.DELETE_BOARD_USER_TABLE);
        db.execSQL(FeedReaderContract.DELETE_TRAP_USER_TABLE);
        onCreate(db);
    }

    public void AddRow_User(DataRowUser dr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_USER_NAME, dr.GetUserName());
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_PASSWORD, dr.GetPassword());
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_COINS, dr.GetCoins());
        // Inserting Row
        db.insert(FeedReaderContract.FeedData.TABLE_NAME_USER, null, values);
        db.close(); // Closing database connection
    }

    public void AddRow_Traps(DataRowTrap dr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_TRAP_ID, dr.GetId());
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_TRAP_TYPE, dr.GetType());
        // Inserting Row
        db.insert(FeedReaderContract.FeedData.TABLE_NAME_TRAP, null, values);
        db.close(); // Closing database connection
    }

    public void AddRow_Tiles(DataRowTile dr) {//2
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_ROW, dr.GetRow());
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_COL, dr.GetCol());
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_IS_WALL, dr.GetIsWall());
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_IS_ENTRANCE, dr.GetIsEntrance());
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_IS_EXIT, dr.GetIsExit());
        // Inserting Row
        db.insert(FeedReaderContract.FeedData.TABLE_NAME_TILE, null, values);
        db.close(); // Closing database connection
    }

    public void AddRow_Board(DataRowBoard dr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_BOARD_USER_ID, dr.GetId());
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_ROW, dr.GetRow());
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_COL, dr.GetCol());
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_TILE_ID_BOARD, dr.GetTileId());
        // Inserting Row
        db.insert(FeedReaderContract.FeedData.TABLE_NAME_BOARD, null, values);
        db.close(); // Closing database connection
    }

    public void AddRows_Board(ArrayList<DataRowBoard> rows) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        for (DataRowBoard row : rows) {
            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedData.COLUMN_NAME_BOARD_USER_ID, row.GetId());
            values.put(FeedReaderContract.FeedData.COLUMN_NAME_ROW, row.GetRow());
            values.put(FeedReaderContract.FeedData.COLUMN_NAME_COL, row.GetCol());
            values.put(FeedReaderContract.FeedData.COLUMN_NAME_TILE_ID_BOARD, row.GetTileId());
            // Inserting Row
            db.insert(FeedReaderContract.FeedData.TABLE_NAME_BOARD, null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        Log.i("ending save", "");
        db.close(); // Closing database connection
    }

    public void AddRow_TileTrap(DataRowTileTrap dr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_TRAP_ID_TRAP_TILE, dr.GetTrapId());
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_TILE_ID_TRAP_TILE, dr.GetTileId());
        // Inserting Row
        db.insert(FeedReaderContract.FeedData.TABLE_NAME_TRAP_TILE, null, values);
        db.close(); // Closing database connection
    }

    public void AddRow_UserBoard(DataRowUserBoard dr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_USER_ID_BOARD_USER, dr.GetUserId());
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_BOARD_ID_BOARD_USER, dr.GetBoardId());
        // Inserting Row
        db.insert(FeedReaderContract.FeedData.TABLE_NAME_TRAP, null, values);
        db.close(); // Closing database connection
    }

    public void AddRow_UserTrap(DataRowTrapUser dr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_USER_ID_TRAP_USER, dr.GetUserId());
        values.put(FeedReaderContract.FeedData.COLUMN_NAME_TRAP_ID_TRAP_USER, dr.GetTrapId());
        // Inserting Row
        db.insert(FeedReaderContract.FeedData.TABLE_NAME_TRAP_USER, null, values);
        db.close(); // Closing database connection
    }

    public void UpdateCoins(int coins, int userId){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " +FeedReaderContract.FeedData.TABLE_NAME_USER+
                        " SET " +FeedReaderContract.FeedData.COLUMN_NAME_COINS+
                        " = "+coins+ " WHERE " +FeedReaderContract.FeedData.COLUMN_NAME_ID +
                        " = "+userId;
        db.rawQuery(query,null);
    }

    public DataRowUser GetUserByName(String userName){
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            DataRowUser row = null;
            String query =
                    "SELECT * FROM " + FeedReaderContract.FeedData.TABLE_NAME_USER +
                            " WHERE " + FeedReaderContract.FeedData.COLUMN_NAME_USER_NAME + "='"+userName+"'";

            Cursor c = db.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                row = new DataRowUser(c.getInt(0), c.getString(1), c.getString(2), c.getInt(3),null);
            }
            c.close();
            if (row != null)
                row.SetTrapIndexes(GetUserTraps(row.GetUserId()));

            return row;
        } catch (Exception E) {
            E.printStackTrace();
            Log.i("", "creating data base");
            onCreate(db);
        }
        return null;
    }

    public int GetLastTileIdFromDB() {
        SQLiteDatabase db = null;
        int id = 0;
        try {
            db = getReadableDatabase();
            String query =
                    "SELECT ("+FeedReaderContract.FeedData.COLUMN_NAME_TILE_ID+") FROM " + FeedReaderContract.FeedData.TABLE_NAME_TILE +
                            " ORDER BY " + FeedReaderContract.FeedData.COLUMN_NAME_TILE_ID + " DESC LIMIT 1";

            Cursor c = db.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                id =  c.getInt(0);
            }
            c.close();

            return id;
        } catch (Exception E) {
            Log.i("", "creating data base");
            onCreate(db);
        }
        return -1;
    }

    public DataRowUser GetLastUserFromDB() {
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            DataRowUser row = null;
            String query =
                    "SELECT * FROM " + FeedReaderContract.FeedData.TABLE_NAME_USER +
                            " ORDER BY " + FeedReaderContract.FeedData.COLUMN_NAME_ID + " DESC LIMIT 1";

            Cursor c = db.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                if (c.getString(1).compareToIgnoreCase("null") == 0) {
                    c.close();
                    return null;
                }
                row = new DataRowUser(c.getInt(0), c.getString(1), c.getString(2), c.getInt(3),null);
            }
            c.close();
            row.SetTrapIndexes(GetUserTraps(row.GetUserId()));
            return row;
        } catch (Exception E) {
            Log.i("", "creating data base");
            onCreate(db);
        }
        return null;
    }

    public DataRowUser GetLastUserFromDB(int lastUserId) {
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            DataRowUser row = null;
            String query =
                    "SELECT * FROM " + FeedReaderContract.FeedData.TABLE_NAME_USER +
                            " WHERE " + FeedReaderContract.FeedData.COLUMN_NAME_ID + " = "+lastUserId + " DESC LIMIT 1";

            Cursor c = db.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                if (c.getString(1).compareToIgnoreCase("null") == 0) {
                    c.close();
                    return null;
                }
                Log.i("coins", ""+c.getInt(3) );
                row = new DataRowUser(c.getInt(0), c.getString(1), c.getString(2), c.getInt(3),null);
            }
            c.close();
            row.SetTrapIndexes(GetUserTraps(row.GetUserId()));
            return row;
        } catch (Exception E) {
            Log.i("", "creating data base");
            onCreate(db);
        }
        return null;
    }


    public ArrayList<Integer> GetUserTraps(int userId) {
        SQLiteDatabase db = null;
        ArrayList<Integer> rows = new ArrayList<>();
        try {
            db = getReadableDatabase();
            String query =
                    "SELECT * FROM " + FeedReaderContract.FeedData.TABLE_NAME_TRAP_USER +
                            " WHERE " + FeedReaderContract.FeedData.COLUMN_NAME_USER_ID_TRAP_USER + " = " + userId;

            Cursor c = db.rawQuery(query, null);

            if (c != null && c.moveToFirst()) {
                rows.add(c.getInt(1));
                while (c.moveToNext())
                    rows.add(c.getInt(1));
            }
            c.close();

            return rows;
        } catch (Exception E) {
            Log.i("", "creating data base");
            onCreate(db);
        }
        return null;
    }

    public DataRowTrap GetTrapById(int trapId) {
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            DataRowTrap row = null;
            String query =
                    "SELECT * FROM " + FeedReaderContract.FeedData.TABLE_NAME_TRAP +
                            " WHERE " + FeedReaderContract.FeedData.COLUMN_NAME_TRAP_ID + " = " + trapId;

            Cursor c = db.rawQuery(query, null);

            if (c != null && c.moveToFirst()) {
                row  = new DataRowTrap(c.getInt(0), c.getInt(1));
            }
            c.close();

            return row;
        } catch (Exception E) {
            Log.i("", "creating data base");
            onCreate(db);
        }
        return null;
    }

    public DataRowTile GetTileById(int tileId) {
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            DataRowTile row = null;
            String query =
                    "SELECT * FROM " + FeedReaderContract.FeedData.TABLE_NAME_TILE +
                            " WHERE " + FeedReaderContract.FeedData.COLUMN_NAME_TILE_ID + " = " + tileId;

            Cursor c = db.rawQuery(query, null);

            if (c != null && c.moveToFirst()) {
                row  = new DataRowTile(c.getInt(1), c.getInt(2),c.getInt(3),c.getInt(4),c.getInt(5));
            }
            c.close();

            return row;
        } catch (Exception E) {
            Log.i("", "creating data base");
            onCreate(db);
        }
        return null;
    }

    public ArrayList<DataRowBoard> GetBoardByUserId(int userId) {
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            ArrayList<DataRowBoard> rows = new ArrayList<>();
            String query =
                    "SELECT * FROM " + FeedReaderContract.FeedData.TABLE_NAME_BOARD +
                            " WHERE " + FeedReaderContract.FeedData.COLUMN_NAME_BOARD_USER_ID + " = " + userId;

            Cursor c = db.rawQuery(query, null);

            if (c != null && c.moveToFirst()) {
                rows.add(new DataRowBoard(c.getInt(0),c.getInt(1),c.getInt(2),c.getInt(3)));
                while (c.moveToNext())
                    rows.add(new DataRowBoard(c.getInt(0),c.getInt(1),c.getInt(2),c.getInt(3)));
            }
            c.close();

            return rows;
        } catch (Exception E) {
            Log.i("", "creating data base");
            onCreate(db);
        }
        return null;
    }

    public DataRowTileTrap GetTrapByTileId(int tileId) {
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            DataRowTileTrap row = null;
            String query =
                    "SELECT * FROM " + FeedReaderContract.FeedData.TABLE_NAME_TRAP_TILE +
                            " WHERE " + FeedReaderContract.FeedData.COLUMN_NAME_TILE_ID_TRAP_TILE + " = " + tileId;

            Cursor c = db.rawQuery(query, null);

            if (c != null && c.moveToFirst()) {
                row  = new DataRowTileTrap(c.getInt(0), c.getInt(1));
            }
            c.close();

            return row;
        } catch (Exception E) {
            Log.i("", "creating data base");
            onCreate(db);
        }
        return null;
    }
    public void DeleteUserBoard(int userId){
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            String query =
                    "DELETE FROM " + FeedReaderContract.FeedData.TABLE_NAME_BOARD +
                            " WHERE " + FeedReaderContract.FeedData.COLUMN_NAME_BOARD_USER_ID + " = " + userId;
            db.execSQL(query);
        } catch (Exception E) {
            Log.i("", "creating data base");
            onCreate(db);
        }
    }
}
