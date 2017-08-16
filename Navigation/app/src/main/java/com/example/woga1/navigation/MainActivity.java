package com.example.woga1.navigation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }


//        int r = Radius(37.55032216157358, 127.07338783732584, 37.540523224299555,127.06916350022186 );
        Intent intent = new Intent(MainActivity.this, RoadActivity.class);
        startActivity(intent);
    }
    public int Radius(double pointLat, double pointLon, double myLat, double myLon)
    {
        double lat, lon;
        int lon_dist, lat_dist;
        int DIV_VALUE = 10000000;
        if(pointLat > myLat && pointLon > myLon )
        {
            lat = (pointLat - myLat)/(double)DIV_VALUE;
            lon = (pointLon - myLon)/(double)DIV_VALUE;
        }
        else {
            lat = (myLat-pointLat)/(double)DIV_VALUE;
            lon = (myLon-pointLon)/(double)DIV_VALUE;
        }

        int rad = (int)lon;
        int min = (int)(lon-rad)*60;
        double sec = ((lon-rad)*60 - min)*60;
        lon_dist = (int)((rad * 88.8) + (min*1.48) + (sec*0.025)) * 1000; // m단위

/*
위도에 대한 도분초및 거리 계산
*/
        rad = (int)lat;
        min = (int)(lat-rad)*60;
        sec = ((lat-rad)*60 - min)*60;
        lat_dist = (int)((rad * 111) + (min*1.85) + (sec*0.031)) * 1000; // m단위

//        if( nCmpLat == 0 ){ // 원 형태의 구역반경
//            // 직선거리만을 조건으로 한다.
        int realDist = (int)Math.sqrt((lon_dist*lon_dist)+(lat_dist*lat_dist));

        return realDist;
    }
}
