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
import java.util.List;

public class DanhsachmathangActivity extends AppCompatActivity {
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
//        mathangs.add(new Mathang("1","Bánh quy",22,date,R.drawable.mon1));
//        mathangs.add(new Mathang("2","Coca",R.drawable.mon2));
//        mathangs.add(new Mathang("3","Pepsi",33,date,R.drawable.mon3));
//        mathangs.add(new Mathang("4","Bia Ha Noi",44,date,R.drawable.mon4));
//        mathangs.add(new Mathang("5","Bia 333",R.drawable.mon5));
//        mathangs.add(new Mathang("6","Bim bim Swing",R.drawable.mon6));
//        mathangs.add(new Mathang("7","Mì hảo hảo",R.drawable.mon7));
//        mathangs.add(new Mathang("8","Mì omachi",R.drawable.mon8));

        user = FirebaseAuth.getInstance().getCurrentUser();
        datamathang = FirebaseDatabase.getInstance().getReference();

        initUi();
        readDatabase();
        rvItems = findViewById(R.id.recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvItems.setLayoutManager(layoutManager);
        rvItems.setHasFixedSize(true);
        rvItems.setAdapter(new AdapterRecyclerViewCreateMH(this,mathangs));

        readDatabase();
        //set onclick on item


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
        String datetime = "30/01/2001";
        String mota = etMotaMH.getText().toString().trim();
        int image = 1;

        Mathang mathang = new Mathang(MaMH, TenMH, SoluongMH, DongiaMH, datetime, R.drawable.mon1, DonvitinhMH, NhaccMH, mota);

        datamathang.child("MatHang").child(user.getUid()).child(etMaMH.getText().toString()).setValue(mathang);
        Toast.makeText(DanhsachmathangActivity.this, "Thanh cong", Toast.LENGTH_SHORT).show();

    }
    private void readDatabase(){

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("test2");

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
                
                Toast.makeText(DanhsachmathangActivity.this, "Value is: " + value.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(DanhsachmathangActivity.this, "Đọc thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
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