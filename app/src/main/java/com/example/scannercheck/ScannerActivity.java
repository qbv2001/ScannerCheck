package com.example.scannercheck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import java.util.Date;

public class ScannerActivity extends AppCompatActivity {
    public static final int DEFAULT_VIEW = 0x22;

    private static final int REQUEST_CODE_SCAN = 0X01;

    private EditText etMaMH,etTenMH,etDongiaMH,etDonvitinhMH,etSoluongMH,etNhaccMH,etMotaMH;

    private Dialog dialog;
    private DatabaseReference datamathang;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        user = FirebaseAuth.getInstance().getCurrentUser();
        datamathang = FirebaseDatabase.getInstance().getReference();

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
            ScanUtil.startScan(ScannerActivity.this, REQUEST_CODE_SCAN, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());
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
                    // Read from the database
                    datamathang.child("MatHang").child(user.getUid()).child(((HmsScan) obj).getOriginalValue()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(ScannerActivity.this, "Đọc dữ liệu thất bại", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(ScannerActivity.this, ((HmsScan) obj).getOriginalValue(), Toast.LENGTH_SHORT).show();
//                                Mathang value = new Mathang();
//                                for (DataSnapshot unit : task.getResult().getChildren()){
//                                    value = unit.getValue(Mathang.class);
//                                    if(value.getId()==((HmsScan) obj).getOriginalValue()){
//                                    }
//                                }
                            }
                        }
                    });
//                    Toast.makeText(ScannerActivity.this, ((HmsScan) obj).getOriginalValue(), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void openFeedbackDialog(int gravity){
        dialog = new Dialog(ScannerActivity.this);
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

        TextView trolai   = dialog.findViewById(R.id.trolai);
        Button dongy      = dialog.findViewById(R.id.dongy);

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

        Mathang mathang = new Mathang(MaMH, TenMH, SoluongMH, DongiaMH, datetime, R.drawable.mon1, DonvitinhMH, NhaccMH, mota);

        datamathang.child("MatHang").child(user.getUid()).child(MaMH).setValue(mathang, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                dialog.dismiss();
                Toast.makeText(ScannerActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
            }
        });

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