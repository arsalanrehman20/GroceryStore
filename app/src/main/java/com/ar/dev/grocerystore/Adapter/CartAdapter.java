package com.ar.dev.grocerystore.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ar.dev.grocerystore.Model.CartModel;
import com.ar.dev.grocerystore.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private DatabaseReference databaseDeleteRef;
    public Context context;
    public List<CartModel> cartModelList;
    public TextView tvTotalPrice;

    public CartAdapter(Context context, List<CartModel> cartModelList, TextView tv) {
        this.context = context;
        this.cartModelList = cartModelList;
        this.tvTotalPrice = tv;
    }

    public CartAdapter(Context context, List<CartModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cart_item_layout, parent, false);
        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {

        final CartModel currentCartModel = cartModelList.get(position);

        final String deleteID = currentCartModel.getProductID();
        holder.tvName.setText(currentCartModel.getProductName());
        holder.tvPrice.setText(currentCartModel.getProductPrice());
        holder.tvQuantity.setText(currentCartModel.getProductQuantity());

        Picasso.get().load(currentCartModel.getImgUrl()).into(holder.imageViewProduct);

        holder.btnDeleteCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseDeleteRef = FirebaseDatabase.getInstance().getReference("Cart");
                databaseDeleteRef.child(deleteID).removeValue();
                CartModel.TOTAL_AMOUNT -= Integer.parseInt(currentCartModel.getProductPrice());
                tvTotalPrice.setText("Rs " + String.valueOf(CartModel.TOTAL_AMOUNT));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        public ImageButton btnDeleteCart;
        public ImageView imageViewProduct;
        public TextView tvName, tvPrice, tvQuantity;

        public CartViewHolder(View itemView) {
            super(itemView);

            btnDeleteCart = itemView.findViewById(R.id.imageButtonDeleteCart);
            imageViewProduct = itemView.findViewById(R.id.imgViewCart);
            tvName = itemView.findViewById(R.id.tvNameCart);
            tvPrice = itemView.findViewById(R.id.tvPriceCart);
            tvQuantity = itemView.findViewById(R.id.tvQuantityCart);
        }
    }
}
