//package com.example.scannercheck;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.cardview.widget.CardView;
//import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.google.android.material.appbar.MaterialToolbar;
//import com.google.android.material.navigation.NavigationView;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class ThongkeActivity extends AppCompatActivity {
//
//    private FirebaseUser user;
//    private DatabaseReference dataUser;
//    private DatabaseReference data;
//    NavigationView mNavigationView;
//
//    TextView thongkeMH;
//    TextView thongkeNCC;
//
//    private int tongMH,tongNCC;
//
//    @SuppressLint("SetTextI18n")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_thongke);
//        data = FirebaseDatabase.getInstance().getReference();
//        dataUser = FirebaseDatabase.getInstance().getReference();
//        user = FirebaseAuth.getInstance().getCurrentUser();
//        initUi();
//        tongMH = 0;
//        tongNCC = 0;
//        readDatabaseNCC();
//        readDatabaseMH();
//
//        thongkeMH.setText("Tổng số mặt hàng đã hết: "+tongMH);
//        thongkeNCC.setText("Tổng số nhà cung cấp: "+tongNCC);
//        // sidebar
//        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
//        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
//        NavigationView navigationView = findViewById(R.id.navigation_view);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                drawerLayout.openDrawer(GravityCompat.START);
//
//            }
//        });
//        HomeActivity.sidebar(this, mNavigationView, user, dataUser, drawerLayout);
//    }
//
//    private void readDatabaseMH() {
//        // Read from the database
//        data.child("MatHang").child(user.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                Mathang value = new Mathang();
//                for (DataSnapshot unit : dataSnapshot.getChildren()){
//                    value = unit.getValue(Mathang.class);
//                    if(value.getSoluong()==0){
//                        tongMH++;
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Toast.makeText(ThongkeActivity.this, "Đọc thất bại!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void readDatabaseNCC() {
//        // Read from the database
//        data.child("NhaCungCap").child(user.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                for (DataSnapshot unit : dataSnapshot.getChildren()){
//                    tongNCC++;
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Toast.makeText(ThongkeActivity.this, "Đọc thất bại!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void initUi(){
//        thongkeMH = findViewById(R.id.thongkemathang);
//        thongkeNCC = findViewById(R.id.thongkenhacungcap);
//
//        mNavigationView = findViewById(R.id.navigation_view);
//    }
//}