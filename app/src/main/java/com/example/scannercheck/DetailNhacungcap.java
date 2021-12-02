package com.example.scannercheck;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailNhacungcap extends AppCompatActivity {
    public static final int ANHNCC_VIEW = 0x23;
    TextView tvTenNCC;
    EditText edtTenNCC,edtDiachiNCC,edtSdtNCC,edtMotaNCC;
    private String tenanh;
    ImageView imgNCC;
    Button btnsuaNCC,btnxoaNCC;
    Nhacungcap nhacungcap;
    ProgressDialog progressDialog;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private DatabaseReference datanhacungcap;
    private FirebaseUser user;
    private DatabaseReference dataUser;

    NavigationView mNavigationView;
    CircleImageView imgprofilepic;
    TextView tvname;
    TextView tvuseremail;

    private Uri uri;
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

    public void setBitmapImageView(Bitmap bitmapImageView){
        imgNCC.setImageBitmap(bitmapImageView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_nhacungcap);

        dataUser = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        datanhacungcap = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        storage = FirebaseStorage.getInstance("gs://scanner-check-27051.appspot.com");
        storageRef = storage.getReference();

        initUi();
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            return;
        }
        nhacungcap = (Nhacungcap) bundle.get("object_nhacungcap");
        tvTenNCC.setText(nhacungcap.getName());
        edtTenNCC.setText(nhacungcap.getName());
        edtDiachiNCC.setText(nhacungcap.getDiachi());
        edtSdtNCC.setText(""+nhacungcap.getSdt());
        edtMotaNCC.setText(""+nhacungcap.getMota());
        Picasso.with(DetailNhacungcap.this).load(nhacungcap.getImage()).into(imgNCC);

        tenanh = nhacungcap.getTenimage();
        showUserInfo();
        readDatabaseUser();

        onclickUpdateAnh();
        onclickUpdateNCC();
        onclickDeleteNCC();


        // sidebar
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerLayout.openDrawer(GravityCompat.START);

            }
        });
        navigationView.setNavigationItemSelectedListener(item -> {
            Intent i1;
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);
            switch (id)
            {
                case R.id.nav_home:
                    i1 = new Intent(this, HomeActivity.class);startActivity(i1);
                    finish();
                    break;
                case R.id.nav_dsmh:
                    i1 = new Intent(this, DanhsachmathangActivity.class);startActivity(i1);
                    finish();
                    break;
                case R.id.nav_dsncc:
                    i1 = new Intent(this, DanhsachnhacungcapActivity.class);startActivity(i1);
                    finish();
                    break;
                case R.id.nav_thongke:
                    i1 = new Intent(this, ThongkeActivity.class);startActivity(i1);
                    finish();
                    break;
                case R.id.nav_logout:
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(DetailNhacungcap.this,LoginActivity.class));
                    Toast.makeText(DetailNhacungcap.this, "Đã đăng xuất",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case R.id.nav_hotro:
                    i1 = new Intent(this, HotroActivity.class);startActivity(i1);
                    finish();
                    break;
                case R.id.nav_ttud:
                    i1 = new Intent(this, ThongtinungdungActivity.class);startActivity(i1);
                    finish();
                    break;
                case R.id.nav_scanner:
                    i1 = new Intent(this, ScannerActivity.class);startActivity(i1);
                    finish();
                    break;
                case R.id.nav_ttcn:
                    i1 = new Intent(this, ThongtintaikhoanActivity.class);startActivity(i1);
                    finish();
                    break;
                default:
                    return true;

            }
            return true;
        });
    }

    private void onclickUpdateNCC(){
        btnsuaNCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateData();
            }
        });
    }

    private void UpdateData() {

        String TenNCC = edtTenNCC.getText().toString().trim();
        String DiachiNCC = edtDiachiNCC.getText().toString().trim();
        String SdtNCC = edtSdtNCC.getText().toString().trim();
        String mota = edtMotaNCC.getText().toString().trim();
        // Check ncc
        if(TenNCC.equalsIgnoreCase("")){
            Toast.makeText(DetailNhacungcap.this, "Vui lòng nhập tên nhà cung cấp", Toast.LENGTH_SHORT).show();
            return;
        }
        if(DiachiNCC.equalsIgnoreCase("")){
            Toast.makeText(DetailNhacungcap.this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
            return;
        }
        if(SdtNCC.equalsIgnoreCase("")){
            Toast.makeText(DetailNhacungcap.this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mota.equalsIgnoreCase("")){
            Toast.makeText(DetailNhacungcap.this, "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        // Get the data from an ImageView as bytes
        imgNCC.setDrawingCacheEnabled(true);
        imgNCC.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imgNCC.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        Calendar calendar = Calendar.getInstance();
        StorageReference mountainsRef = storageRef.child("image"+calendar.getTimeInMillis()+".jpg");

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                progressDialog.dismiss();
                Toast.makeText(DetailNhacungcap.this, "Upload ảnh lỗi", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // Khi upload ảnh thành công
                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        String MaNCC = nhacungcap.getId();

                        Nhacungcap suanhacungcap = new Nhacungcap(MaNCC, TenNCC, mota, DiachiNCC, SdtNCC, imageUrl,"image"+calendar.getTimeInMillis()+".jpg");
                        datanhacungcap.child("NhaCungCap").child(user.getUid()).child(MaNCC).setValue(suanhacungcap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                //Xóa ảnh cũ
                                storageRef.child(tenanh).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        tenanh = suanhacungcap.getTenimage();
                                        tvTenNCC.setText(TenNCC);
                                        progressDialog.dismiss();
                                        Toast.makeText(DetailNhacungcap.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                        // File deleted successfully
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        progressDialog.dismiss();
                                        // Uh-oh, an error occurred!
                                    }
                                });
                            }
                        });

                    }
                });

            }
        });
    }

    private void onclickDeleteNCC(){
        btnxoaNCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                datanhacungcap.child("NhaCungCap").child(user.getUid()).child(nhacungcap.getId()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        // Xóa ảnh trên db
                        storageRef = storage.getReference();
                        StorageReference desertRef = storageRef.child(tenanh);
                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                // File deleted successfully
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                progressDialog.dismiss();
                                // Uh-oh, an error occurred!
                            }
                        });

                        Intent intent = new Intent(DetailNhacungcap.this,DanhsachnhacungcapActivity.class);
                        finish();
                    }
                });
            }
        });

    }

    private void initUi(){
        mNavigationView = findViewById(R.id.navigation_view);
        imgprofilepic = mNavigationView.getHeaderView(0).findViewById(R.id.profilepic);
        tvname = mNavigationView.getHeaderView(0).findViewById(R.id.name);
        tvuseremail = mNavigationView.getHeaderView(0).findViewById(R.id.useremail);

        btnsuaNCC = findViewById(R.id.suanhacungcap);
        btnxoaNCC = findViewById(R.id.xoanhacungap);

        imgNCC = findViewById(R.id.imgNCC);
        tvTenNCC = findViewById(R.id.tvTenNCC);
        edtTenNCC = findViewById(R.id.edtTenNCC);
        edtDiachiNCC = findViewById(R.id.edtDiachiNCC);
        edtSdtNCC = findViewById(R.id.edtSdtNCC);
        edtMotaNCC = findViewById(R.id.edtMotaNCC);
    }
    private void readDatabaseUser() {
        // Read from the database
        dataUser.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showUserInfo();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }
    private void showUserInfo(){
        if(user==null){
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        if (name == null){
            tvname.setVisibility(View.GONE);
        }else{
            tvname.setVisibility(View.VISIBLE);
            tvname.setText(name);
        }

        tvuseremail.setText(email);
        Glide.with(this).load(photoUrl).error(R.drawable.profilepic).into(imgprofilepic);

    }

    private void onclickUpdateAnh(){
        imgNCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAnh();
            }
        });
    }

    private void updateAnh(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requestPermissions(
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    ANHNCC_VIEW);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions == null || grantResults == null || grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (requestCode == ANHNCC_VIEW) {
            openGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //receive result after your activity finished scanning
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        // Xu ly anhNCC
        if (requestCode == ANHNCC_VIEW && resultCode == RESULT_OK && data != null) {
            return;
        }
    }

    public void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent,"Select Piture"));

    }
}