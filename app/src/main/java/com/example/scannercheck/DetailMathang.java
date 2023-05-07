package com.example.scannercheck;

import static java.lang.Integer.parseInt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
    Button btnthemdvt;

    TextView tvTenMH;
    EditText edtTenMH,edtQuydoi,edtDongiaMH, edtSoluongMH;
    EditText etDongiaMH,etDonvitinhMH,etQuydoi, etTenMH;
    Button btnsuaMH,btnxoaMH, btnxoadvtMH;
    Mathang mathang;
    ImageView imgMH;

    private Dialog dialog;

    private String tenanh;
    ProgressDialog progressDialog;
    private String ma_dvt;

    private Spinner spnCategory;
    private CategoryAdapter categoryAdapter;
    List<Donvitinh> donvitinhs;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private DatabaseReference datamathang;
    private FirebaseUser user;
    private DatabaseReference dataUser;

    NavigationView mNavigationView;

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

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        initUi();
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            return;
        }
        mathang = (Mathang) bundle.get("object_mathang");
        tvTenMH.setText(mathang.getName());
        edtTenMH.setText(mathang.getName());
        Picasso.with(DetailMathang.this).load(mathang.getImage()).into(imgMH);

        tenanh = mathang.getName();

        getDVT(mathang.getId());
        onclickUpdateAnh();
        onclickInsertDonvitinhMH();
        onclickUpdateMH();
        onclickDeleteMH();
        onclickDeleteDonvitinhMH();

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
        HomeActivity.sidebar(this, mNavigationView, user, dataUser, drawerLayout);

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
        int Quydoi = parseInt(edtQuydoi.getText().toString().trim());
        String getMadvt = ma_dvt;
        Date now = new Date();
        String datetime = ""+now;

        // Check mh
        if(TenMH.equalsIgnoreCase("")){
            Toast.makeText(DetailMathang.this, "Vui lòng nhập tên mặt hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        if(Quydoi==0){
            Toast.makeText(DetailMathang.this, "Vui lòng nhập giá trị quy đổi", Toast.LENGTH_SHORT).show();
            return;
        }
        if(edtDongiaMH.getText().toString().trim().equalsIgnoreCase("")){
            Toast.makeText(DetailMathang.this, "Vui lòng nhập đơn giá", Toast.LENGTH_SHORT).show();
            return;
        }
        if(getMadvt.equalsIgnoreCase("")||getMadvt.equalsIgnoreCase("1")){
            Toast.makeText(DetailMathang.this, "Vui lòng chọn đơn vị tính", Toast.LENGTH_SHORT).show();
            return;
        }

        float DongiaMH = Float.parseFloat(edtDongiaMH.getText().toString().trim());

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

                        Mathang suamathang = new Mathang(MaMH, TenMH, imageUrl,"image"+calendar.getTimeInMillis()+".jpg",donvitinhs);

                        donvitinhs.forEach(dvt->{
                            if (dvt.getId().compareTo(ma_dvt)==0){
                                dvt.setDongia(DongiaMH);
                                dvt.setQuydoi(Quydoi);
                                dvt.setSoluong(Integer.parseInt(String.valueOf(edtSoluongMH.getText())));
                            }
                        });
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

    private void onclickDeleteDonvitinhMH(){
        btnxoadvtMH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                datamathang.child("MatHang").child(user.getUid()).child(mathang.getId()).child("donvitinhs").removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    }
                });

                for(int i = 0; i < donvitinhs.size(); i++)
                {
                    Donvitinh obj = donvitinhs.get(i);

                    if(obj.getId().equals(ma_dvt)){
                        //found, delete.
                        donvitinhs.remove(i);
                        break;
                    }

                }

                datamathang.child("MatHang").child(user.getUid()).child(mathang.getId()).child("donvitinhs").setValue(donvitinhs, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        progressDialog.dismiss();
                        Toast.makeText(DetailMathang.this, "Xóa đơn vị tính thành công", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void onclickInsertDonvitinhMH(){
        btnthemdvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFeedbackDialog(Gravity.CENTER);
            }
        });
    }

    private void openFeedbackDialog(int gravity){
        dialog = new Dialog(DetailMathang.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dsmh_createmhkhacdvt);

        Window window = dialog.getWindow();
        if(window==null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if( Gravity.BOTTOM == gravity){
            dialog.setCancelable(true);
        }else {
            dialog.setCancelable(false);
        }

        initUiDialog();

        etTenMH.setText(mathang.getName());

        TextView    trolai   = dialog.findViewById(R.id.trolai);
        Button      dongy      = dialog.findViewById(R.id.dongy);

        trolai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dongy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPushData();
            }
        });
        dialog.show();
    }


    private void onClickPushData() {

        String DonvitinhMH = etDonvitinhMH.getText().toString().trim();
        String Quydoidialog = etQuydoi.getText().toString().trim();

        //Check them don vi tinh
        if(DonvitinhMH.equalsIgnoreCase("")){
            Toast.makeText(DetailMathang.this, "Vui lòng nhập đơn vị tính", Toast.LENGTH_SHORT).show();
            return;
        }
        if(etDongiaMH.getText().toString().trim().equalsIgnoreCase("")){
            Toast.makeText(DetailMathang.this, "Vui lòng nhập đơn giá", Toast.LENGTH_SHORT).show();
            return;
        }

        float DongiaMH = Float.parseFloat(etDongiaMH.getText().toString().trim());

        progressDialog.show();

        Calendar calendar = Calendar.getInstance();
        String madvt = "dvt"+calendar.getTimeInMillis();

        Donvitinh donvitinh = new Donvitinh(madvt, DonvitinhMH, DongiaMH,Integer.parseInt(Quydoidialog),0);

        datamathang.child("MatHang").child(user.getUid()).child(mathang.getId()).child("donvitinhs").child(String.valueOf(donvitinhs.size())).setValue(donvitinh, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                progressDialog.dismiss();
                dialog.dismiss();
                Toast.makeText(DetailMathang.this, "Thêm đơn vị tính thành công", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initUiDialog(){
        etDongiaMH = dialog.findViewById(R.id.etDongiaMH);
        etDonvitinhMH = dialog.findViewById(R.id.etDonvitinhMH);
        etTenMH = dialog.findViewById(R.id.etTenMH);
        etQuydoi = dialog.findViewById(R.id.etGiatriquydoi);

    }

    private void initUi(){
        mNavigationView = findViewById(R.id.navigation_view);
        btnsuaMH = findViewById(R.id.suamathang);
        btnxoaMH = findViewById(R.id.xoamathang);
        btnxoadvtMH = findViewById(R.id.xoadvtmathang);
        btnthemdvt = findViewById(R.id.themmhkhacdvt);
        tvTenMH = findViewById(R.id.tvTenMH);
        edtTenMH = findViewById(R.id.edtTenMH);
        edtQuydoi = findViewById(R.id.edtQuydoi);
        edtDongiaMH = findViewById(R.id.edtDongiaMH);
        edtSoluongMH = findViewById(R.id.edtSoluongMH);

        imgMH = findViewById(R.id.imgMH);
    }

    private void getDVT(String id_mh) {

        List<Category> list = new ArrayList<>();

        DatabaseReference dataDVT = FirebaseDatabase.getInstance().getReference();
        // Read from the database
        dataDVT.child("MatHang").child(user.getUid()).child(id_mh).child("donvitinhs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                list.clear();
                donvitinhs = new ArrayList<>();
                Donvitinh value = new Donvitinh();
                for (DataSnapshot unit : dataSnapshot.getChildren()){
                    value = unit.getValue(Donvitinh.class);
                    if(value.getQuydoi()==1){
                        ma_dvt=value.getId();
                        list.add(new Category(value.getId(),value.getTendvt()));
                        donvitinhs.add(value);
                    }
                }
                if(ma_dvt==""){
                    list.add(new Category("1","Chọn đơn vị tính"));
                }
                for (DataSnapshot unit : dataSnapshot.getChildren()){
                    value = unit.getValue(Donvitinh.class);
                    if(!value.getId().equalsIgnoreCase(ma_dvt)){
                        list.add(new Category(value.getId(),value.getTendvt()));
                        donvitinhs.add(value);
                    }
                }
                spnCategory = findViewById(R.id.spn_category);
                categoryAdapter = new CategoryAdapter(DetailMathang.this,R.layout.item_selected,list);
                spnCategory.setAdapter(categoryAdapter);
                spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        ma_dvt = categoryAdapter.getItem(i).getId();
                        donvitinhs.forEach(dvt-> {
                            if(dvt.getId().compareTo(ma_dvt)==0){
                                Float dongia =  dvt.getDongia();
                                String quydoi = String.valueOf(dvt.getQuydoi());
                                edtDongiaMH.setText(dongia.toString());
                                edtQuydoi.setText(quydoi);
                                edtSoluongMH.setText(String.valueOf(dvt.getSoluong()));
                            }
                        });
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