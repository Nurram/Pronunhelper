package com.nurram.project.imagetextrecognition;

import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.detectlanguage.errors.APIError;
import com.nurram.project.imagetextrecognition.room.Word;
import com.nurram.project.imagetextrecognition.room.WordRepository;
import com.nurram.project.imagetextrecognition.util.LangDetect;

import java.util.List;
import java.util.Locale;

public class TtsActivity extends AppCompatActivity implements android.speech.tts.TextToSpeech.OnInitListener {

    private TextToSpeech mToSpeech;
    private EditText editText;
    private TextView mPitchVal;
    private TextView mSpeedVal;

    private WordRepository mRepo;
    private List<Word> mWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);

        Toolbar toolbar = findViewById(R.id.tts_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Text-To-Speech");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRepo = new WordRepository(getApplication());
        mRepo.getAllWords().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(@Nullable List<Word> words) {
                mWords = words;
            }
        });

        editText = findViewById(R.id.tts_kata);
        mToSpeech = new android.speech.tts.TextToSpeech(this, this);

        final Switch cekBahasa = findViewById(R.id.switch1);
        Button androTtsButton = findViewById(R.id.android_button);
        androTtsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString().toLowerCase();
                boolean fromDb = false;

                if (cekBahasa.isChecked()) {

                    for (Word word : mWords) {
                        if (word.getWord().equals(text)) {
                            fromDb = true;
                            speak(text.toLowerCase());
                            break;
                        }
                    }

                    if (!fromDb) {
                        new TtsAsync().execute(text.toLowerCase());
                    }
                } else {
                    speak(text.toLowerCase());
                }
            }
        });

        Button pengecualianButton = findViewById(R.id.tts_daftar_peng);
        pengecualianButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TtsActivity.this, SavedListActivity.class);
                startActivity(intent);
            }
        });

        mPitchVal = findViewById(R.id.pitch_value);
        SeekBar pitchBar = findViewById(R.id.pitch_seekbar);
        pitchBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float convertedProgress = convertProgress(progress);
                mToSpeech.setPitch(convertedProgress);
                mPitchVal.setText(String.valueOf(convertedProgress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mSpeedVal = findViewById(R.id.speed_value);
        SeekBar speedBar = findViewById(R.id.speed_seekbar);
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float convertedProgress = convertProgress(progress);
                mToSpeech.setSpeechRate(convertedProgress);
                mSpeedVal.setText(String.valueOf(convertProgress(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
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
        if (status == android.speech.tts.TextToSpeech.SUCCESS) {

            mToSpeech.setSpeechRate(0.8f);
            mToSpeech.setLanguage(Locale.US);
        } else {
            Toast.makeText(this, "Initiazion failed", Toast.LENGTH_SHORT).show();
        }

    }

    private void speak(String text) {
        if (mToSpeech.isSpeaking()) {
            mToSpeech.stop();
            mToSpeech.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null);
        } else {
            mToSpeech.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private String checkAndSpeak(String text) throws APIError {
        try {
            String detect = LangDetect.getInstance().detectLanguage(text);

            if (detect.equals("")) {
                return text;
            } else {
                speak(text);
                return "";
            }
        } catch (Exception e) {
            return "err";
        }
    }
    private float convertProgress(int progressInt) {
        return .5f * progressInt;
    }

    private class TtsAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                return checkAndSpeak(params[0]);
            } catch (APIError apiError) {
                return apiError.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("err")) {
                Toast.makeText(TtsActivity.this, "Periksa koneksi internet kamu", Toast.LENGTH_SHORT).show();
            } else if (!s.equals("")) {
                showDialog(s);
            }
        }
    }

    private void showDialog(final String text) {
        AlertDialog.Builder builder;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }

        builder.setTitle("Kata terdeteksi bukan bahasa inggris")
                .setMessage("Masukan '" + text + "' pada pengecualian?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Word word = new Word(text);
                        mRepo.insert(word);
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

}