package com.example.scannercheck;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class DetailMathang extends AppCompatActivity {
    public static final int ANHMH_VIEW = 0x23;
    TextView tvTenMH;
    EditText edtTenMH,edtTenNCC,edtDonvitinhMH,edtSoluongMH,edtDongiaMH,edtMotaMH;
    Button btnsuaMH,btnxoaMH;
    Mathang mathang;
    ImageView imgMH;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private DatabaseReference datamathang;
    private FirebaseUser user;

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

        user = FirebaseAuth.getInstance().getCurrentUser();
        datamathang = FirebaseDatabase.getInstance().getReference();

        storage = FirebaseStorage.getInstance("gs://scanner-check-27051.appspot.com");
        storageRef = storage.getReference();

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
        Picasso.with(DetailMathang.this).load(mathang.getImage()).into(imgMH);

        onclickUpdateAnh();
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
                        // khi upload ảnh thành công
                        String imageUrl = uri.toString();
                        String MaMH = mathang.getId();
                        String TenMH = edtTenMH.getText().toString().trim();
                        String DonvitinhMH = edtDonvitinhMH.getText().toString().trim();
                        float DongiaMH = Float.parseFloat(edtDongiaMH.getText().toString().trim());
                        int SoluongMH = Integer.parseInt(edtSoluongMH.getText().toString().trim());
                        String NhaccMH = edtTenNCC.getText().toString().trim();
                        Date now = new Date();
                        String datetime = ""+now;
                        String mota = edtMotaMH.getText().toString().trim();


                        Mathang suamathang = new Mathang(MaMH, TenMH, SoluongMH, DongiaMH, datetime, imageUrl,"image"+calendar.getTimeInMillis()+".jpg", DonvitinhMH, mota, NhaccMH);
                        datamathang.child("MatHang").child(user.getUid()).child(MaMH).setValue(suamathang, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                //Xóa ảnh cũ
                                storageRef.child(mathang.getTenimage()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // File deleted successfully
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
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
                datamathang.child("MatHang").child(user.getUid()).child(mathang.getId()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        // Xóa ảnh trên db
                        storageRef = storage.getReference();
                        StorageReference desertRef = storageRef.child(mathang.getTenimage());
                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // File deleted successfully
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
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
        btnsuaMH = findViewById(R.id.suamathang);
        btnxoaMH = findViewById(R.id.xoamathang);

        tvTenMH = findViewById(R.id.tvTenMH);
        edtTenMH = findViewById(R.id.edtTenMH);
        edtTenNCC = findViewById(R.id.edtTenNCC);
        edtDonvitinhMH = findViewById(R.id.edtDonvitinhMH);
        edtSoluongMH = findViewById(R.id.edtSoluongMH);
        edtDongiaMH = findViewById(R.id.edtDongiaMH);
        edtMotaMH = findViewById(R.id.edtMotaMH);
        imgMH = findViewById(R.id.imgMH);
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