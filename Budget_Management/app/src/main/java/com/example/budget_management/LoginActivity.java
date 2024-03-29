package com.example.budget_management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaCodec;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class   LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private TextView signupRedirectText;
    private CheckBox checkShowPassword;

    TextView forgotPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth =FirebaseAuth.getInstance();
        loginEmail=findViewById(R.id.login_email);
        loginPassword=findViewById(R.id.login_password);
        loginButton=findViewById(R.id.login_button);
        forgotPassword =findViewById(R.id.forgot_password);
        checkShowPassword=findViewById(R.id.checkShowPassword);

        signupRedirectText=findViewById(R.id.SignupRedirectText);
        checkShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=loginEmail.getText().toString();
                String pass=loginPassword.getText().toString();
               /* if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!pass.isEmpty())
                    {
                        auth.signInWithEmailAndPassword(email,pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else
                    {
                        loginPassword.setError("Password cannot be empty");
                    }

                }
                else if (email.isEmpty())
                {
                    loginEmail.setError("Email cannot be empty");
                }*/
                if(TextUtils.isEmpty(email)){
                    loginEmail.setError("Email required");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    loginPassword.setError("Password required");
                    return;
                }
                else {
                    auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                Toast.makeText(getApplicationContext(),"Login successful",Toast.LENGTH_SHORT).show();

                            }
                            else {
                                Toast.makeText(getApplicationContext(),"Login failed",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }

            }
        });
        //Sign up activity
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
            }
        });
        //Forget password activity
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                View dialogView =getLayoutInflater().inflate(R.layout.dialog_forgot,null);
                EditText emailBox= dialogView.findViewById(R.id.emailBox);
                builder.setView(dialogView);
                AlertDialog dialog= builder.create();
                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userEmail=emailBox.getText().toString();
                        if (TextUtils.isEmpty(userEmail)&& !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
                        {
                            Toast.makeText(LoginActivity.this, "Enter your registered email id", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Unable to send, failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();

            }
        });

    }
}