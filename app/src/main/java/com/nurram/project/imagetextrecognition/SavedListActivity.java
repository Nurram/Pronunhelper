package com.nurram.project.imagetextrecognition;

import androidx.lifecycle.Observer;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.nurram.project.imagetextrecognition.room.Word;
import com.nurram.project.imagetextrecognition.room.WordRepository;
import com.nurram.project.imagetextrecognition.util.PengecualianAdapter;
import com.nurram.project.imagetextrecognition.util.SavedClickUtil;

import java.util.List;

public class SavedListActivity extends AppCompatActivity implements SavedClickUtil {
    private WordRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_list);

        Toolbar toolbar = findViewById(R.id.pengecualian_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daftar Pengecualian");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final RecyclerView view = findViewById(R.id.list_pengecualian);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        final PengecualianAdapter adapter = new PengecualianAdapter(this, this);
        repository = new WordRepository(getApplication());
        repository.getAllWords().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(@Nullable List<Word> words) {
                adapter.addData(words);
                view.setAdapter(adapter);
            }
        });
    }

    @Override
    public void listClicked(View view, Word words) {
        repository.delete(words);
        Toast.makeText(this, words.getWord() + " deleted", Toast.LENGTH_SHORT).show();
    }
}
