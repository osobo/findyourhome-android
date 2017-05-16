package se.findyourhome.findyourhomeapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class FetchListingsService extends Service {

    private boolean running;
    private SQLiteDatabase db;
    private boolean dbReady;

    public FetchListingsService() {
        running = false;
        db = null;
        dbReady = false;
    }

    private synchronized void setup() {
        running = true;
        db = new ListingDbHelper(this).getWritableDatabase();
        System.out.println("DBG: Service setup with new db handler.");
    }

    private synchronized void cleanup() {
        running = false;
        db.close();
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

    /**
     * Fetches new listings from server and inserts them into local db.
     * @param db The db to insert into.
     * @return True of something new was fetched. Otherwise false.
     * @throws IOException Thrown on network issues.
     */
    private boolean fetchAndInsert(SQLiteDatabase db) throws IOException {
        URL url = new URL("http://findyourhome.se:2932/api?apikey=aKwo4vIzpEKSeE70kQG7yQoLAfHV2lPo");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        boolean ret = false;

        JSONArray jArr = readAllJSON(conn);
        for(int i=0; i<jArr.length(); ++i) {
            JSONObject jObjListing = jArr.optJSONObject(i);
            if(jObjListing == null) {
                System.out.println("DBG: An elem was not an obj...");
            } else {
                ListingStruct listing = new ListingStruct(jObjListing);
                ContentValues cvs = listing.toContentValues();
                long rowId = db.insert(ListingDbContract.Listing.TABLE_NAME, null, cvs);
                if(rowId != -1) { // The insertion was successful -> the listing was new.
                    ret = true;
                }
            }
        }

        return ret;
    }

    private void issueNotification() {
        // The intent to start the activity showing the listings.
        Intent showInfoIntent = new Intent(this, DisplayListingsActivity.class);

        PendingIntent pendingShowInfoIntent = PendingIntent.getActivity(
                this,
                0,
                showInfoIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // The notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.notif_icon)
                        .setContentTitle("New listings available")
                        .setContentText("")
                        .setContentIntent(pendingShowInfoIntent);

        NotificationManager notMgr = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notMgr.notify(1, mBuilder.build());
    }

    private void broadCastDbReady() {
        if(dbReady) {
            Intent intent = new Intent("db-ready");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Indefinitely fetches and updates.
     */
    private void taskToDo() {
        for(;;) {
            try {
                boolean result = fetchAndInsert(db);
                dbReady = true;
                broadCastDbReady();

                // Display notification if something new was fetched.
                if (result) {
                    System.out.println("DBG: New listings found. Notifying!");
                    issueNotification();
                }
            } catch (IOException ex) {
                // Failed to fetch listings. Log and ignore.
                System.out.println("DBG: Exception on fetchAndInsert: " + ex);
            }

            try {
                //Thread.sleep(1000*60*2);
                Thread.sleep(1000*10);
            } catch(IllegalArgumentException ex) {
                // Only happens when time passed is negative.
            } catch(InterruptedException ex) {
                // TODO: How should this be dealt with?
                // For now: kill the service.
                System.out.println("DBG: FetchListingsService thread was interrupted: " + ex);
                this.running = false;
                break;
            }

        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!running) {
            setup();
            new Thread(new TaskStarter()).start();
        }
        broadCastDbReady();
        return START_STICKY;
    }


    /**
     * @param intent
     * @return Always returns null since the service isn't meant to be bound.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class TaskStarter implements Runnable {
        @Override
        public void run() {
            taskToDo();
        }
    }
}
