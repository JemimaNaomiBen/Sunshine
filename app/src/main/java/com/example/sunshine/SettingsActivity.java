package com.example.sunshine;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
// TODO (1) Add new Activity called SettingsActivity using Android Studio wizard
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // TODO (2) Set setDisplayHomeAsUpEnabled to true on the support ActionBar
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id ==R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
