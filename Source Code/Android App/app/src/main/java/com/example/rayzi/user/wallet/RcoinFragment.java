package com.example.rayzi.user.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.activity.BaseFragment;
import com.example.rayzi.databinding.FragmentRcoinBinding;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.popups.PopupBuilder;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RcoinFragment extends BaseFragment {


    FragmentRcoinBinding binding;
    private PopupBuilder popupBuilder;

    public RcoinFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rcoin, container, false);
        popupBuilder = new PopupBuilder(getActivity());
        initMain();
        return binding.getRoot();
    }

    private void initMain() {
        binding.tvSettingRcoin.setText(String.valueOf(sessionManager.getSetting().getRCoinForDiamond()) + Const.CoinName);
        binding.tvRcoin.setText(String.valueOf(sessionManager.getUser().getRCoin()));
        binding.tvWithdrawingRcoin.setText(String.valueOf(sessionManager.getUser().getWithdrawalRcoin()));
        binding.btnConvert.setOnClickListener(v -> {
            popupBuilder.showRcoinConvertPopup(false, sessionManager.getUser().getRCoin(), rcoin -> converRcoinToDiamond(rcoin));
        });

        binding.btnCashout.setOnClickListener(v -> {
            if (!sessionManager.getUser().getLevel().getAccessibleFunction().isCashOut()) {
                new PopupBuilder(getActivity()).showSimplePopup(getString(R.string.you_are_not_able_to_cashout_at_your_level),  getString(R.string.dismiss), () -> {
                });
                return;
            }
            startActivity(new Intent(getActivity(), CashOutActivity.class));
           /* PopupBuilder popupBuilder = new PopupBuilder(getActivity());
            popupBuilder.showRcoinConvertPopup(true, myRcoin, rcoin -> {
                double cash = rcoin / 100;

            });
*/
        });

    }

    private void converRcoinToDiamond(int rcoin) {
        customDialogClass.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sessionManager.getUser().getId());
        jsonObject.addProperty("rCoin", rcoin);
        Call<UserRoot> call = RetrofitBuilder.create().convertRcoinToDiamond(jsonObject);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        sessionManager.saveUser(response.body().getUser());
                        double dimonds = rcoin / sessionManager.getSetting().getRCoinForDiamond();
                        String s = getString(R.string.your) + rcoin + Const.CoinName + getString(R.string.successfully_converted_into) + dimonds +  getString(R.string.diamonds);
                        popupBuilder.showSimplePopup(s,  getString(R.string.continue_text), () -> initMain());
                    } else {
                        popupBuilder.showSimplePopup(response.body().getMessage(),  getString(R.string.continue_text), () -> initMain());
                    }
                }
                customDialogClass.dismiss();
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
                customDialogClass.dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initMain();
    }
}