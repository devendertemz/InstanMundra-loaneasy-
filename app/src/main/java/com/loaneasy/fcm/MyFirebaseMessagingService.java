package com.loaneasy.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;


import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.loaneasy.HomeActivity;
import com.loaneasy.R;

import java.util.Map;

/**
 * Created by Ravindra on 17-Sep-17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Map<String, String> data;
    String driverName, driverNo, vehicleNo, bookingId;
    public static String BROADCAST_ACTION = "android.intent.action.SmsReceiver";

   /* @Override
    public boolean zzH(Intent intent) {

        Log.i(" ", "--------->" + intent.toString());
        return super.zzH(intent);
    }*/


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional

        //String text = remoteMessage.getNotification().getBody();

        /* String title = remoteMessage.getNotification().getTitle();
         */

       /* NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_bell)
                .setContentTitle(title)
                .setContentText(text)
                .build();
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL|;
        notificationManager.notify(1000, notification);*/

        /*NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_bell)
                .setSubText(text).setContentTitle(title);

        notificationBuilder.notify(1, notificationBuilder.build());*/

       /* if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload:" + remoteMessage.getData());
            data =remoteMessage.getData();

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body:" + remoteMessage.getNotification().getBody());
        }

        //Calling method to generate notification
        sendNotification(remoteMessage.getNotification().getBody());*/

      /*  Log.d(TAG, "From: " + remoteMessage.getFrom());


        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            sendNotification(remoteMessage.getData().get("msg"));

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
        }*/
    }


   /* private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }*/

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }


    /*private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code*//* , intent,
                PendingIntent.FLAG_ONE_SHOT);

        //String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_bell)
                        .setContentTitle("Chalo Bhai")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0  *//*ID of notification*//* , notificationBuilder.build());
    }*/


    //This method is only generating push notification
    //It is same as we did in earlier posts


    //This method is only generating push notification
   /* private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        // Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "IM_1")
                .setSmallIcon(R.drawable.ic_bell)
                .setContentTitle("Micro Browser")
                .setContentText(messageBody)
                .setAutoCancel(true)
                //   .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_tone))
                //.setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }*/

    private void sendNotification(String message) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notif";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel("ID_1", name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "ID_1")
                //         .addAction()
                .setSmallIcon(R.drawable.ic_notif)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.logo_notif))
                //.setColor(getApplicationContext().getResources().getColor(R.color.colorPrimary))
                .setContentTitle("Instant Mudra")
                .setContentText(message)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                //.setSound(Uri.parse("android.resource://" + getPackageName() + "/raw/spring_board"))
                //.setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .setContentIntent(pendingIntent);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
