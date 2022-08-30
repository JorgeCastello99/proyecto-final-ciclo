package com.example.toletaxifinal.canal;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.toletaxifinal.R;


public class NotificacionHelper extends ContextWrapper {
    private static final String CANAL_ID="com.example.damtoletaxi";
    private static final String CANAL_NOMBRE="ToleTaxi";

    private NotificationManager manager;
    public NotificacionHelper(Context base){
        super(base);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            crearCanal();
        }

    }
    @RequiresApi(api=Build.VERSION_CODES.O)
    private void crearCanal(){
        NotificationChannel notificationChannel = new
                NotificationChannel(
                CANAL_ID,
                CANAL_NOMBRE,
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.GRAY);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager(){
        if (manager==null){
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
        //crea notificacion en android oreo y superiores
        @RequiresApi(api = Build.VERSION_CODES.O)
        public Notification.Builder getNotification(String title, String body, PendingIntent intent, Uri soundUri) {
            return new Notification.Builder(getApplicationContext(), CANAL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(soundUri)
                    .setContentIntent(intent)
                    .setSmallIcon(R.drawable.ic_coche_notify)
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(body).setBigContentTitle(title));
        }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotificationActions(String title,
                                                       String body,
                                                       Uri soundUri,
                                                       Notification.Action acceptAction ,
                                                       Notification.Action cancelAction) {
        return new Notification.Builder(getApplicationContext(), CANAL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setSmallIcon(R.drawable.ic_coche_notify)
                .addAction(acceptAction)
                .addAction(cancelAction)
                .setStyle(new Notification.BigTextStyle()
                        .bigText(body).setBigContentTitle(title));
    }

    public NotificationCompat.Builder getNotificationOldAPI(String title, String body, PendingIntent intent, Uri soundUri) {
        return new NotificationCompat.Builder(getApplicationContext(), CANAL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(intent)
                .setSmallIcon(R.drawable.ic_coche_notify)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));
    }

    public NotificationCompat.Builder getNotificationOldAPIActions(
            String title,
            String body,
            Uri soundUri,
            NotificationCompat.Action acceptAction,
            NotificationCompat.Action cancelAction) {
        return new NotificationCompat.Builder(getApplicationContext(), CANAL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setSmallIcon(R.drawable.ic_coche_notify)
                .addAction(acceptAction)
                .addAction(cancelAction)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));
    }
}
