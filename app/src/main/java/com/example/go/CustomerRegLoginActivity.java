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

public class CustomerRegLoginActivity extends AppCompatActivity {

    TextView customerStatus;
    Button signInBtn, signUpBtn;
    EditText emailET, passwordET;

    FirebaseAuth nAuth;
    ProgressDialog loadingBar;
    DatabaseReference CustomerDatabaseRef;
    String OnlineCustomerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_reg_login);
        customerStatus = (TextView) findViewById(R.id.statusCustomer);
        signInBtn = (Button) findViewById(R.id.signInCustomer);
        signUpBtn = (Button) findViewById(R.id.accCreate);
        emailET = (EditText) findViewById(R.id.customerEmail);
        passwordET = (EditText) findViewById(R.id.customerPassword);
        loadingBar = new ProgressDialog(this);
        nAuth = FirebaseAuth.getInstance();


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regCIntent =  new Intent(CustomerRegLoginActivity.this, RegCustomerActivity.class);
                startActivity(regCIntent);
            }
        });
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                SignInCustomer(email, password);
            }
        });

    }
    private void SignInCustomer(String email, String password)
    {
        loadingBar.setTitle("Вход");
        loadingBar.setMessage("Пожалуйста дождитесь загрузки");
        loadingBar.show();
        nAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(CustomerRegLoginActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Intent customerIntent = new Intent(CustomerRegLoginActivity.this, CustomersMapActivity.class);
                    startActivity(customerIntent);
                }
                else
                {
                    Toast.makeText(CustomerRegLoginActivity.this, "Произошла ошибка, попробуйте снова!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }
}