package com.example.salmangeforce.food_order;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salmangeforce.food_order.Adapter.CartAdapter;
import com.example.salmangeforce.food_order.Common.Common;
import com.example.salmangeforce.food_order.Model.Food;
import com.example.salmangeforce.food_order.Model.Order;
import com.example.salmangeforce.food_order.Model.Request;
import com.example.salmangeforce.food_order.ViewHolders.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class OrderStatusActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView textViewName;
    TextView textViewStatus;
    TextView textViewPhone;
    TextView textViewAddress;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference request;
    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //init Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        request = firebaseDatabase.getReference("Request");

        textViewName = findViewById(R.id.order_id);
        textViewStatus = findViewById(R.id.order_status);
        textViewPhone = findViewById(R.id.order_phone);
        textViewAddress = findViewById(R.id.order_address);

        recyclerView = findViewById(R.id.recycler_order_status);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        showOrders(Common.currentUser.getPhone());
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
    private void showOrders(String phone)
    {
        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>().setQuery(
                request.orderByChild("phone").equalTo(phone), Request.class).build();
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
                return new OrderViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Request model) {
                TextView textViewId = holder.itemView.findViewById(R.id.order_id);
                textViewId.setText(adapter.getRef(position).getKey());

                TextView textViewPhone = holder.itemView.findViewById(R.id.order_phone);
                textViewPhone.setText(model.getPhone());

                TextView textViewAddress = holder.itemView.findViewById(R.id.order_address);
                textViewAddress.setText(model.getAddress());

                TextView textViewStatus = holder.itemView.findViewById(R.id.order_status);
                textViewStatus.setText(getStatus(model.getStatus()));
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private String getStatus(String status) {
        switch (status) {
            case "0":
                return "Placed";
            case "1":
                return "Shipping";
            case "2":
                return "Shipped";
            default:
                return "Status Not Available";
        }
    }

}//class ends
