package com.example.salmangeforce.food_order.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.salmangeforce.food_order.Interface.ItemClickListener;
import com.example.salmangeforce.food_order.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ItemClickListener itemClickListener;


    public MenuViewHolder(View itemView) {
        super(itemView);

        itemView.setOnClickListener(this);
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onclick(view, getAdapterPosition(), false);
    }
}
