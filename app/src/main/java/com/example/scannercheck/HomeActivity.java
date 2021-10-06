package com.example.scannercheck;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    private CardView scanner,dsmh,dsncc,thongke,profile,logout,thongtin,hotro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //homeview
        scanner     = findViewById(R.id.scanner_card);
        dsmh     = findViewById(R.id.dsmh_card);
        dsncc     = findViewById(R.id.dsncc_card);
        thongke     = findViewById(R.id.thongke_card);
        profile     = findViewById(R.id.profile_card);
        logout     = findViewById(R.id.logout_card);
        thongtin     = findViewById(R.id.thongtin_card);
        hotro     = findViewById(R.id.hotro_card);
        //nghe homeview
        scanner.setOnClickListener(this);
        dsmh.setOnClickListener(this);
        dsncc.setOnClickListener(this);
        thongke.setOnClickListener(this);
        profile.setOnClickListener(this);
        logout.setOnClickListener(this);
        thongtin.setOnClickListener(this);
        hotro.setOnClickListener(this);


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
                    break;
                case R.id.nav_dsmh:
                    i1 = new Intent(this, DanhsachmathangActivity.class);startActivity(i1);
                    break;
                case R.id.nav_dsncc:
                    i1 = new Intent(this, DanhsachnhacungcapActivity.class);startActivity(i1);
                    break;
                case R.id.nav_thongke:
                    i1 = new Intent(this, ThongkeActivity.class);startActivity(i1);
                    break;
                case R.id.nav_logout:
                    startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                    Toast.makeText(HomeActivity.this, "Đã đăng xuất",Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.scanner_card:
                i = new Intent(this, ScannerActivity.class);startActivity(i);
                break;
            case R.id.dsmh_card:_card:
                i = new Intent(this, DanhsachmathangActivity.class);startActivity(i);
                break;
            case R.id.dsncc_card:
                i = new Intent(this, DanhsachnhacungcapActivity.class);startActivity(i);
                break;
            case R.id.thongke_card:
                i = new Intent(this, ThongkeActivity.class);startActivity(i);
                break;
            case R.id.profile_card:
                i = new Intent(this, ThongtintaikhoanActivity.class);startActivity(i);
                break;
            case R.id.thongtin_card:
                i = new Intent(this, ThongtinungdungActivity.class);startActivity(i);
                break;
            case R.id.logout_card:
                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                Toast.makeText(HomeActivity.this, "Đã đăng xuất",Toast.LENGTH_SHORT).show();
                break;
            case R.id.hotro_card:
                i = new Intent(this, HotroActivity.class);startActivity(i);
                break;
            default: break;
        }
    }
}