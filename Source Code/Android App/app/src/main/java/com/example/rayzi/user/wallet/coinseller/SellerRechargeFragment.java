package com.example.rayzi.user.wallet.coinseller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.activity.BaseFragment;
import com.example.rayzi.databinding.FragmentSellerRechargeBinding;
import com.example.rayzi.modelclass.CoinSellerDataRoot;
import com.example.rayzi.modelclass.GuestUsersListRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerRechargeFragment extends BaseFragment {

    FragmentSellerRechargeBinding binding;
    private CoinSellerDataRoot.CoinSeller coinseller;
    private boolean isUserIdVerify = false;
    private final String TAG = "SellerRechargeFragment";

    public SellerRechargeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_seller_recharge, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getMyData();
        initView();

    }

    private void initView() {
        binding.ivFind.setOnClickListener(view -> {

            if (binding.etUserId.getText().toString().isEmpty()) {
                Toast.makeText(requireActivity(),  getString(R.string.enter_user_id), Toast.LENGTH_SHORT).show();
                return;
            }

            if (sessionManager.getUser().getUniqueId().equals(binding.etUserId.getText().toString())) {
                isUserIdVerify = true;
                binding.tvUserName.setVisibility(View.VISIBLE);
                binding.tvUserName.setText(sessionManager.getUser().getName());
                binding.btnTopup.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.pink));
            } else {
                customDialogClass.show();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("userId", sessionManager.getUser().getId());
                jsonObject.addProperty("value", binding.etUserId.getText().toString());
                jsonObject.addProperty("start", 0);
                jsonObject.addProperty("limit", Const.LIMIT);
                RetrofitBuilder.create().searchUser(jsonObject).enqueue(new Callback<>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(@NonNull Call<GuestUsersListRoot> call, @NonNull Response<GuestUsersListRoot> response) {
                        if (response.code() == 200) {
                            assert response.body() != null;
                            if (response.body().isStatus() && !response.body().getUser().isEmpty()) {
                                isUserIdVerify = true;
                                binding.tvUserName.setVisibility(View.VISIBLE);
                                binding.tvUserName.setText(response.body().getUser().get(0).getName());
                                binding.btnTopup.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.pink));
                            } else {
                                isUserIdVerify = false;
                                binding.tvUserName.setVisibility(View.VISIBLE);
                                binding.tvUserName.setText(R.string.user_not_found );
                            }
                        }
                        customDialogClass.dismiss();
                    }

                    @Override
                    public void onFailure(@NonNull Call<GuestUsersListRoot> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });
    }

    private void getMyData() {
        customDialogClass.show();
        RetrofitBuilder.create().getMyCoinSellerData(sessionManager.getUser().getId()).enqueue(new Callback<CoinSellerDataRoot>() {
            @Override
            public void onResponse(@NonNull Call<CoinSellerDataRoot> call, @NonNull Response<CoinSellerDataRoot> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().isStatus() && response.body().getCoinSeller() != null) {
                        coinseller = response.body().getCoinSeller();
                        binding.tvMyCoins.setText(String.valueOf(coinseller.getCoin()));
                        customDialogClass.dismiss();
                        initListeners();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CoinSellerDataRoot> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void initListeners() {
        binding.btnTopup.setOnClickListener(view -> {

            if (isUserIdVerify) {
                Log.d(TAG, "onResponse: coinSeller id " + coinseller.getId());
                String userId = binding.etUserId.getText().toString();
                String coin = binding.etCoin.getText().toString();
                String note = binding.etBaht.getText().toString();

                if (userId.isEmpty()) {
                    Toast.makeText(getActivity(),  getString(R.string.enter_user_id), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (coin.isEmpty()) {
                    Toast.makeText(getActivity(), R.string.enter_coin_value , Toast.LENGTH_SHORT).show();
                    return;
                }

                if (coinseller.getCoin() < Integer.parseInt(coin)){
                    Toast.makeText(getActivity(), R.string.insufficient_coins, Toast.LENGTH_SHORT).show();
                    return;
                }

                customDialogClass.show();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("coinSellerId", coinseller.getId());
                jsonObject.addProperty("uniqueId", userId);
                jsonObject.addProperty("coin", coin);
                jsonObject.addProperty("note", note);
                RetrofitBuilder.create().sendCoinToUser(jsonObject).enqueue(new Callback<CoinSellerDataRoot>() {
                    @Override
                    public void onResponse(@NonNull Call<CoinSellerDataRoot> call, @NonNull Response<CoinSellerDataRoot> response) {
                        if (response.code() == 200) {
                            assert response.body() != null;
                            if (response.body().isStatus()) {
                                Toast.makeText(getActivity(), R.string.send_successfully , Toast.LENGTH_SHORT).show();
                                binding.tvUserName.setVisibility(View.GONE);
                                binding.etBaht.setText("");
                                binding.etUserId.setText("");
                                binding.etCoin.setText("");
                                binding.tvMyCoins.setText(String.valueOf(response.body().getCoinSeller().getCoin()));
                                customDialogClass.dismiss();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<CoinSellerDataRoot> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });
            } else {
                Toast.makeText(requireActivity(), R.string.please_verify_user_id , Toast.LENGTH_SHORT).show();
            }
        });
    }
}