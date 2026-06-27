package com.example.rayzi.fake.pk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.R;
import com.example.rayzi.RayziUtils;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.databinding.ActivityHostFakePkliveBinding;
import com.example.rayzi.emoji.EmojiBottomsheetFragment;
import com.example.rayzi.fake.pk.model.FakeGiftRoot;
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
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FakeWatchPKLiveActivity extends BaseActivity {
    public static final String TAG = "FakeHostPKLiveActivity";
    ActivityHostFakePkliveBinding binding;
    boolean ishost;
    int count = 0;
    EmojiBottomsheetFragment emojiBottomsheetFragment;
    PkAudioLiveUserRoot.UsersItem host;
    Handler handler = new Handler();
    Handler handler2 = new Handler();
    int giftcount = 0;
    int leftcount = 0;
    int rightcount = 0;
    long animationDurationMillis;
    List<FakeGiftRoot> giftRootList = new ArrayList<>();
    List<GiftRoot> giftList = new ArrayList<>();
    private EmojiSheetViewModel giftViewModel;
    private CountDownTimer timer;
    private HostLiveViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_fake_pklive);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new HostLiveViewModel()).createFor()).get(HostLiveViewModel.class);
        giftViewModel = ViewModelProviders.of(this, new ViewModelFactory(new EmojiSheetViewModel()).createFor()).get(EmojiSheetViewModel.class);
        viewModel.initLister();
        emojiBottomsheetFragment = new EmojiBottomsheetFragment();
        giftRootList = Demo_contents.getRandomGiftList();
        giftViewModel.initEmojiSheet(this);
        giftViewModel.getGiftCategory();
        handleIntentData();
    }

    private void initView() {
        viewModel.liveStramCommentAdapter.addSingleComment(null);
        viewModel.liveStramCommentAdapter.addSingleComment(new LiveStramComment("", sessionManager.getUser(), true, null, "", "comment", ""));
        handler.postDelayed(runnable, 4000);
        //Glide.with(HostPKLiveActivity.this).load(giftRootList.get(0).getUrl()).into(binding.imgGift);
        handler2.postDelayed(giftrunnable, 5000);
        //leftcount++;

        //count++;
        Log.d(TAG, "initView: 1");
        binding.rvComments.setAdapter(viewModel.liveStramCommentAdapter);

        binding.rvViewUsers.setAdapter(viewModel.liveViewUserAdapter);

        Log.d(TAG, "initView: 2");
        if (!ishost && host != null) {
            binding.lytHost.setVisibility(View.VISIBLE);
            binding.lytFilterFunctions.setVisibility(View.GONE);
            if (!isFinishing()) {
                Glide.with(FakeWatchPKLiveActivity.this).load(host.getImage()).circleCrop().into(binding.imgProfile);
            }
            binding.tvName.setText(host.getUsername());
            String uniqueId;
            if (host.getUniqueId() != null && !host.getUniqueId().isEmpty()) {
                uniqueId = host.getUniqueId();
            } else {
                Random random = new Random();
                int randomNumber = 100000 + random.nextInt(900000);
                uniqueId = String.valueOf(randomNumber);
            }
            binding.tvUniqueId.setText(getString(R.string.id) + uniqueId);
        } else {
            binding.lytHost.setVisibility(View.GONE);
            binding.lytFilterFunctions.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, "initView: 3");
        binding.pkHostLayout.playAnim();

//        binding.pkHostLayout.setRightUserImage("https://images.unsplash.com/photo-1597983073453-ef06cfc2240e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=880&q=80");


        binding.pkHostLayout.setPoints(count, 0);

        if (host.getPkVideoArray() != null) {
            binding.pkHostLayout.setPlayer1(host.getPkVideoArray().get(0));
            binding.pkHostLayout.setPlayer2(host.getPkVideoArray().get(1));
        }

        setUpTimer();
        entryEffectShow();

        Log.d(TAG, "initView: 4");
        binding.btnClose.setOnClickListener(view -> {
            new PopupBuilder(this).showLiveEndPopup(new PopupBuilder.OnMultButtonPopupLister() {
                @Override
                public void onClickCountinue() {
                    if (ishost) endlive();
                    else {
                        binding.pkHostLayout.stopPlayer();
                        finish();
                    }
                }

                @Override
                public void onClickCancel() {

                }
            });
        });


        binding.imgfilterclose.setOnClickListener(view -> {
            binding.lytFilters.setVisibility(View.GONE);
        });

    }

    public void entryEffectShow() {
        if (sessionManager.getUser().getLiveJoinSvga() != null) {
            binding.layEntry.setVisibility(View.VISIBLE);

            SVGAImageView imageView = binding.svgImage;
            SVGAParser parser = new SVGAParser(FakeWatchPKLiveActivity.this);
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
            Glide.with(FakeWatchPKLiveActivity.this).load(sessionManager.getUser().getImage()).circleCrop().into(binding.userImage);
            Glide.with(FakeWatchPKLiveActivity.this).load(sessionManager.getUser().getAvatarFrameImage() != null && !sessionManager.getUser().getAvatarFrameImage().isEmpty() ? BuildConfig.BASE_URL + sessionManager.getUser().getAvatarFrameImage() : "").into(binding.avatarFrameImage);

            Animation animation = AnimationUtils.loadAnimation(FakeWatchPKLiveActivity.this, R.anim.slide_in_right);
            animation.setFillAfter(true);
            binding.nameLyt.startAnimation(animation);
        }
    }

    private void initListener() {

        binding.lytHost.setOnClickListener(view -> {
            startActivity(new Intent(FakeWatchPKLiveActivity.this, GuestActivity.class).putExtra(Const.USERID, host.getId()));
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
                }  else {
                    new BottomSheetGameTeenPatti(this, gameItem.getLink(), new BottomSheetGameTeenPatti.OnDialogDismissListener() {
                        @Override
                        public void onDismiss() {
                            MySocketManager.getInstance().getSocket().emit(Const.USER_COIN_UPDATE, sessionManager.getUser().getId());
                        }
                    });
                }
            });
        });
        binding.giftBtn.setOnClickListener(v -> {
            if (!emojiBottomsheetFragment.isAdded()) {
                emojiBottomsheetFragment.show(getSupportFragmentManager(), "emojifragfmetn");
            }
        });
        binding.imgshare.setOnClickListener(v -> {
            binding.imgshare.setEnabled(false);
            BranchUniversalObject buo = new BranchUniversalObject().setCanonicalIdentifier("content/12345").setTitle("Watch Live Video").setContentDescription("By : " + host.getName()).setContentImageUrl(host.getImage()).setContentMetadata(new ContentMetadata().addCustomMetadata("type", "LIVE").addCustomMetadata(Const.DATA, new Gson().toJson(host)));

            LinkProperties lp = new LinkProperties().setChannel("facebook").setFeature("sharing").setCampaign("content 123 launch").setStage("new user")
                    .addControlParameter("", "").addControlParameter("", Long.toString(Calendar.getInstance().getTimeInMillis()));

            buo.generateShortUrl(this, lp, (url, error) -> {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareMessage = url;
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.choose_one)));
                    binding.imgshare.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        giftViewModel.finelGift.observe(this, giftItem -> {
            if (giftItem != null) {
                int totalCoin = giftItem.getCoin() * giftItem.getCount();
                if (sessionManager.getUser().getDiamond() < totalCoin) {
                    Toast.makeText(FakeWatchPKLiveActivity.this, getString(R.string.you_not_have_enough_diamonds_to_send_gift), Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d(TAG, "initListener: giftViewModel.finelGift");
                getCoin(giftItem);

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
                    SVGAParser parser = new SVGAParser(FakeWatchPKLiveActivity.this);
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
                    binding.imgGift.setVisibility(View.VISIBLE);
                    if (!isFinishing()) {
                        Glide.with(this).load(finalGiftLink).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.imgGift);
                    }
                    new Handler(Looper.getMainLooper()).postDelayed(() -> binding.imgGift.setImageDrawable(null), 4000);
                }

                emojiBottomsheetFragment.dismiss();

            }
        });

    }

    private void handleIntentData() {
        Intent intent = getIntent();
        String dataStr = intent.getStringExtra(Const.DATA);
        if (dataStr != null && !dataStr.isEmpty()) {
            host = new Gson().fromJson(dataStr, PkAudioLiveUserRoot.UsersItem.class);
            initView();
            initListener();
        }
    }

    private void setUpTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        // Log.d(TAG, "setUpTimer: ");

        timer = new CountDownTimer(60 * 1000L, 1000) {

            public void onTick(long millisUntilFinished) {
                long counter = millisUntilFinished / 1000;

                // Log.d(TAG, "onTick: " + convertSecondsToHMmSs(counter));

                binding.pkHostLayout.setTime(convertSecondsToHMmSs(counter));

            }

            public void onFinish() {
                try {
                    endlive();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };

        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.pkHostLayout.stopPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.pkHostLayout.stopPlayer();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onBackPressed() {
        new PopupBuilder(this).showLiveEndPopup(new PopupBuilder.OnMultButtonPopupLister() {
            @Override
            public void onClickCountinue() {
                if (ishost) endlive();
                else {
                    binding.pkHostLayout.stopPlayer();
                    finish();
                }
            }

            @Override
            public void onClickCancel() {

            }
        });
    }


    public void endlive() {

        handler.removeCallbacks(runnable);
        handler2.removeCallbacks(giftrunnable);


        if (binding.pkHostLayout.getLeftPoints() > binding.pkHostLayout.getRightpoint()) {
            binding.pkHostLayout.setWinner(1);
        } else if (binding.pkHostLayout.getRightpoint() > binding.pkHostLayout.getLeftPoints()) {
            binding.pkHostLayout.setWinner(2);
        } else {
            binding.pkHostLayout.setWinner(0);
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.pkHostLayout.stopPlayer();
                finish();
            }
        }, 3000);


    }


    public void onClickGifIcon(View view) {

        if (binding.lytFilters.getVisibility() == View.GONE)
            binding.lytFilters.setVisibility(View.VISIBLE);
        else binding.lytFilters.setVisibility(View.GONE);

        viewModel.isShowFilterSheet.setValue(true);
        binding.rvFilters.setAdapter(viewModel.filterAdapter2);
    }

    public void onLocalAudioMuteClicked(View view) {

    }

    public void onSwitchCameraClicked(View view) {

    }

    public void onClickStickerIcon(View view) {
        if (binding.lytFilters.getVisibility() == View.GONE)
            binding.lytFilters.setVisibility(View.VISIBLE);
        else binding.lytFilters.setVisibility(View.GONE);

        viewModel.isShowFilterSheet.setValue(true);
        binding.rvFilters.setAdapter(viewModel.stickerAdapter);
    }

    public void onClickFilter(View view) {

        if (binding.lytFilters.getVisibility() == View.GONE)
            binding.lytFilters.setVisibility(View.VISIBLE);
        else binding.lytFilters.setVisibility(View.GONE);

        viewModel.isShowFilterSheet.setValue(true);
        binding.rvFilters.setAdapter(viewModel.filterAdapter_tt);

    }

    private void getCoin(GiftRoot.GiftItem selectedGift) {
        Call<UserRoot> call = RetrofitBuilder.create().getCoin(sessionManager.getUser().getId(), selectedGift.getCoin(), "", Const.LIVE);
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

    public static String convertSecondsToHMmSs(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%d:%02d:%02d", h, m, s);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewModel.liveStramCommentAdapter.addSingleComment(Demo_contents.getLiveStreamComment().get(0));
            binding.rvComments.scrollToPosition(0);
            handler.postDelayed(this, 4000);
        }
    };
    Runnable giftrunnable = new Runnable() {
        @Override
        public void run() {
            giftcount++;

            if (giftcount == 12) {
                giftcount = 0;
                return;
            }
            int random = new Random().nextInt((10 - 1) + 1) + 1;

            if (giftcount % 2 == 0) {
                leftcount++;
                if (!isFinishing()) {
                    Glide.with(FakeWatchPKLiveActivity.this).load(giftRootList.get(2).getUrl()).into(binding.imgGift);
                }
            } else {
                rightcount++;
                if (!isFinishing()) {
                    Glide.with(FakeWatchPKLiveActivity.this).load(giftRootList.get(4).getUrl()).into(binding.imgGift);
                }
            }
            if (!isFinishing()) {
                Glide.with(FakeWatchPKLiveActivity.this).load(RayziUtils.getImageFromNumber(random)).into(binding.imgGiftCount);
            }
            binding.tvGiftUserName.setText(Demo_contents.getUsers(true).get(0).getName() + " :"+ getString(R.string.send_a_gift));
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                binding.imgGift.setImageDrawable(null);
                binding.imgGiftCount.setImageDrawable(null);
                binding.tvGiftUserName.setText("");
            }, 3000);
            binding.pkHostLayout.setPoints(leftcount, rightcount);

            handler2.postDelayed(this, 5000);

        }
    };
}