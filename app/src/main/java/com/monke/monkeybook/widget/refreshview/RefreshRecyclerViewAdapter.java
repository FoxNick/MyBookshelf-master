package com.monke.monkeybook.widget.refreshview;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monke.monkeybook.R;
import com.victor.loading.rotate.RotateLoading;

public abstract class RefreshRecyclerViewAdapter extends RecyclerView.Adapter {
    private final int LOAD_MORE_TYPE = 2001;

    private Handler handler;
    private int isRequesting = 0;   //0是未执行网络请求  1是正在下拉刷新  2是正在加载更多
    private Boolean needLoadMore = false;
    private Boolean isAll = false;  //判断是否还有更多
    private Boolean loadMoreError = false;

    private OnClickTryAgainListener clickTryAgainListener;

    public interface OnClickTryAgainListener {
        void loadMoreErrorTryAgain();
    }

    public RefreshRecyclerViewAdapter(Boolean needLoadMore) {
        this.needLoadMore = needLoadMore;
        handler = new Handler();
    }

    public int getIsRequesting() {
        return isRequesting;
    }

    public void setIsRequesting(int isRequesting, Boolean needNoti) {
        this.isRequesting = isRequesting;
        if (this.isRequesting == 1) {
            isAll = false;
        }
        if (needNoti) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                notifyItemRangeChanged(getItemCount(), getItemCount() - getICount());
            } else {
                handler.post(this::notifyDataSetChanged);
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == LOAD_MORE_TYPE) {
            return new LoadMoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_refresh_load_more, parent, false));
        } else
            return onCreateIViewHolder(parent, viewType);
    }

    public abstract RecyclerView.ViewHolder onCreateIViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == LOAD_MORE_TYPE) {
            LoadMoreViewHolder loadHolder = (LoadMoreViewHolder) holder;
            if (!loadMoreError) {
                loadHolder.progressBar.setVisibility(View.VISIBLE);
                loadHolder.progressBar.start();
                loadHolder.tvLoadMore.setText("正在加载...");
            } else {
                loadHolder.progressBar.setVisibility(View.GONE);
                loadHolder.progressBar.stop();
                loadHolder.tvLoadMore.setText("加载失败,点击重试");
            }
            ((LoadMoreViewHolder) holder).llLoadMore.setOnClickListener(v -> {
                if (null != clickTryAgainListener && loadMoreError) {
                    clickTryAgainListener.loadMoreErrorTryAgain();
                    loadMoreError = false;
                    ((LoadMoreViewHolder) holder).tvLoadMore.setText("正在加载...");
                }
            });
        } else
            onBindIViewHolder(holder, position);
    }

    public abstract void onBindIViewHolder(RecyclerView.ViewHolder holder, int position);

    @Override
    public int getItemViewType(int position) {
        if (needLoadMore && isRequesting != 1 && !isAll && position == getItemCount() - 1 && getICount() > 5) {
            return LOAD_MORE_TYPE;
        } else {
            return getIViewType(position);
        }
    }

    public abstract int getIViewType(int position);

    @Override
    public int getItemCount() {
        if (needLoadMore && isRequesting != 1 && !isAll && getICount() > 5) {
            return getICount() + 1;
        } else
            return getICount();
    }

    public abstract int getICount();

    public void setIsAll(Boolean isAll, Boolean needNoti) {
        this.isAll = isAll;
        if (needNoti) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                if (getItemCount() > getICount()) {
                    notifyItemRangeChanged(getItemCount(), getItemCount() - getICount());
                } else
                    notifyItemRemoved(getItemCount() + 1);
            } else {
                handler.post(this::notifyDataSetChanged);
            }
        }
    }

    class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        View llLoadMore;
        TextView tvLoadMore;
        RotateLoading progressBar;

        public LoadMoreViewHolder(View itemView) {
            super(itemView);
            llLoadMore = itemView.findViewById(R.id.ll_loadmore);
            tvLoadMore = itemView.findViewById(R.id.tv_loadmore);
            progressBar = itemView.findViewById(R.id.rl_loading);
        }
    }

    public Boolean canLoadMore() {
        return needLoadMore && isRequesting == 0 && !isAll && getICount() > 0;
    }

    public OnClickTryAgainListener getClickTryAgainListener() {
        return clickTryAgainListener;
    }

    public void setClickTryAgainListener(OnClickTryAgainListener clickTryAgainListener) {
        this.clickTryAgainListener = clickTryAgainListener;

    }

    public Boolean getLoadMoreError() {
        return loadMoreError;
    }

    public void setLoadMoreError(Boolean loadMoreError, Boolean needNoti) {
        this.isRequesting = 0;
        this.loadMoreError = loadMoreError;
        if (needNoti) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                notifyDataSetChanged();
            } else {
                handler.post(this::notifyDataSetChanged);
            }
        }
    }
}
