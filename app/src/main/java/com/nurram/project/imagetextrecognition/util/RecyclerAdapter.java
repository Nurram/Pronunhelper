package com.nurram.project.imagetextrecognition.util;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nurram.project.imagetextrecognition.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> {
    private Context mContext;
    private ArrayList<String> mBlocks;
    private ClickUtil mClickUtil;

    public RecyclerAdapter(Context context, ArrayList<String> blocks, ClickUtil clickUtil) {
        mContext = context;
        mBlocks = blocks;
        mClickUtil = clickUtil;
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_item, parent, false);
        return new RecyclerHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mBlocks.size();
    }

    class RecyclerHolder extends RecyclerView.ViewHolder {
        private TextView mTextLines;
        private ImageView mPlay;

        private RecyclerHolder(View itemView) {
            super(itemView);

            mTextLines = itemView.findViewById(R.id.textLines);
            mPlay = itemView.findViewById(R.id.play_image);
        }

        private void bind(int position) {
            mTextLines.append(mBlocks.get(position));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = mTextLines.getText().toString();
                    mClickUtil.listClicked(v, text, mPlay);
                }
            });
        }
    }
}