package com.example.rayzi.audioLive;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.MainApplication;
import com.example.rayzi.R;
import com.example.rayzi.RayziUtils;
import com.example.rayzi.SessionManager;
import com.example.rayzi.adapter.GiftReceiveAdapter;
import com.example.rayzi.agora.AgoraBaseActivity;
import com.example.rayzi.agora.stats.RemoteStatsData;
import com.example.rayzi.agora.stats.StatsData;
import com.example.rayzi.agora.token.RtcTokenBuilderSample;
import com.example.rayzi.audioLive.reactions.BottomSheetReactions;
import com.example.rayzi.audioLive.reactions.ReactionsViewModel;
import com.example.rayzi.bottomsheets.BottomSheetAudioRoomPasscode;
import com.example.rayzi.bottomsheets.BottomSheetReport_g;
import com.example.rayzi.bottomsheets.UserProfileBottomSheet;
import com.example.rayzi.databinding.ActivityWatchAudioLiveBinding;
import com.example.rayzi.databinding.ItemSeatBinding;
import com.example.rayzi.emoji.EmojiBottomsheetFragment;
import com.example.rayzi.emoji.UserSelectableClass;
import com.example.rayzi.liveGame.bottomsheet.BottomSheetGameCasino;
import com.example.rayzi.liveGame.bottomsheet.BottomSheetGameList;
import com.example.rayzi.liveGame.bottomsheet.BottomSheetGameTeenPatti;
import com.example.rayzi.liveGame.dialog.DialogGame;
import com.example.rayzi.modelclass.GiftRoot;
import com.example.rayzi.modelclass.GuestProfileRoot;
import com.example.rayzi.modelclass.LiveStramComment;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;
import com.example.rayzi.popups.PopupBuilder;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.socket.AudioRoomHandler;
import com.example.rayzi.socket.MySocketManager;
import com.example.rayzi.socket.SocketConnectHandler;
import com.example.rayzi.utils.FloatingButtonService;
import com.example.rayzi.viewModel.EmojiSheetViewModel;
import com.example.rayzi.viewModel.ViewModelFactory;
import com.example.rayzi.viewModel.WatchLiveViewModel;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import eightbitlab.com.blurview.RenderScriptBlur;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;

public class WatchAudioLiveActivity extends AgoraBaseActivity {
    private static final String TAG = "watchliveact";
    private static int MY_UID = 0;
    ActivityWatchAudioLiveBinding binding;
    boolean isMuteByHost = false;
    SessionManager sessionManager;
    String token = "";
    EmojiBottomsheetFragment emojiBottomsheetFragment;
    List<PkAudioLiveUserRoot.UsersItem.SeatItem> bookedSeatItemList = new ArrayList<>();
    WatchLiveViewModel viewModel;
    PkAudioLiveUserRoot.UsersItem host;
    EmojiSheetViewModel giftViewModel;
    int uuid;
    ArrayList<PkAudioLiveUserRoot.UsersItem.SeatItem> coHostList = new ArrayList<>();
    SeatAdapter seatAdapter;
    List<String> uidlist = new ArrayList<>();
    int selfPosition = -1;
    UserProfileBottomSheet userProfileBottomSheet;
    GridLayoutManager gridLayoutManager;
    boolean isHost = false;
    JSONArray blockedUsersList = new JSONArray();
    long animationDurationMillis;
    List<GiftRoot.GiftItem> giftList = new ArrayList<>();
    GiftReceiveAdapter giftReceiveAdapter = new GiftReceiveAdapter();

    private boolean isProcessingSeatClick = false;
    private boolean hostExists;
    AudioRoomHandler audioRoomHandler = new AudioRoomHandler() {

        @Override
        public void onTotalRoomcoins(Object[] args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: =====watch roomcoin " + args[0].toString());
                    binding.tvRcoins.setText(args[0].toString());
                }
            });
        }

        @Override
        public void onAudioLiveHostRemove(Object[] args) {
            runOnUiThread(() -> {

                Log.d(TAG, "onAudioLiveHostRemove: ====" + args[0].toString());
                if (args[0] != null) {
                    String data = args[0].toString();
                    Log.d(TAG, "onAudioLiveHostRemove: " + data);

                    if (!data.isEmpty()) {
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            hostExists = jsonObject.getBoolean("isHostExists");

                            if (hostExists) {
                                binding.mainHostProfileImage.setUserImage(host.getImage(), host.getAvatarFrameImage(), 20);
                                binding.mainHostProfileImage.setVisibility(VISIBLE);
                                binding.ivPlaceholder.setVisibility(GONE);
                            } else {
                                binding.ivPlaceholder.setVisibility(VISIBLE);
                                binding.mainHostProfileImage.setVisibility(GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        @Override
        public void onHostEnter(Object[] args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: ====enter host");

                }
            });
        }
        @Override
        public void onLiveEndByEnd(Object[] args) {

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
//                        binding.rvComments.smoothScrollToPosition(viewModel.liveStramCommentAdapter.getItemCount() - 1);
                        }

                    }

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

        private Queue<JSONObject> giftQueue = new LinkedList<>();
        private boolean isGiftDisplaying = false;
        long timeStamp;
        @Override
        public void onGift(Object[] args) {

            runOnUiThread(() -> {
                if (args.length > 0 && args[0] != null) {
                    String data = args[0].toString();
                    Log.d(TAG, "onGift0: =================" + args[0].toString());
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        if (jsonObject.get("gift") != null) {
                            // Add the gift data to the queue

                            giftQueue.add(jsonObject);

                            if (!isGiftDisplaying) {
                                processNextGift();
                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (args.length > 1 && args[1] != null) {  // gift sender user
//                    Log.d(TAG, "user string   : " + args[1].toString());
                    Log.d(TAG, "onGift1: =================" + args[1].toString());
                    try {
                        JSONObject jsonObject = new JSONObject(args[1].toString());
                        UserRoot.User user = new Gson().fromJson(jsonObject.toString(), UserRoot.User.class);
                        if (user != null) {
                            Log.d(TAG, ":getted user    " + user.toString());
                            if (user.getId().equals(sessionManager.getUser().getId())) {
                                sessionManager.saveUser(user);
                                giftViewModel.localUserCoin.setValue(user.getDiamond());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                if (args.length > 2 && args[2] != null) {   // host
//                    Log.d(TAG, "host string   : " + args[2].toString());
                    Log.d(TAG, "onGift2: =================" + args[2].toString());
                    try {
                        JSONObject jsonObject = new JSONObject(args[2].toString());
                        UserRoot.User user = new Gson().fromJson(jsonObject.toString(), UserRoot.User.class);
                        if (user != null && user.getId().equals(host.getLiveUserId())) {
                            Log.d(TAG, ":getted host    " + user.toString());
//                            binding.tvRcoins.setText(String.valueOf(host.getRCoin()));
//                            updateCoin = updateCoin + coin;
//                            sessionManager.saveInt("updateCoin",updateCoin);
//                            binding.tvRcoins.setText(String.valueOf(updateCoin));

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

                        Log.d(TAG, "processNextGift: ====call this");

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

                            Log.d(TAG, "processNextGift: ====" + giftData);

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
        private final Set<String> displayedGifts = new HashSet<>();
        private void handleImageGift(String giftLink, GiftRoot.GiftItem giftData) {
            Log.d(TAG, "handleImageGift: ========" + giftData);
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

            Log.d(TAG, "handleSVGAGift: ========call this");
            binding.svgaImage.setVisibility(VISIBLE);
            SVGAImageView imageView = binding.svgaImage;
            SVGAParser parser = new SVGAParser(WatchAudioLiveActivity.this);

            try {
                    parser.decodeFromURL(new URL(giftLink), new SVGAParser.ParseCompletion() {
                        @Override
                        public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                            SVGADynamicEntity dynamicEntity = new SVGADynamicEntity();
                            dynamicEntity.setDynamicImage(giftLink, "99");
                            SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
                            imageView.setImageDrawable(drawable);
                            imageView.startAnimation();

                            Glide.with(WatchAudioLiveActivity.this)
                                    .load(RayziUtils.getImageFromNumber(giftData.getCount()))
                                    .into(binding.imgSvgaGiftCount);

                            String name = jsonObject.optString("userName", "User");
                            String receivername = jsonObject.optString("receiverUserName", "User");

                            String receiverNames = receivername
                                    .replace("[", "")
                                    .replace("]", "");
                            binding.tvSvgaGiftUserName.setText(name + " Sent a gift to " + receiverNames);
                            binding.lytSvgagift.setVisibility(VISIBLE);

                            long animationDurationMillis = svgaVideoEntity.getFrames() / svgaVideoEntity.getFPS() * 1000L;

                            new Handler().postDelayed(() -> {
                                binding.svgaImage.setVisibility(GONE);
                                binding.lytSvgagift.setVisibility(GONE);
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
            runOnUiThread(() -> {

                if (args[0] != null) {
                    Object args1 = args[0];
                    Log.d(TAG, "viewListner : " + args1.toString());

                    try {
                        JSONArray jsonArray = new JSONArray(args1.toString());
                        JSONArray finalArray = new JSONArray();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getBoolean("isAdd")) {
                                finalArray.put(jsonObject);
                            }
                        }

                        viewModel.liveViewUserAdapter.addData(finalArray);
//                        viewModel.liveViewUserAdapter.addData(jsonArray);
                        binding.tvViewUserCount.setText(String.valueOf(finalArray.length()));
                        Log.d(TAG, "views2 : " + jsonArray);
                    } catch (JSONException e) {
                        Log.d(TAG, "207: ");
                        e.printStackTrace();
                    }

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("blocked", blockedUsersList);
                        jsonObject.put("liveStreamingId", host.getLiveStreamingId());
                        Log.d(TAG, "onView: data send " + jsonObject);
                        MySocketManager.getInstance().getSocket().emit(Const.EVENT_BLOCK, jsonObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                binding.layEntry.setVisibility(VISIBLE);

                SVGAImageView imageView = binding.svgImage;
                SVGAParser parser = new SVGAParser(WatchAudioLiveActivity.this);

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
                                    binding.svgaImage.setVisibility(GONE);
                                    binding.layEntry.setVisibility(GONE);
                                    binding.svgaImage.clear();

                                    isEntryEffectRunning = false;
                                    triggerNextEntryEffect();  // Trigger next effect after animation ends
                                }, animationDurationMillis);
                            }

                            @Override
                            public void onError() {

                                binding.layEntry.setVisibility(GONE);
                                binding.svgaImage.clear();
                                triggerNextEntryEffect();  // Continue even if an error occurs
                            }
                        }, null
                );

                // Update UI for user details
                binding.userName.setText(userName);
                Glide.with(WatchAudioLiveActivity.this).load(userImage).circleCrop().into(binding.userImage);
                Glide.with(WatchAudioLiveActivity.this).load(avatarFrame != null && !avatarFrame.isEmpty() ? BuildConfig.BASE_URL + avatarFrame : "").into(binding.avatarFrameImage);

                Animation animation = AnimationUtils.loadAnimation(WatchAudioLiveActivity.this, R.anim.slide_in_right);
                animation.setFillAfter(true);
                binding.nameLyt.startAnimation(animation);

            } catch (JSONException | MalformedURLException e) {
                e.printStackTrace();
                isEntryEffectRunning = false;
                binding.layEntry.setVisibility(GONE);
                binding.svgaImage.clear();
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
            if (args[0] != null) {

                runOnUiThread(() -> {
                    String data = args[0].toString();
                    Log.d(TAG, "onLessParticipants: data ==== " + data);
                    if (data.equals(MY_UID)){
                        rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
                        selfPosition = -1;
                    }
                });

            }
        }

        @Override
        public void onMuteSeat(Object[] args) {
            Log.d(TAG, "onMuteSeat: " + args[0].toString());
            if (args[0] != null) {
                runOnUiThread(() -> {
                    String data = args[0].toString();
                    try {
                        JSONObject json = new JSONObject(data);
                        int seatPosition = json.getInt("position");

                        if (json.getInt("position") == -1) {
                            int mute = json.getInt("mute");
                            Log.d("mute======", "onMuteSeat: ====" + mute);
                            if (json.getInt("mute") == 1 || json.getInt("mute") == 2) {
                                binding.ivMute.setVisibility(View.VISIBLE);
                            } else {
                                binding.ivMute.setVisibility(View.GONE);
                            }
                        }


                        if (json.getInt("position") != -1) {
                            if (json.has("agoraId")) {
                                if (bookedSeatItemList.get(json.getInt("position")).getAgoraUid() == json.getInt("agoraId")) {
                                    int mute = json.getInt("mute");
                                    String seatUserId = bookedSeatItemList.get(seatPosition).getUserId();
                                    String localUserId = sessionManager.getUser().getId();
                                    if (seatUserId != null && seatUserId.equals(localUserId)) {
                                        if (mute == 2) {
                                            isMuteByHost = true;
                                            viewModel.isMuted = true;
                                            binding.btnMute.setEnabled(false);
                                            binding.btnMute.setImageDrawable(ContextCompat.getDrawable(WatchAudioLiveActivity.this, R.drawable.mute_blocked));
                                        } else if (mute == 1) {
                                            isMuteByHost = false;
                                            viewModel.isMuted = true;
                                            binding.btnMute.setEnabled(true);
                                            binding.btnMute.setImageDrawable(ContextCompat.getDrawable(WatchAudioLiveActivity.this, R.drawable.ic_mute));
                                        } else {
                                            isMuteByHost = false;
                                            viewModel.isMuted = false;
                                            binding.btnMute.setEnabled(true);
                                            binding.btnMute.setImageDrawable(ContextCompat.getDrawable(WatchAudioLiveActivity.this, R.drawable.ic_unmute));
                                        }
                                        rtcEngine().muteLocalAudioStream(viewModel.isMuted);
                                        Log.d(TAG, "onMuteSeat: isMute isMuteByHost===== " + isMuteByHost);
                                        Log.d(TAG, "onMuteSeat: isMute viewModel.isMuted===== " + viewModel.isMuted);
                                    }
                                }
                            }
                        } else {
                            if (json.getInt("mute") == 1 || json.getInt("mute") == 2) {
                                binding.ivMute.setVisibility(VISIBLE);
                            } else {
                                binding.ivMute.setVisibility(GONE);
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
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
                        host.setBackground(image);
                        Glide.with(WatchAudioLiveActivity.this)
                                .load(BuildConfig.BASE_URL + image)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(binding.mainImg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        @Override
        public void onSeat(Object[] args) {
            if (args[0] != null) {
                Log.d(TAG, "onSeat: listener ma listen thya che =================" + args[0].toString());
                Log.d("onAudioVolumeIndication", ": seat listner" + args[0]);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = args[0].toString();
                        Log.d("onAudioVolumeIndication", "initLister: usr sty1 " + data);
                        JsonParser parser = new JsonParser();
                        JsonElement mJson = parser.parse(data);
                        Log.d("onAudioVolumeIndication", "initLister: usr sty2 " + mJson);
                        Gson gson = new Gson();

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(data);
                            hostExists = jsonObject.getBoolean("isHostExists");

                            JSONObject audioConfig = jsonObject.optJSONObject("audioConfig");
                            int isHostMute = (audioConfig != null) ? audioConfig.optInt("isHostMute", 0) : 0;

                            Log.d("mute======", "seat: ========" + isHostMute);

//                            if (isHostMute == 1) {
//                                binding.ivMute.setVisibility(VISIBLE);
//                            }else {
//                                binding.ivMute.setVisibility(GONE);
//                            }
                            Log.d("&&&&&", "run: === watch setishostmute + " + isHostMute);
                            Log.d(TAG, "run: hostExists==== hostExists" + hostExists);

                            if (hostExists) {
                                binding.mainHostProfileImage.setUserImage(host.getImage(), host.getAvatarFrameImage(), 20);
                                binding.mainHostProfileImage.setVisibility(VISIBLE);
                                binding.ivPlaceholder.setVisibility(GONE);
                            } else {
                                binding.ivPlaceholder.setVisibility(VISIBLE);
                                binding.mainHostProfileImage.setVisibility(GONE);
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }


                        PkAudioLiveUserRoot.UsersItem userData = gson.fromJson(mJson, PkAudioLiveUserRoot.UsersItem.class);
                        host.setSeat(userData.getSeat());
                        binding.tvRcoins.setText(String.valueOf(userData.getRCoin()));
                        bookedSeatItemList = userData.getSeat();
                        runOnUiThread(() -> {
                            if (host.getSeat().size() >= 15) {
                                gridLayoutManager.setSpanCount(5); // Change to 5 columns when item count is 16 or more
                            } else {
                                gridLayoutManager.setSpanCount(4); // Default back to 4 columns for less than 16 items
                            }
                            seatAdapter.updateData(userData.getSeat());
                        });
                        coHostList.clear();
                        for (int i = 0; i < userData.getSeat().size(); i++) {
                            if (userData.getSeat().get(i).getUserId() != null) {
                                coHostList.add(userData.getSeat().get(i));
                            }
                        }
                    }
                });
            }
            /*if (args[1] != null) { // todo aa sukam htu @dhruvil
                String data = args[1].toString();
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if (jsonObject.has("agoraId") && jsonObject.has("mute")) {
                        int agoraId = Integer.parseInt(jsonObject.getString("agoraId"));
                        int isMute = jsonObject.getInt("mute");
                        Log.d(TAG, "onSeat: args[1] == agoraId " + agoraId);
                        Log.d(TAG, "onSeat: args[1] == isMute " + isMute);
                       // rtcEngine().muteRemoteAudioStream(agoraId, isMute == 1 || isMute == 2);
                      //  binding.btnMute.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_grey)));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }*/

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
                        Log.d(TAG, "onBlock: BBKK blocklist " + blockedList.length());
                        for (int i = 0; i < blockedList.length(); i++) {
                            Log.d(TAG, "block user : " + blockedList.get(i).toString());
                            if (blockedList.get(i).toString().equals(sessionManager.getUser().getId())) {
                                Toast.makeText(WatchAudioLiveActivity.this, getString(R.string.you_are_blocked_by_host), Toast.LENGTH_SHORT).show();
                                new Handler(Looper.myLooper()).postDelayed(() -> confirmEndLive(), 500);
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
            Log.d(TAG, "onBlock:onBanned " + args[0].toString());
            runOnUiThread(() -> {
                if (args[0] != null) {
                    Object data = args[0];
                    try {
                        JSONObject jsonObject = new JSONObject(data.toString());
                        JSONArray blockedList = jsonObject.getJSONArray("blocked");
                        Log.d(TAG, "onBlock: BBKK blocklist " + blockedList.length());
                        for (int i = 0; i < blockedList.length(); i++) {
                            Log.d(TAG, "block user : " + blockedList.get(i).toString());
                            if (blockedList.get(i).toString().equals(sessionManager.getUser().getId())) {
                                Toast.makeText(WatchAudioLiveActivity.this, getString(R.string.you_are_blocked_by_host), Toast.LENGTH_SHORT).show();
                                new Handler(Looper.myLooper()).postDelayed(() -> confirmEndLive(), 500);
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

        }

        @Override
        public void onBlockuseralert(Object[] args) {
            Log.d(TAG, "onBlock:onBlockuseralert " + args[0].toString());
            runOnUiThread(() -> {
                Toast.makeText(WatchAudioLiveActivity.this, getString(R.string.you_are_blocked_by_host), Toast.LENGTH_SHORT).show();
                confirmEndLive();
            });
        }

        @Override
        public void onGetUser(Object[] args) {
            Log.d(TAG, "onGetUser: " + args[0].toString());
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
                        if (userData.getUserId().equals(host.getLiveUserId())) {
                            userProfileBottomSheet.show(false, userData, host.getLiveStreamingId(), true);
                        } else {
                            userProfileBottomSheet.show(false, userData, "", true);
                        }
                        customDialogClass.dismiss();
                    }
                }
            });

        }

        @Override
        public void onGetUser2(Object[] args) {

        }

        @Override
        public void onInvite(Object[] args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (args[0] != null) {
                        try {
                            JSONObject jsonObject1 = new JSONObject(args[0].toString());

                            Log.d(TAG, "call:inviteListner " + args[0]);
                            String id = jsonObject1.getString("userId");
                            if (id.equalsIgnoreCase(sessionManager.getUser().getId())) {
                                new PopupBuilder(WatchAudioLiveActivity.this).showPkRequestPopup(getString(R.string.audio_request_received_from) + host.getName(), host.getImage(), host.getAvatarFrameImage(), "Accept", "Decline", new PopupBuilder.OnMultButtonPopupLister() {
                                    @Override
                                    public void onClickCountinue() {
                                        JsonObject jsonObject = new JsonObject();
                                        try {
                                            jsonObject.addProperty("position", jsonObject1.getInt("position"));
                                            jsonObject.addProperty("liveUserMongoId", host.getId());
                                            jsonObject.addProperty("userId", sessionManager.getUser().getId());
                                            jsonObject.addProperty("name", sessionManager.getUser().getName());
                                            jsonObject.addProperty("country", sessionManager.getUser().getCountry());
                                            jsonObject.addProperty("agoraUid", MY_UID);
                                            jsonObject.addProperty("liveStreamingId", host.getLiveStreamingId());
                                            jsonObject.addProperty("image", sessionManager.getUser().getImage());
                                            jsonObject.addProperty("avatarFrame", sessionManager.getUser().getAvatarFrameImage());
                                            MySocketManager.getInstance().getSocket().emit(Const.EVENT_ADD_PARTICIPATED, jsonObject);
                                            selfPosition = jsonObject1.getInt("position");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                    @Override
                                    public void onClickCancel() {


                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

            });

        }

        @Override
        public void onLiveEnd(Object[] argr) {
            if (argr[0] != null) {
                if (!isFinishing()) {
                    confirmEndLive();
                }
            }
        }

        @Override
        public void onReactionReceived(Object[] args1) {
            Log.d(TAG, "onReactionRecived: ");
            runOnUiThread(() -> {
                if (args1[0] != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(args1[0].toString());
                        if (jsonObject.getInt("position") == -1) {
                            UserRoot.User user = new Gson().fromJson(jsonObject.getString("user"), UserRoot.User.class);
                            LiveStramComment liveStramComment = new LiveStramComment("", user, false, host.getLiveStreamingId(), jsonObject.getString("image"), "reaction", "");

                            viewModel.liveStramCommentAdapter.addSingleComment(liveStramComment);
                            scrollAdapterLogic();

                        } else if (jsonObject.getInt("position") == -2) {
                            setUpReaction(jsonObject.getString("image"), binding.imgHostReaction, 7000);
                        } else {
                            RecyclerView.LayoutManager layoutManager = binding.rvSeat.getLayoutManager();
                            if (layoutManager instanceof LinearLayoutManager) {
                                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                                int position = jsonObject.getInt("position"); // Replace 0 with the position of the item you want
                                // Get the View at the specified position from the LayoutManager
                                View itemView = linearLayoutManager.findViewByPosition(position);
                                // Now you can use itemView to access the ViewBinding object if you have one
                                if (itemView != null) {
                                    @NonNull ItemSeatBinding seatBinding = DataBindingUtil.bind(itemView);
                                    PkAudioLiveUserRoot.UsersItem.SeatItem seatItem = seatAdapter.getList().get(position);
                                    setUpReaction(jsonObject.getString("image"),seatItem, seatBinding.imgHostReaction,7000);
                                }
                            }

                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        @Override
        public void onRoomNameChange(Object[] args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject(args[0].toString());
                        binding.tvName.setText(jsonObject.getString("roomName"));
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
                    host.setRoomImage(args[0].toString());
                    Glide.with(WatchAudioLiveActivity.this).load(args[0].toString()).into(binding.imgProfile);
                }
            });
        }

    };

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
                jsonObject.put("liveStreamingId", host.getLiveStreamingId());
                jsonObject.put("userId", sessionManager.getUser().getId());
                MySocketManager.getInstance().getSocket().emit(Const.LIVE_REJOIN, jsonObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    };
    private BottomSheetReactions bottomSheetReactions;
    private boolean isUserJoined = false;
    private boolean firstTime = false;

    private void scrollAdapterLogic() {
        binding.rvComments.scrollToPosition(0);
    }

    private void updateSeatFunction(SeatItem seat) {
//        becomeHost();
        if (seat.isMute() == 1) {
            if (seat.getUserId() != null && seat.getUserId().equalsIgnoreCase(sessionManager.getUser().getId()))
                rtcEngine().muteLocalAudioStream(seat.isMute() == 1);
            rtcEngine().enableLocalAudio(false);
            Toast.makeText(this, "muted", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_watch_audio_live);

        MySocketManager.getInstance().addAudioRoomHandler(audioRoomHandler);
        MySocketManager.getInstance().addSocketConnectHandler(socketConnectHandler);

        giftViewModel = ViewModelProviders.of(this, new ViewModelFactory(new EmojiSheetViewModel()).createFor()).get(EmojiSheetViewModel.class);
        ReactionsViewModel reactionsViewModel = ViewModelProviders.of(this, new ViewModelFactory(new ReactionsViewModel()).createFor()).get(ReactionsViewModel.class);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new WatchLiveViewModel()).createFor()).get(WatchLiveViewModel.class);

        sessionManager = new SessionManager(this);
        binding.setViewModel(viewModel);
        viewModel.initLister();
        giftViewModel.initEmojiSheet(this);
        giftViewModel.getGiftCategory();

        bottomSheetReactions = new BottomSheetReactions(this);
        reactionsViewModel.loadReactions(bottomSheetReactions::loadData);

        Intent intent = getIntent();
        String userStr = intent.getStringExtra(Const.DATA);
        boolean isNotification = intent.getBooleanExtra(Const.isNotification, false);

        if (isNotification) {
            ((MainApplication) getApplication()).initAgora(WatchAudioLiveActivity.this);
        }

        if (userStr != null && !userStr.isEmpty()) {
            host = new Gson().fromJson(userStr, PkAudioLiveUserRoot.UsersItem.class);
            token = host.getToken();

            binding.tvRcoins.setText(String.valueOf(host.getRCoin()));
            binding.tvName.setText(host.getRoomName());
            RayziUtils.marqueeText(binding.tvName);
            binding.tvUniqueId.setText(getString(R.string.id) + host.getUniqueId());
            Glide.with(this).load(host.getRoomImage()).apply(MainApplication.requestOptions).circleCrop().into(binding.imgProfile);

            gridLayoutManager = new GridLayoutManager(this, 4);
            binding.rvSeat.setLayoutManager(gridLayoutManager);
            seatAdapter = new SeatAdapter(WatchAudioLiveActivity.this, sessionManager);
            binding.rvSeat.setAdapter(seatAdapter);
//            binding.mainHostProfileImage.setUserImage(host.getImage(), host.getAvatarFrameImage(), 20);
            binding.mainHostnameCount.setText(host.getName());

            if (host.getBackground() != null && !host.getBackground().isEmpty()) {
                Glide.with(WatchAudioLiveActivity.this).load(host.getBackground()).thumbnail(Glide.with(WatchAudioLiveActivity.this).load(BuildConfig.BASE_URL + host.getBackground())).into(binding.mainImg);
            } else {
                Glide.with(WatchAudioLiveActivity.this).load(R.drawable.bg4).into(binding.mainImg);
            }

            Log.d(TAG, "onCreate: " + host.getBackground());

            if (isMyServiceRunning()) {
                stopService(new Intent(WatchAudioLiveActivity.this, FloatingButtonService.class));
            }
            seatAdapter.addData(host.getSeat());
            PkAudioLiveUserRoot.UsersItem.SeatItem selfPos = getSelfPositionFromSeat();

            if (selfPos != null) {
                MY_UID = selfPos.getAgoraUid();
            } else {
                MY_UID = new Random().nextInt(500) + 2;
            }


            initView();
            if (host.getPrivateCode() != 0) {

                float radius = 1f;
                View decorView = getWindow().getDecorView();
                ViewGroup rootView = decorView.findViewById(android.R.id.content);
                Drawable windowBackground = decorView.getBackground();

                binding.blurView.setupWith(rootView, new RenderScriptBlur(this)) // or RenderEffectBlur
                        .setFrameClearDrawable(windowBackground) // Optional
                        .setBlurRadius(radius);

                new BottomSheetAudioRoomPasscode(this, host, new BottomSheetAudioRoomPasscode.OnWelcomeMessageSubmittedListener() {
                    @Override
                    public void OnWelcomeMessageSubmitted(int privateCode) {
                        if (privateCode != 0) {
                            addLessView(true);
                            if (selfPos == null) {
                                joinChannel();
                            }
                            binding.blurView.setVisibility(GONE);
                        } else {
                            rtcEngine().leaveChannel();
                            finish();
                        }
                    }
                });
            } else {
                addLessView(true);
                if (selfPos == null) {
                    joinChannel();
                }
            }

            // becomeHost();
            initLister();


//            bookedSeatItemList=host.getSeat();
//            updateSeat(bookedSeatItemList);

            binding.rvGift.setAdapter(giftReceiveAdapter);

            if (host.getRoomWelcome() != null) {
                viewModel.liveStramCommentAdapter.addSingleComment(null);
                LiveStramComment liveStramComment1 = new LiveStramComment("Announcement : " + host.getRoomWelcome(), sessionManager.getUser(), true, host.getLiveStreamingId(), "", "comment", "");
                viewModel.liveStramCommentAdapter.addSingleComment(liveStramComment1);
            }else {
                viewModel.liveStramCommentAdapter.addSingleComment(null);
                LiveStramComment liveStramComment1 = new LiveStramComment(getString(R.string.announcement_welcome_to_room), sessionManager.getUser(), true, host.getLiveStreamingId(), "", "comment", "");
                viewModel.liveStramCommentAdapter.addSingleComment(liveStramComment1);
            }
            LiveStramComment liveStramComment = new LiveStramComment("", sessionManager.getUser(), true, host.getLiveStreamingId(), "", "comment", "");
            if (!sessionManager.getBooleanValue("isUserKeep")) {
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_COMMENT_AUDIO, new Gson().toJson(liveStramComment));
            }
            binding.rvComments.scrollToPosition(viewModel.liveStramCommentAdapter.getItemCount() - 1);

            Log.d("&&&&&", "onCreate: ====watch ishostmute = " + host.getAudioRoomConfig().isHostMute());


            if (host.getAudioRoomConfig()!=null&&host.getAudioRoomConfig().isHostMute()==1) {
                binding.ivMute.setVisibility(VISIBLE);
            } else {
                binding.ivMute.setVisibility(GONE);
            }

          handleIfIamHost(selfPos);
        }
    }

    private void handleIfIamHost(PkAudioLiveUserRoot.UsersItem.SeatItem selfPos) {
        if (selfPos != null) {
            MY_UID = selfPos.getAgoraUid();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("position", selfPos.getPosition());
            jsonObject.addProperty("liveUserMongoId", host.getId());
            jsonObject.addProperty("liveStreamingId", host.getLiveStreamingId());
            jsonObject.addProperty("userId", sessionManager.getUser().getId());
            jsonObject.addProperty("name", sessionManager.getUser().getName());
            jsonObject.addProperty("country", sessionManager.getUser().getCountry());
            jsonObject.addProperty("agoraUid", MY_UID);
            jsonObject.addProperty("mute", selfPos.isMute());
            jsonObject.addProperty("image", sessionManager.getUser().getImage());
            jsonObject.addProperty("avatarFrame", sessionManager.getUser().getAvatarFrameImage());

            MySocketManager.getInstance().getSocket().emit(Const.EVENT_ADD_PARTICIPATED, jsonObject);
            Log.d(TAG, "doWork: add partiicpate emit " + jsonObject);

            becomeHost(selfPos, false);
            selfPosition = selfPos.getPosition();


            if (selfPos.isMute() == 2) {
                isMuteByHost = true;
                viewModel.isMuted = true;
                binding.btnMute.setEnabled(false);
                binding.btnMute.setImageDrawable(ContextCompat.getDrawable(WatchAudioLiveActivity.this, R.drawable.mute_blocked));
            } else if (selfPos.isMute() == 1) {
                isMuteByHost = false;
                viewModel.isMuted = true;
                binding.btnMute.setEnabled(true);
                binding.btnMute.setImageDrawable(ContextCompat.getDrawable(WatchAudioLiveActivity.this, R.drawable.ic_mute));
            } else {
                isMuteByHost = false;
                viewModel.isMuted = false;
                binding.btnMute.setEnabled(true);
                binding.btnMute.setImageDrawable(ContextCompat.getDrawable(WatchAudioLiveActivity.this, R.drawable.ic_unmute));
            }
            rtcEngine().muteLocalAudioStream(viewModel.isMuted);
        }
    }

    private void becomeHost(PkAudioLiveUserRoot.UsersItem.SeatItem seatItem, boolean fromOnSeatClicked) {
        if (rtcEngine() != null) {
            isHost = true;
            rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
            rtcEngine().enableAudio();
            rtcEngine().disableVideo();
            Log.d(TAG, "becomeHost: isMute ======n " + seatItem.isMute());
            Log.d(TAG, "becomeHost: isMuteByHost === " + isMuteByHost);
//            if (!isMuteByHost) {
//                new Handler().postDelayed((Runnable) () -> {
//                    if (seatItem.isMute() == 1) {
//                        Log.d(TAG, "becomeHost: if ma jay che ==");
//                        binding.btnMute.setImageDrawable(ContextCompat.getDrawable(WatchAudioLiveActivity.this, R.drawable.mute_blocked));
//                        binding.btnMute.setEnabled(false);
//                        if (rtcEngine() != null) {
//                            rtcEngine().muteLocalAudioStream(true);
//                            Log.d(TAG, "becomeHost:  rtcEngine().muteLocalAudioStream(true); ====== ");
//                        }
//                    } else {
//                        binding.btnMute.setImageDrawable(ContextCompat.getDrawable(WatchAudioLiveActivity.this, R.drawable.ic_unmute));
//                        rtcEngine().muteLocalAudioStream(false);
//                    }
//                }, 500);
//            }
        }
    }

    private void addLessView(boolean isAdd) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("liveStreamingId", host.getLiveStreamingId());
            jsonObject.put("liveUserMongoId", host.getId());
            jsonObject.put("userId", sessionManager.getUser().getId());
            jsonObject.put("isVIP", sessionManager.getUser().isIsVIP());
            jsonObject.put("image", sessionManager.getUser().getImage());
            jsonObject.put("name", sessionManager.getUser().getName());
            jsonObject.put("gender", sessionManager.getUser().getGender());
            jsonObject.put("country", sessionManager.getUser().getCountry());
            jsonObject.put("userName", sessionManager.getUser().getName());
            jsonObject.put("avatarFrame", sessionManager.getUser().getAvatarFrameImage());
            jsonObject.put("entrySvga", sessionManager.getUser().getSvgaImage());
            jsonObject.put("isUserBackgroundLive", sessionManager.getIsUserBackgroundLive());
            jsonObject.put("fcmToken",sessionManager.getUser().getFcmToken());
            jsonObject.put("notification",sessionManager.isNotificationOn());
            Log.d(TAG, "addLessView: =====" + sessionManager.getUser().getFcmToken());
            if (isAdd) {
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_ADDVIEW, jsonObject);
            } else {
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_LESSVIEW, jsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void joinChannel() {
        try {
            if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
                token = null; // default, no token
            }
            String tkn = RtcTokenBuilderSample.main(host.getChannel() + "audio", sessionManager.getSetting().getAgoraKey(), sessionManager.getSetting().getAgoraCertificate());
            rtcEngine().setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            rtcEngine().disableVideo();
            rtcEngine().enableAudioVolumeIndication(1000, 3, false); // atyare kon bole chhe ae detect karva mate
            // configVideo();
            Log.d(TAG, "joinChannel: ");
            rtcEngine().joinChannel(tkn, host.getChannel() + "audio", "", MY_UID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        emojiBottomsheetFragment = new EmojiBottomsheetFragment(true);
        userProfileBottomSheet = new UserProfileBottomSheet(this);

        if (rtcEngine() == null) {
            Log.d(TAG, "initView: rtc engine null");
            return;
        }
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
        isHost = false;

    }

    @Override
    public void onBackPressed() {
        endLive();
    }

    private void endLive() {
        Log.d(TAG, "endLive: ma jay che ==== ");
        new PopupBuilder(this).showLiveEndPopup(new PopupBuilder.OnMultButtonPopupLister() {
            @Override
            public void onClickCountinue() {
                confirmEndLive();
                sessionManager.setIsUserBackgroundLive(false);
                finish();
            }

            @Override
            public void onClickCancel() {// todo
                if (checkOverlayDisplayPermission()) {
                    rtcEngine().muteLocalAudioStream(viewModel.isMuted);
                    sessionManager.setIsUserBackgroundLive(true);
                    sessionManager.saveUserAudioBgModel(host);
                    sessionManager.saveBooleanValue("isUserKeep", true);
                    startService(new Intent(WatchAudioLiveActivity.this, FloatingButtonService.class).putExtra("image", host.getImage()));
                    finish();
                } else {
                    requestOverlayDisplayPermission();
                }

            }
        });

    }

    private void confirmEndLive() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("position", selfPosition);
        jsonObject.addProperty("liveUserMongoId", host.getId());
        jsonObject.addProperty("liveStreamingId", host.getLiveStreamingId());

        MySocketManager.getInstance().getSocket().emit(Const.EVENT_LESS_PARTICIPATED, jsonObject);

        if (rtcEngine() != null) {
            rtcEngine().leaveChannel();
        }

        addLessView(false);
        sessionManager.saveBooleanValue("isUserKeep", false);
        try {
            //removeRtcVideo(0, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //mVideoGridContainer.removeUserVideo(0, true);

        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MySocketManager.getInstance().removeAudioRoomHandler(audioRoomHandler);
        MySocketManager.getInstance().removeSocketConnectHandler(socketConnectHandler);
        statsManager().clearAllData();
    }

    private void initLister() {

        binding.imgGame.setOnClickListener(v -> {
            new BottomSheetGameList(this, gameItem -> {
                if (gameItem.getName().contains("Roulette")) {
                    new BottomSheetGameCasino(this, gameItem.getLink(), new BottomSheetGameCasino.OnDialogDismissListener() {
                        @Override
                        public void onDismiss() {
                            MySocketManager.getInstance().getSocket().emit(Const.USER_COIN_UPDATE, sessionManager.getUser().getId());
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

            PkAudioLiveUserRoot.UsersItem.SeatItem selfItem = getSelfPositionFromSeat();
            if (selfItem != null && selfItem.isMute() == 2) {
                Toast.makeText(this, "You cant Unmute your slef", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.isMuted = !viewModel.isMuted;
            if (rtcEngine() != null) {
                int kk = rtcEngine().muteLocalAudioStream(viewModel.isMuted);
                Log.e(TAG, "initLister: kkk  " + kk);
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("position", selfPosition);
            jsonObject.addProperty("liveUserMongoId", host.getId());
            jsonObject.addProperty("liveUserId", host.getLiveUserId());
            jsonObject.addProperty("liveStreamingId", host.getLiveStreamingId());
            jsonObject.addProperty("agoraId", host.getAgoraUID());
            jsonObject.addProperty("mute", (viewModel.isMuted) ? 1 : 0);
            jsonObject.addProperty("mutedUserId", sessionManager.getUser().getId());
            if (selfPosition != -1) {
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_MUTESEAT, jsonObject);
            }
            if (viewModel.isMuted) {
                binding.btnMute.setImageDrawable(ContextCompat.getDrawable(WatchAudioLiveActivity.this, R.drawable.ic_mute));
            } else {
                binding.btnMute.setImageDrawable(ContextCompat.getDrawable(WatchAudioLiveActivity.this, R.drawable.ic_unmute));
            }
        });

        viewModel.clickedComment.observe(this, user -> {
            getUser(user.getId());
        });
        viewModel.clickedUser.observe(this, user -> {
            try {
                getUser(user.get("userId").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        binding.lytHost.setOnClickListener(v -> getUser(host.getLiveUserId()));

        binding.ivShare.setOnClickListener(v -> {
            binding.ivShare.setEnabled(false);
            BranchUniversalObject buo = new BranchUniversalObject().setCanonicalIdentifier("content/12345").setTitle("Watch My room").setContentDescription("By : " + sessionManager.getUser().getName()).setContentImageUrl(sessionManager.getUser().getImage()).setContentMetadata(new ContentMetadata().addCustomMetadata("type", "AUDIO_LIVE").addCustomMetadata(Const.DATA, new Gson().toJson(host)));
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
                jsonObject.put("liveStreamingId", host.getLiveStreamingId());
                jsonObject.put("position", selfPosition);// seatpos
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
                    Toast.makeText(WatchAudioLiveActivity.this, getString(R.string.you_not_have_enough_diamonds_to_send_gift), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!giftViewModel.userListAdapter.getUsers().isEmpty()) {
                    List<String> selectedUsers = giftViewModel.userListAdapter.getUsers().stream()
                            .filter(UserSelectableClass::isSelected)
                            .map(user -> user.getSeatItem().getUserId())
                            .collect(Collectors.toList());

                    List<String> selectedUsersName = giftViewModel.userListAdapter.getUsers().stream()
                            .filter(UserSelectableClass::isSelected)
                            .map(user -> user.getSeatItem().getName())
                            .collect(Collectors.toList());

                    if (selectedUsers.isEmpty()) {
                        Toast.makeText(this, getString(R.string.select_at_least_one_user), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("senderUserId", sessionManager.getUser().getId());
                        jsonObject.put("receiverUserId", Arrays.toString(selectedUsers.toArray()));
                        jsonObject.put("hostId", host.getLiveUserId());
                        jsonObject.put("liveStreamingId", host.getLiveStreamingId());
                        jsonObject.put("userName", sessionManager.getUser().getName());
                        jsonObject.put("receiverUserName", Arrays.toString(selectedUsersName.toArray()));
                        jsonObject.put("coin", giftItem.getCoin() * giftItem.getCount());
                        jsonObject.put("gift", new Gson().toJson(giftItem));
                        jsonObject.put("giftCount", giftItem.getCount());
                        jsonObject.put("timeStamp", System.currentTimeMillis());
                        jsonObject.put("liveType","audio");
                        int i = selectedUsers.size();
                        int totalGiftCoin = giftItem.getCoin() * i;
                        double totalDiamond = sessionManager.getUser().getDiamond();

                        if (totalDiamond >= totalGiftCoin) {
                            MySocketManager.getInstance().getSocket().emit(Const.EVENT_NORMALUSER_GIFT, jsonObject);
                        } else {
                            Toast.makeText(WatchAudioLiveActivity.this, getString(R.string.you_not_have_enough_diamonds_to_send_gift), Toast.LENGTH_SHORT).show();
                        }
                        Log.d(TAG, "initLister: =========== gift emited");
                        emojiBottomsheetFragment.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.don_t_have_user_to_sent_a_gift_wait_for_user), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnGift.setOnClickListener(v -> {
            if (!emojiBottomsheetFragment.isAdded()) {
                giftViewModel.users.clear();
                giftViewModel.users.add(new UserSelectableClass(new PkAudioLiveUserRoot.UsersItem.SeatItem(host.getImage(), host.getCountry(), true,
                        "Host", false, host.getAgoraUID(), 0, true, host.getId(), -1, false, host.getLiveUserId())));
                host.getSeat().stream().filter(PkAudioLiveUserRoot.UsersItem.SeatItem::isReserved).map(UserSelectableClass::new).forEach(giftViewModel.users::add);
                emojiBottomsheetFragment.show(getSupportFragmentManager(), "emojifragfmetn");
            }
        });

        seatAdapter.setOnSeatClick(new SeatAdapter.onSeatClick() {
            @Override
            public void OnClickSeat(PkAudioLiveUserRoot.UsersItem.SeatItem seatItem, int position) {
//                Toast.makeText(WatchAudioLiveActivity.this, "" + position, Toast.LENGTH_SHORT).show();

                Log.d(TAG, "OnClickSeat: isMute ====  " + seatItem.isMute());
                if (!isMuteByHost) {
                    rtcEngine().muteLocalAudioStream(seatItem.isMute() != 0);
                }
                doWork(seatItem, position);

            }

        });

    }

    private PkAudioLiveUserRoot.UsersItem.SeatItem getSelfPositionFromSeat() {
        List<PkAudioLiveUserRoot.UsersItem.SeatItem> seatList = seatAdapter.getList();
        for (int i = 0; i < seatList.size(); i++) {
            if (seatList.get(i).isReserved()) {
                if (seatList.get(i).getUserId().equals(sessionManager.getUser().getId())) {

                    return seatList.get(i);
                }
            }
        }
        return null;
    }

    private void doWork(PkAudioLiveUserRoot.UsersItem.SeatItem seatItem, int i) {
        Log.d(TAG, "doWork: isReseved " + seatItem.isReserved());

        if (seatItem.isReserved() && seatItem.getUserId().equalsIgnoreCase(sessionManager.getUser().getId())) {
            new PopupBuilder(WatchAudioLiveActivity.this).showRemovePopup(() -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", i);
                jsonObject.addProperty("liveUserMongoId", host.getId());
                jsonObject.addProperty("liveStreamingId", host.getLiveStreamingId());
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_LESS_PARTICIPATED, jsonObject);
                Log.d(TAG, "doWork: become audence");
                rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
                //rtcEngine().disableAudio();
                rtcEngine().disableVideo();
                isHost = false;
                selfPosition = -1;
            });
            return;
        }

        if (seatItem.isReserved()) {
            Toast.makeText(this, R.string.please_choose_another_seat, Toast.LENGTH_SHORT).show();
        } else if (seatItem.isLock()) {
            Toast.makeText(this, R.string.this_seat_is_locked_by_host, Toast.LENGTH_SHORT).show();
        } else {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("position", i);
            jsonObject.addProperty("liveUserMongoId", host.getId());
            jsonObject.addProperty("liveStreamingId", host.getLiveStreamingId());
            jsonObject.addProperty("userId", sessionManager.getUser().getId());
            jsonObject.addProperty("name", sessionManager.getUser().getName());
            jsonObject.addProperty("country", sessionManager.getUser().getCountry());
            jsonObject.addProperty("agoraUid", MY_UID);

            int currentState = seatItem.isMute();
            if (currentState == 0) {
                currentState = viewModel.isMuted ? 1 : 0;
            }
            PkAudioLiveUserRoot.UsersItem.SeatItem selfPos = getSelfPositionFromSeat();
            if (selfPos != null && selfPos.isMute() == 2) {
                currentState = 2;
            }

            jsonObject.addProperty("mute", currentState);
            jsonObject.addProperty("image", sessionManager.getUser().getImage());
            jsonObject.addProperty("avatarFrame", sessionManager.getUser().getAvatarFrameImage());

            MySocketManager.getInstance().getSocket().emit(Const.EVENT_ADD_PARTICIPATED, jsonObject);
            Log.d(TAG, "doWork: add partiicpate emit " + jsonObject);

            becomeHost(seatItem, true);
            selfPosition = i;
            Log.d(TAG, "doWork: CURRENTSTSTE MUTE : "+currentState);
            if (currentState == 2) {
                isMuteByHost = true;
                viewModel.isMuted = true;
                binding.btnMute.setEnabled(false);
                binding.btnMute.setImageDrawable(ContextCompat.getDrawable(WatchAudioLiveActivity.this, R.drawable.mute_blocked));
            } else if (currentState == 1) {
                isMuteByHost = false;
                viewModel.isMuted = true;
                binding.btnMute.setEnabled(true);
                binding.btnMute.setImageDrawable(ContextCompat.getDrawable(WatchAudioLiveActivity.this, R.drawable.ic_mute));
            } else {
                isMuteByHost = false;
                viewModel.isMuted = false;
                binding.btnMute.setEnabled(true);
                binding.btnMute.setImageDrawable(ContextCompat.getDrawable(WatchAudioLiveActivity.this, R.drawable.ic_unmute));
               /* if (viewModel.isMuted){
                    isMuteByHost = false;
                    viewModel.isMuted = true;
                    binding.btnMute.setEnabled(true);
                    binding.btnMute.setImageDrawable(ContextCompat.getDrawable(WatchAudioLiveActivity.this, R.drawable.ic_mute));
                }else {
                    isMuteByHost = false;
                    viewModel.isMuted = false;
                    binding.btnMute.setEnabled(true);
                    binding.btnMute.setImageDrawable(ContextCompat.getDrawable(WatchAudioLiveActivity.this, R.drawable.ic_unmute));
                }*/

            }
            rtcEngine().muteLocalAudioStream(viewModel.isMuted);
        }
    }


    private void getUser(String userId) {
        customDialogClass.show();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fromUserId", sessionManager.getUser().getId());
            jsonObject.put("toUserId", userId);
            MySocketManager.getInstance().getSocket().emit(Const.EVENT_GET_USER, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClickBack(View view) {
        onBackPressed();
    }

    public void onClickSendComment(View view) {

        String comment = binding.etComment.getText().toString();
        if (!comment.isEmpty()) {
            binding.etComment.setText("");
            LiveStramComment liveStramComment = new LiveStramComment(comment, sessionManager.getUser(), false, host.getLiveStreamingId(), "", "comment", "");
            MySocketManager.getInstance().getSocket().emit(Const.EVENT_COMMENT_AUDIO, new Gson().toJson(liveStramComment));
        }
    }

    public void onclickShare(View view) {


        BranchUniversalObject buo = new BranchUniversalObject().setCanonicalIdentifier("content/12345").setTitle("Watch Live Video").setContentDescription("By : " + host.getName()).setContentImageUrl(host.getImage()).setContentMetadata(new ContentMetadata().addCustomMetadata("type", "LIVE").addCustomMetadata(Const.DATA, new Gson().toJson(host)));

        LinkProperties lp = new LinkProperties().setChannel("facebook").setFeature("sharing").setCampaign("content 123 launch").setStage("new user")

                .addControlParameter("", "").addControlParameter("", Long.toString(Calendar.getInstance().getTimeInMillis()));

        buo.generateShortUrl(this, lp, (url, error) -> {
            Log.d(TAG, "initListnear: branch url" + url);
            try {
                Log.d(TAG, "initListnear: share");
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareMessage = url;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.choose_one)));
            } catch (Exception e) {
                Log.d(TAG, "initListnear: " + e.getMessage());
                //e.toString();
            }
        });
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

//    public void onclickGiftIcon(View view) {
//        emojiBottomsheetFragment.show(getSupportFragmentManager(), "emojifragfmetn");
//    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        Log.d(TAG, "onFirstRemoteVideoDecoded: elapsed ===== " + elapsed);
    }

    private void renderRemoteUser(int uid) {
        Log.d(TAG, "renderRemoteUser: ");
//        SurfaceView surface = prepareRtcVideo(uid, false);
//        mVideoGridContainer.addUserVideoSurface(uid, surface, false);
        LiveStramComment liveStramComment = new LiveStramComment("", sessionManager.getUser(), true, host.getLiveStreamingId(), "", "comment", "");
        MySocketManager.getInstance().getSocket().emit(Const.EVENT_COMMENT_AUDIO, new Gson().toJson(liveStramComment));
        addLessView(true);
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("liveStreamingId", host.getLiveStreamingId());
//            jsonObject.put("comment", new Gson().toJson(liveStramComment));
//            getSocket().emit(Const.EVENT_COMMENT, jsonObject);
//
//            addLessView(true);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private void removeRemoteUser(int uid) {
        removeRtcVideo(uid, false);
//        mVideoGridContainer.removeUserVideo(uid, false);
    }

    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
        Log.d(TAG, "onLeaveChannel: stts" + stats);
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        Log.d(TAG, "onJoinChannelSuccess: ");
        this.uuid = uid;
        Log.d(TAG, "onJoinChannelSuccess: isUserJoined  " + isUserJoined);

//        runOnUiThread((() -> {
//            new Handler().postDelayed(() -> {
//                Log.d(TAG, "onJoinChannelSuccess: isUserJoined ====== " + isUserJoined);
//                if (isUserJoined) {
//                    Log.d(TAG, "onJoinChannelSuccess: live joined ");
//                } else {
//                    Toast.makeText(WatchAudioLiveActivity.this, "Live has ended", Toast.LENGTH_SHORT).show();
//                    confirmEndLive();
//                }
//            }, 4000);
//        }));

    }

    @Override
    public void onUserOffline(int uid, int reason) {
        Log.d(TAG, "onUserOffline: " + uid + " reason" + reason);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // removeRemoteUser(uid);
//                if (uid == 1) {
//                    endLive();
//                }
            }
        });
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        Log.d(TAG, "onUserJoined: ma jay che =================================================================");
        Log.d(TAG, "onUserJoined: " + uid + "  elapsed" + elapsed);
        binding.rvSeat.setVisibility(VISIBLE);
        binding.mining.setVisibility(GONE);
        isUserJoined = true;
    }

    @Override
    public void onLastmileQuality(int quality) {

    }

    @Override
    public void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result) {

    }

    @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
        if (!statsManager().isEnabled()) return;


    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
        if (!statsManager().isEnabled()) return;

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
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setWidth(stats.width);
        data.setHeight(stats.height);
        data.setFramerate(stats.rendererOutputFrameRate);
        data.setVideoDelay(stats.delay);
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

    }

    @Override
    public void onFirstRemoteAudioFrame(int uid, int elapsed) {
        Log.d(TAG, "onFirstRemoteAudioFrame: user join thay che ========= " + elapsed);
    }

    @Override
    public void onUserMuteAudio(int uid, boolean muted) {

    }

    @Override
    public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {
        Log.d("onAudioVolumeIndication", "onAudioVolumeIndication: ============================ NEW SESSION ============================");

        Log.d("onAudioVolumeIndication", "onAudioVolumeIndication: speakers.length  " + speakers.length);
        Log.d("onAudioVolumeIndication", "onAudioVolumeIndication: totalVolume  " + totalVolume);


        runOnUiThread(() -> {
            if (totalVolume <= 0) return;

            for (IRtcEngineEventHandler.AudioVolumeInfo info : speakers) {
                Log.d("onAudioVolumeIndication", "onAudioVolumeIndication: uid " + info.uid);
                Log.d("onAudioVolumeIndication", "onAudioVolumeIndication: volumne" + info.volume);
                if (info.uid == 1) {
                    Log.d("onAudioVolumeIndication", "onAudioVolumeIndication: host is speaking ");
                    info.uid = host.getAgoraUID();
                    info.channelId = host.getChannel();

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.animationView1.setVisibility(GONE);
                        }
                    }, 1500);
                    binding.animationView1.setVisibility(VISIBLE);

                } else {
                    if (isHost && info.uid == 0) {
                        Log.d(TAG, "onAudioVolumeIndication: I AM HOST");
                        info.uid = MY_UID;
                        info.channelId = host.getChannel();

                    } else {
                        Log.d("onAudioVolumeIndication", "onAudioVolumeIndication: OTHER SPEAKING");
                    }
                    seatAdapter.onAudioVolumeIndicationSingle(info);
                }


                if (isHost && info.uid == 0) {

                }
//                if (info.uid == 1) {
//                    info.uid = host.getAgoraUID();
//                    info.channelId = host.getChannel();
//                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            binding.animationView1.setVisibility(View.GONE);
//                        }
//                    }, 1500);
//                    binding.animationView1.setVisibility(View.VISIBLE);
//                }
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
            String tkn = RtcTokenBuilderSample.main(host.getChannel(), sessionManager.getSetting().getAgoraKey(), sessionManager.getSetting().getAgoraCertificate());
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
    public void finish() {
        super.finish();
        statsManager().clearAllData();
    }

    public void onClickReport(View view) {
        new BottomSheetReport_g(this, host.getLiveUserId(), () -> {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout, findViewById(R.id.customtoastlyt));
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();

        });
    }
    private Handler reactionHandler = new Handler();
    private Runnable currentReactionRunnable;
    private boolean isReactionRunning = false;
    public void setUpReaction(String image, PkAudioLiveUserRoot.UsersItem.SeatItem seatItem, ImageView imgHostReaction, int duration) {
        // Update the dataset
        seatItem.setReactionImage(image);
        seatItem.setReactionRunning(true);

        Glide.with(this).load(image).into(imgHostReaction);

        // Clear the reaction after the duration
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            seatItem.setReactionRunning(false);
            seatItem.setReactionImage(null);
            imgHostReaction.setImageDrawable(null);
            seatAdapter.notifyItemChanged(seatAdapter.getList().indexOf(seatItem)); // Update UI for the item
        }, duration);
    }
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

}