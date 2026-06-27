package com.example.rayzi.pk.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.MainApplication;
import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.ItemLivestramCommentBinding;
import com.example.rayzi.modelclass.PKLiveStramComment;
import com.example.rayzi.modelclass.UserRoot;

import java.util.ArrayList;
import java.util.List;

public class PKLiveStramCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "PKLiveStramCommentAdapter";
    private static final int VIEW1 = 1;
    private static final int VIEW2 = 2;
    SessionManager sessionManager;
    List<PKLiveStramComment> comments = new ArrayList<>();
    OnCommentClickListner onCommentClickListner;
    private Context context;
    private String hostLiveStreamingId;
    private boolean isPkOn;

    @Override
    public int getItemViewType(int position) {
        if (comments.get(position) == null) return VIEW1;
        return VIEW2;
    }

    public OnCommentClickListner getOnCommentClickListner() {
        return onCommentClickListner;
    }

    public void setOnCommentClickListner(OnCommentClickListner onCommentClickListner) {
        this.onCommentClickListner = onCommentClickListner;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        sessionManager = new SessionManager(context);
        if (viewType == VIEW1) {
            return new NoticeViewHOlder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_livestream_comment_1, parent, false));
        }
        return new CommentViewHOlder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_livestram_comment, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CommentViewHOlder) {
            ((CommentViewHOlder) holder).setCommentData(position);
        }
    }


    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void addSingleComment(PKLiveStramComment liveStramCommentDummy, String liveHostId, boolean isPkOn) {
        this.comments.add(0, liveStramCommentDummy);
        this.hostLiveStreamingId = liveHostId;
        this.isPkOn = isPkOn;
        notifyItemInserted(0);
    }

    public interface OnCommentClickListner {
        void onClickCommet(UserRoot.User userDummy);
    }

    public class NoticeViewHOlder extends RecyclerView.ViewHolder {
        public NoticeViewHOlder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class CommentViewHOlder extends RecyclerView.ViewHolder {
        ItemLivestramCommentBinding binding;

        public CommentViewHOlder(@NonNull View itemView) {
            super(itemView);
            binding = ItemLivestramCommentBinding.bind(itemView);

        }

        public void setCommentData(int position) {
            PKLiveStramComment pkLiveComment = comments.get(position);

            if (pkLiveComment.getUser() != null) {
                if (pkLiveComment.getUser().getLevel() != null) {
//                    binding.tvLevel.setText(pkLiveComment.getUser().getLevel().getName());
                    Glide.with(context)
                            .load(BuildConfig.BASE_URL + pkLiveComment.getUser().getLevel().getImage())
                            .into(binding.ivLevel);
                }
            }

            if (pkLiveComment.isJoined()) {
                binding.tvComment.setText("Joined");
            } else {
                binding.tvComment.setText(pkLiveComment.getComment());
            }

            binding.imgReaction.setVisibility(View.GONE);

            Log.d(TAG, "setCommentData: isPkOn ====" + isPkOn);
            Log.d(TAG, "setCommentData: liveHostId " + hostLiveStreamingId + "=======================" + sessionManager.getUser().getId());
            Log.d(TAG, "setCommentData: getLiveStreamingId " + pkLiveComment.getLiveStreamingId());
            Log.d(TAG, "setCommentData: .getAvatarFrameImage() " + pkLiveComment.getUser().getAvatarFrameImage());

            binding.tvName.setText(pkLiveComment.getUser().getName());

            if (pkLiveComment.getType() != null && pkLiveComment.getType().equals("reaction")) {
                binding.tvComment.setText(R.string.reacted);
                binding.tvName.setText(pkLiveComment.getUser().getName());
                binding.tvJoined.setVisibility(View.GONE);
                binding.imgReaction.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(pkLiveComment.getReaction())
                        .into(binding.imgReaction);
            } else {
                if (pkLiveComment.isJoined() && !pkLiveComment.getComment().isEmpty()) {
                    binding.tvJoined.setText(pkLiveComment.getComment());
                    binding.tvName.setVisibility(View.GONE);
                    binding.tvJoined.setVisibility(View.VISIBLE);
                    binding.imgReaction.setVisibility(View.GONE);
                    binding.layUserImage.setVisibility(View.GONE);
                    binding.layTvComment.setVisibility(View.GONE);
                    binding.tvJoined.setTextColor(context.getResources().getColor(R.color.green_light));
                } else if (pkLiveComment.isJoined()) {
                    binding.tvJoined.setText(R.string.enter_a_room);
                    binding.tvName.setText(pkLiveComment.getUser().getName() + " :");
                    binding.tvName.setVisibility(View.VISIBLE);
                    binding.tvJoined.setVisibility(View.VISIBLE);
                    binding.imgReaction.setVisibility(View.GONE);
                    binding.layTvComment.setVisibility(View.GONE);
                    binding.layUserImage.setVisibility(View.VISIBLE);
                    binding.tvJoined.setTextColor(context.getResources().getColor(R.color.white));
                } else {
                    binding.tvJoined.setVisibility(View.GONE);
                    binding.tvName.setVisibility(View.VISIBLE);
                    binding.imgReaction.setVisibility(View.GONE);
                    binding.layTvComment.setVisibility(View.VISIBLE);
                    binding.layUserImage.setVisibility(View.VISIBLE);
                    binding.tvComment.setText(pkLiveComment.getComment());
                    binding.tvName.setText(pkLiveComment.getUser().getName());
                }
            }

            binding.imgUser.setUserImage(pkLiveComment.getUser().getImage(), pkLiveComment.getUser().getAvatarFrameImage(), 10);
            binding.getRoot().setOnClickListener(v -> onCommentClickListner.onClickCommet(pkLiveComment.getUser()));
        }

        private void setUserLevel(String image, ImageView buttomLevel) {
            Glide.with(context).load(BuildConfig.BASE_URL + image)
                    .apply(MainApplication.requestOptions)
                    .into(buttomLevel);
        }


    }
}
