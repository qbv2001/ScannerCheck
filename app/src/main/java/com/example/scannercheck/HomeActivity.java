package com.example.scannercheck;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private CardView scanner,dsmh,dsncc,thongke,profile,logout,thongtin,hotro;

    NavigationView mNavigationView;

    static CircleImageView imgprofilepic;
    TextView tvname;
    TextView tvuseremail;

    private FirebaseUser user;
    private DatabaseReference dataUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        user = FirebaseAuth.getInstance().getCurrentUser();
        dataUser = FirebaseDatabase.getInstance().getReference();
        initUI();
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

        //sidebar
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerLayout.openDrawer(GravityCompat.START);

            }
        });

        sidebar(this, mNavigationView, tvname, tvuseremail, user, dataUser, drawerLayout);

    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.scanner_card:
                i = new Intent(this, ScannerActivity.class);startActivity(i);
                break;
            case R.id.dsmh_card:
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
            case R.id.hotro_card:
                i = new Intent(this, HotroActivity.class);startActivity(i);
                break;
            case R.id.logout_card:
                FirebaseAuth.getInstance().signOut();
                i = new Intent(this, LoginActivity.class);startActivity(i);
                Toast.makeText(HomeActivity.this, "Đã đăng xuất",Toast.LENGTH_SHORT).show();
                finish();
                break;
            default: break;
        }
    }

    private void initUI(){
        mNavigationView = findViewById(R.id.navigation_view);
        imgprofilepic = mNavigationView.getHeaderView(0).findViewById(R.id.profilepic);
        tvname = mNavigationView.getHeaderView(0).findViewById(R.id.name);
        tvuseremail = mNavigationView.getHeaderView(0).findViewById(R.id.useremail);
    }

    public static void sidebar(Context activity, NavigationView mNavigationView, TextView tvname, TextView tvuseremail, FirebaseUser user, DatabaseReference dataUser, DrawerLayout drawerLayout) {

        // showuserinfo
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
//        Glide.with(activity).load(photoUrl).error(R.drawable.profilepic).into(imgprofilepic);

        // Read from the database
        dataUser.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // showuserinfo
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
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        //select item
        mNavigationView.setNavigationItemSelectedListener(item -> {
            Intent i1;
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);
            switch (id)
            {
                case R.id.nav_home:
                    if (activity.equals(HomeActivity.class))
                        break;
                    else {
                        i1 = new Intent(activity, HomeActivity.class);activity.startActivity(i1);
                        break;
                    }
                case R.id.nav_dsmh:
                    if (activity.equals(DanhsachmathangActivity.class))
                        break;
                    else {
                        i1 = new Intent(activity, DanhsachmathangActivity.class);activity.startActivity(i1);
                        break;
                    }
                case R.id.nav_dsncc:
                    if (activity.equals(DanhsachnhacungcapActivity.class))
                        break;
                    else {
                        i1 = new Intent(activity, DanhsachnhacungcapActivity.class);activity.startActivity(i1);
                        break;
                    }
                case R.id.nav_thongke:
                    if (activity.equals(ThongkeActivity.class))
                        break;
                    else {
                        i1 = new Intent(activity, ThongkeActivity.class);activity.startActivity(i1);
                        break;
                    }
                case R.id.nav_logout:
                    FirebaseAuth.getInstance().signOut();
                    activity.startActivity(new Intent(activity,LoginActivity.class));
                    Toast.makeText(activity, "Đã đăng xuất",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.nav_hotro:
                    if (activity.equals(HotroActivity.class))
                        break;
                    else {
                        i1 = new Intent(activity, HotroActivity.class);activity.startActivity(i1);
                        break;
                    }
                case R.id.nav_ttud:
                    if (activity.equals(ThongtinungdungActivity.class))
                        break;
                    else {
                        i1 = new Intent(activity, ThongtinungdungActivity.class);activity.startActivity(i1);
                        break;
                    }
                case R.id.nav_ttcn:
                    if (activity.equals(ThongtinungdungActivity.class))
                        break;
                    else {
                        i1 = new Intent(activity, ThongtintaikhoanActivity.class);activity.startActivity(i1);
                        break;
                    }
                default:
                    return true;

            }
            return true;
        });
    }
}