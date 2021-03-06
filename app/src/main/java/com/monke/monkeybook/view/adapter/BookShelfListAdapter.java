//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.monke.monkeybook.view.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.monke.monkeybook.R;
import com.monke.monkeybook.bean.BookShelfBean;
import com.monke.monkeybook.model.annotation.BookType;
import com.monke.monkeybook.view.adapter.base.BaseBookListAdapter;
import com.monke.monkeybook.widget.BadgeView;
import com.monke.monkeybook.widget.RotateLoading;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BookShelfListAdapter extends BaseBookListAdapter<BookShelfListAdapter.MyViewHolder> {

    public BookShelfListAdapter(Context context, int group, int bookPx) {
        super(context, group, bookPx);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookshelf_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull List<Object> payloads) {
        final BookShelfBean item = getItem(holder.getLayoutPosition());
        Glide.with(getContext()).load(item.getBookInfoBean().getRealCoverUrl())
                .apply(new RequestOptions().dontAnimate()
                        .centerCrop().placeholder(R.drawable.img_cover_default)
                        .error(R.drawable.img_cover_default))
                .into(holder.ivCover);

        holder.tvName.setText(getBookName(item.getBookInfoBean().getName(), item.getNewChapters()));

        if (!TextUtils.isEmpty(item.getBookInfoBean().getAuthor())) {
            holder.tvAuthor.setText(item.getBookInfoBean().getAuthor());
        } else {
            holder.tvAuthor.setText(R.string.author_unknown);
        }

        String durChapterName = item.getDisplayDurChapterName();
        if (TextUtils.isEmpty(durChapterName)) {
            String bookType = item.getBookInfoBean().getBookType();
            holder.tvRead.setText(getContext().getString(TextUtils.equals(bookType, BookType.AUDIO) ?
                    R.string.play_dur_progress : R.string.read_dur_progress, getContext().getString(R.string.text_placeholder)));
        } else {
            holder.tvRead.setText(durChapterName);
        }
        String lastChapterName = item.getDisplayLastChapterName();
        if (TextUtils.isEmpty(lastChapterName)) {
            holder.tvLast.setText(getContext().getString(R.string.book_search_last, getContext().getString(R.string.text_placeholder)));
        } else {
            holder.tvLast.setText(lastChapterName);
        }

        holder.content.setOnClickListener(v -> callOnItemClick(v, item));

        if (getBookshelfPx() == 2) {
            holder.ivCover.setClickable(true);
            holder.ivCover.setOnClickListener(v -> callOnItemLongClick(v, item));
            holder.content.setOnLongClickListener(null);
        } else {
            holder.ivCover.setClickable(false);
            holder.content.setOnLongClickListener(v -> {
                callOnItemLongClick(v, item);
                return true;
            });
        }

        if (item.isFlag()) {
            holder.tvHasNew.setVisibility(View.INVISIBLE);
            holder.rotateLoading.show();
        } else {
            holder.rotateLoading.hide();
            if (item.getHasUpdate()) {
                holder.tvHasNew.setBadgeCount(item.getNewChapters());
            } else {
                holder.tvHasNew.setBadgeCount(item.getUnreadChapterNum());
            }
            holder.tvHasNew.setHighlight(item.getHasUpdate());
        }
    }

    private SpannableStringBuilder getBookName(String name, int newChapters) {
        SpannableStringBuilder sbs = new SpannableStringBuilder(name);
        if (newChapters == 0) {
            return sbs;
        }
        SpannableString chaptersSpan = new SpannableString(String.format(Locale.getDefault(), "(新增%d章)", newChapters));
        chaptersSpan.setSpan(new RelativeSizeSpan(0.75f), 0, chaptersSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        chaptersSpan.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.colorTextSecondary)), 0, chaptersSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //sbs.append(chaptersSpan);
        return sbs;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        BadgeView tvHasNew;
        TextView tvName;
        TextView tvAuthor;
        TextView tvRead;
        TextView tvLast;
        RotateLoading rotateLoading;
        public View content;

        MyViewHolder(View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvHasNew = itemView.findViewById(R.id.tv_has_new);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvRead = itemView.findViewById(R.id.tv_read);
            tvLast = itemView.findViewById(R.id.tv_last);
            rotateLoading = itemView.findViewById(R.id.rl_loading);
            content = itemView.findViewById(R.id.content_card);
        }
    }
}