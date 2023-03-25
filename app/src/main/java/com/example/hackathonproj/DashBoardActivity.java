/*
package com.example.hackathonproj;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
*/
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
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DashBoardActivity extends AppCompatActivity implements SensorEventListener {
    // oil change and oil filter change 4000 miles
    // tire rotation 6000 miles and check brakes
    // air filter replacement 17000 miles
    // every 30,000 miles schedualed maintence


    // MConstants
    //-----------------------
    public final int OILTIME = 40;
    public final int TIREANDBRAKES = 60;
    public final int FILTERREPLACEMENT = 17;
    public final int APPOINTMENTS = 300;


    // Progress Bar
    //------------------------------

    private ProgressBar progressBar;
    private TextView progressText;

    private ProgressBar progressBar2;
    private TextView progressText2;

    private ProgressBar progressBar3;
    private TextView progressText3;

    private ProgressBar progressBar4;
    private TextView progressText4;


    // Pedometer
    //-------------------------------
    private SensorManager sensorManager;
    private Sensor mStepCounter;
    private boolean isCounterSensorPresent;
    boolean firstTime = true;
    int stepCount = 0;
    int previousCount = 0;

    boolean isZero = true;

    //    ProgressBar progressBar = findViewById(R.id.progressBar);
    FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Pedometer
        //-----------------------------------------------------
        ActivityCompat.requestPermissions(DashBoardActivity.this, new String[]
                {Manifest.permission.ACTIVITY_RECOGNITION}, 100);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCounterSensorPresent = true;
        } else {
            isCounterSensorPresent = false;
        }
        // Circular Progress Bar
        // ---------------------------------------------------------------
        setContentView(R.layout.activity_dashboard);

        // set the id for the progressbar and progress text
//            createProgressBar(OILTIME, R.id.progress_bar, R.id.progress_text);
//            createProgressBar(TIREANDBRAKES, R.id.progress_bar2, R.id.progress_text3);
//            createProgressBar(FILTERREPLACEMENT, R.id.progress_bar3, R.id.progress_text4);
//            createProgressBar(APPOINTMENTS, R.id.progress_bar4, R.id.progress_text5);

        progressBar = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // set the limitations for the numeric
                // text under the progress bar
                progressBar.setMax(OILTIME);
                int i = stepCount % OILTIME;


                notification(OILTIME, "Oil Maintenance", "You're Oil needs Change!", i);
                if (i <= OILTIME) {
                    progressText.setText("" + (OILTIME - i));
                    progressBar.setProgress(i);
                    i++;
                    handler.postDelayed(this, 1);
                }
            }
        }, 200);


        // ---------------------------------------------------------------
        progressBar2 = findViewById(R.id.progress_bar2);
        progressText2 = findViewById(R.id.progress_text3);

        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                // set the limitations for the numeric
                // text under the progress bar
                progressBar2.setMax(TIREANDBRAKES);
                int i = stepCount % TIREANDBRAKES;
                notification(TIREANDBRAKES, "Tire Brakes", "You're tires needs Change!", i);
                if (i <= TIREANDBRAKES) {
                    progressText2.setText("" + (TIREANDBRAKES - i));
                    progressBar2.setProgress(i);
                    i++;
                    handler2.postDelayed(this, 1);
                }
            }
        }, 200);
        // ---------------------------------------------------------------
        progressBar3 = findViewById(R.id.progress_bar3);
        progressText3 = findViewById(R.id.progress_text4);

        Handler handler3 = new Handler();
        handler3.postDelayed(new Runnable() {
            @Override
            public void run() {
                // set the limitations for the numeric
                // text under the progress bar
                progressBar3.setMax(APPOINTMENTS);
                int i = stepCount % APPOINTMENTS;
                notification(APPOINTMENTS, "Filter Maintenance", "You're Filters needs Change!", i);
                if (i <= APPOINTMENTS) {
                    progressText3.setText("" + (APPOINTMENTS - i));
                    progressBar3.setProgress(i);
                    i++;
                    handler3.postDelayed(this, 1);
                }
            }
        }, 200);

        // ---------------------------------------------------------------

        progressBar4 = findViewById(R.id.progress_bar4);
        progressText4 = findViewById(R.id.progress_text5);

        Handler handler4 = new Handler();
        handler4.postDelayed(new Runnable() {
            @Override
            public void run() {
                // set the limitations for the numeric
                // text under the progress bar
                progressBar4.setMax(FILTERREPLACEMENT);
                int i = stepCount % FILTERREPLACEMENT;
                notification(FILTERREPLACEMENT, "Appointment Maintenance", "You're Appointment needs Change!", i);
                if (i <= FILTERREPLACEMENT) {
                    progressText4.setText("" + (FILTERREPLACEMENT - i));
                    progressBar4.setProgress(i);
                    i++;
                    handler4.postDelayed(this, 1);
                }
            }
        }, 200);


    }

    public void buttonPush(View view) {
        lastLocation();
    }

    // This function is responsable for calculating the coordinates from the GPS
    public double[] lastLocation() {
        double[] coordinates = new double[2];

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashBoardActivity.this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 100);
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
                                addresses = geocoder.getFromLocation(location.getLatitude(),
                                        location.getLongitude(), 1);

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
        //notification(constant, Message, Message);
    }

    int j = 1;

    public void notification(int constant, String Message1, String Message2, int i) {
        if (i == 0 && !isZero) {
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

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ch1")
                    .setSmallIcon(R.drawable.car)
                    .setContentTitle(Message1)
                    .setContentText(Message2)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Much longer text that cannot fit one line..."))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(1, builder.build());
            isZero = true;
        }
        if (i != 0) {
            isZero = false;
        }
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
            if (!firstTime) {
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

    public void createProgressBar(int Constant, int IDprogressBar, int IDprogressText) {

        progressBar = findViewById(IDprogressBar);
        progressText = findViewById(IDprogressText);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // set the limitations for the numeric
                // text under the progress bar
                progressBar.setMax(Constant);
                int i = stepCount % Constant;
                if (i <= Constant) {
                    progressText.setText("" + (Constant - i));
                    progressBar.setProgress(i);
                    i++;
                    handler.postDelayed(this, 1);
                }
            }
        }, 200);
    }

}
