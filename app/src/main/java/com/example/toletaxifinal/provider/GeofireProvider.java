package com.example.toletaxifinal.provider;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeofireProvider {
    private DatabaseReference mDatabase;
    private GeoFire mGeofire;

    public GeofireProvider(String reference){
        mDatabase= FirebaseDatabase.getInstance().getReference().child(reference);
        mGeofire=new GeoFire(mDatabase);
    }
    public void guardarLocation(String idDriver, LatLng latLng){
        mGeofire.setLocation(idDriver,new GeoLocation(latLng.latitude,latLng.longitude));
    }

    public void borrarLocation(String idDriver){
        mGeofire.removeLocation(idDriver);
    }

    public GeoQuery getActiveDrivers(LatLng latLng, double radius){
        GeoQuery geoQuery=mGeofire.queryAtLocation(new GeoLocation(latLng.latitude,latLng.longitude),radius);
        geoQuery.removeAllListeners();
        return geoQuery;

    }


}
