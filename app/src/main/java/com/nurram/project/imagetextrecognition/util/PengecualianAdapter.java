package com.nurram.project.imagetextrecognition.util;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nurram.project.imagetextrecognition.R;
import com.nurram.project.imagetextrecognition.room.Word;

import java.util.ArrayList;
import java.util.List;

public class PengecualianAdapter extends RecyclerView.Adapter<PengecualianAdapter.PengecualianHolder>{
    private List<Word> mWords = new ArrayList<>();
    private Context mContext;
    private SavedClickUtil mClick;

    public PengecualianAdapter(Context context, SavedClickUtil clickUtil){
        mContext = context;
        mClick = clickUtil;
    }

    @NonNull
    @Override
    public PengecualianHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PengecualianHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.pengecualian_item__list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PengecualianHolder pengecualianHolder, int i) {
        pengecualianHolder.bind(mWords.get(i));
    }

    @Override
    public int getItemCount() {
        return mWords.size();
    }

    class PengecualianHolder extends RecyclerView.ViewHolder{
        private TextView mKata;
        private ImageView mHapus;

        PengecualianHolder(@NonNull View itemView) {
            super(itemView);
            mKata = itemView.findViewById(R.id.kata_pengecualian);
            mHapus = itemView.findViewById(R.id.hapus_pengecualian);
        }

        void bind(final Word word){
            mKata.setText(word.getWord());

            mHapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClick.listClicked(v, word);
                }
            });
        }
    }

    public void addData(List<Word> words){
        mWords = words;
    }
}
