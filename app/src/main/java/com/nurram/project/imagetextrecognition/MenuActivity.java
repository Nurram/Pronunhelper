package com.nurram.project.imagetextrecognition;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {
    private Button mCaptueImage;
    private Button mInputText;
    private Button mExit;
    private Button mKuis;
    private MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mCaptueImage = findViewById(R.id.captured_image_menu);
        mCaptueImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer = MediaPlayer.create(MenuActivity.this, R.raw.button);
                mPlayer.start();
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        mInputText = findViewById(R.id.input_text_menu);
        mInputText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer = MediaPlayer.create(MenuActivity.this, R.raw.button);
                mPlayer.start();
                Intent intent = new Intent(MenuActivity.this, TtsActivity.class);
                startActivity(intent);
            }
        });

        mExit = findViewById(R.id.exit_app);
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mKuis = findViewById(R.id.kuis_menu);
        mKuis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer = MediaPlayer.create(MenuActivity.this, R.raw.button);
                mPlayer.start();
                Intent intent = new Intent(MenuActivity.this, KuisActivity.class);
                startActivity(intent);
            }
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
