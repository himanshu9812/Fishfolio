package com.example.fishfolio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class EmailVerifyActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Button verifiedEmailbtn;
    TextView userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_email_verify);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        verifiedEmailbtn = findViewById(R.id.verifyLoginBtn);
        userEmail = findViewById(R.id.UserEmailTextView);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;
        String email = currentUser.getEmail();
        userEmail.setText(email);

        verifiedEmailbtn.setOnClickListener((v) -> verifyEmail());
    }

    private void verifyEmail() {
        Objects.requireNonNull(firebaseAuth.getCurrentUser()).reload().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                    startActivity(new Intent(EmailVerifyActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Email Not verified", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to reload user.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}