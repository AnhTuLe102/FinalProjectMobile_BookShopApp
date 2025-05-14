package com.bookstore.app.fragment;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookstore.app.R;
import com.bookstore.app.activity.CartActivity;
import com.bookstore.app.activity.LoginActivity;
import com.bookstore.app.activity.MainActivity;
import com.bookstore.app.adapter.CategoryAdapter;
import com.bookstore.app.adapter.ProductAdapter;
import com.bookstore.app.model.CategoryModel;
import com.bookstore.app.model.Product;
import com.bookstore.app.service.ProductAPIService;
import com.bookstore.app.service.RetrofitClient;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    ProductAPIService productAPIService;
    private View mView;
    private ViewFlipper adFlipper;
    private RecyclerView rvProduct;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private ImageButton cart;

    private androidx.appcompat.widget.SearchView searchView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        anhXa();
        // Set up RecyclerView
        rvProduct = mView.findViewById(R.id.productRV);
        productList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvProduct.setLayoutManager(linearLayoutManager);

        // Call the API to load products
        loadAllProduct();
        setupViewFlipper();
        initListener();
        List<CategoryModel> categoryList = Arrays.asList(
                new CategoryModel(-1, "Tất cả"),
                new CategoryModel(1, "Khoa học"),
                new CategoryModel(2, "Truyện tranh"),
                new CategoryModel(3, "SGK"),
                new CategoryModel(4, "Viễn tưởng"),
                new CategoryModel(5, "Trẻ em"),
                new CategoryModel(6, "Kinh dị"),
                new CategoryModel(7, "Chính kịch"),
                new CategoryModel(8, "Lãng mạn"),
                new CategoryModel(9, "Tài liệu"),
                new CategoryModel(10, "Selfhelp"),
                new CategoryModel(11, "Lịch sử"),
                new CategoryModel(12, "Sức khỏe"),
                new CategoryModel(13, "Trinh thám"),
                new CategoryModel(14, "Kiến thức"),
                new CategoryModel(15, "Tiểu thuyết")
        );
        RecyclerView categoryRecyclerView = mView.findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        CategoryAdapter adapter = new CategoryAdapter(categoryList, categoryId -> {
            // GỌI API hoặc filter danh sách sản phẩm nổi bật theo categoryId
            if(categoryId == -1){
                loadAllProduct();
            }else {
                filterProductByCategory(categoryId);
            }
        });

        categoryRecyclerView.setAdapter(adapter);

        return mView;
    }

    private void filterProductByCategory(int categoryId) {
        List<Product> categoryProductList = new ArrayList<>();
        for (Product i : productList) {
            int filterId = i.getBookTypeById();  // Normalize the product name
            if (categoryId == filterId) {
                categoryProductList.add(i);
            }
        }
        productAdapter = new ProductAdapter(getActivity(), categoryProductList);
        rvProduct.setAdapter(productAdapter);
    }

    private void anhXa() {
        cart = mView.findViewById(R.id.cart);
        searchView = mView.findViewById(R.id.searchView);
        adFlipper = mView.findViewById(R.id.adFlipper);


    }

    private void initListener() {
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the user is a guest
                if (isGuest()) {
                    showLoginRequiredDialog();
                } else {
                    Intent intent = new Intent(getActivity(), CartActivity.class);
                    startActivity(intent);
                }
            }
        });

        // Tìm kiếm
        androidx.appcompat.widget.SearchView.OnQueryTextListener listener = new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Xử lý khi người dùng nhấn nút "Tìm kiếm"
                searchProduct(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newQuery) {
                // Xử lý khi người dùng thay đổi truy vấn tìm kiếm
                searchProduct(newQuery);
                return false;
            }
        };

        searchView.setOnQueryTextListener(listener);
    }
    // Check if the user is a guest by accessing MainActivity
    private boolean isGuest() {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            return mainActivity.getIntent().getBooleanExtra("isGuest", false);
        }
        return false; // Default to non-guest if activity is not MainActivity
    }

    // Show login required dialog
    private void showLoginRequiredDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Thông báo")
                .setMessage("Bạn cần đăng nhập để sử dụng chức năng này.")
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    if (getActivity() != null) {
                        getActivity().finish(); // Optional, or use startActivityForResult
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void setupViewFlipper() {
        // Hardcode danh sách ảnh quảng cáo
        int[] adImages = {
                R.drawable.ad_banner_1, // banner
                R.drawable.ad_banner_2,
                R.drawable.ad_banner_3,
                R.drawable.ad_banner_4
        };

        // Thêm ảnh vào ViewFlipper
        for (int adImage : adImages) {
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(adImage);
            adFlipper.addView(imageView);
        }

        // Thiết lập animation cho ViewFlipper
        adFlipper.setInAnimation(getContext(), android.R.anim.slide_in_left);
        adFlipper.setOutAnimation(getContext(), android.R.anim.slide_out_right);
        adFlipper.setFlipInterval(3000); // 3 giây
        adFlipper.setAutoStart(true); // Tự động chuyen
    }

    private void searchProduct(String query) {
        List<Product> searchProductList = new ArrayList<>();
        String normalizedQuery = normalizeString(query);  // Normalize the query
        for (Product i : productList) {
            String normalizedProductName = normalizeString(i.getName());  // Normalize the product name
            if (normalizedProductName.contains(normalizedQuery)) {
                searchProductList.add(i);
            }
        }
        productAdapter = new ProductAdapter(getActivity(), searchProductList);
        rvProduct.setAdapter(productAdapter);
    }

    private String normalizeString(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").toLowerCase();
    }



    private void loadAllProduct() {

        productAPIService = RetrofitClient.getRetrofit().create(ProductAPIService.class);
        productAPIService.loadAllProduct().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                productList = response.body();
                productAdapter = new ProductAdapter(getActivity(),productList);
                rvProduct.setAdapter(productAdapter);
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable throwable) {
                Toast.makeText(getActivity(),"Lỗi rồi", Toast.LENGTH_SHORT).show();
            }
        });
    }


}