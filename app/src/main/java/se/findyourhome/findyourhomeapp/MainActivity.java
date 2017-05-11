package se.findyourhome.findyourhomeapp;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "se.findyouhome.myapplication.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    /** Called when the button is tapped */
    public void updateListings(View view) {
        System.out.println("DBG: Starting FetchListingsIntentService");
        Intent updateIntent = new Intent(this, FetchListingsIntentService.class);
        startService(updateIntent);
    }



}
