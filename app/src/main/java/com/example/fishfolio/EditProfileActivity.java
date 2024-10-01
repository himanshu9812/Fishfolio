package com.example.fishfolio;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppPrefs";
    private static final String PROFILE_PIC_URL_KEY = "profile_pic_url_";

    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;
    ImageButton editProfilePic;
    ImageView overlappingImage;
    EditText etFullName;
    CheckBox chMale, chFemale;
    Button btnUpdate;
    FirebaseAuth auth;
    FirebaseUser user;
    SharedPreferences prefs;
    FirebaseDatabase database;
    String gender, currentName, currentProfilePicUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        editProfilePic = findViewById(R.id.editProfilePic);
        overlappingImage = findViewById(R.id.overlappingImage);
        etFullName = findViewById(R.id.etFullname);
        chMale = findViewById(R.id.chMale);
        chFemale = findViewById(R.id.chFemale);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Fetch current user data
        loadCurrentUserData();

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            overlappingImage.setImageURI(selectedImageUri);
                        }
                    }
                });

        editProfilePic.setOnClickListener(v -> ImagePicker.with(this)
                .cropSquare()
                .compress(512)
                .maxResultSize(512, 512)
                .createIntent(intent -> {
                    imagePickLauncher.launch(intent);
                    return null;
                }));

        btnUpdate.setOnClickListener(v -> updateProfile());
    }

    private void loadCurrentUserData() {
        // Fetch the current profile picture URL from shared preferences
        currentProfilePicUrl = prefs.getString(PROFILE_PIC_URL_KEY + user.getUid(), null);
        if (currentProfilePicUrl != null) {
            loadProfilePicIntoImageView(currentProfilePicUrl);
        }

        // Fetch other user data from Firebase Database
        database.getReference("Users").child(user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Users currentUser = task.getResult().getValue(Users.class);
                if (currentUser != null) {
                    currentName = currentUser.getName();
                    gender = currentUser.getGender();

                    etFullName.setText(currentName);
                    if ("Male".equals(gender)) {
                        chMale.setChecked(true);
                    } else if ("Female".equals(gender)) {
                        chFemale.setChecked(true);
                    }
                }
            }
        });
    }

    private void updateProfile() {
        boolean hasChanges = false;

        // Check if the name has been updated
        String newName = etFullName.getText().toString();
        if (!TextUtils.isEmpty(newName) && !newName.equals(currentName)) {
            database.getReference("Users").child(user.getUid()).child("name").setValue(newName);
            hasChanges = true;
            Toast.makeText(this, "Name updated successfully", Toast.LENGTH_SHORT).show(); // Immediate toast for name update
        }

        // Check if the gender has been updated
        String newGender = gender;
        if (chMale.isChecked()) {
            newGender = "Male";
        } else if (chFemale.isChecked()) {
            newGender = "Female";
        }

        if (!newGender.equals(gender)) {
            database.getReference("Users").child(user.getUid()).child("gender").setValue(newGender);
            hasChanges = true;
            Toast.makeText(this, "Gender updated successfully", Toast.LENGTH_SHORT).show(); // Immediate toast for gender update
        }

        // Check if profile picture has been updated
        if (selectedImageUri != null) {
            uploadProfilePicToFirebase(selectedImageUri);
            hasChanges = true; // Indicate that profile picture has changed
        }

        // This toast will only show if no changes were made
        if (!hasChanges) {
            Toast.makeText(this, "No changes made", Toast.LENGTH_SHORT).show();
        }
    }



    private void uploadProfilePicToFirebase(Uri imageUri) {
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("profile_pics")
                .child(userId + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    database.getReference("Users").child(user.getUid()).child("profilePicUrl").setValue(downloadUrl);
                    saveProfilePicUrlToPrefs(downloadUrl);
                    loadProfilePicIntoImageView(downloadUrl);

                    // Add toast message here after successful upload
                    Toast.makeText(EditProfileActivity.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Profile picture upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }



    private void saveProfilePicUrlToPrefs(String profilePicUrl) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROFILE_PIC_URL_KEY + user.getUid(), profilePicUrl);
        editor.apply();
    }

    private void loadProfilePicIntoImageView(String url) {
        Glide.with(this)
                .load(url)
                .circleCrop()
                .into(overlappingImage);
    }
}
