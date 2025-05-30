package com.bookstore.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bookstore.app.R;
import com.bookstore.app.activity.DetailAccountActivity;
import com.bookstore.app.activity.LoginActivity;
import com.bookstore.app.activity.UpdatePasswordActivity;
import com.bookstore.app.model.Cart;
import com.bookstore.app.model.User;
import com.bookstore.app.util.SharedPrefManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;


public class AccountFragment extends Fragment {

    private static final int DETAIL_ACCOUNT_REQUEST_CODE = 1;
    private View mView;
    private TextView txtLogout, txtHoVaTen, txtThongTin, txtDoiMK;
    private ImageView imgAvatar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_account, container, false);

        anhXa();

        loadUserData();

        initLinsenter();

        return mView;
    }

    private void loadUserData() {
        if (SharedPrefManager.getInstance(requireContext()).isLoggedIn()) {
            User user = SharedPrefManager.getInstance(requireContext()).getUser();
            txtHoVaTen.setText(user.getFullName());

        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }
    }


    private void anhXa() {
        txtLogout = mView.findViewById(R.id.txtLogout);
        txtHoVaTen = mView.findViewById(R.id.txtHoVaTen);
        txtThongTin = mView.findViewById(R.id.txtThongTin);
        txtDoiMK = mView.findViewById(R.id.txtDoiMK);
        imgAvatar = mView.findViewById(R.id.imgAvatar);
    }

    private void initLinsenter() {

        txtThongTin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailAccountActivity.class);
                startActivityForResult(intent, DETAIL_ACCOUNT_REQUEST_CODE);
            }
        });

        txtDoiMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdatePasswordActivity.class);
                startActivity(intent);
            }
        });

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(getActivity()).logout();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                Cart cart = new Cart(getActivity());
                cart.clearCart();
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DETAIL_ACCOUNT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            loadUserData();
        }
    }
}