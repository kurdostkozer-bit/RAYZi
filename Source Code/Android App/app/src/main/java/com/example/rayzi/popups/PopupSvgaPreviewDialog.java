package com.example.rayzi.popups;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.PopupSvgaPreviewBinding;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import java.net.MalformedURLException;
import java.net.URL;


public class PopupSvgaPreviewDialog {
    private static final String TAG = "PopupSvgaPreview";
    SessionManager sessionManager;
    Dialog dialog;

    public PopupSvgaPreviewDialog() {
    }

    public Dialog showPopupSvgaPreview(Context context, String svgaImage, String avatarFrame, String userImage, String userName) {
        sessionManager = new SessionManager(context);
        dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        PopupSvgaPreviewBinding binding = DataBindingUtil.inflate(inflater, R.layout.popup_svga_preview, null, false);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(binding.getRoot());
        SVGAImageView imageView = binding.svgImage;
        SVGAParser parser = new SVGAParser(context);
        try {
            parser.decodeFromURL(new URL(svgaImage != null && !svgaImage.isEmpty() ? BuildConfig.BASE_URL + svgaImage : ""), new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                    SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
                    imageView.setImageDrawable(drawable);
                    imageView.startAnimation();
                    new Handler().postDelayed(() -> {
                        if (dialog != null) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    }, 6000);
                }

                @Override
                public void onError() {

                }
            }, list -> {

            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        /*        Glide.with(context).load().into(binding.svgImage);*/
        binding.userName.setText(userName);
        Glide.with(context).load(userImage).circleCrop().into(binding.userImage);
        Glide.with(context).load(avatarFrame != null && !avatarFrame.isEmpty() ? BuildConfig.BASE_URL + avatarFrame : "").into(binding.avatarFrameImage);

        binding.btnClose.setVisibility(View.GONE);

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
        animation.setFillAfter(true);
        binding.nameLyt.startAnimation(animation);

        binding.btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

        return dialog;
    }

}
