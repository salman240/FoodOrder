package com.example.salmangeforce.food_order.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.salmangeforce.food_order.Interface.ItemClickListener;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ItemClickListener itemClickListener;


    public FoodViewHolder(View itemView) {
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
