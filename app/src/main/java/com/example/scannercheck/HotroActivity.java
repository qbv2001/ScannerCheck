package com.example.scannercheck;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class HotroActivity extends AppCompatActivity {
    private EditText edtCode;
    private Button taocode;
    private ImageView qr_code,mavach_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotro);
        
//        initUi();

        
    }

//    private void initUi() {
//        edtCode = findViewById(R.id.edtCode);
//        taocode = findViewById(R.id.taocode);
//        qr_code = findViewById(R.id.qr_code);
//        mavach_code = findViewById(R.id.mavach_code);
//
//        // onclick
//        taocode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getCode();
//            }
//        });
//    }
//
//    private void getCode() {
//        try {
//            qrcode();
//            barcode();
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    // tao ham qrcode
//    MultiFormatWriter multiFormatWriter= new MultiFormatWriter();
//
//    public void qrcode() throws WriterException {
//
//        BitMatrix bitMatrix = multiFormatWriter.encode(edtCode.getText().toString(), BarcodeFormat.QR_CODE, 350, 300);
//        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
//        qr_code.setImageBitmap(bitmap);
//    }
//
//    private void barcode() throws WriterException {
//        BitMatrix bitMatrix = multiFormatWriter.encode(edtCode.getText().toString(), BarcodeFormat.CODE_128, 400, 170,null);
//        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
//        mavach_code.setImageBitmap(bitmap);
//    }


}