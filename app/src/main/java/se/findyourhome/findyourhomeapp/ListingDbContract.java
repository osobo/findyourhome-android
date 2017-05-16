package se.findyourhome.findyourhomeapp;

import android.provider.BaseColumns;

public final class ListingDbContract {

    /** The class should never be instantiated. */
    private ListingDbContract() {}

    public static final class Listing implements BaseColumns{
        public static final String TABLE_NAME = "listing";

        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_SEQ = "seqNumber";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_PUB_DATE = "pubDate";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_CONTRACT = "contract";
        public static final String COLUMN_NAME_AREA = "area";
        public static final String COLUMN_NAME_SIZE = "size";
    }

    public static final String SQL_CREATE_LISTINGS =
        "CREATE TABLE " + Listing.TABLE_NAME + " (" +
        Listing._ID + " INTEGER PRIMARY KEY," +
        Listing.COLUMN_NAME_URL + " TEXT NOT NULL UNIQUE," +
        Listing.COLUMN_NAME_SEQ + " INTEGER," +
        Listing.COLUMN_NAME_ADDRESS + " TEXT," +
        Listing.COLUMN_NAME_PRICE + " TEXT," +
        Listing.COLUMN_NAME_PUB_DATE + " TEXT," +
        Listing.COLUMN_NAME_IMAGE + " BLOB," +
        Listing.COLUMN_NAME_CONTRACT + " TEXT," +
        Listing.COLUMN_NAME_AREA + " TEXT," +
        Listing.COLUMN_NAME_SIZE + " TEXT)";

    public static final String SQL_DELETE_LISTINGS =
        "DROP TABLE IF EXISTS " + Listing.TABLE_NAME;





    public static class Favorite implements BaseColumns {
        public static final String TABLE_NAME = "favorite";

        public static final String COLUMN_NAME_URL = "url";
    }

    public static final String SQL_CREATE_FAVORITES =
            "CREATE TABLE " + Favorite.TABLE_NAME + " (" +
                    Favorite._ID + " INTEGER PRIMARY KEY," +
                    Favorite.COLUMN_NAME_URL + " TEXT NOT NULL UNIQUE)";

    public static final String SQL_DELETE_FAVORITES =
            "DROP TABLE IF EXISTS " + Favorite.TABLE_NAME;




}
