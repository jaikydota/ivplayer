package com.ctrlvideo.comment.net;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpClient {


    private static HttpClient client;
    private OkHttpClient okHttpClient;


    private HttpClient() {
        okHttpClient = new OkHttpClient();
    }

    public static HttpClient getInstanse() {
        if (client == null) {
            client = new HttpClient();
        }
        return client;
    }

    public void getIVideoInfo(GetIVideoInfoCallback callback) {


        Request request = new Request.Builder()
                .get()
                .url("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5165902802815866")
//                .url("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/")
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(callback);

    }


}
