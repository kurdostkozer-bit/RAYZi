package com.example.rayzi.pk.BottomSheets;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.rayzi.R;
import com.example.rayzi.databinding.BottomsheetPkResultBinding;
import com.example.rayzi.modelclass.LiveStreamPkEndRoot;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class PkResultBottomSheet {
    private static final String TAG = "PkResultBottomSheet";

    public BottomSheetDialog bottomSheetDialog;

    public Context context;


    public PkResultBottomSheet(Context context) {

        if (context != null) {
            this.context = context;

            bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
            bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            bottomSheetDialog.setOnShowListener(dialog -> {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            });
            bottomSheetDialog.setCancelable(true);
            bottomSheetDialog.setCanceledOnTouchOutside(true);
        }
    }

    public BottomSheetDialog openPKResultSheet(PkAudioLiveUserRoot.UsersItem localLiveHost, boolean isHost, JSONObject jsonObject, OnPkResultClickLister onPkResultClickLister) {
        if (context == null) return null;
        BottomsheetPkResultBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottomsheet_pk_result, null, false);
        bottomSheetDialog.setContentView(binding.getRoot());

        binding.topLytForPkAudience.setVisibility(View.GONE);
        binding.topLytResultForHosts.setVisibility(View.GONE);
        binding.lytButtonsForPk.setVisibility(View.GONE);
        binding.pkResultTopLyt.setVisibility(View.GONE);

        if (localLiveHost.getPkConfig().getIsWinner() == 2) {
            binding.pkResultMainBg.setBackgroundResource(R.drawable.pkwinresultmainbg);
            binding.congratsText.setText(R.string.congrats);
            binding.resultWinnerText.setText(R.string.you_win);
            binding.pkResultEmoji.setBackgroundResource(R.drawable.pkresultwinemoji);
        } else if (localLiveHost.getPkConfig().getIsWinner() == 1) {
            binding.pkResultMainBg.setBackgroundResource(R.drawable.pkloseresultmainbg);
            binding.congratsText.setText(R.string.ohh);
            binding.resultWinnerText.setText(R.string.you_lose);
            binding.pkResultEmoji.setBackgroundResource(R.drawable.pkresultloseemoji);
        } else {
            binding.pkResultMainBg.setBackgroundResource(R.drawable.pktieresultmainbg);
            binding.congratsText.setText(R.string.ohh);
            binding.resultWinnerText.setText(R.string.tie);
            binding.pkResultEmoji.setBackgroundResource(R.drawable.pkresulttieemoji);

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.pkResultEmoji.getLayoutParams();
            int rightMargin = 32; // Adjust this value according to your requirements
            params.setMargins(params.leftMargin, params.topMargin, rightMargin, params.bottomMargin);
            binding.pkResultEmoji.setLayoutParams(params);
        }


        if (isHost) {
            binding.topLytResultForHosts.setVisibility(View.VISIBLE);
            binding.lytButtonsForPk.setVisibility(View.VISIBLE);
            binding.pkResultTopLyt.setVisibility(View.VISIBLE);
            setPoints(localLiveHost.getPkConfig().getLocalRank(), localLiveHost.getPkConfig().getRemoteRank(), binding);
            binding.localHostImage.setUserImage(localLiveHost.getImage(), localLiveHost.getAvatarFrameImage(), 15);
            binding.remoteHostImage.setUserImage(localLiveHost.getPkConfig().getHost2Details().getImage(), localLiveHost.getPkConfig().getHost2Details().getAvatarFrameImage(), 15);
            binding.localHostName.setText(localLiveHost.getName());
            binding.localHostRank.setText(String.valueOf(localLiveHost.getPkConfig().getLocalRank()));
            binding.remoteHostName.setText(localLiveHost.getPkConfig().getHost2Name());
            binding.remoteHostRank.setText(String.valueOf(localLiveHost.getPkConfig().getRemoteRank()));

            binding.matchAgainForPk.setOnClickListener(view -> {
                onPkResultClickLister.onMatchAgainClick();
                bottomSheetDialog.dismiss();
            });
            binding.cancelForPk.setOnClickListener(view -> {
                bottomSheetDialog.dismiss();
            });
        } else {
            try {
                LiveStreamPkEndRoot.Winner winner = null;
                if (jsonObject.has("winner")) {
                    winner = new Gson().fromJson(jsonObject.getString("winner"), LiveStreamPkEndRoot.Winner.class);
                }
                if (winner != null) {
                    binding.topLytForPkAudience.setVisibility(View.VISIBLE);
                    binding.pkResultMainBg.setBackgroundResource(R.drawable.audienceforpkmainbg);
                    if (localLiveHost.getPkConfig().getIsWinner() == 0) {
                        binding.ohhhText.setText(R.string.ohh);
                        binding.winnerText.setText(R.string.it_s_a_tie);
                        binding.winnerHostNameForAudience.setVisibility(View.GONE);
                        binding.winnerHostImageForAudience.setVisibility(View.GONE);
                        binding.cancelForPk.setVisibility(View.VISIBLE);
                    } else {
                        Glide.with(context).load(winner.getImage()).circleCrop().placeholder(R.drawable.fiveplaceholderround).into(binding.winnerHostImageForAudience);
                        binding.winnerHostNameForAudience.setText(winner.getName());
                    }
                }


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        Log.d(TAG, "PkResultBottomSheet: first time " + bottomSheetDialog.isShowing());
        return bottomSheetDialog;
    }

    public void showDialog() {
        Log.d(TAG, "PkResultBottomSheet showDialog: second time" + bottomSheetDialog.isShowing());
        if (bottomSheetDialog != null && !bottomSheetDialog.isShowing()) {
            bottomSheetDialog.show();
        }
    }

    public void setPoints(int localPoint, int remotePoint, BottomsheetPkResultBinding binding) {
        if (localPoint < 0 || remotePoint < 0) {
            return;
        }
        int localWeight;
        int remoteWeight;
        if (localPoint == 0 && remotePoint == 0) {
            localWeight = 1;
            remoteWeight = 1;
        } else if (localPoint == 0) {
            localWeight = 10;
            remoteWeight = 90;
        } else if (remotePoint == 0) {
            localWeight = 90;
            remoteWeight = 10;
        } else {
            localWeight = localPoint;
            remoteWeight = remotePoint;
        }
        setWeight(binding.pkProgressLeftText, localWeight);
        setWeight(binding.pkProgressRightText, remoteWeight);
    }

    public void setWeight(LinearLayout textView, int weight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
        params.weight = weight;
        textView.setLayoutParams(params);
    }


    public interface OnPkResultClickLister {
        void onMatchAgainClick();
    }

}
