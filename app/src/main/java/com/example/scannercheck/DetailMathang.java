package com.example.scannercheck;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class DetailMathang extends AppCompatActivity {
    TextView tvTenMH;
    EditText edtTenMH,edtTenNCC,edtDonvitinhMH,edtSoluongMH,edtDongiaMH,edtMotaMH;
    Button btnsuaMH,btnxoaMH;
    Mathang mathang;

    private DatabaseReference datamathang;
    private FirebaseUser user;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_mathang);

        user = FirebaseAuth.getInstance().getCurrentUser();
        datamathang = FirebaseDatabase.getInstance().getReference();
        initUi();
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            return;
        }
        mathang = (Mathang) bundle.get("object_mathang");
        tvTenMH.setText(mathang.getName());
        edtTenMH.setText(mathang.getName());
        edtTenNCC.setText(mathang.getNhacc());
        edtDonvitinhMH.setText(mathang.getDvt());
        edtSoluongMH.setText(""+mathang.getSoluong());
        edtDongiaMH.setText(""+mathang.getDongia());
        edtMotaMH.setText(mathang.getMota());

        onclickUpdateMH();
        onclickDeleteMH();

    }

    private void onclickUpdateMH(){
        btnsuaMH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateData();
            }
        });
    }

    private void UpdateData() {
        String MaMH = mathang.getId();
        String TenMH = edtTenMH.getText().toString().trim();
        String DonvitinhMH = edtDonvitinhMH.getText().toString().trim();
        float DongiaMH = Float.parseFloat(edtDongiaMH.getText().toString().trim());
        int SoluongMH = Integer.parseInt(edtSoluongMH.getText().toString().trim());
        String NhaccMH = edtTenNCC.getText().toString().trim();
        Date now = new Date();
        String datetime = ""+now;
        String mota = edtMotaMH.getText().toString().trim();
        int image = 1;

        Mathang mathang = new Mathang(MaMH, TenMH, SoluongMH, DongiaMH, datetime, R.drawable.mon1, DonvitinhMH, NhaccMH, mota);

        datamathang.child("MatHang").child(user.getUid()).child(MaMH).setValue(mathang, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(DetailMathang.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void onclickDeleteMH(){
        btnxoaMH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datamathang.child("MatHang").child(user.getUid()).child(mathang.getId()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Intent intent = new Intent(DetailMathang.this,DanhsachmathangActivity.class);
                        finish();
                    }
                });
            }
        });

    }

    private void initUi(){
        btnsuaMH = findViewById(R.id.suamathang);
        btnxoaMH = findViewById(R.id.xoamathang);

        tvTenMH = findViewById(R.id.tvTenMH);
        edtTenMH = findViewById(R.id.edtTenMH);
        edtTenNCC = findViewById(R.id.edtTenNCC);
        edtDonvitinhMH = findViewById(R.id.edtDonvitinhMH);
        edtSoluongMH = findViewById(R.id.edtSoluongMH);
        edtDongiaMH = findViewById(R.id.edtDongiaMH);
        edtMotaMH = findViewById(R.id.edtMotaMH);
    }
}