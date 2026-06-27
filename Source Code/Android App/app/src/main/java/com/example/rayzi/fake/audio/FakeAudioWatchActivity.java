package com.example.rayzi.fake.audio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.R;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.databinding.ActivityFakeAudioWatchBinding;
import com.example.rayzi.databinding.ItemSeatBinding;
import com.example.rayzi.emoji.EmojiBottomsheetFragment;
import com.example.rayzi.emoji.UserSelectableClass;
import com.example.rayzi.fake.audio.adapter.FakeSeatAdapter;
import com.example.rayzi.fake.audio.model.SeatModalClass;
import com.example.rayzi.liveGame.bottomsheet.BottomSheetGameAviator;
import com.example.rayzi.liveGame.bottomsheet.BottomSheetGameCasino;
import com.example.rayzi.liveGame.bottomsheet.BottomSheetGameList;
import com.example.rayzi.liveGame.bottomsheet.BottomSheetGameRocketCrash;
import com.example.rayzi.liveGame.bottomsheet.BottomSheetGameTeenPatti;
import com.example.rayzi.liveGame.dialog.DialogGame;
import com.example.rayzi.modelclass.GiftRoot;
import com.example.rayzi.modelclass.LiveStramComment;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;
import com.example.rayzi.popups.PopupBuilder;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.socket.MySocketManager;
import com.example.rayzi.user.guestUser.GuestActivity;
import com.example.rayzi.viewModel.EmojiSheetViewModel;
import com.example.rayzi.viewModel.HostLiveViewModel;
import com.example.rayzi.viewModel.ViewModelFactory;
import com.example.rayzi.z_demo.Demo_contents;
import com.google.gson.Gson;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGADynamicEntity;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FakeAudioWatchActivity extends BaseActivity {
    private static final String TAG = "FakeAudioWatchActivity";
    private ActivityFakeAudioWatchBinding binding;
    private PkAudioLiveUserRoot.UsersItem host;
    private EmojiSheetViewModel giftViewModel;
    private HostLiveViewModel viewModel;
    EmojiBottomsheetFragment emojiBottomsheetFragment;
    FakeSeatAdapter seatAdapter = new FakeSeatAdapter();

    long animationDurationMillis;
    Handler handler = new Handler();
    Handler viewHandler = new Handler();
    Runnable viewRunnable = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            int random = new Random().nextInt((50 - 5) + 1) + 5;

            binding.tvViewUserCount.setText("" + random);
            viewHandler.postDelayed(viewRunnable, 7000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fake_audio_watch);
        handleIntentData();

    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new HostLiveViewModel()).createFor()).get(HostLiveViewModel.class);
        giftViewModel = ViewModelProviders.of(this, new ViewModelFactory(new EmojiSheetViewModel()).createFor()).get(EmojiSheetViewModel.class);
        binding.setViewmodel(viewModel);
        viewModel.initLister();
        giftViewModel.initEmojiSheet(this);
        giftViewModel.getGiftCategory();
        emojiBottomsheetFragment = new EmojiBottomsheetFragment(true);
        viewModel.liveStramCommentAdapter.addSingleComment(new LiveStramComment("", sessionManager.getUser(), true, null, "", "comment", ""));
        handler.postDelayed(runnable, 4000);
        viewHandler.postDelayed(viewRunnable, 7000);
        if (!isFinishing()) {
            Glide.with(FakeAudioWatchActivity.this).load(host.getRoomImage()).circleCrop().into(binding.userImageHostLyt);
            binding.mainHostProfileImage.setUserImage(host.getImage(), host.getAvatarFrameImage(), 30);
            binding.mainHostnameCount.setText(host.getName());
            binding.tvName.setText(host.getRoomName());
            String uniqueId;
            if (host.getUniqueId() != null && !host.getUniqueId().isEmpty()) {
                uniqueId = host.getUniqueId();
            } else {
                Random random = new Random();
                int randomNumber = 100000 + random.nextInt(900000);
                uniqueId = String.valueOf(randomNumber);
            }
            binding.tvUniqueId.setText("ID: " + uniqueId);
            binding.tvRcoins.setText(host.getRCoin() + "");
            binding.tvViewUserCount.setText("" + host.getView());
        }
        binding.rvSeat.setAdapter(seatAdapter);
        viewModel.liveStramCommentAdapter.addSingleComment(null);
        entryEffectShow();
        getData();
    }

    public void entryEffectShow() {
        if (sessionManager.getUser().getLiveJoinSvga() != null) {
            binding.layEntry.setVisibility(View.VISIBLE);

            SVGAImageView imageView = binding.svgImage;
            SVGAParser parser = new SVGAParser(FakeAudioWatchActivity.this);
            try {
                parser.decodeFromURL(new URL(sessionManager.getUser().getLiveJoinSvga() != null && !sessionManager.getUser().getLiveJoinSvga().getImage().isEmpty() ? BuildConfig.BASE_URL + sessionManager.getUser().getLiveJoinSvga().getImage() : ""), new SVGAParser.ParseCompletion() {
                    @Override
                    public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                        SVGADynamicEntity dynamicEntity = new SVGADynamicEntity();
                        dynamicEntity.setDynamicImage(BuildConfig.BASE_URL + sessionManager.getUser().getLiveJoinSvga().getImage(), "99");
                        SVGADrawable drawable = new SVGADrawable(svgaVideoEntity, dynamicEntity);
                        imageView.setImageDrawable(drawable);
                        imageView.startAnimation();
                        animationDurationMillis = svgaVideoEntity.getFrames() / svgaVideoEntity.getFPS() * 1000L;
                        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(() -> {
                            binding.svgaImage.setVisibility(View.GONE);
                            binding.layEntry.setVisibility(View.GONE);
                            binding.svgaImage.clear();
                        }, animationDurationMillis);
                    }

                    @Override
                    public void onError() {

                    }
                }, null);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            binding.userName.setText(sessionManager.getUser().getName());
            Glide.with(FakeAudioWatchActivity.this).load(sessionManager.getUser().getImage()).circleCrop().into(binding.userImage);
            Glide.with(FakeAudioWatchActivity.this).load(sessionManager.getUser().getAvatarFrameImage() != null && !sessionManager.getUser().getAvatarFrameImage().isEmpty() ? BuildConfig.BASE_URL + sessionManager.getUser().getAvatarFrameImage() : "").into(binding.avatarFrameImage);

            Animation animation = AnimationUtils.loadAnimation(FakeAudioWatchActivity.this, R.anim.slide_in_right);
            animation.setFillAfter(true);
            binding.nameLyt.startAnimation(animation);
        }
    }

    private void getData() {
        SeatModalClass seatModalClass;
        List<SeatModalClass> seatList1 = new ArrayList<>();
        List<SeatModalClass> seatList2 = new ArrayList<>();
        List<SeatModalClass> seatList3 = new ArrayList<>();

        seatModalClass = new SeatModalClass("1", R.drawable.audio_sit, "", false);
        seatList1.add(0, seatModalClass);
        seatModalClass = new SeatModalClass("2", R.drawable.img1, getString(R.string.miya), true);
        seatList1.add(1, seatModalClass);
        seatModalClass = new SeatModalClass("3", R.drawable.img2, getString(R.string.riya), true);
        seatList1.add(2, seatModalClass);
        seatModalClass = new SeatModalClass("4", R.drawable.audio_sit, "", false);
        seatList1.add(3, seatModalClass);
        seatModalClass = new SeatModalClass("5", R.drawable.img5, getString(R.string.luliya), true);
        seatList1.add(4, seatModalClass);
        seatModalClass = new SeatModalClass("6", R.drawable.audio_sit, "", false);
        seatList1.add(5, seatModalClass);
        seatModalClass = new SeatModalClass("7", R.drawable.img7, getString(R.string.rehan), true);
        seatList1.add(6, seatModalClass);
        seatModalClass = new SeatModalClass("8", R.drawable.audio_sit, "", false);
        seatList1.add(7, seatModalClass);

        seatModalClass = new SeatModalClass("1", R.drawable.audio_sit, "", false);
        seatList2.add(0, seatModalClass);
        seatModalClass = new SeatModalClass("2", R.drawable.img1, getString(R.string.susmita), true);
        seatList2.add(1, seatModalClass);
        seatModalClass = new SeatModalClass("3", R.drawable.audio_sit, "", false);
        seatList2.add(2, seatModalClass);
        seatModalClass = new SeatModalClass("4", R.drawable.img2, getString(R.string.rahi), true);
        seatList2.add(3, seatModalClass);
        seatModalClass = new SeatModalClass("5", R.drawable.audio_sit, "", false);
        seatList2.add(4, seatModalClass);
        seatModalClass = new SeatModalClass("6", R.drawable.img5, getString(R.string.nirma), true);
        seatList2.add(5, seatModalClass);
        seatModalClass = new SeatModalClass("7", R.drawable.audio_sit, "", false);
        seatList2.add(6, seatModalClass);
        seatModalClass = new SeatModalClass("8", R.drawable.img7, getString(R.string.jashvi), true);
        seatList2.add(7, seatModalClass);

        seatModalClass = new SeatModalClass("1", R.drawable.audio_sit, "", false);
        seatList3.add(0, seatModalClass);
        seatModalClass = new SeatModalClass("2", R.drawable.audio_sit, "", false);
        seatList3.add(1, seatModalClass);
        seatModalClass = new SeatModalClass("3", R.drawable.img2, getString(R.string.hely), true);
        seatList3.add(2, seatModalClass);
        seatModalClass = new SeatModalClass("4", R.drawable.img1, getString(R.string.shruti), true);
        seatList3.add(3, seatModalClass);
        seatModalClass = new SeatModalClass("5", R.drawable.img5, getString(R.string.dhruvi), true);
        seatList3.add(4, seatModalClass);
        seatModalClass = new SeatModalClass("6", R.drawable.img7, getString(R.string.mamta), true);
        seatList3.add(5, seatModalClass);
        seatModalClass = new SeatModalClass("7", R.drawable.audio_sit, "", false);
        seatList3.add(6, seatModalClass);
        seatModalClass = new SeatModalClass("8", R.drawable.img10, getString(R.string.zeel), true);
        seatList3.add(seatModalClass);

        Random random = new Random();
        int randomIndex = random.nextInt((5 - 1) + 1) + 1;
        if (randomIndex == 2) {    // lock system handle
            seatAdapter.adddata(seatList1);
        } else if (randomIndex == 5) {
            seatAdapter.adddata(seatList2);
        } else {
            seatAdapter.adddata(seatList3);
        }

        seatAdapter.setOntakeseatlistener(new FakeSeatAdapter.ontakeseat() {
            @Override
            public void onClickseat(SeatModalClass seatModalClass, int position, ItemSeatBinding binding) {
//                if (!seatModalClass.isReserved()) {
//                    boolean userRemoved = false;
//                    for (int i = 0; i < host.getSeat().size(); i++) {
//                        SeatModalClass currentItem = seatAdapter.getSeatList().get(i);
//                        if (currentItem != null && currentItem.getName() != null) {
//                            if (currentItem.getName().equals(sessionManager.getUser().getName())) {
//                                seatAdapter.getSeatList().set(i, new SeatModalClass(null, 0, null, false));
//                                userRemoved = true;
//                                Log.d(TAG, "OnClickSeat: " + i);
//                                break;
//                            }
//                        }
//                    }
//                    if (!userRemoved) {
//                        Log.d(TAG, "User not found in any seat: " + sessionManager.getUser().getName());
//                    }
//                    SeatModalClass seatItem1 = seatAdapter.getSeatList().get(position);
//                    seatItem1.setReserved(true);
//                    seatItem1.setImage(R.drawable.img10);
//                    seatItem1.setName(sessionManager.getUser().getName());
//                    seatAdapter.getSeatList().set(position, seatItem1);
//                    seatAdapter.updateData(seatAdapter.getSeatList());
//                }
            }
        });

    }

    private void handleIntentData() {
        String dataStr = getIntent().getStringExtra(Const.DATA);
        if (dataStr != null && !dataStr.isEmpty()) {
            host = new Gson().fromJson(dataStr, PkAudioLiveUserRoot.UsersItem.class);
            initView();
            initListener();
        }
    }

    private void initListener() {

        binding.lytHost.setOnClickListener(view -> startActivity(new Intent(FakeAudioWatchActivity.this, GuestActivity.class).putExtra(Const.USERID, host.getId())));

        binding.btnClose.setOnClickListener(view -> {
            new PopupBuilder(this).showLiveEndPopup(new PopupBuilder.OnMultButtonPopupLister() {
                @Override
                public void onClickCountinue() {
                    confirmEndLive();
                }

                @Override
                public void onClickCancel() {

                }
            });
        });
        binding.btnsend.setOnClickListener(v -> {
            String comment = binding.etComment.getText().toString();
            if (!comment.isEmpty()) {
                LiveStramComment liveStramComment = new LiveStramComment(comment, sessionManager.getUser(), false, host.getLiveStreamingId(), "", "comment", "");
                viewModel.liveStramCommentAdapter.addSingleComment(liveStramComment);
                binding.rvComments.scrollToPosition(0);
                binding.etComment.setText("");
            }
        });

        binding.btnGift.setOnClickListener(v -> {
            if (!emojiBottomsheetFragment.isAdded()) {
                if (giftViewModel.users.isEmpty()) {
                    giftViewModel.users.add(new UserSelectableClass(new PkAudioLiveUserRoot.UsersItem.SeatItem(host.getImage(), host.getCountry(), true,
                            "Host", false, host.getAgoraUID(), 0, true, host.getId(), -1, false, host.getLiveUserId())));
                    giftViewModel.users.add(new UserSelectableClass(new PkAudioLiveUserRoot.UsersItem.SeatItem(String.valueOf(R.drawable.new1), host.getCountry(), true,
                            "Tracy", false, host.getAgoraUID(), 0, true, host.getId(), -1, false, host.getLiveUserId())));
                    giftViewModel.users.add(new UserSelectableClass(new PkAudioLiveUserRoot.UsersItem.SeatItem(String.valueOf(R.drawable.new2), host.getCountry(), true,
                            "Lella", false, host.getAgoraUID(), 0, true, host.getId(), -1, false, host.getLiveUserId())));
                    giftViewModel.users.add(new UserSelectableClass(new PkAudioLiveUserRoot.UsersItem.SeatItem(String.valueOf(R.drawable.new3), host.getCountry(), true,
                            "Liza", false, host.getAgoraUID(), 0, true, host.getId(), -1, false, host.getLiveUserId())));
                    host.getSeat().stream().filter(PkAudioLiveUserRoot.UsersItem.SeatItem::isReserved).map(UserSelectableClass::new).forEach(giftViewModel.users::add);
                }
                emojiBottomsheetFragment.show(getSupportFragmentManager(), "emojifragfmetn");
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

        giftViewModel.finelGift.observe(this, giftItem -> {
            if (giftItem != null) {
                int totalCoin = giftItem.getCoin() * giftItem.getCount();
                if (sessionManager.getUser().getDiamond() < totalCoin) {
                    Toast.makeText(FakeAudioWatchActivity.this, getString(R.string.you_not_have_enough_diamonds_to_send_gift), Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d(TAG, "initListener: giftViewModel.finelGift");
                getCoin(giftItem);

                if (!giftViewModel.userListAdapter.getUsers().isEmpty()) {

                    List<String> selectedUsers = giftViewModel.userListAdapter.getUsers().stream()
                            .filter(UserSelectableClass::isSelected)
                            .map(user -> user.getSeatItem().getUserId())
                            .collect(Collectors.toList());

                    if (selectedUsers.isEmpty()) {
                        Toast.makeText(this, getString(R.string.select_at_least_one_user), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String finalGiftLink = null;
                    List<GiftRoot.GiftItem> giftItemList = sessionManager.getGiftsList(giftItem.getCategory());
                    for (int i = 0; i < giftItemList.size(); i++) {
                        if (giftItem.getId().equals(giftItemList.get(i).getId())) {
                            finalGiftLink = BuildConfig.BASE_URL + giftItemList.get(i).getImage();
                        }
                    }

                    if (giftItem.getType() == 2) {
                        binding.svgaImage.setVisibility(View.VISIBLE);
                        SVGAImageView imageView = binding.svgaImage;
                        SVGAParser parser = new SVGAParser(FakeAudioWatchActivity.this);
                        try {
                            parser.decodeFromURL(new URL(finalGiftLink), new SVGAParser.ParseCompletion() {
                                @Override
                                public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                                    SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
                                    imageView.setImageDrawable(drawable);
                                    imageView.startAnimation();
                                    Log.d("TAG", "setData: " + giftItem.getImage());
                                    new Handler(Looper.myLooper()).postDelayed(() -> {
                                        binding.svgaImage.setVisibility(View.GONE);
                                        binding.svgaImage.clear();
                                    }, 5000);
                                }

                                @Override
                                public void onError() {

                                }
                            }, new SVGAParser.PlayCallback() {
                                @Override
                                public void onPlay(@NonNull List<? extends File> list) {
                                }
                            });

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                    } else {
                        binding.lytGift.setVisibility(View.VISIBLE);
                        if (!isFinishing()) {
                            assert finalGiftLink != null;
                            if (finalGiftLink.contains(".gif")) {
                                Glide.with(this).asGif().load(finalGiftLink).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.imgGift);
                            } else {
                                Glide.with(this).load(finalGiftLink).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.imgGift);
                            }
                        }
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            binding.imgGift.setImageDrawable(null);
                            binding.lytGift.setVisibility(View.GONE);
                        }, 4000);
                    }

                    emojiBottomsheetFragment.dismiss();
                } else {
                    Toast.makeText(this, getString(R.string.don_t_have_user_to_sent_a_gift_wait_for_user), Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.btnMute.setOnClickListener(v -> {
            viewModel.isMuted = !viewModel.isMuted;
            if (viewModel.isMuted) {
                binding.btnMute.setImageDrawable(ContextCompat.getDrawable(FakeAudioWatchActivity.this, R.drawable.ic_mute));
            } else {
                binding.btnMute.setImageDrawable(ContextCompat.getDrawable(FakeAudioWatchActivity.this, R.drawable.ic_unmute));
            }
        });
    }

    private void getCoin(GiftRoot.GiftItem selectedGift) {
        Call<UserRoot> call = RetrofitBuilder.create().getCoin(sessionManager.getUser().getId(), (selectedGift.getCoin() * selectedGift.getCount()) * giftViewModel.users.size(), "", Const.LIVE);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getUser() != null) {
                        sessionManager.saveUser(response.body().getUser());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: getCoin ==  " + t.getMessage());
            }
        });
    }


    public void confirmEndLive() {
        handler.removeCallbacks(runnable);
        handler.removeCallbacksAndMessages(null);
        finish();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnable: fake comments ");
            viewModel.liveStramCommentAdapter.addSingleComment(Demo_contents.getLiveStreamComment().get(0));
            binding.rvComments.scrollToPosition(0);
            handler.postDelayed(this, 4000);
        }
    };

    @Override
    public void onBackPressed() {
        new PopupBuilder(this).showLiveEndPopup(new PopupBuilder.OnMultButtonPopupLister() {
            @Override
            public void onClickCountinue() {
                confirmEndLive();
            }

            @Override
            public void onClickCancel() {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        confirmEndLive();
    }
}