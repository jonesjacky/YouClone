package com.example.unknown.youclone.network;


import com.example.unknown.youclone.network.models1.Duca;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by Unknown .
 */

public interface IyouTubeGetContentDetailsService {

    @GET( "youtube/v3/videos")
    Call<Duca> getDetails(@Query("part") String content , @Query("id") String id , @Query("key") String KEY);

    public class YouTubeItemContentDetailsService {


       public static IyouTubeGetContentDetailsService iyouTubeGetContentDetailsService;
        public static IyouTubeGetContentDetailsService getIyouTubeGetContentDetailsService(){

            if (iyouTubeGetContentDetailsService == null){

                Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.googleapis.com/").addConverterFactory(GsonConverterFactory.create()).build();
                iyouTubeGetContentDetailsService = retrofit.create(IyouTubeGetContentDetailsService.class);
                return iyouTubeGetContentDetailsService;
            } else {
                return iyouTubeGetContentDetailsService;
            }


        }
    }

}
