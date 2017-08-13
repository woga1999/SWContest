package com.example.woga1.navigation;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {
    //거의 Main화면이다. 맨처음 나오는 Activity
    String[] names = {"","","","","","","","","","","","","","",""} ;
    List<String> destinationLists;

    static final int[] images={R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,
            R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,
            R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,};
    private GridView gv;
    private String longitude;
    private String latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        DestinationList destinationList = DestinationList.getInstance();
//        destinationList.addDestination("신도림역");
//        destinationList.addDestinationDetail("2222");
        Toast.makeText(getApplicationContext(),destinationList.getDestination("신도림역"),Toast.LENGTH_SHORT).show();
        EditText search = (EditText) findViewById(R.id.search);
//        imageGridView = (GridView) findViewById(R.id.gridView);

        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button) findViewById(R.id.button4);
        Button button5 = (Button) findViewById(R.id.button5);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);

        search.setMovementMethod(null);



//        ArrayList<Integer> imageList = new ArrayList<>();
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);

//        ImageGridViewCustomAdapter customAdapter = new ImageGridViewCustomAdapter(this,imageList);
//        imageGridView.setAdapter(customAdapter);
//
//        imageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                Toast.makeText(MenuActivity.this,""+id,Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(MenuActivity.this, ReadyActivity.class));
//            }
//        });

        search.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, SearchActivity.class));
            }

        });

        button1.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, FavoriteActivity.class));
            }

        });

        button2.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, NearActivity.class));
            }

        });

        button3.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, DestinationActivity.class);
                intent.putExtra("destination", names);
                startActivityForResult(intent, 1);
            }

        });

        button4.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, BluetoothActivity.class);
                startActivityForResult(intent, 1);
            }

        });

//        SharedPreferences test = getSharedPreferences("pref", MODE_PRIVATE);
//        String firstData = test.getString("First", "a");
////        Toast.makeText(getApplicationContext(),firstData,Toast.LENGTH_SHORT).show();
//
//        SharedPreferences sharedPreferences = getSharedPreferences("pref", Activity.MODE_PRIVATE);
//        Gson gson = new Gson();
//        String listToString = sharedPreferences.getString("First", "a");
//        List<String> destinationLists;
//        destinationLists=gson.fromJson(listToString,List.class);
//        for(int i=0; i<destinationLists.size() ; i++)
//        {
//            Log.e("test",destinationLists.get(i));
//        }

        SharedPreferences sharedPreferences = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String listToString = sharedPreferences.getString("Destination", null);
        if(listToString != null) {
            Log.e("test","null이 아님");
            Gson gson = new Gson();
            List<String> destinationLists;
            destinationLists = gson.fromJson(listToString, List.class);

            for (int i = 0; i < destinationLists.size(); i++) {
                Log.e("destination", destinationLists.get(i));
                names[i] = destinationLists.get(i);
            }

        }
        gv = (GridView) findViewById(R.id.gridView);

        //Adapter
        ImageGridViewCustomAdapter adapter = new ImageGridViewCustomAdapter(this, getImageandText());
        gv.setAdapter(adapter);

//        ImageGridViewCustomAdapter customAdapter = new ImageGridViewCustomAdapter(this,imageList);
//        imageGridView.setAdapter(customAdapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                changeToLongitudeLatitude(names[position]);
//                changeToLongitudeLatitude("서울 영등포구 도림로53길 9");
                Intent intent = new Intent(MenuActivity.this, ReadyActivity.class);
                intent.putExtra("destination", names[position]);
                intent.putExtra("longitude",longitude);
                intent.putExtra("latitude",latitude);
                startActivityForResult(intent, 1);
            }
        });
    }


    private ArrayList<Player> getImageandText()
    {
        ArrayList<Player> players = new ArrayList<Player>();
        for(int i=0; i<15;i++)
        {
            players.add(new Player(names[i],images[i]));
        }

        return players;
    }

    private void changeToLongitudeLatitude(String destinations)
    {
        final Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;
        List<Address> list1 = null;
        String start = "세종대학교";
        String destination = destinations;
        try {
            list = geocoder.getFromLocationName(
                    start, // 지역 이름
                    10); // 읽을 개수
            list1 = geocoder.getFromLocationName(destination, 10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
        }

        if (list != null || list1 !=null) {
            if (list.size() == 0) {
                Toast.makeText(getApplicationContext(),"해당되는 주소 정보는 없습니다", Toast.LENGTH_LONG).show();
                //tv.setText("해당되는 주소 정보는 없습니다");
            }
            else if(list1.size() ==0) {
                Toast.makeText(getApplicationContext(),"해당되는 주소 정보는 없습니다", Toast.LENGTH_LONG).show();
            }
            else
            {
                Address addr = list.get(0);
                double startLat = addr.getLatitude();
                double startLon = addr.getLongitude();
                Address addr1 = list1.get(0);
                double endLat = addr1.getLatitude();
                double endLon = addr1.getLongitude();


                latitude= String.valueOf(endLat);
                longitude= String.valueOf(endLon);
//                Toast.makeText(getApplicationContext(),"start- 위도: "+String.valueOf(startLat)+" 경도: "+String.valueOf(startLon)+"  end- 위도:"+String.valueOf(endLat)+" 경도: "+String.valueOf(endLon), Toast.LENGTH_LONG).show();
                //tv.setText(list.get(0).toString());
                //          list.get(0).getCountryName();  // 국가명
                //          list.get(0).getLatitude();        // 위도
                //          list.get(0).getLongitude();    // 경도
            }
        }
    }

}
