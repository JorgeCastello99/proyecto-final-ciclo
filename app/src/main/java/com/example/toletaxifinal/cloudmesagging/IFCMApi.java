package com.example.toletaxifinal.cloudmesagging;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAlfnKCHQ:APA91bGr5mCRW_N9iSV31Sgza3R1r9p4ycuDfnxOmzGf-B7mWdyq2dKl59B6BvjpnNf9DnZqXS_VXYdkTYotPaBUiK2UP6uenyd6caIZN2OKRYYN3hXrt-sq7gB7tkHSEcblCxsO963j"
    })

    //ruta que permitirá enviar notificaciones
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);


}
