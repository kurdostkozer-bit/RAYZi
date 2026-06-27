package com.example.rayzi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.databinding.ActivityCallRequestBinding;
import com.example.rayzi.modelclass.ChatUserListRoot;
import com.example.rayzi.modelclass.GuestProfileRoot;
import com.example.rayzi.retrofit.Const;
import com.google.gson.Gson;

public class FakeCallRequestActivity extends AppCompatActivity {
    ActivityCallRequestBinding binding;
    Handler handler = new Handler();
    private int sec = 0;
    private ChatUserListRoot.ChatUserItem chatUser;
    private GuestProfileRoot.User guestUser;
    private String videoLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_request);
        Intent intent = getIntent();
        boolean isFromRandom = intent.getBooleanExtra(Const.IS_FROM_RANDOM, false);
        if (isFromRandom) {
            String userStr = intent.getStringExtra(Const.USER);
            if (userStr != null && !userStr.isEmpty()) {
                guestUser = new Gson().fromJson(userStr, GuestProfileRoot.User.class);
                binding.tvName.setText(guestUser.getName());
                Log.e("TAG", "onCreate: >>>>>>>>>>>>  fake " + guestUser.getImage());
                binding.imgUser.setUserImage(guestUser.getImage(), guestUser.getAvatarFrameImage(), 30);
                videoLink = guestUser.getLink();
            }
        } else {
            String userStr = intent.getStringExtra(Const.CHATROOM);
            Log.e("TAG", "onCreate: >>>>>>>>>>>>  fake " + userStr);
            if (userStr != null && !userStr.isEmpty()) {
                chatUser = new Gson().fromJson(userStr, ChatUserListRoot.ChatUserItem.class);
                binding.tvName.setText(chatUser.getName());
                Log.e("TAG", "onCreate: >>>>>>>>>>>>  fake " + chatUser.getImage());
                binding.imgUser.setUserImage(chatUser.getImage(), chatUser.getAvatarFrameImage(), 30);
                videoLink = chatUser.getLink();
            }
        }
        handler.postDelayed(runnable, 1000);
        binding.btnDecline.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (sec >= 3) {
                binding.tvStatus.setText(R.string.ringing);
            }
            handler.postDelayed(this, 1000);
            if (sec >= 5) {

                handler.removeCallbacks(runnable);
                //   startActivity(new Intent(CallRequestActivity.this, CallIncomeActivity.class));
                startActivity(new Intent(FakeCallRequestActivity.this, FakeVideoCallActivity.class).putExtra(Const.VIDEOLINK, videoLink));
            }
            sec++;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacks(runnable);
        finish();
    }


}