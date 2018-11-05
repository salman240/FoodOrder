package com.example.salmangeforce.food_order;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.salmangeforce.food_order.Common.Common;
import com.example.salmangeforce.food_order.Model.User;
import com.example.salmangeforce.food_order.Server.HomeActivityServer;
import com.example.salmangeforce.food_order.Server.MainActivityServer;

import io.paperdb.Paper;

import static com.example.salmangeforce.food_order.Common.Common.CLIENT;
import static com.example.salmangeforce.food_order.Common.Common.SERVER;
import static com.example.salmangeforce.food_order.Common.Common.USER_NAME;
import static com.example.salmangeforce.food_order.Common.Common.USER_PASSWORD;
import static com.example.salmangeforce.food_order.Common.Common.USER_PHONE;

public class ChooseActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnClient, btnServer;
    ProgressBar progressBar;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        Paper.init(this);

        relativeLayout = findViewById(R.id.parent);
        btnClient = findViewById(R.id.btnClient);
        btnServer = findViewById(R.id.btnServer);
        progressBar = findViewById(R.id.progress);

        btnClient.setOnClickListener(this);
        btnServer.setOnClickListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                btnClient.animate().alpha(1).setDuration(300);
                btnServer.animate().alpha(1).setDuration(300);
            }
        }, 1000);

        //Services

    }

    @Override
    public void onClick(View v) {
        //checking internet firstly
        if (Common.isInternetAvailable(this)) {
            if (v.getId() == R.id.btnClient) {
                //check if user is already signed in
                String phone = Paper.book(CLIENT).read(USER_PHONE);
                String password = Paper.book(CLIENT).read(USER_PASSWORD);
                String name = Paper.book(CLIENT).read(USER_NAME);

                if (phone != null && password != null && name != null) {
                    Intent intent = new Intent(ChooseActivity.this, HomeActivity.class);
                    startActivity(intent);
                    Common.currentUser = new User(name, password, phone, "false");
                    finish();
                } else {
                    Intent intent = new Intent(ChooseActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else if (v.getId() == R.id.btnServer) {
                //check if user is already signed in
                String phone = Paper.book(SERVER).read(USER_PHONE);
                String password = Paper.book(SERVER).read(USER_PASSWORD);
                String name = Paper.book(SERVER).read(USER_NAME);

                if (phone != null && password != null && name != null) {
                    Intent intent = new Intent(ChooseActivity.this, HomeActivityServer.class);
                    startActivity(intent);
                    Common.currentUser = new User(name, password, phone, "true");
                    finish();
                } else {
                    Intent intent = new Intent(ChooseActivity.this, MainActivityServer.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
        else {
            Snackbar.make(relativeLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
        }
    }

}
