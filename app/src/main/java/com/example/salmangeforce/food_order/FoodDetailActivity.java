package com.example.salmangeforce.food_order;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.salmangeforce.food_order.Database.Database;
import com.example.salmangeforce.food_order.Model.Food;
import com.example.salmangeforce.food_order.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetailActivity extends AppCompatActivity implements View.OnClickListener {

    TextView food_name, food_price, food_description;
    ImageView img_food;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton floatingActionButton;
    ElegantNumberButton elegantNumberButton;
    Food currentFood;

    String foodId;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Firebase init
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Food");

        //init views
        elegantNumberButton = findViewById(R.id.btn_price);
        floatingActionButton = findViewById(R.id.btnCart);
        collapsingToolbarLayout = findViewById(R.id.collapsing);
        food_name = findViewById(R.id.food_name);
        food_price = findViewById(R.id.food_price);
        food_description = findViewById(R.id.food_description);
        img_food = findViewById(R.id.img_food);

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.colapsedAppbar);

        //get food_id from FoodActivity
        if (getIntent() != null) {
            foodId = getIntent().getStringExtra("foodId");
        }

        if(!foodId.isEmpty())
        {
            getDetailFood(foodId);
        }

        floatingActionButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnCart)
        {
            Database database = new Database(this);
            Order order = new Order(foodId, currentFood.getName(), elegantNumberButton.getNumber() , currentFood.getPrice(),
                    currentFood.getDiscount());
            database.addToCart(order);
            Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();
        }
    }

    //Helper Methods
    private void getDetailFood(final String foodId) {
        databaseReference.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);

                assert currentFood != null;
                Picasso.get().load(currentFood.getImage()).into(img_food);
                collapsingToolbarLayout.setTitle(currentFood.getName());

                food_name.setText(currentFood.getName());
                food_price.setText(currentFood.getPrice());
                food_description.setText(currentFood.getDescription());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
