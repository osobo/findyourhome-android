package se.findyourhome.findyourhomeapp;


import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListViewHelper {
    private final Activity ctx;
    private final int layoutResource;
    private ListView listView;

    private ArrayList<ListingStruct> listings;
    private ListingArrayAdapter adapter;

    public ListViewHelper(Activity ctx, int layoutResource, ListView listView) {
        this.ctx = ctx;
        this.layoutResource = layoutResource;
        this.listView = listView;

        this.listings = new ArrayList<>();
        this.adapter = new ListingArrayAdapter(ctx, layoutResource, this.listings);

        this.listView.setAdapter(this.adapter);
    }

    public void update(ListingStruct[] newListings) {
        listings.addAll(Arrays.asList(newListings));
        adapter.notifyDataSetChanged();
    }

    public int getOldestSeqNumber() {
        if(listings.size() < 1) {
            return -1;
        }

        return listings.get(listings.size()-1).seqNumber;
    }

    public ListView getListView() {
        return this.listView;
    }

    public boolean atBottom() {
        return !listView.canScrollVertically(1);
    }
}


class ListingArrayAdapter extends ArrayAdapter<ListingStruct> {
    private final Activity ctx;
    private final int layoutResource;

    /*
     * layoutResource will be used for each entry (listing) in the list. The views with the
     * following ids and type will be filled with data if they are found in the layout.
     * - addressView: TextView
     * - priceView: TextView
     * - pubDateView: TextView
     * - imgView: (ImageView?)
     */
    ListingArrayAdapter(Activity ctx, int layoutResource, List<ListingStruct> listings) {
        super(ctx, 0, listings);
        this.ctx = ctx;
        this.layoutResource = layoutResource;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ctx.getLayoutInflater();
        View entryView = inflater.inflate(layoutResource, parent, false);

        ListingStruct listing = getItem(pos);

        TextView adrView = (TextView) entryView.findViewById(R.id.addressView);
        TextView priceView = (TextView) entryView.findViewById(R.id.priceView);
        TextView pubDateView = (TextView) entryView.findViewById(R.id.pubDateView);
        ImageView imgView = (ImageView) entryView.findViewById(R.id.imgView);

        if(adrView != null) { adrView.setText(listing.address); }
        if(priceView != null) { priceView.setText(listing.price); }
        if(pubDateView != null) { pubDateView.setText(listing.pubDate); }
        if(imgView != null) {
            imgView.setImageBitmap(BitmapFactory.decodeByteArray(   listing.image,
                    0,
                    listing.image.length
            ));
        }

        return entryView;
    }
}