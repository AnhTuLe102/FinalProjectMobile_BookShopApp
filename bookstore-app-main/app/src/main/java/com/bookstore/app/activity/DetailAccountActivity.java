package com.bookstore.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bookstore.app.R;
import com.bookstore.app.model.User;
import com.bookstore.app.util.SharedPrefManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

public class DetailAccountActivity extends AppCompatActivity {

    private static final int UPDATE_INFO_REQUEST_CODE = 1;
    private static final int UPDATE_IMAGES_REQUEST_CODE = 2;
    private TextView txtDiaChi, txtHoVaTen, txtGender, txtEmail, txtPhone, btnUpdate;
    private ImageView imgBack, imgAvatar;

    private int checkResultCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail_account);

        anhXa();

        loadUserData();

        initListener();
    }

    private void loadUserData() {
        User user = SharedPrefManager.getInstance(this).getUser();
        txtDiaChi.setText(user.getAddress());
        txtHoVaTen.setText(user.getFullName());
        txtGender.setText(user.getGender());
        txtEmail.setText(user.getEmail());
        txtPhone.setText(user.getPhone());

        String imagePath = user.getImages();
        String imageUrl;

        if (imagePath.startsWith("http")) {
            imageUrl = imagePath; // là URL đầy đủ rồi
        } else {
            imageUrl = "http://10.0.2.2:8080" + (imagePath.startsWith("/") ? "" : "/") + imagePath;
        }

        Log.d("ImageURL", "kiemtra URL: " + user.getImages());

        Glide.with(getApplicationContext())
                .load(imageUrl)
                .signature(new ObjectKey(System.currentTimeMillis()))
                .into(imgAvatar);


    }

    private void anhXa() {
        txtDiaChi = findViewById(R.id.txtDiaChi);
        txtHoVaTen = findViewById(R.id.txtHoVaTen);
        txtGender = findViewById(R.id.txtGender);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        btnUpdate = findViewById(R.id.btnUpdate);
        imgBack = findViewById(R.id.imgBack);
        imgAvatar = findViewById(R.id.imgAvatar);
    }

    private void initListener() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkResultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                }
                finish();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailAccountActivity.this, UpdateInfoActivity.class);
                startActivityForResult(intent, UPDATE_INFO_REQUEST_CODE);
            }
        });

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailAccountActivity.this, UpdateImagesActivity.class);
                startActivityForResult(intent, UPDATE_IMAGES_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == UPDATE_INFO_REQUEST_CODE || requestCode == UPDATE_IMAGES_REQUEST_CODE) && resultCode == RESULT_OK) {
            // Load lại dữ liệu khi trở về từ UpdateInfoActivity hoặc UpdateImagesActivity
            loadUserData();
        }
        checkResultCode = resultCode;
    }
}