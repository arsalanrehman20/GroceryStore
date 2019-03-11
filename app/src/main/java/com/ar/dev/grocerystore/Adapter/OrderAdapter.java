package com.ar.dev.grocerystore.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ar.dev.grocerystore.Model.OrderItemsModel;
import com.ar.dev.grocerystore.Model.OrderModel;
import com.ar.dev.grocerystore.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private RecyclerView recyclerViewShowOrderProduct;
    private List<OrderItemsModel> orderItemsModelList;
    private OrderProductsAdapter orderProductsAdapter;
    private DatabaseReference databaseOrderProductRef;
    private FirebaseAuth firebaseAuth;
    private String userId;

    public View view;
    private Context context;
    private List<OrderModel> orderModelList;

    public OrderAdapter(Context context, List<OrderModel> orderModelList) {
        this.context = context;
        this.orderModelList = orderModelList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.order_item_layout, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        final OrderModel currentOrderModel = orderModelList.get(position);
        String orderID = currentOrderModel.getOrderID();
        String orderAmount = currentOrderModel.getOrderAmount();
        String orderDate = currentOrderModel.getOrderDate();
        String orderStatus = currentOrderModel.getOrderStatus();

        holder.tvOrderId.setText(orderID);
        holder.tvOrderPrice.setText(orderAmount);
        holder.tvOrderDate.setText(orderDate);
        holder.tvOrderStatus.setText(orderStatus);

        holder.tvShowOrderProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.show_order_product_dialog);

                firebaseAuth = FirebaseAuth.getInstance();
                userId = firebaseAuth.getCurrentUser().getUid();

                databaseOrderProductRef = FirebaseDatabase.getInstance().getReference("Orders Items");

                orderItemsModelList = new ArrayList<>();

                recyclerViewShowOrderProduct = dialog.findViewById(R.id.recyclerViewOrderProducts);
                recyclerViewShowOrderProduct.setHasFixedSize(true);
                recyclerViewShowOrderProduct.setLayoutManager(new LinearLayoutManager(context));

                databaseOrderProductRef.child(currentOrderModel.getOrderID())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                orderItemsModelList.clear();
                                for (DataSnapshot orderItemModelSnapshot : dataSnapshot.getChildren()) {
                                    OrderItemsModel orderItemsModel = orderItemModelSnapshot.getValue(OrderItemsModel.class);
                                    orderItemsModelList.add(orderItemsModel);
                                }
                                orderProductsAdapter = new OrderProductsAdapter(context, orderItemsModelList);
                                recyclerViewShowOrderProduct.setAdapter(orderProductsAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderModelList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        public TextView tvShowOrderProducts;
        public TextView tvOrderId, tvOrderDate, tvOrderPrice, tvOrderStatus;

        public OrderViewHolder(View itemView) {
            super(itemView);

            tvOrderId = itemView.findViewById(R.id.tvOrderID);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderPrice = itemView.findViewById(R.id.tvOrderPrice);
            tvShowOrderProducts = itemView.findViewById(R.id.tvShowOrderProducts);
        }
    }
}
