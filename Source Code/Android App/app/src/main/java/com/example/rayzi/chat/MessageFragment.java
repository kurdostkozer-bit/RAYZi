package com.example.rayzi.chat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.rayzi.MyLoader;
import com.example.rayzi.R;
import com.example.rayzi.activity.BaseFragment;
import com.example.rayzi.activity.FakeChatActivity;
import com.example.rayzi.activity.ProfileActivity;
import com.example.rayzi.databinding.FragmentMessageBinding;
import com.example.rayzi.modelclass.ChatUserListRoot;
import com.example.rayzi.modelclass.RestResponse;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageFragment extends BaseFragment {
    FragmentMessageBinding binding;
    private int start = 0;
    MyLoader myLoader = new MyLoader();

    public MessageFragment() {
        // Required empty public constructor
    }

    ChatUserAdapter chatUserAdapter = new ChatUserAdapter();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container, false);

        binding.setMyLoder(myLoader);
        initView();
//        getChatUserList(false);

        binding.swipeRefresh.setOnRefreshListener((refreshLayout) -> {
            getChatUserList(false);
        });
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
            getChatUserList(true);
        });

        chatUserAdapter.setOnClickListener((position, chatUserDummy) -> {
            if (!chatUserDummy.isFake()) {
                getContext().startActivity(new Intent(getContext(), ChatActivity.class).putExtra(Const.CHATROOM, new Gson().toJson(chatUserDummy)));
            } else {
                getContext().startActivity(new Intent(getContext(), FakeChatActivity.class).putExtra(Const.CHATROOM, new Gson().toJson(chatUserDummy)));
            }
        });

        binding.layDeleteChat.setOnClickListener(view -> {
            Dialog dialog = new Dialog(getContext());
            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
            dialog.setContentView(R.layout.delete_popup);

            TextView textView = dialog.findViewById(R.id.tvTitle);
            textView.setText("This action will permanently remove all chats from the list. Are you sure you want to proceed?");

            Button yes = dialog.findViewById(R.id.yes);
            Button no = dialog.findViewById(R.id.no);

            yes.setOnClickListener(v -> {
                Call<RestResponse> call = RetrofitBuilder.create().deleteAllChat(sessionManager.getUser().getId());
                call.enqueue(new Callback<RestResponse>() {
                    @Override
                    public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                        if (response.code() == 200) {
                            chatUserAdapter.clear();
                            myLoader.noData.set(true);
                            start = 0;
                            Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RestResponse> call, Throwable t) {

                    }
                });

                // Delete chat user from list


                dialog.dismiss();
            });

            no.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        });

        return binding.getRoot();
    }

    private void getChatUserList(boolean isLoadMore) {

        myLoader.noData.set(false);
        if (isLoadMore) {
            start = start + Const.LIMIT;

        } else {
            start = 0;
            chatUserAdapter.clear();
            myLoader.isFristTimeLoading.set(true);
        }

        Call<ChatUserListRoot> call = RetrofitBuilder.create().getChatUserList(sessionManager.getUser().getId(), start, Const.LIMIT);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ChatUserListRoot> call, Response<ChatUserListRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getChatList().isEmpty()) {
                        chatUserAdapter.addData(response.body().getChatList());
                        binding.layDeleteChat.setVisibility(View.VISIBLE);
                    } else if (start == 0) {
                        myLoader.noData.set(true);
                        binding.layDeleteChat.setVisibility(View.GONE);
                    }
                }
                myLoader.isFristTimeLoading.set(false);
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
            }

            @Override
            public void onFailure(Call<ChatUserListRoot> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void initView() {
        binding.rvMessage.setAdapter(chatUserAdapter);

        binding.ivProfile.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ProfileActivity.class));
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getActivity().isFinishing()) {
            binding.ivProfile.setUserImage(sessionManager.getUser().getImage(), sessionManager.getUser().getAvatarFrameImage(), 10);
        }
        getChatUserList(false);
    }

}