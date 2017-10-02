package com.example.woga1.navigation.Search;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.woga1.navigation.R;

public class POIActivity extends AppCompatActivity {
    //POI기능 , 주변검색 할 때 나오는 Activity(주유소 등등)
    Button gasStationButton;
    Button restaurantButton;
    Button martButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_poibar);

        gasStationButton = (Button) findViewById(R.id.gasStationButton);
        restaurantButton = (Button) findViewById(R.id.restaurantButton);
        martButton = (Button) findViewById(R.id.martButton);

        //초기 fragment 시작
        switchFragment("start");

        //POIActivity에서 GasStationFragment의 list에 넣을 때 코드
//        GasStationFragment customListFrgmt = (GasStationFragment) getFragmentManager().findFragmentById(R.id.listFragment);
//        customListFrgmt.addItem(ContextCompat.getDrawable(this, R.drawable.left),
//                "New Box", "New Account Box Black 36dp") ;

        //gasStation 버튼이벤트
        gasStationButton.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                gasStationButton.setTextColor(Color.parseColor("#FF0000"));
                restaurantButton.setTextColor(Color.parseColor("#FFFFFF"));
                martButton.setTextColor(Color.parseColor("#FFFFFF"));
                switchFragment("gasStationButton");
            }
        });
        //restaurant 버튼이벤트
        restaurantButton.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                restaurantButton.setTextColor(Color.parseColor("#FF0000"));
                gasStationButton.setTextColor(Color.parseColor("#FFFFFF"));
                martButton.setTextColor(Color.parseColor("#FFFFFF"));
                switchFragment("restaurantButton");
            }
        });
        //mart 버튼이벤트
        martButton.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                martButton.setTextColor(Color.parseColor("#FF0000"));
                restaurantButton.setTextColor(Color.parseColor("#FFFFFF"));
                gasStationButton.setTextColor(Color.parseColor("#FFFFFF"));
                switchFragment("martButton");
            }
        });
    }

    //Fragment 전환
    public void switchFragment(String clickedButton){
        Fragment fr = new GasStationFragment();

        if(clickedButton == "gasStationButton"){
            fr = new GasStationFragment();
            Toast.makeText(getApplicationContext(),"gasStationButton",Toast.LENGTH_SHORT).show();

        }else if(clickedButton == "restaurantButton"){
            fr = new RestaurantFragment();
            Toast.makeText(getApplicationContext(),"restaurantButton",Toast.LENGTH_SHORT).show();
        }else if(clickedButton == "martButton"){
            fr = new MartFragment();
            Toast.makeText(getApplicationContext(),"martButton",Toast.LENGTH_SHORT).show();
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.listFragment, fr);
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}