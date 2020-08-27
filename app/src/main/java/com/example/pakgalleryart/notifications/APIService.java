package com.example.pakgalleryart.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAfZidaAo:APA91bHGAMapuFYqgw5YydPdhxEYiY-Z7uxBHXbC4hKu-ha9jO3ym4ibHk_slJGdam2fKR0ly0BtG8nY237XDXCBxjflhkbhaQvIGIeJwJW72cPo5mal9LCTLZGeCx02FcLS3ptUS470"
    })
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
