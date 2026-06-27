package com.example.rayzi.demoreels;

import android.graphics.Rect;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VideoAutoPlayHelper {

    private static final String TAG = "VideoAutoPlayHelper";
    private RecyclerView recyclerView;
    private InstaLikePlayerView lastPlayerView;
    private int minVisibilityPercentage = 20;
    private int currentPlayingVideoItemPos = -1;

    public VideoAutoPlayHelper(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void startObserving() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                VideoAutoPlayHelper.this.onScrolled(false);
            }
        });
    }

    public void onScrolled(boolean forHorizontalScroll) {
        int firstVisiblePosition = findFirstVisibleItemPosition();
        int lastVisiblePosition = findLastVisibleItemPosition();
        int pos = getMostVisibleItem(firstVisiblePosition, lastVisiblePosition);

        if (pos == -1) {
            if (currentPlayingVideoItemPos != -1) {
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(currentPlayingVideoItemPos);
                int currentVisibility = getVisiblePercentage(viewHolder);
                if (currentVisibility < minVisibilityPercentage) {
                    if (lastPlayerView != null) {
                        lastPlayerView.removePlayer();
                    }
                }
                currentPlayingVideoItemPos = -1;
            }
        } else {
            if (forHorizontalScroll || currentPlayingVideoItemPos != pos) {
                currentPlayingVideoItemPos = pos;
                attachVideoPlayerAt(pos);
            }
        }
    }

    private void attachVideoPlayerAt(int pos) {
        if (recyclerView.getAdapter() instanceof DemoReelsAdapter) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
            DemoViewHolder itemViewHolder = (DemoViewHolder) recyclerView.findViewHolderForAdapterPosition(firstVisiblePosition);

            if (itemViewHolder != null) {
                if (lastPlayerView == null || lastPlayerView != itemViewHolder.recyclerViewHorizontal) {
                    Log.d(TAG, "attachVideoPlayerAt: ");
                    itemViewHolder.recyclerViewHorizontal.startPlaying();
                    if (lastPlayerView != null) {
                        lastPlayerView.removePlayer();
                    }
                }
                lastPlayerView = itemViewHolder.recyclerViewHorizontal;
            } else {
                if (lastPlayerView != null) {
                    lastPlayerView.removePlayer();
                    lastPlayerView = null;
                }
            }
        }
    }

    private int getMostVisibleItem(int firstVisiblePosition, int lastVisiblePosition) {
        int maxPercentage = -1;
        int pos = 0;

        for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);

            if (viewHolder != null) {
                int currentPercentage = getVisiblePercentage(viewHolder);
                if (currentPercentage > maxPercentage) {
                    maxPercentage = currentPercentage;
                    pos = i;
                }
            }
        }

        if (maxPercentage == -1 || maxPercentage < minVisibilityPercentage) {
            return -1;
        }
        return pos;
    }

    private int getVisiblePercentage(RecyclerView.ViewHolder holder) {
        Rect rectParent = new Rect();
        recyclerView.getGlobalVisibleRect(rectParent);
        int[] location = new int[2];
        holder.itemView.getLocationOnScreen(location);

        Rect rectChild = new Rect(
                location[0],
                location[1],
                location[0] + holder.itemView.getWidth(),
                location[1] + holder.itemView.getHeight()
        );

        float rectParentArea = (rectChild.right - rectChild.left) * (rectChild.bottom - rectChild.top);
        float xOverlap = Math.max(0, Math.min(rectChild.right, rectParent.right) - Math.max(rectChild.left, rectParent.left));
        float yOverlap = Math.max(0, Math.min(rectChild.bottom, rectParent.bottom) - Math.max(rectChild.top, rectParent.top));
        float overlapArea = xOverlap * yOverlap;

        return (int) (overlapArea / rectParentArea * 100.0f);
    }

    private int findFirstVisibleItemPosition() {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        }
        return -1;
    }

    private int findLastVisibleItemPosition() {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        }
        return -1;
    }

    public void pause() {
        if (lastPlayerView != null && lastPlayerView.getPlayer() != null) {
            lastPlayerView.getPlayer().pause();
        }
    }

    public void play() {
        if (lastPlayerView != null && lastPlayerView.getPlayer() != null) {
            lastPlayerView.getPlayer().play();
        }
    }
}
