package se.findyourhome.findyourhomeapp;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;


public class DisplayListingsActivity extends AppCompatActivity {

    /*
     * TODO: Data should be queried from db and added to screen in seperate thread!
     * TODO: Also, maybe load only some (say 20) listings at a time.
     */

    /**
     * The number of listings to load from local db into view at a time.
     */
    private int defaultBatchSize = 10;

    /**
     * If there are less than this many listings left a new batch should be loaded.
     */
    private int defaultScrollMargin = 3;


    private ListViewHelper viewHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_listings);

        ListView listView = (ListView) findViewById(R.id.listingList);

        viewHelper = new ListViewHelper(this, R.layout.listing_entryimg_view, listView);

        //loadBatch(-1, 1);
        initLoadBatch(defaultBatchSize);
        listView.setOnScrollListener(new LoadOnScrollListener());
    }

    /**
     * Loads more Listings from local db into listView.
     * @param batchSize The number of listings to load.
     */
    private void loadBatch(int batchSize) {
        // TODO: Add new listings based off seqNumber (which you can get from helper).

        int seqNum = viewHelper.getOldestSeqNumber();

        ListingDbHelper dbHelper = new ListingDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query =
            "SELECT * FROM " + ListingDbContract.Listing.TABLE_NAME;

        if(seqNum > -1) {
            query += " WHERE " + ListingDbContract.Listing.COLUMN_NAME_SEQ + "<" + seqNum;
        }

        query += " ORDER BY " + ListingDbContract.Listing.COLUMN_NAME_SEQ + " DESC";
        query += " LIMIT " + batchSize;

        Cursor resCursor = db.rawQuery(query, null);

        ListingStruct[] listings = new ListingStruct[resCursor.getCount()];

        if(listings.length >= 1) {
            if (resCursor.moveToFirst()) {
                for (int i = 0; i < listings.length; ++i) {
                    listings[i] = new ListingStruct(resCursor);
                    resCursor.moveToNext();
                }
                viewHelper.update(listings);
            } else {
                System.out.println("DBG: Coulnd't move to the first of resCursor");
            }
        }

        db.close();
    }

    private void initLoadBatch(int batchSize) {
        do {
            loadBatch(batchSize);
        } while(viewHelper.atBottom());

        // Final batch
        loadBatch(batchSize);
    }


    private class LoadOnScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) { }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(totalItemCount - (firstVisibleItem + visibleItemCount) <= defaultScrollMargin) {
                loadBatch(defaultBatchSize);
            }
        }
    }

}





