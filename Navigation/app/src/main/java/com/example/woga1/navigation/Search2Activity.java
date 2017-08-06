package com.example.woga1.navigation;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * Created by woga1 on 2017-08-02.
 */

public class Search2Activity extends AppCompatActivity {
    EditText start,end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);

        start = (EditText)findViewById(R.id.editText1);
        end = (EditText)findViewById(R.id.editText2);

//        Intent intent = new Intent(SearchActivity.this, RoadActivity.class);
//        startActivity(intent);

    }

    public void btnStart(View v) {
        final Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;
        List<Address> list1 = null;
        String str = start.getText().toString();
        String str1 = end.getText().toString();
        try {
            list = geocoder.getFromLocationName(
                    str, // 지역 이름
                    10); // 읽을 개수
            list1 = geocoder.getFromLocationName(str1, 10);
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

                Toast.makeText(getApplicationContext(),"start- 위도: "+String.valueOf(startLat)+" 경도: "+String.valueOf(startLon)+"  end- 위도:"+String.valueOf(endLat)+" 경도: "+String.valueOf(endLon), Toast.LENGTH_LONG).show();
                //tv.setText(list.get(0).toString());
                //          list.get(0).getCountryName();  // 국가명
                //          list.get(0).getLatitude();        // 위도
                //          list.get(0).getLongitude();    // 경도
            }
        }

    }

}
