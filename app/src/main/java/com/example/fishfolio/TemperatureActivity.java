package com.example.fishfolio;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TemperatureActivity extends AppCompatActivity {

    HalfGauge halfGauge;
    LinearLayout highTempBox, normalTempBox, lowTempBox;
    TextView tempHighvalue, tempNormalValue, tempLowValue;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_temperature);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        halfGauge = findViewById(R.id.halfGauge);
        highTempBox = findViewById(R.id.highTempBox);
        normalTempBox = findViewById(R.id.normalTempBox);
        lowTempBox = findViewById(R.id.lowTempBox);
        tempHighvalue = findViewById(R.id.tempHighValue);
        tempNormalValue = findViewById(R.id.tempNormalValue);
        tempLowValue = findViewById(R.id.tempLowValue);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        // Define the color ranges
        Range range = new Range();
        range.setColor(Color.parseColor("#E3E500"));
        range.setFrom(10.0);
        range.setTo(24.9);

        Range range2 = new Range();
        range2.setColor(Color.parseColor("#00b20b"));
        range2.setFrom(24.9);
        range2.setTo(35.9);

        Range range3 = new Range();
        range3.setColor(Color.parseColor("#ce0000"));
        range3.setFrom(35.9);
        range3.setTo(50.0);

// Add color ranges to the gauge
        halfGauge.addRange(range);
        halfGauge.addRange(range2);
        halfGauge.addRange(range3);

// Set min, max, and current value
        halfGauge.setMinValue(10.0);
        halfGauge.setMaxValue(50.0);

        database.getReference().child("IoT Data").child(user.getUid()).child("temp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String tempValue = snapshot.getValue(String.class);
                        int intTempValue = Integer.parseInt(tempValue);
                        halfGauge.setValue(intTempValue);
                        if (intTempValue > 35) {
                            highTempBox.setVisibility(View.VISIBLE);
                            lowTempBox.setVisibility(View.GONE);
                            normalTempBox.setVisibility(View.GONE);
                            tempHighvalue.setText(tempValue+"° C");
                        }
                        if (intTempValue < 25) {
                            lowTempBox.setVisibility(View.VISIBLE);
                            highTempBox.setVisibility(View.GONE);
                            normalTempBox.setVisibility(View.GONE);
                            tempLowValue.setText(tempValue+"° C");
                        }
                        if (intTempValue <= 35 && intTempValue >= 25){
                            normalTempBox.setVisibility(View.VISIBLE);
                            lowTempBox.setVisibility(View.GONE);
                            highTempBox.setVisibility(View.GONE);
                            tempNormalValue.setText(tempValue+"° C");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

    }
}