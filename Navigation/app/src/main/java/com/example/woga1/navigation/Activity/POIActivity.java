package com.example.woga1.navigation.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.woga1.navigation.R;

public class POIActivity extends AppCompatActivity {
    //POI기능 , 주변검색 할 때 나오는 Activity(주유소 등등)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi);
    }
}
