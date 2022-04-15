package com.example.go;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class DriverRegLoginActivity extends AppCompatActivity {

    TextView driverStatus;
    Button signInBtn, signUpBtn;
    EditText emailET, passwordET;

    FirebaseAuth nAuth;
    ProgressDialog loadingBar;
    DatabaseReference DriverDatabaseRef;
    String OnlineDriverID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_reg_login);
        driverStatus = (TextView) findViewById(R.id.statusDriver);
        signInBtn = (Button) findViewById(R.id.signInDriver);
        signUpBtn = (Button) findViewById(R.id.accCreate);
        emailET = (EditText) findViewById(R.id.driverEmail);
        passwordET = (EditText) findViewById(R.id.driverPassword);

        nAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regDIntent =  new Intent(DriverRegLoginActivity.this, RegDriverActivity2.class);
                startActivity(regDIntent);
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                SignInDriver(email, password);
            }
        });
    }

    private void SignInDriver(String email, String password)
    {
        loadingBar.setTitle("Вход");
        loadingBar.setMessage("Пожалуйста дождитесь загрузки");
        loadingBar.show();
        nAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(DriverRegLoginActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Intent driverIntent = new Intent(DriverRegLoginActivity.this, DriversMapActivity.class);
                    startActivity(driverIntent);
                }
                else
                {
                    Toast.makeText(DriverRegLoginActivity.this, "Произошла ошибка, попробуйте снова!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }
}