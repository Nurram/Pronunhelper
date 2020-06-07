package com.nurram.project.imagetextrecognition;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Intent intent = getIntent();
        int skor = intent.getIntExtra("key", 0);
        ArrayList<String> jawabanList = intent.getStringArrayListExtra("key2");

        TextView mView = findViewById(R.id.skor);
        mView.setText(String.valueOf(skor));

        TextView satu = findViewById(R.id.skor_satu);
        satu.append(jawabanList.get(0));
        setGambar(jawabanList.get(0), satu);

        TextView dua = findViewById(R.id.skor_dua);
        dua.append(jawabanList.get(1));
        setGambar(jawabanList.get(1), dua);

        TextView tiga = findViewById(R.id.skor_tiga);
        tiga.append(jawabanList.get(2));
        setGambar(jawabanList.get(2), tiga);

        TextView empat = findViewById(R.id.skor_empat);
        empat.append(jawabanList.get(3));
        setGambar(jawabanList.get(3), empat);

        TextView lima = findViewById(R.id.skor_lima);
        lima.append(jawabanList.get(4));
        setGambar(jawabanList.get(4), lima);

        TextView enam = findViewById(R.id.skor_enam);
        enam.append(jawabanList.get(5));
        setGambar(jawabanList.get(5), enam);

        TextView tujuh = findViewById(R.id.skor_tujuh);
        tujuh.append(jawabanList.get(6));
        setGambar(jawabanList.get(6), tujuh);

        TextView delapan = findViewById(R.id.skor_delapan);
        delapan.append(jawabanList.get(7));
        setGambar(jawabanList.get(7), delapan);

        TextView sembilan = findViewById(R.id.skor_sembilan);
        sembilan.append(jawabanList.get(8));
        setGambar(jawabanList.get(8), sembilan);

        TextView sepuluh = findViewById(R.id.skor_sepuluh);
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
