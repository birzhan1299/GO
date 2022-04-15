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

public class RegCustomerActivity extends AppCompatActivity {

    TextView customerStatus;
    Button signUpBtn;
    EditText emailET, passwordET, passwordET2, nameET;

    FirebaseAuth nAuth;
    ProgressDialog loadingBar;
    DatabaseReference CustomerDatabaseRef;
    String OnlineCustomerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_customer);
        customerStatus = (TextView) findViewById(R.id.textRegCustomer);
        nameET = (EditText) findViewById(R.id.ETNameCustomer);
        signUpBtn = (Button) findViewById(R.id.signInCustomer);
        emailET = (EditText) findViewById(R.id.RegEmailCustomer);
        passwordET = (EditText) findViewById(R.id.PasswordCustomer);
        passwordET2 = (EditText) findViewById(R.id.Password2Customer);

        nAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();
                String password2 = passwordET2.getText().toString();
                String name = nameET.getText().toString();
                RegisterCustomer(email, password, password2, name);
            }


        });

    }

    private void RegisterCustomer(String email, String password, String password2, String name) {
        loadingBar.setTitle("Регистрация водителя...");
        loadingBar.setMessage("Пожалуйста дождитесь загрузки");
        loadingBar.show();
        nAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {


                    OnlineCustomerID = nAuth.getCurrentUser().getUid();
                    CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference()
                            .child("Users").child("Customers").child(OnlineCustomerID);
                    CustomerDatabaseRef.setValue(true);

                    Toast.makeText(RegCustomerActivity.this, " Регистрациа прошла успешно!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Intent customerIntent = new Intent(RegCustomerActivity.this, CustomersMapActivity.class);
                    startActivity(customerIntent);
                }
                else
                {
                    Toast.makeText(RegCustomerActivity.this, "Ошибка!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }
}