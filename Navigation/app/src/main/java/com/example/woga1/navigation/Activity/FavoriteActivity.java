package com.example.woga1.navigation.Activity;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.woga1.navigation.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity implements PlaceSelectionListener {
    //즐겨찾기 Activity

    private static final String LOG_TAG = "PlaceSelectionListener";
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private static final int REQUEST_SELECT_PLACE = 1000;
    private TextView attributionsTextView;

    private String destinationName;
    private String longitude;
    private String latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_favoritebar);


//        Button delete = (Button) findViewById(R.id.delete);
        Button homeButton = (Button) findViewById(R.id.homeButton);
        Button companyButton = (Button) findViewById(R.id.companyButton);
        LinearLayout home = (LinearLayout) findViewById(R.id.home);
        LinearLayout company = (LinearLayout) findViewById(R.id.company);
        ImageButton backImageButton = (ImageButton) findViewById(R.id.backimageButton);
        backImageButton.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FavoriteActivity.this, MenuActivity.class));
            }

        });

        final SharedPreferences sharedPreferences = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final TextView homeNametextView = (TextView) findViewById(R.id.homeName);
        final TextView companyNametextView = (TextView) findViewById(R.id.companyName);
//        final TextView homeLatitudetextView = (TextView) findViewById(R.id.homeName);
//        final TextView homeLongitudetextView = (TextView) findViewById(R.id.homeName);
        Log.e("hwhwhw", sharedPreferences.getString("homeName", "").toString());
        Log.e("hwhwhw", sharedPreferences.getString("companyName", "asasassasaas").toString());


        if (sharedPreferences.getString("homeName", "").toString() == "") {
            homeNametextView.setText("");
        } else {
            homeNametextView.setText(sharedPreferences.getString("homeName", "").toString());
        }

        if (sharedPreferences.getString("companyName", "").toString() == "") {
            companyNametextView.setText("");
        } else {
            companyNametextView.setText(sharedPreferences.getString("companyName", "").toString());
        }


        homeButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getString("homeName", "").toString() == "") {
                    try {
                        Intent intent = new PlaceAutocomplete.IntentBuilder
                                (PlaceAutocomplete.MODE_FULLSCREEN)
                                .setBoundsBias(BOUNDS_MOUNTAIN_VIEW)
                                .build(FavoriteActivity.this);
                        startActivityForResult(intent, REQUEST_SELECT_PLACE);
                    } catch (GooglePlayServicesRepairableException |
                            GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                    Log.e("editor", homeNametextView.getText().toString());
                    editor.putString("homeName", destinationName);
                    editor.putString("homeLatitude", latitude);
                    editor.putString("homeLongitude", longitude);
                    editor.commit();
//                    Log.e("homeName", destinationName);
//                    Log.e("homeLatitude", latitude);
//                    Log.e("homeLongitude", longitude);
                    homeNametextView.setText(destinationName);
                } else {
                    Log.e("editor2", homeNametextView.getText().toString());
//                    editor.remove("homeName");
//                    editor.remove("homeLatitude");
//                    editor.remove("homeLongitude");
//                    editor.commit();
                    Intent intent = new Intent(FavoriteActivity.this, ReadyActivity.class);
                    intent.putExtra("destination", sharedPreferences.getString("homeName", "").toString());
                    intent.putExtra("longitude", sharedPreferences.getString("homeLongitude", "").toString());
                    intent.putExtra("latitude", sharedPreferences.getString("homeLatitude", "").toString());
                    Log.e("homeName2", sharedPreferences.getString("homeName", "").toString());
                    Log.e("homeLatitude2", sharedPreferences.getString("homeLongitude", "").toString());
                    Log.e("homeLongitude2", sharedPreferences.getString("homeLatitude", "").toString());
                    startActivityForResult(intent, 1);
                }


//                homeNametextView.setText(sharedPreferences.getString("homeName", "").toString());
                //startActivity(new Intent(FavoriteActivity.this, MenuActivity.class));
            }

        });
        companyButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getString("companyName", "").toString() == "") {
                    Log.e("start", "ture");
                    try {
                        Log.e("1", "abcddee");
                        Intent intent = new PlaceAutocomplete.IntentBuilder
                                (PlaceAutocomplete.MODE_FULLSCREEN)
                                .setBoundsBias(BOUNDS_MOUNTAIN_VIEW)
                                .build(FavoriteActivity.this);
                        startActivityForResult(intent, REQUEST_SELECT_PLACE);

                    } catch (GooglePlayServicesRepairableException |
                            GooglePlayServicesNotAvailableException e) {
                        Log.e("2", "abcddee");
                        e.printStackTrace();

                    }

                    Log.e("editor", companyNametextView.getText().toString());
                    editor.putString("companyName", destinationName);
                    editor.putString("companyLatitude", latitude);
                    editor.putString("companyLongitude", longitude);
                    editor.commit();
                    companyNametextView.setText(destinationName);

//
//                    Log.e("editor","abcddee");
//                    Log.e("editor",destinationName);
//                    editor.putString("companyName", destinationName);
//                    editor.putString("companyLatitude", latitude);
//                    editor.putString("companyLongitude", longitude);
//                    editor.commit();
//                    companyNametextView.setText(destinationName);
                } else {
                    Log.e("second", "ture");
                    Intent intent = new Intent(FavoriteActivity.this, ReadyActivity.class);
                    intent.putExtra("destination", sharedPreferences.getString("companyName", "").toString());
                    intent.putExtra("longitude", sharedPreferences.getString("companyLongitude", "").toString());
                    intent.putExtra("latitude", sharedPreferences.getString("companyLatitude", "").toString());
                    startActivityForResult(intent, 1);
                }
                //startActivity(new Intent(FavoriteActivity.this, MenuActivity.class));
            }

        });
//        delete.setOnClickListener(new EditText.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                editor.clear();
//                editor.commit();
//            }
//
//        });


    }
//        String homeName = sharedPreferences.getString("homeName", "default value");
//        String companyName = sharedPreferences.getString("companyName", "default value");
//        editor.clear();
//        editor.commit();


    @Override
    public void onPlaceSelected(Place place) {
//        Log.i(LOG_TAG, "Place Selected: " + place.getName());
        Toast.makeText(getApplicationContext(),place.getName(),Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(),place.getAddress(),Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(),place.getPhoneNumber(),Toast.LENGTH_SHORT).show();
        changeToLongitudeLatitude(place.getAddress());
//      changeToLongitudeLatitude("서울 영등포구 도림로53길 9");
//        Intent intent = new Intent(FavoriteActivity.this, ReadyActivity.class);
//        intent.putExtra("destination", place.getName());
//        intent.putExtra("longitude",longitude);
//        intent.putExtra("latitude",latitude);
        Log.e("3","sss");
        destinationName = place.getName().toString();
        Log.e("long",place.getName().toString());
        Log.e("long",longitude);
        Log.e("long",latitude);
        Log.e("3",destinationName);
//        startActivityForResult(intent, 1);

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
