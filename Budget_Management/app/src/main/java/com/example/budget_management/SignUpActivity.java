package com.example.budget_management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword;
    private Button signupButton;
    private TextView loginRedirectText;
    private CheckBox checkShowPasswordSignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth =FirebaseAuth.getInstance();
        signupEmail=findViewById(R.id.signup_email);
        signupPassword=findViewById(R.id.signup_password);
        signupButton=findViewById(R.id.signup_button);
        loginRedirectText=findViewById(R.id.LoginRedirectText);
        checkShowPasswordSignup=findViewById(R.id.checkShowPasswordSignup);
        checkShowPasswordSignup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    signupPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    signupPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email =signupEmail.getText().toString().trim();
                String pass= signupPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email))
                {
                    signupEmail.setError("Email cannot be empty");
                    return;
                }
                if (TextUtils.isEmpty(pass))
                {
                    signupPassword.setError("Password cannot be empty");
                    return;
                }

                else
                {
                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(),"Signup Complelte", Toast.LENGTH_SHORT).show();
                                //Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Sign up Failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));

            }
        });
    }
}