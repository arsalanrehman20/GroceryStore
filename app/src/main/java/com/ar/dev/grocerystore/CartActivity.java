package com.ar.dev.grocerystore;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ar.dev.grocerystore.Adapter.CartAdapter;
import com.ar.dev.grocerystore.Model.CartModel;
import com.ar.dev.grocerystore.Model.OrderModel;
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

public class CartActivity extends AppCompatActivity {

    public TextView tvTotalPrice;
    private Button btnConfirmOrder;

    private RecyclerView recyclerViewCart;
    private List<CartModel> cartModelList;
    private CartAdapter cartAdapter;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference fromPath, toPath;
    private DatabaseReference databaseCartRef;

    private String userId, orderId;
    private String id,username, contact, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(" Cart");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.drawable.icon_cart_white);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseCartRef = FirebaseDatabase.getInstance().getReference("Cart");

        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);

        cartModelList = new ArrayList<>();
        recyclerViewCart = findViewById(R.id.recyclerViewCarts);
        recyclerViewCart.setHasFixedSize(true);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));

        databaseCartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartModelList.clear();
                for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                    CartModel cartModel = cartSnapshot.getValue(CartModel.class);
                    cartModelList.add(cartModel);
                }
                cartAdapter = new CartAdapter(getApplicationContext(), cartModelList,tvTotalPrice);
                recyclerViewCart.setAdapter(cartAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        tvTotalPrice.setText("Rs " + String.valueOf(CartModel.TOTAL_AMOUNT));

        btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartAdapter != null) {
                    if (cartAdapter.getItemCount() == 0) {
                        Toast.makeText(getApplicationContext(), "Please order something", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                confirmOrder();
                addOrderItemListToDatabase();
                emptyCart();
                orderUserDetails();
            }
        });
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
        },3000);

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
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

        Toast.makeText(getApplicationContext(), "Order Submitted", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onResume() {
        super.onResume();
        tvTotalPrice.setText("Rs " + String.valueOf(CartModel.TOTAL_AMOUNT));
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
