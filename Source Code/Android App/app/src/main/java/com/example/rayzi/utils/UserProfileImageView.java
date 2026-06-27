package com.example.rayzi.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.R;
import com.example.rayzi.databinding.ItemUserprofileImageviewBinding;

public class UserProfileImageView extends RelativeLayout {

    ItemUserprofileImageviewBinding binding;

    public UserProfileImageView(Context context) {
        super(context);
        init();
    }

    public UserProfileImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }


    public UserProfileImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public UserProfileImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.item_userprofile_imageview, null, false);
        binding.imguser.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_round_pink));
        addView(binding.getRoot());
    }

    private void init() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.item_userprofile_imageview, null, false);
        addView(binding.getRoot());
    }

    public void setUserImage(String profileImage, String avatarFrame, int padding) {
        if (binding != null) {
            if (profileImage != null && !profileImage.isEmpty()) {
                Glide.with(this).load(profileImage).placeholder(R.drawable.profile_placeholder).circleCrop().into(binding.imguser);
            }
            if (avatarFrame != null && !avatarFrame.isEmpty()) {
                binding.imguser.setPadding(padding, padding, padding, padding);
                binding.imguser.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
                Glide.with(this).load(BuildConfig.BASE_URL + avatarFrame).into(binding.imgvip);
            } else {
                binding.imgvip.setVisibility(GONE);
            }
        }
    }

}
