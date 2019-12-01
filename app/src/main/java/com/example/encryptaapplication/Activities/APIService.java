package com.example.encryptaapplication.Activities;

import com.example.encryptaapplication.Notifications.MyResponse;
import com.example.encryptaapplication.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAS7zS9nI:APA91bG3fWxGD8z9v_KoW3KLYd3aRsTMQCNxBH2c56CRuo1duKr0VRTDTvuEAcC8vu3Iu0f5di9hOXEjMFxHhkqZV3eKZTUezCfdjoZyzm3wR84BQMpcLOYG2KEgx3Xyp-8_aOCqILH6"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}