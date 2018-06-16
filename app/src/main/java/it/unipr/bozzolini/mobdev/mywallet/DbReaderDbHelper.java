package it.unipr.bozzolini.mobdev.mywallet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbReaderDbHelper extends SQLiteOpenHelper {
    private static String TAG = "DbReaderHelper";

    ///incrementare la versione quando si fa la onUpgrade
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DbExpenses.db";

    private static final String SQL_CREATE_CATEGORY =
            "CREATE TABLE " + DbConfig.Category.TABLE_NAME + " (" +
                    DbConfig.Category.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DbConfig.Category.COLUMN_NAME_NAME + " TEXT NOT NULL," +
                    "CONSTRAINT unique_category UNIQUE ("+ DbConfig.Category.COLUMN_NAME_NAME +"));";

    private static final String SQL_CREATE_EXPENSES =
            "CREATE TABLE " + DbConfig.Expenses.TABLE_NAME + " (" +
                    DbConfig.Expenses.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DbConfig.Expenses.COLUMN_NAME_DATE + " DATE NOT NULL," +
                    DbConfig.Expenses.COLUMN_NAME_CENTS_AMOUNT + " REAL NOT NULL," +
                    DbConfig.Expenses.COLUMN_NAME_NOTES + " TEXT," +
                    DbConfig.Expenses.COLUMN_NAME_CATEGORY + " INTEGER," +
                    "FOREIGN KEY(" + DbConfig.Expenses.COLUMN_NAME_CATEGORY + ") REFERENCES " + DbConfig.Category.TABLE_NAME + "(" + DbConfig.Category.COLUMN_NAME_ID +"));";

    private static final String SQL_DELETE_CATEGORY =
            "DROP TABLE IF EXISTS " + DbConfig.Category.TABLE_NAME;

    private static final String SQL_DELETE_EXPENSES =
            "DROP TABLE IF EXISTS " + DbConfig.Expenses.TABLE_NAME;
    
    public DbReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CATEGORY);
        db.execSQL(SQL_CREATE_EXPENSES);
        Log.d(TAG,"onCreate method, creating tables!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_EXPENSES);
        db.execSQL(SQL_DELETE_CATEGORY);
        Log.d(TAG,"onUpdate method, dropping tables and recreating them!");
        onCreate(db);
    }
}
