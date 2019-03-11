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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ar.dev.grocerystore.Adapter.ProductAdapter;
import com.ar.dev.grocerystore.CartActivity;
import com.ar.dev.grocerystore.CategoryProductsActivity;
import com.ar.dev.grocerystore.HomeActivity;
import com.ar.dev.grocerystore.Model.CartModel;
import com.ar.dev.grocerystore.Model.ProductModel;
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

public class HomeFragment extends Fragment implements View.OnClickListener {

    private ProgressBar progressBar;
    private ProductAdapter productAdapter;
    private List<ProductModel> fruitList, vegetableList, electronicsList, groceryList,
            personalcareList, clothshoeList, jewelleryList, cosmeticsList;
    private RecyclerView rvFruits, rvVegetables, rvGrocery, rvElectronics,
            rvPersonalCare, rvClothShoe, rvJewellery, rvCosmetics;
    private Button btnFruit, btnVegetables, btnElectronics, btnGrocery,
            btnPersonalCare, btnClothShoe, btnJewellery, btnCosmetics;
    private DatabaseReference databaseRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("Grocery Store");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        progressBar=view.findViewById(R.id.pbHome);
        progressBar.setVisibility(View.VISIBLE);

        databaseRef = FirebaseDatabase.getInstance().getReference("Products");

        btnFruit = view.findViewById(R.id.btnMoreFruits);
        btnVegetables = view.findViewById(R.id.btnMoreVegetables);
        btnElectronics = view.findViewById(R.id.btnMoreElectronics);
        btnGrocery = view.findViewById(R.id.btnMoreGrocery);
        btnPersonalCare = view.findViewById(R.id.btnMorePersonal);
        btnClothShoe = view.findViewById(R.id.btnMoreClothShoe);
        btnJewellery = view.findViewById(R.id.btnMoreJewellery);
        btnCosmetics = view.findViewById(R.id.btnMoreCosmetics);

        btnFruit.setOnClickListener(this);
        btnVegetables.setOnClickListener(this);
        btnElectronics.setOnClickListener(this);
        btnGrocery.setOnClickListener(this);
        btnPersonalCare.setOnClickListener(this);
        btnClothShoe.setOnClickListener(this);
        btnJewellery.setOnClickListener(this);
        btnCosmetics.setOnClickListener(this);

        rvFruits = view.findViewById(R.id.rvFruits);
        rvVegetables = view.findViewById(R.id.rvVegetables);
        rvElectronics = view.findViewById(R.id.rvElectronics);
        rvGrocery = view.findViewById(R.id.rvGrocery);
        rvPersonalCare = view.findViewById(R.id.rvPersonalCare);
        rvClothShoe = view.findViewById(R.id.rvClothShoe);
        rvJewellery = view.findViewById(R.id.rvJewellery);
        rvCosmetics = view.findViewById(R.id.rvCosmetics);

        rvFruits.setHasFixedSize(true);
        rvFruits.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));

        rvVegetables.setHasFixedSize(true);
        rvVegetables.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));

        rvElectronics.setHasFixedSize(true);
        rvElectronics.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));

        rvGrocery.setHasFixedSize(true);
        rvGrocery.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));

        rvPersonalCare.setHasFixedSize(true);
        rvPersonalCare.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));

        rvClothShoe.setHasFixedSize(true);
        rvClothShoe.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));

        rvJewellery.setHasFixedSize(true);
        rvJewellery.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));

        rvCosmetics.setHasFixedSize(true);
        rvCosmetics.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));


        fruitList = new ArrayList<>();
        vegetableList = new ArrayList<>();
        electronicsList = new ArrayList<>();
        groceryList = new ArrayList<>();
        personalcareList = new ArrayList<>();
        clothshoeList = new ArrayList<>();
        jewelleryList = new ArrayList<>();
        cosmeticsList = new ArrayList<>();


        addDataToRecyclerView("Fruits", rvFruits, fruitList);
        addDataToRecyclerView("Vegetables", rvVegetables, vegetableList);
        addDataToRecyclerView("Electronics", rvElectronics, electronicsList);
        addDataToRecyclerView("Grocery", rvGrocery, groceryList);
        addDataToRecyclerView("Personal Care", rvPersonalCare, personalcareList);
        addDataToRecyclerView("Clothes & Shoes", rvClothShoe, clothshoeList);
        addDataToRecyclerView("Jewellery", rvJewellery, jewelleryList);
        addDataToRecyclerView("Cosmetics", rvCosmetics, cosmeticsList);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        },3000);
        return view;
    }

    private void addDataToRecyclerView(String category, final RecyclerView recyclerView, final List<ProductModel> productModelList) {
        databaseRef.child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productModelList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ProductModel productModel = ds.getValue(ProductModel.class);
                    productModelList.add(productModel);
                }
                productAdapter = new ProductAdapter(getContext(), productModelList);
                recyclerView.setAdapter(productAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(getContext(), CategoryProductsActivity.class);

        if (v == btnFruit) {
            intent.putExtra(HomeActivity.CATEGORY_NAME, "Fruits");
            startActivity(intent);

        } else if (v == btnVegetables) {
            intent.putExtra(HomeActivity.CATEGORY_NAME, "Vegetables");
            startActivity(intent);

        } else if (v == btnElectronics) {
            intent.putExtra(HomeActivity.CATEGORY_NAME, "Electronics");
            startActivity(intent);

        } else if (v == btnGrocery) {
            intent.putExtra(HomeActivity.CATEGORY_NAME, "Grocery");
            startActivity(intent);

        } else if (v == btnPersonalCare) {
            intent.putExtra(HomeActivity.CATEGORY_NAME, "Personal Care");
            startActivity(intent);

        } else if (v == btnClothShoe) {
            intent.putExtra(HomeActivity.CATEGORY_NAME, "Clothes & Shoes");
            startActivity(intent);

        } else if (v == btnJewellery) {
            intent.putExtra(HomeActivity.CATEGORY_NAME, "Jewellery");
            startActivity(intent);

        } else if (v == btnCosmetics) {
            intent.putExtra(HomeActivity.CATEGORY_NAME, "Cosmetics");
            startActivity(intent);

        }


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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);
    }
}
