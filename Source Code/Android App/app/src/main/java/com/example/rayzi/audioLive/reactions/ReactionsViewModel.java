package com.example.rayzi.audioLive.reactions;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rayzi.modelclass.ReactionRoot;
import com.example.rayzi.retrofit.RetrofitBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReactionsViewModel extends ViewModel {
    public MutableLiveData<List<ReactionRoot.DataItem>> reactionsMutableLiveData = new MutableLiveData<>();


    public void loadReactions(OnLoadComplete onLoadComplete) {

        RetrofitBuilder.create().getReactions().enqueue(new Callback<ReactionRoot>() {
            @Override
            public void onResponse(Call<ReactionRoot> call, Response<ReactionRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getData().isEmpty()) {
                        reactionsMutableLiveData.setValue(response.body().getData());
                        onLoadComplete.onLoadComplete(response.body().getData());
                    }
                }
            }

            @Override
            public void onFailure(Call<ReactionRoot> call, Throwable t) {

            }
        });
    }

    public interface OnLoadComplete {
        void onLoadComplete(List<ReactionRoot.DataItem> data);
    }
}
