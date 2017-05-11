package se.findyourhome.findyourhomeapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class FetchListingsIntentService extends IntentService {

    public FetchListingsIntentService() {
        super("FetchListingsIntentService");
    }

    private JSONArray readAllJSON(HttpURLConnection conn) {
        JSONArray a;

        try {
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            a = new JSONArray(sb.toString());
        } catch(Exception ex) { // JSONException or IOException.
            a = null;
        }

        return a;
    }

    private String fetchAndInsert(SQLiteDatabase db) throws IOException {
        URL url = new URL("http://findyourhome.se:2932/api?apikey=aKwo4vIzpEKSeE70kQG7yQoLAfHV2lPo");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        JSONArray jArr = readAllJSON(conn);
        for(int i=0; i<jArr.length(); ++i) {
            JSONObject jObjListing = jArr.optJSONObject(i);
            if(jObjListing == null) {
                System.out.println("DBG: An elem was not an obj...");
            } else {
                ListingStruct listing = new ListingStruct(jObjListing);
                ContentValues cvs = listing.toContentValues();
                db.insert(ListingDbContract.Listing.TABLE_NAME, null, cvs);
            }
        }


        return "resp code = " + conn.getResponseCode() + " & jArr.len = " + jArr.length();
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        ListingDbHelper dbHelper = new ListingDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String resp;
        try {
            resp = fetchAndInsert(db);
        }
        catch(IOException ex) {
            resp = "exception on fetchAndInsert()";
        }

        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + ListingDbContract.Listing.TABLE_NAME, null);

        String countString;
        if(c.moveToFirst()) {
            countString = "" + c.getInt(0);
        } else {
            countString = "NaN";
        }

        // The intention to start the activity showing the listings.
        Intent showInfoIntent = new Intent(this, DisplayMetaActivity.class);

        showInfoIntent.putExtra(DisplayMetaActivity.EXTRA_COUNT, countString);
        showInfoIntent.putExtra(DisplayMetaActivity.EXTRA_NET_STAT, isNetworkConnected());
        showInfoIntent.putExtra(DisplayMetaActivity.EXTRA_RESP, resp);

        PendingIntent pendingShowInfoIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        showInfoIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        // The notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.notif_icon)
                        .setContentTitle("New listings")
                        .setContentText("TEXT")
                        .setContentIntent(pendingShowInfoIntent);

        NotificationManager notMgr = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notMgr.notify(1, mBuilder.build());

    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


}
