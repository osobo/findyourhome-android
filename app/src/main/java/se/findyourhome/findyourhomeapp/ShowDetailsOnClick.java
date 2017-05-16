package se.findyourhome.findyourhomeapp;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;


public class ShowDetailsOnClick implements AdapterView.OnItemClickListener {

    private Context ctx;

    public ShowDetailsOnClick(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String listingUrl = (String) view.getTag();

        Intent detailIntent = new Intent(ctx, ListingDetailsActivity.class);
        detailIntent.putExtra(ListingDetailsActivity.URL_EXTRA_ARG, listingUrl);

        ctx.startActivity(detailIntent);
    }
}
