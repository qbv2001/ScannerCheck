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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class DanhsachnhacungcapActivity extends AppCompatActivity {
    private RecyclerView rvItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danhsachnhacungcap);

        // list view

        List<Nhacungcap> nhacungcaps = new ArrayList<>();
        nhacungcaps.add(new Nhacungcap("1","Công ty A","Hà Nội","22",R.drawable.mon1));
        nhacungcaps.add(new Nhacungcap("3","Công ty B","Hà Nội","a",R.drawable.mon3));
        nhacungcaps.add(new Nhacungcap("2","Công ty C","Ba Vì","b",R.drawable.mon2));
        nhacungcaps.add(new Nhacungcap("4","Công ty D","Vĩnh Phúc","c",R.drawable.mon4));
        nhacungcaps.add(new Nhacungcap("5","Công ty E",R.drawable.mon5));
        nhacungcaps.add(new Nhacungcap("6","Công ty F","Ba Vì","b",R.drawable.mon6));
        nhacungcaps.add(new Nhacungcap("7","Công ty G",R.drawable.mon7));
        nhacungcaps.add(new Nhacungcap("8","Công ty H",R.drawable.mon8));


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
        final Dialog dialog = new Dialog(this);
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
        trolai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dongy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DanhsachnhacungcapActivity.this, "Đã gửi",Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
}