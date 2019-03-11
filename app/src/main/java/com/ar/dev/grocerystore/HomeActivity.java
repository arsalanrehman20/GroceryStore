package com.ar.dev.grocerystore;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ar.dev.grocerystore.Fragments.CartFragment;
import com.ar.dev.grocerystore.Fragments.HomeFragment;
import com.ar.dev.grocerystore.Fragments.OrdersFragment;
import com.ar.dev.grocerystore.Fragments.ProfileFragment;
import com.ar.dev.grocerystore.Model.CartModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private TextView tvNameNav, tvEmailNav;

    public static final String CATEGORY_NAME = "categoryname";

    public NavigationView navigationView;

    public ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        tvNameNav = headerView.findViewById(R.id.tvNameNAV);
        tvEmailNav = headerView.findViewById(R.id.tvEmailNAV);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new HomeFragment())
                    .commit();

            navigationView.getMenu().getItem(0).setChecked(true);
            navigationView.setCheckedItem(0);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, new HomeFragment())
                .commit();

        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setCheckedItem(0);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (firebaseAuth.getCurrentUser() == null) {
            Intent i = new Intent(getApplicationContext(), UserLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(i);
        }
        databaseReference.child(firebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tvNameNav.setText(dataSnapshot.child("name").getValue().toString());
                        tvEmailNav.setText(dataSnapshot.child("email").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(HomeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Exit");
            builder.setMessage("Your cart will get empty. Are you sure you want to exit?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CartModel.TOTAL_AMOUNT = 0;
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            android.app.AlertDialog dialog = builder.create();
            dialog.show();

            Button positiveButton = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);

            positiveButton.setTextColor(Color.parseColor("#22334d"));
            negativeButton.setTextColor(Color.parseColor("#22334d"));
            // super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_logout) {
//            firebaseAuth.signOut();
//
//            new Intent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            finish();
//            startActivity(new Intent(getApplicationContext(), UserLoginActivity.class));
//            return true;
//        }else if(id==R.id.action_cart){
//            startActivity(new Intent(this,CartActivity.class));
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        emptyCart();

    }

    private void emptyCart(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Cart").removeValue();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent = new Intent(this, CategoryProductsActivity.class);

        int id = item.getItemId();


        if (id == R.id.nav_home) {
            actionBar.setLogo(null);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new HomeFragment())
                    .commit();

        } else if (id == R.id.nav_my_profile) {
            actionBar.setLogo(R.drawable.icon_profile_white);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new ProfileFragment())
                    .commit();

        } else if (id == R.id.nav_my_orders) {
            actionBar.setLogo(R.drawable.icon_order_white);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new OrdersFragment())
                    .commit();

        } else if (id == R.id.nav_my_cart) {
            actionBar.setLogo(R.drawable.icon_cart_white);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new CartFragment())
                    .commit();

        } else if (id == R.id.nav_logout) {
            emptyCart();
            firebaseAuth.signOut();
            finish();
            new Intent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(new Intent(this, UserLoginActivity.class));

        } else if (id == R.id.nav_fruits) {
            intent.putExtra(CATEGORY_NAME, "Fruits");
            startActivity(intent);

        } else if (id == R.id.nav_vegetables) {
            intent.putExtra(CATEGORY_NAME, "Vegetables");
            startActivity(intent);

        } else if (id == R.id.nav_electronics) {
            intent.putExtra(CATEGORY_NAME, "Electronics");
            startActivity(intent);

        } else if (id == R.id.nav_grocery) {
            intent.putExtra(CATEGORY_NAME, "Grocery");
            startActivity(intent);

        } else if (id == R.id.nav_personal_care) {
            intent.putExtra(CATEGORY_NAME, "Personal Care");
            startActivity(intent);

        } else if (id == R.id.nav_clothes_shoes) {
            intent.putExtra(CATEGORY_NAME, "Clothes & Shoes");
            startActivity(intent);

        } else if (id == R.id.nav_jewellery) {
            intent.putExtra(CATEGORY_NAME, "Jewellery");
            startActivity(intent);

        } else if (id == R.id.nav_cosmetics) {
            intent.putExtra(CATEGORY_NAME, "Cosmetics");
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
