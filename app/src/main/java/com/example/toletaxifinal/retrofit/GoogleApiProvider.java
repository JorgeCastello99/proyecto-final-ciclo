package com.example.toletaxifinal.retrofit;

import android.content.Context;


import com.example.toletaxifinal.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import retrofit2.Call;

public class GoogleApiProvider {
    private Context context;
    public GoogleApiProvider(Context context){
        this.context=context;
    }
    //Hacer peticion al servicio de google
    public Call<String> getDirections(LatLng origenLatLng, LatLng destinoLatLng){
        String baseUrl="https://maps.googleapis.com";
        String query = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                + "origin=" + origenLatLng.latitude + "," + origenLatLng.longitude + "&"
                + "destination=" + destinoLatLng.latitude + "," + destinoLatLng.longitude + "&"
                + "departure_time=" + (new Date().getTime() + (60*60*1000)) + "&"
                + "traffic_model=best_guess&"
                + "key=" + context.getResources().getString(R.string.google_maps_key);
        return RetrofitCliente.getClient(baseUrl).create(IGoogleApi.class).getDirections(baseUrl+""+query);
    }
}
