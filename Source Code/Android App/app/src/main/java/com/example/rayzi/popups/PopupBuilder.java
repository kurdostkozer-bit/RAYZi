package com.example.rayzi.popups;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.ItemExitLiveBinding;
import com.example.rayzi.databinding.ItemPkRequestPopupBinding;
import com.example.rayzi.databinding.ItemPopup4Binding;
import com.example.rayzi.databinding.ItemRemovepopupBinding;
import com.example.rayzi.databinding.ItemSimplepopupBinding;
import com.example.rayzi.databinding.ItemUpdateDialogBinding;
import com.example.rayzi.databinding.PopupExitAppBinding;
import com.example.rayzi.databinding.PopupRcoinConvertBinding;

import com.example.rayzi.retrofit.Const;

public class PopupBuilder {
    private final Context mContext;
    Dialog mBuilder;
    SessionManager sessionManager;

    public PopupBuilder(Context context) {
        this.mContext = context;
        if (mContext != null) {
            sessionManager = new SessionManager(context);
            mBuilder = new Dialog(mContext);
            mBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mBuilder.setCancelable(false);
            mBuilder.setCanceledOnTouchOutside(false);
            if (mBuilder != null && mBuilder.getWindow() != null) {
                mBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }

    public void showSimplePopup(String s, String btnText, OnPopupClickListner onPopupClickListner) {
        if (mContext == null)
            return;
        mBuilder.setCancelable(true);
        mBuilder.setCanceledOnTouchOutside(true);
        ItemSimplepopupBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_simplepopup, null, false);
        mBuilder.setContentView(binding.getRoot());
        binding.tvText.setText(s);
        binding.btncountinue.setText(btnText);
        binding.btncountinue.setOnClickListener(v -> {
            mBuilder.dismiss();
            onPopupClickListner.onClickCountinue();
        });
        mBuilder.show();

    }

    public void showBlockPopup(String s, String btnText, OnPopupClickListner onPopupClickListner) {
        if (mContext == null)
            return;
        mBuilder.setCancelable(false);
        mBuilder.setCanceledOnTouchOutside(false);
        ItemSimplepopupBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_simplepopup, null, false);
        mBuilder.setContentView(binding.getRoot());
        binding.tvText.setText(s);
        binding.btncountinue.setText(btnText);
        binding.btncountinue.setOnClickListener(v -> {
            mBuilder.dismiss();
            onPopupClickListner.onClickCountinue();
        });
        mBuilder.show();
    }

    public void updatePopup(Context context, String aContinue, String cancel, String text, OnMultButtonPopupLister onPopupClickLisnter) {
        mBuilder = new Dialog(context);
        mBuilder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        ItemUpdateDialogBinding popupbinding = DataBindingUtil.inflate(inflater, R.layout.item_update_dialog, null, false);

        mBuilder.setContentView(popupbinding.getRoot());
        popupbinding.tvText.setText(text);
        popupbinding.btnNo.setText(cancel);
        popupbinding.btnYes.setText(aContinue);

        mBuilder.show();
        popupbinding.btnYes.setOnClickListener(v -> {
            mBuilder.dismiss();
            onPopupClickLisnter.onClickCountinue();
        });
        popupbinding.btnNo.setOnClickListener(view -> {
            mBuilder.dismiss();
            onPopupClickLisnter.onClickCancel();
        });


    }


    public void showRemovePopup(OnRemovePopupClickListner onRemovePopupClickListner) {
        if (mContext == null)
            return;

        mBuilder.setCancelable(true);
        mBuilder.setCanceledOnTouchOutside(true);
        ItemRemovepopupBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_removepopup, null, false);
        mBuilder.setContentView(binding.getRoot());

        binding.btnsure.setOnClickListener(v -> {
            mBuilder.dismiss();
            onRemovePopupClickListner.onClickSure();
        });

        binding.btncancel.setOnClickListener(v -> {
            mBuilder.dismiss();
        });


        mBuilder.show();

    }

    public void showLiveEndPopup(OnMultButtonPopupLister listener) {
        if (mContext == null) return;

        mBuilder.setCancelable(true);
        mBuilder.setCanceledOnTouchOutside(true);
        ItemExitLiveBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_exit_live, null, false);
        mBuilder.setContentView(binding.getRoot());
        mBuilder.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        binding.ivKeep.setOnClickListener(v -> {
            mBuilder.dismiss();
            listener.onClickCancel();
        });
        binding.ivExit.setOnClickListener(v -> {
            mBuilder.dismiss();
            listener.onClickCountinue();
        });
        mBuilder.show();

//        binding.text.setText(text);
//        binding.end.setOnClickListener(view -> {
//            mBuilder.dismiss();
//            listener.onEndClick();
//        });
//        binding.cancel.setOnClickListener(view -> {
//            mBuilder.dismiss();
//        });
//        mBuilder.show();

    }

    public void showExitPopup(OnRemovePopupClickListner listener) {
        if (mContext == null) return;

        mBuilder.setCancelable(true);
        mBuilder.setCanceledOnTouchOutside(true);
        PopupExitAppBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.popup_exit_app, null, false);
        mBuilder.setContentView(binding.getRoot());

        binding.btncountinue.setOnClickListener(view -> {
            mBuilder.dismiss();
            listener.onClickSure();
        });
        binding.btnCancel.setOnClickListener(view -> {
            mBuilder.dismiss();
        });
        mBuilder.show();

    }

    public interface OnCloseLiveClickListner {
        void onEndClick();
    }

    public void showLiveEndPopup(String s, String btnText, OnPopupClickListner onPopupClickListner) {
        if (mContext == null)
            return;

        mBuilder.setCancelable(false);
        mBuilder.setCanceledOnTouchOutside(false);
        ItemSimplepopupBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_simplepopup, null, false);
        mBuilder.setContentView(binding.getRoot());
        binding.tvText.setText(s);
        binding.btncountinue.setText(btnText);
        binding.btncountinue.setOnClickListener(v -> {
            mBuilder.dismiss();
            onPopupClickListner.onClickCountinue();
        });
        mBuilder.show();

    }

    public void showPkRequestPopup(String title, String userImage, String avatarFrame, String btnPositive, String btnNegative, OnMultButtonPopupLister onPopupClickListner) {
        if (mContext == null)
            return;

        mBuilder.setCancelable(true);
        mBuilder.setCanceledOnTouchOutside(true);
        ItemPkRequestPopupBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_pk_request_popup, null, false);
        mBuilder.setContentView(binding.getRoot());

        binding.imgUser.setUserImage(userImage, avatarFrame, 30);

        binding.tvText.setText(title);
        binding.btncountinue.setText(btnPositive);
        binding.btncountinue.setOnClickListener(v -> {
            mBuilder.dismiss();
            onPopupClickListner.onClickCountinue();
        });

        binding.btnCancel.setText(btnNegative);
        binding.btnCancel.setOnClickListener(v -> {
            mBuilder.dismiss();

            onPopupClickListner.onClickCancel();
        });
        mBuilder.show();

    }

    public void showRcoinConvertPopup(boolean isCashOut, double maxCoin, OnRcoinConvertPopupClickListner onRcoinConvertPopupClickListner) {
        if (mContext == null)
            return;

        mBuilder.setCancelable(true);
        mBuilder.setCanceledOnTouchOutside(true);
        PopupRcoinConvertBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.popup_rcoin_convert, null, false);
        mBuilder.setContentView(binding.getRoot());
        final int[] coin = new int[1];


        if (isCashOut) {
            binding.tvText.setText(R.string.how_much_vcoins_convert_cash);
            binding.btncountinue.setText(R.string.cash_out);

        } else {
            binding.tvText.setText(R.string.how_much_vcoins_convert_into_diamonds);
            binding.btncountinue.setText(R.string.convert_to_diamond);
        }


        binding.etRcoin.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (sessionManager.getSetting().getRCoinForDiamond() == 0) {
                    Toast.makeText(mContext, R.string.setting_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                double rCoinForDiamond = sessionManager.getSetting().getRCoinForDiamond();
                if (!s.toString().isEmpty()) {
                    try {
                        coin[0] = Integer.parseInt(s.toString());
                    } catch (NumberFormatException ex) { // handle your exception
                    }
                    if (coin[0] < rCoinForDiamond) {
                        binding.tvDiamondsValue.setText(R.string.minimum_amount_is + rCoinForDiamond + Const.CoinName);
                        binding.tvDiamondsValue.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                        new Handler(Looper.getMainLooper()).postDelayed(() -> binding.tvDiamondsValue.setTextColor(ContextCompat.getColor(mContext, R.color.yellow)), 1000);

                    } else if (coin[0] > maxCoin) {
                        binding.tvDiamondsValue.setText(R.string.you_not_have_enough + Const.CoinName);
                        binding.tvDiamondsValue.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                        new Handler(Looper.getMainLooper()).postDelayed(() -> binding.tvDiamondsValue.setTextColor(ContextCompat.getColor(mContext, R.color.yellow)), 1000);
                    } else {
                        if (isCashOut) {
                            double diamond = coin[0] / rCoinForDiamond;
                            binding.tvDiamondsValue.setText(R.string.you_will_receive + String.valueOf(diamond) + Const.getCurrency());
                        } else {
                            double diamond = coin[0] / rCoinForDiamond;
                            binding.tvDiamondsValue.setText(R.string.you_will_receive + String.valueOf(diamond) + R.string.diamonds);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.btncountinue.setOnClickListener(v -> {
            mBuilder.dismiss();
            onRcoinConvertPopupClickListner.onClickConvert(coin[0]);
        });
        binding.btnCancel.setOnClickListener(v -> mBuilder.dismiss());
        mBuilder.show();


    }


    public void showReliteDiscardPopup(String s1, String s2, String btn1, String btn2, OnPopupClickListner onPopupClickListner) {
        if (mContext == null)
            return;

        mBuilder.setCancelable(true);
        mBuilder.setCanceledOnTouchOutside(true);
        ItemPopup4Binding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_popup_4, null, false);
        mBuilder.setContentView(binding.getRoot());
        binding.tvText.setText(s1);
        binding.tvText2.setText(s2);
        binding.btncountinue.setText(btn1);
        binding.btnCancel.setText(btn2);
        binding.btnCancel.setOnClickListener(v -> mBuilder.dismiss());
        binding.btncountinue.setOnClickListener(v -> {
            mBuilder.dismiss();
            onPopupClickListner.onClickCountinue();
        });
        if (s1.isEmpty()) {
            binding.tvText.setVisibility(View.GONE);
        }
        if (s2.isEmpty()) {
            binding.tvText2.setVisibility(View.GONE);
        }
        if (btn1.isEmpty()) {
            binding.btncountinue.setVisibility(View.GONE);
        }
        if (btn2.isEmpty()) {
            binding.btnCancel.setVisibility(View.GONE);
        }
        mBuilder.show();

    }

    public interface OnPopupClickListner {
        void onClickCountinue();

    }

    public interface OnRcoinConvertPopupClickListner {
        void onClickConvert(int rcoin);
    }


    public interface OnRemovePopupClickListner {
        void onClickSure();

    }

    public interface OnMultButtonPopupLister {
        void onClickCountinue();

        void onClickCancel();
    }

}
