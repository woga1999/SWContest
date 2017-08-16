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
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements PlaceSelectionListener {
    //검색할 때 나오는 Activity
    private static final String LOG_TAG = "PlaceSelectionListener";
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private static final int REQUEST_SELECT_PLACE = 1000;
    private TextView locationTextView;
    private TextView attributionsTextView;
    private String[] names = {"","","","","","","","","","","","","","",""} ;
    private String longitude;
    private String latitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button) findViewById(R.id.button4);
        EditText search = (EditText) findViewById(R.id.search);
        search.setMovementMethod(null);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_searchbar);

        ImageButton backImageButton = (ImageButton) findViewById(R.id.backimageButton);


        search.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder
                            (PlaceAutocomplete.MODE_FULLSCREEN)
                            .setBoundsBias(BOUNDS_MOUNTAIN_VIEW)
                            .build(SearchActivity.this);
                    startActivityForResult(intent, REQUEST_SELECT_PLACE);
                } catch (GooglePlayServicesRepairableException |
                        GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }

        });

        backImageButton.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this, MenuActivity.class));
            }

        });

        button1.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this, DestinationActivity.class));
            }

        });

        button2.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this, FavoriteActivity.class));
            }

        });

        button3.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this, DetailSearchActivity.class));
            }

        });

        SharedPreferences sharedPreferences = getSharedPreferences("pref", Activity.MODE_PRIVATE);
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

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, names) ;

        ListView listview = (ListView) findViewById(R.id.destinationListView) ;
        listview.setAdapter(adapter) ;

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                // get TextView's Text.
                String strText = (String) parent.getItemAtPosition(position) ;
                changeToLongitudeLatitude(names[position]);
//                changeToLongitudeLatitude("서울 영등포구 도림로53길 9");
                Intent intent = new Intent(SearchActivity.this, ReadyActivity.class);
                intent.putExtra("destination", names[position]);
                intent.putExtra("longitude",longitude);
                intent.putExtra("latitude",latitude);
                startActivityForResult(intent, 1);
                // TODO : use strText
            }
        }) ;
    }


    @Override
    public void onPlaceSelected(Place place) {
//        Log.i(LOG_TAG, "Place Selected: " + place.getName());
        Toast.makeText(getApplicationContext(),place.getName(),Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(),place.getAddress(),Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(),place.getPhoneNumber(),Toast.LENGTH_SHORT).show();
        changeToLongitudeLatitude(place.getAddress());
//      changeToLongitudeLatitude("서울 영등포구 도림로53길 9");
        Intent intent = new Intent(SearchActivity.this, ReadyActivity.class);
        intent.putExtra("destination", place.getName());
        intent.putExtra("longitude",longitude);
        intent.putExtra("latitude",latitude);
        startActivityForResult(intent, 1);

        if (!TextUtils.isEmpty(place.getAttributions())){
            attributionsTextView.setText(Html.fromHtml(place.getAttributions().toString()));
        }
    }

    @Override
    public void onError(Status status) {
        Log.e(LOG_TAG, "onError: Status = " + status.toString());
        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                this.onPlaceSelected(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                this.onError(status);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void changeToLongitudeLatitude(CharSequence destinations)
    {
        final Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;
        List<Address> list1 = null;
        String start = "세종대학교";
        String destination = destinations.toString();
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
