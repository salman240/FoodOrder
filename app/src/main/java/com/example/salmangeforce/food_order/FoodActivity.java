package com.example.salmangeforce.food_order;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salmangeforce.food_order.Database.Database;
import com.example.salmangeforce.food_order.Interface.ItemClickListener;
import com.example.salmangeforce.food_order.Model.Food;
import com.example.salmangeforce.food_order.ViewHolders.FoodViewHolder;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.SimpleOnSearchActionListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class FoodActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference foods;
    FirebaseRecyclerAdapter adapter;
    FirebaseRecyclerAdapter searchAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerViewFood;
    String categoryId;

    Database database;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if(ShareDialog.canShow(SharePhotoContent.class)) {
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    List<String> suggested;
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        //fb share
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        swipeRefreshLayout = findViewById(R.id.refreshFood);

        //init firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        foods = firebaseDatabase.getReference("Food");
        database = new Database(this);

         suggested = new ArrayList<>();
         materialSearchBar = findViewById(R.id.searchBar);
         materialSearchBar.setCardViewElevation(10);

        //get categoryId from intent passed from HomeActivity
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");

        recyclerViewFood = findViewById(R.id.recycler_food);
        recyclerViewFood.setHasFixedSize(true);
        recyclerViewFood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        loadFoods();
        loadSuggestion();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (materialSearchBar.getText().equals(""))
                {
                    adapter.stopListening();
                    loadFoods();
                    loadSuggestion();
                    adapter.startListening();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else
                {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        //Search bar logic
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> search = new ArrayList<>();
                for(String newSearch : suggested)
                {
                    if(newSearch.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                    {
                        search.add(newSearch);
                    }
                }
                materialSearchBar.setLastSuggestions(search);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        materialSearchBar.setOnSearchActionListener(new SimpleOnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled)
                    recyclerViewFood.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                if(text.length() > 0)
                    filterResult(text);
                else {
                    Toast.makeText(FoodActivity.this, "Oops, you forgot to enter food", Toast.LENGTH_SHORT).show();
                    materialSearchBar.disableSearch();
                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                super.onButtonClicked(buttonCode);
            }
        });
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
        if(searchAdapter != null)
        searchAdapter.stopListening();
    }


    //Helper Method
    private void loadFoods() {
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>().
                setQuery(foods.orderByChild("menuId").equalTo(categoryId), Food.class).build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final Food model) {
                TextView textViewName = holder.itemView.findViewById(R.id.food_name);
                ImageView imageView = holder.itemView.findViewById(R.id.food_image);
                final ImageView imageViewFav = holder.itemView.findViewById(R.id.favorite);
                final ImageView imageFbShare = holder.itemView.findViewById(R.id.fb_share);

                if(database.isFavorite(adapter.getRef(position).getKey()))
                {
                    imageViewFav.setImageResource(R.drawable.ic_favorite_black_24dp);
                }
                else
                {
                    imageViewFav.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }

                imageViewFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(database.isFavorite(adapter.getRef(position).getKey()))
                        {
                            database.removeFromFavorite(adapter.getRef(position).getKey());
                            imageViewFav.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        }
                        else
                        {
                            database.addToFavorite(adapter.getRef(position).getKey());
                            imageViewFav.setImageResource(R.drawable.ic_favorite_black_24dp);
                        }
                    }
                });

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

                //fb_share_button_click_listener
                imageFbShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Picasso.get().load(model.getImage()).into(target);
                    }
                });
            }
        };

        recyclerViewFood.setAdapter(adapter);
    }


    private void loadSuggestion() {
        foods.orderByChild("menuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot suggestions : dataSnapshot.getChildren())
                {
                    Food item = suggestions.getValue(Food.class);
                    assert item != null;
                    suggested.add(item.getName());
                }
                materialSearchBar.setLastSuggestions(suggested);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void filterResult(CharSequence text) {
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>().
                setQuery(foods.orderByChild("name").equalTo(text.toString()), Food.class).build();

        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
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
                final ImageView imageFbShare = holder.itemView.findViewById(R.id.fb_share);

                textViewName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(imageView);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onclick(View view, int position, boolean isLongClick) {
                        //Sending food_id to FoodDetailActivity
                        Intent intent = new Intent(FoodActivity.this, FoodDetailActivity.class);
                        intent.putExtra("foodId", searchAdapter.getRef(position).getKey());
                        startActivity(intent);
                        materialSearchBar.disableSearch();
                    }
                });

                //fb_share_button_click_listener
                imageFbShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Picasso.get().load(model.getImage()).into(target);
                    }
                });
            }
        };

        recyclerViewFood.setAdapter(searchAdapter);
        searchAdapter.startListening();
    }


}//class ends
