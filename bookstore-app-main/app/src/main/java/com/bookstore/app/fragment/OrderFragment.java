package com.bookstore.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookstore.app.R;
import com.bookstore.app.adapter.OrderAdapter;
import com.bookstore.app.model.Order;
import com.bookstore.app.model.User;
import com.bookstore.app.service.OrderAPIService;
import com.bookstore.app.service.RetrofitClient;
import com.bookstore.app.util.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFragment extends Fragment implements OrderAdapter.OnOrderCancelListener {

    private RecyclerView rcPROCESSING, rcCONFIRMED, rcCANCELED, rcDELIVERING, rcCOMPLETED;
    private OrderAPIService orderAPIService;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private View mView;
    private TabHost orderTab;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_order, container, false);

        user = SharedPrefManager.getInstance(getActivity()).getUser();

        anhXa();
        initView();
        loadData();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onOrderCancelled() {
        loadData();
    }

    public void loadData() {
        getListOrderProcessing(user.getId());
        getListOrderConfirmed(user.getId());
        getListOrderCanceled(user.getId());
        getListOrderDelivery(user.getId());
        getListOrderCompleted(user.getId());
    }

    private void initView() {
        rcPROCESSING = mView.findViewById(R.id.rcPROCESSING);
        rcCONFIRMED = mView.findViewById(R.id.rcCONFIRMED);
        rcCANCELED = mView.findViewById(R.id.rcCANCELED);
        rcDELIVERING = mView.findViewById(R.id.rcDELIVERING);
        rcCOMPLETED = mView.findViewById(R.id.rcCOMPLETED);
    }

    private void anhXa() {
        orderTab = mView.findViewById(R.id.orderTap);
        orderTab.setup();
        TabHost.TabSpec spec1, spec2, spec3, spec4, spec5;

        spec1 = orderTab.newTabSpec("t1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Chờ xác nhận");
        orderTab.addTab(spec1);

        spec2 = orderTab.newTabSpec("t2");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Đã xác nhận");
        orderTab.addTab(spec2);

        spec3 = orderTab.newTabSpec("t3");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator("Đã hủy");
        orderTab.addTab(spec3);

        spec4 = orderTab.newTabSpec("t4");
        spec4.setContent(R.id.tab4);
        spec4.setIndicator("Đang giao hàng");
        orderTab.addTab(spec4);

        spec5 = orderTab.newTabSpec("t5");
        spec5.setContent(R.id.tab5);
        spec5.setIndicator("Đã hoàn thành");
        orderTab.addTab(spec5);
    }

    private void getListOrderProcessing(int userId) {
        orderAPIService = RetrofitClient.getRetrofit().create(OrderAPIService.class);
        orderAPIService.getOrdersByUserIdAndOrderTrackId(userId, 1).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful()) {
                    orderList = response.body();

                    orderAdapter = new OrderAdapter(getActivity(), orderList, OrderFragment.this);
                    rcPROCESSING.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                    rcPROCESSING.setAdapter(orderAdapter);
                } else {
                    Toast.makeText(getActivity(), "Failed to fetch orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getListOrderConfirmed(int userId) {
        orderAPIService = RetrofitClient.getRetrofit().create(OrderAPIService.class);
        orderAPIService.getOrdersByUserIdAndOrderTrackId(userId, 2).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful()) {
                    orderList = response.body();

                    orderAdapter = new OrderAdapter(getActivity(), orderList, OrderFragment.this);
                    rcCONFIRMED.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                    rcCONFIRMED.setAdapter(orderAdapter);
                } else {
                    Toast.makeText(getActivity(), "Failed to fetch orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getListOrderCanceled(int userId) {
        orderAPIService = RetrofitClient.getRetrofit().create(OrderAPIService.class);
        orderAPIService.getOrdersByUserIdAndOrderTrackId(userId, 3).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful()) {
                    orderList = response.body();

                    orderAdapter = new OrderAdapter(getActivity(), orderList, OrderFragment.this);
                    rcCANCELED.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                    rcCANCELED.setAdapter(orderAdapter);
                } else {
                    Toast.makeText(getActivity(), "Failed to fetch orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getListOrderDelivery(int userId) {
        orderAPIService = RetrofitClient.getRetrofit().create(OrderAPIService.class);
        orderAPIService.getOrdersByUserIdAndOrderTrackId(userId, 4).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful()) {
                    orderList = response.body();

                    orderAdapter = new OrderAdapter(getActivity(), orderList, OrderFragment.this);
                    rcDELIVERING.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                    rcDELIVERING.setAdapter(orderAdapter);
                } else {
                    Toast.makeText(getActivity(), "Failed to fetch orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getListOrderCompleted(int userId) {
        orderAPIService = RetrofitClient.getRetrofit().create(OrderAPIService.class);
        orderAPIService.getOrdersByUserIdAndOrderTrackId(userId, 5).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful()) {
                    orderList = response.body();

                    orderAdapter = new OrderAdapter(getActivity(), orderList, OrderFragment.this);
                    rcCOMPLETED.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                    rcCOMPLETED.setAdapter(orderAdapter);
                } else {
                    Toast.makeText(getActivity(), "Failed to fetch orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
