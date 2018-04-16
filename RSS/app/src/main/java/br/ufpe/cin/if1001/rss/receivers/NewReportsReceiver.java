package br.ufpe.cin.if1001.rss.receivers;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import br.ufpe.cin.if1001.rss.ui.MainActivity;

public class NewReportsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext());
        builder
                .setSmallIcon(android.R.drawable.ic_notification_overlay)
                .setContentTitle("RSS Feed App")
                .setContentText("New news");
        Intent openApp = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, openApp, 0);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        NotificationManagerCompat.from(context.getApplicationContext()).notify(0, notification);
    }
}
