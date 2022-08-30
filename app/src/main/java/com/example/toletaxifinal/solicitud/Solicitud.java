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

import androidx.appcompat.app.AppCompatActivity;

import com.example.toletaxifinal.R;
import com.example.toletaxifinal.retrofit.DecodePoints;
import com.example.toletaxifinal.retrofit.GoogleApiProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Solicitud extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    //Recoger del intent
    private double mExtraOrigenLat;
    private double mExtraOrigenLng;
    private double mExtraDestinoLat;
    private double mExtraDestinoLng;
    private String mExtraOrigen;
    private String mExtraDestino;

    private LatLng mOrigenLatLng;
    private LatLng mDestinoLatLng;
        //Trazar lineas en el mapa
    private GoogleApiProvider mGoogleApiProvider;
    private List<LatLng> mPolylineList;
    private PolylineOptions mPolylineOptions;

    //Vistas maps
    private TextView mTxtVOrigen;
    private TextView mTxtVDestino;
    private TextView mTxtVTiempo;
    private TextView mTxtVDistancia;



    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //color ventana
        this.window=getWindow();
        window.setTitle("Tus datos");
        window.setStatusBarColor(Color.parseColor("#000000"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1F74B8")));
        window.setNavigationBarColor(Color.parseColor("#000000"));



        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        //recogemos origen destino de la actividad anterior
        mExtraOrigenLat=getIntent().getDoubleExtra("origen_lat",0);
        mExtraOrigenLng=getIntent().getDoubleExtra("origen_lng",0);
        mExtraDestinoLat=getIntent().getDoubleExtra("destino_lat",0);
        mExtraDestinoLng=getIntent().getDoubleExtra("destino_lng",0);
        mExtraOrigen=getIntent().getStringExtra("m_origen");
        mExtraDestino=getIntent().getStringExtra("m_destino");

        mOrigenLatLng= new LatLng(mExtraOrigenLat, mExtraOrigenLng);
        mDestinoLatLng= new LatLng(mExtraDestinoLat, mExtraDestinoLng);

        mGoogleApiProvider=new GoogleApiProvider(Solicitud.this);

        mTxtVOrigen=findViewById(R.id.txtVOrigen);
        mTxtVDestino=findViewById(R.id.txtVDestino);
        mTxtVDistancia=findViewById(R.id.txtVDistancia);
        mTxtVTiempo=findViewById(R.id.txtVTiempo);

        mTxtVOrigen.setText(" "+ mExtraOrigen);
        mTxtVDestino.setText(" "+mExtraDestino);

    }

    private void drawRuta(){
        mGoogleApiProvider.getDirections(mOrigenLatLng,mDestinoLatLng).enqueue(new Callback<String>() {
            @Override//Recibiendo respuesta del servidor
            public void onResponse(Call<String> call, Response<String> response) {
                try{
                    JSONObject jsonObject=new JSONObject(response.body());
                    JSONArray jsonArray=jsonObject.getJSONArray("routes");
                    JSONObject ruta = jsonArray.getJSONObject(0);
                    JSONObject polylines= ruta.getJSONObject("overview_polyline");
                    String points= polylines.getString("points");

                    mPolylineList= DecodePoints.decodePoly(points);
                    mPolylineOptions=new PolylineOptions();
                    mPolylineOptions.color(Color.BLUE);
                    mPolylineOptions.width(13f);
                    mPolylineOptions.startCap(new SquareCap());
                    mPolylineOptions.jointType(JointType.ROUND);
                    mPolylineOptions.addAll(mPolylineList);
                    mMap.addPolyline(mPolylineOptions);
                    //Obtencionde distancia y tiempo
                    JSONArray legs=ruta.getJSONArray("legs");
                    JSONObject leg=legs.getJSONObject(0);

                    JSONObject distancia=leg.getJSONObject("distance");
                    JSONObject duracion=leg.getJSONObject("duration");

                    String duracionTexto=duracion.getString("text");
                    String distanciaTexto=distancia.getString("text");
                    mTxtVTiempo.setText(duracionTexto);
                    mTxtVDistancia.setText(distanciaTexto);


                }catch (Exception e){
                    Log.d("Error","Error encontrado"+e.getMessage());
                }
            }

            @Override//En caso de que falle peticion
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(Solicitud.this, "HGa fallado la peticion", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //iconos origen destino
        mMap.addMarker(new MarkerOptions().position(mOrigenLatLng).title("Origen").icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_mapa_de_pin_100)));
        mMap.addMarker(new MarkerOptions().position(mDestinoLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_mapa_de_pin_azul)));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(mOrigenLatLng).zoom(14f).build()));
        drawRuta();


    }

    public void btnAceptarSolicitud(View view) {
        Intent intent=new Intent(Solicitud.this,BuscarConductor.class);
        intent.putExtra("origen_lat",mOrigenLatLng.latitude);
        intent.putExtra("origen_lng",mOrigenLatLng.longitude);
        intent.putExtra("origen",mExtraOrigen);
        intent.putExtra("destino",mExtraDestino);
        intent.putExtra("destino_lat",mDestinoLatLng.latitude);
        intent.putExtra("destino_lng",mDestinoLatLng.longitude);
        startActivity(intent);
        finish();
    }
}
