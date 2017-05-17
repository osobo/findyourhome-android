package se.findyourhome.findyourhomeapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ListingDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "Listing.db";


    public ListingDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    private void resetDb(SQLiteDatabase db) {
        System.out.println("DBG: Resetting DB");
        db.execSQL(ListingDbContract.SQL_DELETE_LISTINGS);
        db.execSQL(ListingDbContract.SQL_DELETE_FAVORITES);
        onCreate(db);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ListingDbContract.SQL_CREATE_LISTINGS);
        db.execSQL(ListingDbContract.SQL_CREATE_FAVORITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        resetDb(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }





}