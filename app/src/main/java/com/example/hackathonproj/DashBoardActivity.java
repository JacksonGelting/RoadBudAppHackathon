package com.example.hackathonproj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DashBoardActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor mStepCounter;
    private boolean isCounterSensorPresent;
    boolean firstTime = true;
    int stepCount = 0;
    int previousCount = 0;

//    ProgressBar progressBar = findViewById(R.id.progressBar);
    FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ActivityCompat.requestPermissions(DashBoardActivity.this, new String[] {Manifest.permission.ACTIVITY_RECOGNITION},100);

        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){
            mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCounterSensorPresent = true;
        } else{
            isCounterSensorPresent = false;
        }

    }
    public void buttonPush(View view) {
        lastLocation();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            progressBar.setProgress(50, true);
//        }
//<ProgressBar
//        android:id="@+id/progressBar"
//        style="?android:attr/progressBarStyle"
//        android:layout_width="365dp"
//        android:layout_height="392dp"
//        app:layout_constraintBottom_toBottomOf="parent"
//        app:layout_constraintEnd_toEndOf="parent"
//        app:layout_constraintStart_toStartOf="parent"
//        app:layout_constraintTop_toTopOf="parent"
//        app:layout_constraintVertical_bias="0.4" />
//
   }

    // This function is responsable for calculating the coordinates from the GPS
    public double[] lastLocation() {
        double[] coordinates = new double[2];
        TextView text = (TextView) findViewById(R.id.textView2);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashBoardActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},100);
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Geocoder geocoder = new Geocoder(DashBoardActivity.this, Locale.getDefault());
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                text.setText("Latitude: " + addresses.get(0).getLatitude() +
                                        "\nLongitude: " + addresses.get(0).getLongitude() );

                                coordinates[0] = addresses.get(0).getLatitude();
                                coordinates[0] = addresses.get(0).getLongitude();


                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }

                });
        return coordinates;
    }

    // This button is responsible for the notification
    public void buttonPush2(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel1";
            String description = "Your car Needs Maintenance!";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("ch1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ch1")
                .setSmallIcon(R.drawable.car)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);



        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCounterSensorPresent) {
            sensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isCounterSensorPresent) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Not used
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
//        TextView textViewStepCounter = findViewById(R.id.textView3);
//        if (sensorEvent.sensor == mStepCounter) {
//            stepCount = (int) sensorEvent.values[0];
//            textViewStepCounter.setText(String.valueOf(stepCount));
//        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            // Get the current step count
            int currentCount = (int) sensorEvent.values[0];

            // Calculate the step count since the last time this method was called
            int countSinceLastCall = 0;
            if (!firstTime){
                countSinceLastCall = currentCount - previousCount;
            }


            // Update the step count variable
            stepCount += countSinceLastCall;

            // Update the previous count variable
            previousCount = currentCount;

            firstTime = false;
            // Update the step count text view
            TextView stepCountTextView = findViewById(R.id.textView3);
            stepCountTextView.setText(String.valueOf(stepCount));
        }

    }
}
