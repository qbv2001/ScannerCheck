package com.example.scannercheck;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailMathang extends AppCompatActivity {
    public static final int ANHMH_VIEW = 0x23;
    TextView tvTenMH;
    EditText edtTenMH,edtDonvitinhMH,edtSoluongMH,edtDongiaMH,edtMotaMH;
    Button btnsuaMH,btnxoaMH;
    Mathang mathang;
    ImageView imgMH;

    private String tenanh;
    ProgressDialog progressDialog;
    private String idnccdau;

    private Spinner spnCategory;
    private CategoryAdapter categoryAdapter;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private DatabaseReference datamathang;
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
        imgMH.setImageBitmap(bitmapImageView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_mathang);

        dataUser = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        datamathang = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        storage = FirebaseStorage.getInstance("gs://android-b22a3.appspot.com");
        storageRef = storage.getReference();

        initUi();
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            return;
        }
        mathang = (Mathang) bundle.get("object_mathang");
        tvTenMH.setText(mathang.getName());
        edtTenMH.setText(mathang.getName());
        edtDonvitinhMH.setText(mathang.getDvt());
        edtSoluongMH.setText(""+mathang.getSoluong());
        edtDongiaMH.setText(""+mathang.getDongia());
        edtMotaMH.setText(mathang.getMota());
        Picasso.with(DetailMathang.this).load(mathang.getImage()).into(imgMH);

        tenanh = mathang.getName();
        idnccdau = mathang.getNhacc();

        getNCC();
        showUserInfo();
        readDatabaseUser();
        onclickUpdateAnh();
        onclickUpdateMH();
        onclickDeleteMH();


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
        HomeActivity.sidebar(this, mNavigationView, tvname, tvuseremail, user, dataUser, drawerLayout);

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

        String TenMH = edtTenMH.getText().toString().trim();
        String DonvitinhMH = edtDonvitinhMH.getText().toString().trim();
        String NhaccMH = idnccdau;
        Date now = new Date();
        String datetime = ""+now;
        String mota = edtMotaMH.getText().toString().trim();

        // Check mh
        if(TenMH.equalsIgnoreCase("")){
            Toast.makeText(DetailMathang.this, "Vui lòng nhập tên mặt hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        if(DonvitinhMH.equalsIgnoreCase("")){
            Toast.makeText(DetailMathang.this, "Vui lòng nhập đơn vị tính", Toast.LENGTH_SHORT).show();
            return;
        }
        if(edtDongiaMH.getText().toString().trim().equalsIgnoreCase("")){
            Toast.makeText(DetailMathang.this, "Vui lòng nhập đơn giá", Toast.LENGTH_SHORT).show();
            return;
        }
        if(edtSoluongMH.getText().toString().trim().equalsIgnoreCase("")){
            Toast.makeText(DetailMathang.this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show();
            return;
        }
        if(NhaccMH.equalsIgnoreCase("")||NhaccMH.equalsIgnoreCase("1")){
            Toast.makeText(DetailMathang.this, "Vui lòng chọn nhà cung cấp", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mota.equalsIgnoreCase("")){
            Toast.makeText(DetailMathang.this, "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
            return;
        }

        float DongiaMH = Float.parseFloat(edtDongiaMH.getText().toString().trim());
        int SoluongMH = Integer.parseInt(edtSoluongMH.getText().toString().trim());

        progressDialog.show();

        // Get the data from an ImageView as bytes
        imgMH.setDrawingCacheEnabled(true);
        imgMH.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imgMH.getDrawable()).getBitmap();
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
                Toast.makeText(DetailMathang.this, "Upload ảnh lỗi", Toast.LENGTH_SHORT).show();
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
                        String MaMH = mathang.getId();

                        Mathang suamathang = new Mathang(MaMH, TenMH, SoluongMH, DongiaMH, datetime, imageUrl,"image"+calendar.getTimeInMillis()+".jpg", DonvitinhMH, mota, NhaccMH);
                        datamathang.child("MatHang").child(user.getUid()).child(MaMH).setValue(suamathang, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                //Xóa ảnh cũ
                                storageRef.child(tenanh).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        tenanh = suamathang.getTenimage();
                                        tvTenMH.setText(TenMH);
                                        progressDialog.dismiss();
                                        Toast.makeText(DetailMathang.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                        // File deleted successfully
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        progressDialog.dismiss();
                                        Toast.makeText(DetailMathang.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
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

    private void onclickDeleteMH(){
        btnxoaMH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                datamathang.child("MatHang").child(user.getUid()).child(mathang.getId()).removeValue(new DatabaseReference.CompletionListener() {
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
                        Intent intent = new Intent(DetailMathang.this,DanhsachmathangActivity.class);
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

        btnsuaMH = findViewById(R.id.suamathang);
        btnxoaMH = findViewById(R.id.xoamathang);

        tvTenMH = findViewById(R.id.tvTenMH);
        edtTenMH = findViewById(R.id.edtTenMH);
        edtDonvitinhMH = findViewById(R.id.edtDonvitinhMH);
        edtSoluongMH = findViewById(R.id.edtSoluongMH);
        edtDongiaMH = findViewById(R.id.edtDongiaMH);
        edtMotaMH = findViewById(R.id.edtMotaMH);
        imgMH = findViewById(R.id.imgMH);
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

    private void getNCC() {

        List<Category> list = new ArrayList<>();

        DatabaseReference datanhacungap = FirebaseDatabase.getInstance().getReference();
        // Read from the database
        datanhacungap.child("NhaCungCap").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Nhacungcap value = new Nhacungcap();
                int dem = 0;
                for (DataSnapshot unit : dataSnapshot.getChildren()){
                    value = unit.getValue(Nhacungcap.class);
                    if(value.getId().equalsIgnoreCase(idnccdau)){
                        dem=1;
                        list.add(new Category(value.getId(),value.getName()));
                    }
                }
                if(dem==0){
                    list.add(new Category("1","Chọn nhà cung cấp"));
                }
                for (DataSnapshot unit : dataSnapshot.getChildren()){
                    value = unit.getValue(Nhacungcap.class);
                    if(!value.getId().equalsIgnoreCase(idnccdau)){
                        list.add(new Category(value.getId(),value.getName()));
                    }
                }
                spnCategory = findViewById(R.id.spn_category);
                categoryAdapter = new CategoryAdapter(DetailMathang.this,R.layout.item_selected,list);
                spnCategory.setAdapter(categoryAdapter);
                spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        idnccdau = categoryAdapter.getItem(i).getId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(DetailMathang.this, "Đọc thất bại!", Toast.LENGTH_SHORT).show();
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
        imgMH.setOnClickListener(new View.OnClickListener() {
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
                    ANHMH_VIEW);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions == null || grantResults == null || grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (requestCode == ANHMH_VIEW) {
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
        // Xu ly anh MH
        if (requestCode == ANHMH_VIEW && resultCode == RESULT_OK && data != null) {
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