package com.example.scannercheck;

import android.Manifest;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImportActivity extends AppCompatActivity {
    public static final int DEFAULT_VIEW = 0x22;
    public static final int ANHMH_VIEW = 0x23;
    private static final int REQUEST_CODE_SCAN = 0X01;

    ProgressDialog progressDialog;

    private Spinner spnCategory;
    private Spinner spnCategorydvt;
    private CategoryAdapter categoryAdapter;
    private CategoryAdapter categoryAdapterdvt;
    List<PhieuNhap> phieuNhaps;
    List<Donvitinh> donvitinhs;
    String mancc, tenncc;
    String madvt, tendvt;

    private RecyclerView rvItems;
    private SearchView searchView;
    private EditText etMaMH,etTenMH,etDongiaMH, etSoluong, etThanhtien;

    private Dialog dialog;
    private DatabaseReference datamathang;
    private FirebaseUser user;
    private DatabaseReference dataUser;

    NavigationView mNavigationView;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danhsachnhapmathang);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        progressDialog = new ProgressDialog(this);
        // list view
        phieuNhaps = new ArrayList<>();

        dataUser = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        datamathang = FirebaseDatabase.getInstance().getReference();

        initUi();
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
        readDatabase("");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                readDatabase(s);
                return false;
            }
        });

        rvItems = findViewById(R.id.recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        rvItems.setLayoutManager(layoutManager);
        rvItems.setHasFixedSize(true);
        rvItems.setAdapter(new AdapterRecyclerViewCreatePN(this,phieuNhaps));

        // createmh
        FloatingActionButton floating = findViewById(R.id.fabBtnCreateNMH);
        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFeedbackDialog(Gravity.CENTER);
            }
        });

    }

    private void openFeedbackDialog(int gravity){
        dialog = new Dialog(ImportActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dsmh_importmh);

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

        TextView    trolai   = dialog.findViewById(R.id.trolai);
        Button      dongy      = dialog.findViewById(R.id.dongy);

        List<Category> list = new ArrayList<>();
        list.add(new Category("1","Chọn nhà cung cấp"));

        DatabaseReference datanhacungap = FirebaseDatabase.getInstance().getReference();
        // Read from the database
        datanhacungap.child("NhaCungCap").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Nhacungcap value = new Nhacungcap();
                for (DataSnapshot unit : dataSnapshot.getChildren()){
                    value = unit.getValue(Nhacungcap.class);
                    list.add(new Category(value.getId(),value.getName()));
                }
                spnCategory = dialog.findViewById(R.id.spn_category);
                categoryAdapter = new CategoryAdapter(ImportActivity.this,R.layout.item_selected,list);
                spnCategory.setAdapter(categoryAdapter);
                spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        mancc = categoryAdapter.getItem(i).getId();
                        tenncc = categoryAdapter.getItem(i).getName();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(ImportActivity.this, "Đọc thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        initUiDialog();;

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String dongia = etDongiaMH.getText().toString().trim();
                String soluong = etSoluong.getText().toString().trim();

                if (dongia.isEmpty()||soluong.isEmpty()) {
                    etDongiaMH.setError("Nhập đơn giá");
                } else {
                    etDongiaMH.setError(null);
                    Float thanhtien = Integer.parseInt(dongia)*Float.parseFloat(soluong);
                    etThanhtien.setText(String.format("%.0f", thanhtien));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        etSoluong.addTextChangedListener(textWatcher);
        etDongiaMH.addTextChangedListener(textWatcher);

        String MaMH = etMaMH.getText().toString().trim();

        etMaMH.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Trước khi ký tự thay đổi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Khi ký tự thay đổi
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Sau khi ký tự thay đổi
                Log.d("EditText", "After text changed: " + s.toString());

                String mamhcannhap = s.toString();
                if (!mamhcannhap.isEmpty()){
                    geDvtDialog(mamhcannhap);
                }
            }
        });


        clickquetma();
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
        String MaMH = etMaMH.getText().toString().trim();

        //Check them mat hang
        if(MaMH.equalsIgnoreCase("")){
            Toast.makeText(ImportActivity.this, "Vui lòng nhập mã mặt hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        if(etDongiaMH.getText().toString().trim().equalsIgnoreCase("")){
            Toast.makeText(ImportActivity.this, "Vui lòng nhập đơn giá", Toast.LENGTH_SHORT).show();
            return;
        }

        float DongiaMH = Float.parseFloat(etDongiaMH.getText().toString().trim());
        Integer soluong = Integer.parseInt(etSoluong.getText().toString());
        float ThanhtienMH = Float.parseFloat(etThanhtien.getText().toString().trim());
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String ngaynhap = day + "/" + (month + 1) + "/" + year;

        progressDialog.show();
        String maphieu = "maphieu"+calendar.getTimeInMillis();

        PhieuNhap phieunhap = new PhieuNhap(maphieu, MaMH, etTenMH.getText().toString(), mancc, tenncc, madvt, tendvt, soluong, DongiaMH,ThanhtienMH, calendar.getTimeInMillis());

        datamathang.child("PhieuNhap").child(user.getUid()).child(maphieu).setValue(phieunhap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                progressDialog.dismiss();
                dialog.dismiss();
                Toast.makeText(ImportActivity.this, "Thêm dữ liệu thành công", Toast.LENGTH_SHORT).show();
                donvitinhs.forEach(dvt->{
                    if (dvt.getId().compareTo(madvt)==0){
                        int soluongmoi = dvt.getSoluong() + soluong;
                        dvt.setSoluong(soluongmoi);
                    }
                });

                datamathang.child("MatHang").child(user.getUid()).child(MaMH).child("donvitinhs").setValue(donvitinhs, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(ImportActivity.this, "Cập nhật số lượng trong kho thành công", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


    }
    private void readDatabase(String keyword){

        // Read from the database

        datamathang.child("PhieuNhap").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                PhieuNhap phieunhap = new PhieuNhap();
                phieuNhaps.clear();
                for (DataSnapshot unit : dataSnapshot.getChildren()){
                    phieunhap = unit.getValue(PhieuNhap.class);

                    if(phieunhap.getTenmh().contains(keyword)){
                        phieuNhaps.add(phieunhap);
                    }

                }

                rvItems.setAdapter(new AdapterRecyclerViewCreatePN(ImportActivity.this,phieuNhaps));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(ImportActivity.this, "Đọc thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clickquetma(){
        Button    quetma   = dialog.findViewById(R.id.quetma_createmh);
        quetma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newViewBtnClick();
            }
        });
    }

    private void newViewBtnClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requestPermissions(
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    DEFAULT_VIEW);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions == null || grantResults == null || grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (requestCode == DEFAULT_VIEW) {
            //start ScankitActivity for scanning barcode
            ScanUtil.startScan(ImportActivity.this, REQUEST_CODE_SCAN, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());
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
        // Obtain the return value of HmsScan from the value returned by the onActivityResult method by using ScanUtil.RESULT as the key value.
        if (requestCode == REQUEST_CODE_SCAN) {
            Object obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj instanceof HmsScan) {
                if (!TextUtils.isEmpty(((HmsScan) obj).getOriginalValue())) {
                    etMaMH.setText(((HmsScan) obj).getOriginalValue());
                    geDvtDialog(((HmsScan) obj).getOriginalValue());
                    Toast.makeText(this, ((HmsScan) obj).getOriginalValue(), Toast.LENGTH_SHORT).show();
                }
                return;
            }
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
    }


    private void initUi(){
        mNavigationView = findViewById(R.id.navigation_view);
        searchView = findViewById(R.id.search_view);
    }
    private void initUiDialog(){
        etMaMH = dialog.findViewById(R.id.etMaMH);
        etTenMH = dialog.findViewById(R.id.etTenMH);
        etDongiaMH = dialog.findViewById(R.id.etDongiaMH);
        etSoluong = dialog.findViewById(R.id.etSoluongMH);
        etThanhtien = dialog.findViewById(R.id.etThanhtien);

    }

    public void geDvtDialog(String mamhcannhap){
        DatabaseReference data = FirebaseDatabase.getInstance().getReference();

        List<Category> listdvt = new ArrayList<>();
        // Read from the database

        data.child("MatHang").child(user.getUid()).child(mamhcannhap).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Mathang mathang = new Mathang();
                mathang = dataSnapshot.getValue(Mathang.class);
                if (mathang!=null){
                    listdvt.add(new Category("1","Chọn đơn vị tính"));
                    etTenMH.setText(mathang.getName());
                    donvitinhs = mathang.getDonvitinhs();
                    for (Donvitinh unit : mathang.getDonvitinhs()){
                        listdvt.add(new Category(unit.getId(),unit.getTendvt()));
                    }
                    spnCategorydvt = dialog.findViewById(R.id.spn_categorydvt);
                    categoryAdapterdvt = new CategoryAdapter(ImportActivity.this,R.layout.item_selected,listdvt);
                    spnCategorydvt.setAdapter(categoryAdapterdvt);
                    spnCategorydvt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            madvt = categoryAdapterdvt.getItem(i).getId();
                            tendvt = categoryAdapterdvt.getItem(i).getName();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else {
                    etTenMH.setText(null);
                    spnCategorydvt = dialog.findViewById(R.id.spn_categorydvt);
                    categoryAdapterdvt = new CategoryAdapter(ImportActivity.this,R.layout.item_selected,listdvt);
                    spnCategorydvt.setAdapter(categoryAdapterdvt);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(ImportActivity.this, "Đọc thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}