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

public class DanhsachmathangActivity extends AppCompatActivity {
    public static final int DEFAULT_VIEW = 0x22;
    public static final int ANHMH_VIEW = 0x23;
    private static final int REQUEST_CODE_SCAN = 0X01;

    ProgressDialog progressDialog;

    private RecyclerView rvItems;
    private SearchView searchView;
    private EditText etMaMH,etTenMH,etDongiaMH,etDonvitinhMH,etSoluongMH,etMotaMH;
    private EditText dongiaa,dongiab;
    private Button loc;

    private String tenncc;
    private Spinner spnCategory;
    private CategoryAdapter categoryAdapter;

    private Dialog dialog;
    private DatabaseReference datamathang;
    private FirebaseUser user;
    private DatabaseReference dataUser;

    NavigationView mNavigationView;
    CircleImageView imgprofilepic;
    TextView tvname;
    TextView tvuseremail;

    List<Mathang> mathangs;
    private CircleImageView edtAnhMH;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri uri;

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
        edtAnhMH.setImageBitmap(bitmapImageView);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danhsachmathang);
        storage = FirebaseStorage.getInstance("gs://scanner-check-27051.appspot.com");
        storageRef = storage.getReference();

        progressDialog = new ProgressDialog(this);
        // list view
        mathangs = new ArrayList<>();

        dataUser = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        datamathang = FirebaseDatabase.getInstance().getReference();

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

        rvItems = findViewById(R.id.recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvItems.setLayoutManager(layoutManager);
        rvItems.setHasFixedSize(true);
        rvItems.setAdapter(new AdapterRecyclerViewCreateMH(this,mathangs));

        // createmh
        FloatingActionButton floating = findViewById(R.id.fabBtnCreateMH);
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
                    startActivity(new Intent(DanhsachmathangActivity.this,LoginActivity.class));
                    Toast.makeText(DanhsachmathangActivity.this, "Đã đăng xuất",Toast.LENGTH_SHORT).show();
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

    private void openFeedbackDialog(int gravity){
        dialog = new Dialog(DanhsachmathangActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dsmh_createmh);

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
                categoryAdapter = new CategoryAdapter(DanhsachmathangActivity.this,R.layout.item_selected,list);
                spnCategory.setAdapter(categoryAdapter);
                spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        tenncc = categoryAdapter.getItem(i).getId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(DanhsachmathangActivity.this, "Đọc thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        initUiDialog();
        clickquetma();
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
                onClickPushData();
            }
        });
        dialog.show();
    }

    private void onClickPushData() {

        String MaMH = etMaMH.getText().toString().trim();
        String TenMH = etTenMH.getText().toString().trim();
        String DonvitinhMH = etDonvitinhMH.getText().toString().trim();
        String NhaccMH = tenncc;
        Date date = new Date();
        String datetime = ""+date;
        String mota = etMotaMH.getText().toString().trim();

        //Check them mat hang
        if(MaMH.equalsIgnoreCase("")){
            Toast.makeText(DanhsachmathangActivity.this, "Vui lòng nhập mã mặt hàng", Toast.LENGTH_SHORT).show();
            return;
        }

//        for (Mathang aa : mathangs){
//            if (MaMH.equalsIgnoreCase(aa.getId())){
//                duplicate = "ten nhà cung cấp bị trùng";
//                Toast.makeText(DanhsachmathangActivity.this, duplicate, Toast.LENGTH_LONG).show();
//                return;
//            }
//        }


        if(TenMH.equalsIgnoreCase("")){
            Toast.makeText(DanhsachmathangActivity.this, "Vui lòng nhập tên mặt hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        if(DonvitinhMH.equalsIgnoreCase("")){
            Toast.makeText(DanhsachmathangActivity.this, "Vui lòng nhập đơn vị tính", Toast.LENGTH_SHORT).show();
            return;
        }
        if(etDongiaMH.getText().toString().trim().equalsIgnoreCase("")){
            Toast.makeText(DanhsachmathangActivity.this, "Vui lòng nhập đơn giá", Toast.LENGTH_SHORT).show();
            return;
        }
        if(etSoluongMH.getText().toString().trim().equalsIgnoreCase("")){
            Toast.makeText(DanhsachmathangActivity.this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show();
            return;
        }
        if(NhaccMH.equalsIgnoreCase("")||NhaccMH.equalsIgnoreCase("1")){
            Toast.makeText(DanhsachmathangActivity.this, "Vui lòng chọn nhà cung cấp", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mota.equalsIgnoreCase("")){
            Toast.makeText(DanhsachmathangActivity.this, "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
            return;
        }

        float DongiaMH = Float.parseFloat(etDongiaMH.getText().toString().trim());
        int SoluongMH = Integer.parseInt(etSoluongMH.getText().toString().trim());

        progressDialog.show();
        // Get the data from an ImageView as bytes
        edtAnhMH.setDrawingCacheEnabled(true);
        edtAnhMH.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) edtAnhMH.getDrawable()).getBitmap();
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
                Toast.makeText(DanhsachmathangActivity.this, "Upload ảnh lỗi", Toast.LENGTH_SHORT).show();
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

                        Mathang mathang = new Mathang(MaMH, TenMH, SoluongMH, DongiaMH, datetime, imageUrl,"image"+calendar.getTimeInMillis()+".jpg", DonvitinhMH, mota, NhaccMH);
                        datamathang.child("MatHang").child(user.getUid()).child(MaMH).setValue(mathang, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                progressDialog.dismiss();
                                dialog.dismiss();
                                Toast.makeText(DanhsachmathangActivity.this, "Thêm dữ liệu thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        });

    }
    private void readDatabase(String keyword){

        // Read from the database
        datamathang.child("MatHang").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Mathang value = new Mathang();
                mathangs.clear();
                for (DataSnapshot unit : dataSnapshot.getChildren()){
                    value = unit.getValue(Mathang.class);
                    if(keyword.equalsIgnoreCase("")){
                        mathangs.add(value);
                    }else{
                        if(value.getDongia()== Integer.parseInt(keyword)){

                            mathangs.add(value);
                        }
                    }

                }

                rvItems.setAdapter(new AdapterRecyclerViewCreateMH(DanhsachmathangActivity.this,mathangs));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(DanhsachmathangActivity.this, "Đọc thất bại!", Toast.LENGTH_SHORT).show();
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

    private void clickchonanh(){
        CircleImageView    chonanh   = dialog.findViewById(R.id.etAnhMH);
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

    private void newViewchonanhclick() {
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

        if (requestCode == DEFAULT_VIEW) {
            //start ScankitActivity for scanning barcode
            ScanUtil.startScan(DanhsachmathangActivity.this, REQUEST_CODE_SCAN, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());
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
        mActivityResultLauncher.launch(Intent.createChooser(intent,"Select Piture"));

    }


    private void initUi(){
        mNavigationView = findViewById(R.id.navigation_view);
        imgprofilepic = mNavigationView.getHeaderView(0).findViewById(R.id.profilepic);
        tvname = mNavigationView.getHeaderView(0).findViewById(R.id.name);
        tvuseremail = mNavigationView.getHeaderView(0).findViewById(R.id.useremail);

        searchView = findViewById(R.id.search_view);
        dongiaa = findViewById(R.id.dongiaa);
        dongiab = findViewById(R.id.dongiab);
        loc = findViewById(R.id.loc);

        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loctheogia();
            }
        });

    }

    private void loctheogia() {
        List<Mathang> sapxep = new ArrayList<Mathang>();
        float dongiatua = 0;
        float dongiatub = 0;
        if(!dongiaa.getText().toString().trim().equalsIgnoreCase("")){
            dongiatua = Float.parseFloat(dongiaa.getText().toString().trim());
        }
        if(!dongiab.getText().toString().trim().equalsIgnoreCase("")){
            dongiatub = Float.parseFloat(dongiab.getText().toString().trim());
        }
        sapxep.clear();
        for (Mathang unit : mathangs){
            if(unit.getDongia()<=dongiatub && unit.getDongia()>=dongiatua){
                sapxep.add(unit);
            }
        }
        rvItems.setAdapter(new AdapterRecyclerViewCreateMH(DanhsachmathangActivity.this,sapxep));

    }

    private void initUiDialog(){
        edtAnhMH = dialog.findViewById(R.id.etAnhMH);
        etMaMH = dialog.findViewById(R.id.etMaMH);
        etTenMH = dialog.findViewById(R.id.etTenMH);
        etDongiaMH = dialog.findViewById(R.id.etDongiaMH);
        etDonvitinhMH = dialog.findViewById(R.id.etDonvitinhMH);
        etSoluongMH = dialog.findViewById(R.id.etSoluongMH);
        etMotaMH = dialog.findViewById(R.id.etMotaMH);

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