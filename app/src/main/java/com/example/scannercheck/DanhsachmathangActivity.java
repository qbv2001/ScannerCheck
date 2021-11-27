package com.example.scannercheck;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DanhsachmathangActivity extends AppCompatActivity {
    public static final int DEFAULT_VIEW = 0x22;

    private static final int REQUEST_CODE_SCAN = 0X01;

    private RecyclerView rvItems;
    private SearchView searchView;
    private EditText etMaMH,etTenMH,etDongiaMH,etDonvitinhMH,etSoluongMH,etNhaccMH,etMotaMH;

    private Dialog dialog;
    private DatabaseReference datamathang;
    private FirebaseUser user;
    List<Mathang> mathangs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danhsachmathang);

        // list view
        mathangs = new ArrayList<>();

        user = FirebaseAuth.getInstance().getCurrentUser();
        datamathang = FirebaseDatabase.getInstance().getReference();

        initUi();
        readDatabase();
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
                    break;
                case R.id.nav_dsmh:
                    break;
                case R.id.nav_dsncc:
                    i1 = new Intent(this, DanhsachnhacungcapActivity.class);startActivity(i1);
                    break;
                case R.id.nav_thongke:
                    i1 = new Intent(this, ThongkeActivity.class);startActivity(i1);
                    break;
                case R.id.nav_logout:
                    startActivity(new Intent(DanhsachmathangActivity.this,LoginActivity.class));
                    Toast.makeText(DanhsachmathangActivity.this, "Đã đăng xuất",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.nav_hotro:
                    i1 = new Intent(this, HotroActivity.class);startActivity(i1);
                    break;
                case R.id.nav_ttud:
                    i1 = new Intent(this, ThongtinungdungActivity.class);startActivity(i1);
                    break;
                case R.id.nav_scanner:
                    i1 = new Intent(this, ScannerActivity.class);startActivity(i1);
                    break;
                case R.id.nav_ttcn:
                    i1 = new Intent(this, ThongtintaikhoanActivity.class);startActivity(i1);
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

        initUiDialog();
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
        String TenMH = etTenMH.getText().toString().trim();
        String DonvitinhMH = etDonvitinhMH.getText().toString().trim();
        float DongiaMH = Float.parseFloat(etDongiaMH.getText().toString().trim());
        int SoluongMH = Integer.parseInt(etSoluongMH.getText().toString().trim());
        String NhaccMH = etNhaccMH.getText().toString().trim();
        Date date = new Date();
        String datetime = ""+date;
        String mota = etMotaMH.getText().toString().trim();
        int image = 1;

        Mathang mathang = new Mathang(MaMH, TenMH, SoluongMH, DongiaMH, datetime, R.drawable.mon1, DonvitinhMH, mota, NhaccMH);

        datamathang.child("MatHang").child(user.getUid()).child(MaMH).setValue(mathang, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                dialog.dismiss();
                Toast.makeText(DanhsachmathangActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void readDatabase(){

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
                    mathangs.add(value);
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
            ScanUtil.startScan(DanhsachmathangActivity.this, REQUEST_CODE_SCAN, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());
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
    }

    private void initUi(){
        searchView = findViewById(R.id.search_view);
    }
    private void initUiDialog(){
        etMaMH = dialog.findViewById(R.id.etMaMH);
        etTenMH = dialog.findViewById(R.id.etTenMH);
        etDongiaMH = dialog.findViewById(R.id.etDongiaMH);
        etDonvitinhMH = dialog.findViewById(R.id.etDonvitinhMH);
        etSoluongMH = dialog.findViewById(R.id.etSoluongMH);
        etNhaccMH = dialog.findViewById(R.id.etNhaccMH);
        etMotaMH = dialog.findViewById(R.id.etMotaMH);

    }

}