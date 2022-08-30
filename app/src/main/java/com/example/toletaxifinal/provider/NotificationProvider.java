package com.example.toletaxifinal.provider;



import com.example.toletaxifinal.cloudmesagging.FCMBody;
import com.example.toletaxifinal.cloudmesagging.FCMResponse;
import com.example.toletaxifinal.cloudmesagging.IFCMApi;
import com.example.toletaxifinal.retrofit.RetrofitCliente;

import retrofit2.Call;

public class NotificationProvider {
    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {
    }

    public Call<FCMResponse> sendNotification(FCMBody body) {
        return RetrofitCliente.getClientObject(url).create(IFCMApi.class).send(body);
    }
}
