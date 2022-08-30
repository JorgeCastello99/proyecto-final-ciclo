package com.example.toletaxifinal.solicitud;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.toletaxifinal.R;
import com.example.toletaxifinal.central.CentralUsuario;
import com.example.toletaxifinal.cloudmesagging.FCMBody;
import com.example.toletaxifinal.cloudmesagging.FCMResponse;
import com.example.toletaxifinal.modelos.ClientBooking;
import com.example.toletaxifinal.postsolicitud.ViajeCliente;
import com.example.toletaxifinal.provider.ClientBookingProvider;
import com.example.toletaxifinal.provider.GeofireProvider;
import com.example.toletaxifinal.provider.NotificationProvider;
import com.example.toletaxifinal.provider.TokenProvider;
import com.example.toletaxifinal.retrofit.GoogleApiProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuscarConductor extends AppCompatActivity {
    private Window window;
    private GeofireProvider mGeofireProvider;

    private double mExtraOrigenLat;
    private double mExtraOrigenLng;
    private LatLng mOrigenLatLng;
    private double mExtraDestinoLat;
    private double mExtraDestinoLng;
    private LatLng mDestinoLatLng;
    private String mExtraOrigen;
    private String mExtraDestino;


    private double mRadius=0;

    private boolean mWorkerEncontrado=false;
    private String mIdWorkerEncontrado="";

    private LatLng mWorkerEncontradoLatLng;

    private TextView buscando;

    private ClientBookingProvider mClientBookingProvider;

    private NotificationProvider mNotificationProvider;
    private TokenProvider mTokenProvider;

    private FirebaseAuth mAuth;
    private String id;
    private ValueEventListener mListener;//para controlar la escucha y se finalice

    private GoogleApiProvider mGoogleApiProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_conductor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //color ventana
        this.window=getWindow();
        window.setTitle("Tus datos");
        window.setStatusBarColor(Color.parseColor("#000000"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1F74B8")));
        window.setNavigationBarColor(Color.parseColor("#000000"));

        buscando=findViewById(R.id.txtVBuscando);

        mExtraOrigen=getIntent().getStringExtra("origen");
        mExtraDestino=getIntent().getStringExtra("destino");
        mExtraOrigenLat=getIntent().getDoubleExtra("origen_lat",0);
        mExtraOrigenLng=getIntent().getDoubleExtra("origen_lng",0);
        mExtraDestinoLat=getIntent().getDoubleExtra("destino_lat",0);
        mExtraDestinoLng=getIntent().getDoubleExtra("destino_lng",0);

        mOrigenLatLng=new LatLng(mExtraOrigenLat,mExtraOrigenLng);
        mDestinoLatLng=new LatLng(mExtraDestinoLat,mExtraDestinoLng);


        mGeofireProvider=new GeofireProvider("ubicacion_trabajadores");
        mTokenProvider=new TokenProvider();
        mNotificationProvider=new NotificationProvider();
        mClientBookingProvider=new ClientBookingProvider();
        mAuth = FirebaseAuth.getInstance();
        mGoogleApiProvider=new GoogleApiProvider(BuscarConductor.this);

        id=mAuth.getCurrentUser().getUid();
        Log.e("Ha cogido el id",id);
        obtenerConductorCercano();
    }

    public void btnCancelarViaje(View view) {
        onBackPressed();
    }

    public void obtenerConductorCercano(){
        mGeofireProvider.getActiveDrivers(mOrigenLatLng,mRadius).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.e("Idworker ",id);
                if (!mWorkerEncontrado){
                    mWorkerEncontrado=true;
                    mIdWorkerEncontrado=key;//Encontrar id del conductor mas cercano
                    mWorkerEncontradoLatLng=new LatLng(location.latitude,location.longitude);
                    buscando.setText("ENCONTRADO, ESPERANDO RESPUESTA");
                    Log.e("Idworker ",mIdWorkerEncontrado);
                    crearClienteBooking();


                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //Entra cuando termina la busqueda del conductor en el radio de 0.1 km
                if (mWorkerEncontrado==false){
                    mRadius=mRadius+0.1f;
                    //No encuentra conductor
                    if(mRadius > 20){
                        Toast.makeText(BuscarConductor.this, "No se encontró un conductor", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        //seguimos buscando conductor
                       obtenerConductorCercano();
                    }
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    public void crearClienteBooking(){
        mGoogleApiProvider.getDirections(mOrigenLatLng,mWorkerEncontradoLatLng).enqueue(new Callback<String>() {
            @Override//Recibiendo respuesta del servidor
            public void onResponse(Call<String> call, Response<String> response) {
                try{
                    Log.e("ClienteBooking",mIdWorkerEncontrado);
                    JSONObject jsonObject=new JSONObject(response.body());
                    JSONArray jsonArray=jsonObject.getJSONArray("routes");
                    JSONObject ruta = jsonArray.getJSONObject(0);
                    JSONObject polylines= ruta.getJSONObject("overview_polyline");
                    String points= polylines.getString("points");
                    //Obtencionde distancia y tiempo
                    JSONArray legs=ruta.getJSONArray("legs");
                    JSONObject leg=legs.getJSONObject(0);

                    JSONObject distancia=leg.getJSONObject("distance");
                    JSONObject duracion=leg.getJSONObject("duration");

                    String duracionTexto=duracion.getString("text");
                    String distanciaTexto=distancia.getString("text");

                    enviarNotificacion(duracionTexto,distanciaTexto);


                }catch (Exception e){
                    Log.d("Error","Error encontrado"+e.getMessage());
                }
            }

            @Override//En caso de que falle peticion
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(BuscarConductor.this, "Ha fallado la peticion", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void enviarNotificacion(final String time,final String km){
        //metodo que nos permite obtener el token de firebase apuntando al id del conductor
        mTokenProvider.getToken(mIdWorkerEncontrado).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Obtenemos token del usuario
                if (dataSnapshot.exists()){

                    String token=dataSnapshot.child("token").getValue().toString();
                    Map<String,String> map= new HashMap<>();
                    map.put("title", "SOLICITUD DE SERVICIO A "+time+" DE TU POSICION");
                    map.put("body",
                            "Un cliente esta solicitando un servicio a una distancia de "+km+"\n"+
                                    "Recoger en: " + mExtraOrigen + "\n" +
                                    "Destino: " + mExtraDestino
                    );

                    map.put("idClient", id);
                    FCMBody fcmBody = new FCMBody(token, "high",  map); //utilizamos el provider de notificacion para introducirle el cuerpo(body)
                    mNotificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                            if (response.body()!=null){
                                if (response.body().getSuccess()==1){
                                    ClientBooking clientBooking=new ClientBooking(id,mIdWorkerEncontrado,mExtraDestino,mExtraOrigen
                                            ,time,km,"create",mExtraOrigenLat,mExtraOrigenLng,mExtraDestinoLat,mExtraDestinoLng);

                                    mClientBookingProvider.create(clientBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                           checkStatusClientBooking();
                                        }
                                    });
                                    //Toast.makeText(BuscarConductor.this, "Su notificacion se ha enviado correctamente", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(BuscarConductor.this, "Su notificacion se ha enviado correctamente", Toast.LENGTH_SHORT).show();

                                }
                            }
                            else{

                            }
                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {

                        }
                    });
                }
                else {
                    Toast.makeText(BuscarConductor.this, "No se pudo enviar la notificacion porque el conductor no tiene token de sesion", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Escuchador para conocer en tiempo real el status del conductor, si ha cancelado o aceptado
    private void checkStatusClientBooking() {
        mListener = mClientBookingProvider.getStatus(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String status = dataSnapshot.getValue().toString();
                    if (status.equals("accept")) {
                        Intent intent = new Intent(BuscarConductor.this, ViajeCliente.class);
                        startActivity(intent);
                        finish();
                    } else if (status.equals("cancel")) {
                        Toast.makeText(BuscarConductor.this, "El conductor no acepto el viaje", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(BuscarConductor.this, CentralUsuario.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener!=null){
            mClientBookingProvider.getStatus(id).removeEventListener(mListener);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}
