package com.example.scannercheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    Button btnlogin;
    TextView btn;
    EditText edtEmail,edtPassword;

    ProgressDialog progressDialog;

    ImageView imgShowHidePassword;

    private int failedAttempts = 0; // Khởi tạo biến đếm

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
        signupclick();
        signinclick();
//        showpasswordclick();
    }

    private void initUI(){
        progressDialog = new ProgressDialog(this);
        edtEmail = findViewById(R.id.inputEmail);
        edtPassword = findViewById(R.id.inputPassword);
        btnlogin = findViewById(R.id.btnlogin);
        btn = findViewById(R.id.textViewSignUp);

    }

    private void showpasswordclick(){
        imgShowHidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpassword();
            }
        });
    }

    private void showpassword(){
        if (edtPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            // Nếu đang hiển thị mật khẩu, thì đổi lại về dạng ẩn mật khẩu
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imgShowHidePassword.setImageResource(R.drawable.ic_eye_on);
        } else {
            // Nếu đang ẩn mật khẩu, thì đổi lại về dạng hiển thị mật khẩu
            edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imgShowHidePassword.setImageResource(R.drawable.ic_eye_off);
        }
        edtPassword.setSelection(edtPassword.getText().length()); // Đặt con trỏ nhập vào về cuối
    }

    private void signinclick() {
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkdangnhap();
            }
        });
    }

    private void checkdangnhap() {

        String strEmail = edtEmail.getText().toString().trim();
        String strPassword = edtPassword.getText().toString().trim();

        // Check gia tri
        if(strEmail.equalsIgnoreCase("")){
            Toast.makeText(LoginActivity.this, "Vui lòng nhập Email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(strPassword.equalsIgnoreCase("")){
            Toast.makeText(LoginActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Đăng nhập thành công, đặt lại giá trị của biến đếm
                            failedAttempts = 0;

                            // Sign in success, update UI with the signed-in user's information
                            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công.",
                                    Toast.LENGTH_SHORT).show();
                            finishAffinity();
                        } else {
//                            failedAttempts++;
//                            // Kiểm tra nếu giá trị của biến đếm đạt đến giới hạn
//                            if (failedAttempts >= 5) {
//                                // Hiển thị thông báo yêu cầu người dùng đợi
//                                Toast.makeText(LoginActivity.this, "Bạn đã đăng nhập sai quá số lần cho phép. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
//                                // Khóa nút đăng nhập để ngăn người dùng đăng nhập trong một khoảng thời gian nhất định
//                                btnlogin.setEnabled(false);
//                                // Khởi động một luồng đếm ngược
//                                new CountDownTimer(60000, 1000) {
//                                    public void onTick(long millisUntilFinished) {
//                                        // Hiển thị thời gian còn lại trên giao diện
//                                        btnlogin.setText("Thử lại sau " + millisUntilFinished / 1000 + " giây");
//                                    }
//                                    public void onFinish() {
//                                        // Đếm ngược kết thúc, đặt lại giá trị của biến đếm và mở khóa nút đăng nhập
//                                        failedAttempts = 0;
//                                        btnlogin.setEnabled(true);
//                                        btnlogin.setText("LOGIN");
//                                    }
//                                }.start();
//                            } else {
//                                // Hiển thị thông báo đăng nhập thất bại
//                                Toast.makeText(LoginActivity.this, "Tên đăng nhập hoặc mật khẩu không chính xác. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
//                            }
                                    Toast.makeText(LoginActivity.this, "Tên đăng nhập hoặc mật khẩu không chính xác. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signupclick() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }
}
