package com.example.unknown.youclone;


import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class Main2Activity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Bundle data = new Bundle();
            data.putString("Video_ID", getIntent().getStringExtra("video_data"));
            data.putString("Video_Title", getIntent().getStringExtra("video_title"));
            data.putString("ch_name", getIntent().getStringExtra("c_name"));
            YoutubeNotificVid fragment = new YoutubeNotificVid();
            fragment.setArguments(data);
            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .replace(R.id.activity_main2, fragment)
                    .commit();

    }
}
