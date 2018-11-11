package com.example.salmangeforce.food_order.Server;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salmangeforce.food_order.Common.Common;
import com.example.salmangeforce.food_order.Interface.ItemClickListener;
import com.example.salmangeforce.food_order.Model.Category;
import com.example.salmangeforce.food_order.R;
import com.example.salmangeforce.food_order.Server.ViewHolders.MenuViewHolderServer;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import io.paperdb.Paper;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.salmangeforce.food_order.Common.Common.SERVER;
import static com.example.salmangeforce.food_order.Common.Common.USER_NAME;
import static com.example.salmangeforce.food_order.Common.Common.USER_PASSWORD;
import static com.example.salmangeforce.food_order.Common.Common.USER_PHONE;

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
    SwipeRefreshLayout swipeRefreshLayout;

    private MaterialEditText etName;
    private Button btnUpload;
    private Button btnSelect;
    private Uri saveUri;
    private DrawerLayout drawer;
    private boolean isSinglePressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_server);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu Management");
        setSupportActionBar(toolbar);

        Paper.init(this);

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

        swipeRefreshLayout = findViewById(R.id.swipeHome);

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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.stopListening();
                loadMenu();
                adapter.startListening();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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
        }
        
        if(isSinglePressed)
        {
            super.onBackPressed();
        }
        else
        {
            isSinglePressed = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isSinglePressed = false;
                }
            },2000);
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
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_orders)
        {
            Intent intent = new Intent(HomeActivityServer.this, OrderStatusActivityServer.class);
            startActivity(intent);
        }
        if(id == R.id.nav_sign_out)
        {
            //clearing remember me data in PaperDb
            Paper.book(SERVER).delete(USER_PHONE);
            Paper.book(SERVER).delete(USER_PASSWORD);
            Paper.book(SERVER).delete(USER_NAME);

            Intent intent = new Intent(HomeActivityServer.this, MainActivityServer.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE))
        {
            updateCategory(adapter.getRef(item.getOrder()).getKey(), (Category) adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.DELETE))
        {
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }


    //Helper Methods
    private void loadMenu() {
        ProgressBar progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>().setQuery(category, Category.class).build();
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolderServer>(options) {
            @NonNull
            @Override
            public MenuViewHolderServer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_server, parent, false);
                return new MenuViewHolderServer(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolderServer holder, int position, @NonNull final Category model) {
                TextView textViewMenuName = holder.itemView.findViewById(R.id.menu_name);
                ImageView imageViewMenuImage = holder.itemView.findViewById(R.id.menu_image);

                textViewMenuName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(imageViewMenuImage);
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onclick(View view, int position, boolean isLongClick) {
                        //We need to send CategoryId to the next sub-activity
                        Intent intentFood = new Intent(HomeActivityServer.this, FoodActivityServer.class);
                        intentFood.putExtra("categoryId", adapter.getRef(position).getKey());
                        startActivity(intentFood);
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }


    private void addMenuDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Add new Category");
        alertDialog.setMessage("Please provide information about category");
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        View view = getLayoutInflater().inflate(R.layout.custom_add_category_dialog, null);
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


    private void updateCategory(final String key, final Category item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Update Category");
        alertDialog.setMessage("Please update information about category");
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        View view = getLayoutInflater().inflate(R.layout.custom_add_category_dialog, null);
        alertDialog.setView(view);

        etName = view.findViewById(R.id.food_name);
        btnUpload = view.findViewById(R.id.btnUpload);
        btnSelect = view.findViewById(R.id.btnSelect);

        etName.setText(item.getName());

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateImage(key, item);
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


    private void updateImage(final String key, final Category item) {
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
                            item.setName(etName.getText().toString());
                            item.setImage(uri.toString());
                            category.child(key).setValue(item);
                            Snackbar.make(drawer, "Category updated successfully", Snackbar.LENGTH_SHORT).show();
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


    private void deleteCategory(final String key) {
        category.child(key).removeValue();
        DatabaseReference foods =  firebaseDatabase.getReference("Food");
        foods.orderByChild("menuId").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot food : dataSnapshot.getChildren())
                {
                    food.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}//class ends
