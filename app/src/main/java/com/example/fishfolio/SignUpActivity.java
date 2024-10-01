package com.example.fishfolio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {


    FirebaseAuth auth;
    FirebaseDatabase database;
    TextView tvSignIn;
    EditText etUserFullName, etEmail, etPassword, etConfirmPassword;
    CheckBox chMale, chFemale;
    String gender;
    Button btnSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        tvSignIn = findViewById(R.id.tvSignIn);
        etUserFullName = findViewById(R.id.etUserFullName);
        etEmail = findViewById(R.id.etEmail);
        chMale = findViewById(R.id.chMale);
        chFemale = findViewById(R.id.chFemale);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        chMale.setOnClickListener(this::onCheckboxClicked);
        chFemale.setOnClickListener(this::onCheckboxClicked);


        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            }
        });

        btnSignUp.setOnClickListener((v)-> createAccount());

    }

    private void createAccount() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        boolean isValidated = validateData(email,password,confirmPassword);
        if(!isValidated)
            return;

        createAccountInFirebase(email,password);

    }

    private void createAccountInFirebase(String email, String password){
//        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(
                task -> {
//                    changeInProgress(false);
                    if(task.isSuccessful()){
                        Objects.requireNonNull(firebaseAuth.getCurrentUser()).sendEmailVerification();
                        Users user = new Users(etUserFullName.getText().toString(),gender,etEmail.getText().toString());
                        IoTData ioTData = new IoTData();
                        String id = Objects.requireNonNull(task.getResult().getUser()).getUid();
                        database.getReference().child("Users").child(id).setValue(user);
                        database.getReference().child("IoT Data").child(id).setValue(ioTData);
                        startActivity(new Intent(SignUpActivity.this,EmailVerifyActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    boolean validateData(String email, String password, String confirmPassword){
        // Regular expression pattern for email validation
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";

        if (!email.matches(emailPattern)) {
            etEmail.setError("Email is Invalid");
            return false;
        }
        if(password.length()<6){
            etPassword.setError("Password length is invalid");
            return false;
        }
        if(!password.equals(confirmPassword)){
            etConfirmPassword.setError("Password not matched");
            return false;
        }
        return true;
    }

    public void onCheckboxClicked(View checkedView) {
        boolean checked = ((CheckBox) checkedView).isChecked();

        // Check which checkbox was clicked
        if (checkedView.getId() == R.id.chMale) {
            if (checked) {
                chFemale.setChecked(false);// Uncheck the other checkbox
                gender = chMale.getText().toString();
            }
        } else if (checkedView.getId() == R.id.chFemale) {
            if (checked) {
                chMale.setChecked(false); // Uncheck the other checkbox
                gender = chFemale.getText().toString();
            }
        }
    }
}