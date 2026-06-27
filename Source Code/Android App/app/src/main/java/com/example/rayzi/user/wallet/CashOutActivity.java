package com.example.rayzi.user.wallet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.databinding.ActivityCashOutBinding;
import com.example.rayzi.modelclass.ReedemListRoot;
import com.example.rayzi.modelclass.RestResponse;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CashOutActivity extends BaseActivity {
    ActivityCashOutBinding binding;

    List<String> paymentGateways = new ArrayList<>();
    int minRcoinForCashout = 0;
    double settingCurrency = 1;
    private String selectedPaymentGateway;
    private int amount;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cash_out);

        minRcoinForCashout = sessionManager.getSetting().getMinRcoinForCashOut();
        binding.tvNote.setText("Withdrawal Vcoins " + sessionManager.getUser().getRCoin());
        binding.tvSettingRcoin.setText(sessionManager.getSetting().getRCoinForCaseOut() + Const.CoinName);
        binding.tvSettingCurrency.setText(settingCurrency + " " + Const.getCurrency());

        paymentGateways.clear();
        paymentGateways.addAll(sessionManager.getSetting().getPaymentGateway());

        if (paymentGateways != null && !paymentGateways.isEmpty()) {
            changeDetails(paymentGateways.get(0));
            binding.rvReedemMethods.setAdapter(new ReedemMethodAdapter(paymentGateways, this::changeDetails));


        } else {
            Toast.makeText(this, R.string.no_payment_method_found , Toast.LENGTH_SHORT).show();
        }

        UserRoot.User user = sessionManager.getUser();
        user.setrCoin(sessionManager.getUser().getRCoin());
        sessionManager.saveUser(user);

        initListner();
        getReedemHistotry();
    }

    private void getReedemHistotry() {
        Call<ReedemListRoot> call = RetrofitBuilder.create().getReedemHistotry(sessionManager.getUser().getId());
        call.enqueue(new Callback<ReedemListRoot>() {
            @Override
            public void onResponse(Call<ReedemListRoot> call, Response<ReedemListRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getRedeem().isEmpty()) {
                        binding.rvHistory.setAdapter(new ReedemHistoryAdapter(response.body().getRedeem()));
                    }
                }
            }

            @Override
            public void onFailure(Call<ReedemListRoot> call, Throwable t) {

            }
        });
    }

    private void initListner() {
        binding.btnSubmit.setOnClickListener(v -> submitData());
        binding.etReedemCoin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    if (!s.toString().isEmpty()) {
                        try {
                            amount = Integer.parseInt(s.toString());
                        } catch (NumberFormatException ex) { // handle your exception
                        }
                        if (amount < minRcoinForCashout) {

                            binding.tvNote.setText( getString(R.string.minimum_amount_is) + minRcoinForCashout + Const.CoinName);
                            binding.tvNote.setTextColor(ContextCompat.getColor(CashOutActivity.this, R.color.red));
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                binding.tvNote.setTextColor(ContextCompat.getColor(CashOutActivity.this, R.color.yellow));
                                binding.tvNote.setText(getString(R.string.withdrawable_vcoins) + sessionManager.getUser().getRCoin());
                            }, 1000);

                        } else if (amount > sessionManager.getUser().getRCoin()) {
                            binding.tvNote.setText(R.string.you_not_have_enough_vcoins );
                            binding.tvNote.setTextColor(ContextCompat.getColor(CashOutActivity.this, R.color.red));
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                binding.tvNote.setTextColor(ContextCompat.getColor(CashOutActivity.this, R.color.yellow));
                                binding.tvNote.setText( getString(R.string.withdrawable_vcoins) + sessionManager.getUser().getRCoin());

                            }, 1000);
                        } else {
                            int diamond = amount / 100;
                            //  binding.tvDiamondsValue.setText("You Will Receive " + String.valueOf(diamond) + " Diamonds");
                        }

                        binding.tvSettingRcoin.setText(amount + Const.CoinName);
                        double cash = amount * settingCurrency / sessionManager.getSetting().getRCoinForCaseOut();
                        String formattedCash = String.format("%.2f", cash);
                        binding.tvSettingCurrency.setText(formattedCash + " " + Const.getCurrency());


                    }
                } else {
                    binding.tvSettingRcoin.setText(sessionManager.getSetting().getRCoinForCaseOut() + Const.CoinName);
                    binding.tvSettingCurrency.setText(settingCurrency + " " + Const.getCurrency());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void submitData() {
        if (amount < sessionManager.getSetting().getRCoinForCaseOut()) {
            Toast.makeText(this,  getString(R.string.minimum_amount_is) + minRcoinForCashout + Const.CoinName, Toast.LENGTH_SHORT).show();
            return;
        }
        if (amount > sessionManager.getUser().getRCoin()) {
            Toast.makeText(this, getString(R.string.insufficient) + Const.CoinName, Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedPaymentGateway == null || selectedPaymentGateway.isEmpty()) {
            Toast.makeText(this,  getString(R.string.no_payment_method_found), Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.etDetails.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.enter_valid_details , Toast.LENGTH_SHORT).show();
            return;
        }

        String des = binding.etDetails.getText().toString();
        if (des.isEmpty()) {
            Toast.makeText(this, R.string.please_enter_your_details , Toast.LENGTH_SHORT).show();
            return;
        }
        customDialogClass.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sessionManager.getUser().getId());
        jsonObject.addProperty("paymentGateway", selectedPaymentGateway);
        jsonObject.addProperty("description", des);
        jsonObject.addProperty("rCoin", amount);
        binding.btnSubmit.setEnabled(false);
        Call<RestResponse> call = RetrofitBuilder.create().cashOutDiamonds(jsonObject);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        Toast.makeText(CashOutActivity.this, R.string.redeem_request_sent_successfully , Toast.LENGTH_SHORT).show();
                        UserRoot.User user = sessionManager.getUser();
                        double currentCoin = user.getRCoin() - amount;
                        user.setrCoin(currentCoin);
                        sessionManager.saveUser(user);
                        binding.etDetails.setText("");
                        binding.etReedemCoin.setText("");
                        getReedemHistotry();
                    }
                }
                binding.btnSubmit.setEnabled(true);
           customDialogClass.dismiss();
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                binding.btnSubmit.setEnabled(true);
            }
        });


    }

    private void changeDetails(String s) {
        selectedPaymentGateway = s;
        if (s.equalsIgnoreCase("UPI details")) {
            binding.etDetails.setHint("Enter your UPI details");
        } else if (s.equalsIgnoreCase("Paytm details")) {
            binding.etDetails.setHint("Enter your Paytm details");
        } else {
            binding.etDetails.setHint("Enter your Bank account details");
        }
    }
}