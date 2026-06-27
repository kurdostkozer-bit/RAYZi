package com.example.rayzi.audioLive;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.R;
import com.example.rayzi.RayziUtils;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.ItemSeatBinding;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.IRtcEngineEventHandler;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatHolder> {

    private static final String TAG = "SeatAdapter";
    private Context context;
    private List<PkAudioLiveUserRoot.UsersItem.SeatItem> seatList = new ArrayList<>();
    private SessionManager sessionManager;
    private onSeatClick onSeatClick;

    public SeatAdapter(Context context, SessionManager sessionManager) {
        this.context = context;
        this.sessionManager = sessionManager;
    }

    public void setOnSeatClick(onSeatClick onSeatClick) {
        this.onSeatClick = onSeatClick;
    }

    @NonNull
    @Override
    public SeatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSeatBinding binding = ItemSeatBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SeatHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatHolder holder, int position) {
        PkAudioLiveUserRoot.UsersItem.SeatItem seatItem = seatList.get(position);

        Log.d(TAG, "onBindViewHolder: seatItem.isMute() " + seatItem.isMute());

//        checkMuteValues();
        // Set the seat image
        setImage(seatItem, holder.binding);

        // Handle mute visibility
        holder.binding.ivMute.setVisibility(seatItem.isMute() == 1 || seatItem.isMute() == 2 ? View.VISIBLE : View.GONE);

        // Set the seat name
        holder.binding.nameCount.setText(getNameText(seatItem));
        RayziUtils.marqueeText(holder.binding.nameCount);

        // Handle speaking animation
        handleSpeakingAnimation(seatItem, holder.binding);

        // Set click listeners
        holder.binding.image.setOnClickListener(view -> {
            onSeatClick.OnClickSeat(seatItem, position);
        });

        // Handle item animation
        if (seatItem.isAnimate()) {
            startAnimation(holder.binding);
        } else {
            stopAnimation(holder.binding);
        }
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers) {
        for (IRtcEngineEventHandler.AudioVolumeInfo info : speakers) {
            for (int i = 0; i < seatList.size(); i++) {
                if (info.channelId != null && !info.channelId.isEmpty() && info.uid == seatList.get(i).getAgoraUid()) {
                    Log.d(TAG, "onAudioVolumeIndication: INADAPTER " + info.uid + "  user:pos " + i + "agoraUID " + seatList.get(i).getAgoraUid());
                    seatList.get(i).setAnimate(true);
                    notifyItemChanged(i);
                }
            }
        }
    }

    public void onAudioVolumeIndicationSingle(IRtcEngineEventHandler.AudioVolumeInfo info) {

            for (int i = 0; i < seatList.size(); i++) {
                if (info.channelId != null && !info.channelId.isEmpty() && info.uid == seatList.get(i).getAgoraUid()) {
                    Log.d(TAG, "onAudioVolumeIndication: INADAPTER " + info.uid + "  user:pos " + i + "agoraUID " + seatList.get(i).getAgoraUid());
                    seatList.get(i).setAnimate(true);
                    notifyItemChanged(i);
                }
            }

    }


    private void setImage(PkAudioLiveUserRoot.UsersItem.SeatItem seatItem, ItemSeatBinding binding) {
        Log.d(TAG, "setImage: " + seatItem.toString());

        binding.avatarFrameImage.setVisibility(View.VISIBLE);
        if (seatItem.getAvatarFrame() != null && !seatItem.getAvatarFrame().isEmpty()) {
            binding.userImage.setPadding(5, 5, 5, 5);
        } else {
            binding.userImage.setPadding(0, 0, 0, 0);
        }

        if (seatItem.isReserved()) {
            Glide.with(context).load(seatItem.getImage()).circleCrop().into(binding.userImage);
            Glide.with(context).load(BuildConfig.BASE_URL + seatItem.getAvatarFrame()).into(binding.avatarFrameImage);
            binding.ivMute.setVisibility(View.GONE);
        } else if (!seatItem.isReserved() && !seatItem.isLock()) {
            Glide.with(context).load(R.drawable.audio_sit).into(binding.userImage);
            binding.avatarFrameImage.setVisibility(View.GONE);
            binding.ivMute.setVisibility(View.GONE);
        } else if (seatItem.isLock()) {
            Glide.with(context).load(R.drawable.audio_lock).into(binding.userImage);
            binding.avatarFrameImage.setVisibility(View.GONE);
            binding.ivMute.setVisibility(View.GONE);
        }

        if (!seatItem.isReserved() && (seatItem.isMute() == 1 || seatItem.isMute() == 2)) {
            binding.ivMute.setVisibility(View.VISIBLE);
            binding.muteMicSeat.setVisibility(View.VISIBLE);
        }


        if (seatItem.isReactionRunning()) {
            Glide.with(context).load(seatItem.getReactionImage()).into(binding.imgHostReaction);
        } else {
            binding.imgHostReaction.setImageDrawable(null);
        }
    }

    private void handleSpeakingAnimation(PkAudioLiveUserRoot.UsersItem.SeatItem seatItem, ItemSeatBinding binding) {
        if (seatItem.isIsSpeaking() && seatItem.getUserId() != null && seatItem.getUserId().equalsIgnoreCase(sessionManager.getUser().getId())) {
            binding.animationView1.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> binding.animationView1.setVisibility(View.GONE), 3000);
        }
    }

    private void startAnimation(ItemSeatBinding binding) {
        binding.animationView1.setVisibility(View.VISIBLE);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            binding.animationView1.setVisibility(View.GONE);
        }, 1500);
    }

    private void stopAnimation(ItemSeatBinding binding) {
        binding.animationView1.setVisibility(View.GONE);
    }

    public String getNameText(PkAudioLiveUserRoot.UsersItem.SeatItem seatItem) {
        return seatItem.isReserved() ? seatItem.getName() : String.valueOf(seatItem.getPosition() + 1);
    }

    public void addData(List<PkAudioLiveUserRoot.UsersItem.SeatItem> seat) {
        int startPosition = seatList.size();
        seatList.addAll(seat);
        notifyItemRangeInserted(startPosition, seat.size());
    }

    public void updateData(List<PkAudioLiveUserRoot.UsersItem.SeatItem> seat) {
        seatList.clear();
        seatList.addAll(seat);
        checkMuteValues();
        notifyDataSetChanged();
    }

    public void clear() {
        seatList.clear();
        notifyDataSetChanged();
    }

    public List<PkAudioLiveUserRoot.UsersItem.SeatItem> getList() {
        return seatList;
    }

    public class SeatHolder extends RecyclerView.ViewHolder {
        ItemSeatBinding binding;

        public SeatHolder(ItemSeatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void checkMuteValues() {
        for (int i = 0; i < seatList.size(); i++) {
            PkAudioLiveUserRoot.UsersItem.SeatItem seatItem = seatList.get(i);
            Log.d(TAG, "Position: " + i + " | isMute: " + seatItem.isMute());
        }
    }


    public interface onSeatClick {
        void OnClickSeat(PkAudioLiveUserRoot.UsersItem.SeatItem seatItem, int position);


    }
}
