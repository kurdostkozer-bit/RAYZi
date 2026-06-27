package com.example.rayzi.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.adapter.ProfileAdapter;
import com.example.rayzi.bottomsheets.BottomSheetBlockedUserList;
import com.example.rayzi.databinding.ActivityProfile11Binding;
import com.example.rayzi.modelclass.BlockedUserListRoot;
import com.example.rayzi.modelclass.ProfileRoot;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.posts.FeedGridActivity;
import com.example.rayzi.reels.VideoListGridActivity;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.retrofit.UserApiCall;
import com.example.rayzi.user.EditProfileActivity;
import com.example.rayzi.user.FollowrsListActivity;
import com.example.rayzi.user.MyLevelListActivity;
import com.example.rayzi.user.complain.ComplainListActivity;
import com.example.rayzi.user.complain.CreateComplainActivity;
import com.example.rayzi.user.freeCoins.FreeDimondsActivity;
import com.example.rayzi.user.vip.VipPlanActivity;
import com.example.rayzi.user.wallet.CoinSellerListActivity;
import com.example.rayzi.user.wallet.MyWalletActivity;
import com.example.rayzi.user.wallet.coinseller.SellerRechargeActivity;
import com.example.rayzi.viewModel.ViewModelFactory;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {

    ActivityProfile11Binding binding;
    SessionManager sessionManager;
    UserApiCall userApiCall;
    private UserRoot.User user;
    private ProfileViewModel viewModel;
    ProfileAdapter adapter;
    List<ProfileRoot> profileRootList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_11);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new ProfileViewModel()).createFor()).get(ProfileViewModel.class);
        binding.setViewModel(viewModel);
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        sessionManager = new SessionManager(ProfileActivity.this);
        userApiCall = new UserApiCall(ProfileActivity.this);
        user = sessionManager.getUser();
        viewModel.isLoading.set(true);

        retrofit2.Call<BlockedUserListRoot> call = RetrofitBuilder.create().getBlockUser(sessionManager.getUser().getId());
        call.enqueue(new Callback<BlockedUserListRoot>() {

            @Override
            public void onResponse(retrofit2.Call<BlockedUserListRoot> call, Response<BlockedUserListRoot> response) {
                if (response.body() != null && response.isSuccessful()){
                   binding.tvBlock.setText(String.valueOf(response.body().getTotal()));
                }
            }

            @Override
            public void onFailure(Call<BlockedUserListRoot> call, Throwable t) {

            }
        });


        userApiCall.getUser(new UserApiCall.OnUserApiListner() {
            @Override
            public void onUserGetted(UserRoot.User user) {
                ProfileActivity.this.user = user;
                sessionManager.saveUser(user);
                initView();
                viewModel.isLoading.set(false);
            }

            @Override
            public void onUserStatusFailed(String message) {

            }
        });



    }

    private void initListener() {
        binding.btnSetting.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, SettingActivity.class)));
//        binding.lytReels.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, DemoReelsActivity.class)));
        binding.lytMyPost.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, FeedGridActivity.class).putExtra(Const.DATA, new Gson().toJson(user))));
        binding.lytMyVideos.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, VideoListGridActivity.class).putExtra(Const.DATA, new Gson().toJson(user))));
        binding.lytFollowing.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, FollowrsListActivity.class).putExtra(Const.TYPE, 1).putExtra(Const.USERID, user.getId())));
        binding.lytFollowrs.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, FollowrsListActivity.class).putExtra(Const.TYPE, 2).putExtra(Const.USERID, user.getId())));
        binding.btnEditProfile.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class)));
        binding.tvLevel.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, MyLevelListActivity.class)));
        binding.layUserLevel.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, MyLevelListActivity.class)));
        binding.lytVIP.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, VipPlanActivity.class)));
        binding.layWallet.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, MyWalletActivity.class)));
        binding.lytFreeDimonds.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, FreeDimondsActivity.class)));
        binding.lytStore.setOnClickListener(view -> startActivity(new Intent(ProfileActivity.this, StoreActivity.class)));
        binding.layhostrequest.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, HostRequestActivity.class)));
        binding.lytCoinSeller.setOnClickListener(view -> startActivity(new Intent(ProfileActivity.this, SellerRechargeActivity.class)));
        binding.copy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) ProfileActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", user.getUniqueId());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(ProfileActivity.this, R.string.copied_successfully, Toast.LENGTH_SHORT).show();
        });
        binding.layAgency.setOnClickListener(view -> {

            String url = "";
            String title = "";
            if (user.isAgency()) {
                url = user.getAgencyLoginString();
                title = getString(R.string.agency_center_text);
            } else if (user.isHost()) {
                url = user.getHostLoginString();
                title = getString(R.string.host_center);
            }

            WebActivity.open(ProfileActivity.this, getString(R.string.demo_agency_center), "https://allinoneagency.codderlab.com/agencyLogin?id=66e83a1b60c17cdb24093341", false);
        });

        binding.layHost.setOnClickListener(view -> {

            String url = "";
            String title = "";
            if (user.isAgency()) {
                url = user.getAgencyLoginString();
                title = getString(R.string.agency_center_text);;
            } else if (user.isHost()) {
                url = user.getHostLoginString();
                title = getString(R.string.host_center);;
            }

            WebActivity.open(ProfileActivity.this, getString(R.string.demo_host_center_text), "https://allinonehost.codderlab.com/hostlogin?id=66e54af8c693cc852c349703", false);
        });

        adapter.setOnClickListener(new ProfileAdapter.OnItemClickListener() {
            @Override
            public void onClick(String type) {
                switch (type) {

                    case "Demo Agency Center":
                        WebActivity.open(ProfileActivity.this, getString(R.string.demo_agency_center), "https://allinoneagency.codderlab.com/?id=66eaa40f60c17cdb2409708a", false);
                        break;

                    case "Demo Host Center":
                        WebActivity.open(ProfileActivity.this, getString(R.string.demo_host_center_text), "https://allinonehost.codderlab.com/hostlogin?id=66e54af8c693cc852c349703", false);
                        break;

                    case "Agency Center":
                        WebActivity.open(ProfileActivity.this, getString(R.string.agency_center_text), user.getAgencyLoginString(), false);
                        break;

                    case "Host Center":
                        WebActivity.open(ProfileActivity.this, getString(R.string.host_center), user.getHostLoginString(), false);
                        break;

                    case "Offline Recharge":
                        startActivity(new Intent(ProfileActivity.this, SellerRechargeActivity.class));
                        break;

                    case "My Posts":
                        startActivity(new Intent(ProfileActivity.this, FeedGridActivity.class).putExtra(Const.DATA, new Gson().toJson(user)));
                        break;

                    case "My Relites":
                        startActivity(new Intent(ProfileActivity.this, VideoListGridActivity.class).putExtra(Const.DATA, new Gson().toJson(user)));
                        break;

                    case "Host Request":
                        startActivity(new Intent(ProfileActivity.this, HostRequestActivity.class));
                        break;

                    case "Store":
                        startActivity(new Intent(ProfileActivity.this, StoreActivity.class));
                        break;

                    case "Free Coins":
                        startActivity(new Intent(ProfileActivity.this, FreeDimondsActivity.class));
                        break;

                    case "Become VIP":
                        startActivity(new Intent(ProfileActivity.this, VipPlanActivity.class));
                        break;

                    case "User Level":
                        startActivity(new Intent(ProfileActivity.this, MyLevelListActivity.class));
                        break;

                }
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void initView() {

        if (user.getAvatarFrameImage().isEmpty()) {
            binding.imgUser.setVisibility(View.GONE);
            binding.layImg.setVisibility(View.VISIBLE);
        } else {
            binding.imgUser.setVisibility(View.VISIBLE);
            binding.layImg.setVisibility(View.GONE);
        }

        Glide.with(getApplicationContext()).load(user.getCoverImage()).into(binding.imgUser1);
        Glide.with(getApplicationContext()).load(user.getImage()).into(binding.imgUser2);

        if (!isFinishing()) {
            Log.d(TAG, "initView: user.getAvatarFrameImage() ==== " + BuildConfig.BASE_URL + user.getAvatarFrameImage());
            binding.imgUser.setUserImage(user.getImage(), user.getAvatarFrameImage(), 40);
        }
        binding.tvName.setText(user.getName());
        binding.tvAge.setText(String.valueOf(user.getAge()));
        binding.tvCoin.setText(String.valueOf(user.getDiamond()));
        binding.tvFollowrs.setText(String.valueOf(user.getFollowers()));
        binding.tvLevel.setText(user.getLevel().getName());
        binding.tvFollowing.setText(String.valueOf(user.getFollowing()));
        binding.ivBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.lytBlock.setOnClickListener(v -> {
           startActivity(new Intent(ProfileActivity.this,BlockedUserListActivity.class));
        });

        if (user.getUniqueId() == null || user.getUniqueId().isEmpty()) {
            binding.tvUserId.setVisibility(View.GONE);
            binding.copy.setVisibility(View.GONE);
        } else {
            binding.tvUserId.setVisibility(View.VISIBLE);
            binding.copy.setVisibility(View.VISIBLE);
            binding.tvUserId.setText(getString(R.string.id) + user.getUniqueId());

        }

        if (user.getGender().equalsIgnoreCase(Const.MALE)) {
            binding.imgGender.setImageDrawable(ContextCompat.getDrawable(ProfileActivity.this, R.drawable.male));
        } else {
            binding.imgGender.setImageDrawable(ContextCompat.getDrawable(ProfileActivity.this, R.drawable.female));
        }

        if (user.isHost() || user.isAgency()) {
            binding.layhostrequest.setVisibility(View.GONE);
        } else {
            binding.layhostrequest.setVisibility(View.VISIBLE);
        }

        profileRootList.clear();
        if (user.isAgency()) {
            profileRootList.add(new ProfileRoot(R.drawable.ic_agency_center, "Agency Center"));
//            profileRootList.add(new ProfileRoot(R.drawable.ic_agency_center, getString(R.string.demo_agency_center)));
        }

        if (user.isHost()) {
            profileRootList.add(new ProfileRoot(R.drawable.ic_host_center, "Host Center"));
//          profileRootList.add(new ProfileRoot(R.drawable.ic_host_center, getString(R.string.demo_host_center_text)));
        }

        if (!user.isAgency() && !user.isHost()) {
            profileRootList.add(new ProfileRoot(R.drawable.ic_host_request, getString(R.string.host_request)));
        }

        if (user.isCoinSeller()) {
            profileRootList.add(new ProfileRoot(R.drawable.ic_coin_seller, getString(R.string.offline_recharge)));
        }

        profileRootList.add(new ProfileRoot(R.drawable.ic_my_post1, getString(R.string.my_posts)));
        profileRootList.add(new ProfileRoot(R.drawable.ic_my_relites, getString(R.string.my_relites)));
        profileRootList.add(new ProfileRoot(R.drawable.ic_store, getString(R.string.store)));
        profileRootList.add(new ProfileRoot(R.drawable.ic_free_coin, getString(R.string.free_coins)));
        profileRootList.add(new ProfileRoot(R.drawable.ic_become_vip, getString(R.string.become_vip)));
        profileRootList.add(new ProfileRoot(R.drawable.ic_user_level, getString(R.string.user_level)));

        adapter = new ProfileAdapter();
        binding.rvOptions.setLayoutManager(new GridLayoutManager(this, 4));
        binding.rvOptions.setAdapter(adapter);
        adapter.addData(profileRootList);

        if (user.isAgency()) {
            binding.layAgency.setVisibility(View.VISIBLE);
            binding.ivAgency.setImageResource(R.drawable.ic_agency);
            binding.tvAgency.setText(R.string.agency_center_text);
        } else if (user.isHost()) {
            binding.layAgency.setVisibility(View.VISIBLE);
            binding.ivAgency.setImageResource(R.drawable.ic_user_place);
            binding.tvAgency.setText(R.string.host_center);
        } else {
            binding.layAgency.setVisibility(View.GONE);
        }

         if (sessionManager.getUser().isAgency()) {
            binding.layType.setVisibility(View.VISIBLE);
            binding.ivType.setImageResource(R.drawable.ic_agency);
        } else {
            binding.layType.setVisibility(View.GONE);
        }

        if (sessionManager.getUser().isHost()) {
            binding.layHostType.setVisibility(View.VISIBLE);
            binding.ivHostType.setImageResource(R.drawable.ic_user_place);
        } else {
            binding.layHostType.setVisibility(View.GONE);
        }
        if (sessionManager.getUser().isIsVIP()) {
            binding.layVIPType.setVisibility(View.VISIBLE);
            binding.ivVIPType.setImageResource(R.drawable.crown);
        } else {
            binding.layVIPType.setVisibility(View.GONE);
        }


        if (sessionManager.getUser().isCoinSeller()) {
            binding.lytCoinSeller.setVisibility(View.VISIBLE);
        } else {
            binding.lytCoinSeller.setVisibility(View.GONE);
        }

        initListener();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


    }

    public class ProfileViewModel extends ViewModel {
        public ObservableBoolean isLoading = new ObservableBoolean(true);
    }
}