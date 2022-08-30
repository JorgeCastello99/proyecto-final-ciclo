package com.example.toletaxifinal.cloudmesagging.recibidores;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.example.toletaxifinal.postsolicitud.ViajeTrabajador;
import com.example.toletaxifinal.provider.ClientBookingProvider;
import com.example.toletaxifinal.provider.GeofireProvider;
import com.google.firebase.auth.FirebaseAuth;

public class AcceptReceiver extends BroadcastReceiver {
    private ClientBookingProvider mClientBookingProvider;
    private GeofireProvider mGeofireProvider;
    private FirebaseAuth mAuth;
    private String id;



    @Override
    public void onReceive(Context context, Intent intent) {
        mAuth=  FirebaseAuth.getInstance();
        id=mAuth.getCurrentUser().getUid();
        mGeofireProvider = new GeofireProvider("active_drivers");
        mGeofireProvider.borrarLocation(id);

        String idClient = intent.getExtras().getString("idClient");
        mClientBookingProvider = new ClientBookingProvider();
        mClientBookingProvider.updateStatus(idClient, "accept");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);

        Intent intent1 = new Intent(context, ViajeTrabajador.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setAction(Intent.ACTION_RUN);
        context.startActivity(intent1);

    }


}
