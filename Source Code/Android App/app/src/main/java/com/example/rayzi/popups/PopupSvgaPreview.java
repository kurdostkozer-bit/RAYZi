package com.example.rayzi.popups;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.opensource.svgaplayer.SVGADynamicEntity;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;


public class PopupSvgaPreview {
    private static final String TAG = "PopupSvgaPreview";
    SessionManager sessionManager;
    Dialog dialog;
    long animationDurationMillis;

    public PopupSvgaPreview(Context context, String svgaImage, String avatarFrame, String userImage) {
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
            parser.decodeFromURL(new URL(BuildConfig.BASE_URL + svgaImage), new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                    SVGADynamicEntity dynamicEntity = new SVGADynamicEntity();
                    dynamicEntity.setDynamicImage(BuildConfig.BASE_URL + svgaImage, "99");
                    SVGADrawable drawable = new SVGADrawable(svgaVideoEntity,dynamicEntity);
                    imageView.setImageDrawable(drawable);
                    imageView.startAnimation();

                    animationDurationMillis = svgaVideoEntity.getFrames() / svgaVideoEntity.getFPS() * 1000L;

                    new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(() -> {
                        binding.svgImage.setVisibility(View.GONE);
                        binding.svgImage.clear();
                    }, animationDurationMillis);
                }

                @Override
                public void onError() {

                }
            }, null);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//        Glide.with(context).load(BuildConfig.BASE_URL + svgaImage).into(binding.svgImage);
        Log.d(TAG, "PopupSvgaPreview: " + sessionManager.getUser().getName());
        binding.userName.setText(sessionManager.getUser().getName());
        Glide.with(context).load(userImage).circleCrop().into(binding.userImage);
        Glide.with(context).load(BuildConfig.BASE_URL + avatarFrame).into(binding.avatarFrameImage);

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
        animation.setFillAfter(true);

        binding.nameLyt.startAnimation(animation);
        binding.btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.setOnDismissListener(dialogInterface -> {
            binding.svgImage.stopAnimation();
            binding.svgImage.clearAnimation();
        });

        dialog.show();


    }

}
