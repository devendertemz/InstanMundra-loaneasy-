package com.loaneasy.fcm;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.stats.GCoreWakefulBroadcastReceiver;
import com.loaneasy.utils.NotifDatabase;

public class NotificationReceiver extends GCoreWakefulBroadcastReceiver {

    NotifDatabase notifDatabase;
    String flag = "";

    public void onReceive(Context context, Intent intent) {
        // playNotificationSound(context);

        //  AudioPlay.playAudio(context, R.raw.bryan_sample);
        notifDatabase = new NotifDatabase(context);

        if (intent.getExtras() != null) {
            flag = intent.getExtras().getString("flag");
            if (flag != null) {
                if (!flag.isEmpty() && flag.equalsIgnoreCase("im")) {
                    Toast.makeText(context, intent.getExtras().getString("msg"), Toast.LENGTH_SHORT).show();
                    boolean bl = notifDatabase.insertData(intent.getExtras().getString("msg"));
                    Log.e("TAG111","-inserted--->"+bl);
                    notifDatabase.closedb();
                }
            }
        }
    }

    public void playNotificationSound(Context context) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
