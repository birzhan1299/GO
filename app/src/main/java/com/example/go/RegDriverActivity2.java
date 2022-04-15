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
import com.google.firebase.database.FirebaseDatabase;

public class RegDriverActivity2 extends AppCompatActivity {

    TextView driverStatus;
    Button signUpBtn;
    EditText emailET, passwordET, passwordET2, nameET;

    FirebaseAuth nAuth;

    ProgressDialog loadingBar;
    DatabaseReference DriverDatabaseRef;
    String OnlineDriverID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_driver2);
        driverStatus = (TextView) findViewById(R.id.textRegDriver);
        nameET = (EditText) findViewById(R.id.ETNameDriver);
        signUpBtn = (Button) findViewById(R.id.signInDriver);
        emailET = (EditText) findViewById(R.id.RegEmailDriver);
        passwordET = (EditText) findViewById(R.id.PasswordDriver);
        passwordET2 = (EditText) findViewById(R.id.Password2Driver);

        nAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();
                String password2 = passwordET2.getText().toString();
                String name = nameET.getText().toString();
                RegisterDriver(email, password, password2, name);
            }


        });

    }

    private void RegisterDriver(String email, String password, String password2, String name)
    {
        loadingBar.setTitle("Регистрация водителя...");
        loadingBar.setMessage("Пожалуйста дождитесь загрузки");
        loadingBar.show();
        nAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    OnlineDriverID = nAuth.getCurrentUser().getUid();
                    DriverDatabaseRef = FirebaseDatabase.getInstance().getReference()
                            .child("Users").child("Drivers").child(OnlineDriverID);
                    DriverDatabaseRef.setValue(true);
                    Intent driverIntent = new Intent(RegDriverActivity2.this, DriversMapActivity.class);
                    startActivity(driverIntent);
                    Toast.makeText(RegDriverActivity2.this, " Регистрациа прошла успешно!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                }
                else
                {
                    Toast.makeText(RegDriverActivity2.this, "Ошибка!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }
}