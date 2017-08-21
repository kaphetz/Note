package com.example.kienpt.note;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.kienpt.note.activities.EditActivity;
import com.example.kienpt.note.activities.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {
    public static String TITLE = "title";
    public static String ID = "id";

    @Override
    public void onReceive(Context context, Intent intent) {
        int result = intent.getIntExtra(ID, 0);
        Intent notificationIntent = new Intent(context, EditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(EditActivity.sKEY, result);
        notificationIntent.putExtras(bundle);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(result,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.setContentTitle(context.getString(R.string.note))
                .setContentText(intent.getStringExtra(TITLE))
                .setSmallIcon(R.drawable.ic_noti)
                .setLargeIcon(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.ic_note))
                .setDefaults(Notification.DEFAULT_LIGHTS |
                        Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent).build();
//        notification.defaults = Notification.DEFAULT_VIBRATE;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(result, notification);
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }
}