package com.example.salmangeforce.food_order.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.salmangeforce.food_order.Interface.ItemClickListener;
import com.example.salmangeforce.food_order.Model.Order;
import com.example.salmangeforce.food_order.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>{

    private List<Order> orders;
    private Context context;
    private ItemClickListener itemClickListener;

    public CartAdapter(Context context, List<Order> orders, ItemClickListener itemClickListener) {
        this.orders = orders;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewItemName, textViewItemPrice;
        private ImageView imageView;
        ViewHolder(View itemView) {
            super(itemView);

            textViewItemName = itemView.findViewById(R.id.cart_item_name);
            textViewItemPrice = itemView.findViewById(R.id.cart_item_price);
            imageView = itemView.findViewById(R.id.cart_item_image);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onclick(view, viewHolder.getAdapterPosition(), false);
            }
        });

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewItemName.setText(orders.get(position).getProductName());

        Locale locale = new Locale("en", "US");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        int price = Integer.parseInt(orders.get(position).getPrice()) * Integer.parseInt(orders.get(position).getQuantity())
            - Integer.parseInt(orders.get(position).getDiscount()) * Integer.parseInt(orders.get(position).getQuantity());
        holder.textViewItemPrice.setText(numberFormat.format(price));

        TextDrawable textDrawable = TextDrawable.builder().buildRound(orders.get(position).getQuantity(), Color.RED);
        holder.imageView.setImageDrawable(textDrawable);
    }


    @Override
    public int getItemCount() {
        return orders.size();
    }

}
