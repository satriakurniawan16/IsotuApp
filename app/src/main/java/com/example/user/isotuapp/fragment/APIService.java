package com.example.user.isotuapp.fragment;

import com.example.user.isotuapp.Notification.MyResponse;
import com.example.user.isotuapp.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAlGYscqM:APA91bGF05vZr2vbfNQMDr02wt5bHtbXyBf4YScZHpjrLD0Uc0Axkv3wD5eF3oQyQRpVJboTghlge7UJzLRMOptonqpZugxvyzN9J0wh5vMzc3dipQk7Vk4Kxk-_vgqKgAOtFhM0K48o"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}