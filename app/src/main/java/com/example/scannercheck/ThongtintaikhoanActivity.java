package com.example.scannercheck;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ThongtintaikhoanActivity extends AppCompatActivity {
    public  static final int MY_REQUEST_CODE = 10;
    private CircleImageView imgProfile;
    private EditText edtName;
    private TextView tvEmail,suathongtin,doimatkhau;
    private Uri uri;

    private CircleImageView imgprofilepic;
    private TextView tvnameBar;
    private TextView tvuseremail;

    ProgressDialog progressDialog;
    NavigationView mNavigationView;

    final private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK){
                Intent intent = result.getData();
                if (intent == null){
                    return;
                }
                uri = intent.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                    setBitmapImageView(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thongtintaikhoan);

        initUi();
        progressDialog = new ProgressDialog(this);
        setUserInfo();
        // sidebar
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerLayout.openDrawer(GravityCompat.START);

            }
        });

        showUserInfo();

        mNavigationView.setNavigationItemSelectedListener(item -> {
            Intent i1;
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);
            switch (id)
            {
                case R.id.nav_home:
                    i1 = new Intent(this, HomeActivity.class);startActivity(i1);
                    break;
                case R.id.nav_dsmh:
                    i1 = new Intent(this, DanhsachmathangActivity.class);startActivity(i1);
                    break;
                case R.id.nav_dsncc:
                    i1 = new Intent(this, DanhsachnhacungcapActivity.class);startActivity(i1);
                    break;
                case R.id.nav_thongke:
                    i1 = new Intent(this, ThongkeActivity.class);startActivity(i1);
                    break;
                case R.id.nav_logout:
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(ThongtintaikhoanActivity.this,LoginActivity.class));
                    Toast.makeText(ThongtintaikhoanActivity.this, "Đã đăng xuất",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case R.id.nav_hotro:
                    i1 = new Intent(this, HotroActivity.class);startActivity(i1);
                    break;
                case R.id.nav_ttud:
                    i1 = new Intent(this, ThongtinungdungActivity.class);startActivity(i1);
                    break;
                case R.id.nav_ttcn:
                    i1 = new Intent(this, ThongtintaikhoanActivity.class);startActivity(i1);
                    break;
                default:
                    return true;

            }
            return true;
        });

        initListener();

    }

    private void initListener() {
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclickRequestPermission();
            }
        });

        suathongtin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclickUpdateProfile();
            }
        });
    }

    private void onclickRequestPermission() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            openGallery();
            return;
        }

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            openGallery();
        }else{
            String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission, MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }
    }

    public void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent,"Select Piture"));

    }
    private void setUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        if (name != null) {
            edtName.setText(name);
        }

        tvEmail.setText(email);
        Glide.with(this).load(photoUrl).error(R.drawable.profilepic).into(imgProfile);


    }

    private void initUi(){
        imgProfile = findViewById(R.id.imgProfile);
        tvEmail = findViewById(R.id.tvEmail);
        edtName = findViewById(R.id.edtName);
        suathongtin = findViewById(R.id.suathongtin);
        doimatkhau = findViewById(R.id.doimatkhau);

    }
    public void setBitmapImageView(Bitmap bitmapImageView){
        imgProfile.setImageBitmap(bitmapImageView);
    }

    private void onclickUpdateProfile(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null){
            return;
        }
        progressDialog.show();
        String strName = edtName.getText().toString().trim();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(strName)
                .setPhotoUri(uri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(ThongtintaikhoanActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            showUserInfo();
                        }
                    }
                });
    }

    private void showUserInfo(){
        mNavigationView = findViewById(R.id.navigation_view);
        imgprofilepic = mNavigationView.getHeaderView(0).findViewById(R.id.profilepic);
        tvnameBar = mNavigationView.getHeaderView(0).findViewById(R.id.name);
        tvuseremail = mNavigationView.getHeaderView(0).findViewById(R.id.useremail);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        if (name == null){
            tvnameBar.setVisibility(View.GONE);
        }else{
            tvnameBar.setVisibility(View.VISIBLE);
            tvnameBar.setText(name);
        }

        tvuseremail.setText(email);
        Glide.with(this).load(photoUrl).error(R.drawable.profilepic).into(imgprofilepic);

    }

}