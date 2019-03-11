package com.ar.dev.grocerystore.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ar.dev.grocerystore.Adapter.CartAdapter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CartFragment extends Fragment {

    public TextView tvTotalPrice;
    private Button btnConfirmOrder;

    private RecyclerView recyclerViewCart;
    private List<CartModel> cartModelList;
    private CartAdapter cartAdapter;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference fromPath, toPath;
    private DatabaseReference databaseCartRef;

    private String userId, orderId;
    private String id, username, contact, address;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(" Cart");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);


        firebaseAuth = FirebaseAuth.getInstance();
        databaseCartRef = FirebaseDatabase.getInstance().getReference("Cart");

        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnConfirmOrder = view.findViewById(R.id.btnConfirmOrder);

        cartModelList = new ArrayList<>();
        recyclerViewCart = view.findViewById(R.id.recyclerViewCarts);
        recyclerViewCart.setHasFixedSize(true);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseCartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartModelList.clear();
                for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                    CartModel cartModel = cartSnapshot.getValue(CartModel.class);
                    cartModelList.add(cartModel);
                }
                cartAdapter = new CartAdapter(getContext(), cartModelList, tvTotalPrice);
                recyclerViewCart.setAdapter(cartAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        tvTotalPrice.setText("Rs " + String.valueOf(CartModel.TOTAL_AMOUNT));

        btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartAdapter != null) {
                    if (cartAdapter.getItemCount() == 0) {
                        Toast.makeText(getContext(), "Please order something", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                confirmOrder();
                addOrderItemListToDatabase();
                emptyCart();
                orderUserDetails();
            }
        });
        return view;
    }

    private void orderUserDetails() {

        final DatabaseReference databaseUserRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        databaseUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id = dataSnapshot.child("id").getValue().toString();
                username = dataSnapshot.child("name").getValue().toString();
                contact = dataSnapshot.child("contact").getValue().toString();
                address = dataSnapshot.child("address").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseReference databaseOrderUserRef = FirebaseDatabase.getInstance().getReference("Order User Details");
                databaseOrderUserRef.child(orderId).child("Id").setValue(id);
                databaseOrderUserRef.child(orderId).child("Username").setValue(username);
                databaseOrderUserRef.child(orderId).child("Address").setValue(address);
                databaseOrderUserRef.child(orderId).child("Contact").setValue(contact);
            }
        }, 3000);

    }

    private void emptyCart() {
        DatabaseReference databaseEmptyCarRef = FirebaseDatabase.getInstance().getReference();
        databaseEmptyCarRef.child("Cart").removeValue();
        CartModel.TOTAL_AMOUNT = 0;
        tvTotalPrice.setText("Rs " + String.valueOf(CartModel.TOTAL_AMOUNT));
    }

    private void addOrderItemListToDatabase() {
        fromPath = FirebaseDatabase.getInstance().getReference("Cart");
        toPath = FirebaseDatabase.getInstance().getReference("Orders Items").child(orderId);

        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            //  Toast.makeText(getContext(), "Copy Failed", Toast.LENGTH_SHORT).show();
                        } else {
                            // Toast.makeText(getContext(), "Copy Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmOrder() {

        databaseCartRef = FirebaseDatabase.getInstance().getReference("Orders");

        userId = firebaseAuth.getCurrentUser().getUid();
        orderId = databaseCartRef.push().getKey();

        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = dateFormat.format(currentDate);

        OrderModel orderModel = new OrderModel(orderId, formattedDate, "Submitted", String.valueOf(CartModel.TOTAL_AMOUNT));

        databaseCartRef.child(userId).child(orderId).setValue(orderModel);

        Toast.makeText(getContext(), "Order Submitted", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onResume() {
        super.onResume();
        tvTotalPrice.setText("Rs " + String.valueOf(CartModel.TOTAL_AMOUNT));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.cart_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Cart").removeValue();
            CartModel.TOTAL_AMOUNT = 0;
            new Intent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getActivity().finish();
            startActivity(new Intent(getContext(), UserLoginActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
