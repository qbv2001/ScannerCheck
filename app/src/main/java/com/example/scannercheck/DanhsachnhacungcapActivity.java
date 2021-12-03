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
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
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
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class DanhsachnhacungcapActivity extends AppCompatActivity {
    public static final int ANHNCC_VIEW = 0x23;
    public static final int DEFAULT_VIEW = 0x22;
    private static final int REQUEST_CODE_SCAN = 0X01;

    ProgressDialog progressDialog;

    private CircleImageView edtAnhNCC;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri uri;

    private SearchView searchView;
    private RecyclerView rvItems;
    private Dialog dialog;
    private DatabaseReference datanhacungap;
    private FirebaseUser user;
    private DatabaseReference dataUser;

    NavigationView mNavigationView;
    CircleImageView imgprofilepic;
    TextView tvname;
    TextView tvuseremail;
    private List<Nhacungcap> nhacungcaps;
    private EditText etMaNCC,etTenNCC,etDiachiNCC,etSdtNCC,etMota;
    private Button buttonsapxep;
    String duplicate = "";

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
        edtAnhNCC.setImageBitmap(bitmapImageView);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danhsachnhacungcap);

        dataUser = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        datanhacungap = FirebaseDatabase.getInstance().getReference();

        storage = FirebaseStorage.getInstance("gs://scanner-check-27051.appspot.com");
        storageRef = storage.getReference();

        progressDialog = new ProgressDialog(this);

        // list view
        nhacungcaps = new ArrayList<>();

        initUi();
        showUserInfo();
        readDatabaseUser();

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

        rvItems = findViewById(R.id.recycler_viewNCC);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        rvItems.setLayoutManager(layoutManager);
        rvItems.setHasFixedSize(true);
        rvItems.setAdapter(new AdapterRecyclerViewCreateNCC(this,nhacungcaps));

        // createncc
        FloatingActionButton floating = findViewById(R.id.fabBtnCreateNCC);
        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFeedbackDialog(Gravity.CENTER);
            }
        });

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
                    break;
                case R.id.nav_thongke:
                    i1 = new Intent(this, ThongkeActivity.class);startActivity(i1);
                    finish();
                    break;
                case R.id.nav_logout:
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(DanhsachnhacungcapActivity.this,LoginActivity.class));
                    Toast.makeText(DanhsachnhacungcapActivity.this, "Đã đăng xuất",Toast.LENGTH_SHORT).show();
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

    private void openFeedbackDialog(int gravity){
        dialog = new Dialog(DanhsachnhacungcapActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dsncc_createncc);

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
        Button quetma =dialog.findViewById(R.id.quetma_createnncc);

        initUiDialog();
        clickchonanh();

        trolai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dongy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPushData();            }
        });

        quetma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newViewBtnClick();
            }
        });

        dialog.show();
    }


    private void clickchonanh(){
        CircleImageView    chonanh   = dialog.findViewById(R.id.etAnhNCC);
        chonanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newViewchonanhclick();
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
            ScanUtil.startScan(DanhsachnhacungcapActivity.this, REQUEST_CODE_SCAN, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());
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

        // Obtain the return value of HmsScan from the value returned by the onActivityResult method by using ScanUtil.RESULT as the key value.
        if (requestCode == REQUEST_CODE_SCAN) {
            Object obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj instanceof HmsScan) {
                if (!TextUtils.isEmpty(((HmsScan) obj).getOriginalValue())) {
                    etMaNCC.setText(((HmsScan) obj).getOriginalValue());
                    Toast.makeText(this, ((HmsScan) obj).getOriginalValue(), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }

        // Xu ly anh MH
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

    private void newViewchonanhclick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requestPermissions(
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    ANHNCC_VIEW);
        }
    }


    private void onClickPushData() {

        String TenNCC = etTenNCC.getText().toString().trim();
        String DiachiNCC = etDiachiNCC.getText().toString().trim();
        String SdtNCC = etSdtNCC.getText().toString().trim();
        String mota = etMota.getText().toString().trim();
        String mancc = etMaNCC.getText().toString().trim();
        // Check NCC
        if(TenNCC.equalsIgnoreCase("")){
            Toast.makeText(DanhsachnhacungcapActivity.this, "Vui lòng nhập tên nhà cung cấp", Toast.LENGTH_SHORT).show();
            return;
        }


        for (Nhacungcap aa : nhacungcaps){
            if (mancc.equalsIgnoreCase(aa.getId())){
                duplicate = "mã nhà cung cấp bị trùng";
                Toast.makeText(DanhsachnhacungcapActivity.this, duplicate, Toast.LENGTH_LONG).show();
                return;
            }
        }

        for (Nhacungcap aa : nhacungcaps){
            if (TenNCC.equalsIgnoreCase(aa.getName())){
                duplicate = "tên nhà cung cấp bị trùng";
                Toast.makeText(DanhsachnhacungcapActivity.this, duplicate, Toast.LENGTH_LONG).show();
                return;
            }
        }



        if(DiachiNCC.equalsIgnoreCase("")){
            Toast.makeText(DanhsachnhacungcapActivity.this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
            return;
        }
        if(SdtNCC.equalsIgnoreCase("")){
            Toast.makeText(DanhsachnhacungcapActivity.this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }

        if(SdtNCC.length()!=10){
            Toast.makeText(DanhsachnhacungcapActivity.this, "Số điện thoại phải 10 cs", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mota.equalsIgnoreCase("")){
            Toast.makeText(DanhsachnhacungcapActivity.this, "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
            return;
        }



        progressDialog.show();
        // Get the data from an ImageView as bytes
        edtAnhNCC.setDrawingCacheEnabled(true);
        edtAnhNCC.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) edtAnhNCC.getDrawable()).getBitmap();
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
                Toast.makeText(DanhsachnhacungcapActivity.this, "Upload ảnh lỗi", Toast.LENGTH_SHORT).show();
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
                        String image = "image"+calendar.getTimeInMillis()+".jpg";
                        String MaNCC = mancc;
                        Nhacungcap nhacungcap = new Nhacungcap(MaNCC, TenNCC, mota, DiachiNCC, SdtNCC, imageUrl, image);
                        datanhacungap.child("NhaCungCap").child(user.getUid()).child(MaNCC).setValue(nhacungcap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                progressDialog.dismiss();
                                dialog.dismiss();
                                Toast.makeText(DanhsachnhacungcapActivity.this, "Thêm dữ liệu thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        });
    }

    private void readDatabase(String keyword){

        // Read from the database
        datanhacungap.child("NhaCungCap").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Nhacungcap value = new Nhacungcap();
                nhacungcaps.clear();
                for (DataSnapshot unit : dataSnapshot.getChildren()){
                    value = unit.getValue(Nhacungcap.class);
                    if(value.getName().contains(keyword)){
                        nhacungcaps.add(value);

                    }
                }

                rvItems.setAdapter(new AdapterRecyclerViewCreateNCC(DanhsachnhacungcapActivity.this,nhacungcaps));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(DanhsachnhacungcapActivity.this, "Đọc thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUi() {
        mNavigationView = findViewById(R.id.navigation_view);
        imgprofilepic = mNavigationView.getHeaderView(0).findViewById(R.id.profilepic);
        tvname = mNavigationView.getHeaderView(0).findViewById(R.id.name);
        tvuseremail = mNavigationView.getHeaderView(0).findViewById(R.id.useremail);

        searchView = findViewById(R.id.search_view);

        buttonsapxep = findViewById(R.id.btnsapxepncc);

        buttonsapxep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                readsapxep();
            }
        });
    }
        public void readsapxep(){

                // Read from the database
                datanhacungap.child("NhaCungCap").child(user.getUid()).orderByChild("name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        Nhacungcap value = new Nhacungcap();
                        nhacungcaps.clear();
                        for (DataSnapshot unit : dataSnapshot.getChildren()){
                            value = unit.getValue(Nhacungcap.class);
                            nhacungcaps.add(value);
                        }

                        rvItems.setAdapter(new AdapterRecyclerViewCreateNCC(DanhsachnhacungcapActivity.this,nhacungcaps));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Toast.makeText(DanhsachnhacungcapActivity.this, "Đọc thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }



    private void initUiDialog(){
        edtAnhNCC = dialog.findViewById(R.id.etAnhNCC);
        etTenNCC = dialog.findViewById(R.id.etTenNCC);
        etDiachiNCC = dialog.findViewById(R.id.etDiachiNCC);
        etSdtNCC = dialog.findViewById(R.id.etSdtNCC);
        etMota = dialog.findViewById(R.id.etMotaNCC);
        etMaNCC = dialog.findViewById(R.id.etMaNCC);
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

}