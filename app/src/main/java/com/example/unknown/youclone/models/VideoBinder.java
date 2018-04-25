package com.example.unknown.youclone.models;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.example.unknown.youclone.network.IyouTubeGetContentDetailsService;
import com.example.unknown.youclone.network.models1.Item;
import com.example.unknown.youclone.network.models1.Duca;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeIntents;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.example.unknown.youclone.Constants;
import com.example.unknown.youclone.IFragmentManager;
import com.example.unknown.youclone.R;
import com.example.unknown.youclone.VideoViewHolder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.example.unknown.youclone.MainActivity.mFirebaseAnalytics;


public class VideoBinder {
    private static final String TAG = VideoBinder.class.getSimpleName();
    private static final int PREFIX = 12331903;
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    private static YouTubePlayerSupportFragment youTubePlayerFragment;
    private static YouTubePlayer youTubePlayer;
    private boolean found;
    private int totalsecs , secs ,hour , min;

    private static boolean isFullScreen = false;
    private Video video;



    private ImageRequest imageRequest;
    private Uri uri;


    VideoBinder(Video video ) {


        this.video = video;

    }

    public void prepare() {
        if (!TextUtils.isEmpty(video.image.url) && uri == null) {
            try {
                uri = Uri.parse(video.image.url);
            } catch (Exception e) {
                Log.e(TAG, "", e);
            }
        }
    }

    public void bind(final VideoViewHolder videoViewHolder, final IFragmentManager fragmentManager) {
        videoViewHolder.image.setAspectRatio(16f / 9f);
        if (imageRequest == null) {
            videoViewHolder.image.post(new Runnable() {
                @Override
                public void run() {
                    ImageRequestBuilder builder;
                    if (uri == null) {
                        builder = ImageRequestBuilder.newBuilderWithResourceId(android.R.color.darker_gray);
                    } else {
                        builder = ImageRequestBuilder.newBuilderWithSource(uri);
                    }
                    imageRequest = builder.setResizeOptions(new ResizeOptions(
                            videoViewHolder.image.getWidth(), videoViewHolder.image.getHeight()
                    )).build();
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(imageRequest)
                            .setOldController(videoViewHolder.image.getController())
                            .build();
                    videoViewHolder.image.setController(controller);
                }
            });
        } else {
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setOldController(videoViewHolder.image.getController())
                    .build();
            videoViewHolder.image.setController(controller);
        }
        bindVideo(videoViewHolder, fragmentManager);
        bindTitle(videoViewHolder);
        bindDescription(videoViewHolder);
    }

    private void bindVideo(final VideoViewHolder viewHolder,
                           final IFragmentManager fragmentManager) {
        View view = viewHolder.itemView.findViewWithTag(viewHolder.itemView.getContext()
                .getString(R.string.video_component_tag));
        if (view != null) {
            view.setId(PREFIX + viewHolder.getAdapterPosition());
        }
        handleClick(viewHolder, fragmentManager);
    }

    private void bindTitle(final VideoViewHolder videoViewHolder) {
        videoViewHolder.title.setText(video.title);
    }

    private void bindDescription(final VideoViewHolder videoViewHolder) {
        videoViewHolder.description.setText(video.description);
    }

    private void handleClick(final VideoViewHolder viewHolder,
                             final IFragmentManager fragmentManager) {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (TextUtils.isEmpty(video.videoId)) {
                    return;
                }
                if (!YouTubeIntents.isYouTubeInstalled(view.getContext()) ||
                        YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(view.getContext()) != YouTubeInitializationResult.SUCCESS) {
                    if (YouTubeIntents.canResolvePlayVideoIntent(view.getContext())) {
                        fragmentManager.getSupportFragment().
                                startActivity(YouTubeIntents.createPlayVideoIntent(view.getContext(), video.videoId));
                        return;
                    }
                    Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASE_URL + video.videoId));
                    fragmentManager.getSupportFragment().startActivity(viewIntent);
                    return;
                }
                if (viewHolder.videoContainer.getChildCount() == 0) {
                    if (youTubePlayerFragment == null) {
                        youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
                    }
                    if (youTubePlayerFragment.isAdded()) {
                        if (VideoBinder.youTubePlayer != null) {
                            try {
                                VideoBinder.youTubePlayer.pause();
                                VideoBinder.youTubePlayer.release();
                            } catch (Exception e) {
                                if (VideoBinder.youTubePlayer != null) {
                                    try {
                                        VideoBinder.youTubePlayer.release();
                                    } catch (Exception ignore) {
                                    }

                                }
                            }
                            VideoBinder.youTubePlayer = null;
                        }

                        fragmentManager.getSupportFragmentManager()
                                .beginTransaction()
                                .remove(youTubePlayerFragment)
                                .commit();
                        fragmentManager.getSupportFragmentManager()
                                .executePendingTransactions();
                        youTubePlayerFragment = null;
                    }
                    if (youTubePlayerFragment == null) {
                        youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
                    }
                    fragmentManager.getSupportFragmentManager()
                            .beginTransaction()
                            .add(PREFIX + viewHolder.getAdapterPosition(), youTubePlayerFragment)
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                            .commit();



                    youTubePlayerFragment.initialize(Constants.KEY,
                            new YouTubePlayer.OnInitializedListener() {
                                @Override
                                public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                                    YouTubePlayer youTubePlayer, boolean b) {

                                    VideoBinder.youTubePlayer = youTubePlayer;
                                    VideoBinder.youTubePlayer.loadVideo(video.videoId);
                                    VideoBinder.youTubePlayer.setFullscreenControlFlags(0);
                                    VideoBinder.youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                                        @Override
                                        public void onFullscreen(boolean b) {
                                            isFullScreen = b;
                                        }
                                    });


                                    youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
                                    youTubePlayer.setPlaybackEventListener(playbackEventListener);

                                }

                                @Override
                                public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                                    YouTubeInitializationResult youTubeInitializationResult) {
                                    Log.e(VideoBinder.class.getSimpleName(), youTubeInitializationResult.name());
                                    if (YouTubeIntents.canResolvePlayVideoIntent(
                                            fragmentManager.getSupportFragment().getContext())) {
                                        fragmentManager.getSupportFragment()
                                                .startActivity(YouTubeIntents.createPlayVideoIntent(
                                                        fragmentManager.getSupportFragment().getContext(),
                                                        video.videoId));
                                        return;
                                    }
                                    Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASE_URL + video.videoId));
                                    fragmentManager.getSupportFragment().startActivity(viewIntent);
                                }

                                private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
                                    @Override
                                    public void onPlaying() {

                                    }

                                    @Override
                                    public void onPaused() {

                                    }

                                    @Override
                                    public void onStopped() {

                                    }

                                    @Override
                                    public void onBuffering(boolean b) {

                                    }

                                    @Override
                                    public void onSeekTo(int i) {

                                    }
                                };

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
                                        Log.i("Video Title" , "Video has Started : Title ="+video.title);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("Video_Title" , video.title.substring(0,30));
                                        mFirebaseAnalytics.logEvent("video_started_playing" , bundle);


                                    }

                                    @Override
                                    public void onVideoEnded() {

                                         IyouTubeGetContentDetailsService.YouTubeItemContentDetailsService
                                                .getIyouTubeGetContentDetailsService()
                                                .getDetails("contentDetails" ,video.videoId , Constants.KEY)
                                                .enqueue(new Callback<Duca>() {
                                                    @Override
                                                    public void onResponse(Call<Duca> call, Response<Duca> response) {
                                                        if (response.isSuccessful()) {
                                                            for (Item item : response.body().getItems()){


                                                                String string1 = video.title.substring(0,25);
                                                                String string2 = item.getContentDetails().getDuration().substring(2);
                                                                String hourmin = string2.substring(0,string2.indexOf("M"));
                                                                secs = Integer.parseInt(string2.substring(string2.indexOf("M")+1 , string2.indexOf("S")));
                                                                found = hourmin.contains("H");
                                                                if (found ){
                                                                    hour = Integer.parseInt(hourmin.substring(0 , hourmin.indexOf("H")));
                                                                    min = Integer.parseInt(hourmin.substring(hourmin.indexOf("H")+1 ,hourmin.length()));
                                                                    totalsecs = (hour * 60) + (min * 60) + secs ;
                                                                    Log.i("Data Received is" , Integer.toString(totalsecs));
                                                                    Bundle bundle = new Bundle();
                                                                    bundle.putString("Video_Title" , string1);
                                                                    bundle.putLong(FirebaseAnalytics.Param.VALUE, totalsecs);
                                                                    mFirebaseAnalytics.logEvent("video_play_duration" , bundle);

                                                                }else {
                                                                    min = Integer.parseInt(hourmin) ;
                                                                    totalsecs =  (min * 60) + secs ;
                                                                    Log.i("Data Received is" , Integer.toString(totalsecs));
                                                                    Bundle bundle = new Bundle();
                                                                    bundle.putString("Video_Title" , string1);
                                                                    bundle.putLong(FirebaseAnalytics.Param.VALUE, totalsecs);
                                                                    mFirebaseAnalytics.logEvent("video_play_duration" , bundle);
                                                                }

                                                            }

                                                        } else {
                                                            //unsuccessful response
                                                            Log.i("Info" , "You are doing something wrong here");

                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Duca> call, Throwable t) {

                                                    }
                                                });

                                    }

                                    @Override
                                    public void onError(YouTubePlayer.ErrorReason errorReason) {

                                    }
                                };
                            });
                }
            }
        });
    }

    public void unBind(final VideoViewHolder videoViewHolder, IFragmentManager fragmentManager) {
        if (videoViewHolder.videoContainer.getChildCount() > 0) {
            if (youTubePlayerFragment != null && youTubePlayerFragment.isAdded()) {
                if (VideoBinder.youTubePlayer != null) {
                    try {
                        VideoBinder.youTubePlayer.pause();
                        VideoBinder.youTubePlayer.release();
                    } catch (Exception e) {
                        if (VideoBinder.youTubePlayer != null) {
                            try {
                                VideoBinder.youTubePlayer.release();
                            } catch (Exception ignore) {}
                        }
                    }
                    VideoBinder.youTubePlayer = null;
                }

                fragmentManager.getSupportFragmentManager()
                        .beginTransaction()
                        .remove(youTubePlayerFragment)
                        .commit();
                fragmentManager.getSupportFragmentManager()
                        .executePendingTransactions();
                youTubePlayerFragment = null;
            }
        }
    }
}
