package DB;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import Logic.ExceptionHandler;
import Logic.Trap;
import Logic.User;
import activities.MainActivity;


/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * Class represents the operator of DB
 *
 */

public class DBOperator extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "AmazingMaze";
    public static final String CREATE_DB_TAG = "Create data base";

    public DataBaseManager mDBManager;
    public MainActivity mActivity;

    public DBOperator(MainActivity context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mActivity = context;
    }

    public void closeConnection(){
        if (getWritableDatabase().isOpen())
            getWritableDatabase().close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            mDBManager = new DataBaseManager();
            mDBManager.start();
            db.execSQL(FeedReaderContract.CREATE_TABLE_USER);
            db.execSQL(FeedReaderContract.CREATE_TABLE_TRAP);
            db.execSQL(FeedReaderContract.CREATE_TABLE_TILE);
            db.execSQL(FeedReaderContract.CREATE_TABLE_BOARD);
            db.execSQL(FeedReaderContract.CREATE_TABLE_TRAP_TILE);
            db.execSQL(FeedReaderContract.CREATE_TABLE_BOARD_USER);
            db.execSQL(FeedReaderContract.CREATE_TABLE_TRAP_USER);

        } catch (Exception e) {
            onUpgrade(db, DB_VERSION, 0);
            new ExceptionHandler( e,mActivity.GetFireBaseOperator());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        try {
            db.execSQL(FeedReaderContract.DELETE_USER_TABLE);
            db.execSQL(FeedReaderContract.DELETE_TRAP_TABLE);
            db.execSQL(FeedReaderContract.DELETE_TILE_TABLE);
            db.execSQL(FeedReaderContract.DELETE_BOARD_TABLE);
            db.execSQL(FeedReaderContract.DELETE_TRAP_TILE_TABLE);
            db.execSQL(FeedReaderContract.DELETE_BOARD_USER_TABLE);
            db.execSQL(FeedReaderContract.DELETE_TRAP_USER_TABLE);
            onCreate(db);
        }catch(Exception e){
            e.printStackTrace();
            new ExceptionHandler( e,mActivity.GetFireBaseOperator());
        }
    }

    public void AddRow_User(DataRowUser dr) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedData.COLUMN_NAME_ID, dr.GetUserId());
            values.put(FeedReaderContract.FeedData.COLUMN_NAME_USER_NAME, dr.GetUserName());
            values.put(FeedReaderContract.FeedData.COLUMN_NAME_PASSWORD, dr.GetPassword());
            values.put(FeedReaderContract.FeedData.COLUMN_NAME_COINS, dr.GetCoins());
            // Inserting Row
            db.insert(FeedReaderContract.FeedData.TABLE_NAME_USER, null, values);
            db.close(); // Closing database connection
        }catch (Exception e){
            new ExceptionHandler( e,mActivity.GetFireBaseOperator());
        }
    }
    
    public void AddRow_Tiles(DataRowTile dr) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedData.COLUMN_NAME_TILE_ID, dr.GetId());
            values.put(FeedReaderContract.FeedData.COLUMN_NAME_ROW, dr.GetRow());
            values.put(FeedReaderContract.FeedData.COLUMN_NAME_COL, dr.GetCol());
            values.put(FeedReaderContract.FeedData.COLUMN_NAME_IS_WALL, dr.GetIsWall());
            values.put(FeedReaderContract.FeedData.COLUMN_NAME_IS_ENTRANCE, dr.GetIsEntrance());
            values.put(FeedReaderContract.FeedData.COLUMN_NAME_IS_EXIT, dr.GetIsExit());
            // Inserting Row
            db.insert(FeedReaderContract.FeedData.TABLE_NAME_TILE, null, values);
            //db.close(); // Closing database connection
        }catch (Exception e){
            new ExceptionHandler( e,mActivity.GetFireBaseOperator());
        }
    }

    public void AddRows_Board(ArrayList<DataRowBoard> rows) {
        try {
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
            // db.close(); // Closing database connection
        }catch (Exception e){
            new ExceptionHandler( e,mActivity.GetFireBaseOperator());
        }
    }

    public void AddRow_TileTrap(DataRowTileTrap dr) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedData.COLUMN_NAME_TRAP_ID_TRAP_TILE, dr.GetTrapId());
            values.put(FeedReaderContract.FeedData.COLUMN_NAME_TILE_ID_TRAP_TILE, dr.GetTileId());
            // Inserting Row
            db.insert(FeedReaderContract.FeedData.TABLE_NAME_TRAP_TILE, null, values);
            // db.close(); // Closing database connection
        }catch(Exception e){
            new ExceptionHandler( e,mActivity.GetFireBaseOperator());
        }
    }
    
    public DataRowUser GetUserByName(String userName){
        closeConnection();
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            DataRowUser row = null;
            String query =
                    "SELECT * FROM " + FeedReaderContract.FeedData.TABLE_NAME_USER +
                            " WHERE " + FeedReaderContract.FeedData.COLUMN_NAME_USER_NAME + "='"+userName+"'";

            Cursor c = db.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                row = new DataRowUser(c.getString(0), c.getString(1), c.getString(2), c.getInt(3),null);
            }
            c.close();
            if (row != null)
                row.SetTrapIndexes(GetUserTraps(row.GetUserId()));

            return row;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("", CREATE_DB_TAG);
            new ExceptionHandler( e,mActivity.GetFireBaseOperator());
            onCreate(db);
        }
        return null;
    }

    public DataRowUser GetLastUserFromDB(String lastUserId) {
        closeConnection();
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            DataRowUser row = null;
            String query =
                    "SELECT * FROM " + FeedReaderContract.FeedData.TABLE_NAME_USER +
                            " WHERE " + FeedReaderContract.FeedData.COLUMN_NAME_ID + " = '"+lastUserId+"'" ;

            Cursor c = db.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                if (c.getString(1).compareToIgnoreCase("null") == 0) {
                    c.close();
                    return null;
                }
                Log.i("coins", ""+c.getInt(3) );
                row = new DataRowUser(c.getString(0), c.getString(1), c.getString(2), c.getInt(3),null);
            }
            c.close();
            if (row != null) {
                row.SetTrapIndexes(GetUserTraps(row.GetUserId()));
            }
            return row;
        } catch (Exception e) {
            Log.i("", CREATE_DB_TAG);
            new ExceptionHandler( e,mActivity.GetFireBaseOperator());
            onCreate(db);
        }
        return null;
    }


    public ArrayList<Integer> GetUserTraps(String userId) {
        SQLiteDatabase db = null;
        ArrayList<Integer> rows = new ArrayList<>();
        try {
            db = getReadableDatabase();
            String query =
                    "SELECT * FROM " + FeedReaderContract.FeedData.TABLE_NAME_TRAP_USER +
                            " WHERE " + FeedReaderContract.FeedData.COLUMN_NAME_USER_ID_TRAP_USER + " = '" + userId+"'";

            Cursor c = db.rawQuery(query, null);

            if (c != null && c.moveToFirst()) {
                rows.add(c.getInt(1));
                while (c.moveToNext())
                    rows.add(c.getInt(1));
            }
            c.close();

            return rows;
        } catch (Exception e) {
            Log.i("", CREATE_DB_TAG);
            new ExceptionHandler( e,mActivity.GetFireBaseOperator());
            onCreate(db);
        }
        return null;
    }

    public DataRowTile GetTileById(String tileId) {
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            DataRowTile row = null;
            String query =
                    "SELECT * FROM " + FeedReaderContract.FeedData.TABLE_NAME_TILE +
                            " WHERE " + FeedReaderContract.FeedData.COLUMN_NAME_TILE_ID + " = '" + tileId+"'";

            Cursor c = db.rawQuery(query, null);

            if (c != null && c.moveToFirst()) {
                row  = new DataRowTile(c.getString(1),c.getInt(2), c.getInt(3),c.getInt(4),c.getInt(5),c.getInt(6));
            }
            c.close();

            return row;
        } catch (Exception e) {
            Log.i("", CREATE_DB_TAG);
            new ExceptionHandler( e,mActivity.GetFireBaseOperator());
            onCreate(db);
        }
        return null;
    }

    public ArrayList<DataRowBoard> GetBoardByUserId(String userId) {
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            ArrayList<DataRowBoard> rows = new ArrayList<>();
            String query =
                    "SELECT * FROM " + FeedReaderContract.FeedData.TABLE_NAME_BOARD +
                            " WHERE " + FeedReaderContract.FeedData.COLUMN_NAME_BOARD_USER_ID + " = '" + userId+"'";

            Cursor c = db.rawQuery(query, null);

            if (c != null && c.moveToFirst()) {
                rows.add(new DataRowBoard(c.getString(0),c.getInt(1),c.getInt(2),c.getString(3)));
                while (c.moveToNext())
                    rows.add(new DataRowBoard(c.getString(0),c.getInt(1),c.getInt(2),c.getString(3)));
            }
            c.close();

            return rows;
        } catch (Exception e) {
            Log.i("", CREATE_DB_TAG);
            e.printStackTrace();
            new ExceptionHandler( e,mActivity.GetFireBaseOperator());
            onCreate(db);
        }
        return null;
    }

    public DataRowTileTrap GetTrapByTileId(String tileId) {
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            DataRowTileTrap row = null;
            String query =
                    "SELECT * FROM " + FeedReaderContract.FeedData.TABLE_NAME_TRAP_TILE +
                            " WHERE " + FeedReaderContract.FeedData.COLUMN_NAME_TILE_ID_TRAP_TILE + " = '" + tileId+"'";

            Cursor c = db.rawQuery(query, null);

            if (c != null && c.moveToFirst()) {
                row  = new DataRowTileTrap(c.getInt(0), c.getString(1));
            }
            c.close();

            return row;
        } catch (Exception e) {
            Log.i("", CREATE_DB_TAG);
            new ExceptionHandler( e,mActivity.GetFireBaseOperator());
            onCreate(db);
        }
        return null;
    }
    public void DeleteUserBoard(String userId){
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            String query =
                    "DELETE FROM " + FeedReaderContract.FeedData.TABLE_NAME_BOARD +
                            " WHERE " + FeedReaderContract.FeedData.COLUMN_NAME_BOARD_USER_ID + " = '" + userId+"'";
            db.execSQL(query);
        } catch (Exception e) {
            Log.i("", CREATE_DB_TAG);
            new ExceptionHandler( e,mActivity.GetFireBaseOperator());
            onCreate(db);
        }
    }

    public DataBaseManager GetDBManager(){
        return mDBManager;
    }

    public void SetDBManager(DataBaseManager manager){
        this.mDBManager = manager;
    }
}
