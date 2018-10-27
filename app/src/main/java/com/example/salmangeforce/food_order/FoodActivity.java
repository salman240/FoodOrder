package com.example.salmangeforce.food_order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salmangeforce.food_order.Interface.ItemClickListener;
import com.example.salmangeforce.food_order.Model.Food;
import com.example.salmangeforce.food_order.ViewHolders.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FoodActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference foods;
    FirebaseRecyclerAdapter adapter;
    RecyclerView recyclerViewFood;
    String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        //init firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        foods = firebaseDatabase.getReference("Food");

        //get categoryId from intent passed from HomeActivity
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");

        recyclerViewFood = findViewById(R.id.recycler_food);
        recyclerViewFood.setHasFixedSize(true);
        recyclerViewFood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        loadFoods();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    //Helper Method
    private void loadFoods() {
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>().
                setQuery(foods.orderByChild("MenuId").equalTo(categoryId), Food.class).build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull final Food model) {
                TextView textViewName = holder.itemView.findViewById(R.id.food_name);
                ImageView imageView = holder.itemView.findViewById(R.id.food_image);

                textViewName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(imageView);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onclick(View view, int position, boolean isLongClick) {
                        //Sending food_id to FoodDetailActivity
                        Intent intent = new Intent(FoodActivity.this, FoodDetailActivity.class);
                        intent.putExtra("foodId", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }
        };

        recyclerViewFood.setAdapter(adapter);
    }

}//class ends
