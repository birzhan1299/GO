package com.example.go;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    Button driverBtn, customerBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        driverBtn = (Button)findViewById(R.id.driverBtn);
        customerBtn = (Button)findViewById(R.id.customerBtn);

        driverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent driverintent =  new Intent(WelcomeActivity.this, DriverRegLoginActivity.class);
                startActivity(driverintent);
            }
        });

        customerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent customerIntent =  new Intent(WelcomeActivity.this, CustomerRegLoginActivity.class);
                startActivity(customerIntent);
            }
        });
    }
}