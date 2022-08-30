package com.example.toletaxifinal.central;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.toletaxifinal.MainActivity;
import com.example.toletaxifinal.R;
import com.example.toletaxifinal.provider.GeofireProvider;
import com.example.toletaxifinal.provider.TokenProvider;
import com.example.toletaxifinal.solicitud.Solicitud;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CentralUsuario extends AppCompatActivity implements OnMapReadyCallback {


    private FirebaseAuth mAuth;
    private GoogleMap mMap;


    private DatabaseReference mDataBase;
    private int MY_PERMISSIONS_REQUEST_MAP;
    private String id;

    //Places recogida
    private PlacesClient mPlaces;
    private AutocompleteSupportFragment mAutocomplete;
    private String mOrigen;
    private LatLng mOrigenLatLong;
    //places destino
    private AutocompleteSupportFragment mAutocompleteDestino;
    private String mDestino;
    private LatLng mDestinoLatLong;


    private GeofireProvider mGeofireProvider;
    private LatLng miPosicion;


    private Window window;

    //Token
    private TokenProvider mTokenProvider;

    //Obtener ubicacion
    private List<Marker> mWorkersMarkers = new ArrayList<>();
    private SupportMapFragment mMapFragment;
    private Location location;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;
    private boolean mPrimeravez = true;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    //Marcador

                    miPosicion = new LatLng(location.getLatitude(), location.getLongitude());
                    // OBTENER LA LOCALIZACION DEL USUARIO EN TIEMPO REAL
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(15f)
                                    .build()
                    ));


                    if (mPrimeravez == true) {
                        mPrimeravez = false;
                        getActiveWorkers();
                        limitarBusqueda();
                    }


                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central_usuario);


        //color ventana
        this.window = getWindow();
        window.setStatusBarColor(Color.parseColor("#000000"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1F74B8")));
        window.setTitle("TOLETAXI");
        window.setNavigationBarColor(Color.parseColor("#000000"));

        //Meto el maps
        setContentView(R.layout.activity_maps);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);


        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference();
        mGeofireProvider = new GeofireProvider("ubicacion_trabajadores");
        mTokenProvider = new TokenProvider();
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        id = mAuth.getCurrentUser().getUid();


        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }
        mPlaces = Places.createClient(this);
        // Places origen
        setAutocompleteOrigen();
        //Destino
        setAutocompleteDestino();
        //Token Cloud Messaging
        generarToken();

    }


    //onClick boton solicitud
    public void onClickSolicitarViaje(View view) {
        validarSelecciones();
    }

    //Validar si usuario shizo las selecciones
    public void validarSelecciones() {
        if (mOrigenLatLong != null && mDestinoLatLong != null) {
            Intent intent = new Intent(CentralUsuario.this, Solicitud.class);
            intent.putExtra("origen_lat", mOrigenLatLong.latitude);
            intent.putExtra("origen_lng", mOrigenLatLong.longitude);
            intent.putExtra("destino_lat", mDestinoLatLong.latitude);
            intent.putExtra("destino_lng", mDestinoLatLong.longitude);
            intent.putExtra("m_origen", mOrigen);
            intent.putExtra("m_destino", mDestino);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Debe seleccionar el lugar de recogida y el destino", Toast.LENGTH_SHORT).show();
        }
    }

    //Funcionamiento GPS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActived()) {
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    } else {
                        showAlertDialogNOGPS();
                    }
                } else {
                    checkLocationPermissions();
                }
            } else {
                checkLocationPermissions();
            }
        }
    }


    //Funcionamiento GPS
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActived()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

        } else if (requestCode == SETTINGS_REQUEST_CODE && !gpsActived()) {
            showAlertDialogNOGPS();
        }
    }

    private void showAlertDialogNOGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Por favor activa tu ubicacion para continuar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }

    //Funcionamiento GPS
    private boolean gpsActived() {
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isActive = true;
        }
        return isActive;
    }

    //Funcionamiento GPS
    private void startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsActived()) {
                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                } else {
                    showAlertDialogNOGPS();
                }
            } else {
                checkLocationPermissions();
            }
        } else {
            if (gpsActived()) {
                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            } else {
                showAlertDialogNOGPS();
            }
        }
    }

    //Funcionamiento GPS
    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicacion requiere de los permisos de ubicacion para poder utilizarse")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(CentralUsuario.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(CentralUsuario.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);
        startLocation();
    }

    //Limitar busqueda de Edittext
    private void limitarBusqueda(){
        LatLng northSide= SphericalUtil.computeOffset(miPosicion,15000,0);
        LatLng southSide= SphericalUtil.computeOffset(miPosicion,15000,0);
        mAutocomplete.setCountry("ESP");
        mAutocomplete.setLocationBias(RectangularBounds.newInstance(southSide,northSide));
        mAutocompleteDestino.setCountry("ESP");

        mAutocompleteDestino.setLocationBias(RectangularBounds.newInstance(southSide,northSide));

    }
    //Autocompletar edittext destino
    private void setAutocompleteDestino(){
        mAutocompleteDestino=(AutocompleteSupportFragment)getSupportFragmentManager().findFragmentById(R.id.placeAutocompleteDestino);
        mAutocompleteDestino.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutocompleteDestino.setHint("Destino");
        mAutocompleteDestino.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mDestino=place.getName();
                mDestinoLatLong=place.getLatLng();
                Log.d("PLACE","Name:"+mOrigenLatLong.latitude);
                Log.d("PLACE","Lat:"+mOrigenLatLong.latitude);
                Log.d("PLACE","Lng:"+mOrigenLatLong.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

    }

    //Autocompletar edittext origen
    private void setAutocompleteOrigen(){

        mAutocomplete=(AutocompleteSupportFragment)getSupportFragmentManager().findFragmentById(R.id.placeAutocomplete);
        mAutocomplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutocomplete.setHint("Lugar de recogida");
        mAutocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mOrigen=place.getName();
                mOrigenLatLong=place.getLatLng();
                Log.d("PLACE","Name:"+mOrigenLatLong.latitude);
                Log.d("PLACE","Lat:"+mOrigenLatLong.latitude);
                Log.d("PLACE","Lng:"+mOrigenLatLong.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

    }
    //obtenertrabajadores activos
    private void getActiveWorkers(){
        mGeofireProvider.getActiveDrivers(miPosicion,20).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override//añadiendo marcadores cuando se conectan
            public void onKeyEntered(String key, GeoLocation location) {
                for(Marker marker: mWorkersMarkers){
                    if (marker.getTag() != null) {

                        if (marker.getTag().equals(key)){
                            return;
                        }
                    }
                }
                LatLng workerLatlng=new LatLng(location.latitude,location.longitude);
                Marker marker=mMap.addMarker(new MarkerOptions().position(workerLatlng).title("Conductor disponible").icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon)));
                marker.setTag(key);
                mWorkersMarkers.add(marker);
            }

            @Override//eliminar marcadores de conductores que se vayan desconectando
            public void onKeyExited(String key) {
                for(Marker marker: mWorkersMarkers){
                    if (marker.getTag() != null) {

                        if (marker.getTag().equals(key)){
                            marker.remove();
                            mWorkersMarkers.remove(marker);
                            return;
                        }
                    }
                }
            }

            @Override//Actualizando en tiempo real posicion de conductor
            public void onKeyMoved(String key, GeoLocation location) {
                for(Marker marker: mWorkersMarkers){
                    if (marker.getTag() != null) {

                        if (marker.getTag().equals(key)){
                            marker.setPosition(new LatLng(location.latitude,location.longitude));
                        }
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }


    //Token
    void generarToken(){
        mTokenProvider.crear(id);
    }

    //Menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_overflow, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.itemconfig) {

        }
        if (id == R.id.itemcierre) {

            mAuth.signOut();

            startActivity(new Intent(CentralUsuario.this, MainActivity.class));
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==event.KEYCODE_BACK){
            Intent intent=new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }

        return false;
    }



    /////////////////////////




}
