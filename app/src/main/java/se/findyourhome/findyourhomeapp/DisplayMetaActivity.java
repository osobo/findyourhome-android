package se.findyourhome.findyourhomeapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DisplayMetaActivity extends AppCompatActivity {

    public static final String EXTRA_COUNT = "se.findyouhome.myapplication.COUNT";
    public static final String EXTRA_NET_STAT = "se.findyouhome.myapplication.NET_STAT";
    public static final String EXTRA_RESP = "se.findyouhome.myapplication.RESP_CODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_meta);

        Intent intent = getIntent();

        String countStr = intent.getStringExtra(EXTRA_COUNT);
        TextView countView = (TextView) findViewById(R.id.countView);
        countView.setText("Number of listings in local DB: " + countStr);

        boolean netStat = intent.getBooleanExtra(EXTRA_NET_STAT, false);
        TextView netStatView = (TextView) findViewById(R.id.netStatView);
        netStatView.setText("Network status: " + netStat);

        String resp = intent.getStringExtra(EXTRA_RESP);
        TextView respCodeView = (TextView) findViewById(R.id.respView);
        respCodeView.setText("Response: " + resp);
    }

    public void toListings(View view) {
        Intent toListingsIntent = new Intent(this, DisplayListingsActivity.class);
        startActivity(toListingsIntent);
    }
}
