package com.trkpo.ptinder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trkpo.ptinder.R;
import com.trkpo.ptinder.pojo.Feed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FeedCardAdapter extends RecyclerView.Adapter<FeedCardAdapter.ViewHolder> {
    private List<Feed> feeds = new ArrayList<>();

    class ViewHolder extends RecyclerView.ViewHolder {
        private Feed feedInfo;
        private TextView title;
        private TextView author;
        private TextView score;
        private ImageView feedImage;

        public ViewHolder(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.feed_title);
            this.author = itemView.findViewById(R.id.feed_author);
            this.score = itemView.findViewById(R.id.feed_score);
            this.feedImage = itemView.findViewById(R.id.feed_image);
            //itemView.setOnClickListener(this);
        }

        public void bind(Feed feedInfo) {
            this.feedInfo = feedInfo;
            title.setText(feedInfo.getTitle());
            author.setText(feedInfo.getAuthor());
            score.setText(feedInfo.getScore());
            feedImage.setImageBitmap(feedInfo.getContent());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(feeds.get(position));
    }

    @Override
    public int getItemCount() {
        return feeds.size();
    }

    public void setItems(Collection<Feed> pets) {
        feeds.addAll(pets);
        notifyDataSetChanged();
    }

    public void clearItems() {
        feeds.clear();
        notifyDataSetChanged();
    }
}

