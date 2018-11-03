package com.example.salmangeforce.food_order;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.salmangeforce.food_order.Common.Common;
import com.example.salmangeforce.food_order.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

import static com.example.salmangeforce.food_order.Common.Common.CLIENT;
import static com.example.salmangeforce.food_order.Common.Common.SERVER;
import static com.example.salmangeforce.food_order.Common.Common.USER_NAME;
import static com.example.salmangeforce.food_order.Common.Common.USER_PASSWORD;
import static com.example.salmangeforce.food_order.Common.Common.USER_PHONE;

public class SignIn extends AppCompatActivity {

    EditText editPhone, editPassord;
    Button btnSignIn;
    com.rey.material.widget.CheckBox rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Paper.init(this);

        editPhone = findViewById(R.id.editPhone);
        editPassord = findViewById(R.id.editPassord);
        btnSignIn = findViewById(R.id.btnSignIn);
        rememberMe = findViewById(R.id.remember_me);

        //Firebase Init
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = firebaseDatabase.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(SignIn.this);
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Checking User avail
                        if(dataSnapshot.child(editPhone.getText().toString()).exists())
                        {
                            //Get User data
                            progressDialog.dismiss();
                            User user = dataSnapshot.child(editPhone.getText().toString()).getValue(User.class);
                            assert user != null;
                            if (user.getPassword().equals(editPassord.getText().toString()))
                            {
                                //remember me
                                if(rememberMe.isChecked())
                                {
                                    Paper.book(CLIENT).write(USER_PHONE, editPhone.getText().toString());
                                    Paper.book(CLIENT).write(USER_PASSWORD, editPassord.getText().toString());
                                    Paper.book(CLIENT).write(USER_NAME, user.getName());
                                }

                                user.setPhone(editPhone.getText().toString());
                                Intent intent = new Intent(SignIn.this, HomeActivity.class);
                                Common.currentUser = user;
                                startActivity(intent);
                                finish();
                            } else
                            {
                                Toast.makeText(SignIn.this, "Sign in failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(SignIn.this, "User not exists!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }
}
