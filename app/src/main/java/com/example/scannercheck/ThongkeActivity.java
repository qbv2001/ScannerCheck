package com.example.scannercheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ThongkeActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference dataUser;
    private DatabaseReference data;
    NavigationView mNavigationView;

    TextView thongkeMH, thongkePN;
    TextView thongkeNCC;

    private int tongMH,tongNCC;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thongke);
        data = FirebaseDatabase.getInstance().getReference();
        dataUser = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        initUi();
        readDatabaseNCC();
        readDatabaseMH();
        thongketiennhaptheothang();
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
    }

    private void readDatabaseMH() {
        // Read from the database
        data.child("MatHang").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                tongMH = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                thongkeMH.setText("Tổng số mặt hàng: "+tongMH);

                Mathang value = new Mathang();
                for (DataSnapshot unit : dataSnapshot.getChildren()){
                    value = unit.getValue(Mathang.class);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(ThongkeActivity.this, "Đọc thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void thongketiennhaptheothang() {

        // Lấy dữ liệu trong khoảng thời gian từ ngày 1 đến ngày cuối của tháng
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR); // Năm cần thống kê
        int month = calendar.get(Calendar.MONTH); // Tháng cần thống kê (từ 0 đến 11)
        calendar.set(year, month, 1, 0, 0, 0);
        long start = calendar.getTimeInMillis();
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, lastDay, 23, 59, 59);
        long end = calendar.getTimeInMillis();

        // Sử dụng truy vấn để lấy đơn hàng trong khoảng thời gian nhất định
        Query query = data.child("PhieuNhap").child(user.getUid()).orderByChild("ngaynhap").startAt(start).endAt(end);

        // Read from the database
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Đếm số lượng đơn hàng và tính tổng giá trị
                int orderCount = 0;
                int totalAmount = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    orderCount++;
                    totalAmount += snapshot.child("thanhtien").getValue(Integer.class);
                }

                // Hiển thị kết quả trên giao diện người dùng
                String result = "Thống kê đơn hàng nhập tháng hiện tại " + (month + 1) + "/" + year + "\n";
                result += "Số đơn hàng nhập: " + orderCount + "\n";
                result += "Tổng giá trị: " + totalAmount + "\n";
                thongkePN.setText(result);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(ThongkeActivity.this, "Đọc thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readDatabaseNCC() {
        // Read from the database
        data.child("NhaCungCap").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                tongNCC = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                thongkeNCC.setText("Tổng số nhà cung cấp: "+tongNCC);

                for (DataSnapshot unit : dataSnapshot.getChildren()){

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(ThongkeActivity.this, "Đọc thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUi(){
        thongkeMH = findViewById(R.id.thongkemathang);
        thongkeNCC = findViewById(R.id.thongkenhacungcap);
        thongkePN = findViewById(R.id.thongkedonhangnhap);
        mNavigationView = findViewById(R.id.navigation_view);
    }
}