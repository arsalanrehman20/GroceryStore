package com.ar.dev.grocerystore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ar.dev.grocerystore.Model.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPass;
    private EditText etAddress, etCity, etPinCode, etContact;
    private Button btnRegister;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        progressBar = findViewById(R.id.pbReg);
        progressBar.setVisibility(View.INVISIBLE);
        etName = findViewById(R.id.etRegName);
        etEmail = findViewById(R.id.etRegEmail);
        etPass = findViewById(R.id.etRegPass);
        etAddress = findViewById(R.id.etRegAddress);
        etCity = findViewById(R.id.etRegCity);
        etPinCode = findViewById(R.id.etRegPinCode);
        etContact = findViewById(R.id.etRegContact);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegistration();
            }
        });
    }

    private void userRegistration() {

        final String name = etName.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String pass = etPass.getText().toString().trim();
        final String address = etAddress.getText().toString().trim();
        final String city = etCity.getText().toString().trim();
        final String pincode = etPinCode.getText().toString().trim();
        final String contact = etContact.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(address)
                || TextUtils.isEmpty(city) || TextUtils.isEmpty(pincode) || TextUtils.isEmpty(contact)) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String id = firebaseAuth.getCurrentUser().getUid();
                        UserModel userModel = new UserModel(id, name, email, pass,
                                address, city, pincode, contact);
                        databaseReference.child(id).setValue(userModel);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegistrationActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(getApplicationContext(), UserLoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
