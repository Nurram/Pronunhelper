package com.nurram.project.imagetextrecognition;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {
    private MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button mCaptueImage = findViewById(R.id.captured_image_menu);
        mCaptueImage.setOnClickListener(v -> {
            mPlayer = MediaPlayer.create(MenuActivity.this, R.raw.button);
            mPlayer.start();
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(intent);
        });

        Button mInputText = findViewById(R.id.input_text_menu);
        mInputText.setOnClickListener(v -> {
            mPlayer = MediaPlayer.create(MenuActivity.this, R.raw.button);
            mPlayer.start();
            Intent intent = new Intent(MenuActivity.this, TtsActivity.class);
            startActivity(intent);
        });

        Button mExit = findViewById(R.id.exit_app);
        mExit.setOnClickListener(v -> finish());

        Button mKuis = findViewById(R.id.kuis_menu);
        mKuis.setOnClickListener(v -> {
            mPlayer = MediaPlayer.create(MenuActivity.this, R.raw.button);
            mPlayer.start();
            Intent intent = new Intent(MenuActivity.this, KuisActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }
}
