package com.kotov.smartnotes.utils.alarm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.kotov.smartnotes.R;
import com.kotov.smartnotes.activity.editor.Notes;

import java.util.Objects;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationUtils {


    public static void notification(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, Notes.class);
        notificationIntent.putExtra("id", intent.getStringExtra("id"));
        notificationIntent.putExtra("close", true);
        notificationIntent.putExtra("close_code", intent.getIntExtra("close_id", -1));
        @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(context,
                intent.getIntExtra("close_id", -1), notificationIntent,
                Intent.FLAG_ACTIVITY_NEW_TASK);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, Objects.requireNonNull(intent.getStringExtra("id")))
                        .setSmallIcon(R.drawable.ic_delete)
                        .setContentTitle("Remind you")
                        .setContentText(intent.getStringExtra("title"))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.edt_bg_selector)) // большая картинка
                        .addAction(R.drawable.ic_done, "Запустить активность",
                                pendingIntent)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(intent.getStringExtra("title")))
                        .setOngoing(true)
                        .setAutoCancel(true); // автоматически закрыть уведомление после нажатия


        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_SOUND |
                Notification.DEFAULT_VIBRATE;
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.notify(intent.getIntExtra("close_id", -1), notification);
    }
}
