package com.example.woga1.navigation.SaveData;

import android.content.Intent;
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

import com.example.woga1.navigation.MainActivity;
import com.example.woga1.navigation.Navigation.ReadyActivity;
import com.example.woga1.navigation.R;

import java.io.IOException;
import java.util.List;

public class DestinationActivity extends AppCompatActivity {
    //최근목적지를 나타내는 Activity

    private String longitude;
    private String latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);


        Intent intent = getIntent();
        final String[] destinationArrays = intent.getStringArrayExtra("destination");

        //customAction바
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_destinationbar);

        //customAction바의 backButton
        ImageButton backImageButton = (ImageButton) findViewById(R.id.backimageButton);
        backImageButton.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DestinationActivity.this, MainActivity.class));
            }

        });

        //customAction바의 편집버튼
        Button editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.e("clear","완료");
            }

        });


        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, destinationArrays) ;
        ListView listview = (ListView) findViewById(R.id.listview1) ;
        listview.setAdapter(adapter) ;

        //리스트뷰 클릭이벤트
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                if(destinationArrays[position].equals(""))
                {
                    Log.e("position", "빈칸");
                }
                else {
                    //리스트뷰 클릭하면 ReadyActivity로 넘어가기
                    String strText = (String) parent.getItemAtPosition(position);
                    changeToLongitudeLatitude(destinationArrays[position]);
                    Intent intents = new Intent(DestinationActivity.this, ReadyActivity.class);
                    intents.putExtra("destination", strText);
                    intents.putExtra("longitude", longitude);
                    intents.putExtra("latitude", latitude);
                    //Toast.makeText(getApplicationContext(), strText, Toast.LENGTH_SHORT).show();
                    startActivityForResult(intents, 1);

                }
            }
        }) ;

    }

    //목적지를 위도경도로 변환하기
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
//                Toast.makeText(getApplicationContext(),"해당되는 주소 정보는 없습니다", Toast.LENGTH_LONG).show();
            }
            else if(list1.size() ==0) {
//                Toast.makeText(getApplicationContext(),"해당되는 주소 정보는 없습니다", Toast.LENGTH_LONG).show();
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

            }
        }
    }
}
