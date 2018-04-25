package com.example.unknown.youclone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.facebook.drawee.backends.pipeline.Fresco;

public class MainActivity extends AppCompatActivity  {

    private static final String FRAGMENT_TAG = MainActivityFragment.class.getSimpleName();
    public static FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
        Fresco.initialize(this);
        ((EditText) findViewById(R.id.search)).addTextChangedListener(new TimedTextWatcher(600) {
            @Override
            public void work(final String text) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivityFragment fragment =
                                (MainActivityFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
                        fragment.setSearchString(text);
                    }
                });
            }
        });
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, new MainActivityFragment(), FRAGMENT_TAG)
                .commit();

        if (getIntent().hasExtra("vide_id")) {

            if(getIntent().hasExtra("titl")){
            Intent intent = new Intent(this, Main2Activity.class);

            intent.putExtra("video_data" , getIntent().getStringExtra("vide_id"));
            intent.putExtra("video_title" , getIntent().getStringExtra("titl"));

            startActivity(intent);}
            else {

                Bundle bundle = new Bundle();
                bundle.putString("Video_Title" , getIntent().getStringExtra("live"));
                bundle.putString("Channel_Name" , getIntent().getStringExtra("channel_name"));
                mFirebaseAnalytics.logEvent("tried_playing_live_notification" , bundle);

                Intent intent = new Intent(this, Main2Activity.class);

                intent.putExtra("video_data" , getIntent().getStringExtra("vide_id"));
                intent.putExtra("video_title" , getIntent().getStringExtra("live"));
                intent.putExtra("c_name" , getIntent().getStringExtra("channel_name"));

                startActivity(intent);

            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
