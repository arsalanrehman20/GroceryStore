package com.ar.dev.grocerystore.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ar.dev.grocerystore.Adapter.OrderAdapter;
import com.ar.dev.grocerystore.CartActivity;
import com.ar.dev.grocerystore.Model.CartModel;
import com.ar.dev.grocerystore.Model.OrderModel;
import com.ar.dev.grocerystore.R;
import com.ar.dev.grocerystore.UserLoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private RecyclerView recyclerViewOrder;
    private List<OrderModel> orderModelList;
    private OrderAdapter orderAdapter;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseOrderRef;

    private String userID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(" Orders");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        databaseOrderRef = FirebaseDatabase.getInstance().getReference("Orders");

        orderModelList = new ArrayList<>();
        recyclerViewOrder = view.findViewById(R.id.recyclerViewOrder);
        recyclerViewOrder.setHasFixedSize(true);
        recyclerViewOrder.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseOrderRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderModelList.clear();
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    OrderModel orderModel = orderSnapshot.getValue(OrderModel.class);
                    orderModelList.add(orderModel);
                }
                orderAdapter = new OrderAdapter(getContext(), orderModelList);
                recyclerViewOrder.setAdapter(orderAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Cart").removeValue();
            CartModel.TOTAL_AMOUNT=0;
            new Intent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getActivity().finish();
            startActivity(new Intent(getContext(), UserLoginActivity.class));
            return true;
        } else if (id == R.id.action_cart) {
            startActivity(new Intent(getContext(), CartActivity.class));
        }
        return super.onOptionsItemSelected(item);

    }
}
