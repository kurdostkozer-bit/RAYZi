package com.example.rayzi.user;

import android.os.Bundle;
import android.view.View;

import com.example.rayzi.MyLoader;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.databinding.ActivityMyRecords2Binding;
import com.example.rayzi.modelclass.MyRecordTopDataRoot;
import com.example.rayzi.modelclass.RecordRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRecordsActivity extends BaseActivity {

    MyLoader myLoader = new MyLoader();
    private ActivityMyRecords2Binding binding;
    public static String LIVESTREAM = "liveStreaming";
    public static String AUDIO = "audio";
    MyRecordAdapter myRecordAdapter = new MyRecordAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyRecords2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.setLoader(myLoader);

        binding.rvTikit.setAdapter(myRecordAdapter);
        getTopData();
        getData();
        initListner();


    }

    private void getTopData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sessionManager.getUser().getId());

        Call<MyRecordTopDataRoot> myRecordTopDataRootCall = RetrofitBuilder.create().getMyRecordTopData(jsonObject);
        myRecordTopDataRootCall.enqueue(new Callback<MyRecordTopDataRoot>() {
            @Override
            public void onResponse(Call<MyRecordTopDataRoot> call, Response<MyRecordTopDataRoot> response) {
                if (response.code()==200 && response.isSuccessful() && response.body()!=null) {
                    binding.tvTodayLive.setText(String.valueOf(response.body().getTodayLiveStreaming()));
                    binding.tvThisWeekLive.setText(String.valueOf(response.body().getWeekLiveStreaming()));
                    binding.tvTodayAudio.setText(String.valueOf(response.body().getTodayAudio()));
                    binding.tvThisWeekAudio.setText(String.valueOf(response.body().getWeekAudio()));
                }
            }

            @Override
            public void onFailure(Call<MyRecordTopDataRoot> call, Throwable t) {

            }
        });
    }

    private void initListner() {
        binding.swipe.setOnRefreshListener(refreshLayout -> getData());


    }

    private void getData() {

        binding.noData.setVisibility(View.GONE);
        myLoader.isFristTimeLoading.set(true);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sessionManager.getUser().getId());
        jsonObject.addProperty("start", 0);
        jsonObject.addProperty("limit", Const.LIMIT);
        // jsonObject.addProperty("startDate",);
        //  jsonObject.addProperty("endDate",);
        jsonObject.addProperty("type", "liveStreaming");


        Call<RecordRoot> call = RetrofitBuilder.create().getMyRecord(jsonObject);
        call.enqueue(new Callback<RecordRoot>() {
            @Override
            public void onResponse(Call<RecordRoot> call, Response<RecordRoot> response) {
                if (response.code() == 200 && response.body().getStatus() && !response.body().getHistory().isEmpty()) {
                    myRecordAdapter.addData(response.body().getHistory());
                } else {
                    binding.noData.setVisibility(View.VISIBLE);
                }

                myLoader.isFristTimeLoading.set(false);
                binding.swipe.finishRefresh();
            }

            @Override
            public void onFailure(Call<RecordRoot> call, Throwable t) {

            }
        });
    }
}