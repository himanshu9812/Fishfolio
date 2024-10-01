package com.example.fishfolio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etForgotPassword_Email;
    Button btnContinue;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        etForgotPassword_Email = findViewById(R.id.etForgotPassword_Email);
        btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener((v -> {
            String email = etForgotPassword_Email.getText().toString();
            boolean isValidated = validateData(email);
            if(!isValidated)
                return;
            forgotPassword();
        }));

    }

    private void forgotPassword() {
        String email = etForgotPassword_Email.getText().toString();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(this, "Check your mail", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ForgotPasswordActivity.this,SignInActivity.class));
                finish();
            }else {
                Toast.makeText(this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean validateData(String email){
        // Regular expression pattern for email validation
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";

        if (!email.matches(emailPattern)) {
            etForgotPassword_Email.setError("Email is Invalid");
            return false;
        }
        return true;
    }
}