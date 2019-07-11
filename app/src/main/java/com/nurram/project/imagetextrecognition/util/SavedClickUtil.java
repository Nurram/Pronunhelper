package com.nurram.project.imagetextrecognition.util;

import android.view.View;
import android.widget.ImageView;

import com.nurram.project.imagetextrecognition.room.Word;

public interface SavedClickUtil {
    void listClicked(View view, Word words);
}
