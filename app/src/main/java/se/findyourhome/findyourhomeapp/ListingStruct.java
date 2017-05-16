package se.findyourhome.findyourhomeapp;


import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

public class ListingStruct {
    public String url, address, price, pubDate, contract, area, size;
    public int seqNumber;
    byte[] image;

    private static String optStringFromCursor(Cursor c, String colName) {
        // TODO: Deal with bad column names.
        int idx = c.getColumnIndex(colName);
        if(idx == -1) {
            System.out.println("DBG: optStringFromCursor(): no such column '" + colName + "'");
            return null;
        }

        String ret = c.getString(idx);
        if(ret == null) {
            System.out.println("DBG: optStringFromCursor(): got null back with colName '" + colName + "'");
            return null;
        }

        return ret;
    }

    private static int optIntFromCursor(Cursor c, String colName) {
        String s = optStringFromCursor(c, colName);

        int num;
        if(s == null) {
            num = -1;
        } else {
            try {
                num = Integer.parseInt(s);
            } catch (NumberFormatException ex) {
                num = -1;
            }
        }

        return num;
    }

    private static byte[] optByteArrayFromCursor(Cursor c, String colName) {
        // TODO: Deal with bad column names.
        int idx = c.getColumnIndex(colName);
        if(idx == -1) {
            System.out.println("DBG: optBlobFromCursor(): no such column '" + colName + "'");
            return null;
        }

        byte[] ret = c.getBlob(idx);
        if(ret == null) {
            System.out.println("DBG: optBlobFromCursor(): got null back with colName '" + colName + "'");
            return null;
        }

        return ret;
    }

    private static byte[] readWholeByteStream(InputStream is) {
        int size = 100 * 1024;
        int totBytesRead = 0;

        LinkedList<Buffer> buffers = new LinkedList<>();
        try {
            while(true) {
                byte[] b = new byte[size];
                int bytesRead = is.read(b);

                if(bytesRead == -1) { break; }

                buffers.add(new Buffer(b, bytesRead));
                totBytesRead += bytesRead;
            }

        } catch(IOException ex) {
            System.out.println("DBG: exception reading img data: " + ex);
            return null;
        }

        byte[] ret = new byte[totBytesRead];
        int i = 0;
        for(Buffer b : buffers) {
            System.arraycopy(b.a, 0, ret, i, b.size);
            i += b.size;
        }

        return ret;
    }

    private static byte[] blobFromUrl(String url) {
        if(url == null) {
            System.out.println("DBG: Can't get blob from null url");
            return null;
        }

        byte[] ret;

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            InputStream is = conn.getInputStream();
            ret = readWholeByteStream(is);
        } catch(IOException ex) {
            System.out.println("DBG: Error establishing connection to img '" + url + "'");
            ret = null;
        }

        return ret;
    }

    public ListingStruct(String url, int seqNumber, String address, String price, String pubDate,
            byte[] image, String contract, String area, String size) {
        this.url = url;
        this.seqNumber = seqNumber;
        this.address = address;
        this.price = price;
        this.pubDate = pubDate;
        this.image = image;
        this.contract = contract;
        this.area = area;
        this.size = size;
    }

    public ListingStruct(Cursor c) {
        this(   optStringFromCursor(c, ListingDbContract.Listing.COLUMN_NAME_URL),
                optIntFromCursor(c, ListingDbContract.Listing.COLUMN_NAME_SEQ),
                optStringFromCursor(c, ListingDbContract.Listing.COLUMN_NAME_ADDRESS),
                optStringFromCursor(c, ListingDbContract.Listing.COLUMN_NAME_PRICE),
                optStringFromCursor(c, ListingDbContract.Listing.COLUMN_NAME_PUB_DATE),
                optByteArrayFromCursor(c, ListingDbContract.Listing.COLUMN_NAME_IMAGE),
                optStringFromCursor(c, ListingDbContract.Listing.COLUMN_NAME_CONTRACT),
                optStringFromCursor(c, ListingDbContract.Listing.COLUMN_NAME_AREA),
                optStringFromCursor(c, ListingDbContract.Listing.COLUMN_NAME_SIZE)
        );
    }



    public ListingStruct(JSONObject jObj) {
        this(   jObj.optString("id"),
                jObj.optInt("seqNumber", -1),
                jObj.optString("addess"),
                jObj.optString("price"),
                jObj.optString("publishedDate"),
                blobFromUrl(jObj.optString("imageUrl")),
                jObj.optString("contract"),
                jObj.optString("area"),
                jObj.optString("size")

        );
    }

    public ContentValues toContentValues() {
        ContentValues ret = new ContentValues();

        ret.put(ListingDbContract.Listing.COLUMN_NAME_URL, url);
        ret.put(ListingDbContract.Listing.COLUMN_NAME_SEQ, seqNumber);
        ret.put(ListingDbContract.Listing.COLUMN_NAME_ADDRESS, address);
        ret.put(ListingDbContract.Listing.COLUMN_NAME_PRICE, price);
        ret.put(ListingDbContract.Listing.COLUMN_NAME_PUB_DATE, pubDate);
        ret.put(ListingDbContract.Listing.COLUMN_NAME_IMAGE, image);
        ret.put(ListingDbContract.Listing.COLUMN_NAME_CONTRACT, contract);
        ret.put(ListingDbContract.Listing.COLUMN_NAME_AREA, area);
        ret.put(ListingDbContract.Listing.COLUMN_NAME_SIZE, size);

        return ret;
    }
}

class Buffer {
    public byte[] a;
    public int size;

    public Buffer(byte[] a, int size) {
        this.a = a;
        this.size = size;
    }
}