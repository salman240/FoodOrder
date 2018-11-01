package com.example.salmangeforce.food_order.Server;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salmangeforce.food_order.Common.Common;
import com.example.salmangeforce.food_order.Interface.ItemClickListener;
import com.example.salmangeforce.food_order.Model.Category;
import com.example.salmangeforce.food_order.Model.Food;
import com.example.salmangeforce.food_order.R;
import com.example.salmangeforce.food_order.ViewHolders.FoodViewHolder;
import com.example.salmangeforce.food_order.ViewHolders.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import static android.widget.Toast.LENGTH_SHORT;

public class HomeActivityServer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int IMAGE_REQUEST_CODE = 100;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter adapter;
    TextView textViewHeader;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference category;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    private MaterialEditText etName;
    private Button btnUpload;
    private Button btnSelect;
    private Uri saveUri;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_server);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu Management");
        setSupportActionBar(toolbar);

        //Firebase init
        firebaseDatabase = FirebaseDatabase.getInstance();
        category = firebaseDatabase.getReference("Category");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        //View init
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMenuDialog();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        textViewHeader = headerView.findViewById(R.id.textviewName);
        textViewHeader.setText(Common.currentUser.getName());

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        loadMenu();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == IMAGE_REQUEST_CODE)
        {
            if(data != null && data.getData() != null)
            {
                saveUri = data.getData();
                Toast.makeText(this, "Image selected", LENGTH_SHORT).show();
            }
        }
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_activity_server, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    //Helper Methods
    private void addMenuDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Add new Category");
        alertDialog.setMessage("Please provide information about category");
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        View view = getLayoutInflater().inflate(R.layout.custom_add_food_dialog, null);
        alertDialog.setView(view);

        etName = view.findViewById(R.id.food_name);
        btnUpload = view.findViewById(R.id.btnUpload);
        btnSelect = view.findViewById(R.id.btnSelect);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });


        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                saveUri = null;
            }
        });

        alertDialog.show();
    }


    private void uploadImage() {
        if(saveUri != null && !etName.getText().toString().equals(""))
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading...");
            progressDialog.show();

            final String imageName = UUID.randomUUID().toString();
            final StorageReference imageStorage = storageReference.child("images/" + imageName);
            imageStorage.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            progressDialog.dismiss();
                            Category newCategory = new Category(etName.getText().toString(), uri.toString());
                            category.push().setValue(newCategory);
                            Snackbar.make(drawer, "Category added successfully", Snackbar.LENGTH_SHORT).show();
                            saveUri = null;
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Snackbar.make(drawer, e.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    int progress = (int) (100.0 * (taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploading : " + progress);
                }
            });
        }
        else
        {
            Toast.makeText(this, "Please provide name and image both", Toast.LENGTH_SHORT).show();
        }
    }


    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }


    private void loadMenu() {
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>().setQuery(category, Category.class).build();
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull final Category model) {
                TextView textViewMenuName = holder.itemView.findViewById(R.id.menu_name);
                ImageView imageViewMenuImage = holder.itemView.findViewById(R.id.menu_image);

                textViewMenuName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(imageViewMenuImage);
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onclick(View view, int position, boolean isLongClick) {
                        Toast.makeText(HomeActivityServer.this, model.getName(), LENGTH_SHORT).show();
                        //We need to send CategoryId to the next sub-activity
//                        Intent intentFood = new Intent(HomeActivityServer.this, FoodActivity.class);
//                        intentFood.putExtra("categoryId", adapter.getRef(position).getKey());
//                        startActivity(intentFood);
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }


}
