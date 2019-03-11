package com.ar.dev.grocerystore.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ar.dev.grocerystore.Model.CartModel;
import com.ar.dev.grocerystore.Model.ProductModel;
import com.ar.dev.grocerystore.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Filterable {

    private DatabaseReference databaseCartRef;
    public Context context;
    public List<ProductModel> productModelsList;
    private List<ProductModel> productModelListFull;

    public ProductAdapter(Context context, List<ProductModel> productModelsList) {
        this.context = context;
        this.productModelsList = productModelsList;

        productModelListFull=new ArrayList<>(productModelsList);

    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.product_item_layout, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, final int position) {

        databaseCartRef = FirebaseDatabase.getInstance().getReference("Cart");

        final ProductModel currentProductModel = productModelsList.get(position);
        holder.tvNameRV.setText(currentProductModel.getName());
        holder.tvPriceRV.setText(currentProductModel.getPrice());
        Picasso.get().load(currentProductModel.getImgUrl1())
                .into(holder.imgViewRV);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ImageView im1, im2, im3, im4, imBig;
                TextView tvName, tvPrice, tvDesc;
                final EditText etQuantity;
                ImageView imgIncrease, imgDecrease;
                ImageButton imgCancel;
                final Button btnAddtoCart;

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.add_product_dialogue);

                tvName = dialog.findViewById(R.id.tvNameProductDialogue);
                tvPrice = dialog.findViewById(R.id.tvPriceProductDialogue);
                tvDesc = dialog.findViewById(R.id.tvDescProductDialogue);
                etQuantity = dialog.findViewById(R.id.etQuantityProductDialogue);
                im1 = dialog.findViewById(R.id.ivProductDialogue1);
                im2 = dialog.findViewById(R.id.ivProductDialogue2);
                im3 = dialog.findViewById(R.id.ivProductDialogue3);
                im4 = dialog.findViewById(R.id.ivProductDialogue4);
                imBig = dialog.findViewById(R.id.imageViewBigProductDialogue);
                imgIncrease = dialog.findViewById(R.id.ivIncreaseQuantity);
                imgDecrease = dialog.findViewById(R.id.ivDecreaseQuantity);
                btnAddtoCart = dialog.findViewById(R.id.btnAddToCart);
                imgCancel=dialog.findViewById(R.id.imgCutDialog);

                tvName.setText(currentProductModel.getName());
                tvPrice.setText(currentProductModel.getPrice());
                tvDesc.setText(currentProductModel.getDesc());
                etQuantity.setText("1");

                Picasso.get().load(currentProductModel.getImgUrl1()).into(im1);
                Picasso.get().load(currentProductModel.getImgUrl2()).into(im2);
                Picasso.get().load(currentProductModel.getImgUrl3()).into(im3);
                Picasso.get().load(currentProductModel.getImgUrl4()).into(im4);
                Picasso.get().load(currentProductModel.getImgUrl1()).into(imBig);
                dialog.show();

                imgCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                imgIncrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quantity = Integer.parseInt(etQuantity.getText().toString());
                        quantity++;
                        etQuantity.setText(String.valueOf(quantity));
                    }
                });

                imgDecrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quantity = Integer.parseInt(etQuantity.getText().toString());
                        if (quantity != 1)
                            quantity--;
                        etQuantity.setText(String.valueOf(quantity));
                    }
                });

                im1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.get().load(currentProductModel.getImgUrl1()).into(imBig);
                    }
                });
                im2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.get().load(currentProductModel.getImgUrl2()).into(imBig);
                    }
                });
                im3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.get().load(currentProductModel.getImgUrl3()).into(imBig);
                    }
                });
                im4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.get().load(currentProductModel.getImgUrl4()).into(imBig);
                    }
                });

                btnAddtoCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String cartProductID = databaseCartRef.push().getKey();
                        String productName = currentProductModel.getName();
                        String productQuantity = etQuantity.getText().toString().trim();
                        int totalPrice = Integer.parseInt(productQuantity) * Integer.parseInt(currentProductModel.getPrice());

                        CartModel.TOTAL_AMOUNT+=totalPrice;

                        CartModel cartModel = new CartModel(cartProductID,productName,String.valueOf(totalPrice),productQuantity,currentProductModel.getImgUrl1());

                        databaseCartRef.child(cartProductID).setValue(cartModel);
                        Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return productModelsList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView imgViewRV;
        public TextView tvNameRV, tvPriceRV;

        public ProductViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardview_id);
            imgViewRV = itemView.findViewById(R.id.imgProduct);
            tvNameRV = itemView.findViewById(R.id.tvNameProduct);
            tvPriceRV = itemView.findViewById(R.id.tvPriceProduct);


        }
    }

    @Override
    public Filter getFilter() {
        return productFilter;
    }
    private Filter productFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ProductModel> filteredList = new ArrayList<>();

            if(constraint==null || constraint.length()==0){
                filteredList.addAll(productModelListFull);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(ProductModel productItem : productModelListFull){
                    if(productItem.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(productItem);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values=filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            productModelsList.clear();
            productModelsList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}
