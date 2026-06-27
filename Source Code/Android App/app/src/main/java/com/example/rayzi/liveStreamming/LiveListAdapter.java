package com.example.rayzi.liveStreamming;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.rayzi.MainApplication;
import com.example.rayzi.R;
import com.example.rayzi.databinding.ItemPartyBinding;
import com.example.rayzi.databinding.ItemPkInviteHostBinding;
import com.example.rayzi.databinding.ItemVideoGridBinding;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class LiveListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "LiveListAdapter";
    public static final int PKLIST_MODE = 1;
    public static final int LIVELIST_MODE = 2;
    public static final int PARTY_MODE = 3;

    private Context context;
    private int live_layout = 1;
    private int viewMode;
    private int[] colors = {
            R.color.lavender,
            R.color.light_yellow,
            R.color.light_pink,
            R.color.light_sky
    };
    OnHostClickLister onHostClickLister;
    private List<PkAudioLiveUserRoot.UsersItem> userDummies = new ArrayList<>();

    public LiveListAdapter(int viewMode) {
        this.viewMode = viewMode;
    }

    public void setOnHostClickLister(OnHostClickLister onHostClickLister) {
        this.onHostClickLister = onHostClickLister;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewMode == LIVELIST_MODE) {
            return new VideoListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_grid, parent, false));
        } else if (viewMode == PARTY_MODE) {
            return new PartyListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_party, parent, false));
        } else {
            return new PkInviteListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pk_invite_host, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VideoListViewHolder) {
            ((VideoListViewHolder) holder).setData(position);
        } else if (holder instanceof PkInviteListViewHolder) {
            ((PkInviteListViewHolder) holder).setData(position);
        } else if (holder instanceof PartyListViewHolder) {
            ((PartyListViewHolder) holder).setData(position);
        }
    }

    @Override
    public int getItemCount() {
        return userDummies.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
      /*  if (i % 3 == 0) {
            return this.adbanner_layout;
        }*/
        return this.live_layout;
    }

    public void addData(List<PkAudioLiveUserRoot.UsersItem> userDummies) {
        this.userDummies.addAll(userDummies);
        notifyItemRangeInserted(this.userDummies.size(), userDummies.size());
    }


    public void updateViewMode(int viewMode) {
        this.viewMode = viewMode;
    }

    public void clear() {
        userDummies.clear();
        notifyDataSetChanged();
    }

    public interface OnHostClickLister {
        void onHostItemClick(PkAudioLiveUserRoot.UsersItem userDummy, ItemVideoGridBinding itemVideoGridBinding, ItemPkInviteHostBinding itemPkInviteHostBinding);
    }

    public class VideoListViewHolder extends RecyclerView.ViewHolder {
        ItemVideoGridBinding binding;

        public VideoListViewHolder(View itemView) {
            super(itemView);
            binding = ItemVideoGridBinding.bind(itemView);
        }

        public void setData(int position) {
            PkAudioLiveUserRoot.UsersItem userDummy = userDummies.get(position);
            binding.tvName.setText(userDummy.getName());
            binding.tvCountry.setText(userDummy.getCountry());

            MultiTransformation<Bitmap> transformations = new MultiTransformation<>(
                    new BlurTransformation(70),
                    new CenterCrop()
            );

            Glide.with(context).load(userDummy.getImage())
                    .circleCrop()
                    .apply(MainApplication.requestOptionsLive)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(transformations)
                    .into(binding.ivDetails);

            AsyncTask.execute(() -> {
                try {
                    String url = userDummy.getCountryFlagImage();
                    SVG svg = SVG.getFromInputStream(new URL(url).openStream());
                    Picture picture = svg.renderToPicture();
                    ((Activity) context).runOnUiThread(() -> {
                        binding.svgWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        binding.svgWebView.setImageDrawable(new PictureDrawable(picture));
                    });

                } catch (SVGParseException | IOException e) {
                    e.printStackTrace();
                }
            });
            if (userDummy.isIsFake()) {
                if (userDummy.isIsPkMode()) {
                    binding.doubleLay.setVisibility(View.VISIBLE);
                    binding.vsLay.setVisibility(View.VISIBLE);
                    binding.signleLay.setVisibility(View.GONE);
                    binding.ivPk.setVisibility(View.VISIBLE);

                    if (userDummy.getPkImageArray() != null && userDummy.getPkImageArray().size() > 1) {

                        Glide.with(context).load(userDummy.getPkImageArray().get(0))
                                .apply(MainApplication.requestOptionsLive)
                                .centerCrop().into(binding.host1);

                        Glide.with(context).load(userDummy.getPkImageArray().get(1))
                                .apply(MainApplication.requestOptionsLive)
                                .centerCrop().into(binding.host2);
                    }
                } else {
                    binding.doubleLay.setVisibility(View.GONE);
                    binding.vsLay.setVisibility(View.GONE);
                    binding.signleLay.setVisibility(View.VISIBLE);
                    binding.ivPk.setVisibility(View.GONE);

                    Glide.with(context).load(userDummy.getImage())
                            .apply(MainApplication.requestOptionsLive)
                            .centerCrop().into(binding.image);
                }
            } else {
                if (!userDummy.isIsFake() && userDummy.isIsPkMode()) {
                    binding.doubleLay.setVisibility(View.VISIBLE);
                    binding.vsLay.setVisibility(View.VISIBLE);
                    binding.signleLay.setVisibility(View.GONE);
                    binding.ivPk.setVisibility(View.GONE);

                    if (userDummy.getPkConfig() != null) {
                        if (userDummy.getPkConfig().getHost1Details() != null && userDummy.getPkConfig().getHost2Details() != null) {
                            Glide.with(context).load(userDummy.getPkConfig().getHost1Details().getImage())
                                    .apply(MainApplication.requestOptionsLive)
                                    .centerCrop().into(binding.host1);

                            Glide.with(context).load(userDummy.getPkConfig().getHost2Details().getImage())
                                    .apply(MainApplication.requestOptionsLive)
                                    .centerCrop().into(binding.host2);
                        }
                    }

                } else {
                    binding.doubleLay.setVisibility(View.GONE);
                    binding.vsLay.setVisibility(View.GONE);
                    binding.signleLay.setVisibility(View.VISIBLE);
                    binding.ivPk.setVisibility(View.GONE);

                    if (userDummy.isAudio()) {
                        Glide.with(context).load(userDummy.getRoomImage())
                                .apply(MainApplication.requestOptionsLive)
                                .centerCrop().into(binding.image);
                    } else {

                        Glide.with(context).load(userDummy.getImage())
                                .apply(MainApplication.requestOptionsLive)
                                .centerCrop().into(binding.image);
                    }

                }
            }

            if (userDummy.isAudio()) binding.ivAudioRoom.setVisibility(View.VISIBLE);
            else binding.ivAudioRoom.setVisibility(View.GONE);
            binding.tvCountry.setText(userDummy.getCountry());
            binding.tvViewCount.setText(String.valueOf(userDummy.getView()));
            if (userDummy.isAudio()) {
                binding.tvName.setText(userDummy.getRoomName());
                binding.imag1.setUserImage(userDummy.getRoomImage(), userDummy.getAvatarFrameImage(), 10);
            } else {
                binding.tvName.setText(userDummy.getName());
                binding.imag1.setUserImage(userDummy.getImage(), userDummy.getAvatarFrameImage(), 10);
            }
            binding.getRoot().setOnClickListener(v -> onHostClickLister.onHostItemClick(userDummy, binding, null));
        }
    }

    private class PkInviteListViewHolder extends RecyclerView.ViewHolder {
        ItemPkInviteHostBinding binding;

        public PkInviteListViewHolder(View inflate) {
            super(inflate);
            binding = ItemPkInviteHostBinding.bind(itemView);

        }

        public void setData(int position) {
            PkAudioLiveUserRoot.UsersItem userDummy = userDummies.get(position);
            binding.tvName.setText(userDummy.getName());
            binding.tvCountry.setText(userDummy.getCountry());
            binding.imageUser.setUserImage(userDummy.getImage(), userDummy.getAvatarFrameImage(), 15);
//              binding.tvViewCount.setText(String.valueOf(userDummy.getView()));
            binding.getRoot().setOnClickListener(v -> onHostClickLister.onHostItemClick(userDummy, null, binding));
        }
    }

    public class PartyListViewHolder extends RecyclerView.ViewHolder {
        ItemPartyBinding binding;

        public PartyListViewHolder(View itemView) {
            super(itemView);
            binding = ItemPartyBinding.bind(itemView);
        }

        public void setData(int position) {
            PkAudioLiveUserRoot.UsersItem userDummy = userDummies.get(position);
            binding.tvPartyTitle.setText((userDummy.getRoomName() == null) ? userDummy.getName() : userDummy.getRoomName());

            int colorIndex = position % colors.length;
            binding.layMain.setCardBackgroundColor(ContextCompat.getColor(context, colors[colorIndex]));

            if (userDummy.getPrivateCode() == 0) {
                binding.ivLock.setImageResource(R.drawable.ic_public);
                binding.tvPublic.setText(R.string.public_text);
            } else {
                binding.ivLock.setImageResource(R.drawable.ic_private);
                binding.tvPublic.setText(R.string.private_text);
            }

            Glide.with(context).load((userDummy.getRoomImage() == null) ? userDummy.getImage() : userDummy.getRoomImage())
                    .apply(MainApplication.requestOptionsLive)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop().into(binding.ivPartyImage);

            binding.tvViewCount.setText(String.valueOf(userDummy.getView()));
            binding.tvPartyDescription.setText((userDummy.getRoomWelcome() == null) ? context.getString(R.string.welcome_to_the_party) : userDummy.getRoomWelcome());
            binding.getRoot().setOnClickListener(v -> onHostClickLister.onHostItemClick(userDummy, null, null));
        }
    }

}
