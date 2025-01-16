package com.cyberproject.alpha.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cyberproject.alpha.R;

public class FourthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);
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
            intent = new Intent(this, SecondActivity.class);
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
}