package com.example.rayzi.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.adapter.BlockedUserListAdapter;
import com.example.rayzi.databinding.ActivityBlockedUserListBinding;
import com.example.rayzi.modelclass.BlockedUserListRoot;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.retrofit.UserApiCall;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockedUserListActivity extends BaseActivity {

    ActivityBlockedUserListBinding binding;

    BlockedUserListAdapter blockedUserListAdapter;
    UserApiCall userApiCall;
    SessionManager sessionManager;
    List<BlockedUserListRoot.BlockedUsersItem> blockuserList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_blocked_user_list);
        userApiCall = new UserApiCall(this);
        sessionManager = new SessionManager(this);

        binding.rvBannedList.setLayoutManager(new LinearLayoutManager(this));

        binding.progressbar.setVisibility(VISIBLE);
        binding.ivBack.setOnClickListener(v -> {
            onBackPressed();
        });
        Call<BlockedUserListRoot> call = RetrofitBuilder.create().getBlockUser(sessionManager.getUser().getId());
        call.enqueue(new Callback<BlockedUserListRoot>() {

            @Override
            public void onResponse(Call<BlockedUserListRoot> call, Response<BlockedUserListRoot> response) {
                if (response.body() != null && response.isSuccessful()){
                    binding.progressbar.setVisibility(GONE);
                    blockuserList = response.body().getBlockedUsers();
                    blockedUserListAdapter.addData(blockuserList);
                    binding.rvBannedList.setAdapter(blockedUserListAdapter);

                    if (blockuserList.isEmpty()){
                        binding.tvNodataFound.setVisibility(VISIBLE);
                    }else {
                        binding.tvNodataFound.setVisibility(GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<BlockedUserListRoot> call, Throwable t) {

            }
        });


        blockedUserListAdapter = new BlockedUserListAdapter(this, new BlockedUserListAdapter.onUnblockListener() {
            @Override
            public void onUnblock(String id, int position) {

                userApiCall.blockUnblock(id, new UserApiCall.OnBlockUnblockListner() {
                    @Override
                    public void onBlockSuccess() {

                    }

                    @Override
                    public void onUnblockSuccess() {
                        blockedUserListAdapter.removeItem(position);
                        Toast.makeText(BlockedUserListActivity.this, "Unblocked Successfully...", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
}