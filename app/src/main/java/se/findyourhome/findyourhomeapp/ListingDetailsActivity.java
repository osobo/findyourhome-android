package se.findyourhome.findyourhomeapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ListingDetailsActivity extends AppCompatActivity {

    public static String URL_EXTRA_ARG = "findyourhome.extra.listing-url";

    private ImageView imgView;
    private TextView addressView;
    private TextView priceView;
    private TextView sizeView;
    private TextView contractView;
    private TextView pubDateView;
    private TextView areaView;

    private ImageView favIconView;

    private String url;

    private boolean isFavorite = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_details);

        imgView = (ImageView) this.findViewById(R.id.imgView);
        addressView = (TextView) this.findViewById(R.id.addressView);
        priceView = (TextView) this.findViewById(R.id.priceView);
        sizeView = (TextView) this.findViewById(R.id.sizeView);
        contractView = (TextView) this.findViewById(R.id.contractView);
        pubDateView = (TextView) this.findViewById(R.id.pubDateView);
        areaView = (TextView) this.findViewById(R.id.areaView);

        favIconView = (ImageView) this.findViewById(R.id.favIcon);

        Intent intent = getIntent();
        this.url = intent.getStringExtra(URL_EXTRA_ARG);

        favIconView.setOnClickListener(new ToggleFavOnCLick());

        ListingDbHelper helper = new ListingDbHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        insertListingInfo(db);
        checkFavorite(db);

        db.close();
    }

    /**
     * By default this activity shows the listing as not be favorited. This methods checks if
     * the listing is favorited and changes to 'favIcon' to reflect that if so.
     * @param db The db to use to check if the listing is favorited.
     */
    private void checkFavorite(SQLiteDatabase db) {
        String query = "SELECT * FROM " + ListingDbContract.Favorite.TABLE_NAME +
                " WHERE " + ListingDbContract.Favorite.COLUMN_NAME_URL + " = '" + url + "'";

        Cursor c = db.rawQuery(query, null);

        if(c.moveToFirst()) {
            // If we could move to first there was a result => The listing is a favorite.
            setFavoriteIcon();
        }
        // Else: do nothing as the default is correct.
    }

    private void insertListingInfo(SQLiteDatabase db) {
        String query = "SELECT * FROM " + ListingDbContract.Listing.TABLE_NAME +
                " WHERE " + ListingDbContract.Listing.COLUMN_NAME_URL + " = '" + url + "'";

        Cursor c = db.rawQuery(query, null);

        if(!c.moveToFirst()) {
            onInvalidUrl();
        } else {
            ListingStruct listing = new ListingStruct(c);
            this.url = listing.url;

            imgView.setImageBitmap(BitmapFactory.decodeByteArray(listing.image, 0,
                    listing.image.length));
            addressView.setText(listing.address);
            priceView.setText(listing.price + " / month");
            pubDateView.setText(listing.pubDate);
            sizeView.setText(listing.size);
            areaView.setText(listing.area);
            contractView.setText(listing.contract);
        }
    }

    private void setFavoriteIcon() {
        favIconView.setImageResource(R.mipmap.favorite_icon);
    }

    private void unsetFavoriteIcon() {
        favIconView.setImageResource(R.mipmap.not_favorite_icon);
    }

    private void setFavoriteStat() {
        System.out.println("Setting fav " + url);
        ListingDbHelper helper = new ListingDbHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cvs = new ContentValues();
        cvs.put(ListingDbContract.Favorite.COLUMN_NAME_URL, url);
        db.insert(ListingDbContract.Favorite.TABLE_NAME, null, cvs);

        db.close();
    }

    private void unsetFavoriteStat() {
        System.out.println("Unsetting fav " + url);
        ListingDbHelper helper = new ListingDbHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        String whereClause = ListingDbContract.Favorite.COLUMN_NAME_URL + " = '" + url + "'";
        db.delete(ListingDbContract.Favorite.TABLE_NAME, whereClause, null);

        db.close();
    }

    private void onInvalidUrl() {
        // TODO
        System.out.println("DBG: Invalid url. Can't show details.");
        /*
        ((ViewManager)imgView.getParent()).removeView(imgView);
        ((ViewManager)addressView.getParent()).removeView(addressView);
        */
    }

    // Called when "goto site" button is clicked.
    public void gotoSite(View view) {
        System.out.println("DBG: Redirecting to site " + this.url);
        Uri uri = Uri.parse(url);
        System.out.println("DBG: uri: " + uri);
        Intent gotoSiteIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(gotoSiteIntent);
    }

    public void toggleFavorite() {
        if(isFavorite) {
            unsetFavoriteIcon();
            unsetFavoriteStat();
        } else {
            setFavoriteIcon();
            setFavoriteStat();
        }
        isFavorite = !isFavorite;
    }

    private class ToggleFavOnCLick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            toggleFavorite();
        }
    }

}