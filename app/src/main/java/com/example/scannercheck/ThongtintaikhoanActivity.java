package com.example.scannercheck;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class ThongtintaikhoanActivity extends AppCompatActivity {
    private CircleImageView imgProfile;
    private TextView tvName1,tvName2,tvEmail;
    private TextView suathongtin,doimatkhau;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thongtintaikhoan);

        initUi();
        setUserInfo();

    }

    private void setUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        if (name == null){
            tvName1.setVisibility(View.GONE);
            tvName2.setVisibility(View.GONE);
        }else{
            tvName1.setVisibility(View.VISIBLE);
            tvName2.setVisibility(View.VISIBLE);
        }

        tvEmail.setText(email);
        Glide.with(this).load(photoUrl).error(R.drawable.profilepic).into(imgProfile);


    }

    private void initUi(){
        imgProfile = findViewById(R.id.imgProfile);
        tvName1 = findViewById(R.id.tvName1);
        tvName2 = findViewById(R.id.tvName2);
        tvEmail = findViewById(R.id.tvEmail);
        suathongtin = findViewById(R.id.suathongtin);
        doimatkhau = findViewById(R.id.doimatkhau);

    }


}