package com.ctrlvideo.ivplayerdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ctrlvideo.ivplayer.IVPlayer;

import java.util.List;

public class ListPlayerAdapter extends RecyclerView.Adapter<ListPlayerAdapter.ListPlayerHoldet> {

    private List<String> mData;
    private Context context;

    public ListPlayerAdapter(Context context, List<String> mData) {
        this.mData = mData;
        this.context = context;
    }

    @NonNull
    @Override
    public ListPlayerHoldet onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListPlayerHoldet(LayoutInflater.from(context).inflate(R.layout.item_player, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListPlayerHoldet holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ListPlayerHoldet extends RecyclerView.ViewHolder {

        public IVPlayer videoPlayerView;

        public ListPlayerHoldet(@NonNull View itemView) {
            super(itemView);
//            videoPlayerView =  itemView.findViewById(R.id.video_player_view);

            videoPlayerView=itemView.findViewById(R.id.video_player_view);

        }
    }
}
