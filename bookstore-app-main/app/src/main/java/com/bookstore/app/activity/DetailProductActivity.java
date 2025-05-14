package com.bookstore.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookstore.app.R;
import com.bookstore.app.adapter.RelatedProductAdapter;
import com.bookstore.app.model.Cart;
import com.bookstore.app.model.CartItem;
import com.bookstore.app.model.Product;
import com.bookstore.app.service.ProductAPIService;
import com.bookstore.app.service.RetrofitClient;
import com.bookstore.app.util.SharedPrefManager;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailProductActivity extends AppCompatActivity {
    private ImageView cardViewProduct, back, btnCart;
    private TextView nameProduct, nameBookType;
    private TextView priceProduct;
    private WebView descripProduct;
    private Button addToCart;
    private Product product;
    private Cart cart;
    private RelatedProductAdapter relatedAdapter;

    private boolean isGuest = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail_product);

        cart = new Cart(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle == null)
            return;

        product = (Product) bundle.get("object_product");

        anhXa();
        isGuest = !SharedPrefManager.getInstance(this).isLoggedIn();

        Glide.with(this)
                .load(product.getImages())
                .into(cardViewProduct);

        nameBookType.setText(product.getBookType().getTypeName().toString());
        nameProduct.setText(product.getName());
        priceProduct.setText(product.getPrice()  + " VNĐ");
        descripProduct.loadData(product.getDescription(), "text/html", "UTF-8");

        initListener();

        relatedAdapter = new RelatedProductAdapter(this, new ArrayList<>());
        RecyclerView recyclerRelated = findViewById(R.id.recyclerRelated);
        recyclerRelated.setLayoutManager(new GridLayoutManager(this, 2));

        recyclerRelated.setAdapter(relatedAdapter);


        loadRelatedProducts(product.getBookType().getId());
        Log.d("RelatedProducts", "Product BookType ID: " + product.getBookType().getId());

    }

    private void anhXa() {
        cardViewProduct = findViewById(R.id.imageCard);
        nameProduct = findViewById(R.id.nameDetail);
        nameBookType = findViewById(R.id.txtTheLoai);
        priceProduct = findViewById(R.id.priceDetail);
        descripProduct = findViewById(R.id.descripDetail);
        back = findViewById(R.id.back);
        btnCart = findViewById(R.id.cart);
        addToCart = findViewById(R.id.addToCart);
    }

    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGuest) {
                    showLoginRequiredDialog();
                } else {
                    Intent intent = new Intent(DetailProductActivity.this, CartActivity.class);
                    startActivity(intent);
                }
            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGuest) {
                    showLoginRequiredDialog();
                } else {
                    CartItem cartItem = new CartItem(product.getId(), product, 1);
                    cart.addToCart(cartItem);
                    Toast.makeText(getApplicationContext(), "Added to cart", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadRelatedProducts(int bookTypeId) {
        Log.d("RelatedProducts", "Loading related products for bookTypeId: " + bookTypeId);
        ProductAPIService apiService = RetrofitClient.getRetrofit().create(ProductAPIService.class);
        Call<List<Product>> call = apiService.getProductsByBookType(bookTypeId);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> relatedProducts = response.body();

                    // Loại bỏ sản phẩm hiện tại (dùng vòng lặp để tránh lỗi API level)
                    List<Product> filtered = new ArrayList<>();
                    for (Product p : relatedProducts) {
                        if (p.getId() != product.getId()) {
                            filtered.add(p);
                        }
                    }

                    relatedAdapter.setProductList(filtered);
                    Log.d("RelatedProducts", "Loaded " + relatedProducts.size() + " products");
                }

            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("RelatedProducts", "Error loading related products: " + t.getMessage(), t);
                Toast.makeText(DetailProductActivity.this, "Lỗi khi tải sản phẩm liên quan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoginRequiredDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage("Bạn cần đăng nhập để sử dụng chức năng này.")
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    startActivity(new Intent(this, LoginActivity.class));
                    finish(); // tuỳ chọn, hoặc dùng `startActivityForResult`
                })
                .setNegativeButton("Hủy", null)
                .show();
    }


}
