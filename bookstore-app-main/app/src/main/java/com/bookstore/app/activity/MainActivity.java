package com.bookstore.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bookstore.app.R;
import com.bookstore.app.adapter.ViewPagerAdapter;
import com.bookstore.app.transformer.ZoomOutPageTransformer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 mViewPager2;
    private BottomNavigationView mBottomNavigationView;

    private boolean isGuest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        isGuest = getIntent().getBooleanExtra("isGuest", false);

        mViewPager2 = findViewById(R.id.view_pager_2);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);

        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(this);
        mViewPager2.setAdapter(mViewPagerAdapter);

        if (isGuest) {
            mViewPager2.setUserInputEnabled(false);
        }

        mBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.bottom_home) {
                    mViewPager2.setCurrentItem(0);
                    return true;
                } else if (id == R.id.bottom_order || id == R.id.bottom_account) {
                    if (isGuest) {
                        showLoginRequiredDialog();
                        return false;
                    } else {
                        mViewPager2.setCurrentItem(id == R.id.bottom_order ? 1 : 2);
                        return true;
                    }
                }
                return false;
            }
        });

        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mBottomNavigationView.getMenu().findItem(R.id.bottom_home).setChecked(true);
                        break;
                    case 1:
                        mBottomNavigationView.getMenu().findItem(R.id.bottom_order).setChecked(true);
                        break;
                    case 2:
                        mBottomNavigationView.getMenu().findItem(R.id.bottom_account).setChecked(true);
                        break;
                }
            }
        });

        mViewPager2.setPageTransformer(new ZoomOutPageTransformer());
    }

    private void showLoginRequiredDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage("Bạn cần đăng nhập để sử dụng chức năng này.")
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish(); // tuỳ chọn, hoặc dùng `startActivityForResult`
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

}