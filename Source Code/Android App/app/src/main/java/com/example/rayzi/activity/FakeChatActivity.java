package com.example.rayzi.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rayzi.FakeChat.FakeChatAdapter;
import com.example.rayzi.FakeChat.fakemodelclass.ChatRootFake;
import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.bottomsheets.BottomSheetReport_g;
import com.example.rayzi.bottomsheets.BottomSheetReport_option;
import com.example.rayzi.databinding.ActivityFakeChatBinding;
import com.example.rayzi.modelclass.ChatUserListRoot;
import com.example.rayzi.modelclass.UploadImageRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.socket.MySocketManager;
import com.example.rayzi.user.guestUser.GuestActivity;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FakeChatActivity extends BaseActivity {
    ActivityFakeChatBinding binding;
    FakeChatAdapter chatAdapter = new FakeChatAdapter();
    SessionManager sessionManager;
    private ChatUserListRoot.ChatUserItem chatUser;
    boolean isSend = false;
    boolean isShowFullImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fake_chat);
        getWindow().setStatusBarColor(Color.parseColor("#292132"));

        sessionManager = new SessionManager(this);
        Intent intent = getIntent();
        String userStr = intent.getStringExtra(Const.CHATROOM);
        Log.e(TAG, "onCreate: >>>>>>>>>>>> " + userStr);
        if (userStr != null && !userStr.isEmpty()) {
            chatUser = new Gson().fromJson(userStr, ChatUserListRoot.ChatUserItem.class);
            initView();
        }
        initListener();
    }

    private void initListener() {
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messgae = binding.etChat.getText().toString();
                if (messgae.equals("")) {
                    Toast.makeText(FakeChatActivity.this, R.string.type_message_first, Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.etChat.setText("");
                chatAdapter.addSingleMessage(new ChatRootFake(1, messgae, sessionManager.getUser().getImage()));
                binding.rvChat.scrollToPosition(0);
            }
        });
    }

    private void initView() {

        Glide.with(this).load(chatUser.getImage()).circleCrop().into(binding.imgUser);
        binding.tvUserNamew.setText(chatUser.getName());
        binding.rvChat.setAdapter(chatAdapter);

        chatAdapter.setOnClickListener(new FakeChatAdapter.OnClickListener() {
            @Override
            public void onImageClick(int position, String imageUrl) {

                isShowFullImage = true;

                binding.layFullImage.setVisibility(View.VISIBLE);

                Glide.with(FakeChatActivity.this)
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.ivFullImage);

                binding.ivClose.setOnClickListener(view -> {
                    isShowFullImage = false;
                    binding.layFullImage.setVisibility(View.GONE);
                });
            }
        });

        chatAdapter.updateChatData(generateFakeChatData());
        binding.rvChat.scrollToPosition(0);
    }

    public void onClickVideoCall(View view) {
        startActivity(new Intent(this, FakeCallRequestActivity.class).putExtra(Const.CHATROOM, new Gson().toJson(chatUser)));
    }

    public void onClickUser(View view) {
        if (chatUser != null) {
            startActivity(new Intent(this, GuestActivity.class).putExtra(Const.USER_STR, new Gson().toJson(chatUser)));
        }
    }

    public void onClickCamara(View view) {
        if(chatUser.isFake()){
            Toast.makeText(this, R.string.this_is_demo_user_you_can_t_sent_image_to_demo_user, Toast.LENGTH_SHORT).show();
        }
//        choosePhoto();
    }

    private boolean checkPermission() {
        int result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void choosePhoto() {

        if (checkPermission()) {
            openGallery();
        } else {
            requestPermission();
        }

    }

    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 10001);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
        }

    }

    private String picturePath;
    private Uri selectedImage;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 201 && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            startCropActivity(data.getData());

            Glide.with(this)
                    .load(selectedImage)
                    .placeholder(R.drawable.ic_user_place).error(R.drawable.ic_user_place)
                    .into(binding.imageview);

            picturePath = getRealPathFromURI(selectedImage);

            isSend = false;
        } else if (requestCode == 69 && resultCode == -1) {
            handleCropResult(data);
        }
        if (resultCode == 96) {
            handleCropError(data);
        }

    }

    private void handleCropResult(@androidx.annotation.NonNull Intent result) {
        Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {

            selectedImage = resultUri;

            Glide.with(this)
                    .load(selectedImage)
                    .placeholder(R.drawable.ic_user_place).error(R.drawable.ic_user_place)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imageview);
            binding.imageview.setAdjustViewBounds(true);
            picturePath = getRealPathFromURI(selectedImage);

            chatAdapter.addSingleMessage(new ChatRootFake(2, picturePath, chatUser.getImage()));
            binding.rvChat.scrollToPosition(0);

        } else {
            Toast.makeText(this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCropError(@androidx.annotation.NonNull Intent result) {
        Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e("TAG", "handleCropError: ", cropError);
            Toast.makeText(this, cropError.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show();
    }

    public void openGallery() {
        try {
            startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 201);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<ChatRootFake> generateFakeChatData() {
        List<ChatRootFake> fakeChatData = new ArrayList<>();
        Random random = new Random(); // Create a Random object for generating random numbers

        // Sample messages for sender and receiver
        String[] senderMessages = {
                "Hey! How's it going?",
                "I'm planning to watch a movie tonight.",
                "What do you think about the latest news?",
                "I'm working on a new project at work.",
                "Want to grab a coffee later?",
                "Just got back from a run, feeling great!",
                "Do you have any book recommendations?",
                "How was your weekend?",
                "I'm thinking of taking a vacation next month.",
                "Have you tried the new restaurant downtown?",
                "What are you up to today?",
                "Just finished a great book, can't wait to tell you about it.",
                "Got any plans for the evening?",
                "I'm thinking about learning a new language.",
                "Have you seen the latest episode of the show?",
                "Do you like the new album that came out?",
                "I've been really into podcasts lately.",
                "What are your thoughts on the new gadget?",
                "I'm planning to start a blog.",
                "What's your favorite hobby?",
                "How do you stay motivated?",
                "Any tips for productivity?",
                "What do you do to relax?",
                "How do you handle stress?",
                "What inspires you?"
        };

        String[] receiverMessages = {
                "I'm good, how about you?",
                "That sounds fun! Which movie?",
                "I'm not sure, I haven't been following the news much.",
                "Really? What's the project about?",
                "Sure, I'd love that!",
                "Wow, good for you! I need to get back into exercising.",
                "I recently read a great thriller.",
                "It was relaxing, didn't do much.",
                "That sounds awesome! Where are you planning to go?",
                "No, not yet. Is it good?",
                "Just working from home, you?",
                "That sounds interesting, tell me more!",
                "Not really, just taking it easy.",
                "That's cool! Which language are you interested in?",
                "Yes, it was amazing! Can't wait for the next one.",
                "I haven't listened to it yet, any good tracks?",
                "Same here, they've been quite informative.",
                "I'm thinking of getting one too. What features do you like?",
                "That's exciting! What will you write about?",
                "I enjoy painting and reading.",
                "I set small goals to keep myself going.",
                "I find making to-do lists helpful.",
                "Watching a movie or reading a book.",
                "I try to meditate or go for a walk.",
                "Nature and art are my biggest inspirations."
        };

        // Generate 50 random fake chat messages, alternating between sender and receiver
        for (int i = 0; i < 50; i++) {
            if (i % 2 == 0) {
                // Add a random sender message
                String randomSenderMessage = senderMessages[random.nextInt(senderMessages.length)];
                fakeChatData.add(new ChatRootFake(1, randomSenderMessage, sessionManager.getUser().getImage()));
            } else {
                // Add a random receiver message
                String randomReceiverMessage = receiverMessages[random.nextInt(receiverMessages.length)];
                fakeChatData.add(new ChatRootFake(2, randomReceiverMessage, chatUser.getImage()));
            }
        }

        return fakeChatData;
    }

    public void onClickReport(View view) {
        if (chatUser == null) return;
        new BottomSheetReport_option(FakeChatActivity.this, new BottomSheetReport_option.OnReportedListener() {
            @Override
            public void onReported() {
                new BottomSheetReport_g(FakeChatActivity.this, chatUser.getUserId(), () -> {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.customtoastlyt));
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();

                });
            }

            @Override
            public void onBlocked() {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (isShowFullImage) {
            binding.layFullImage.setVisibility(View.GONE);
            isShowFullImage = false;
        } else {
            super.onBackPressed();
        }

    }
}