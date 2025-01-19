package com.cyberproject.alpha.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cyberproject.alpha.NotificationHelper;
import com.cyberproject.alpha.R;
import com.cyberproject.alpha.receivers.NotificationReceiver;

import java.util.Calendar;

public class FourthActivity extends AppCompatActivity {

    EditText editText;
    Button setReminder;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    int ALARM_REQUEST_CODE = 1;
    NotificationReceiver notificationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);

        editText = findViewById(R.id.editText);
        setReminder = findViewById(R.id.setReminder);

        notificationReceiver = new NotificationReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String s = item.getTitle().toString();
        Intent intent;
        if (s.equals("Register")) {
            intent = new Intent(this, FirstActivity.class);
            startActivity(intent);
        } else if (s.equals("Pick Image")) {
            intent = new Intent(
                    this, SecondActivity.class);
            startActivity(intent);
        } else if (s.equals("Snap Image")) {
            intent = new Intent(this, ThirdActivity.class);
            startActivity(intent);
        } else if (s.equals("Reminder")) {
            intent = new Intent(this, FourthActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("com.cyberproject.alpha.receivers.NotificationReceiver");
        registerReceiver(notificationReceiver, filter);
    }

    private void openTimePickerDialog(boolean is24hr) {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calNow = Calendar.getInstance();
                Calendar calSet = (Calendar) calNow.clone();

                calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calSet.set(Calendar.MINUTE, minute);
                calSet.set(Calendar.SECOND, 0);
                calSet.set(Calendar.MILLISECOND, 0);

                if (calSet.compareTo(calNow) <= 0) {
                    calSet.add(Calendar.DATE, 1);
                }
                setAlarm(calSet);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24hr);
        timePickerDialog.setTitle("Choose Time");
        timePickerDialog.show();
    }

    private void setAlarm(@NonNull Calendar calSet) {
        ALARM_REQUEST_CODE++;
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("message", editText.getText().toString());
        pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pendingIntent);
    }

    public void setReminder(View view) {
        openTimePickerDialog(true);
    }
}