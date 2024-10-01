package com.example.fishfolio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationService extends Service {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    private static final String CHANNEL_ID = "Warning Channel";
    private static final int TEMP_NOTIFICATION_ID = 100;
    MediaPlayer mp;

    // Flag to track if the notification has already been sent
    boolean hasNotified = false;

    @Override
    public void onCreate() {
        super.onCreate();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (user != null) {
            database.getReference().child("IoT Data").child(user.getUid()).child("temp")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String tempVal = snapshot.getValue(String.class);
                            int tempIntVal = Integer.parseInt(tempVal);

                            // Check if temperature is above 30°C and notification has not been sent yet
                            if (tempIntVal > 30 && !hasNotified) {
                                showNotification(R.drawable.temperature_icon, "Temperature is fluctuating.", "Temperature Warning!!!", TEMP_NOTIFICATION_ID);
                                hasNotified = true;  // Set the flag to true after notifying

                                mp = MediaPlayer.create(NotificationService.this, Settings.System.DEFAULT_ALARM_ALERT_URI);
                                mp.setLooping(true);
                                mp.start();
                            }
                            // Reset notification if temperature goes below 30°C
                            else if (tempIntVal <= 30 && hasNotified) {
                                hasNotified = false;  // Reset the flag
                                if (mp != null && mp.isPlaying()) {
                                    mp.stop();
                                    mp.release();  // Release resources when no longer needed
                                    mp = null;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
        }

        return START_NOT_STICKY;
    }

    private void showNotification(int drawableResId, String contentText, String subText, int NOTIFICATION_ID) {

        Drawable Drawable = ResourcesCompat.getDrawable(getResources(), drawableResId, null);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) Drawable;
        Bitmap largeIcon = bitmapDrawable.getBitmap();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification;


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentText(contentText)
                    .setSubText(subText)
                    .setChannelId(CHANNEL_ID)
                    .build();

            notificationManager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, "Warning Channel", NotificationManager.IMPORTANCE_HIGH));

        } else {
            notification = new Notification.Builder(this)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentText(contentText)
                    .setSubText(subText)
                    .build();
        }

        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
