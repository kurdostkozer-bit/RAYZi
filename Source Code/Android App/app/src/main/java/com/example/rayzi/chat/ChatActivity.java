package com.example.rayzi.chat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.bottomsheets.BottomSheetMessageDetails;
import com.example.rayzi.bottomsheets.BottomSheetReport_g;
import com.example.rayzi.bottomsheets.BottomSheetReport_option;
import com.example.rayzi.databinding.ActivityChatBinding;
import com.example.rayzi.modelclass.ChatItem;
import com.example.rayzi.modelclass.ChatUserListRoot;
import com.example.rayzi.modelclass.GuestProfileRoot;
import com.example.rayzi.modelclass.RestResponse;
import com.example.rayzi.modelclass.UploadImageRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.retrofit.UserApiCall;
import com.example.rayzi.socket.ChatHandler;
import com.example.rayzi.socket.MySocketManager;
import com.example.rayzi.user.guestUser.GuestActivity;
import com.example.rayzi.videocall.CallRequestActivity;
import com.example.rayzi.viewModel.ChatViewModel;
import com.example.rayzi.viewModel.ViewModelFactory;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatActivity extends BaseActivity {
    public static final int RESULT_LOAD_IMAGE = 201;
    private static final String TAG = "chatactivity";
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "cropimage";
    public static String otherUserId = "";
    public static boolean isOPEN = false;
    ActivityChatBinding binding;
    SessionManager sessionManager;
    boolean isSend = false;
    String destinationUri = SAMPLE_CROPPED_IMAGE_NAME + ".png";
    private ChatViewModel viewModel;
    private GuestProfileRoot.User guestUser;
    ChatHandler chatHandler = new ChatHandler() {
        @Override
        public void onChat(Object[] args) {
            Log.d(TAG, "chetlister : " + args[0]);
            if (args[0] != null) {
                runOnUiThread(() -> {
                    ChatItem chatUserItem = new Gson().fromJson(args[0].toString(), ChatItem.class);
                    if (chatUserItem != null) {
                        Log.d(TAG, "chetlister : " + chatUserItem.getMessage());
                        viewModel.chatAdapter.addSingleChat(chatUserItem);
                        binding.rvChat.scrollToPosition(0);
                        isSend = true;
                    } else {
                        Log.d(TAG, "lister : chet obj null");
                    }
                });
            }
        }
    };
    private String picturePath;
    private Uri selectedImage;

    @Override
    protected void onStart() {
        super.onStart();
        isOPEN = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        getWindow().setStatusBarColor(Color.parseColor("#292132"));
        Log.d(TAG, "onCreate: dfgndgngdngfnnrhnyf");
        MySocketManager.getInstance().addChatHandler(chatHandler);

        sessionManager = new SessionManager(this);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new ChatViewModel()).createFor()).get(ChatViewModel.class);

        binding.setViewmodel(viewModel);
        viewModel.chatAdapter.initLocalUser(sessionManager.getUser().getImage(), sessionManager.getUser().getAvatarFrameImage());
        viewModel.chatAdapter.initLocalUserId(sessionManager.getUser().getId());

        binding.backimg.setScaleX(isRTL(this) ? -1 : 1);
        binding.backimg.setOnClickListener(v -> {
            onBackPressed();
        });
        Intent intent = getIntent();

        String chatRootStr = intent.getStringExtra(Const.CHATROOM);
        if (chatRootStr != null && !chatRootStr.isEmpty()) {
            ChatUserListRoot.ChatUserItem chatRoot = new Gson().fromJson(chatRootStr, ChatUserListRoot.ChatUserItem.class);
            otherUserId = chatRoot.getUserId();
            userApiCall.getGuestProfile(chatRoot.getUserId(), new UserApiCall.OnGuestUserApiListner() {
                @Override
                public void onUserGetted(GuestProfileRoot.User user) {
                    guestUser = user;
                    if (!isFinishing()) {
                        binding.imgUser.setUserImage(guestUser.getImage(), guestUser.getAvatarFrameImage(), 10);
                    }
                }

                @Override
                public void onFailure() {

                }
            });

//            binding.rvChat.smoothScrollToPosition(0);
            if (!isFinishing()) {
                binding.imgUser.setUserImage(chatRoot.getImage(), chatRoot.getAvatarFrameImage(), 10);
            }
            binding.tvUserNamew.setText(chatRoot.getName());
            viewModel.chatAdapter.initGuestUser(chatRoot.getImage(), chatRoot.getAvatarFrameImage());

            viewModel.chatTopic = chatRoot.getTopic();

            viewModel.lastMessageId.observe(this, lastMessageId -> {
                if (lastMessageId != null) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("messageId", lastMessageId);
                        MySocketManager.getInstance().getSocket().emit("messageReadStatus", jsonObject);
                        Log.d(TAG, "onCreate: ====message event emitted... " + jsonObject);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

//            viewModel.messageIds.observe(this, messageIds -> {
//                if (messageIds != null && !messageIds.isEmpty()) {
//                    for (String messageId : messageIds) {
//                        JSONObject jsonObject = new JSONObject();
//                        try {
//                            jsonObject.put("messageId", messageId);
//                            MySocketManager.getInstance().getSocket().emit("messageReadStatus", jsonObject);
//                            Log.d(TAG, "onCreate: Emitting messageReadStatus for ID: " + messageId);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });




            initListener();
            viewModel.getOldChat(false);

        }

        String userStr = intent.getStringExtra(Const.USER);
        if (userStr != null && !userStr.isEmpty()) {
            guestUser = new Gson().fromJson(userStr, GuestProfileRoot.User.class);
            if (!isFinishing()) {
                binding.imgUser.setUserImage(guestUser.getImage(), guestUser.getAvatarFrameImage(), 10);
            }
            binding.tvUserNamew.setText(guestUser.getName());

            userApiCall.createChatTopic(sessionManager.getUser().getId(), guestUser.getUserId(), topic -> {
                viewModel.chatTopic = topic;
                initListener();
                viewModel.getOldChat(false);
            });
        }

        viewModel.isLodingComplete.observe(this, aBoolean -> {
            if (aBoolean) {
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
                if (viewModel.start == 0) {
                    Log.d(TAG, "onCreate: isLodingComplete.observe == " + viewModel.chatAdapter.getItemCount());
                    binding.rvChat.postDelayed(() -> {
                        binding.rvChat.scrollToPosition(0);
                    }, 100);
                }
            }
        });


    }

    private void initListener() {
        binding.imgUser.setOnClickListener(v -> {
            if (guestUser != null) {
                startActivity(new Intent(this, GuestActivity.class).putExtra(Const.USERID, guestUser.getUserId()));
            }
        });

        binding.etChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                Log.d("TAG", "afterTextChanged: " + charSequence.toString());
                viewModel.sendBtnEnable.postValue(!charSequence.toString().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.tvSend.setOnClickListener(v -> {
            String message = binding.etChat.getText().toString().trim();

            if (message.isEmpty()) {
                Toast.makeText(this, R.string.enter_valid_message, Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("senderId", sessionManager.getUser().getId());
                jsonObject.put("messageType", "message");
                jsonObject.put("topic", viewModel.chatTopic);
                jsonObject.put("message", message);
                Log.d(TAG, "initListner: send chat " + jsonObject);
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_CHAT, jsonObject);
                binding.etChat.setText("");

                isSend = false;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });


        viewModel.chatAdapter.setOnChatItemClickLister((chatDummy, position) -> {
//            new BottomSheetMessageDetails(this, chatDummy, () -> {
//                viewModel.deleteChat(chatDummy, position);
//            });


            Dialog dialog = new Dialog(this);
            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
            dialog.setContentView(R.layout.delete_popup);

            TextView textView = dialog.findViewById(R.id.tvTitle);
            textView.setText("Are you sure You want to delete this Message?");

            Button yes = dialog.findViewById(R.id.yes);
            Button no = dialog.findViewById(R.id.no);

            yes.setOnClickListener(v -> {
                viewModel.deleteChat(chatDummy,position);
                dialog.dismiss();
            });
            no.setOnClickListener(v -> dialog.dismiss());
            dialog.show();


        });


        binding.swipeRefresh.setOnRefreshListener(refreshLayout -> {
            viewModel.getOldChat(true);
        });

    }

    public void onClickVideoCall(View view) {
        if (guestUser == null) return;
        if (sessionManager.getUser().getGender().equals("Male")) {
            if (sessionManager.getUser().getDiamond() < sessionManager.getSetting().getMaleCallCharge()) {
                Toast.makeText(this, "Insufficient coins", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, CallRequestActivity.class).putExtra(Const.USER, new Gson().toJson(guestUser)).putExtra("type", guestUser.getGender()).putExtra("random",false));
            }
        }

        if (sessionManager.getUser().getGender().equals("Female")) {
            if (sessionManager.getUser().getDiamond() < sessionManager.getSetting().getFemaleCallCharge()) {
                Toast.makeText(this, "Insufficient coins", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, CallRequestActivity.class).putExtra(Const.USER, new Gson().toJson(guestUser)).putExtra("type", guestUser.getGender()).putExtra("random",false));
            }
        }
    }


    public void onClickCamara(View view) {
        choosePhoto();
    }

    public void openGallery(Context context) {
        try {
            startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI), RESULT_LOAD_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MySocketManager.getInstance().removeChatHandler(chatHandler);
        isOPEN = false;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
                choosePhoto();
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
        }
    }

    private void choosePhoto() {

        if (checkPermission()) {
            openGallery(this);
        } else {
            requestPermission();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            startCropActivity(data.getData());

            Glide.with(this)
                    .load(selectedImage)
                    .placeholder(R.drawable.ic_user_place).error(R.drawable.ic_user_place)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imageview);
//            binding.imageview.setAdjustViewBounds(true);

            picturePath = getRealPathFromURI(selectedImage);

            isSend = false;
//            uploadImage();
        } else if (requestCode == 69 && resultCode == -1) {
            handleCropResult(data);
        }
        if (resultCode == 96) {
            handleCropError(data);
        }

    }

    public void startCropActivity(@androidx.annotation.NonNull Uri uri) {
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME + ".png"))).useSourceImageAspectRatio();
        UCrop.Options options = new UCrop.Options();
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.pink));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.pink));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.blacklight));
        options.setToolbarColor(ContextCompat.getColor(this, R.color.blacklight));
        options.setCropFrameColor(ContextCompat.getColor(this, R.color.blackpure));
        options.setDimmedLayerColor(ContextCompat.getColor(this, R.color.blackpure));
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        uCrop.withOptions(options);
        uCrop.start(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

            uploadImage();

        } else {
            Toast.makeText(this, "toast_cannot_retrieve_cropped_image", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCropError(@androidx.annotation.NonNull Intent result) {
        Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e("TAG", "handleCropError: ", cropError);
            Toast.makeText(this, cropError.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, getString(R.string.toast_unexpected_error), Toast.LENGTH_SHORT).show();
    }

    private void uploadImage() {
        binding.lytImage.setVisibility(View.VISIBLE);
        if (picturePath != null) {
            File file = new File(picturePath);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            RequestBody messageTypeBody = RequestBody.create(MediaType.parse("text/plain"), "image");
            RequestBody topicBody = RequestBody.create(MediaType.parse("text/plain"), viewModel.chatTopic);
            RequestBody senderIdBody = RequestBody.create(MediaType.parse("text/plain"), sessionManager.getUser().getId());

            HashMap<String, RequestBody> map = new HashMap<>();

            map.put("messageType", messageTypeBody);
            map.put("topic", topicBody);
            map.put("senderId", senderIdBody);

            Call<UploadImageRoot> call = RetrofitBuilder.create().uploadChatImage(map, body);
            call.enqueue(new Callback<UploadImageRoot>() {
                @Override
                public void onResponse(Call<UploadImageRoot> call, Response<UploadImageRoot> response) {
                    if (response.code() == 200) {
                        if (response.body().isStatus()) {
                            binding.lytImage.setVisibility(View.GONE);
                            try {
                                JSONObject jsonObject = new JSONObject();

                                jsonObject.put("senderId", sessionManager.getUser().getId());
                                jsonObject.put("messageType", "image");
                                jsonObject.put("topic", viewModel.chatTopic);
                                jsonObject.put("message", "image");
                                jsonObject.put("date", response.body().getChat().getDate());
                                jsonObject.put("image", response.body().getChat().getImage());
                                Log.d(TAG, "initListner: send chat " + jsonObject);
                                MySocketManager.getInstance().getSocket().emit(Const.EVENT_CHAT, jsonObject);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<UploadImageRoot> call, Throwable t) {

                }
            });
        }
    }

    public String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public void onClickReport(View view) {
        if (guestUser == null) return;

        new BottomSheetReport_option(ChatActivity.this, new BottomSheetReport_option.OnReportedListener() {
            @Override
            public void onReported() {
                new BottomSheetReport_g(ChatActivity.this, guestUser.getUserId(), new BottomSheetReport_g.OnReportedListner() {
                    @Override
                    public void onReported() {
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.customtoastlyt));
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                    }
                });
            }

            @Override
            public void onBlocked() {
               userApiCall.blockUnblock(guestUser.getId(), new UserApiCall.OnBlockUnblockListner() {
                   @Override
                   public void onBlockSuccess() {
                       finish();
                   }

                   @Override
                   public void onUnblockSuccess() {

                   }
               });
            }

        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        viewModel.getOldChat(false);
    }
}