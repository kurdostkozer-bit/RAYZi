package com.example.rayzi.user.wallet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.databinding.CoinselleractivityBinding;
import com.example.rayzi.dilog.CustomDialogClass;
import com.example.rayzi.modelclass.CoinSellerRoot;
import com.example.rayzi.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoinSellerListActivity extends BaseActivity {
    public static final String TAG = "CoinSellerListActivity";
    CoinselleractivityBinding binding;
    private CustomDialogClass customDialogClass;
    private CoinSellerListAdapter coinSellerListAdapter = new CoinSellerListAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.coinselleractivity);

        initMain();
        initListener();

    }

    private void initListener() {
        coinSellerListAdapter.setOnCoinPlanClickListener(coinPlan -> {
            openWhatsApp(coinPlan.getCountryCode(),coinPlan.getMobileNo());
        });

        binding.swipeRefresh.setOnRefreshListener((refreshLayout) -> {
            coinSellerListAdapter.clearData();
            getCoinSellerData();
        });

    }

    private void initMain() {
        customDialogClass = new CustomDialogClass(this, R.style.customStyle);
        customDialogClass.setCancelable(false);
        customDialogClass.show();
        binding.rvRecharge.setAdapter(coinSellerListAdapter);
        getCoinSellerData();
    }

    private void getCoinSellerData() {
        Call<CoinSellerRoot> call = RetrofitBuilder.create().getCoinSellerList();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<CoinSellerRoot> call, Response<CoinSellerRoot> response) {
                if (response.code() == 200 && response.body().isStatus()) {
                    if (response.body().getCoinSeller() != null && !response.body().getCoinSeller().isEmpty()) {
                        coinSellerListAdapter.addData(response.body().getCoinSeller());
                        binding.swipeRefresh.finishRefresh();
                        binding.swipeRefresh.finishLoadMore();
                        binding.tvNodata.setVisibility(View.GONE);
                    }else {
                        binding.tvNodata.setVisibility(View.VISIBLE);
                    }
                }
                customDialogClass.dismiss();
            }

            @Override
            public void onFailure(Call<CoinSellerRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                customDialogClass.dismiss();
            }
        });
    }

    private void openWhatsApp(String countrycode,String smsNumber) {
        Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + countrycode + smsNumber);
        Log.d(TAG, "openWhatsApp: smsnumber " + countrycode + smsNumber);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}
