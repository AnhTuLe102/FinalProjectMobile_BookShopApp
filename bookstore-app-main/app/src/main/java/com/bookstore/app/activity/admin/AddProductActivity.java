package com.bookstore.app.activity.admin;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bookstore.app.R;
import com.bookstore.app.model.BookType;
import com.bookstore.app.response.ProductResponse;
import com.bookstore.app.service.ProductAPIService;
import com.bookstore.app.service.RetrofitClient;
import com.bookstore.app.util.RealPathUtil;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {

    private ProductAPIService productAPIService;
    private ProductResponse productResponse;
    public static final int MY_REQUEST_CODE = 101;
    private Uri mUri;
    ImageView imgChoose;
    EditText edtName, edtPrice, edtDesc;
    Button btnAddImg,btnAdd, btnCancel;
    String name, desc;

    Spinner spnBookType;
    double price;

    private ProgressDialog progressDialog;
    List<BookType> bookTypeList = new ArrayList<>();
    ArrayAdapter<BookType> bookTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_product);

        anhXa();
        loadBookTypes();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");

        initLinsenter();
    }

    private void anhXa() {
        btnCancel = findViewById(R.id.btnCancel);
        btnAdd = findViewById(R.id.btnAdd);
        btnAddImg = findViewById(R.id.btnAddImg);
        imgChoose = findViewById(R.id.imgChooseProImg);
        edtName = findViewById(R.id.edtName);
        edtPrice = findViewById(R.id.edtPrice);
        edtDesc = findViewById(R.id.edtDesc);
        spnBookType = findViewById(R.id.spinnerTheLoai);
    }

    private void initLinsenter() {
        btnAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInfo()) {
                    name = edtName.getText().toString().trim();
                    desc = edtDesc.getText().toString().trim();
                    price = Double.parseDouble(edtPrice.getText().toString().trim());
                    addProduct(name, desc, price);
                }
            }
        });
    }

    private void loadBookTypes() {
        ProductAPIService bookTypeService = RetrofitClient.getRetrofit().create(ProductAPIService.class);
        bookTypeService.getBookTypes().enqueue(new Callback<List<BookType>>() {
            @Override
            public void onResponse(Call<List<BookType>> call, Response<List<BookType>> response) {
                Log.d("Response", "Body: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    bookTypeList = response.body();
                    bookTypeAdapter = new ArrayAdapter<>(AddProductActivity.this, android.R.layout.simple_spinner_item, bookTypeList);
                    bookTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnBookType.setAdapter(bookTypeAdapter);
                    Log.d("BookTypes", "Số lượng: " + bookTypeList.size());
                } else {
                    Log.d("Response", "Response không thành công: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<BookType>> call, Throwable t) {
                Toast.makeText(AddProductActivity.this, "Không thể tải loại sách", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void addProduct(String name, String description, double price) {
        progressDialog.show();

        try {
            // Đọc ảnh thành byte[]
            InputStream inputStream = getContentResolver().openInputStream(mUri);
            byte[] imageBytes = IOUtils.toByteArray(inputStream);
            inputStream.close();

            // Tạo RequestBody cho ảnh
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);

            MultipartBody.Part partbodyimages = MultipartBody.Part.createFormData(
                    "images", "product_image.jpg", requestFile);

            // Các field khác
            BookType selectedBookType = (BookType) spnBookType.getSelectedItem();
            int bookTypeId = selectedBookType.getId();
            RequestBody requestBookTypeId = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(bookTypeId));
            RequestBody requestName = RequestBody.create(MediaType.parse("multipart/form-data"), name);
            RequestBody requestDescription = RequestBody.create(MediaType.parse("multipart/form-data"), description);
            RequestBody requestPrice = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(price));

            // Gọi API
            productAPIService = RetrofitClient.getRetrofit().create(ProductAPIService.class);
            productAPIService.addProduct(requestName, requestDescription, requestPrice, requestBookTypeId, partbodyimages)
                    .enqueue(new Callback<ProductResponse>() {
                        @Override
                        public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                            progressDialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                ProductResponse productResponse = response.body();
                                if (productResponse.isError()) {
                                    Toast.makeText(AddProductActivity.this, productResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddProductActivity.this, productResponse.getMessage(), Toast.LENGTH_LONG).show();
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            } else {
                                Toast.makeText(AddProductActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ProductResponse> call, Throwable t) {
                            progressDialog.dismiss();
                            Log.e("TAG", "Lỗi kết nối: " + t.toString());
                            Toast.makeText(AddProductActivity.this, "Gọi API thất bại", Toast.LENGTH_LONG).show();
                        }
                    });

        } catch (Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();
            Toast.makeText(AddProductActivity.this, "Lỗi xử lý ảnh", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean checkInfo() {
        if ((edtName.getText().toString()).isEmpty() || (edtDesc.getText().toString()).isEmpty() || (edtPrice.getText().toString()).isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mUri == null) {
            Toast.makeText(this, "Vui lòng chọn hình ảnh", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private final ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data == null) {
                            return;
                        }
                        Uri uri = data.getData();
                        mUri = uri;
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            imgChoose.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    public static String[] storge_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storge_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    };

    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storge_permissions_33;
        } else {
            p = storge_permissions;
        }
        return p;
    }

    private void CheckPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }

        boolean granted = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 100);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }


        if (granted) {
            openGallery();
        } else {
            requestPermissions(permissions(), MY_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }

            if (granted) {
                openGallery();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

}