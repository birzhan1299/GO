package com.example.go;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity implements Main {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread =  new Thread()
        {
            @Override
            public void run() {
                super.run();
                try
                {
                    sleep(5000);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally {
                    Intent welcomeIntent =  new Intent(MainActivity.this, WelcomeActivity.class);
                    startActivity(welcomeIntent);
                }
            }
        };
         thread.start();

    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

}