package DB;

import android.provider.BaseColumns;

/**
 * Created by eilon & dvir on 29/11/2016.
 */

public final class FeedReaderContract {

    //****************mUser***********************
    public static final String CREATE_TABLE_USER =
            "CREATE TABLE IF NOT EXISTS " + FeedData.TABLE_NAME_USER +
                    " (" + FeedData.COLUMN_NAME_ID + " TEXT PRIMARY KEY, " +
                    FeedData.COLUMN_NAME_USER_NAME + " TEXT, " +
                    FeedData.COLUMN_NAME_PASSWORD + " TEXT, " +
                    FeedData.COLUMN_NAME_COINS + " INTEGER " +
                    ")";
    public static final String DELETE_USER_TABLE = "DROP TABLE IF EXISTS " + FeedData.TABLE_NAME_USER;

    //****************trap***********************
    public static final String CREATE_TABLE_TRAP =
            "CREATE TABLE IF NOT EXISTS " + FeedData.TABLE_NAME_TRAP +
                    " (" + FeedData.COLUMN_NAME_TRAP_ID + " INTEGER PRIMARY KEY, " +
                    FeedData.COLUMN_NAME_TRAP_TYPE + " INTEGER " +
                    ")";
    public static final String DELETE_TRAP_TABLE = "DROP TABLE IF EXISTS " + FeedData.TABLE_NAME_TRAP;

    //****************tile***********************
    public static final String CREATE_TABLE_TILE =
            "CREATE TABLE IF NOT EXISTS " + FeedData.TABLE_NAME_TILE +
                    " (" + FeedData.COLUMN_NAME_TILE_ID + " TEXT PRIMARY KEY, "+
                    FeedData.COLUMN_NAME_ROW + " INTEGER, " +
                    FeedData.COLUMN_NAME_COL + " INTEGER, " +
                    FeedData.COLUMN_NAME_IS_WALL + " INTEGER, " +
                    FeedData.COLUMN_NAME_IS_ENTRANCE + " INTEGER, " +
                    FeedData.COLUMN_NAME_IS_EXIT + " INTEGER" +

                    ")";
    public static final String DELETE_TILE_TABLE = "DROP TABLE IF EXISTS " + FeedData.TABLE_NAME_TILE;

    //****************board***********************
    public static final String CREATE_TABLE_BOARD =
            "CREATE TABLE IF NOT EXISTS " + FeedData.TABLE_NAME_BOARD +
                    " (" + FeedData.COLUMN_NAME_BOARD_USER_ID + " TEXT, " +
                    FeedData.COLUMN_NAME_ROW_VALUE + " INTEGER, " +
                    FeedData.COLUMN_NAME_COL_VALUE + " INTEGER, " +
                    FeedData.COLUMN_NAME_TILE_ID_BOARD + " INTEGER " +

                    ")";
    public static final String DELETE_BOARD_TABLE = "DROP TABLE IF EXISTS " + FeedData.TABLE_NAME_BOARD;

    //****************index1***********************
    public static final String CREATE_TABLE_TRAP_TILE =
            "CREATE TABLE IF NOT EXISTS " + FeedData.TABLE_NAME_TRAP_TILE +
                    " (" + FeedData.COLUMN_NAME_TRAP_ID_TRAP_TILE + " INTEGER, " +
                    FeedData.COLUMN_NAME_TILE_ID_TRAP_TILE + " TEXT " +
                    ")";
    public static final String DELETE_TRAP_TILE_TABLE = "DROP TABLE IF EXISTS " + FeedData.TABLE_NAME_TRAP_TILE;

    //****************index2***********************
    public static final String CREATE_TABLE_BOARD_USER =
            "CREATE TABLE IF NOT EXISTS " + FeedData.TABLE_NAME_BOARD_USER +
                    " (" + FeedData.COLUMN_NAME_USER_ID_BOARD_USER + " TEXT, " +
                    FeedData.COLUMN_NAME_BOARD_ID_BOARD_USER + " TEXT " +
                    ")";
    public static final String DELETE_BOARD_USER_TABLE = "DROP TABLE IF EXISTS " + FeedData.TABLE_NAME_BOARD_USER;

    //****************index3***********************
    public static final String CREATE_TABLE_TRAP_USER =
            "CREATE TABLE IF NOT EXISTS " + FeedData.TABLE_NAME_TRAP_USER +
                    " (" + FeedData.COLUMN_NAME_USER_ID_TRAP_USER + " TEXT, " +
                    FeedData.COLUMN_NAME_TRAP_ID_TRAP_USER + " INTEGER " +
                    ")";
    public static final String DELETE_TRAP_USER_TABLE = "DROP TABLE IF EXISTS " + FeedData.TABLE_NAME_TRAP_USER;

    public DBOperator db;

    private FeedReaderContract() {
    }

    public static class FeedData implements BaseColumns {
        //**********User table***************//
        public static final String TABLE_NAME_USER = "User";
        public static final String COLUMN_NAME_ID = "User_ID";
        public static final String COLUMN_NAME_USER_NAME = "User_name";
        public static final String COLUMN_NAME_PASSWORD = "Password";
        public static final String COLUMN_NAME_COINS = "Coins";

        //**********Traps**********//
        public static final String TABLE_NAME_TRAP = "Trap";
        public static final String COLUMN_NAME_TRAP_ID = "Trap_ID";
        public static final String COLUMN_NAME_TRAP_TYPE = "Trap_Type";
        //**********Tile**********//
        public static final String TABLE_NAME_TILE = "Tile";
        public static final String COLUMN_NAME_TILE_ID = "Tile_ID";
        public static final String COLUMN_NAME_ROW = "Row";
        public static final String COLUMN_NAME_COL = "Col";
        public static final String COLUMN_NAME_IS_WALL = "Is_Wall";//0=FALSE 1= TRUE
        public static final String COLUMN_NAME_IS_ENTRANCE = "Is_Entrance";//0=FALSE 1= TRUE
        public static final String COLUMN_NAME_IS_EXIT = "Is_Exit";//0=FALSE 1= TRUE

        //**********matrices**********//
        public static final String TABLE_NAME_BOARD = "Board";
        public static final String COLUMN_NAME_BOARD_USER_ID = "User_ID";
        public static final String COLUMN_NAME_ROW_VALUE = "Row";
        public static final String COLUMN_NAME_COL_VALUE = "Col";
        public static final String COLUMN_NAME_TILE_ID_BOARD = "Tile_ID_Board";

        //**********index Trap - Tile**********//
        public static final String TABLE_NAME_TRAP_TILE = "Trap_tile";
        public static final String COLUMN_NAME_TRAP_ID_TRAP_TILE = "Trap_ID";
        public static final String COLUMN_NAME_TILE_ID_TRAP_TILE = "Tile_ID";

        //**********index Board - User**********//
        public static final String TABLE_NAME_BOARD_USER = "Board_User";
        public static final String COLUMN_NAME_USER_ID_BOARD_USER = "User_ID";
        public static final String COLUMN_NAME_BOARD_ID_BOARD_USER = "Board_ID";

        //**********index Trap - User**********//
        public static final String TABLE_NAME_TRAP_USER = "Trap_User";
        public static final String COLUMN_NAME_USER_ID_TRAP_USER = "User_ID";
        public static final String COLUMN_NAME_TRAP_ID_TRAP_USER = "Trap_ID";

    }
}
