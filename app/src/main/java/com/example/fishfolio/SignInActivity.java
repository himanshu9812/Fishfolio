package com.example.fishfolio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {


    TextView tvForgotPassword, tvSignUp;
    EditText etLoginEmail, etLoginPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        tvSignUp = findViewById(R.id.tvSignUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);


        tvSignUp.setOnClickListener((v)-> {
            startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
            finish();
        });

        tvForgotPassword.setOnClickListener((v -> {
            startActivity(new Intent(SignInActivity.this,ForgotPasswordActivity.class));
            finish();
        }));

        btnLogin.setOnClickListener((v)->loginUser());

    }

    private void loginUser() {
        String email = etLoginEmail.getText().toString();
        String password = etLoginPassword.getText().toString();

        boolean isValidated = validateData(email,password);
        if(!isValidated)
            return;

        loginAccountInFirebase(email,password);
    }

    private void loginAccountInFirebase(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                task -> {
                    if(task.isSuccessful()){
                        if(Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()){
                            Toast.makeText(SignInActivity.this, "Successfully login", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            finish();
                        }else{
                            Toast.makeText(SignInActivity.this, "Email not verified, Please verify your email.", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(SignInActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_LONG).show();

                    }
        });
    }

    boolean validateData(String email, String password){
        // Regular expression pattern for email validation
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";

        if (!email.matches(emailPattern)) {
            etLoginEmail.setError("Email is Invalid");
            return false;
        }
        if(password.length()<6){
            etLoginPassword.setError("Password length is invalid");
            return false;
        }
        return true;
    }
}