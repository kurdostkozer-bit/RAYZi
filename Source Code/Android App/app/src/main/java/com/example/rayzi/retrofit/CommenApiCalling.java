package com.example.rayzi.retrofit;

import android.content.Context;

import com.example.rayzi.SessionManager;
import com.example.rayzi.modelclass.RestResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommenApiCalling {
    SessionManager sessionManager;
    private Context context;

    public CommenApiCalling(Context context) {

        this.context = context;
        sessionManager = new SessionManager(context);
    }

    public void toggleLikePost(String postId, OnToggleLikeListner onToggleLikeListner) {
        Call<RestResponse> call = RetrofitBuilder.create().toggleLikePost(sessionManager.getUser().getId(), postId);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {

                    if (response.body() != null) {
                        onToggleLikeListner.onToggleLiked(response.body().isLiked());
                    }

                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {

            }
        });
    }

    public void toggleLikeRelite(String reliteId, OnToggleLikeListner onToggleLikeListner) {
        Call<RestResponse> call = RetrofitBuilder.create().toggleLikeReel(sessionManager.getUser().getId(), reliteId);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {

                    if (response.body() != null) {
                        onToggleLikeListner.onToggleLiked(response.body().isLiked());
                    }

                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {

            }
        });
    }

    public interface OnToggleLikeListner {
        void onToggleLiked(boolean isLiked);
    }
}
