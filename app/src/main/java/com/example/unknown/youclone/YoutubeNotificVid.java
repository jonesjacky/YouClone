package com.example.unknown.youclone;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import static com.example.unknown.youclone.MainActivity.mFirebaseAnalytics;
import static com.example.unknown.youclone.R.id.*;



public class YoutubeNotificVid extends Fragment {

    public String Video_ID ;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Video_ID = getArguments().getString("Video_ID");

        View view = inflater.inflate(R.layout.fragment_youtube_notific_vid, container, false);
        TextView titletext = (TextView) view.findViewById(R.id.notiftitle);
        titletext.setText(getArguments().getString("Video_Title"));
        YouTubePlayerSupportFragment youTubePlayerSupportFragment = YouTubePlayerSupportFragment.newInstance();

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(youtube_layout , youTubePlayerSupportFragment).commit();


        youTubePlayerSupportFragment.initialize(Constants.KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                if (!b){

                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    youTubePlayer.loadVideo(Video_ID);
                    youTubePlayer.play();
                    youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                //You Tube error

                String error = youTubeInitializationResult.toString();
                Toast.makeText(getActivity() , error ,Toast.LENGTH_LONG).show();
                Log.d("Error Message", error);
            }

            YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
                @Override
                public void onLoading() {

                }

                @Override
                public void onLoaded(String s) {

                }

                @Override
                public void onAdStarted() {

                }

                @Override
                public void onVideoStarted() {

                    String string = getArguments().getString("Video_Title");
                    Bundle bundle = new Bundle();
                    bundle.putString("Watching_Live_Stream" ,string.substring(0,20));
                    bundle.putString("Channel_Name" ,getArguments().getString("ch_name"));
                    mFirebaseAnalytics.logEvent("Started_Watching_Live_Stream_Video" , bundle);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Live_Video" ,string.substring(0,15));
                    bundle1.putString("Channel_Name" ,getArguments().getString("ch_name"));
                    mFirebaseAnalytics.logEvent("Tried_Watching_Live_Video" , bundle1);
                }

                @Override
                public void onVideoEnded() {

                }

                @Override
                public void onError(YouTubePlayer.ErrorReason errorReason) {

                }
            };
        });


        return view;



    }

}

