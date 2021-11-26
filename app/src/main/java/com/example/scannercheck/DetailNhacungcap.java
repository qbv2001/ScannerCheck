package com.example.scannercheck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class DetailNhacungcap extends AppCompatActivity {

    TextView tvTenNCC;
    EditText edtTenNCC,edtDiachiNCC,edtSdtNCC,edtMotaNCC;
    Button btnsuaNCC,btnxoaNCC;
    Nhacungcap nhacungcap;

    private DatabaseReference datanhacungcap;
    private FirebaseUser user;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_nhacungcap);

        user = FirebaseAuth.getInstance().getCurrentUser();
        datanhacungcap = FirebaseDatabase.getInstance().getReference();
        initUi();
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            return;
        }
        nhacungcap = (Nhacungcap) bundle.get("object_nhacungcap");
        edtTenNCC.setText(nhacungcap.getName());
        edtDiachiNCC.setText(nhacungcap.getDiachi());
        edtSdtNCC.setText(""+nhacungcap.getSdt());
        edtMotaNCC.setText(""+nhacungcap.getMota());

        onclickUpdateMH();
        onclickDeleteMH();

    }

    private void onclickUpdateMH(){
        btnsuaNCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateData();
            }
        });
    }

    private void UpdateData() {
        String MaNCC = nhacungcap.getId();
        String TenNCC = edtTenNCC.getText().toString().trim();
        String DiachiNCC = edtDiachiNCC.getText().toString().trim();
        String SdtNCC = edtSdtNCC.getText().toString().trim();
        String mota = edtMotaNCC.getText().toString().trim();
        int image = 1;

        Nhacungcap nhacungcap = new Nhacungcap(MaNCC, TenNCC, mota, DiachiNCC, SdtNCC, R.drawable.mon2);

        datanhacungcap.child("NhaCungCap").child(user.getUid()).child(MaNCC).setValue(nhacungcap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(DetailNhacungcap.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void onclickDeleteMH(){
        btnxoaNCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datanhacungcap.child("NhaCungCap").child(user.getUid()).child(nhacungcap.getId()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Intent intent = new Intent(DetailNhacungcap.this,DanhsachnhacungcapActivity.class);
                        finish();
                    }
                });
            }
        });

    }

    private void initUi(){
        btnsuaNCC = findViewById(R.id.suanhacungcap);
        btnxoaNCC = findViewById(R.id.xoanhacungap);

        tvTenNCC = findViewById(R.id.tvTenNCC);
        edtTenNCC = findViewById(R.id.edtTenNCC);
        edtDiachiNCC = findViewById(R.id.edtDiachiNCC);
        edtSdtNCC = findViewById(R.id.edtSdtNCC);
        edtMotaNCC = findViewById(R.id.edtMotaNCC);
    }
}