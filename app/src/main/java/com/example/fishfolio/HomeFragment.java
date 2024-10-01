package com.example.fishfolio;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {


    private static final String PREFS_NAME = "AppPrefs";
    private static final String PROFILE_PIC_URL_KEY = "profile_pic_url_";
    private CircleImageView userProfilePic;
    private SharedPreferences prefs;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    TextView tvHelloUsername, tvTemperatueValue, tvTurbidityValue, tvPhValue, tvOxygenValue
            ,tvTempCondition, tvTurboCondition, tvPhCondition, tvOxyCondition;
    private LinearLayout tempratureBox, turbidityBox;


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);


        //Asking for notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the permission is already granted
            if (ContextCompat.checkSelfPermission(requireActivity(),
                    android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }



        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        userProfilePic = rootView.findViewById(R.id.userProfilePic);
        tvHelloUsername = rootView.findViewById(R.id.tvHelloUsername);
        tvTemperatueValue = rootView.findViewById(R.id.tvTemperatureValue);
        tvTurbidityValue = rootView.findViewById(R.id.tvTurbidityValue);
        tvPhValue = rootView.findViewById(R.id.tvPhValue);
        tvOxygenValue = rootView.findViewById(R.id.tvOxygenValue);
        tvTempCondition = rootView.findViewById(R.id.tvTempCondition);
        tvTurboCondition = rootView.findViewById(R.id.tvTurboCondition);
        tvPhCondition = rootView.findViewById(R.id.tvPhCondition);
        tvOxygenValue = rootView.findViewById(R.id.tvOxyCondition);
        prefs = getActivity().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);

        tempratureBox = rootView.findViewById(R.id.tempratureBox);
        turbidityBox = rootView.findViewById(R.id.turbidityBox);

        tempratureBox.setOnClickListener((v) -> {startActivity(new Intent(getContext(), TemperatureActivity.class));});

        database.getReference("Users").child(user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Users currentUser = task.getResult().getValue(Users.class);
                if (currentUser != null) {
                    tvHelloUsername.setText("Hello, "+currentUser.getName());
                }
            }
        });

        database.getReference().child("IoT Data").child(user.getUid()).child("temp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int tempValue = Integer.parseInt(snapshot.getValue(String.class));
                        if (tempValue > 35) {
                            tvTempCondition.setText("HIGH");
                            tvTempCondition.setTextColor(Color.parseColor("#FF0000"));
                        }
                        if (tempValue < 25) {
                            tvTempCondition.setText("LOW");
                            tvTempCondition.setTextColor(Color.parseColor("#FFFF00"));
                        }
                        if (tempValue <= 35 && tempValue >= 25) {
                            tvTempCondition.setText("NORMAL");
//                            tvTempCondition.setTextColor(Color.parseColor("#00FF00"));
                        }
                        tvTemperatueValue.setText(snapshot.getValue(String.class)+"°");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        database.getReference().child("IoT Data").child(user.getUid()).child("turbo")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tvTurbidityValue.setText(snapshot.getValue(String.class));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        database.getReference().child("IoT Data").child(user.getUid()).child("oxy")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tvOxygenValue.setText(snapshot.getValue(String.class));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        database.getReference().child("IoT Data").child(user.getUid()).child("ph")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tvPhValue.setText(snapshot.getValue(String.class));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        loadProfilePicFromPrefs();

        return rootView; // Return the inflated view
    }

    private void loadProfilePicFromPrefs() {
        String userId = auth.getCurrentUser().getUid(); // Get the current user's unique ID
        String profilePicUrl = prefs.getString(PROFILE_PIC_URL_KEY + userId, null); // Load the user-specific URL from SharedPreferences

        if (profilePicUrl != null) {
            // Load the image using Glide if the URL exists
            Glide.with(this)
                    .load(profilePicUrl)
                    .placeholder(R.drawable.profilephoto) // Default placeholder if the image is not available
                    .circleCrop()
                    .into(userProfilePic);
        } else {
            // Optionally, handle cases where the image URL is not found (show default image)
            userProfilePic.setImageResource(R.drawable.profilephoto);
        }
    }

}
