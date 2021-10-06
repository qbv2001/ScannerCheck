package com.example.scannercheck;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailMathang extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_mathang);

        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            return;
        }
        Mathang mathang = (Mathang) bundle.get("object_mathang");
        TextView tvTenMH = findViewById(R.id.tvTenMH);
        TextView tvTenMH2 = findViewById(R.id.tvTenMH2);
        TextView tvTenNCC = findViewById(R.id.tvTenNCC);
        TextView tvDonvitinhMH = findViewById(R.id.tvDonvitinhMH);
        TextView tvSoluongMH = findViewById(R.id.tvSoluongMH);
        TextView tvDongiaMH = findViewById(R.id.tvDongiaMH);
        TextView tvMotaMH = findViewById(R.id.tvMotaMH);


        tvTenMH.setText(mathang.getName());
        tvTenMH2.setText(mathang.getName());

    }
}