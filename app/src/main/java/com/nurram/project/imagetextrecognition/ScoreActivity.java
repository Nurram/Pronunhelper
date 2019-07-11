package com.nurram.project.imagetextrecognition;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity {
    private TextView mView;
    private TextView satu, dua, tiga, empat, lima, enam, tujuh, delapan, sembilan, sepuluh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Intent intent = getIntent();
        int skor = intent.getIntExtra("key", 0);
        ArrayList<String> jawabanList = intent.getStringArrayListExtra("key2");

        mView = findViewById(R.id.skor);
        mView.setText(String.valueOf(skor));

        satu = findViewById(R.id.skor_satu);
        satu.append(jawabanList.get(0));
        setGambar(jawabanList.get(0), satu);

        dua = findViewById(R.id.skor_dua);
        dua.append(jawabanList.get(1));
        setGambar(jawabanList.get(1), dua);

        tiga = findViewById(R.id.skor_tiga);
        tiga.append(jawabanList.get(2));
        setGambar(jawabanList.get(2), tiga);

        empat = findViewById(R.id.skor_empat);
        empat.append(jawabanList.get(3));
        setGambar(jawabanList.get(3), empat);

        lima = findViewById(R.id.skor_lima);
        lima.append(jawabanList.get(4));
        setGambar(jawabanList.get(4), lima);

        enam = findViewById(R.id.skor_enam);
        enam.append(jawabanList.get(5));
        setGambar(jawabanList.get(5), enam);

        tujuh = findViewById(R.id.skor_tujuh);
        tujuh.append(jawabanList.get(6));
        setGambar(jawabanList.get(6), tujuh);

        delapan = findViewById(R.id.skor_delapan);
        delapan.append(jawabanList.get(7));
        setGambar(jawabanList.get(7), delapan);

        sembilan = findViewById(R.id.skor_sembilan);
        sembilan.append(jawabanList.get(8));
        setGambar(jawabanList.get(8), sembilan);

        sepuluh = findViewById(R.id.skor_sepuluh);
        sepuluh.append(jawabanList.get(9));
        setGambar(jawabanList.get(9), sepuluh);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setGambar(String benarkah, TextView view) {
        Drawable drawable = this.getResources().getDrawable(R.drawable.ic_salah);
        if (benarkah.equals("Salah")) {
            view.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        }
    }
}
