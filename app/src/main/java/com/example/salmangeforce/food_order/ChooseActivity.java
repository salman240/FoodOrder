package com.example.salmangeforce.food_order;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.salmangeforce.food_order.Server.MainActivityServer;
import com.example.salmangeforce.food_order.Server.SignInServer;

public class ChooseActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnClient, btnServer;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        btnClient = findViewById(R.id.btnClient);
        btnServer = findViewById(R.id.btnServer);
        progressBar = findViewById(R.id.progress);

        btnClient.setOnClickListener(this);
        btnServer.setOnClickListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                btnClient.animate().alpha(1).setDuration(300);
                btnServer.animate().alpha(1).setDuration(300);
            }
        }, 1000);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnClient)
        {
            Intent intent = new Intent(ChooseActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if(v.getId() == R.id.btnServer)
        {
            Intent intent = new Intent(ChooseActivity.this, MainActivityServer.class);
            startActivity(intent);
            finish();
        }
    }

}
