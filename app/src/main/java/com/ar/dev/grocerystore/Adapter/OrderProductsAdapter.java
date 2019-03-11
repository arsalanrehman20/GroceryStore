package com.ar.dev.grocerystore.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ar.dev.grocerystore.Model.OrderItemsModel;
import com.ar.dev.grocerystore.R;

import java.util.List;

public class OrderProductsAdapter extends RecyclerView.Adapter<OrderProductsAdapter.OrderProductViewHolder> {

    private Context context;
    private List<OrderItemsModel> orderItemsModelList;

    public OrderProductsAdapter(Context context, List<OrderItemsModel> orderItemsModelList) {
        this.context = context;
        this.orderItemsModelList = orderItemsModelList;
    }

    @NonNull
    @Override
    public OrderProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.show_order_products_layout, parent, false);
        return new OrderProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderProductViewHolder holder, int position) {
        OrderItemsModel currentOrderItemModel = orderItemsModelList.get(position);
        holder.tvProductName.setText(currentOrderItemModel.getProductName());
        holder.tvProductPrice.setText(currentOrderItemModel.getProductPrice());
        holder.tvProductQuantity.setText(currentOrderItemModel.getProductQuantity());
    }

    @Override
    public int getItemCount() {
        return orderItemsModelList.size();
    }


    public class OrderProductViewHolder extends RecyclerView.ViewHolder {

        public TextView tvProductName, tvProductPrice, tvProductQuantity;

        public OrderProductViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvNameList);
            tvProductPrice = itemView.findViewById(R.id.tvPriceList);
            tvProductQuantity= itemView.findViewById(R.id.tvQuantityList);
        }
    }
}
