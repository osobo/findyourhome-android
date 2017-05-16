package se.findyourhome.findyourhomeapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

public class DisplayFavoritesActivity extends AppCompatActivity {

    private ListViewHelper viewHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_listings);

        ListView listView = (ListView) findViewById(R.id.listingList);

        viewHelper = new ListViewHelper(this, R.layout.listing_entryimg_view, listView);

        viewHelper.getListView().setOnItemClickListener(new ShowDetailsOnClick(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("DBG: RESUME!");
        viewHelper.removeAll();
        addAllFavorites();
    }


    private void addAllFavorites() {
        ListingDbHelper helper = new ListingDbHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        String query = "SELECT * FROM " + ListingDbContract.Favorite.TABLE_NAME;
        Cursor c = db.rawQuery(query, null);

        while(c.moveToNext()) {
            String url = c.getString(c.getColumnIndex(ListingDbContract.Favorite.COLUMN_NAME_URL));
            if(url == null) {
                System.out.println("DBG: Hm, how did that happen?");
                continue;
            }

            addFavorite(url, db);
        }

        db.close();
    }

    private void addFavorite(String url, SQLiteDatabase db) {
        String query = "SELECT * FROM " + ListingDbContract.Listing.TABLE_NAME +
                " WHERE " + ListingDbContract.Listing.COLUMN_NAME_URL + " = '" + url + "'";
        Cursor c = db.rawQuery(query, null);
        if(!c.moveToFirst()) {
            System.out.println("DBG: This shouldn't happen either...");
            return;
        }

        ListingStruct listing = new ListingStruct(c);
        System.out.println("DBG: Adding new fav " + url);
        viewHelper.update(listing);
    }
}
