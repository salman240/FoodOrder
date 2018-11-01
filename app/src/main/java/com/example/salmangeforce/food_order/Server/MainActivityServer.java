package com.example.salmangeforce.food_order.Server;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salmangeforce.food_order.R;

public class MainActivityServer extends AppCompatActivity implements View.OnClickListener {
    Button btnSignIn;
    TextView textSlogan;
    private boolean isSinglePressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_server);

        btnSignIn = findViewById(R.id.btnSignIn);

        textSlogan = findViewById(R.id.txtSlogan);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        textSlogan.setTypeface(typeface);

        btnSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnSignIn)
        {
            Intent intent = new Intent(MainActivityServer.this, SignInServer.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
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
}
