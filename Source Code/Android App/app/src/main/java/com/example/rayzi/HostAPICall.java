package com.example.rayzi;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.rayzi.audioLive.HostLiveAudioActivity;
import com.example.rayzi.modelclass.RestResponse;
import com.example.rayzi.retrofit.RetrofitBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HostAPICall {

    private Handler apiHandler = new Handler(Looper.getMainLooper());
    private Runnable apiRunnable;
    SessionManager sessionManager;
    Context context;
    String type;

    public HostAPICall(Context context,String type) {
        this.context = context;
        this.type = type;
        sessionManager = new SessionManager(context);
    }

    public void startApiCallLoop() {
        apiRunnable = new Runnable() {
            @Override
            public void run() {

                callApi();
                apiHandler.postDelayed(this, 60 * 1000);
            }
        };

        apiHandler.postDelayed(apiRunnable, 60 * 1000);
    }

    public void callApi() {
        String date = getIndianDate();
        Log.d("TAG", "callApi: ===date" + date);
        if (sessionManager.getUser() != null) {
            Call<RestResponse> call = RetrofitBuilder.create().getHostApi(sessionManager.getUser().getId(), type, date);
            call.enqueue(new Callback<RestResponse>() {
                @Override
                public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {

                }

                @Override
                public void onFailure(Call<RestResponse> call, Throwable t) {

                }
            });
        }

    }

    private String getIndianDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        return dateFormat.format(new Date());
    }

    public void stopApiCallLoop() {
        if (apiHandler != null) {
            Log.d("fatal", "stopApiCallLoop: ");
            apiHandler.removeCallbacksAndMessages(null);
        }
    }

}
