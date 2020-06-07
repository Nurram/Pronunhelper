package com.nurram.project.imagetextrecognition;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class KuisActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private Button mMengerti, mPutar, mCek;
    private TextView mSoal, mMasukanJawaban;
    private EditText mJawaban;
    private TextToSpeech mToSpeech;
    private String[] soal;
    private ArrayList<String> mListJawaban;
    private int index = 0;
    private int skor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuis);

        Toolbar toolbar = findViewById(R.id.kuis_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        soal = getResources().getStringArray(R.array.soal);
        mListJawaban = new ArrayList<>();
        mJawaban = findViewById(R.id.kuis_jawaban);
        mMasukanJawaban = findViewById(R.id.textjawaban);
        mToSpeech = new TextToSpeech(this, this);

        mPutar = findViewById(R.id.kuis_putar);
        mPutar.setOnClickListener(v -> speak(soal[index % soal.length]));

        mCek = findViewById(R.id.kuis_cek);
        mCek.setOnClickListener(v -> {
            if (index < soal.length) {
                if (!mJawaban.getText().toString().isEmpty()) {
                    cekJawaban(mJawaban.getText().toString());
                    index++;
                    mJawaban.setText(null);

                    if (index == soal.length) {
                        Intent intent = new Intent(KuisActivity.this, ScoreActivity.class);
                        intent.putExtra("key", skor);
                        intent.putExtra("key2", mListJawaban);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    Toast.makeText(getApplication(), "Jawaban kamu masih kosong", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mSoal = findViewById(R.id.textaturan);

        mMengerti = findViewById(R.id.btn_1);
        mMengerti.setOnClickListener(v -> {
            mSoal.setVisibility(View.GONE);
            mMasukanJawaban.setVisibility(View.VISIBLE);
            mJawaban.setVisibility(View.VISIBLE);
            mPutar.setVisibility(View.VISIBLE);
            mCek.setVisibility(View.VISIBLE);
            mMengerti.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mToSpeech.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mToSpeech.shutdown();
    }

    @Override
    public void onInit(int status) {
        initTts(status);
    }

    private void initTts(int status) {
        if (status == android.speech.tts.TextToSpeech.SUCCESS) {
            mToSpeech.setLanguage(Locale.US);
            mToSpeech.setSpeechRate(0.8f);

        } else {
            Log.e("TTS", "Initilization Failed");
            Toast.makeText(this, "Initiazion failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void speak(String mWord) {
        if (mToSpeech.isSpeaking()) {
            mToSpeech.stop();
            mToSpeech.speak(mWord, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null);
        } else {
            mToSpeech.speak(mWord, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void cekJawaban(String jawaban) {
        if (jawaban.toLowerCase().equals(soal[index % soal.length])) {
            skor += 1;
            mListJawaban.add("Benar");

        } else {
            mListJawaban.add("Salah");

        }
    }
}
