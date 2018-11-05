package com.example.salmangeforce.food_order;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.salmangeforce.food_order.Common.Common;
import com.example.salmangeforce.food_order.Database.Database;
import com.example.salmangeforce.food_order.Model.Food;
import com.example.salmangeforce.food_order.Model.Order;
import com.example.salmangeforce.food_order.Model.Rating;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FoodDetailActivity extends AppCompatActivity implements View.OnClickListener, RatingDialogListener {

    TextView food_name, food_price, food_description;
    ImageView img_food;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton floatingActionButton;
    ElegantNumberButton elegantNumberButton;
    Food currentFood;

    String foodId;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference foods;
    DatabaseReference ratings;
    private FloatingActionButton floatingActionButtonRating;
    private RatingBar ratingBar;
    private int ratingValue = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Firebase init
        firebaseDatabase = FirebaseDatabase.getInstance();
        foods = firebaseDatabase.getReference("Food");
        ratings = firebaseDatabase.getReference("Rating");

        //init views
        elegantNumberButton = findViewById(R.id.btn_price);
        floatingActionButton = findViewById(R.id.btnCart);
        floatingActionButtonRating = findViewById(R.id.btnRating);
        collapsingToolbarLayout = findViewById(R.id.collapsing);
        food_name = findViewById(R.id.food_name);
        food_price = findViewById(R.id.food_price);
        food_description = findViewById(R.id.food_description);
        img_food = findViewById(R.id.img_food);
        ratingBar = findViewById(R.id.food_rating);


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
        floatingActionButtonRating.setOnClickListener(this);

        setRatingBarValue();
    }

    //floating button click listener
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
        else if(view.getId() == R.id.btnRating)
        {
            showRatingDialog();
        }
    }


    //rating dialog listener
    @Override
    public void onNegativeButtonClicked() {
        Toast.makeText(this, "You provide no rating", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNeutralButtonClicked() {
        //No neutral button
    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {
        final Rating rating =  new Rating(String.valueOf(i), s, foodId);
        ratings.child(Common.currentUser.getPhone()).child(foodId).setValue(rating);
        Toast.makeText(this, "Thank you for rating food", Toast.LENGTH_SHORT).show();
    }


    //show rating dialog
    private void showRatingDialog() {
            new AppRatingDialog.Builder()
                    .setPositiveButtonText("Submit")
                    .setNegativeButtonText("Cancel")
                    .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                    .setDefaultRating(ratingValue)
                    .setTitle("Rate this food")
                    .setDescription("Please select some stars and give your feedback")
                    .setCommentInputEnabled(true)
                    .setStarColor(R.color.yellow)
                    .setNoteDescriptionTextColor(R.color.colorAccent)
                    .setTitleTextColor(R.color.colorAccent)
                    .setDescriptionTextColor(R.color.colorAccent)
                    .setHint("Please write your comment here ...")
                    .setHintTextColor(R.color.gray)
                    .setCommentTextColor(R.color.white)
                    .setCommentBackgroundColor(R.color.colorPrimaryDark)
                    .setWindowAnimation(R.style.MyDialogFadeAnimation)
                    .setCancelable(false)
                    .setCanceledOnTouchOutside(false)
                    .create(FoodDetailActivity.this)
                    .show();
    }


    //Helper Methods
    private void getDetailFood(final String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
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


    private void setRatingBarValue() {
        ratings.child(Common.currentUser.getPhone()).orderByChild("foodId").equalTo(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Rating rating = dataSnapshot.child(foodId).getValue(Rating.class);
                if(rating != null)
                {
                    ratingBar.setRating(Float.parseFloat(rating.getRating()));
                    ratingValue = Integer.parseInt(rating.getRating());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}//class ends

