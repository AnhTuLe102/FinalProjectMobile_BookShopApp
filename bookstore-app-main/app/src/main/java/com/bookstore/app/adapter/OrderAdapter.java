package com.bookstore.app.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bookstore.app.R;
import com.bookstore.app.activity.DetailOrderActivity;
import com.bookstore.app.model.Order;
import com.bookstore.app.model.OrderItem;
import com.bookstore.app.model.User;
import com.bookstore.app.service.OrderAPIService;
import com.bookstore.app.service.RetrofitClient;
import com.bookstore.app.util.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private OrderAPIService orderAPIService;
    private final Context context;
    private final List<Order> orderList;
    private final OnOrderCancelListener cancelListener;

    public OrderAdapter(Context context, List<Order> orderList, OnOrderCancelListener cancelListener) {
        this.context = context;
        this.orderList = orderList;
        this.cancelListener = cancelListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item, parent, false);
        return new MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView idOrder;
        TextView priceOrder;
        TextView countItem;
        TextView dateOrder;
        Button btnDetail, btnCancel, btnConfirm, btnDelivery, btnCompleted;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            idOrder = itemView.findViewById(R.id.idOrder);
            priceOrder = itemView.findViewById(R.id.priceOrder);
            dateOrder = itemView.findViewById(R.id.dateOrder);
            countItem = itemView.findViewById(R.id.countItem);
            btnDetail = itemView.findViewById(R.id.btnDetail);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            btnDelivery = itemView.findViewById(R.id.btnDelivery);
            btnCompleted = itemView.findViewById(R.id.btnCompleted);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = SharedPrefManager.getInstance(context).getUser();



        Order order = orderList.get(position);
        holder.idOrder.setText("Mã đơn hàng: " + order.getId());

        if (order.getOrderTrack().getId() == 1) {
            holder.btnDelivery.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnConfirm.setVisibility(View.VISIBLE);
            holder.btnCompleted.setVisibility(View.GONE);
        } else if (order.getOrderTrack().getId() == 2) {
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnDelivery.setVisibility(View.VISIBLE);
            holder.btnCompleted.setVisibility(View.GONE);
        } else if (order.getOrderTrack().getId() == 3) {
            holder.btnDelivery.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCompleted.setVisibility(View.GONE);
        }   else if (order.getOrderTrack().getId() == 4) {
            holder.btnDelivery.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCompleted.setVisibility(View.VISIBLE);
        } else if (order.getOrderTrack().getId() == 5) {
            holder.btnDelivery.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCompleted.setVisibility(View.GONE);
        }

        if (user.getRole().getId() == 1) {
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnDelivery.setVisibility(View.GONE);
        }

        if (user.getRole().getId() == 3 || user.getRole().getId() == 2) {
            holder.btnCompleted.setVisibility(View.GONE);
        }

        List<OrderItem> orderItems = order.getOrderItems();
        holder.countItem.setText(orderItems.size() + " items");

        holder.dateOrder.setText(order.getDate());

        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += (int) (orderItem.getPrice() * orderItem.getQuantity());
        }
        holder.priceOrder.setText(totalPrice + " VNĐ");

        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailOrderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("obiect_order", order);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xác nhận hủy");
                builder.setMessage("Bạn có chắc chắn muốn hủy đơn hàng có mã " + order.getId() + " không?");
                builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nếu người dùng chọn "Xác nhận", thực hiện hủy đơn hàng
                        updateStatus(order.getId(), 3);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nếu người dùng chọn "Hủy", đóng dialog
                        dialog.dismiss();
                    }
                });
                // Hiển thị dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        holder.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xác nhận đơn hàng");
                builder.setMessage("Bạn có chắc chắn muốn xác nhận đơn hàng có mã " + order.getId() + " không?");
                builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nếu người dùng chọn "Xác nhận", thực hiện xác nhận đơn hàng
                        updateStatus(order.getId(), 2);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nếu người dùng chọn "Hủy", đóng dialog
                        dialog.dismiss();
                    }
                });
                // Hiển thị dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        holder.btnDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xác nhận giao cho đơn vị vận chuyển");
                builder.setMessage("Bạn có chắc chắn muốn xác nhận đa giao cho đơn vị vận chuyển đơn hàng có mã " + order.getId() + " không?");
                builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nếu người dùng chọn "Xác nhận", thực hiện xác nhận đơn hàng
                        updateStatus(order.getId(), 4);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nếu người dùng chọn "Hủy", đóng dialog
                        dialog.dismiss();
                    }
                });
                // Hiển thị dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        holder.btnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xác nhận đã nhận được hàng");
                builder.setMessage("Bạn có chắc chắn muốn xác nhận đã nhận được đơn hàng có mã " + order.getId() + " không?");
                builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nếu người dùng chọn "Xác nhận", thực hiện xác nhận đơn hàng
                        updateStatus(order.getId(), 5);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nếu người dùng chọn "Hủy", đóng dialog
                        dialog.dismiss();
                    }
                });
                // Hiển thị dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 :orderList.size();
    }

    private void updateStatus(int orderId, int orderTrackId) {
        orderAPIService = RetrofitClient.getRetrofit().create(OrderAPIService.class);
        orderAPIService.updateStatus(orderId, orderTrackId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()) {
                    Order order = response.body();
                    if (orderTrackId == 2) {
                        Toast.makeText(context, "Đã xác nhận đơn hàng có mã " + order.getId(), Toast.LENGTH_SHORT).show();
                    } else if (orderTrackId == 4) {
                        Toast.makeText(context, "Đã xác nhận vận chuyển đơn hàng có mã " + order.getId(), Toast.LENGTH_SHORT).show();
                    } else if (orderTrackId == 5) {
                        Toast.makeText(context, "Đã nhận được đơn hàng có mã " + order.getId(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Đã hủy đơn hàng có mã " + order.getId(), Toast.LENGTH_SHORT).show();
                    }
                    cancelListener.onOrderCancelled(); // Notify the fragment
                } else {
                    // Xử lý khi gọi API không thành công
                    int statusCode = response.code();
                    Log.e("API Error", "Status code: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface OnOrderCancelListener {
        void onOrderCancelled();
    }
}
