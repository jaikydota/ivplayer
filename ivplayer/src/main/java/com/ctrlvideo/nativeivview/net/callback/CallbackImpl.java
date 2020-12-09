package com.ctrlvideo.nativeivview.net.callback;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class CallbackImpl implements Callback {

    @Override
    public void onFailure(Call call, IOException e) {

        onFailure(e.getMessage());
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

        ResponseBody responseBody = response.body();
        if (responseBody == null) {

            onFailure("empty responseBody");
            return;
        }

        String responseString = responseBody.string();
        JsonObject jsonObject = new JsonParser().parse(responseString).getAsJsonObject();
        String statue = jsonObject.get("status").getAsString();

        JsonElement jsonElement = jsonObject.get("result");

        if ("fail".equals(statue)) {

            onFailure(jsonElement.getAsString());
            return;
        }
        converData(jsonElement.toString());

    }

    protected abstract void onFailure(String error) ;

    protected abstract void converData(String data) ;
}
