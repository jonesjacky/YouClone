package com.example.unknown.youclone;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import static android.app.Notification.*;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String vid,titl;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        vid = remoteMessage.getData().get("video_id");
        titl = remoteMessage.getData().get("title");

        if ((titl.contains("LIVE") || titl.contains("Live"))|| remoteMessage.getData().containsKey("liveBroadcastContent")) {

                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("vide_id", vid);
                intent.putExtra("live", titl);
                intent.putExtra("channel_name", remoteMessage.getData().get("channel_name"));
                PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setContentTitle(remoteMessage.getData().get("channel_name") + " is LIVE now")
                        .setContentText(remoteMessage.getData().get("title"))
                        .setSmallIcon(android.R.drawable.stat_notify_chat)
                        .setContentIntent(pi)
                        .setPriority(PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setDefaults(-1);


                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.notify(0, builder.build());
            }else {

                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("vide_id", vid);
                intent.putExtra("titl" , titl);
                PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setContentTitle("New Video from "+remoteMessage.getData().get("channel_name")+" is available")
                        .setContentText(remoteMessage.getData().get("title"))
                        .setSmallIcon(android.R.drawable.stat_notify_chat)
                        .setContentIntent(pi)
                        .setPriority(PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setDefaults(-1);


                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.notify(0, builder.build());

            }


    }
}