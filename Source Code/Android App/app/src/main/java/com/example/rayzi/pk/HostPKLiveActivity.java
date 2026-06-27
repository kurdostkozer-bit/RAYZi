package com.example.rayzi.pk;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.example.rayzi.RayziUtils.convertSecondsToHMmSs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.HostAPICall;
import com.example.rayzi.R;
import com.example.rayzi.RayziUtils;
import com.example.rayzi.SessionManager;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.agora.AgoraBaseActivity;
import com.example.rayzi.agora.stats.LocalStatsData;
import com.example.rayzi.agora.stats.RemoteStatsData;
import com.example.rayzi.agora.stats.StatsData;
import com.example.rayzi.agora.token.RtcTokenBuilderSample;
import com.example.rayzi.agora.ui.VideoGridContainer;
import com.example.rayzi.bottomsheets.BottomSheetBannedList;
import com.example.rayzi.bottomsheets.BottomSheetBeautyOptions;
import com.example.rayzi.bottomsheets.BottomSheetReport_g;
import com.example.rayzi.bottomsheets.UserProfileBottomSheet;
import com.example.rayzi.databinding.ActivityHostPkliveBinding;
import com.example.rayzi.dilog.CustomDialogClass;
import com.example.rayzi.emoji.EmojiBottomsheetFragment;
import com.example.rayzi.liveGame.bottomsheet.BottomSheetGameCasino;
import com.example.rayzi.liveGame.bottomsheet.BottomSheetGameList;
import com.example.rayzi.liveGame.bottomsheet.BottomSheetGameTeenPatti;
import com.example.rayzi.liveGame.dialog.DialogGame;
import com.example.rayzi.liveStreamming.LiveSummaryActivity;
import com.example.rayzi.modelclass.GiftRoot;
import com.example.rayzi.modelclass.GuestProfileRoot;
import com.example.rayzi.modelclass.LiveStramComment;
import com.example.rayzi.modelclass.PKLiveStramComment;
import com.example.rayzi.modelclass.StickerRoot;
import com.example.rayzi.modelclass.UpdateLiveTime;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.pk.BottomSheets.BottemSheetLiveHosts;
import com.example.rayzi.pk.BottomSheets.PkInvitationBottomSheet;
import com.example.rayzi.pk.BottomSheets.PkResultBottomSheet;
import com.example.rayzi.pk.BottomSheets.PkWaitingForResponseBottomSheet;
import com.example.rayzi.pk.viewmodel.PKLiveViewModel;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;
import com.example.rayzi.popups.PopupBuilder;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.socket.LiveHandler;
import com.example.rayzi.socket.MySocketManager;
import com.example.rayzi.socket.SocketConnectHandler;
import com.example.rayzi.utils.Filters.FilterUtils;
import com.example.rayzi.viewModel.EmojiSheetViewModel;
import com.example.rayzi.viewModel.ViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
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
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.ChannelMediaInfo;
import io.agora.rtc.video.ChannelMediaRelayConfiguration;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HostPKLiveActivity extends AgoraBaseActivity {

    private static final String TAG = "hostpkliveactivity";
    public static int LOCAL_HOST_AGORA_ID = 0;
    PkAudioLiveUserRoot.UsersItem pkAnswerLiveUser;
    ActivityHostPkliveBinding binding;
    private boolean isPkStart = false, isPkView = false, isGone = false;
    JSONArray blockedUsersList = new JSONArray();
    Handler socketHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));
    Handler timerHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));
    Handler handler = new Handler(Objects.requireNonNull(Looper.myLooper()));
    private boolean isBottomSheetOpen = false;
    private boolean isVideoDecoded = false;
    private PkAudioLiveUserRoot.UsersItem liveUser;
    private PkAudioLiveUserRoot.UsersItem localLiveUser;
    private String hostLiveStreamingId;
    private CustomDialogClass customDialogClass;
    private BottomSheetDialog pkWaitingResponsePopup;
    private PkResultBottomSheet pkResultPopup;
    private BottomSheetDialog pkInvitationPopup;
    private PkAudioLiveUserRoot.UsersItem hostDetailsForAudienceModel;
    private VideoGridContainer mVideoGridContainer;
    long animationDurationMillis;
    JSONArray blockUserList = new JSONArray();
    private String token = "";
    private String channel = "";
    private boolean isHost = true;
    private EmojiSheetViewModel giftViewModel;
    private PKLiveViewModel viewModel;
    private SessionManager sessionManager;
    JSONArray finalArray;
    HostAPICall hostAPICall;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            Call<UpdateLiveTime> call = RetrofitBuilder.create().updateLiveTime(sessionManager.getUser().getId(), liveUser.getLiveStreamingId());
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<UpdateLiveTime> call, Response<UpdateLiveTime> response) {

                    Log.e(TAG, "onResponse: " + response.body());

                }

                @Override
                public void onFailure(Call<UpdateLiveTime> call, Throwable t) {

                }
            });

            Log.e(TAG, "run: live================================");

            handler.postDelayed(this, 30000);
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
                jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
                jsonObject.put("userId", sessionManager.getUser().getId());
                MySocketManager.getInstance().getSocket().emit(Const.LIVE_REJOIN, jsonObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    };
    Runnable socketRunnable = () -> {
        socketHandler.postDelayed(HostPKLiveActivity.this.socketRunnable, 3000);
    };
    private EmojiBottomsheetFragment emojiBottomsheetFragment;
    private UserProfileBottomSheet userProfileBottomSheet;
    private int seconds = 0;
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            seconds++;
            int p1 = seconds % 60;
            int p2 = seconds / 60;
            int p3 = p2 % 60;
            p2 = p2 / 60;

            String sec;
            String hour;
            String min;
            if (p1 < 10) {
                sec = "0" + p1;
            } else {
                sec = String.valueOf(p1);
            }
            if (p2 < 10) {
                hour = "0" + p2;
            } else {
                hour = String.valueOf(p2);
            }
            if (p3 < 10) {
                min = "0" + p3;
            } else {
                min = String.valueOf(p3);
            }
            binding.tvtimer.setText(hour + ":" + min + ":" + sec);

            timerHandler.postDelayed(this, 1000);
        }
    };
    private String host1id;
    private CountDownTimer timer;

    LiveHandler liveHandler = new LiveHandler() {

        @Override
        public void onTotalRoomcoins(Object[] args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: ====video roomcoin " + args[0].toString());
                    binding.tvRcoins.setText(args[0].toString());
                }
            });
        }

        private Queue<JSONObject> giftQueue = new LinkedList<>();
        private boolean isGiftDisplaying = false;

        @Override
        public void onGift(Object[] args) {
            Log.d(TAG, "onGift: ====gift listen" );
            runOnUiThread(() -> {
                if (args.length > 0 && args[0] != null) {
                    String data = args[0].toString();
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        if (jsonObject.get("gift") != null) {
                            // Add the gift data to the queue
                            giftQueue.add(jsonObject);
                            // Start processing gifts if not already doing so
                            if (!isGiftDisplaying) {
                                processNextGift();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (args.length > 1 && args[1] != null) {  // gift sender user
                    // Log.d(TAG, "user string   : " + args[1].toString());
                    try {
                        JSONObject jsonObject = new JSONObject(args[1].toString());
                        UserRoot.User user = new Gson().fromJson(jsonObject.toString(), UserRoot.User.class);
                        if (user != null) {
                            //  Log.d(TAG, ":getted user    " + user.toString());
                            if (user.getId().equals(sessionManager.getUser().getId())) {
                                sessionManager.saveUser(user);
                                giftViewModel.localUserCoin.setValue(user.getDiamond());
                                Log.d(TAG, "giftListner: user.getDiamond() " + user.getDiamond());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


                if (args.length > 2 && args[2] != null) {   // host
                    //  Log.d(TAG, "host string   : " + args[2].toString());
                    try {
                        JSONObject jsonObject = new JSONObject(args[2].toString());
                        UserRoot.User host = new Gson().fromJson(jsonObject.toString(), UserRoot.User.class);
                        if (host != null) {
                            //  Log.d(TAG, ":getted host    " + host.toString());
                            if (sessionManager.getUser().getId().equals(host.getId())) {
                                sessionManager.saveUser(host);
                                // binding.tvDiamonds.setText(String.valueOf(host.getDiamond()));
//                                binding.tvRcoins.setText(String.valueOf(host.getRCoin()));
                                Log.d(TAG, "giftListner: host.getRCoin()" + host.getRCoin());
                                giftViewModel.localUserCoin.setValue(host.getDiamond());
                                PkAudioLiveUserRoot.UsersItem usersItem = sessionManager.getLiveUserForBackground();
                                if (usersItem != null) {
                                    usersItem.setrCoin(host.getRCoin());
                                }
                                sessionManager.saveLiveUserForBackground(usersItem);
                            } else {
//                                binding.tvRcoins.setText(String.valueOf(host.getRCoin()));
                            }
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
                    GiftRoot.GiftItem giftData = new Gson().fromJson(giftJson.get("gift").toString(), GiftRoot.GiftItem.class);
                    if (giftData != null) {
                        String finalGiftLink = null;
                        List<GiftRoot.GiftItem> giftItemList = sessionManager.getGiftsList(giftData.getCategory());
                        for (int i = 0; i < giftItemList.size(); i++) {
                            if (giftData.getId().equals(giftItemList.get(i).getId())) {
                                finalGiftLink = BuildConfig.BASE_URL + giftItemList.get(i).getImage();
                            }
                        }

                        // Display the gift based on its type
                        if (giftData.getType() == 2) {
                            displaySVGAAnimation(finalGiftLink, giftJson, giftData);
                        } else if (giftData.getType() == 0 || giftData.getType() == 1) {
                            displayImageGift(finalGiftLink, giftJson, giftData);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                isGiftDisplaying = false;
            }
        }

        private void displayImageGift(String giftLink, JSONObject jsonObject, GiftRoot.GiftItem giftData) throws JSONException {

            if (isDestroyed() || isFinishing()) return;

            Glide.with(HostPKLiveActivity.this).load(giftLink).into(binding.imgGift);
            Glide.with(HostPKLiveActivity.this).load(RayziUtils.getImageFromNumber(giftData.getCount())).into(binding.imgGiftCount);
            String name = jsonObject.getString("userName");
            binding.tvGiftUserName.setText(name + getString(R.string.send_a_gift));
            binding.lytGift.setVisibility(VISIBLE);
            binding.tvGiftUserName.setVisibility(VISIBLE);

            new Handler().postDelayed(() -> {
                binding.lytGift.setVisibility(GONE);
                binding.tvGiftUserName.setVisibility(GONE);
                binding.tvGiftUserName.setText("");
                binding.imgGift.setImageDrawable(null);
                binding.imgGiftCount.setImageDrawable(null);

                // Process the next gift
                processNextGift();
            }, 4000); // 4 seconds duration
        }

        private void displaySVGAAnimation(String giftLink, JSONObject jsonObject, GiftRoot.GiftItem giftData) {
            if (isFinishing() || isDestroyed()) return;
            binding.svgaImage.setVisibility(VISIBLE);
            SVGAImageView imageView = binding.svgaImage;
            SVGAParser parser = new SVGAParser(HostPKLiveActivity.this);
            try {
                parser.decodeFromURL(new URL(giftLink), new SVGAParser.ParseCompletion() {
                    @Override
                    public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                        SVGADynamicEntity dynamicEntity = new SVGADynamicEntity();
                        dynamicEntity.setDynamicImage(giftLink, "99");
                        SVGADrawable drawable = new SVGADrawable(svgaVideoEntity, dynamicEntity);
                        imageView.setImageDrawable(drawable);
                        imageView.startAnimation();

                        Glide.with(HostPKLiveActivity.this).load(RayziUtils.getImageFromNumber(giftData.getCount())).into(binding.imgSvgaGiftCount);
                        String name;
                        try {
                            name = jsonObject.getString("userName");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        binding.tvSvgaGiftUserName.setText(name + " Sent a gift");
                        binding.lytSvgagift.setVisibility(VISIBLE);

                        long animationDurationMillis = svgaVideoEntity.getFrames() / svgaVideoEntity.getFPS() * 1000L;

                        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(() -> {
                            binding.svgaImage.setVisibility(GONE);
                            binding.lytSvgagift.setVisibility(GONE);
                            binding.svgaImage.clear();

                            // Process the next gift
                            processNextGift();
                        }, animationDurationMillis);
                    }

                    @Override
                    public void onError() {
                        // Handle error and process the next gift
                        processNextGift();
                    }
                }, null);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onComment(Object[] args) {
            if (args[0] != null) {
                runOnUiThread(() -> {
                    Log.d(TAG, "commentlister : " + args[0].toString());
                    String data = args[0].toString();
                    if (!data.isEmpty()) {
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            if (!isFinishing()) {
                                if (jsonObject.has("comment")) {
                                    PKLiveStramComment liveStramComment = new Gson().fromJson(jsonObject.get("comment").toString(), PKLiveStramComment.class);
                                    boolean isPkOn = jsonObject.getBoolean("isPkRunning");
                                    if (liveStramComment != null) {
                                        if (hostLiveStreamingId.equalsIgnoreCase(liveStramComment.getLiveStreamingId())) {
                                            viewModel.pkLiveStramCommentAdapter.addSingleComment(liveStramComment, liveUser.getLiveStreamingId(), isPkOn);
                                            binding.rvComments.scrollToPosition(0);
                                        }
                                        Log.d(TAG, "commentListner: liveUser.getLiveStreamingId() = " + hostLiveStreamingId);
                                        Log.d(TAG, "commentListner: liveStramComment.getLiveStreamingId() = " + liveStramComment.getLiveStreamingId());
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        }

        @Override
        public void onView(Object[] data) {
            runOnUiThread(() -> {
                if (data[0] != null) {
                    Object args = data[0];

                    Log.d(TAG, "viewListner : " + args);

                    try {
                        JSONArray jsonArray = new JSONArray(args.toString());
                        finalArray = new JSONArray();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getBoolean("isAdd")) {
                                finalArray.put(jsonObject);
                            }
                        }
                        viewModel.liveViewUserAdapter.addData(finalArray);
                        binding.tvViewUserCount.setText(String.valueOf(finalArray.length()));
                        //Log.d(TAG, "views2 : " + jsonArray);
                        binding.tvNoOneJoined.setVisibility(viewModel.liveViewUserAdapter.getItemCount() > 0 ? GONE : VISIBLE);

                    } catch (JSONException e) {
                        // Log.d(TAG, "207: ");
                        e.printStackTrace();
                    }

//                    Toast.makeText(HostPKLiveActivity.this, "" + blockedUsersList.length(), Toast.LENGTH_SHORT).show();

//                    try {
//                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put(getString(R.string.blocked), blockedUsersList);
//                        MySocketManager.getInstance().getSocket().emit(Const.EVENT_BLOCK, jsonObject);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }
                if (data[1] != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(data[1].toString());
                        if (jsonObject.has("entrySvga") && jsonObject.has("avatarFrame") && jsonObject.has("image")) {
                            Log.d(TAG, "onView: jsonObject.toString() ====== " + jsonObject);
                            String avatarFrame = jsonObject.getString("avatarFrame");
                            String entrySvga = jsonObject.getString("entrySvga");
                            String userImage = jsonObject.getString("image");
                            String userName = jsonObject.getString("userName");
                            if (!isFinishing()) {
//                                showEntraceEffect(HostPKLiveActivity.this, avatarFrame, entrySvga, userImage, userName);
                                binding.svgImage.clear();
                                binding.layEntry.setVisibility(VISIBLE);

                                SVGAImageView imageView = binding.svgImage;
                                SVGAParser parser = new SVGAParser(HostPKLiveActivity.this);
                                try {
                                    parser.decodeFromURL(new URL(entrySvga != null && !entrySvga.isEmpty() ? BuildConfig.BASE_URL + entrySvga : ""), new SVGAParser.ParseCompletion() {
                                        @Override
                                        public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                                            SVGADynamicEntity dynamicEntity = new SVGADynamicEntity();
                                            dynamicEntity.setDynamicImage(BuildConfig.BASE_URL + entrySvga, "99");
                                            SVGADrawable drawable = new SVGADrawable(svgaVideoEntity, dynamicEntity);
                                            imageView.setImageDrawable(drawable);
                                            imageView.startAnimation();

                                            animationDurationMillis = svgaVideoEntity.getFrames() / svgaVideoEntity.getFPS() * 1000L;

                                            new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(() -> {
                                                binding.svgaImage.setVisibility(GONE);
                                                binding.layEntry.setVisibility(GONE);
                                                binding.svgaImage.clear();
                                            }, animationDurationMillis);
//
//                                                new Handler().postDelayed(() -> {
//                                                    binding.layEntry.setVisibility(View.GONE);
//                                                }, 3000);
                                        }

                                        @Override
                                        public void onError() {

                                        }
                                    }, null);
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }

                                /*        Glide.with(context).load().into(binding.svgImage);*/
                                binding.userName.setText(userName);
                                Glide.with(HostPKLiveActivity.this).load(userImage).circleCrop().into(binding.userImage);
                                Glide.with(HostPKLiveActivity.this).load(avatarFrame != null && !avatarFrame.isEmpty() ? BuildConfig.BASE_URL + avatarFrame : "").into(binding.avatarFrameImage);

                                Animation animation = AnimationUtils.loadAnimation(HostPKLiveActivity.this, R.anim.slide_in_right);
                                animation.setFillAfter(true);
                                binding.nameLyt.startAnimation(animation);

                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }


            });
        }

        @Override
        public void onRemoveCrone(Object[] args) {

        }

        @Override
        public void onBlock(Object[] args) {
            runOnUiThread(() -> {
                if (args[0] != null) {
                    Object data = args[0];
                    try {
                        JSONObject jsonObject = new JSONObject(data.toString());
                        JSONArray blockedList = jsonObject.getJSONArray("blocked");
                        for (int i = 0; i < blockedList.length(); i++) {
                            Log.d(TAG, "block user : " + blockedList.get(i).toString());
                            if (blockedList.get(i).toString().equals(sessionManager.getUser().getId())) {
                                new Handler(Looper.myLooper()).postDelayed(() -> {
                                    Toast.makeText(HostPKLiveActivity.this, getString(R.string.you_are_blocked_by_host), Toast.LENGTH_SHORT).show();
                                    endLive();
                                }, 500);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onBlockuseralert(Object[] args) {
            Log.d(TAG, "onBlock:onBlockuseralert " + args[0].toString());
            runOnUiThread(() -> {
                Toast.makeText(HostPKLiveActivity.this, getString(R.string.you_are_blocked_by_host), Toast.LENGTH_SHORT).show();
                endLive();
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
                        for (int i = 0; i < blockedList.length(); i++) {
                            Log.d(TAG, "block user : " + blockedList.get(i).toString());
                            if (blockedList.get(i).toString().equals(sessionManager.getUser().getId())) {
                                Toast.makeText(HostPKLiveActivity.this, R.string.you_are_blocked_by_host, Toast.LENGTH_SHORT).show();
                                new Handler(Looper.myLooper()).postDelayed(() -> endLive(), 500);
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
            if (args[0] != null){
                try {
                    blockUserList = new JSONArray(args[0].toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Log.d(TAG, "createGlobal: event blockeduserlist args0 " + args[0].toString());
            }
        }

        @Override
        public void onAnimationFilter(Object[] args) {
            if (args[0] != null) {
                runOnUiThread(() -> {
                    String filtertype = null;
                    try {
                        JSONObject jsonObject = new JSONObject(args[0].toString());
                        filtertype = jsonObject.getString("filter");
                        if (!isFinishing()) {
                            if (filtertype.equalsIgnoreCase("None")) {
                                binding.imgFilter2.setImageDrawable(null);
                            } else {
                                Glide.with(binding.imgFilter2).load(FilterUtils.getDraw(filtertype)).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(binding.imgFilter2);
                            }
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                });
            }
        }

        @Override
        public void onSimpleFilter(Object[] args) {
            if (args[0] != null) {
                runOnUiThread(() -> {
                    String filtertype = null;
                    try {
                        JSONObject jsonObject = new JSONObject(args[0].toString());
                        filtertype = jsonObject.getString("filter");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    if (filtertype != null) {
                        if (filtertype.equalsIgnoreCase("None")) {
                            Log.d(TAG, "initLister: null");
                            binding.imgFilter.setImageDrawable(null);
                        } else {
                            Log.d(TAG, "initLister: ffff");
                            if (!isFinishing()) {
                                Glide.with(HostPKLiveActivity.this).load(FilterUtils.getDraw(filtertype)).into(binding.imgFilter);
                            }
                        }
                    }

                });

            }
        }

        @Override
        public void onGif(Object[] args) {
            if (args[0] != null) {
                runOnUiThread(() -> {
                    Log.d(TAG, "commentlister : " + args[0].toString());
                    String data = args[0].toString();
                    if (!data.isEmpty()) {
                        StickerRoot.StickerItem sticker_dummy = new Gson().fromJson(data, StickerRoot.StickerItem.class);
                        if (sticker_dummy != null) {
                            //    binding.imgSticker.setVisibility(View.VISIBLE);

                            //   binding.imgSticker.setImageURI(sticker_dummy.getSticker());

                            //   new Handler(Looper.myLooper()).postDelayed(() -> binding.imgSticker.setVisibility(View.GONE), 4000);

                        }
                    }

                });

            }
        }

        @Override
        public void onLiveEndByEnd(Object[] args) {

        }

        @Override
        public void onPkRequest(Object[] args) {
            runOnUiThread(() -> {
                if (args[0] != null) {
                    String data = args[0].toString();
                    Log.d(TAG, "pkRequestListner: " + data);
                    if (!data.isEmpty()) {
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            Log.d(TAG, "EVENT_PK_REQUEST_ANSWER : jsonObject user    =========================" + jsonObject.toString());
                            String host1_Id = jsonObject.getString(Const.HOST_1_ID);
                            String host1_Image = jsonObject.getString(Const.HOST_1_IMAGE);
                            String host1_AvatarFrame_Image = jsonObject.getString(Const.HOST_1_AVATARFRAME_IMAGE);
                            String host1_Name = jsonObject.getString(Const.HOST_1_NAME);
                            int host1_Agora_Id = jsonObject.getInt(Const.HOST_1_AGORA_ID);
                            String host1_live_Id = jsonObject.getString(Const.HOST_1_LIVEID);
                            String host1_Channel = jsonObject.getString(Const.HOST_1_CHANNEL);
                            String host1_UniqueId = jsonObject.getString(Const.HOST_1_UNIQUEID);
                            String host2_Id = jsonObject.getString(Const.HOST_2_ID);
                            String host2_live_Id = jsonObject.getString(Const.HOST_2_LIVEID);
                            String host2_UniqueId = jsonObject.getString(Const.HOST_2_UNIQUEID);

                            //   String HOST_STR1 = jsonObject.getString(Const.HOST_STR1);

                            if (host2_Id.equals(sessionManager.getUser().getId())) {
//                            Toast.makeText(HostPKLiveActivity.this, "PK Received from " + host2_Id, Toast.LENGTH_SHORT).show();
                          /*  if (currentlyOpenBottomSheet != null && currentlyOpenBottomSheet.isShowing()) {
                                currentlyOpenBottomSheet.dismiss();
                                currentlyOpenBottomSheet = null;
                            }*/
                                if (!isBottomSheetOpen) {
                                    isBottomSheetOpen = true;
                                    if (!isFinishing()) {
                                        pkInvitationPopup = new PkInvitationBottomSheet(HostPKLiveActivity.this).pkBottomSheet(host1_Name, host1_Image, host1_AvatarFrame_Image, new PkInvitationBottomSheet.OnPkInvitationClickLister() {
                                            @Override
                                            public void onClickCountinue() {
                                                try {
                                                    JSONObject jsonObject2 = new JSONObject();
                                                    jsonObject2.put(Const.HOST_1_ID, host1_Id);
                                                    jsonObject2.put(Const.HOST_1_AGORA_ID, host1_Agora_Id);
                                                    jsonObject2.put(Const.HOST_1_IMAGE, host1_Image);
                                                    jsonObject2.put(Const.HOST_1_AVATARFRAME_IMAGE, host1_AvatarFrame_Image);
                                                    jsonObject2.put(Const.HOST_1_NAME, host1_Name);
                                                    jsonObject2.put(Const.HOST_1_CHANNEL, host1_Channel);
                                                    jsonObject2.put(Const.HOST_1_LIVEID, host1_live_Id);
                                                    jsonObject2.put(Const.HOST_1_UNIQUEID, host1_UniqueId);

                                                    jsonObject2.put(Const.HOST_2_LIVEID, host2_live_Id);
                                                    jsonObject2.put(Const.HOST_2_ID, host2_Id);
                                                    jsonObject2.put(Const.HOST_2_AGORA_ID, 8);
                                                    jsonObject2.put(Const.HOST_2_IMAGE, sessionManager.getUser().getImage());
                                                    jsonObject2.put(Const.HOST_2_AVATARFRAME_IMAGE, sessionManager.getUser().getAvatarFrameImage());
                                                    jsonObject2.put(Const.HOST_2_NAME, sessionManager.getUser().getName());
                                                    jsonObject2.put(Const.HOST_2_CHANNEL, liveUser.getChannel());
                                                    jsonObject2.put(Const.HOST_2_UNIQUEID, host2_UniqueId);
//                                                jsonObject2.put(Const.HOST_2_LIVEID, liveUser.getLiveStreamingId());

                                                    jsonObject2.put(Const.ISACCEPT, true);
                                                    Log.d(TAG, "onClickCountinue: pk and send" + jsonObject2.toString());
                                                    MySocketManager.getInstance().getSocket().emit(Const.EVENT_PK_REQUEST_ANSWER, jsonObject2);


                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                            @Override
                                            public void onClickCancel() {
                                                try {
                                                    JSONObject jsonObject2 = new JSONObject();
                                                    jsonObject2.put(Const.HOST_1_ID, host1_Id);
                                                    jsonObject2.put(Const.HOST_1_AGORA_ID, host1_Agora_Id);
                                                    jsonObject2.put(Const.HOST_1_IMAGE, host1_Image);
                                                    jsonObject2.put(Const.HOST_1_AVATARFRAME_IMAGE, host1_AvatarFrame_Image);
                                                    jsonObject2.put(Const.HOST_1_NAME, host1_Name);
                                                    jsonObject2.put(Const.HOST_1_CHANNEL, host1_Channel);
                                                    jsonObject2.put(Const.HOST_1_LIVEID, host1_live_Id);
                                                    jsonObject2.put(Const.HOST_2_LIVEID, host2_live_Id);

                                                    jsonObject2.put(Const.HOST_2_ID, host2_Id);
                                                    jsonObject2.put(Const.HOST_2_AGORA_ID, 8);
                                                    jsonObject2.put(Const.HOST_2_IMAGE, sessionManager.getUser().getImage());
                                                    jsonObject2.put(Const.HOST_2_AVATARFRAME_IMAGE, sessionManager.getUser().getAvatarFrameImage());
                                                    jsonObject2.put(Const.HOST_2_NAME, sessionManager.getUser().getName());
                                                    jsonObject2.put(Const.HOST_2_CHANNEL, liveUser.getChannel());
                                                    jsonObject2.put(Const.HOST_2_LIVEID, liveUser.getLiveStreamingId());

                                                    jsonObject2.put(Const.ISACCEPT, false);

                                                    MySocketManager.getInstance().getSocket().emit(Const.EVENT_PK_REQUEST_ANSWER, jsonObject2);

                                                    Log.d(TAG, "onClickCancel: " + jsonObject);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        });
                                    }
                                    new Handler().postDelayed(() -> {
                                        if (pkInvitationPopup != null) {
                                            if (!pkInvitationPopup.isShowing() && !isFinishing()) {
                                                pkInvitationPopup.show();
                                            }
                                        }
                                    }, 800);
                                    if (pkInvitationPopup != null) {
                                        pkInvitationPopup.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialogInterface) {
                                                isBottomSheetOpen = false;
                                            }
                                        });
                                    }
                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } else if (args[1] != null) {   // pk request cut by hostOwner
                    String data1 = args[1].toString();
                    if (!data1.isEmpty()) {
                        try {
                            JSONObject jsonObject = new JSONObject(data1);
                            String host1Id = jsonObject.getString(Const.HOST_1_ID);
                            String host2Id = jsonObject.getString(Const.HOST_2_ID);
                            boolean isHostOwnerRequestCut = jsonObject.getBoolean(Const.DIRECTREQUESTCUTBYHOST);

                            if (isHost) {
                                if (isHostOwnerRequestCut) {
                                    if (host2Id.equals(sessionManager.getUser().getId())) {
                                        if (pkInvitationPopup != null) {
                                            pkInvitationPopup.dismiss();
                                        }
                                    }
                                }
                            }
                            Log.d(TAG, "pkRequestListner: isHostOwnerRequestCut boolean =============" + isHostOwnerRequestCut);
                            Log.d(TAG, "pkRequestListner: sessionManager.getUser().getId() =============" + sessionManager.getUser().getId());
                            Log.d(TAG, "pkRequestListner: host1Id =============" + host1Id);
                            Log.d(TAG, "pkRequestListner: host2Id =============" + host2Id);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
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
        public void onPkRequestAnswer(Object[] args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = args[0].toString();
                    Log.d(TAG, "run: ==========listenpk" );
                    if (!data.isEmpty()) {
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            if (jsonObject.has("isAccept")) {
                                boolean isAccept = jsonObject.getBoolean("isAccept");
                                if (pkWaitingResponsePopup != null) {
                                    pkWaitingResponsePopup.dismiss();
                                }
                            }
                            Log.d(TAG, "pkRequestAnswerListner : ma listn thay che" + jsonObject.toString());

                            int type = jsonObject.getInt("type");
                            pkAnswerLiveUser = new Gson().fromJson(jsonObject.get("data").toString(), PkAudioLiveUserRoot.UsersItem.class);
                            try {
                                boolean ispkcon = jsonObject.getBoolean("pkContinue");
                                Log.d(TAG, "pkRequestAnswerListner ===================: ispkcon" + ispkcon);
                                if (pkAnswerLiveUser.isIsPkMode()) {
                                    binding.pkHostLayout.setPoints(pkAnswerLiveUser.getPkConfig().getLocalRank(), pkAnswerLiveUser.getPkConfig().getRemoteRank());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (!isFinishing()) {
                                binding.pkHostLayout.setUpHostsDetails(isHost, pkAnswerLiveUser.getPkConfig());
                            }

                            binding.singleLiveLay.setBackground(ContextCompat.getDrawable(HostPKLiveActivity.this, R.drawable.login_bg));

                            Log.d(TAG, ": whole object " + jsonObject);

                            //todo aya timer start nthi karvanu

                            isPkStart = pkAnswerLiveUser.isIsPkMode();
                            isPkView = pkAnswerLiveUser.isIsPkMode();

                            Log.d(TAG, ": pkrquestans  sessionManager.getBooleanValue(Const.PKOWNERHOST) ==========" + sessionManager.getBooleanValue(Const.PKOWNERHOST));
                            if (sessionManager.getBooleanValue(Const.PKOWNERHOST)) {
                                if (pkWaitingResponsePopup != null) {
                                    pkWaitingResponsePopup.dismiss();
                                    sessionManager.clearBooleanValue(Const.PKOWNERHOST);
                                }
                            }

                            if (pkAnswerLiveUser.isIsPkMode()) {
                                Log.d(TAG, ":pk answers  " + pkAnswerLiveUser.isIsPkMode());
                                Log.d(TAG, ":pk answers  " + pkAnswerLiveUser.getDuration());
                                binding.pkHostLayout.setPKbuttonGone();
                                setUpTimer(618);
                            }

                            // setUpTimer(liveUser);

                            if (type == 0) {  // host

                                Log.d(TAG, "onPkRequestAnswer == :type 0  host");
                                if (pkAnswerLiveUser != null) {
                                    PkAudioLiveUserRoot.UsersItem.PkConfig pkCongig = pkAnswerLiveUser.getPkConfig();
                                    if (pkCongig.getHost1Id().equals(sessionManager.getUser().getId())) {
                                        Toast.makeText(HostPKLiveActivity.this, "PK Accepted from " + pkCongig.getHost1Name(), Toast.LENGTH_SHORT).show();
                                    }
                                    setUpView(true, pkAnswerLiveUser);
                                    isPkStart = true;
                                    setUpPkBehaviour(pkCongig);
                                    startMediaRelay2(pkCongig);
                                }

                            } else if (type == 1) {  // user

                                Log.d(TAG, "onPkRequestAnswer ===== :type 1 user ");

                                if (pkAnswerLiveUser != null) {
                                    localLiveUser = pkAnswerLiveUser;
                                    setUpView(true, pkAnswerLiveUser);
                                    isPkStart = true;
                                    Log.d(TAG, "hostDetailsForAudience : user----- " + pkAnswerLiveUser.getPkConfig().toString());
                                    // binding.pkHostLayout.setUserLayVisible();

                                    //  Toast.makeText(HostPKLiveActivity.this, "Its a user ", Toast.LENGTH_SHORT).show();

                                    String host1image = pkAnswerLiveUser.getPkConfig().getHost1Details().getImage();
                                    String host2image = pkAnswerLiveUser.getPkConfig().getHost2Details().getImage();


                                    Log.d(TAG, "user ma host image: " + pkAnswerLiveUser.getPkConfig().getHost1Details().getImage());
                                    Log.d(TAG, "user ma host image: " + pkAnswerLiveUser.getPkConfig().getHost2Details().getImage());


                                    binding.pkHostLayout.setLeftUserImage(host1image);
                                    binding.pkHostLayout.setRightUserImage(host2image);

                                    SurfaceView surface = prepareRtcVideo(pkAnswerLiveUser.getPkConfig().getHost1AgoraUID(), false);

                                    binding.pkHostLayout.getLeftVideoLayout().removeAllViews();
                                    binding.pkHostLayout.getLeftVideoLayout().addView(surface);


                                    SurfaceView remoteSurfaceView = prepareRtcVideo(pkAnswerLiveUser.getPkConfig().getHost2AgoraUID(), false);
                                    binding.pkHostLayout.getRightVideoLayout().removeAllViews();
                                    binding.pkHostLayout.getRightVideoLayout().addView(remoteSurfaceView);

                                    remoteSurfaceView.setZOrderMediaOverlay(true);

                                }
                            } else {
                                binding.pkHostLayout.setPoints(pkAnswerLiveUser.getPkConfig().getLocalRank(), pkAnswerLiveUser.getPkConfig().getRemoteRank());
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }
            });

        }

        @Override
        public void onPkEnd(Object[] args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d(TAG, "pkEndListner: " + args[0].toString());
                        Log.d(TAG, ": pkendlive" + args[0].toString());

                        if (args[0] != null) {
                            JSONObject jsonObject = new JSONObject(args[0].toString());
                            PkAudioLiveUserRoot.UsersItem liveUser1 = new Gson().fromJson(jsonObject.get("data").toString(), PkAudioLiveUserRoot.UsersItem.class);
                            Log.d(TAG, "run: liveUser1.getRCoin() == " + liveUser1.getRCoin());
                            Log.d(TAG, "onPkEnd: pkendlive 1" + args[0].toString());
                            if (liveUser1.isDisconnect()) {

                                Log.d(TAG, " pkEndListner run : normal sivay pk end thayu che =============== " + liveUser1 + "live user name 1" + liveUser1.getPkConfig().getHost1Name());
                                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isFinishing()) {
                                            endLive();
                                        }
                                        if (isHost) {
                                            setUpView(false, liveUser1);
                                            isPkStart = false;
                                            isPkView = false;
                                            removeRtcVideo(LOCAL_HOST_AGORA_ID, true);
                                            mVideoGridContainer.removeUserVideo(LOCAL_HOST_AGORA_ID, true);
                                        } else {

                                            addLessView(false);

                                            //todo ispkstart change to ispkstart

                                            if (isPkStart) {

                                            } else {
                                                try {
                                                    removeRtcVideo(LOCAL_HOST_AGORA_ID, true);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                mVideoGridContainer.removeUserVideo(LOCAL_HOST_AGORA_ID, true);
                                            }

                                            //todo single live user live rakhvano

                                            // MySocketManager.getInstance().getSocet().emit("")
                                        }
                                    }
                                }, 1000);
                            } else {   // normally pk live end
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (!isFinishing()) {
                                            Log.d(TAG, " pkEndListner run : normal pk end thayu che =============== " + liveUser1 + "live user name 1" + liveUser1.getPkConfig().getHost1Name());

                                            binding.pkHostLayout.setWinner(liveUser1.getPkConfig().getIsWinner());
                                            isPkStart = false;
                                            isPkView = false;
                                            Log.d(TAG, ": winner in pk end " + liveUser1.getPkConfig().getIsWinner());

                                            if (liveUser1.getPkConfig().getIsWinner() == 1) {
                                                Log.d(TAG, ": winner 1 ");

                                            } else if (liveUser1.getPkConfig().getIsWinner() == 2) {
                                                Log.d(TAG, ": winner 2 ");

                                            } else {

                                                Log.d(TAG, ": winner 3");
                                            }

                                            JSONObject jsonObject1 = new JSONObject();
                                            try {
                                                jsonObject1.put(Const.HOST_1_ID, sessionManager.getUser().getId());
                                                jsonObject1.put(Const.HOST_2_ID, anOtherId);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            MySocketManager.getInstance().getSocket().emit(Const.EVENT_PK_CONTINUE_PK, jsonObject1);

                                            // todo farithi normal live karvanu pending che
                                            setUpView(false, liveUser1);

                                            Log.d(TAG, "run: liveUser1.getPkConfig()       liveUser1.getPkConfig().     === =" + liveUser1.getPkConfig().toString());
                                            Log.d(TAG, "run: liveUser1.getPkConfig()       liveUser1.getAvatarFrameImage().     === =" + liveUser1.getPkConfig().getHost2Details().getAvatarFrameImage());

                                            pkResultPopup.openPKResultSheet(liveUser1, isHost, jsonObject, () -> {
                                                binding.btnHostList.performClick();
                                            });
                                            if (!isFinishing()) {
                                                new Handler().postDelayed(() -> {
                                                    pkResultPopup.showDialog();
                                                }, 800);
                                            }
                                            setUpNormalLiveBehaviour(liveUser1.getPkConfig());
                                            binding.pkHostLayout.normalizePoints();
                                            sessionManager.clearBooleanValue(Const.PKOWNERHOST);
                                        }
                                    }
                                }, 1000);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        @Override
        public void onPkStart(Object[] args) {

        }

        @Override
        public void onPkContinuePk(Object[] args) {

        }

        @Override
        public void onHostDetailsForAudience(Object[] args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = args[0].toString();
                    if (!data.isEmpty()) {
                        hostDetailsForAudienceModel = new Gson().fromJson(data, PkAudioLiveUserRoot.UsersItem.class);
                        Log.d(TAG, "hostDetailsForAudience : hostDetailsForAudienceModel.getDuration()  " + hostDetailsForAudienceModel.getDuration());
                        Log.d(TAG, "hostDetailsForAudience : hostDetailsForAudienceModel.  getLocalRank()  " + hostDetailsForAudienceModel.getPkConfig().getLocalRank());
                        Log.d(TAG, "hostDetailsForAudience : hostDetailsForAudienceModel   getRemoteRank() " + hostDetailsForAudienceModel.getPkConfig().getRemoteRank());
                        if (hostDetailsForAudienceModel.isIsPkMode()) {
//                        setUpView(true);
                            if (!isHost) {
                                setUpTimer(559);
                                binding.pkHostLayout.setPoints(hostDetailsForAudienceModel.getPkConfig().getLocalRank(), hostDetailsForAudienceModel.getPkConfig().getRemoteRank());
//                                customDialogClass.dismiss();
                            }

                        }
                    }
                }
            });

        }

        @Override
        public void onHostLiveEnd(Object[] args) {
            runOnUiThread(() -> {
                String data = args[0].toString();
                Log.d(TAG, "hostLiveEnd : Strin data ==========" + data);
                if (!isHost) {
                    if (!isFinishing()) {
                        endLive();
                    }
                }
            });
        }

        @Override
        public void onSingleLiveUser(Object[] args) {
            runOnUiThread(() -> {
                try {
                    if (args[0] != null) {
                        // Log.d(TAG, "run: SIngle Live user getted  :" + args[0].toString());
                        Log.d(TAG, "singleLiveUserEventFire: " + args[0]);

                        PkAudioLiveUserRoot.UsersItem l = new Gson().fromJson(args[0].toString(), PkAudioLiveUserRoot.UsersItem.class);

                        isPkStart = l.isIsPkMode();
                        isPkView = l.isIsPkMode();

                        Log.d(TAG, "singleLiveUserEventFire: isPkStart " + isPkStart);
                        Log.d(TAG, "singleLiveUserEventFire: isPkView " + isPkView);

                        //todo ispkstart change to ispkview
                        if (!localLiveUser.isIsPkMode()) {
                            if (l.isIsPkMode()) {
                                Log.d(TAG, "singleLiveUserEventFire: if ma aave che --------------");
                            }
                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }

            });
        }

        @Override
        public void onGetUser(Object[] args1) {
            runOnUiThread(() -> {
                if (args1[0] != null) {
                    Log.d(TAG, "onGetUser: args1[0] ==  " + args1[0].toString());
                    String data = args1[0].toString();
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = parser.parse(data);
                    Gson gson = new Gson();
                    GuestProfileRoot.User userData = gson.fromJson(mJson, GuestProfileRoot.User.class);
                    if (userData != null) {
                        customDialogClass.dismiss();
                        if (!isFinishing()) {
                            userProfileBottomSheet.show(true, userData, localLiveUser.getLiveStreamingId(), !isHost);
                        }
                    }
                }
            });
        }
    };
    private String anOtherId;
    private BottomSheetBeautyOptions bottomSheetBeautyOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate:  activity_host_pklive ==============");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_pklive);

        MySocketManager.getInstance().addLiveHandler(liveHandler);
        MySocketManager.getInstance().addSocketConnectHandler(socketConnectHandler);
        binding.imggift2.setEnabled(false);

        giftViewModel = ViewModelProviders.of(this, new ViewModelFactory(new EmojiSheetViewModel()).createFor()).get(EmojiSheetViewModel.class);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new PKLiveViewModel()).createFor()).get(PKLiveViewModel.class);
        sessionManager = new SessionManager(this);
        hostAPICall = new HostAPICall(this,"video");
        sessionManager.clearBooleanValue(Const.PKOWNERHOST);
        binding.setViewModel(viewModel);
        giftViewModel.initEmojiSheet(this);
        giftViewModel.getGiftCategory();
        bottomSheetBeautyOptions = new BottomSheetBeautyOptions(this, rtcEngine());

        socketHandler.postDelayed(socketRunnable, 3000);
        Intent intent = getIntent();
        isHost = intent.getBooleanExtra(Const.ISHOST, false);
        customDialogClass = new CustomDialogClass(this, R.style.customStyle);
        customDialogClass.setCanceledOnTouchOutside(false);
        if (intent != null) {
            if (isHost) {
                String data = intent.getStringExtra(Const.DATA);
                String privacy = intent.getStringExtra(Const.PRIVACY);

                if (data != null && !data.isEmpty()) {
                    liveUser = new Gson().fromJson(data, PkAudioLiveUserRoot.UsersItem.class);
                    localLiveUser = new Gson().fromJson(data, PkAudioLiveUserRoot.UsersItem.class);
                    Log.d(TAG, "onCreate: liveUser======" + liveUser.getLiveStreamingId());
                    Log.d(TAG, "onCreate:localLiveUser ======" + localLiveUser.getLiveStreamingId());

                    liveRoomConnect();
                    LOCAL_HOST_AGORA_ID = liveUser.getAgoraUID();

                    if (!isFinishing()) {
                        binding.imgProfile.setUserImage(localLiveUser.getImage(), localLiveUser.getAvatarFrameImage(), 10);
                    }
                    binding.tvName.setText(localLiveUser.getName());
                    RayziUtils.marqueeText(binding.tvName);
                    binding.tvUniqueId.setText(localLiveUser.getUniqueId());

                    binding.hostProfileBig.setVisibility(GONE);
                    binding.mining.setVisibility(GONE);
                    token = liveUser.getToken();
                    channel = liveUser.getChannel();
                    BaseActivity.STATUS_LIVE = true;
                    timerHandler.postDelayed(timerRunnable, 1000);
                    // Log.d(TAG, "onCreate: live room id " + liveUser.getLiveStreamingId());
                    hostLiveStreamingId = liveUser.getLiveStreamingId();
//                    initSoketIo(liveUser.getLiveStreamingId(), true);
                    handler.postDelayed(runnable, 30000);
                    binding.imgblock.setVisibility(VISIBLE);
                    binding.tvPrivacy.setText(privacy);
                    binding.imggift2.setVisibility(GONE);
                    if (privacy.equalsIgnoreCase("Private")) {
                        binding.imgPrivacyk.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.lock));
                    }
                    isPkStart = liveUser.isIsPkMode();
                    isPkView = liveUser.isIsPkMode();
                    viewModel.pkLiveStramCommentAdapter.addSingleComment(null, liveUser.getLiveUserId(), false);
                    PKLiveStramComment liveStramComment1 = new PKLiveStramComment(getString(R.string.announcement_welcome_to_room), sessionManager.getUser(), true, liveUser.getLiveStreamingId(), "", "comment", "");
                    viewModel.pkLiveStramCommentAdapter.addSingleComment(liveStramComment1, liveUser.getLiveUserId(), true);
                }
            } else {
//                customDialogClass.show();
                String userStr = intent.getStringExtra(Const.DATA);
                if (userStr != null && !userStr.isEmpty()) {
                    liveUser = new Gson().fromJson(userStr, PkAudioLiveUserRoot.UsersItem.class);
                    localLiveUser = new Gson().fromJson(userStr, PkAudioLiveUserRoot.UsersItem.class);

                    Log.d(TAG, "onCreate: liveUser======" + liveUser.getLiveStreamingId());
                    Log.d(TAG, "onCreate:localLiveUser ======" + localLiveUser.getLiveStreamingId());

                    LOCAL_HOST_AGORA_ID = localLiveUser.getAgoraUID();
                    channel = localLiveUser.getChannel();

                    isPkStart = localLiveUser.isIsPkMode();
                    isPkView = localLiveUser.isIsPkMode();
                    Log.d(TAG, "onCreate: getUniqueId == " + localLiveUser.getUniqueId());
                    try {
                        token = RtcTokenBuilderSample.main_Attendee(channel, sessionManager.getSetting().getAgoraKey(), sessionManager.getSetting().getAgoraCertificate(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    binding.tvtimer.setVisibility(GONE);
                    addLessView(true);
                    if (!isFinishing()) {
                        binding.imgProfile.setUserImage(localLiveUser.getImage(), localLiveUser.getAvatarFrameImage(), 10);
                    }
                    binding.tvName.setText(localLiveUser.getName());
                    RayziUtils.marqueeText(binding.tvName);
                    binding.tvUniqueId.setText(localLiveUser.getUniqueId());
                    binding.imgblock.setVisibility(GONE);
                    hostLiveStreamingId = localLiveUser.getLiveStreamingId();
                    binding.tvRcoins.setText(String.valueOf(localLiveUser.getRCoin()));
                    binding.imggift2.setVisibility(VISIBLE);
//                       binding.tvUserId.setText(host.getUsername());
                    if (localLiveUser.isIsPkMode()) {
                        setUpPkAudienceBehaviour(localLiveUser.getPkConfig());
                        startMediaRelay2(localLiveUser.getPkConfig());
                        binding.pkHostLayout.setUpHostsDetails(isHost, localLiveUser.getPkConfig());
                        isPKRunning = false;
                    }
//                    if (!isPkView) {
//                        customDialogClass.dismiss();
//                    }

                    viewModel.pkLiveStramCommentAdapter.addSingleComment(null, localLiveUser.getLiveUserId(), false);
                    PKLiveStramComment liveStramComment1 = new PKLiveStramComment(getString(R.string.announcement_welcome_to_room), sessionManager.getUser(), true, localLiveUser.getLiveStreamingId(), "", "comment", "");
                    viewModel.pkLiveStramCommentAdapter.addSingleComment(liveStramComment1, localLiveUser.getLiveUserId(), false);
                    PKLiveStramComment liveStramComment = new PKLiveStramComment("", sessionManager.getUser(), true, localLiveUser.getLiveStreamingId(), liveUser.getLiveUserId(), isPkStart);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("liveStreamingId", localLiveUser.getLiveStreamingId());
                        jsonObject.put("comment", new Gson().toJson(liveStramComment));
                        jsonObject.put("isPkRunning", isPkStart);
                        jsonObject.put("liveUserId", localLiveUser.getId());
                        MySocketManager.getInstance().getSocket().emit(Const.EVENT_COMMENT, jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

//            setUpView(isPkStart);
            setUpView(isPkView, localLiveUser);
            viewModel.initLister();
            initView();
            if (rtcEngine() != null) {
                rtcEngine().setAudioProfile(Constants.AUDIO_PROFILE_DEFAULT, Constants.AUDIO_SCENARIO_CHATROOM_ENTERTAINMENT);
            }
            configureAudioRouting(rtcEngine());
            joinChannel();

            pkResultPopup = new PkResultBottomSheet(HostPKLiveActivity.this);

            if (!isPkView) {
                if (isHost) startBroadcast();
                else {
                    renderRemoteUser(1);
                }
            }

            initLister();

            binding.rvComments.scrollToPosition(0);

        }


    }


    private void setUpPkBehaviour(PkAudioLiveUserRoot.UsersItem.PkConfig pkCongig) {
        Log.d(TAG, "setUpPkBehaviour: 1");
        if (rtcEngine() != null) {
            rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
            SurfaceView surface = prepareRtcVideo(pkCongig.getHost1AgoraUID(), true);
            binding.pkHostLayout.getLeftVideoLayout().removeAllViews();
            binding.pkHostLayout.getLeftVideoLayout().addView(surface);

            String host1image = pkCongig.getHost1Details().getImage();
            String host2image = pkCongig.getHost2Details().getImage();
            binding.pkHostLayout.setLeftUserImage(host2image);
            binding.pkHostLayout.setRightUserImage(host1image);
            SurfaceView remoteSurfaceView = prepareRtcVideo(pkCongig.getHost2AgoraUID(), false);
            binding.pkHostLayout.getRightVideoLayout().removeAllViews();
            binding.pkHostLayout.getRightVideoLayout().addView(remoteSurfaceView);
            rtcEngine().muteLocalAudioStream(viewModel.isMuted);
            remoteSurfaceView.setZOrderMediaOverlay(true);
        }
    }

    private void setUpPkAudienceBehaviour(PkAudioLiveUserRoot.UsersItem.PkConfig pkConfig) {
        if (rtcEngine() != null) {
            Log.d(TAG, "setUpPkAudienceBehaviour:  pkConfig.toString() 1006 ==== " + pkConfig.toString());
            rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
            String host1image = pkConfig.getHost1Details().getImage();
            String host2image = pkConfig.getHost2Details().getImage();
            binding.pkHostLayout.setLeftUserImage(host1image);
            binding.pkHostLayout.setRightUserImage(host2image);
            SurfaceView surface = prepareRtcVideo(pkConfig.getHost1AgoraUID(), false);
            binding.pkHostLayout.getLeftVideoLayout().removeAllViews();
            binding.pkHostLayout.getLeftVideoLayout().addView(surface);
            SurfaceView remoteSurfaceView = prepareRtcVideo(pkConfig.getHost2AgoraUID(), false);
            binding.pkHostLayout.getRightVideoLayout().removeAllViews();
            binding.pkHostLayout.getRightVideoLayout().addView(remoteSurfaceView);
            remoteSurfaceView.setZOrderMediaOverlay(true);
        }
    }

    private void setUpNormalLiveBehaviour(PkAudioLiveUserRoot.UsersItem.PkConfig pkCongig) {
        if (rtcEngine() != null) {
            rtcEngine().stopChannelMediaRelay();
            rtcEngine().stopAudioMixing();
            if (isHost) {
                rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
                SurfaceView surface = prepareRtcVideo(pkCongig.getHost1AgoraUID(), true);
                mVideoGridContainer.addView(surface);
            } else {
                SurfaceView surfaceView = RtcEngine.CreateRendererView(this);
                rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, LOCAL_HOST_AGORA_ID));
                mVideoGridContainer.removeAllViews();
                mVideoGridContainer.addView(surfaceView);
            }
            Log.d(TAG, "setUpPkAudienceBehaviour: setUpNormalLiveBehaviour == viewModel.isMuted " + viewModel.isMuted);
            rtcEngine().muteLocalAudioStream(viewModel.isMuted);
        }
    }

    private void startMediaRelay2(PkAudioLiveUserRoot.UsersItem.PkConfig pkCongig) {
        try {
            Log.d(TAG, "startMediaRelay2: pkCongig.toString() 1056 ======= " + pkCongig.toString());
            ChannelMediaRelayConfiguration relayConfig = new ChannelMediaRelayConfiguration();
            relayConfig.setSrcChannelInfo(new ChannelMediaInfo(pkCongig.getHost1Channel(), pkCongig.getHost1Token(), 0));
            relayConfig.setDestChannelInfo(pkCongig.getHost2Channel(), new ChannelMediaInfo(pkCongig.getHost2Channel(), pkCongig.getHost2Token(), pkCongig.getHost2AgoraUID()));
            rtcEngine().stopChannelMediaRelay();
//            rtcEngine().renewToken(pkCongig.getHost1Token());
            rtcEngine().startChannelMediaRelay(relayConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpTimer(int line) {
        Log.d(TAG, "setUpTimer: line ================ " + line);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        // Log.d(TAG, "setUpTimer: ");

        if (hostDetailsForAudienceModel != null) {
            Log.d(TAG, "setUpTimer: timeer ============= " + hostDetailsForAudienceModel.getDuration());
        }
//        (!isHost && hostDetailsForAudienceModel.isIsPkMode())?hostDetailsForAudienceModel.getDuration():liveUser.getDuration()
        Log.d(TAG, "setUpTimer: liveUser.getDuration() ========================== " + liveUser.getDuration());

        if (pkAnswerLiveUser != null) {
            timer = new CountDownTimer(pkAnswerLiveUser.getDuration() * 1000L, 1000) {
                public void onTick(long millisUntilFinished) {
                    long counter = millisUntilFinished / 1000;
                    binding.pkHostLayout.setTime(convertSecondsToHMmSs(counter));
                }

                public void onFinish() {
                    if (isHost) {
                        try {
                            Log.d(TAG, "onFinish: pk end emit thay che ===============");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put(Const.HOST_1_ID, pkAnswerLiveUser.getPkConfig().getHost1Id());
                            jsonObject.put(Const.HOST_2_ID, pkAnswerLiveUser.getPkConfig().getHost2Id());
                            jsonObject.put(Const.LIVECUTINBETWEEN, false);
                            MySocketManager.getInstance().getSocket().emit(Const.EVENT_PK_END, jsonObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            };
            timer.start();
        } else {
            timer = new CountDownTimer(hostDetailsForAudienceModel.getDuration() * 1000L, 1000) {
                public void onTick(long millisUntilFinished) {
                    long counter = millisUntilFinished / 1000;
                    binding.pkHostLayout.setTime(convertSecondsToHMmSs(counter));
                }

                public void onFinish() {
                    if (isHost) {
                        try {
                            Log.d(TAG, "onFinish: pk end emit thay che ===============");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put(Const.HOST_1_ID, pkAnswerLiveUser.getPkConfig().getHost1Id());
                            jsonObject.put(Const.HOST_2_ID, pkAnswerLiveUser.getPkConfig().getHost2Id());
                            jsonObject.put(Const.LIVECUTINBETWEEN, false);
                            MySocketManager.getInstance().getSocket().emit(Const.EVENT_PK_END, jsonObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            timer.start();
        }
    }

    private void liveRoomConnect() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
            jsonObject.put("liveUserId", sessionManager.getUser().getId());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Log.d(TAG, "liveRoomConnect: " + jsonObject);
        MySocketManager.getInstance().getSocket().emit("liveRoomConnect", jsonObject);
    }

    private void initLister() {
        binding.pkHostLayout.setOnClickSwitchPKRoom(v -> {
            switchPkRoom();
        });

        binding.imgblock.setOnClickListener(v -> {
            new BottomSheetBannedList(HostPKLiveActivity.this, blockUserList, new BottomSheetBannedList.OnclickListener() {
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
        });


        binding.imgshare.setOnClickListener(v -> {
            binding.imgshare.setEnabled(false);
            BranchUniversalObject buo = new BranchUniversalObject().setCanonicalIdentifier("content/12345").setTitle("Watch My Live Video").setContentDescription("By : " + sessionManager.getUser().getName()).setContentImageUrl(sessionManager.getUser().getImage()).setContentMetadata(new ContentMetadata().addCustomMetadata("type", "LIVE").addCustomMetadata(Const.DATA, new Gson().toJson(liveUser)));


            LinkProperties lp = new LinkProperties().setChannel("facebook").setFeature("sharing").setCampaign("content 123 launch").setStage("new user").addControlParameter("", "").addControlParameter("", Long.toString(Calendar.getInstance().getTimeInMillis()));

            buo.generateShortUrl(this, lp, (url, error) -> {
                // Log.d(TAG, "initListnear: branch url" + url);
                try {
                    //  Log.d(TAG, "initListnear: share");
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareMessage = url;
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.choose_one)));
                    binding.imgshare.setEnabled(true);
                } catch (Exception e) {
                    //  Log.d(TAG, "initListnear: " + e.getMessage());
                    //e.toString();
                }
            });
        });
        binding.btnHostList.setOnClickListener(view -> {
            new BottemSheetLiveHosts(HostPKLiveActivity.this).openLiveHostListSheet((userDummy, itemPkInviteHostBinding) -> {
                Log.d(TAG, "BottemSheetLiveHosts : isPkView ===  " + isPkView);
                if (!isPkView) {
                    if (userDummy.isIsPkMode()) {
                        Toast.makeText(HostPKLiveActivity.this, R.string.host_is_doing_pk_battle_with_someone_else, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "initLister:  getUniqueId == " + localLiveUser.getUniqueId());
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put(Const.HOST_1_ID, sessionManager.getUser().getId());
                            jsonObject.put(Const.HOST_1_IMAGE, sessionManager.getUser().getImage());
                            jsonObject.put(Const.HOST_1_AVATARFRAME_IMAGE, sessionManager.getUser().getAvatarFrameImage());
                            jsonObject.put(Const.HOST_1_NAME, sessionManager.getUser().getName());
                            jsonObject.put(Const.HOST_1_AGORA_ID, localLiveUser.getAgoraUID());
                            jsonObject.put(Const.HOST_1_CHANNEL, localLiveUser.getChannel());
                            jsonObject.put(Const.HOST_1_LIVEID, localLiveUser.getLiveStreamingId());
                            jsonObject.put(Const.HOST_1_UNIQUEID, sessionManager.getUser().getUniqueId());


                            jsonObject.put(Const.HOST_2_ID, userDummy.getLiveUserId());
                            jsonObject.put(Const.HOST_2_IMAGE, userDummy.getImage());
                            jsonObject.put(Const.HOST_2_AVATARFRAME_IMAGE, userDummy.getAvatarFrameImage());
                            jsonObject.put(Const.HOST_2_LIVEID, userDummy.getLiveStreamingId());
                            jsonObject.put(Const.HOST_2_UNIQUEID, userDummy.getUniqueId());
                            //jsonObject.put(Const.DURATON, minutes * 60);

                            anOtherId = userDummy.getLiveUserId();

                            host1id = sessionManager.getUser().getId();
                            Log.d(TAG, "onCreate: getUniqueId == " + localLiveUser.getUniqueId());
                            jsonObject.put(Const.HOST_STR1, new Gson().toJson(liveUser));
                            MySocketManager.getInstance().getSocket().emit(Const.EVENT_PK_REQUEST, jsonObject);
                            sessionManager.saveBooleanValue(Const.PKOWNERHOST, true);
                            Log.d(TAG, "pkRequestListner ne emit kre che ========= " + jsonObject);
                            if (!isBottomSheetOpen) {
                                isBottomSheetOpen = true;
                                Log.d(TAG, "initLister: liveUser.getAvatarFrameImage() ===  " + liveUser.getAvatarFrameImage());
                                pkWaitingResponsePopup = new PkWaitingForResponseBottomSheet(HostPKLiveActivity.this).pkBottomSheet(liveUser, userDummy, () -> {
                                    JSONObject jsonObject1 = new JSONObject();
                                    try {
                                        jsonObject1.put(Const.HOST_1_ID, sessionManager.getUser().getId());
                                        jsonObject1.put(Const.HOST_2_ID, userDummy.getLiveUserId());
                                        jsonObject1.put(Const.DIRECTREQUESTCUTBYHOST, true);
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                    Log.d(TAG, "pkrquestans: btn click pk request " + sessionManager.getBooleanValue(Const.PKOWNERHOST));
                                    MySocketManager.getInstance().getSocket().emit(Const.EVENT_PK_REQUEST, jsonObject1);
                                });
                                pkWaitingResponsePopup.setOnDismissListener(dialogInterface -> {
                                    isBottomSheetOpen = false;
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "initLister: EVENT_PK_REQUEST == " + e.getMessage());
                        }
                    }
                }
            });

        });

        binding.lytHost.setOnClickListener(v -> getUser(liveUser.getLiveUserId()));

        viewModel.isShowFilterSheet.observe(this, aBoolean -> {
            //Log.d(TAG, "initLister:filter sheet  " + aBoolean);
            if (aBoolean) {
                binding.lytFilters.setVisibility(VISIBLE);
            } else {
                binding.lytFilters.setVisibility(GONE);
            }
        });

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

        viewModel.selectedFilter.observe(this, selectedFilter -> {
            viewModel.isShowFilterSheet.setValue(false);
            if (selectedFilter.getTitle().equalsIgnoreCase("None")) {
                //  Log.d(TAG, "initLister: null");
                binding.imgFilter.setImageDrawable(null);
            } else {

            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("filter", selectedFilter.getTitle());
                jsonObject.put("liveStreamingId", hostLiveStreamingId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MySocketManager.getInstance().getSocket().emit(Const.EVENT_ANIMFILTER, jsonObject);

        });


        viewModel.selectedFilter2.observe(this, selectedFilter -> {
            if (selectedFilter.getTitle().equalsIgnoreCase("None")) {
                binding.imgFilter.setImageDrawable(null);
            } else {

            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("filter", selectedFilter.getTitle());
                jsonObject.put("liveStreamingId", hostLiveStreamingId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MySocketManager.getInstance().getSocket().emit(Const.EVENT_SIMPLEFILTER, jsonObject);

        });

        viewModel.selectedSticker.observe(this, new Observer<StickerRoot.StickerItem>() {
            @Override
            public void onChanged(StickerRoot.StickerItem selectedSticker) {

                MySocketManager.getInstance().getSocket().emit(Const.EVENT_GIF, new Gson().toJson(selectedSticker));

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

        binding.btnClose.setOnClickListener(v -> showEndLivePopup(isHost));

        giftViewModel.finelGift.observe(this, giftItem -> {
            if (giftItem != null) {
                int totalCoin = giftItem.getCoin() * giftItem.getCount();
                Log.d(TAG, "initListerm Host PK : totalCoin =============== " + totalCoin);
                Log.d(TAG, "initListerm Host PK : .getUser().getDiamond() =============== " + sessionManager.getUser().getDiamond());
                if (sessionManager.getUser().getDiamond() < totalCoin) {
                    Toast.makeText(HostPKLiveActivity.this, getString(R.string.you_not_have_enough_diamonds_to_send_gift), Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("coin", giftItem.getCoin() * giftItem.getCount());
                    jsonObject.put("gift", new Gson().toJson(giftItem));
                    jsonObject.put("giftCount", giftItem.getCount());
                    jsonObject.put("userName", sessionManager.getUser().getName());

                    if (isHost) {
                        jsonObject.put("userId", sessionManager.getUser().getId());
                        jsonObject.put("senderUserName", sessionManager.getUser().getName());
                        MySocketManager.getInstance().getSocket().emit(Const.EVENT_LIVEUSER_GIFT, jsonObject);
                    } else {
                        jsonObject.put("senderUserId", sessionManager.getUser().getId());
                        jsonObject.put("receiverUserId", liveUser.getLiveUserId());
                        jsonObject.put("hostId", liveUser.getLiveUserId());
                        jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
                        jsonObject.put("senderUserName", sessionManager.getUser().getName());
                        jsonObject.put("liveType","video");
                        MySocketManager.getInstance().getSocket().emit(Const.EVENT_NORMALUSER_GIFT, jsonObject);
                        Log.d(TAG, "initLister: ======gift emited");
                    }

                    emojiBottomsheetFragment.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        userProfileBottomSheet.setOnUserTapListner(new UserProfileBottomSheet.OnUserTapListner() {
            @Override
            public void onBlockClick(GuestProfileRoot.User userDummy) {

                blockedUsersList.put(userDummy.getUserId());
                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("blocked", blockedUsersList);
                    jsonObject1.put("type","block");
                    jsonObject1.put("liveStreamingId", liveUser.getLiveStreamingId());
                    jsonObject1.put("blockedUserId",userDummy.getUserId());
                    MySocketManager.getInstance().getSocket().emit(Const.EVENT_UPDATEBLOCKEDLIST, jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void switchPkRoom() {
        customDialogClass.show();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", localLiveUser.getPkConfig().getHost2Id());
            jsonObject.put("joinUserId", sessionManager.getUser().getId());
            jsonObject.put("liveStreamingId", localLiveUser.getPkConfig().getHost2LiveId());
            jsonObject.put("type", (liveUser.isAudio()) ? "audio" : "other");
            MySocketManager.getInstance().getSocket().emit("singleLiveUser", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MySocketManager.getInstance().getSocket().once(Const.DUMMY, args -> {
            runOnUiThread(() -> {
                if (args[0] != null) {
                    // Log.d(TAG, "run: SIngle Live user getted  :" + args[0].toString());
                    Log.d(TAG, "singleLiveUserEventFire: " + args[0].toString());
                    endLive();
                    overridePendingTransition(0, 0);
                    PkAudioLiveUserRoot.UsersItem socketLiveUser = new Gson().fromJson(args[0].toString(), PkAudioLiveUserRoot.UsersItem.class);
                    if (!isGone) {
                        isGone = true;
                        isPKRunning = true;
                        runOnUiThread(() -> {
                            new Handler().postDelayed(() -> {
                                startActivity(new Intent(HostPKLiveActivity.this, HostPKLiveActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra(Const.ISHOST, false).putExtra(Const.DATA, new Gson().toJson(socketLiveUser)));
                                finish();
//                                customDialogClass.dismiss();
                            }, 1000);
                        });
                    }
                }
            });
        });
    }


    public void onClickReport(View view) {
        if (!isHost) {
            new BottomSheetReport_g(this, liveUser.getLiveUserId(), () -> {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.customtoastlyt));

                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            });
        }
    }

    @Override
    public void onBackPressed() {
        showEndLivePopup(isHost);
//        super.onBackPressed();
    }

    private void showEndLivePopup(boolean isHost) {
        new PopupBuilder(this).showLiveEndPopup(new PopupBuilder.OnMultButtonPopupLister() {
            @Override
            public void onClickCountinue() {
                if (!isFinishing()) {
                    endLive();
                }
            }

            @Override
            public void onClickCancel() {

            }
        });
    }


    private void setUpView(boolean isPKMode, PkAudioLiveUserRoot.UsersItem liveUser) {

        binding.pkHostLayout.setHost(isHost);
        binding.pkHostLayout.playAnim();

        if (host1id != null && !isPkStart) {
            if (host1id.equalsIgnoreCase(sessionManager.getUser().getId())) {
                binding.pkHostLayout.setPKbuttonVisible();
            }
        }

        if (isPKMode) {
            binding.pkHostLayout.setVisibility(VISIBLE);
            binding.liveVideoGridLayout.setVisibility(GONE);
            binding.btnHostList.setVisibility(GONE);
            binding.rCoinLyt.setVisibility(GONE);
        } else {
            binding.btnHostList.setVisibility(VISIBLE);
            binding.singleLiveLay.setBackground(null);
            binding.pkHostLayout.setVisibility(GONE);
            binding.liveVideoGridLayout.setVisibility(VISIBLE);
            binding.rCoinLyt.setVisibility(VISIBLE);
            binding.tvRcoins.setText(String.valueOf(liveUser.getRCoin()));
        }

        if (isHost) {
            binding.lytHost.setVisibility(GONE);
            binding.lytFilterFunctions.setVisibility(VISIBLE);
//            binding.lytPrivacy.setVisibility(View.VISIBLE);
            binding.btnRepot.setVisibility(GONE);

        } else {
            binding.lytHost.setVisibility(VISIBLE);
            binding.lytFilterFunctions.setVisibility(GONE);
//            binding.lytPrivacy.setVisibility(View.GONE);
            binding.btnRepot.setVisibility(VISIBLE);
        }
    }

    private void addLessView(boolean isAdd) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("liveStreamingId", localLiveUser.getLiveStreamingId());
            jsonObject.put("liveUserMongoId", localLiveUser.getId());
            jsonObject.put("userId", sessionManager.getUser().getId());
            jsonObject.put("isVIP", sessionManager.getUser().isIsVIP());
            jsonObject.put("image", sessionManager.getUser().getImage());
            jsonObject.put("userName", sessionManager.getUser().getName());
            jsonObject.put("avatarFrame", sessionManager.getUser().getAvatarFrameImage());
            jsonObject.put("entrySvga", sessionManager.getUser().getSvgaImage());
            Log.d(TAG, "addLessView: " + jsonObject);
            if (isAdd) {
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_ADDVIEW, jsonObject);
            } else {
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_LESSVIEW, jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getUser(String userId) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fromUserId", sessionManager.getUser().getId());
            jsonObject.put("toUserId", userId);
            Log.d(TAG, "getUser:request  " + jsonObject);
            MySocketManager.getInstance().getSocket().emit(Const.EVENT_GET_USER, jsonObject);
            customDialogClass.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onClickFilter(View view) {
        viewModel.isShowFilterSheet.setValue(true);
        binding.rvFilters.setAdapter(viewModel.filterAdapter_tt);
    }

    public void onSwitchCameraClicked(View view) {
        if (rtcEngine() != null) {
            rtcEngine().switchCamera();
        }
    }

    public void onClickGifIcon(View view) {
//        viewModel.isShowFilterSheet.setValue(true);
//        binding.rvFilters.setAdapter(viewModel.filterAdapter2);
        bottomSheetBeautyOptions.show();
    }

    public void onClickStickerIcon(View view) {
        viewModel.isShowFilterSheet.setValue(true);
        binding.rvFilters.setAdapter(viewModel.stickerAdapter);
    }

    public void onClickEmojiIcon(View view) {
    }

    public void onLocalAudioMuteClicked(View view) {
        viewModel.isMuted = !viewModel.isMuted;
        rtcEngine().muteLocalAudioStream(viewModel.isMuted);
        if (viewModel.isMuted) {
            binding.btnMute.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mute));
        } else {
            binding.btnMute.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.unmute));
        }
    }

    private void endLive() {
        BaseActivity.STATUS_LIVE = false;
        pkLiveCutInBetweenFireEvent();
        MySocketManager.getInstance().removeSocketConnectHandler(socketConnectHandler);
        statsManager().clearAllData();
        finish();
        overridePendingTransition(0, 0);
    }

    private void liveEndFireEvent() {

        if (isHost) {
            rtcEngine().stopChannelMediaRelay();
            rtcEngine().stopAudioMixing();
            JSONObject jsonObject2 = new JSONObject();
            try {
                jsonObject2.put("liveStreamingId", localLiveUser.getLiveStreamingId());
                jsonObject2.put("liveUserId", localLiveUser.getLiveUserId());
                jsonObject2.put("time", seconds);
                jsonObject2.put(Const.HOST_1_LIVEID, liveUser.getPkConfig().getHost1LiveId());
                jsonObject2.put(Const.HOST_2_LIVEID, liveUser.getPkConfig().getHost2LiveId());
                jsonObject2.put("HOST_1_ID", liveUser.getPkConfig().getHost1Id());
                jsonObject2.put("HOST_2_ID", liveUser.getPkConfig().getHost2Id());

                MySocketManager.getInstance().getSocket().emit(Const.HOSTLIVEEND, jsonObject2);
                Log.d(TAG, "liveEndFireEvent: HOSTLIVEEND ");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


            removeRtcVideo(LOCAL_HOST_AGORA_ID, true);
            mVideoGridContainer.removeUserVideo(LOCAL_HOST_AGORA_ID, true);
            rtcEngine().leaveChannel();
            //  startActivity(new Intent(this, LiveSummaryActivity.class).putExtra(Const.DATA, liveUser.getLiveStreamingId()));
            if (!isFinishing()) {
                startActivity(new Intent(this, LiveSummaryActivity.class).putExtra(Const.DATA, localLiveUser.getLiveStreamingId()));
            }

        } else {

            addLessView(false);

            //todo ispkstart change to ispkview

            if (isPkStart) {

            } else {
                try {
                    removeRtcVideo(LOCAL_HOST_AGORA_ID, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mVideoGridContainer.removeUserVideo(LOCAL_HOST_AGORA_ID, false);
            }

            if (rtcEngine() != null) {
                rtcEngine().leaveChannel();
            }

        }
    }

    public void onClickSendComment(View view) {
        String comment = binding.etComment.getText().toString();
        if (!comment.isEmpty()) {
            binding.etComment.setText("");
            PKLiveStramComment liveStramComment = new PKLiveStramComment(comment, sessionManager.getUser(), false, localLiveUser.getLiveStreamingId(), liveUser.getLiveUserId(), isPkStart);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("liveStreamingId", localLiveUser.getLiveStreamingId());
                jsonObject.put("comment", new Gson().toJson(liveStramComment));
                jsonObject.put("isPkRunning", isPkStart);
                jsonObject.put("liveUserId", localLiveUser.getId());
              /*  if (!isHost) {
                    jsonObject.put("hostId", hostLiveStreamingId);
                }*/
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_COMMENT, jsonObject);
                Log.d("<<<<<live straem id>>>>>> comment   ", "onCreate: " + liveUser.getLiveStreamingId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
//
//            hideKeyboard(HostPKLiveActivity.this);

        }
    }

    private void joinChannel() {

        try {
            rtcEngine().setDefaultAudioRoutetoSpeakerphone(true);
            rtcEngine().setEnableSpeakerphone(true);
            rtcEngine().setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            rtcEngine().enableVideo();

            configVideo();
            Log.d(TAG, "joinChannel:tkn " + liveUser.getChannel());
            Log.d("<<<<<<agorauid>>>>>>", "joinChannel:chanel " + LOCAL_HOST_AGORA_ID);

            if (isHost) {
                rtcEngine().joinChannel(token, channel, "", LOCAL_HOST_AGORA_ID);
            } else {
                rtcEngine().joinChannel(token, channel, "", 0);
            }

        } catch (Exception e) {
            //  Log.d(TAG, "joinChannel: catch" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void startBroadcast() {
        //  Log.d(TAG, "startBroadcast: ");
        if (rtcEngine() != null) {
            rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
            rtcEngine().enableAudio();
            SurfaceView surface = prepareRtcVideo(LOCAL_HOST_AGORA_ID, true);
            mVideoGridContainer.addUserVideoSurface(LOCAL_HOST_AGORA_ID, surface, true);
        }

    }

    private void initView() {
        mVideoGridContainer = binding.liveVideoGridLayout;
        mVideoGridContainer.setStatsManager(statsManager());
        emojiBottomsheetFragment = new EmojiBottomsheetFragment();
        userProfileBottomSheet = new UserProfileBottomSheet(this);
        Glide.with(binding.imgFilter2).load(FilterUtils.getDraw(localLiveUser.getFilter())).into(binding.imgFilter2);
        if (rtcEngine() != null) {
            if (isHost) {
                rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
            } else {
                rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
            }
        }
        binding.imggift2.setOnClickListener(view -> {
            if (!emojiBottomsheetFragment.isAdded()) {
                emojiBottomsheetFragment.show(getSupportFragmentManager(), "emojifragfmetn");
            }
        });

        giftViewModel.giftCategoryGetted.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.imggift2.setEnabled(aBoolean);
            }
        });
        Log.d(TAG, "initView: getIntent().getBooleanExtra(Const.CAMERAFACING, false) == " + getIntent().getBooleanExtra(Const.CAMERAFACING, false));
        if (getIntent().getBooleanExtra(Const.CAMERAFACING, false)) rtcEngine().switchCamera();
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        Log.d(TAG, "onFirstRemoteVideoDecoded: " + uid);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.hostProfileBig.setVisibility(GONE);
                binding.mining.setVisibility(GONE);
                isVideoDecoded = true;
//                 renderRemoteUser(uid);
            }
        });
    }

    private void renderRemoteUser(int uid) {
        Log.d(TAG, "renderRemoteUser: " + uid);
        SurfaceView surface = prepareRtcVideo(uid, false);
        mVideoGridContainer.addUserVideoSurface(uid, surface, false);

        LiveStramComment liveStramComment = new LiveStramComment("", sessionManager.getUser(), true, localLiveUser.getLiveStreamingId(), "", "comment", "");
        MySocketManager.getInstance().getSocket().emit(Const.EVENT_COMMENT, new Gson().toJson(liveStramComment));

    }

    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
        Log.d(TAG, "onLeaveChannel: ");
    }

    @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(LOCAL_HOST_AGORA_ID);
        if (data == null) return;

        VideoEncoderConfiguration.VideoDimensions mVideoDimension = VideoEncoderConfiguration.VD_960x720;
        data.setWidth(mVideoDimension.width);
        data.setHeight(mVideoDimension.height);
        data.setFramerate(stats.sentFrameRate);
    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(LOCAL_HOST_AGORA_ID);
        if (data == null) return;

        data.setLastMileDelay(stats.lastmileDelay);
        data.setVideoSendBitrate(stats.txVideoKBitRate);
        data.setVideoRecvBitrate(stats.rxVideoKBitRate);
        data.setAudioSendBitrate(stats.txAudioKBitRate);
        data.setAudioRecvBitrate(stats.rxAudioKBitRate);
        data.setCpuApp(stats.cpuAppUsage);
        data.setCpuTotal(stats.cpuAppUsage);
        data.setSendLoss(stats.txPacketLossRate);
        data.setRecvLoss(stats.rxPacketLossRate);
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
        Log.d(TAG, "Media relay state changed: " + state + ", code: " + code);
    }

    @Override
    public void onChannelMediaRelayEvent(int code) {
        Log.d(TAG, "Media relay event: " + code);
    }

    @Override
    public void onFirstLocalAudioFramePublished(int elapsed) {

    }

    @Override
    public void onFirstRemoteAudioFrame(int uid, int elapsed) {

    }

    @Override
    public void onUserMuteAudio(int uid, boolean muted) {

    }

    @Override
    public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {

    }

    @Override
    public void onActiveSpeaker(int uid) {

    }

    @Override
    public void onAudioMixingStateChanged(int state, int reason) {

    }

    @Override
    public void onTokenPrivilegeWillExpire(String token) {

    }

    @Override
    public void onRequestToken() {

    }

    // In your implementation of IRtcEngineEventHandler
    @Override
    public void onAudioRouteChanged(int routing) {
        switch (routing) {
            case Constants.AUDIO_ROUTE_SPEAKERPHONE:
                Log.d("AudioRouting", "Audio routed to speakerphone");
                break;
            case Constants.AUDIO_ROUTE_HEADSET:
                Log.d("AudioRouting", "Audio routed to wired headset");
                break;
            case Constants.AUDIO_ROUTE_HEADSETBLUETOOTH:
                Log.d("AudioRouting", "Audio routed to Bluetooth headset");
                break;
            case Constants.AUDIO_ROUTE_EARPIECE:
                Log.d("AudioRouting", "Audio routed to earpiece");
                break;
            default:
                Log.d("AudioRouting", "Unknown audio route: " + routing);
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        statsManager().clearAllData();
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        Log.d(TAG, "onJoinChannelSuccess: " + uid);
        //binding.btnHostList.setEnabled(true);
        if (!isHost) {
            runOnUiThread(() -> new Handler().postDelayed(() -> {
                if (isVideoDecoded) {
                    Log.d(TAG, "onJoinChannelSuccess: isVideoDecoded true male che -========= ");
                } else {
                    Toast.makeText(HostPKLiveActivity.this, "Live has Ended.", Toast.LENGTH_SHORT).show();
                    endLive();
                }
                Log.d(TAG, "onJoinChannelSuccess: isVideoDecoded === " + isVideoDecoded);
            }, 4000));
        }


    }

    @Override
    public void onUserOffline(int uid, int reason) {
        Log.d(TAG, "onUserOffline: jay che ");

        if (!isHost) {
            // todo Watch krva aavva vala user ni particular Host ni ID match karavi ne leave karavano che Monday
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /*  removeRemoteUser(uid);*/

                }
            });
        }

    }

    private void removeRemoteUser(int uid) {
        if (!isHost) {
            Log.d(TAG, "removeRemoteUser: jay che ");
            removeRtcVideo(uid, false);
            mVideoGridContainer.removeUserVideo(uid, false);
            onBackPressed();
        }
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        //Log.d(TAG, "onUserJoined: " + uid);
        /*SurfaceView remoteSurfaceView = prepareRtcVideo(uid, false);
        binding.pkHostLayout.getRightVideoLayout().removeAllViews();
        binding.pkHostLayout.getRightVideoLayout().addView(remoteSurfaceView);
        rtcEngine().muteLocalAudioStream(false);
        remoteSurfaceView.setZOrderMediaOverlay(true);*/
    }

    @Override
    public void onLastmileQuality(int quality) {

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
    public void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result) {
        Log.d(TAG, "onLastmileProbeResult: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: isFinishing " + isFinishing());
        BaseActivity.STATUS_LIVE = false;
        if (!isFinishing()) {
            pkLiveCutInBetweenFireEvent();
            MySocketManager.getInstance().removeLiveHandler(liveHandler);

        }
        hostAPICall.stopApiCallLoop();
        statsManager().clearAllData();
        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacks(runnable);
        handler.removeCallbacksAndMessages(null);
        socketHandler.removeCallbacks(socketRunnable);
        socketHandler.removeCallbacksAndMessages(null);


        Log.d(TAG, "onDestroy: is pk mode" + isPkView);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop:  ma jkay che -------------------------------- ");
      /*  if (!isFinishing()) {
            pkLiveCutInBetweenFireEvent();
        }*/
    }

    private void pkLiveCutInBetweenFireEvent() {
        if (timer != null) {
            timer.cancel();
        }
        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.removeCallbacksAndMessages(null);
        socketHandler.removeCallbacks(socketRunnable);
        socketHandler.removeCallbacksAndMessages(null);
        Log.d(TAG, "pkLiveCutInBetweenFireEvent: isPkStart" + isPkStart);
        Log.d(TAG, "pkLiveCutInBetweenFireEvent: isPkView" + isPkView);
        liveEndFireEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.getUser().isHost()) {
            hostAPICall.startApiCallLoop();
        }
    }


}