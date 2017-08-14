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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class DestinationActivity extends AppCompatActivity {
//최근목적지를 나타내는 Activity

//    static final String[] LIST_MENU = {"홍대", "건대", "세종대학교","어린이대공원역"} ;
    private String longitude;
    private String latitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        Intent intent = getIntent();
        final String[] myStrings = intent.getStringArrayExtra("destination");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_destinationbar);

        ImageButton backImageButton = (ImageButton) findViewById(R.id.backimageButton);
        backImageButton.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DestinationActivity.this, MenuActivity.class));
            }

        });

        Button editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("pref", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
//                String listToString = sharedPreferences.getString("Destination", "a");
//                Gson gson = new Gson();
//                List<String> destinationLists;
//                destinationLists=gson.fromJson(listToString,List.class);
//                destinationLists.add(0,destinationName);
//                String json = gson.toJson(destinationLists);
//                editor.putString("Destination", json);
                editor.clear();
                editor.commit();
                Log.e("clear","완료");
            }

        });

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, myStrings) ;

        ListView listview = (ListView) findViewById(R.id.listview1) ;
        listview.setAdapter(adapter) ;

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                // get TextView's Text.
                String strText = (String) parent.getItemAtPosition(position) ;
                changeToLongitudeLatitude(myStrings[position]);
                Intent intents = new Intent(DestinationActivity.this, ReadyActivity.class);
                intents.putExtra("destination", strText);
                intents.putExtra("longitude",longitude);
                intents.putExtra("latitude",latitude);
                //Toast.makeText(getApplicationContext(),names[position], Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),strText, Toast.LENGTH_SHORT).show();
                startActivityForResult(intents, 1);




                // TODO : use strText
            }
        }) ;

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
