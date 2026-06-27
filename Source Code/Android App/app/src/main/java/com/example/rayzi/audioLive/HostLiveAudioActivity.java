package com.example.rayzi.audioLive;

import static android.provider.MediaStore.MediaColumns.DATA;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.HostAPICall;
import com.example.rayzi.MainApplication;
import com.example.rayzi.R;
import com.example.rayzi.RayziUtils;
import com.example.rayzi.SessionManager;
import com.example.rayzi.activity.GotoLiveActivityNew;
import com.example.rayzi.activity.SettingActivity;
import com.example.rayzi.adapter.GiftReceiveAdapter;
import com.example.rayzi.agora.AgoraBaseActivity;
import com.example.rayzi.agora.RtcStatsView;
import com.example.rayzi.agora.stats.RemoteStatsData;
import com.example.rayzi.agora.stats.StatsData;
import com.example.rayzi.agora.token.RtcTokenBuilderSample;
import com.example.rayzi.audioLive.reactions.BottomSheetReactions;
import com.example.rayzi.audioLive.reactions.ReactionsViewModel;
import com.example.rayzi.bottomsheets.BottomSheetAudioRoomChangePasscode;
import com.example.rayzi.bottomsheets.BottomSheetAudioRoomName;
import com.example.rayzi.bottomsheets.BottomSheetAudioRoomSetting;
import com.example.rayzi.bottomsheets.BottomSheetAudioRoomWelcomeMsg;
import com.example.rayzi.bottomsheets.BottomSheetAudioRoomWheatMode;
import com.example.rayzi.bottomsheets.BottomSheetBannedList;
import com.example.rayzi.bottomsheets.BottomSheetBlockTime;
import com.example.rayzi.bottomsheets.UserProfileBottomSheet;
import com.example.rayzi.databinding.ActivityHostLiveAudioBinding;
import com.example.rayzi.databinding.BottomSheetAudioroomSettingsBinding;
import com.example.rayzi.databinding.BottomSheetOnlineProfileBinding;
import com.example.rayzi.databinding.ItemSeatBinding;
import com.example.rayzi.emoji.EmojiBottomsheetFragment;
import com.example.rayzi.emoji.UserSelectableClass;
import com.example.rayzi.liveGame.bottomsheet.BottomSheetGameCasino;
import com.example.rayzi.liveGame.bottomsheet.BottomSheetGameList;
import com.example.rayzi.liveGame.bottomsheet.BottomSheetGameTeenPatti;
import com.example.rayzi.liveGame.dialog.DialogGame;
import com.example.rayzi.liveStreamming.LiveSummaryActivity;
import com.example.rayzi.modelclass.GiftRoot;
import com.example.rayzi.modelclass.GuestProfileRoot;
import com.example.rayzi.modelclass.LiveStramComment;
import com.example.rayzi.modelclass.LiveStreamRoot;
import com.example.rayzi.modelclass.RestResponse;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.pk.HostPKLiveActivity;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;
import com.example.rayzi.popups.PopupBuilder;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.socket.AudioRoomHandler;
import com.example.rayzi.socket.MySocketManager;
import com.example.rayzi.socket.SocketConnectHandler;
import com.example.rayzi.utils.FloatingButtonService;
import com.example.rayzi.viewModel.EmojiSheetViewModel;
import com.example.rayzi.viewModel.HostLiveViewModel;
import com.example.rayzi.viewModel.ViewModelFactory;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGADynamicEntity;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HostLiveAudioActivity extends AgoraBaseActivity {

    public static final String TAG = "hostliveactivity";
    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    private static final int REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE = 1002;
    public static int hostPosition = -1;
    ActivityHostLiveAudioBinding binding;
    SessionManager sessionManager;
    JSONArray jsonArray;
    JSONArray finalArray;
    SeatAdapter seatAdapter;
    boolean isspeak = false;
    GridLayoutManager gridLayoutManager;
    JSONArray blockedUsersList = new JSONArray();
    UserProfileBottomSheet userProfileBottomSheet;
    long animationDurationMillis;
    EmojiBottomsheetFragment emojiBottomsheetFragment;
    List<PkAudioLiveUserRoot.UsersItem.SeatItem> bookedSeatItemList = new ArrayList<>();
    List<GiftRoot.GiftItem> giftList = new ArrayList<>();
    GiftReceiveAdapter giftReceiveAdapter = new GiftReceiveAdapter();
    int uuid;
    int selfPosition = 0;
    private int userListPosition = 0;
    private HostLiveViewModel viewModel;
    private EmojiSheetViewModel giftViewModel;
    private PkAudioLiveUserRoot.UsersItem liveUser;

    int finalCoin = 0;
    int updateCoin;
    int coin;
    HostAPICall hostAPICall;
    JSONArray blockUserList = new JSONArray();
    SocketConnectHandler socketConnectHandler = new SocketConnectHandler() {
        @Override
        public void onConnect() {

        }

        @Override
        public void onDisconnect() {

        }

        @Override
        public void onReconnecting() {

        }

        @Override
        public void onReconnected(Object[] args) {
            Log.d(TAG, "onReconnected: " + args[0].toString());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
                jsonObject.put("userId", sessionManager.getUser().getId());
                MySocketManager.getInstance().getSocket().emit(Const.LIVE_REJOIN, jsonObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    };
    private PkAudioLiveUserRoot.UsersItem.SeatItem viewerListItem = null;
    AudioRoomHandler audioRoomHandler = new AudioRoomHandler() {


        @Override
        public void onTotalRoomcoins(Object[] args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: ====host roomcoin " + args[0].toString());
                    binding.tvRcoins.setText(args[0].toString());
                }
            });
        }

        @Override
        public void onAudioLiveHostRemove(Object[] args) {

        }

        @Override
        public void onHostEnter(Object[] args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: =====enter host");
                }
            });
        }

        @Override
        public void onLiveEndByEnd(Object[] args) {
            if (args[0] != null) {
                runOnUiThread(() -> {

                    removeRtcVideo(0, true);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("liveRoom", liveUser.getLiveStreamingId());
                        jsonObject.put("liveHostRoom", sessionManager.getUser().getId());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    MySocketManager.getInstance().getSocket().emit("liveHostEnd", jsonObject);
                    PopupBuilder popupBuilder = new PopupBuilder(HostLiveAudioActivity.this);
                    popupBuilder.showLiveEndPopup(getString(R.string.your_live_session_end_by_admin_text), getString(R.string.dismiss), () -> {

                        startActivity(new Intent(HostLiveAudioActivity.this, LiveSummaryActivity.class).putExtra(Const.DATA, liveUser.getLiveStreamingId()));
                        finish();
                        Toast.makeText(HostLiveAudioActivity.this, R.string.end_live_video, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "liveEndByEnd: liveEndByEnd" + args[0].toString());

                    });

                });
            }

        }

        @Override
        public void onUserCoinUpdate(Object[] args) {
            if (args[0] != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UserRoot.User user = sessionManager.getUser();
                        user.setDiamond(Integer.parseInt(args[0].toString()));
                        sessionManager.saveUser(user);
                    }
                });
            }
        }

        @Override
        public void onComment(Object[] args) {
            if (args[0] != null) {
                Log.d(TAG, "onComment: " + args[0].toString());
                runOnUiThread(() -> {
                    String data = args[0].toString();
                    if (!data.isEmpty()) {
                        LiveStramComment liveStramComment = new Gson().fromJson(data.toString(), LiveStramComment.class);
                        if (liveStramComment != null) {
                            viewModel.liveStramCommentAdapter.addSingleComment(liveStramComment);
                            scrollAdapterLogic();
                        }
                    }
                });
            }
        }

        private Queue<JSONObject> giftQueue = new LinkedList<>();
        private boolean isGiftDisplaying = false;
        long timeStamp;

        @Override
        public void onGift(Object[] args) {

            runOnUiThread(() -> {
                if (args.length > 0 && args[0] != null) {
                    String data = args[0].toString();
                    Log.d(TAG, "onGift: ======" + args[0]);
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        if (jsonObject.get("gift") != null) {
                            // Add the gift data to the queue
                            giftQueue.add(jsonObject);
                            // Start processing if not already running
                            if (!isGiftDisplaying) {
                                processNextGift();
                            }
                        }

                        coin = jsonObject.getInt("coin");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // Handle sender details (args[1])
                if (args.length > 1 && args[1] != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(args[1].toString());
                        UserRoot.User user = new Gson().fromJson(jsonObject.toString(), UserRoot.User.class);
                        if (user != null && user.getId().equals(sessionManager.getUser().getId())) {
                            sessionManager.saveUser(user);
                            giftViewModel.localUserCoin.setValue(user.getDiamond());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // Handle host details (args[2])
                if (args.length > 2 && args[2] != null) {
                    try {
                        Log.d(TAG, "onGift2: =====" + args[2].toString());
                        JSONObject jsonObject = new JSONObject(args[2].toString());
                        UserRoot.User host = new Gson().fromJson(jsonObject.toString(), UserRoot.User.class);
                        if (host != null && host.getId().equals(sessionManager.getUser().getId())) {
//                            updateCoin = updateCoin + coin;
//                            sessionManager.saveInt("updateCoin",updateCoin);
//                            binding.tvRcoins.setText(String.valueOf(host.getRCoin()));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }


        private void processNextGift() {
            if (!giftQueue.isEmpty()) {
                isGiftDisplaying = true;
                JSONObject giftJson = giftQueue.poll(); // Get the next gift
                try {

                    long receivedTimeStamp = giftJson.getLong("timeStamp");

                    if (timeStamp != receivedTimeStamp) {
                        timeStamp = receivedTimeStamp;

                        GiftRoot.GiftItem giftData = new Gson().fromJson(giftJson.get("gift").toString(), GiftRoot.GiftItem.class);
                        if (giftData != null) {
                            String finalGiftLink = null;
                            List<GiftRoot.GiftItem> giftItemList = sessionManager.getGiftsList(giftData.getCategory());
                            for (GiftRoot.GiftItem item : giftItemList) {
                                if (giftData.getId().equals(item.getId())) {
                                    finalGiftLink = BuildConfig.BASE_URL + item.getImage();
                                    break;
                                }
                            }
                            String name = giftJson.getString("userName");
                            giftData.setName(name);
                            String receivername = giftJson.getString("receiverUserName");
                            giftData.setReceiverUserName(Collections.singletonList(receivername));

                            if (giftData.getType() == 2) {
                                if (finalGiftLink.contains(".webp")) {
                                    handleImageGift(finalGiftLink, giftData);
                                } else {
                                    handleSVGAGift(finalGiftLink, giftJson, giftData);
                                }
                            } else {
                                handleImageGift(finalGiftLink, giftData);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                isGiftDisplaying = false;
                if (!giftQueue.isEmpty()) {
                    processNextGift();
                }

            } else {
                isGiftDisplaying = false;
            }
        }

        private void handleImageGift(String giftLink, GiftRoot.GiftItem giftData) {
            giftList.clear();
            giftList.add(giftData);
            giftReceiveAdapter.addData(giftList);

            new Handler().postDelayed(() -> {
                giftReceiveAdapter.remove(giftData);
                giftList.remove(giftData);
                processNextGift(); // Start the next gift immediately
            }, 3000); // Use the same duration as in your original code
        }

        private void handleSVGAGift(String giftLink, JSONObject jsonObject, GiftRoot.GiftItem giftData) {
            binding.svgaImage.setVisibility(View.VISIBLE);
            SVGAImageView imageView = binding.svgaImage;
            SVGAParser parser = new SVGAParser(HostLiveAudioActivity.this);

            try {
                parser.decodeFromURL(new URL(giftLink), new SVGAParser.ParseCompletion() {
                    @Override
                    public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                        SVGADynamicEntity dynamicEntity = new SVGADynamicEntity();
                        dynamicEntity.setDynamicImage(giftLink, "99");
                        SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
                        imageView.setImageDrawable(drawable);
                        imageView.startAnimation();

                        Glide.with(HostLiveAudioActivity.this)
                                .load(RayziUtils.getImageFromNumber(giftData.getCount()))
                                .into(binding.imgSvgaGiftCount);

                        String name = jsonObject.optString("userName", "User");

                        String receivername = jsonObject.optString("receiverUserName", "User");

                        String receiverNames = receivername
                                .replace("[", "")
                                .replace("]", "");
                        binding.tvSvgaGiftUserName.setText(name + " Sent a gift to" + receiverNames);
                        binding.lytSvgagift.setVisibility(View.VISIBLE);

                        long animationDurationMillis = svgaVideoEntity.getFrames() / svgaVideoEntity.getFPS() * 1000L;

                        new Handler().postDelayed(() -> {
                            binding.svgaImage.setVisibility(View.GONE);
                            binding.lytSvgagift.setVisibility(View.GONE);
                            binding.svgaImage.clear();
                            processNextGift(); // Start the next gift immediately
                        }, animationDurationMillis);
                    }

                    @Override
                    public void onError() {
                        processNextGift(); // Skip to the next gift on error
                    }
                }, null);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        private Queue<JSONObject> entryQueue = new LinkedList<>();
        private boolean isEntryEffectRunning = false;


        @Override
        public void onView(Object[] args) {
            HostLiveAudioActivity.this.runOnUiThread(() -> {
                Log.d(TAG, "onView: viewListner " + args.toString());

                if (args[0] != null) {
                    try {
                        jsonArray = new JSONArray(args[0].toString());
                        finalArray = new JSONArray();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getBoolean("isAdd")) {
                                finalArray.put(jsonObject);
                            }
                        }
                        viewModel.liveViewUserAdapter.addData(finalArray);
                        binding.tvViewUserCount.setText(String.valueOf(finalArray.length()));
                        Log.d(TAG, "views2 : " + jsonArray);
                        binding.tvNoOneJoined.setVisibility(jsonArray.length() > 0 ? View.GONE : View.VISIBLE);

                    } catch (JSONException e) {
                        Log.d(TAG, "207: ");
                        e.printStackTrace();
                    }

//                    try {
//                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put("blocked", blockedUsersList);
//                        jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
//                        MySocketManager.getInstance().getSocket().emit(Const.EVENT_BLOCK, jsonObject);
//                        Log.d(TAG, "onView: BBKK blocklist " + blockedUsersList.length());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }

                if (args[1] != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(args[1].toString());
                        if (jsonObject.has("entrySvga") && jsonObject.has("avatarFrame") && jsonObject.has("image")) {
                            Log.d(TAG, "onView: New Entry Detected: " + jsonObject.toString());
                            entryQueue.add(jsonObject);
                            triggerNextEntryEffect();  // Trigger the next effect
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            });

        }

        private void triggerNextEntryEffect() {
            if (isEntryEffectRunning || entryQueue.isEmpty() || isFinishing()) return;

            isEntryEffectRunning = true;
            JSONObject jsonObject = entryQueue.poll();  // Get next user entry

            try {
                String avatarFrame = jsonObject.getString("avatarFrame");
                String entrySvga = jsonObject.getString("entrySvga");
                String userImage = jsonObject.getString("image");
                String userName = jsonObject.getString("userName");
                boolean isUserBackgroundLive = jsonObject.getBoolean("isUserBackgroundLive");

                if (isUserBackgroundLive) {
                    isEntryEffectRunning = false;
                    triggerNextEntryEffect();
                    return;
                }

                binding.svgImage.clear();
                binding.layEntry.setVisibility(View.VISIBLE);

                SVGAImageView imageView = binding.svgImage;
                SVGAParser parser = new SVGAParser(HostLiveAudioActivity.this);

                parser.decodeFromURL(new URL(entrySvga != null && !entrySvga.isEmpty() ? BuildConfig.BASE_URL + entrySvga : ""),
                        new SVGAParser.ParseCompletion() {
                            @Override
                            public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                                SVGADynamicEntity dynamicEntity = new SVGADynamicEntity();
                                dynamicEntity.setDynamicImage(BuildConfig.BASE_URL + entrySvga, "entry_effect");
                                SVGADrawable drawable = new SVGADrawable(svgaVideoEntity, dynamicEntity);
                                imageView.setImageDrawable(drawable);
                                imageView.startAnimation();

                                animationDurationMillis = svgaVideoEntity.getFrames() / svgaVideoEntity.getFPS() * 1000L;

                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    binding.svgaImage.setVisibility(View.GONE);
                                    binding.layEntry.setVisibility(View.GONE);
                                    binding.svgaImage.clear();

                                    isEntryEffectRunning = false;
                                    triggerNextEntryEffect();
                                    // Trigger next effect after animation ends
                                }, animationDurationMillis);
                            }

                            @Override
                            public void onError() {
                                isEntryEffectRunning = false;
                                binding.layEntry.setVisibility(View.GONE);
                                triggerNextEntryEffect();  // Continue even if an error occurs
                            }
                        }, null
                );

                // Update UI for user details
                binding.userName.setText(userName);
                Glide.with(HostLiveAudioActivity.this).load(userImage).circleCrop().into(binding.userImage);
                Glide.with(HostLiveAudioActivity.this).load(avatarFrame != null && !avatarFrame.isEmpty() ? BuildConfig.BASE_URL + avatarFrame : "").into(binding.avatarFrameImage);

                Animation animation = AnimationUtils.loadAnimation(HostLiveAudioActivity.this, R.anim.slide_in_right);
                animation.setFillAfter(true);
                binding.nameLyt.startAnimation(animation);

            } catch (JSONException | MalformedURLException e) {
                e.printStackTrace();
                isEntryEffectRunning = false;
                binding.layEntry.setVisibility(View.GONE);
                triggerNextEntryEffect();  // Proceed even if something fails
            }
        }

        @Override
        public void onAddRequested(Object[] args) {

        }

        @Override
        public void onDeclineInvite(Object[] args) {

        }

        @Override
        public void onAddParticipants(Object[] args) {

        }

        @Override
        public void onLessParticipants(Object[] args) {

        }

        @Override
        public void onMuteSeat(Object[] args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args[0] != null) {
                        Log.d("mute======", "run: ===muteseat.." + args[0].toString());
                        try {
                            JSONObject jsonObject = new JSONObject(args[0].toString());
                            if (jsonObject.getInt("position") == -1) {
                                int mute = (jsonObject.getInt("mute"));
//                                viewModel.isMuted = mute != 0;

                                if (jsonObject.getInt("mute") == 1 || jsonObject.getInt("mute") == 2) {
                                    binding.ivMute.setVisibility(View.VISIBLE);
                                } else {
                                    binding.ivMute.setVisibility(View.GONE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        @Override
        public void onLockSeat(Object[] args) {

        }

        @Override
        public void onAllSeatLock(Object[] args) {

        }

        @Override
        public void onChangeTheme(Object[] args) {
            runOnUiThread(() -> {
                if (args[0] != null) {
                    Log.d(TAG, "call: changetheme" + args[0]);

                    try {
                        JSONObject jsonObject = new JSONObject(args[0].toString());
                        String image = jsonObject.getString("background");
                        Glide.with(HostLiveAudioActivity.this).load(BuildConfig.BASE_URL + image).into(binding.mainImg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        @Override
        public void onSeat(Object[] args) {
            Log.d(TAG, "onSeat: args[0] =================== " + Arrays.toString(args));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args[0] != null) {
                        String data = args[0].toString();
                        JsonParser parser = new JsonParser();
                        JsonElement mJson = parser.parse(data);
                        Log.d(TAG, "run: ====" + data);
                        Gson gson = new Gson();
                        liveUser = gson.fromJson(mJson, PkAudioLiveUserRoot.UsersItem.class);
                        bookedSeatItemList = liveUser.getSeat();
                        binding.tvRcoins.setText(String.valueOf(liveUser.getRCoin()));

                        Log.d("mute======", "run: ====seat mute + " + liveUser.getAudioRoomConfig().isHostMute());

                        int mute = liveUser.getAudioRoomConfig().isHostMute();
//                        viewModel.isMuted = mute != 0;
//                        rtcEngine().muteLocalAudioStream(viewModel.isMuted);

                        if (viewModel.isMuted) {
                            binding.ivMute.setVisibility(View.VISIBLE);
//                            binding.btnMute.setImageDrawable(ContextCompat.getDrawable(HostLiveAudioActivity.this, R.drawable.ic_mute));
                        } else {
                            binding.ivMute.setVisibility(GONE);
//                            binding.btnMute.setImageDrawable(ContextCompat.getDrawable(HostLiveAudioActivity.this, R.drawable.ic_unmute));
                        }

//                        if (liveUser.getAudioRoomConfig().isHostMute() == 1) {
//                            binding.ivMute.setVisibility(VISIBLE);
//                        } else {
//                            binding.ivMute.setVisibility(GONE);
//                        }


                        if (liveUser.getSeat().size() >= 15) {
                            gridLayoutManager.setSpanCount(5); // Change to 5 columns when item count is 16 or more
                        } else {
                            gridLayoutManager.setSpanCount(4); // Default back to 4 columns for less than 16 items
                        }

                        seatAdapter.updateData(liveUser.getSeat());

                        //todo first time seat set
                        // updateSeat(liveUser.getSeat());
                    }
                }
            });

        }

        @Override
        public void onBlock(Object[] args) {
            Log.d(TAG, "onBlock: " + args[0].toString());
            runOnUiThread(() -> {
                if (args[0] != null) {
                    Object data = args[0];
                    try {
                        JSONObject jsonObject = new JSONObject(data.toString());
                        JSONArray blockedList = jsonObject.getJSONArray("blocked");
                        for (int i = 0; i < blockedList.length(); i++) {
                            Log.d(TAG, "block user : " + blockedList.get(i).toString());
                            if (blockedList.get(i).toString().equals(sessionManager.getUser().getId())) {
                                Toast.makeText(HostLiveAudioActivity.this, R.string.you_are_blocked_by_host, Toast.LENGTH_SHORT).show();
                                new Handler(Looper.myLooper()).postDelayed(() -> confirmedEndLive(), 500);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }


        @Override
        public void onBanned(Object[] args) {
            Log.d(TAG, "onBlock: " + args[0].toString());
            runOnUiThread(() -> {
                if (args[0] != null) {
                    Object data = args[0];
                    try {
                        JSONObject jsonObject = new JSONObject(data.toString());
                        JSONArray blockedList = jsonObject.getJSONArray("blocked");
                        for (int i = 0; i < blockedList.length(); i++) {
                            Log.d(TAG, "block user : " + blockedList.get(i).toString());
                            if (blockedList.get(i).toString().equals(sessionManager.getUser().getId())) {
                                Toast.makeText(HostLiveAudioActivity.this, R.string.you_are_blocked_by_host, Toast.LENGTH_SHORT).show();
                                new Handler(Looper.myLooper()).postDelayed(() -> confirmedEndLive(), 500);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onBannedUserlist(Object[] args) {
            if (args[0] != null) {
                try {
                    blockUserList = new JSONArray(args[0].toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Log.d(TAG, "createGlobal: event blockeduserlist args0 " + args[0].toString());
            }
        }

        @Override
        public void onBlockuseralert(Object[] args) {
            Log.d(TAG, "onBlock: " + args[0].toString());
            runOnUiThread(() -> {
                Toast.makeText(HostLiveAudioActivity.this, getString(R.string.you_are_blocked_by_host), Toast.LENGTH_SHORT).show();
                confirmedEndLive();
            });
        }

        @Override
        public void onGetUser(Object[] args) {
            Log.d(TAG, "onGetUser: " + args[0].toString());
            runOnUiThread(() -> {
                if (args[0] != null) {
                    String data = args[0].toString();
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = parser.parse(data);
                    Gson gson = new Gson();
                    GuestProfileRoot.User userData = gson.fromJson(mJson, GuestProfileRoot.User.class);

                    if (userData != null) {
                        doUserTask(userData, userListPosition, viewerListItem);
                    }
                    customDialogClass.dismiss();
                }
            });
        }

        @Override
        public void onGetUser2(Object[] args) {
            runOnUiThread(() -> {
                if (args[0] != null) {
                    String data = args[0].toString();
//                    Log.d(TAG, "initLister: usr sty1 " + data);
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = parser.parse(data);
//                    Log.d(TAG, "initLister: usr sty2 " + mJson);
                    Gson gson = new Gson();
                    GuestProfileRoot.User userData = gson.fromJson(mJson, GuestProfileRoot.User.class);

                    if (userData != null) {
                        if (!isFinishing()) {
                            if (userData.getUserId().equals(liveUser.getLiveUserId())) {
                                userProfileBottomSheet.show(false, userData, liveUser.getLiveStreamingId(), false);
                            } else {
                                userProfileBottomSheet.show(false, userData, "", false);
                            }
                        }
                        customDialogClass.dismiss();
                    }
                }
            });

        }

        @Override
        public void onInvite(Object[] args) {

        }

        @Override
        public void onLiveEnd(Object[] argr) {

        }

        @Override
        public void onReactionReceived(Object[] args1) {
            handleReactionReceived(args1);
        }

        @Override
        public void onRoomNameChange(Object[] args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject(args[0].toString());
                        binding.tvName.setText(jsonObject.getString("roomName"));
                        liveUser.setRoomName(jsonObject.getString("roomName"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        @Override
        public void onWelcomeMessage(Object[] args) {

        }

        @Override
        public void onRoomImageChange(Object[] args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    liveUser.setRoomImage(args[0].toString());
                    Glide.with(HostLiveAudioActivity.this).load(args[0].toString()).into(binding.imgProfile);
                }
            });
        }

    };
    private RtcStatsView rtcStatsView;
    private BottomSheetReactions bottomSheetReactions;
    private String picturePath;

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void handleReactionReceived(Object[] args1) {
        Log.d(TAG, "onReactionRecived: ");

        runOnUiThread(() -> {
            if (args1[0] != null) {
                try {
                    JSONObject jsonObject = new JSONObject(args1[0].toString());
                    if (jsonObject.getInt("position") == -1) {
                        UserRoot.User user = new Gson().fromJson(jsonObject.getString("user"), UserRoot.User.class);
                        LiveStramComment liveStramComment = new LiveStramComment("", user, false, liveUser.getLiveStreamingId(), jsonObject.getString("image"), "reaction", "");
                        viewModel.liveStramCommentAdapter.addSingleComment(liveStramComment);
                        scrollAdapterLogic();

                    } else if (jsonObject.getInt("position") == -2) {
                        setUpReaction(jsonObject.getString("image"), binding.imgHostReaction, 7000);
                    } else {

                        RecyclerView.LayoutManager layoutManager = binding.rvSeat.getLayoutManager();

                        if (layoutManager instanceof LinearLayoutManager linearLayoutManager) {

                            int position = jsonObject.getInt("position"); // Replace 0 with the position of the item you want

                            // Get the View at the specified position from the LayoutManager
                            View itemView = linearLayoutManager.findViewByPosition(position);

                            // Now you can use itemView to access the ViewBinding object if you have one
                            if (itemView != null) {
                                @NonNull ItemSeatBinding seatBinding = Objects.requireNonNull(DataBindingUtil.bind(itemView));
                                assert seatBinding != null;
                                PkAudioLiveUserRoot.UsersItem.SeatItem seatItem = seatAdapter.getList().get(position);
                                setUpReaction(jsonObject.getString("image"), seatBinding.imgHostReaction, 7000);
                            }
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void scrollAdapterLogic() {
        binding.rvComments.scrollToPosition(0);
    }

    @Override
    public void onBackPressed() {
        endLive();
    }

    private void endLive() {
        Log.d(TAG, "endLive: ma jay che ==== ");
        if (!isFinishing()) {
            new PopupBuilder(this).showLiveEndPopup(new PopupBuilder.OnMultButtonPopupLister() {
                @Override
                public void onClickCountinue() {
                    PkAudioLiveUserRoot.UsersItem.AudioRoomConfig audioRoomConfig = new PkAudioLiveUserRoot.UsersItem.AudioRoomConfig();
                    audioRoomConfig.setHostMute(viewModel.isMuted ? 1 : 1);
                    rtcEngine().muteLocalAudioStream(viewModel.isMuted);
                    Log.d(TAG, "onClickCancel: ===ishostmute" + viewModel.isMuted);
                    confirmedEndLive();
                }

                @Override
                public void onClickCancel() {
                    if (checkOverlayDisplayPermission()) {
                        sessionManager.saveBooleanValue("isHostKeep", true);
                        startService(new Intent(HostLiveAudioActivity.this, FloatingButtonService.class).putExtra("image", sessionManager.getUser().getImage()));
                        finish();
                    } else {
                        requestOverlayDisplayPermission();
                    }
                    if (rtcEngine() != null) {
                        rtcEngine().muteLocalAudioStream(viewModel.isMuted);
                    }
                    PkAudioLiveUserRoot.UsersItem.AudioRoomConfig audioRoomConfig = new PkAudioLiveUserRoot.UsersItem.AudioRoomConfig();
                    audioRoomConfig.setHostMute(viewModel.isMuted ? 1 : 0);
                    Log.d(TAG, "onClickCancel: ===ishostmute" + viewModel.isMuted);
                    liveUser.setAudioRoomConfig(audioRoomConfig);
                    sessionManager.saveLiveUserForBackground(liveUser);
                    sessionManager.setIsAudioRoomBackground(true);
                    sessionManager.setIsAudioRoomExit(false);
                }
            });
        }
    }

    private void confirmedEndLive() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("liveUserId", liveUser.getId());
            jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        MySocketManager.getInstance().getSocket().emit("audioLiveHostRemove", jsonObject);
        Log.d(TAG, "confirmedEndLive: ===audioLiveHostRemove");
        if (rtcEngine() != null) {
            rtcEngine().leaveChannel();
        }
        sessionManager.saveBooleanValue("isHostKeep", false);
        sessionManager.saveLiveUserForBackground(liveUser);
        sessionManager.setIsAudioRoomBackground(true);
        sessionManager.setIsAudioRoomExit(true);
        finish();
    }

//    private void confirmedEndLive() {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            Log.d(TAG, "onClickCountinue: liveUser.getLiveStreamingId() ================ " + liveUser.getLiveStreamingId());
//            Log.d(TAG, "onClickCountinue: sessionManager.getUser().getId() ================ " + sessionManager.getUser().getId());
//            jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
//            jsonObject.put("liveUserId", liveUser.getLiveUserId());
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
////        MySocketManager.getInstance().getSocket().emit("liveHostEnd", jsonObject);
//        Log.d(TAG, "onClickCountinue: emit kre che ============== " + Const.HOSTLIVEEND);
//        seatAdapter.clear();
////        startActivity(new Intent(HostLiveAudioActivity.this, LiveSummaryActivity.class).putExtra(Const.DATA, liveUser.getLiveStreamingId()));
//        finish();
//    }

    private void joinChannel() {
        try {
            rtcEngine().setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            String tkn = RtcTokenBuilderSample.main(liveUser.getChannel() + "audio", sessionManager.getSetting().getAgoraKey(), sessionManager.getSetting().getAgoraCertificate());
            rtcEngine().joinChannel(tkn, liveUser.getChannel() + "audio", "", liveUser.getAgoraUID());
            Log.d("fatal", "onCreate: audio live" + liveUser.getChannel());
            rtcEngine().enableAudioVolumeIndication(1000, 3, true); // atyare kon bole chhe ae detect karva mate
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startBroadcast() {

        Log.d(TAG, "startBroadcast: ");
        try {
            rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
            rtcEngine().enableAudio();
            rtcEngine().disableVideo();
//            SurfaceView surface = prepareRtcVideo(0, true);
//            mVideoGridContainer.addUserVideoSurface(0, surface, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_live_audio);

        MySocketManager.getInstance().addAudioRoomHandler(audioRoomHandler);
        MySocketManager.getInstance().addSocketConnectHandler(socketConnectHandler);

        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new HostLiveViewModel()).createFor()).get(HostLiveViewModel.class);
        giftViewModel = ViewModelProviders.of(this, new ViewModelFactory(new EmojiSheetViewModel()).createFor()).get(EmojiSheetViewModel.class);
        ReactionsViewModel reactionsViewModel = ViewModelProviders.of(this, new ViewModelFactory(new ReactionsViewModel()).createFor()).get(ReactionsViewModel.class);
        sessionManager = new SessionManager(this);
        hostAPICall = new HostAPICall(this, "audio");
        giftViewModel.initEmojiSheet(this);
        giftViewModel.getGiftCategory();
        binding.setViewModel(viewModel);

        emojiBottomsheetFragment = new EmojiBottomsheetFragment(true);

        bottomSheetReactions = new BottomSheetReactions(this);
        reactionsViewModel.loadReactions(bottomSheetReactions::loadData);

        userProfileBottomSheet = new UserProfileBottomSheet(this);
        binding.rvComments.scrollToPosition(0);
        viewModel.initLister();

        gridLayoutManager = new GridLayoutManager(this, 4);
        binding.rvSeat.setLayoutManager(gridLayoutManager);
        seatAdapter = new SeatAdapter(HostLiveAudioActivity.this, sessionManager);
        binding.rvSeat.setAdapter(seatAdapter);

        if (isMyServiceRunning()) {
            stopService(new Intent(HostLiveAudioActivity.this, FloatingButtonService.class));
        }

        Intent intent = getIntent();

        if (intent != null) {
            String data = intent.getStringExtra(Const.DATA);
            String privacy = intent.getStringExtra(Const.PRIVACY);

            if (data != null && !data.isEmpty()) {
                liveUser = new Gson().fromJson(data, PkAudioLiveUserRoot.UsersItem.class);
                if (liveUser != null) {
                    liveUser.setAgoraUID(1);
                    binding.tvRcoins.setText(String.valueOf(liveUser.getRCoin()));
                }

                if (sessionManager.getIsAudioRoomExit()) {
                    Call<RestResponse> call = RetrofitBuilder.create().getNotification(sessionManager.getUser().getId());
                    call.enqueue(new Callback<RestResponse>() {
                        @Override
                        public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {

                        }

                        @Override
                        public void onFailure(Call<RestResponse> call, Throwable t) {

                        }
                    });
                }

                assert liveUser != null;
                Log.d(TAG, "onCreate: live room id " + liveUser.getLiveStreamingId());
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("liveUserId", sessionManager.getUser().getId());
                    jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
                    if (sessionManager.getIsAudioRoomExit()) {
                        MySocketManager.getInstance().getSocket().emit(Const.EVENT_HOSTJOINAUDIOROOM, jsonObject);
                    }
                    Log.d(TAG, "onCreate: liveroomconnect emitted...");
                    Log.d(TAG, "onCreate: hostjoinaudioroom emitted...");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                boolean userfrombackround = getIntent().getBooleanExtra("backgroundroom", false);


                binding.tvName.setText(liveUser.getRoomName());
                RayziUtils.marqueeText(binding.tvName);

                binding.tvUniqueId.setText(getString(R.string.id) + sessionManager.getUser().getUniqueId());
                Glide.with(this).load(liveUser.getRoomImage()).apply(MainApplication.requestOptions).circleCrop().into(binding.imgProfile);

                binding.mainHostProfileImage.setUserImage(sessionManager.getUser().getImage(), sessionManager.getUser().getAvatarFrameImage(), 20);
                binding.mainHostnameCount.setText(liveUser.getName());
                bookedSeatItemList = liveUser.getSeat();

                seatAdapter.addData(liveUser.getSeat());
            }
        }

        rtcStatsView = findViewById(R.id.single_host_rtc_stats);
        initLister();
        joinChannel();
        startBroadcast();
        if (liveUser.getBackground() != null && !liveUser.getBackground().isEmpty()) {
            Glide.with(HostLiveAudioActivity.this).load(liveUser.getBackground()).thumbnail(Glide.with(HostLiveAudioActivity.this).load(BuildConfig.BASE_URL + liveUser.getBackground())).into(binding.mainImg);
        } else {
            Glide.with(HostLiveAudioActivity.this).load(R.drawable.bg4).into(binding.mainImg);
        }

        binding.rvGift.setAdapter(giftReceiveAdapter);

        if (liveUser.getRoomWelcome() != null) {
            viewModel.liveStramCommentAdapter.addSingleComment(null);
            LiveStramComment liveStramComment1 = new LiveStramComment("Announcement : " + liveUser.getRoomWelcome(), sessionManager.getUser(), true, liveUser.getLiveStreamingId(), "", "comment", "");
            viewModel.liveStramCommentAdapter.addSingleComment(liveStramComment1);
        } else {
            viewModel.liveStramCommentAdapter.addSingleComment(null);
            LiveStramComment liveStramComment1 = new LiveStramComment(getString(R.string.announcement_welcome_to_room), sessionManager.getUser(), true, liveUser.getLiveStreamingId(), "", "comment", "");
            viewModel.liveStramCommentAdapter.addSingleComment(liveStramComment1);
        }

        rtcEngine().muteLocalAudioStream(false);

        if (sessionManager.getLiveUserForBackground() != null && sessionManager.getLiveUserForBackground().getAudioRoomConfig() != null) {
            int isMuted = sessionManager.getLiveUserForBackground().getAudioRoomConfig().isHostMute();
            viewModel.isMuted = isMuted != 0;

            rtcEngine().muteLocalAudioStream(viewModel.isMuted);
            Log.e(TAG, "initLister: >>>>>>>>>>>>>  " + viewModel.isMuted);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("position", -1);
            jsonObject.addProperty("liveUserMongoId", liveUser.getId());
            jsonObject.addProperty("liveUserId", liveUser.getLiveUserId());
            Log.d(TAG, "onMuteMic: liveUser.getLiveStreamingId() === " + liveUser.getLiveStreamingId());
            jsonObject.addProperty("liveStreamingId", liveUser.getLiveStreamingId());
            jsonObject.addProperty("agoraId", liveUser.getAgoraUID());

            jsonObject.addProperty("mute", (viewModel.isMuted) ? 1 : 0);
            jsonObject.addProperty("mutedUserId", sessionManager.getUser().getId());
            MySocketManager.getInstance().getSocket().emit(Const.EVENT_MUTESEAT, jsonObject);
            if (viewModel.isMuted) {
                binding.ivMute.setVisibility(View.VISIBLE);
                binding.btnMute.setImageDrawable(ContextCompat.getDrawable(HostLiveAudioActivity.this, R.drawable.ic_mute));
            } else {
                binding.ivMute.setVisibility(GONE);
                binding.btnMute.setImageDrawable(ContextCompat.getDrawable(HostLiveAudioActivity.this, R.drawable.ic_unmute));
            }
            sessionManager.saveLiveUserForBackground(null);

        }

    }

    public void onClickSendComment(View view) {
        String comment = binding.etComment.getText().toString();
        if (!comment.isEmpty()) {
            binding.etComment.setText("");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("comment", comment);
                jsonObject.put("user", new Gson().toJson(sessionManager.getUser()));
                jsonObject.put("isJoined", false);
                jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
                jsonObject.put("userId", sessionManager.getUser().getId());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            LiveStramComment liveStramComment = new LiveStramComment(comment, sessionManager.getUser(), false, liveUser.getLiveStreamingId(), "", "comment", "");
            MySocketManager.getInstance().getSocket().emit(Const.EVENT_COMMENT_AUDIO, new Gson().toJson(liveStramComment));
//            hideKeyboard(HostLiveAudioActivity.this);
        }
    }

    private void initLister() {

        viewModel.clickedComment.observe(this, user -> {
            getUser2(user.getId());
        });
        viewModel.clickedUser.observe(this, user -> {
            try {
                getUser2(user.get("userId").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        binding.imgGame.setOnClickListener(v -> {
            new BottomSheetGameList(this, gameItem -> {
                if (gameItem.getName().contains("Roulette")) {
                    new BottomSheetGameCasino(this, gameItem.getLink(), new BottomSheetGameCasino.OnDialogDismissListener() {
                        @Override
                        public void onDismiss() {
                            MySocketManager.getInstance().getSocket().emit(Const.USER_COIN_UPDATE, sessionManager.getUser().getId());
                            Log.d(TAG, "onDismiss: couns:..." + sessionManager.getUser().getDiamond());
                        }
                    });
                } else if (gameItem.getName().contains("Ferry")) {
                    new DialogGame(this, gameItem.getLink(), new DialogGame.OnDialogDismissListener() {
                        @Override
                        public void onDismiss() {
                            MySocketManager.getInstance().getSocket().emit(Const.USER_COIN_UPDATE, sessionManager.getUser().getId());
                        }
                    });
                } else {
                    new BottomSheetGameTeenPatti(this, gameItem.getLink(), new BottomSheetGameTeenPatti.OnDialogDismissListener() {
                        @Override
                        public void onDismiss() {
                            MySocketManager.getInstance().getSocket().emit(Const.USER_COIN_UPDATE, sessionManager.getUser().getId());
                        }
                    });
                }
            });
        });

        binding.btnMute.setOnClickListener(v -> {
            if (rtcEngine() != null) {
                viewModel.isMuted = !viewModel.isMuted;
                rtcEngine().muteLocalAudioStream(viewModel.isMuted);
                Log.e(TAG, "initLister: >>>>>>>>>>>>>  " + viewModel.isMuted);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", -1);
                jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                jsonObject.addProperty("liveUserId", liveUser.getLiveUserId());
                Log.d(TAG, "onMuteMic: liveUser.getLiveStreamingId() === " + liveUser.getLiveStreamingId());
                jsonObject.addProperty("liveStreamingId", liveUser.getLiveStreamingId());
                jsonObject.addProperty("agoraId", liveUser.getAgoraUID());
                jsonObject.addProperty("mute", (viewModel.isMuted) ? 1 : 0);
                jsonObject.addProperty("mutedUserId", sessionManager.getUser().getId());
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_MUTESEAT, jsonObject);
                if (viewModel.isMuted) {
                    binding.ivMute.setVisibility(View.VISIBLE);
                    binding.btnMute.setImageDrawable(ContextCompat.getDrawable(HostLiveAudioActivity.this, R.drawable.ic_mute));
                } else {
                    binding.ivMute.setVisibility(GONE);
                    binding.btnMute.setImageDrawable(ContextCompat.getDrawable(HostLiveAudioActivity.this, R.drawable.ic_unmute));
                }
            }
        });

        binding.btnSetting.setOnClickListener(v -> {
            new BottomSheetAudioRoomSetting(HostLiveAudioActivity.this, liveUser, new BottomSheetAudioRoomSetting.RoomSettingListener() {
                @Override
                public void onRoomNameChanged(BottomSheetAudioroomSettingsBinding audioroomSettingsBinding) {
                    new BottomSheetAudioRoomName(HostLiveAudioActivity.this, liveUser, audioroomSettingsBinding.tvName::setText);
                }

                @Override
                public void onRoomImageChanged(BottomSheetAudioroomSettingsBinding audioroomSettingsBinding) {
                    requestGalleryPermissions(HostLiveAudioActivity.this);
                }

                @Override
                public void onSeatSizeChanged(BottomSheetAudioroomSettingsBinding audioroomSettingsBinding) {
                    new BottomSheetAudioRoomWheatMode(HostLiveAudioActivity.this, liveUser.getSeat().size(), new BottomSheetAudioRoomWheatMode.OnSeatClickListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSeatClick(int seatCount) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
                                jsonObject.put("seatCount", seatCount);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            MySocketManager.getInstance().getSocket().emit(Const.EVENT_UPDATE_SEAT_COUNT, jsonObject);
                            Toast.makeText(HostLiveAudioActivity.this, R.string.seat_update, Toast.LENGTH_SHORT).show();
                            audioroomSettingsBinding.tvSeatCount.setText(seatCount + getString(R.string.people));
                        }
                    });
                }

                @Override
                public void onRoomWelcomeMessageChanged(BottomSheetAudioroomSettingsBinding audioroomSettings) {
                    new BottomSheetAudioRoomWelcomeMsg(HostLiveAudioActivity.this, liveUser, text -> {
                        audioroomSettings.tvWelcomemsg.setText(text);
                        liveUser.setRoomWelcome(text);
                    });
                }

                @Override
                public void onRoomPasscodeChanged(BottomSheetAudioroomSettingsBinding audioroomSettings) {
                    new BottomSheetAudioRoomChangePasscode(HostLiveAudioActivity.this, liveUser, Roompasscode -> {
                        audioroomSettings.tvPassCode.setText(Roompasscode);
                        liveUser.setPrivateCode(Integer.parseInt(Roompasscode));
                    });

                }

                @Override
                public void onRoomBackgroundChanged() {
                    new BottomSheetOtions(HostLiveAudioActivity.this, image -> {

                        Glide.with(HostLiveAudioActivity.this).load(BuildConfig.BASE_URL + image).into(binding.mainImg);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("liveUserMongoId", liveUser.getId());
                            jsonObject.put("background", image);
                            jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        MySocketManager.getInstance().getSocket().emit(Const.EVENT_CHANGE_THEME, jsonObject);
                    });
                }

                @Override
                public void onBannedUser() {
                    new BottomSheetBannedList(HostLiveAudioActivity.this, blockUserList, new BottomSheetBannedList.OnclickListener() {
                        @Override
                        public void onUnblockclick(String id) {
                            try {

                                JSONObject jsonObject1 = new JSONObject();
                                jsonObject1.put("blocked", blockedUsersList);
                                jsonObject1.put("type", "unblock");
                                jsonObject1.put("liveStreamingId", liveUser.getLiveStreamingId());
                                jsonObject1.put("blockedUserId", id);
//                                jsonObject1.put("blockUntil",blockUntil);
                                MySocketManager.getInstance().getSocket().emit(Const.EVENT_UPDATEBLOCKEDLIST, jsonObject1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onRoomClose() {
                    new PopupBuilder(HostLiveAudioActivity.this).showReliteDiscardPopup("Are you sure you want to delete your room?", "", "Yes", "No", () -> {
                        RetrofitBuilder.create().deleteRoom(sessionManager.getUser().getId()).enqueue(new Callback<RestResponse>() {
                            @Override
                            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                                confirmedEndLive();
                                Toast.makeText(HostLiveAudioActivity.this, "Your room deleted successfully..", Toast.LENGTH_SHORT).show();
                                sessionManager.setIsAudioRoomBackground(false);
                            }

                            @Override
                            public void onFailure(Call<RestResponse> call, Throwable t) {

                            }
                        });
                    });
                }
            });
        });

        binding.btnClose.setOnClickListener(v -> {
            endLive();
        });

        binding.ivShare.setOnClickListener(v -> {
            binding.ivShare.setEnabled(false);
            BranchUniversalObject buo = new BranchUniversalObject().setCanonicalIdentifier("content/12345").setTitle("Watch My room").setContentDescription("By : " + sessionManager.getUser().getName()).setContentImageUrl(sessionManager.getUser().getImage()).setContentMetadata(new ContentMetadata().addCustomMetadata("type", "AUDIO_LIVE").addCustomMetadata(Const.DATA, new Gson().toJson(liveUser)));
            LinkProperties lp = new LinkProperties().setChannel("facebook").setFeature("sharing").setCampaign("content 123 launch").setStage("new user").addControlParameter("", "").addControlParameter("", Long.toString(Calendar.getInstance().getTimeInMillis()));
            buo.generateShortUrl(this, lp, (url, error) -> {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareMessage = url;
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.choose_one)));
                    binding.ivShare.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        binding.btnReaction.setOnClickListener(view -> bottomSheetReactions.show());
        bottomSheetReactions.setOnReactionClickListner(reaction -> {
            Log.d(TAG, "initLister: " + reaction.getImage());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
                jsonObject.put("position", -2); // for host
                jsonObject.put("image", reaction.getImage());
                jsonObject.put("user", new Gson().toJson(sessionManager.getUser()));
                MySocketManager.getInstance().getSocket().emit(Const.EVENTSENDREACTION, jsonObject);
            } catch (Exception o) {
                o.printStackTrace();
            }
        });

        giftViewModel.finelGift.observe(this, giftItem -> {
            if (giftItem != null) {
                int totalCoin = giftItem.getCoin() * giftItem.getCount();
                if (sessionManager.getUser().getDiamond() < totalCoin) {
                    Toast.makeText(HostLiveAudioActivity.this, R.string.you_not_have_enough_diamonds_to_send_gift, Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (!giftViewModel.userListAdapter.getUsers().isEmpty()) {
                        List<String> selectedUsers = giftViewModel.userListAdapter.getUsers().stream().filter(UserSelectableClass::isSelected).map(user -> user.getSeatItem().getUserId()).collect(Collectors.toList());

                        List<String> selectedUsersName = giftViewModel.userListAdapter.getUsers().stream()
                                .filter(UserSelectableClass::isSelected)
                                .map(user -> user.getSeatItem().getName())
                                .collect(Collectors.toList());
                        if (selectedUsers.isEmpty()) {
                            Toast.makeText(this, R.string.select_at_least_one_user, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("senderUserId", sessionManager.getUser().getId());
                        jsonObject.put("receiverUserId", Arrays.toString(selectedUsers.toArray()));
                        jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
                        jsonObject.put("userName", sessionManager.getUser().getName());
                        jsonObject.put("receiverUserName", Arrays.toString(selectedUsersName.toArray()));
                        jsonObject.put("coin", giftItem.getCoin() * giftItem.getCount());
                        jsonObject.put("gift", new Gson().toJson(giftItem));
                        jsonObject.put("giftCount", giftItem.getCount());
                        jsonObject.put("timeStamp", System.currentTimeMillis());
                        jsonObject.put("liveType", "audio");
                        int i = selectedUsers.size();
                        int totalGiftCoin = giftItem.getCoin() * i;
                        long totalDiamond = sessionManager.getUser().getDiamond();

                        Log.d("===TAG", "receiverUserId: " + i);
                        Log.d("===TAG", "totalGiftCoin: " + totalGiftCoin);
                        Log.d("===TAG", "totalDiamond: " + totalDiamond);
                        if (totalDiamond >= totalGiftCoin) {
                            MySocketManager.getInstance().getSocket().emit(Const.EVENT_NORMALUSER_GIFT, jsonObject);
                        } else {
                            Toast.makeText(HostLiveAudioActivity.this, getString(R.string.you_not_have_enough_diamonds_to_send_gift), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, R.string.don_t_have_user_to_sent_a_gift_wait_for_user, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        seatAdapter.setOnSeatClick(new SeatAdapter.onSeatClick() {
            @Override
            public void OnClickSeat(PkAudioLiveUserRoot.UsersItem.SeatItem seatItem, int position) {
                doWork(seatItem, position);
                selfPosition = position;
            }


        });

        userProfileBottomSheet.setOnUserTapListner(new UserProfileBottomSheet.OnUserTapListner() {
            @Override
            public void onBlockClick(GuestProfileRoot.User userDummy) {
                blockedUsersList.put(userDummy.getUserId());
//                try {
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("blocked", blockedUsersList);
//                    jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
//                    MySocketManager.getInstance().getSocket().emit(Const.EVENT_BLOCK, jsonObject);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("blocked", blockedUsersList);
                    jsonObject1.put("type", "block");
                    jsonObject1.put("liveStreamingId", liveUser.getLiveStreamingId());
                    jsonObject1.put("blockedUserId", userDummy.getUserId());
                    MySocketManager.getInstance().getSocket().emit(Const.EVENT_UPDATEBLOCKEDLIST, jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


//                new BottomSheetBlockTime(HostLiveAudioActivity.this, new BottomSheetBlockTime.BlockTimeListner() {
//
//                    GuestProfileRoot.User userdata = new GuestProfileRoot.User();
//
//                    @Override
//                    public void onehour(String blockUntil) {
//
//                        blockUntil = "1 hour";
//                        try {
//                            JSONObject jsonObject1 = new JSONObject();
//                            jsonObject1.put("blocked", blockedUsersList);
//                            jsonObject1.put("type", "block");
//                            jsonObject1.put("liveStreamingId", liveUser.getLiveStreamingId());
//                            jsonObject1.put("blockedUserId", userDummy.getUserId());
//                            jsonObject1.put("blockUntil", blockUntil);
//                            Log.d(TAG, "onehour: userid" + userdata.getUserId());
//                            MySocketManager.getInstance().getSocket().emit(Const.EVENT_UPDATEBLOCKEDLIST, jsonObject1);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//
//                    @Override
//                    public void oneday(String blockUntil) {
//                        blockUntil = "1 day";
//                        try {
//                            JSONObject jsonObject1 = new JSONObject();
//                            jsonObject1.put("blocked", blockedUsersList);
//                            jsonObject1.put("type", "block");
//                            jsonObject1.put("liveStreamingId", liveUser.getLiveStreamingId());
//                            jsonObject1.put("blockedUserId", userDummy.getUserId());
//                            jsonObject1.put("blockUntil", blockUntil);
//                            MySocketManager.getInstance().getSocket().emit(Const.EVENT_UPDATEBLOCKEDLIST, jsonObject1);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void lifetime(String blockUntil) {
//                        blockUntil = "lifetime";
//
//                        try {
//                            JSONObject jsonObject1 = new JSONObject();
//                            jsonObject1.put("blocked", blockedUsersList);
//                            jsonObject1.put("type", "block");
//                            jsonObject1.put("liveStreamingId", liveUser.getLiveStreamingId());
//                            jsonObject1.put("blockedUserId", userDummy.getUserId());
//                            jsonObject1.put("blockUntil", blockUntil);
//                            MySocketManager.getInstance().getSocket().emit(Const.EVENT_UPDATEBLOCKEDLIST, jsonObject1);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });

            }
        });


        binding.options.setOnClickListener(v -> {
            new BottomSheetOtions(HostLiveAudioActivity.this, image -> {
                Glide.with(HostLiveAudioActivity.this).load(BuildConfig.BASE_URL + image).into(binding.mainImg);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("liveUserMongoId", liveUser.getId());
                    jsonObject.put("background", image);
                    jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
                    sessionManager.saveStringValue(Const.THEME_STR, image);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MySocketManager.getInstance().getSocket().emit(Const.EVENT_CHANGE_THEME, jsonObject);
                Log.d(TAG, "onGalleryClick: " + jsonObject.toString());

            });

        });

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    private void requestGalleryPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE);
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE);
            } else {
                openGallery();
            }
        }
    }


    private void doWork(PkAudioLiveUserRoot.UsersItem.SeatItem seatItem, int i) {
        Log.e(TAG, "doWork: >>>>>>>>>>  " + i);

        if (seatItem.isReserved() && seatItem.getUserId().equalsIgnoreCase(sessionManager.getUser().getId())) {
            new PopupBuilder(this).showRemovePopup(() -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", i);
                jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                jsonObject.addProperty("liveStreamingId", liveUser.getLiveStreamingId());

                MySocketManager.getInstance().getSocket().emit(Const.EVENT_LESS_PARTICIPATED, jsonObject);

                Log.d(TAG, "doWork: remove sit by it self" + jsonObject.toString());

            });
            return;
        }

        if (seatItem.isReserved()) {
            getUser(seatItem.getUserId(), i, seatItem);
            return;
        }

        new BottomSheetHostMic(HostLiveAudioActivity.this, seatItem, new BottomSheetHostMic.OnClickListner() {
            @Override
            public void onTakeMic() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", i);
                jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                jsonObject.addProperty("userId", sessionManager.getUser().getId());
                jsonObject.addProperty("name", sessionManager.getUser().getName());
                jsonObject.addProperty("country", sessionManager.getUser().getCountry());
                jsonObject.addProperty("agoraUid", liveUser.getAgoraUID());
                jsonObject.addProperty("image", sessionManager.getUser().getImage());
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_ADD_PARTICIPATED, jsonObject);
//                hostPosition = i;

            }

            @Override
            public void onGiveMic() {
                if (finalArray != null && finalArray.length() > 0) {
                    Log.e(TAG, "onGiveMic: Click>>>>>>>>>>>>>>>>>>>>>> if condition");

                    new BottomSheetViewersUsers(HostLiveAudioActivity.this, finalArray, userDummy -> {
                        try {
                            Log.e(TAG, "onGiveMic: >>>>>>>>>>>>>>>try catch");
//                            getUser(userDummy.get("userId").toString(), i, seatItem);
//                            userPosition = i;
                            Log.d(TAG, "onGiveMic: userDummy.toString() ==  " + userDummy.toString());
                            Log.d(TAG, "onGiveMic: userDummy.toString() ==  i " + i);
                            Log.d(TAG, "onGiveMic: userDummy.toString() ==  liveUser.getId() " + liveUser.getId());
                            Log.d(TAG, "onGiveMic: userDummy.toString() ==  liveUser.getLiveStreamingId() " + liveUser.getLiveStreamingId());
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("position", i);
                            jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                            jsonObject.addProperty("userId", userDummy.get("userId").toString());
                            jsonObject.addProperty("liveStreamingId", liveUser.getLiveStreamingId());
                            jsonObject.addProperty("name", userDummy.get("name").toString());
                            jsonObject.addProperty("country", userDummy.get("country").toString());
                            jsonObject.addProperty("agoraUid", -1);
                            jsonObject.addProperty("image", userDummy.get("image").toString());
                            jsonObject.addProperty("avatarFrame", userDummy.get("avatarFrameImage").toString());
                            MySocketManager.getInstance().getSocket().emit(Const.EVENT_ADD_REQUESTED, jsonObject);
                            Log.d(TAG, "onGiveMic: emit(Const.EVENT_ADD_REQUESTED=== " + jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onGiveMic: catch  == " + e.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(HostLiveAudioActivity.this, R.string.there_s_no_user_to_invite_on_mic, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onLockMic() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", i);
                jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                jsonObject.addProperty("lock", !seatItem.isLock());
                jsonObject.addProperty("liveStreamingId", liveUser.getLiveStreamingId());
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_LOCK_SEAT, jsonObject);
            }

            @Override
            public void onMuteMic() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", i);
                jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                jsonObject.addProperty("liveUserId", liveUser.getLiveUserId());
                Log.d(TAG, "onMuteMic: liveUser.getLiveStreamingId() === " + liveUser.getLiveStreamingId());
                jsonObject.addProperty("liveStreamingId", liveUser.getLiveStreamingId());
                jsonObject.addProperty("agoraId", seatItem.getAgoraUid());
                jsonObject.addProperty("mute", (seatItem.isMute() == 1) ? 0 : 1);
                jsonObject.addProperty("mutedUserId", seatItem.getUserId());
                if (rtcEngine() != null) {
                    rtcEngine().muteRemoteAudioStream(seatItem.getAgoraUid(), true);
                }
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_MUTESEAT, jsonObject);
            }

            @Override
            public void onCancelClick() {

            }

            @Override
            public void onClickRemove() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", i);
                jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                jsonObject.addProperty("liveStreamingId", liveUser.getLiveStreamingId());

                MySocketManager.getInstance().getSocket().emit(Const.EVENT_LESS_PARTICIPATED, jsonObject);

            }
        });
    }

    public void onclickGiftIcon(View view) {
        if (!emojiBottomsheetFragment.isAdded()) {
            giftViewModel.users.clear();
            giftViewModel.users.add(new UserSelectableClass(new PkAudioLiveUserRoot.UsersItem.SeatItem(liveUser.getImage(), liveUser.getCountry(), true, "Host", false, liveUser.getAgoraUID(), 0, true, liveUser.getId(), -1, false, liveUser.getLiveUserId())));

            liveUser.getSeat().stream().filter(PkAudioLiveUserRoot.UsersItem.SeatItem::isReserved).map(UserSelectableClass::new).forEach(giftViewModel.users::add);
            emojiBottomsheetFragment.show(getSupportFragmentManager(), "emojifragfmetn");
        }
    }

    private void getUser(String userId, int postion, PkAudioLiveUserRoot.UsersItem.SeatItem seatItem) {
        customDialogClass.show();
        userListPosition = postion;
        viewerListItem = seatItem;
        Log.d(TAG, "getUser: vali methos call thay che userListPosition ======" + userListPosition);
        Log.d(TAG, "getUser: vali methos call thay che viewerListItem ======" + viewerListItem);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fromUserId", sessionManager.getUser().getId());
            jsonObject.put("toUserId", userId);
            Log.d(TAG, "getUser:request  " + jsonObject);
            MySocketManager.getInstance().getSocket().emit(Const.EVENT_GET_USER, jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getUser2(String userId) {
        customDialogClass.show();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fromUserId", sessionManager.getUser().getId());
            jsonObject.put("toUserId", userId);
            Log.d(TAG, "getUser:request  " + jsonObject);
            MySocketManager.getInstance().getSocket().emit(Const.EVENT_GET_USER_2, jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doUserTask(GuestProfileRoot.User userData, int postion, PkAudioLiveUserRoot.UsersItem.SeatItem seatItem) {

        new BottomSheetViewersUserProfile(this, seatItem, userData, new BottomSheetViewersUserProfile.OnClickListner() {
            @Override
            public void onUnMute(BottomSheetOnlineProfileBinding sheetDilogBinding) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", postion);
                jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                jsonObject.addProperty("liveUserId", liveUser.getLiveUserId());
                jsonObject.addProperty("mute", (seatItem.isMute() == 1 || seatItem.isMute() == 2) ? 0 : 2);
                jsonObject.addProperty("liveStreamingId", liveUser.getLiveStreamingId());
                jsonObject.addProperty("agoraId", seatItem.getAgoraUid());
                jsonObject.addProperty("mutedUserId", seatItem.getUserId());
                Log.e(TAG, "onUnMute: >>>>>>>>>>>>>>>>  " + seatItem.isMute());
                Log.e(TAG, "onUnMute: >>>>>>>>>>>>>>>> getAgoraUid  " + seatItem.getAgoraUid());
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_MUTESEAT, jsonObject);
                //   rtcEngine().muteRemoteAudioStream(seatItem.getAgoraUid(), seatItem.isMute() != 0);

                if (seatItem.isMute() == 1) {
                    sheetDilogBinding.txtMic.setText(getString(R.string.unmute_mic));
                    Glide.with(HostLiveAudioActivity.this).load(R.drawable.speaker_off).into(sheetDilogBinding.mute);
                } else {
                    sheetDilogBinding.txtMic.setText(getString(R.string.mute_mic));
                    Glide.with(HostLiveAudioActivity.this).load(R.drawable.speaker).into(sheetDilogBinding.mute);
                }
            }

            @Override
            public void onRemoveSeat() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", postion);
                jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                jsonObject.addProperty("userId", seatItem.getUserId());
                jsonObject.addProperty("liveStreamingId", liveUser.getLiveStreamingId());
                jsonObject.addProperty("name", sessionManager.getUser().getName());
                jsonObject.addProperty("country", sessionManager.getUser().getCountry());
                jsonObject.addProperty("agoraUid", liveUser.getAgoraUID());
                jsonObject.addProperty("removedUserID", seatItem.getUserId());
                jsonObject.addProperty("image", sessionManager.getUser().getImage());
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_LESS_PARTICIPATED, jsonObject);

            }

            @Override
            public void onkickOut() {

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", postion);
                jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                jsonObject.addProperty("liveStreamingId", liveUser.getLiveStreamingId());
                jsonObject.addProperty("removedUserID", seatItem.getUserId());
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_LESS_PARTICIPATED, jsonObject);
                blockedUsersList.put(seatItem.getUserId());
                Log.d(TAG, "initLister: blocked " + blockedUsersList.toString());
                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("blocked", blockedUsersList);
                    jsonObject1.put("type", "block");
                    jsonObject1.put("liveStreamingId", liveUser.getLiveStreamingId());
                    jsonObject1.put("blockedUserId", seatItem.getUserId());
                    MySocketManager.getInstance().getSocket().emit(Const.EVENT_UPDATEBLOCKEDLIST, jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


//                new BottomSheetBlockTime(HostLiveAudioActivity.this, new BottomSheetBlockTime.BlockTimeListner() {
//
//                    @Override
//                    public void onehour(String blockUntil) {
//                        blockUntil = "1 hour";
//
//                        JsonObject jsonObject = new JsonObject();
//                        jsonObject.addProperty("position", postion);
//                        jsonObject.addProperty("liveUserMongoId", liveUser.getId());
//                        jsonObject.addProperty("liveStreamingId", liveUser.getLiveStreamingId());
//                        jsonObject.addProperty("removedUserID", seatItem.getUserId());
//                        MySocketManager.getInstance().getSocket().emit(Const.EVENT_LESS_PARTICIPATED, jsonObject);
//                        blockedUsersList.put(seatItem.getUserId());
//                        Log.d(TAG, "initLister: blocked " + blockedUsersList.toString());
//                        try {
//                            JSONObject jsonObject1 = new JSONObject();
//                            jsonObject1.put("blocked", blockedUsersList);
//                            jsonObject1.put("type", "block");
//                            jsonObject1.put("liveStreamingId", liveUser.getLiveStreamingId());
//                            jsonObject1.put("blockedUserId", seatItem.getUserId());
//                            jsonObject1.put("blockUntil", blockUntil);
//                            MySocketManager.getInstance().getSocket().emit(Const.EVENT_UPDATEBLOCKEDLIST, jsonObject1);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//
//                    @Override
//                    public void oneday(String blockUntil) {
//                        blockUntil = "1 day";
//
//                        JsonObject jsonObject = new JsonObject();
//                        jsonObject.addProperty("position", postion);
//                        jsonObject.addProperty("liveUserMongoId", liveUser.getId());
//                        jsonObject.addProperty("liveStreamingId", liveUser.getLiveStreamingId());
//                        jsonObject.addProperty("removedUserID", seatItem.getUserId());
//                        MySocketManager.getInstance().getSocket().emit(Const.EVENT_LESS_PARTICIPATED, jsonObject);
//                        blockedUsersList.put(seatItem.getUserId());
//                        Log.d(TAG, "initLister: blocked " + blockedUsersList.toString());
//                        try {
//                            JSONObject jsonObject1 = new JSONObject();
//                            jsonObject1.put("blocked", blockedUsersList);
//                            jsonObject1.put("type", "block");
//                            jsonObject1.put("liveStreamingId", liveUser.getLiveStreamingId());
//                            jsonObject1.put("blockedUserId", seatItem.getUserId());
//                            jsonObject1.put("blockUntil", blockUntil);
//                            MySocketManager.getInstance().getSocket().emit(Const.EVENT_UPDATEBLOCKEDLIST, jsonObject1);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void lifetime(String blockUntil) {
//                        blockUntil = "lifetime";
//
//                        JsonObject jsonObject = new JsonObject();
//                        jsonObject.addProperty("position", postion);
//                        jsonObject.addProperty("liveUserMongoId", liveUser.getId());
//                        jsonObject.addProperty("liveStreamingId", liveUser.getLiveStreamingId());
//                        jsonObject.addProperty("removedUserID", seatItem.getUserId());
//                        MySocketManager.getInstance().getSocket().emit(Const.EVENT_LESS_PARTICIPATED, jsonObject);
//                        blockedUsersList.put(seatItem.getUserId());
//                        Log.d(TAG, "initLister: blocked " + blockedUsersList.toString());
//                        try {
//                            JSONObject jsonObject1 = new JSONObject();
//                            jsonObject1.put("blocked", blockedUsersList);
//                            jsonObject1.put("type", "block");
//                            jsonObject1.put("liveStreamingId", liveUser.getLiveStreamingId());
//                            jsonObject1.put("blockedUserId", seatItem.getUserId());
//                            jsonObject1.put("blockUntil", blockUntil);
//                            MySocketManager.getInstance().getSocket().emit(Const.EVENT_UPDATEBLOCKEDLIST, jsonObject1);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });

            }

            @Override
            public void inviteUser() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", postion);
                jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                jsonObject.addProperty("userId", userData.getUserId());
                jsonObject.addProperty("liveStreamingId", liveUser.getLiveStreamingId());
                jsonObject.addProperty("name", userData.getName());
                jsonObject.addProperty("country", userData.getCountry());
                jsonObject.addProperty("agoraUid", -1);
                jsonObject.addProperty("image", userData.getImage());
                jsonObject.addProperty("avatarFrame", userData.getAvatarFrameImage());
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_ADD_REQUESTED, jsonObject);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

    }

    @Override
    public void onErr(int err) {
        Log.d(TAG, "onErr: " + err);
    }

    @Override
    public void onConnectionLost() {
        Log.d(TAG, "onConnectionLost: ");
    }

    @Override
    public void onVideoStopped() {
        Log.d(TAG, "onVideoStopped: ");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sessionManager.getUser().isHost()) {
            hostAPICall.stopApiCallLoop();
        }
        endLive();
        MySocketManager.getInstance().removeAudioRoomHandler(audioRoomHandler);
        MySocketManager.getInstance().removeSocketConnectHandler(socketConnectHandler);
        statsManager().clearAllData();
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
    }

    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
        Log.d(TAG, "onLeaveChannel: stts " + stats);
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        Log.d(TAG, "onJoinChannelSuccess: chanel " + channel + " uid" + uid + "  elapsed " + elapsed);
        this.uuid = uid;
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        Log.d(TAG, "onUserOffline: " + uid + " reason" + reason);

    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        Log.d(TAG, "onUserJoined: " + uid + "  elapsed" + elapsed);
    }

    @Override
    public void onLastmileQuality(int quality) {

    }

    @Override
    public void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result) {

    }

    @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
        Log.d(TAG, "onLocalVideoStats: ");
        if (!statsManager().isEnabled()) return;

    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
        runOnUiThread(() -> {
            if (rtcStatsView != null && rtcStatsView.getVisibility() == View.VISIBLE) {
                rtcStatsView.setLocalStats(stats.rxKBitRate, stats.rxPacketLossRate, stats.txKBitRate, stats.txPacketLossRate);
            }
        });

    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (!statsManager().isEnabled()) return;

        StatsData data = statsManager().getStatsData(uid);
        if (data == null) return;

        data.setSendQuality(statsManager().qualityToString(txQuality));
        data.setRecvQuality(statsManager().qualityToString(rxQuality));
    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setWidth(stats.width);
        data.setHeight(stats.height);
        data.setFramerate(stats.rendererOutputFrameRate);
        data.setVideoDelay(stats.delay);
    }

    @Override
    public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {

        Log.d(TAG, "onAudioVolumeIndication: speakers.length  " + speakers.length);
        Log.d(TAG, "onAudioVolumeIndication: totalVolume  " + totalVolume);
        Log.d(TAG, "onAudioVolumeIndication: host.getAgoraUID()  " + liveUser.getAgoraUID());
        runOnUiThread(() -> {

            if (totalVolume <= 0) return;
            for (IRtcEngineEventHandler.AudioVolumeInfo info : speakers) {
                if (info.uid == 0) {
                    info.uid = liveUser.getAgoraUID();
                    info.channelId = liveUser.getChannel();
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.animationView1.setVisibility(View.GONE);
                        }
                    }, 1500);
                    binding.animationView1.setVisibility(View.VISIBLE);

                } else {
                    seatAdapter.onAudioVolumeIndication(speakers);
                }
            }


            for (IRtcEngineEventHandler.AudioVolumeInfo speaker : speakers) {
                Log.d(TAG, "onAudioVolumeIndication: uid " + speaker.uid);
                Log.d("onAudioVolumeIndication", "onAudioVolumeIndication: channelid" + speaker.channelId);
                Log.d("onAudioVolumeIndication", "onAudioVolumeIndication: volumne" + speaker.volume);
                Log.d("onAudioVolumeIndication", "onAudioVolumeIndication: vad" + speaker.vad);
            }

        });
    }

    @Override
    public void onActiveSpeaker(int uid) {
        Log.d(TAG, "onActiveSpeaker: " + uid);
    }

    @Override
    public void onAudioMixingStateChanged(int state, int reason) {

    }

    @Override
    public void onTokenPrivilegeWillExpire(String token) {
        Log.d(TAG, "onTokenPrivilegeWillExpire: ");
        try {
            String tkn = RtcTokenBuilderSample.main(liveUser.getChannel(), sessionManager.getSetting().getAgoraKey(), sessionManager.getSetting().getAgoraCertificate());
            rtcEngine().renewToken(tkn);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onRequestToken() {
        Log.d(TAG, "onRequestToken: ");
    }

    @Override
    public void onAudioRouteChanged(int routing) {

    }

    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {

        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setAudioNetDelay(stats.networkTransportDelay);
        data.setAudioNetJitter(stats.jitterBufferDelay);
        data.setAudioLoss(stats.audioLossRate);
        data.setAudioQuality(statsManager().qualityToString(stats.quality));
    }

    @Override
    public void onChannelMediaRelayStateChanged(int state, int code) {

    }

    @Override
    public void onChannelMediaRelayEvent(int code) {

    }

    @Override
    public void onFirstLocalAudioFramePublished(int elapsed) {
        Log.d(TAG, "onFirstLocalAudioFramePublished: " + elapsed);
    }

    @Override
    public void onFirstRemoteAudioFrame(int uid, int elapsed) {
        Log.d(TAG, "onFirstRemoteAudioFrame: " + uid);
    }

    @Override
    public void onUserMuteAudio(int uid, boolean muted) {
        Log.d(TAG, "onUserMuteAudio: " + uid);
    }

    private Handler reactionHandler = new Handler();
    private Runnable currentReactionRunnable;
    private boolean isReactionRunning = false;

    public void setUpReaction(String image, ImageView imgHostReaction, int duration) {
        // If a reaction is already running, cancel it
        if (isReactionRunning && currentReactionRunnable != null) {
            reactionHandler.removeCallbacks(currentReactionRunnable);
            imgHostReaction.setImageDrawable(null); // Clear the current reaction
            isReactionRunning = false;
        }

        // Set up the new reaction
        isReactionRunning = true;
        Glide.with(this).load(image).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                isReactionRunning = false;
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                // Define a new runnable for the current reaction
                currentReactionRunnable = new Runnable() {
                    @Override
                    public void run() {
                        imgHostReaction.setImageDrawable(null); // Clear the reaction after the duration
                        isReactionRunning = false;
                    }
                };

                // Schedule the runnable to execute after the specified duration
                reactionHandler.postDelayed(currentReactionRunnable, duration);
                return false;
            }
        }).into(imgHostReaction);
    }
//    public void setUpReaction(String image,PkAudioLiveUserRoot.UsersItem.SeatItem seatItem, ImageView imgHostReaction) {
//        Glide.with(this).load(image).into(imgHostReaction);
//        new Handler().postDelayed(() -> {
//            imgHostReaction.setImageDrawable(null);
//        }, 8000);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {

            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));

            Glide.with(this).load(data.getData()).apply(requestOptions).into(binding.imgProfile);
            String[] filePathColumn = {DATA};
            Cursor cursor = this.getContentResolver().query(data.getData(), filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            RequestBody liveUserIdBody = RequestBody.create(MediaType.parse("text/plain"), liveUser.getLiveUserId());

            HashMap<String, RequestBody> map = new HashMap<>();
            MultipartBody.Part body = null;

            if (picturePath != null) {
                File file = new File(picturePath);
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                body = MultipartBody.Part.createFormData("roomImage", file.getName(), requestFile);
            }
            map.put("liveUserId", liveUserIdBody);

            Call<RestResponse> call = RetrofitBuilder.create().updateRoomImage(map, body);
            call.enqueue(new Callback<RestResponse>() {
                @Override
                public void onResponse(@NonNull Call<RestResponse> call, Response<RestResponse> response) {

                    if (response.body() != null) {
                        if (response.body().isStatus()) {
                            Toast.makeText(HostLiveAudioActivity.this, R.string.room_image_updated, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RestResponse> call, Throwable t) {

                }
            });

        }

    }

    @Override
    public void finish() {
        super.finish();
        statsManager().clearAllData();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: isFinishing ===== " + isFinishing());
//        if (!isFinishing()) {
//            confirmedEndLive();
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.getUser().isHost()) {
            hostAPICall.startApiCallLoop();
        }
    }
}