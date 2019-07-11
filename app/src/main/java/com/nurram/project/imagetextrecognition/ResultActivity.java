package com.nurram.project.imagetextrecognition;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.detectlanguage.errors.APIError;
import com.nurram.project.imagetextrecognition.room.Word;
import com.nurram.project.imagetextrecognition.room.WordRepository;
import com.nurram.project.imagetextrecognition.util.ClickUtil;
import com.nurram.project.imagetextrecognition.util.LangDetect;
import com.nurram.project.imagetextrecognition.util.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity implements ClickUtil, TextToSpeech.OnInitListener {

    private ArrayList<String> mBlocks;
    private TextToSpeech mToSpeech;
    private Switch cekBahasa;
    private WordRepository mRepo;
    private List<Word> mWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        if (savedInstanceState != null) {
            mBlocks = savedInstanceState.getStringArrayList("blockBack");
        } else {
            Intent intent = getIntent();
            mBlocks = intent.getStringArrayListExtra("block");
        }

        mRepo = new WordRepository(getApplication());
        mRepo.getAllWords().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(@Nullable List<Word> words) {
                mWords = words;
            }
        });

        cekBahasa = findViewById(R.id.switch1);
        Toolbar toolbar = findViewById(R.id.result_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hasil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button pengecualianButton = findViewById(R.id.result_cek_saved);
        pengecualianButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, SavedListActivity.class);
                startActivity(intent);
            }
        });

        RecyclerAdapter adapter = new RecyclerAdapter(this, mBlocks, this);
        mToSpeech = new TextToSpeech(this, this);
        RecyclerView recyclerView = findViewById(R.id.textRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("blockBack", mBlocks);
    }

    @Override
    public void listClicked(View view, String words, ImageView play) {
        String text = words.toLowerCase();
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
                new ResultAsync().execute(text.toLowerCase());
            }

        } else {
            speak(text.toLowerCase());
        }
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
            Toast.makeText(this, "Initiazion failed", Toast.LENGTH_SHORT).show();
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

    private void speak(String text) {
        if (mToSpeech.isSpeaking()) {
            mToSpeech.stop();
            mToSpeech.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null);
        } else {
            mToSpeech.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, ResultActivity.class);
    }

    private class ResultAsync extends AsyncTask<String, Void, String> {

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
                Toast.makeText(ResultActivity.this, "Periksa koneksi internet kamu", Toast.LENGTH_SHORT).show();
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
