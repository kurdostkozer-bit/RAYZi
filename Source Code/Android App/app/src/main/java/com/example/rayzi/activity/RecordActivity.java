package com.example.rayzi.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.databinding.ActivityRecordBinding;
import com.example.rayzi.dilog.CustomDialogClass;
import com.example.rayzi.modelclass.CoinRecordRoot;
import com.example.rayzi.modelclass.CustomDate;
import com.example.rayzi.popups.PopupBuilder;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.user.wallet.HistoryActivity;
import com.example.rayzi.utils.datepicker.utils.DateUtils;
import com.example.rayzi.utils.datepicker.view.datePicker.DatePicker;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordActivity extends BaseActivity {

    ActivityRecordBinding binding;
    CustomDate startDate;
    CustomDate endDate;
    int day;
    int month;
    int year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_record);

        day = Integer.parseInt(getDate(System.currentTimeMillis(), "dd"));
        month = Integer.parseInt(getDate(System.currentTimeMillis(), "MM"));
        year = Integer.parseInt(getDate(System.currentTimeMillis(), "yyyy"));
        startDate = new CustomDate(day, month, year);
        endDate = new CustomDate(day, month, year);
        binding.tvDate1.setText(startDate.getDateForHuman());
        binding.tvDate2.setText(endDate.getDateForHuman());
        getRecordData();

        binding.lytDiamonds.setOnClickListener(v -> startActivity(new Intent(RecordActivity.this, HistoryActivity.class).putExtra(Const.TYPE, Const.DIAMOND).putExtra(Const.STARTDATE, new Gson().toJson(startDate)).putExtra(Const.ENDDATE, new Gson().toJson(endDate))));
        binding.lytRcoins.setOnClickListener(v -> startActivity(new Intent(RecordActivity.this, HistoryActivity.class).putExtra(Const.TYPE, Const.RCOINS).putExtra(Const.STARTDATE, new Gson().toJson(startDate)).putExtra(Const.ENDDATE, new Gson().toJson(endDate))));
        binding.backBtn.setOnClickListener(v -> onBackPressed());

        binding.lytDate1.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(RecordActivity.this);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
            datePickerDialog.setOnDateSetListener((datePicker, i, i1, i2) -> {
                day = i2;
                month = i1;
                year = i;
                startDate = new CustomDate(day, month + 1, year);
                binding.tvDate1.setText(startDate.getDateForHuman());
                getRecordData();
            });
        });

        binding.lytDate2.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(RecordActivity.this);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(android.widget.DatePicker datePicker, int i, int i1, int i2) {
                    day = i2;
                    month = i1;
                    year = i;
                    endDate = new CustomDate(day, month + 1, year);
                    binding.tvDate2.setText(endDate.getDateForHuman());
                    getRecordData();
                }
            });
        });

    }

    private void getRecordData() {
        if (startDate != null && endDate != null) {
            clearTextView();
            binding.shimmer.setVisibility(View.VISIBLE);
            Call<CoinRecordRoot> call = RetrofitBuilder.create().getCoinRecord(sessionManager.getUser().getId(), startDate.getDateForServer(), endDate.getDateForServer());
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<CoinRecordRoot> call, Response<CoinRecordRoot> response) {
                    if (response.code() == 200) {
                        if (response.body().isStatus()) {
                            if (response.body().getDiamond() != null) {
                                CoinRecordRoot.Diamond diamonds = response.body().getDiamond();
                                binding.tvDimondsIncome.setText(String.valueOf(diamonds.getIncome()));
                                binding.tvDiamondsOutcome.setText(String.valueOf(diamonds.getOutgoing()));
                            }
                            if (response.body().getRCoin() != null) {
                                CoinRecordRoot.RCoin rCoins = response.body().getRCoin();
                                binding.tvRcoinIncome.setText(String.valueOf(rCoins.getIncome()));
                                binding.tvRcoinOutcome.setText(String.valueOf(rCoins.getOutgoing()));
                            }
                        }
                    }
                    binding.shimmer.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<CoinRecordRoot> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private void clearTextView() {
        binding.tvRcoinIncome.setText(null);
        binding.tvRcoinOutcome.setText(null);
        binding.tvDimondsIncome.setText(null);
        binding.tvDiamondsOutcome.setText(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}