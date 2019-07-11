package com.nurram.project.imagetextrecognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private CameraView mCameraView;
    private ImageButton mCameraButton;
    private ProgressBar mCameraProgress;
    private ImageView mImageView;
    private RelativeLayout mCapturedLayout;
    private FloatingActionButton mProcessImage;

    private boolean isCapturedShow = false;
    private FirebaseVisionImage mImage;
    private ArrayList<String> mBlockList;
    private FirebaseVisionTextRecognizer mRecognizer;
    private Intent mIntent;
    private MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraProgress = findViewById(R.id.progress);
        mCapturedLayout = findViewById(R.id.captured_image);
        mImageView = findViewById(R.id.image);
        mBlockList = new ArrayList<>();

        mCameraView = findViewById(R.id.camera_layout);
        mCameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {}

            @Override
            public void onError(CameraKitError cameraKitError) {}

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createBitmap(bitmap);

                mImage = FirebaseVisionImage.fromBitmap(bitmap);

                mCapturedLayout.setVisibility(View.VISIBLE);
                mCameraProgress.setVisibility(View.GONE);
                isCapturedShow = true;

                mImageView.setImageBitmap(mImage.getBitmapForDebugging());
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {}
        });

        mCameraButton = findViewById(R.id.button_camera);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Tahan sampai proses selesai", Toast.LENGTH_SHORT).show();
                mCameraButton.setVisibility(View.GONE);
                mCameraProgress.setVisibility(View.VISIBLE);

                mCameraView.captureImage();

                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.camera);
                mPlayer.start();

            }
        });

        mProcessImage = findViewById(R.id.process_fab);
        mProcessImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcessImage.setClickable(false);
                processTextRecog(mImage);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.start();
        mCameraButton.setVisibility(View.VISIBLE);
        mCameraProgress.setVisibility(View.GONE);
        mProcessImage.setClickable(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCameraView.stop();

        if (mPlayer != null) {
            mPlayer.stop();
        }

    }

    @Override
    public void onBackPressed() {
        if (isCapturedShow) {
            mCapturedLayout.setVisibility(View.GONE);
            mCameraProgress.setVisibility(View.GONE);
            mCameraButton.setVisibility(View.VISIBLE);
            isCapturedShow = false;
        } else {
            super.onBackPressed();
        }
    }

    private void processTextRecog(FirebaseVisionImage image) {
        mBlockList.clear();

        mRecognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        mRecognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText result) {
                        mIntent = ResultActivity.getIntent(MainActivity.this);
                        mCapturedLayout.setVisibility(View.GONE);

                        for (FirebaseVisionText.TextBlock block : result.getTextBlocks()) {
                            for (FirebaseVisionText.Line line : block.getLines()) {
                                String lineText = line.getText();
                                mBlockList.add(lineText);
                            }
                        }

                        mIntent.putStringArrayListExtra("block", mBlockList);

                        startActivity(mIntent);
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAG", e.getMessage());
                            }
                        });
    }
}
