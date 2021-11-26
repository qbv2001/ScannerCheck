package com.example.scannercheck;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DanhsachnhacungcapActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView rvItems;
    private Dialog dialog;
    private DatabaseReference datanhacungap;
    private FirebaseUser user;
    private List<Nhacungcap> nhacungcaps;
    private EditText etMaNCC,etTenNCC,etDiachiNCC,etSdtNCC,etMota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danhsachnhacungcap);
        user = FirebaseAuth.getInstance().getCurrentUser();
        datanhacungap = FirebaseDatabase.getInstance().getReference();
        // list view
        nhacungcaps = new ArrayList<>();

        initUi();
        readDatabase();

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
                    break;
                case R.id.nav_dsmh:
                    i1 = new Intent(this, DanhsachmathangActivity.class);startActivity(i1);
                    break;
                case R.id.nav_dsncc:
                    break;
                case R.id.nav_thongke:
                    i1 = new Intent(this, ThongkeActivity.class);startActivity(i1);
                    break;
                case R.id.nav_logout:
                    startActivity(new Intent(DanhsachnhacungcapActivity.this,LoginActivity.class));
                    Toast.makeText(DanhsachnhacungcapActivity.this, "Đã đăng xuất",Toast.LENGTH_SHORT).show();
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
        initUiDialog();

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
        dialog.show();
    }


    private void onClickPushData() {
        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        String MaNCC = timeStamp;
        String TenNCC = etTenNCC.getText().toString().trim();
        String DiachiNCC = etDiachiNCC.getText().toString().trim();
        String SdtNCC = etSdtNCC.getText().toString().trim();
        String mota = etMota.getText().toString().trim();
        int image = 1;

        Nhacungcap nhacungcap = new Nhacungcap(MaNCC, TenNCC, mota, DiachiNCC, SdtNCC, R.drawable.mon3);

        datanhacungap.child("NhaCungCap").child(user.getUid()).child(MaNCC).setValue(nhacungcap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                dialog.dismiss();
                Toast.makeText(DanhsachnhacungcapActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void readDatabase(){

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

    private void initUi(){
        searchView = findViewById(R.id.search_view);
    }
    private void initUiDialog(){
        etTenNCC = dialog.findViewById(R.id.etTenNCC);
        etDiachiNCC = dialog.findViewById(R.id.etDiachiNCC);
        etSdtNCC = dialog.findViewById(R.id.etSdtNCC);
        etMota = dialog.findViewById(R.id.etMotaNCC);

    }

}