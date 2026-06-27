package com.example.rayzi.user.wallet;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.rayzi.MyLoader;
import com.example.rayzi.R;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.databinding.ActivityHistoryBinding;
import com.example.rayzi.modelclass.CustomDate;
import com.example.rayzi.modelclass.HistoryListRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.google.gson.Gson;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends BaseActivity {
    ActivityHistoryBinding binding;
    CoinHistoryAdapter coinHistoryAdapter = new CoinHistoryAdapter();
    private CustomDate startDate;
    private CustomDate endDate;
    private String coinType = "";
    private int start = 0;
    MyLoader myLoader = new MyLoader();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history);
        binding.setMyLoder(myLoader);

        Intent intent = getIntent();
        coinType = intent.getStringExtra(Const.TYPE);
        if (Objects.requireNonNull(coinType).equals(Const.RCOINS)) {
            binding.lytTop.setBackground(ContextCompat.getDrawable(this, R.color.purple));
            binding.tvTitle.setText(Const.CoinName +  getString(R.string.record));
        }

        binding.rvHistory.setAdapter(coinHistoryAdapter);
        coinHistoryAdapter.setCoinType(coinType);

        startDate = new Gson().fromJson(intent.getStringExtra(Const.STARTDATE), CustomDate.class);
        endDate = new Gson().fromJson(intent.getStringExtra(Const.ENDDATE), CustomDate.class);
        binding.tvDate1.setText(startDate.getDateForHuman());
        binding.tvDate2.setText(endDate.getDateForHuman());
        getRecordData(false);

        binding.lytDate1.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryActivity.this);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
            datePickerDialog.setOnDateSetListener((datePicker, i, i1, i2) -> {
                startDate = new CustomDate(i2, i1 + 1, i);
                binding.tvDate1.setText(startDate.getDateForHuman());
                getRecordData(false);
            });
        });

        binding.lytDate2.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryActivity.this);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(android.widget.DatePicker datePicker, int i, int i1, int i2) {
                    endDate = new CustomDate(i2, i1 + 1, i);
                    binding.tvDate2.setText(endDate.getDateForHuman());
                    getRecordData(false);
                }
            });
        });

        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

    }

    private void getRecordData(boolean isLoadMore) {
        if (startDate != null && endDate != null) {
            myLoader.noData.set(false);
            if (isLoadMore) {
                start = start + Const.LIMIT;
            } else {
                start = 0;
                coinHistoryAdapter.clear();
                myLoader.isFristTimeLoading.set(true);
            }

            Call<HistoryListRoot> call = RetrofitBuilder.create().getCoinHostory(sessionManager.getUser().getId(), startDate.getDateForServer(), endDate.getDateForServer(), coinType, start, Const.LIMIT);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<HistoryListRoot> call, Response<HistoryListRoot> response) {
                    if (response.code() == 200) {
                        if (response.body().isStatus() && !response.body().getHistory().isEmpty()) {
                            coinHistoryAdapter.addData(response.body().getHistory());
                            binding.tvIncome.setText(String.valueOf(response.body().getIncomeTotal()));
                            binding.tvOutcome.setText(String.valueOf(response.body().getOutgoingTotal()));
                        } else if (start == 0) {
                            myLoader.noData.set(true);
                        }
                    }
                    myLoader.isFristTimeLoading.set(false);
                    binding.swipeRefresh.finishRefresh();
                    binding.swipeRefresh.finishLoadMore();
                }

                @Override
                public void onFailure(Call<HistoryListRoot> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}