package com.example.woga1.navigation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//api 23이상 퍼미션 위치찾기 먹지 않아서 꼭 필요한 코드 먼저실행하지 않으면 어플 처음 깐 핸드폰에서 어플이 죽는다.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // Android M Permission check 
//            if ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ||
//                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            }
//        }
//
//        Intent intent = new Intent(MainActivity.this, RoadActivity.class);
//        startActivity(intent);
    }
}
