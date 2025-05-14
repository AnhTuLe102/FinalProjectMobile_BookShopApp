package com.bookstore.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bookstore.app.R;
import com.bookstore.app.activity.DetailProductActivity;
import com.bookstore.app.model.Product;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;
import java.util.Locale;

public class RelatedProductAdapter extends RecyclerView.Adapter<RelatedProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public RelatedProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "%.0f₫", product.getPrice()));

        String imageUrl = product.getImages();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Log.d("GlideLoad", "Loading image from: " + imageUrl);

            Glide.with(context)
                    .load(Uri.parse(imageUrl)) // BẮT BUỘC DÙNG Uri.parse để tránh Glide hiểu nhầm là file local
                    .signature(new ObjectKey(System.currentTimeMillis())) // để không bị cache ảnh cũ
                    .into(holder.ivImage);
        }

        // Nếu bạn muốn ẩn phần favorite hoặc review trong danh sách liên quan
        holder.tvRate.setVisibility(View.GONE);
        holder.countReview.setVisibility(View.GONE);

        // Click vào sản phẩm để mở chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailProductActivity.class);
            intent.putExtra("object_product", product);
            intent.putExtra("isGuest", true);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        ShapeableImageView ivImage;
        TextView tvName, tvPrice, tvRate, countReview;
        ImageButton btnFavorite;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.imageProduct);
            tvName = itemView.findViewById(R.id.nameProduct);
            tvPrice = itemView.findViewById(R.id.priceProduct);
            tvRate = itemView.findViewById(R.id.rateReview);
            countReview = itemView.findViewById(R.id.countReview);

        }
    }
}


