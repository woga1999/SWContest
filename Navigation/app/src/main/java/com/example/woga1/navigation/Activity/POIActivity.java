package com.example.woga1.navigation.Activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.example.woga1.navigation.GasStationFragment;
import com.example.woga1.navigation.R;

public class POIActivity extends AppCompatActivity {
    //POI기능 , 주변검색 할 때 나오는 Activity(주유소 등등)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi);

        GasStationFragment customListFrgmt = (GasStationFragment) getSupportFragmentManager().findFragmentById(R.id.listFragment);
        customListFrgmt.addItem(ContextCompat.getDrawable(this, R.drawable.left),
                "New Box", "New Account Box Black 36dp") ;


    }
}
