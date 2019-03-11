package com.ar.dev.grocerystore.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ar.dev.grocerystore.CartActivity;
import com.ar.dev.grocerystore.Model.CartModel;
import com.ar.dev.grocerystore.R;
import com.ar.dev.grocerystore.UserLoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private EditText etName, etEmail, etPass;
    private EditText etAddress, etCity, etPinCode, etContact;
    private Button btnUpdateProfile;
    private ProgressBar progressBar;

    String userID;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(" User Profile");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        progressBar = view.findViewById(R.id.pbUpdate);
        progressBar.setVisibility(View.INVISIBLE);
        etName = view.findViewById(R.id.etUpdateName);
        etEmail = view.findViewById(R.id.etUpdateEmail);
        etPass = view.findViewById(R.id.etUpdatePass);
        etAddress = view.findViewById(R.id.etUpdateAddress);
        etCity = view.findViewById(R.id.etUpdateCity);
        etPinCode = view.findViewById(R.id.etUpdatePinCode);
        etContact = view.findViewById(R.id.etUpdateContact);
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);

        etEmail.setEnabled(false);

        databaseReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                etName.setText(dataSnapshot.child("name").getValue().toString());
                etEmail.setText(dataSnapshot.child("email").getValue().toString());
                etPass.setText(dataSnapshot.child("pass").getValue().toString());
                etAddress.setText(dataSnapshot.child("address").getValue().toString());
                etCity.setText(dataSnapshot.child("city").getValue().toString());
                etPinCode.setText(dataSnapshot.child("pincode").getValue().toString());
                etContact.setText(dataSnapshot.child("contact").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
        return view;
    }

    private void updateProfile() {
        String name = etName.getText().toString();
        String pass = etPass.getText().toString();
        String address = etAddress.getText().toString();
        String city = etCity.getText().toString();
        String pincode = etPinCode.getText().toString();
        String contact = etContact.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(address)
                || TextUtils.isEmpty(city) || TextUtils.isEmpty(pincode) || TextUtils.isEmpty(contact)) {
            Toast.makeText(getContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.child(userID).child("name").setValue(name);
        databaseReference.child(userID).child("pass").setValue(pass);
        databaseReference.child(userID).child("address").setValue(address);
        databaseReference.child(userID).child("city").setValue(city);
        databaseReference.child(userID).child("pincode").setValue(pincode);
        databaseReference.child(userID).child("contact").setValue(contact);

        Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
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
