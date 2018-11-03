package com.example.salmangeforce.food_order;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSignUp, btnSignIn;
    TextView textSlogan;
//    private boolean isSinglePressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);

        textSlogan = findViewById(R.id.txtSlogan);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        textSlogan.setTypeface(typeface);

        btnSignUp.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnSignUp)
        {
//            SignUp
            Intent intent = new Intent(MainActivity.this, SignUp.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.btnSignIn)
        {
//            Login
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);
                finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
