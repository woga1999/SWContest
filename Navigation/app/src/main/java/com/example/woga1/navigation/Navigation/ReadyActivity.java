package com.example.woga1.navigation.Navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.woga1.navigation.MenuActivity;
import com.example.woga1.navigation.R;
import com.google.gson.Gson;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;
import java.util.List;

import static com.example.woga1.navigation.R.id.mapview;
import static com.skp.Tmap.TMapView.TILETYPE_HDTILE;

public class ReadyActivity extends AppCompatActivity {
    //Navigation전 화면으로 나오는 Activity  경로안내 Activity
//    static final String[] names = {"신도림역","초지역","화정역","","","","","","","","",""} ;

    TMapView tmapview = null;
    TMapGpsManager tmapgps = null;
    public TMapPolyLine tpolyline;
    int totalDistance;
    int totalTime;
    RelativeLayout mapView = null;
    double startLatitiude = 0;
    double startLongitude=0;
    String endLongitude;
    String  endLatitude;
    String destinationName;
    private static final String TAG = "ReadyActivity";
    public Location nowPlace = null;
    String start;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // 블루투스용
        // ((MenuActivity)MenuActivity.mContext).sendMessage("100 14.");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable());

        tmapgps = new TMapGpsManager(this);


        Intent intent = getIntent();
        destinationName = intent.getExtras().getString("destination");
        endLongitude  = intent.getExtras().getString("longitude");
        endLatitude = intent.getExtras().getString("latitude");
        nowPlace = nowLocation();
        startLatitiude = nowPlace.getLatitude();
        startLongitude = nowPlace.getLongitude();
//        startLatitiude = 37.517278;
//        startLongitude = 127.040598;
        setContentView(R.layout.activity_ready);
        TMapData tMapData = new TMapData();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_readybar);

        callSharedPreference();
        TextView totalDis = (TextView)findViewById(R.id.distance);
        TextView totaltime = (TextView)findViewById(R.id.time);
        TextView startPointAddress = (TextView) findViewById(R.id.startPointAddress);
        TextView departmentAddress = (TextView) findViewById(R.id.departmentAddress);
        startPointAddress.setText("현재 위치");
        departmentAddress.setText(destinationName);

        Button startNavigation = (Button) findViewById(R.id.startNavigation);
        ImageButton backImageButton = (ImageButton) findViewById(R.id.backimageButton);
        startNavigation.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(ReadyActivity.this, NavigationActivity.class));
                Intent intent = new Intent(ReadyActivity.this, NavigationActivity.class);
                intent.putExtra("destination", destinationName);
                intent.putExtra("endLongitude",endLongitude);
                intent.putExtra("endLatitude",endLatitude);
//                intent.putExtra("startLatitude", String.valueOf(startLatitiude));
//                intent.putExtra("startLongitude", String.valueOf(startLongitude));
                startActivityForResult(intent, 1);
            }
        });
        mapView = (RelativeLayout) findViewById(mapview);
        execute(startLatitiude,startLongitude, Double.parseDouble(endLatitude),Double.parseDouble(endLongitude));
        Log.e("Totaldistance", String.valueOf(totalDistance));
        double instant = totalDistance/(double)1000;
        totalDis.setText(String.valueOf(instant)+"km");
        totaltime.setText(String.valueOf(totalTime/60)+"분");
        backImageButton.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReadyActivity.this, MenuActivity.class));
            }

        });
    }

    public void execute(double startLat, double startLon, double endLatitude, double endLongitude)  {
        //위도 경도를 받아서 지도상에 띄움

        tmapview = new TMapView(this);
        TMapPoint startPoint = new TMapPoint(startLat, startLon);
        TMapPoint endPoint = new TMapPoint(endLatitude, endLongitude);
        PathData pathData = new PathData(startPoint, endPoint);


       TMapData tmapdata = new TMapData();

        tmapview.setLocationPoint(startPoint.getLongitude(),startPoint.getLatitude());
        tmapview.setMapPosition(TMapView.POSITION_NAVI);
        tmapview.setTileType(TILETYPE_HDTILE);
        tmapview.setSKPMapApiKey("cad2cc9b-a3d5-3c32-8709-23279b7247f9");
        tmapview.setCompassMode(true);
//        tmapview.setZoomLevel(11);
        totalDistance= pathData.getTotalDistance();
       // totalDistance = (int)pointDistance(startLat, startLon, endLatitude, endLongitude);
        totalTime = pathData.getTotalTime();
        setMapView(totalDistance);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        tmapview.setTrackingMode(true); //화면중심을 단말의 현재위치로 이동시켜주는 트래킹 모드
        tmapview.setPathRotate(true); //나침반 회전 시 출,도착 아이콘을 같이 회전시킬지 여부를 결정합니다.


        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, startPoint, endPoint,  new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
//폴리 라인을 그리고 시작, 끝지점의 거리를 산출하여 로그에 표시한다

                tmapview.addTMapPath(polyLine);
                double wayDistance = polyLine.getDistance();
                Log.d(TAG, "Distance: " + wayDistance + "M");
            }
        });
        mapView.addView(tmapview);
    }
    public double pointDistance(double lat1, double lon1, double lat2, double lon2){

        double theta, dist;
        theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

        return dist;
    }

    // 주어진 도(degree) 값을 라디언으로 변환
    private double deg2rad(double deg){
        return (double)(deg * Math.PI / (double)180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad){
        return (double)(rad * (double)180d / Math.PI);
    }
    public boolean isNetworkConnected(Context context){
        boolean isConnected = false;

        ConnectivityManager manager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
//        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null) {

                        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnectedOrConnecting()) {
                               // wifi 연결중
                            isConnected = true;
                            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && networkInfo.isConnectedOrConnecting()) {
                                // 모바일 네트워크 연결중
                            isConnected = true;
                           }
                       else
                        {
                            isConnected = false;
                                // 네트워크 오프라인 상태.
                            }
                    } else {
                       // 네트워크 null.. 모뎀이 없는 경우??
            isConnected = false;
                   }


//        if (mobile.isConnected() || wifi.isConnected()){
//            isConnected = true;
//        }else{
//            isConnected = false;
//        }
        return isConnected;
    }

    public void callSharedPreference()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String destinationListToString = sharedPreferences.getString("Destination", null);
        String latitudeListToString = sharedPreferences.getString("Latitude",null);
        String longitudeListToString = sharedPreferences.getString("Longitude",null);

        Gson gson = new Gson();
        List<String> destinationLists = new ArrayList<String>();
        List<String> latitudeLists = new ArrayList<String>();
        List<String> longitudeLists = new ArrayList<String>();
        if(destinationListToString != null) {
            Log.e("test","null이 아니다");
            destinationLists = gson.fromJson(destinationListToString, List.class);
            latitudeLists = gson.fromJson(latitudeListToString, List.class);
            longitudeLists = gson.fromJson(longitudeListToString, List.class);
        }
        if(endLatitude==null)
        {
            Log.e("latitude","null이다");
            for(int i=0; i<destinationLists.size(); i++)
            {
                if(destinationName.equals(destinationLists.get(i)))
                {
                    endLatitude = latitudeLists.get(i);
                    endLongitude = longitudeLists.get(i);
                }
            }
        }
        destinationLists.add(0,destinationName);
        latitudeLists.add(0,endLatitude);
        longitudeLists.add(0,endLongitude);

        String json = gson.toJson(destinationLists);
        editor.putString("Destination", json);
        json = gson.toJson(latitudeLists);
        editor.putString("Latitude", json);
        json = gson.toJson(longitudeLists);
        editor.putString("Longitude", json);

        editor.commit();

//        int d=destinationLists.size();
//        String total2 = String.valueOf(d);
//        Log.e("tests",total2);
//        Log.e("test","사이즈0아님");
//        Toast.makeText(getApplicationContext(),total2, Toast.LENGTH_LONG).show();
        for(int i=1; i<destinationLists.size(); i++)
        {
//            Log.e("destination",destinationLists.get(i));
//            Toast.makeText(getApplicationContext(),destinationLists.get(i), Toast.LENGTH_LONG).show();
            if(destinationName.equals(destinationLists.get(i)))
            {
                destinationLists.remove(i);
                latitudeLists.remove(i);
                longitudeLists.remove(i);
                break;
            }
        }
        json = gson.toJson(destinationLists);
        editor.putString("Destination", json);
        json = gson.toJson(latitudeLists);
        editor.putString("Latitude", json);
        json = gson.toJson(longitudeLists);
        editor.putString("Longitude", json);
        editor.commit(); //완료한다.

//        for(int i=0; i<destinationLists.size(); i++) {
//            Log.e("destination~", destinationLists.get(i));
//            Log.e("destination~", latitudeLists.get(i));
//            Log.e("destination~", longitudeLists.get(i));
//        }

//        Toast.makeText(getApplicationContext(),,Toast.LENGTH_SHORT).show();
//        if(destinationLists.isEmpty())
//        {
//            Log.e("test","사이즈0");
//            destinationLists.add(0,destinationName);
//        }
//        else {

//        }
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        String json = gson.toJson(destinationLists);
//        editor.putString("First", json);

//        editor.clear();

//        for(int i=0; i<destinationLists.size(); i++)//
//        {
//            Log.e("final",destinationLists.get(i));
//        }
    }


    // 경로안내 시작 누르기 전에, 전체경로를 보여주는 지도 셋팅부분
    public void setMapView(int total_distance) {
        int set_zoom_level = 0;                                             // 7-19 레벨 설정가능
        if (total_distance < 1000) set_zoom_level = 17;                    // 1km
        else if (total_distance < 5000) set_zoom_level = 14;               // 5km
        else if (total_distance < 10000) set_zoom_level = 12;              // 10km
        else if (total_distance < 30000) set_zoom_level = 11;              // 30km
        else if (total_distance < 70000) set_zoom_level = 10;              // 70km
        else if (total_distance < 150000) set_zoom_level = 9;              // 150km
        else set_zoom_level = 7;                                            // 그 이상

        tmapview.setZoomLevel(set_zoom_level);
    }


    public Location nowLocation() {
        Location myLocation = null;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            tmapgps = new TMapGpsManager(this);
            tmapgps.setMinDistance(0);
            tmapgps.setMinTime(100);
            tmapgps.OpenGps();
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            myLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (myLocation == null) {
                myLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(myLocation != null)
                {
                    Log.d("myLocation: ", String.valueOf(myLocation.getLatitude()));
                }
            }
            else if(myLocation != null){
//                Criteria criteria = new Criteria();
//                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//                String provider = lm.getBestProvider(criteria, true);
//                myLocation = lm.getLastKnownLocation(provider);
//                Log.d("myLocation: ", String.valueOf(myLocation.getLatitude()));
            }
        }
        return myLocation;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("onStart","true");

    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        Log.e("onRestart","true");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // GPS중단
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {


        super.onDestroy();
        // Thread 종료
    }
}
