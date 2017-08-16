package com.example.woga1.navigation;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.skp.Tmap.TMapView.TILETYPE_HDTILE;
public class ReadyActivity extends AppCompatActivity {
    //Navigation전 화면으로 나오는 Activity  경로안내 Activity
//    static final String[] names = {"신도림역","초지역","화정역","","","","","","","","",""} ;

    RelativeLayout relativeLayout = null;
    TMapView tmapview;
    public TMapPolyLine tpolyline;
    ArrayList<TMapPoint> passList = new ArrayList<TMapPoint>();
    ArrayList<Integer> trunTypeList = new ArrayList<Integer>();
    ArrayList<String> des = new ArrayList<String>();
    int totalDistance;
    int totalTime;
    RelativeLayout mapview = null;
    String description;
    String longitude;
    String  latitude;
    String destinationName;
    private static final String TAG = "RoadTracker";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable());


        Intent intent = getIntent();
        destinationName = intent.getExtras().getString("destination");
        longitude  = intent.getExtras().getString("longitude");
        latitude = intent.getExtras().getString("latitude");


        setContentView(R.layout.activity_ready);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_readybar);

        one();

        Button startNavigation = (Button) findViewById(R.id.startNavigation);
        ImageButton backImageButton = (ImageButton) findViewById(R.id.backimageButton);
        startNavigation.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(ReadyActivity.this, NavigationActivity.class));
                Intent intent = new Intent(ReadyActivity.this, NavigationActivity.class);
                intent.putExtra("destination", destinationName);
                intent.putExtra("longitude",longitude);
                intent.putExtra("latitude",latitude);
                startActivityForResult(intent, 1);
            }
        });
        relativeLayout = new RelativeLayout(this);
        mapview = (RelativeLayout) findViewById(R.id.mapview);
        //sendBroadcast(new Intent("com.skt.intent.action.GPS_TURN_ON")); //GPS를 켜놓지 않아도 현재위치를 받아와서 출발지로 인식한다.
        //alertCheckGPS();
//        execute();
        execute(Double.parseDouble(longitude), Double.parseDouble(latitude));


        backImageButton.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReadyActivity.this, MenuActivity.class));
            }

        });
        alertCheckGPS();
        if( !isNetworkConnected(this) ){
            Toast.makeText(getApplicationContext(),"YEs",Toast.LENGTH_LONG).show();
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("네트워크 연결 오류").setMessage("네트워크 연결 상태 확인 후 다시 시도해 주십시요.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick( DialogInterface dialog, int which )
                        {
                            finish();
                        }
                    }).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"네트워크 연결완료",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("onStart","true");
//        alertCheckGPS();
//        if( !isNetworkConnected(this) ){
//            Toast.makeText(getApplicationContext(),"YEs",Toast.LENGTH_LONG).show();
//            new AlertDialog.Builder(this)
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setTitle("네트워크 연결 오류").setMessage("네트워크 연결 상태 확인 후 다시 시도해 주십시요.")
//                    .setPositiveButton("확인", new DialogInterface.OnClickListener()
//                    {
//                        @Override
//                        public void onClick( DialogInterface dialog, int which )
//                        {
//                            finish();
//                        }
//                    }).show();
//        }
//        else{
//            Toast.makeText(getApplicationContext(),"NO",Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        Log.e("onRestart","true");
        alertCheckGPS();
        // Activity being restarted from stopped state
        if( !isNetworkConnected(this) ){
            Toast.makeText(getApplicationContext(),"YEs",Toast.LENGTH_LONG).show();
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("네트워크 연결 오류").setMessage("네트워크 연결 상태 확인 후 다시 시도해 주십시요.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick( DialogInterface dialog, int which )
                        {
                            finish();
                        }
                    }).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"네트워크 연결완료",Toast.LENGTH_LONG).show();
        }
    }

    public boolean isNetworkConnected(Context context){
        boolean isConnected = false;

        ConnectivityManager manager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mobile.isConnected() || wifi.isConnected()){
            isConnected = true;
        }else{
            isConnected = false;
        }
        return isConnected;
    }

    public void one()
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
        if(latitude==null)
        {
            Log.e("latitude","null이다");
            for(int i=0; i<destinationLists.size(); i++)
            {
                if(destinationName.equals(destinationLists.get(i)))
                {
                    latitude = latitudeLists.get(i);
                    longitude = longitudeLists.get(i);
                }
            }
        }
        destinationLists.add(0,destinationName);
        latitudeLists.add(0,latitude);
        longitudeLists.add(0,longitude);

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

        for(int i=0; i<destinationLists.size(); i++) {
            Log.e("destination~", destinationLists.get(i));
            Log.e("destination~", latitudeLists.get(i));
            Log.e("destination~", longitudeLists.get(i));
        }

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

    public void execute(double longitude, double latitude) {
        //위도 경도를 받아서 지도상에 띄움

        //sendBroadcast(new Intent("com.skt.intent.action.GPS_TURN_ON"));
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        tmapview = new TMapView(this);
        TMapGpsManager tmapgps = new TMapGpsManager(this);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);
        tmapgps.OpenGps();
        TMapPoint point = tmapgps.getLocation();
        TMapPoint tpoint1 = new TMapPoint(37.550447, 127.073118);
        TMapPoint tpoint2 = new TMapPoint(latitude, longitude);
//        TMapPoint tpoint2 = new TMapPoint(35.666565, 127.069235);

        tpolyline = new TMapPolyLine();
        TMapMarkerItem tItem = new TMapMarkerItem();
        TMapData tmapdata = new TMapData();
//        tmapview.setLocationPoint(tpoint1.getLongitude(),tpoint1.getLatitude());
        tmapview.setMapPosition(TMapView.POSITION_NAVI);

        tItem.setTMapPoint(tpoint1);
        tItem.setName("뚝섬유원지");
//        tItem.setName("신도림역");

        tItem.setVisible(TMapMarkerItem.VISIBLE);
        TMapMarkerItem tItem2 = new TMapMarkerItem();
        tItem2.setTMapPoint(tpoint2);
        tItem2.setName("세종대학교");
        passList= getJsonData(tpoint1,tpoint2);
        tmapview.addMarkerItem("1", tItem);
        //tmapview.addMarkerItem("2", tItem2);

//        TMapTapi tmaptapi = new TMapTapi(this);
//        tmaptapi.setSKPMapAuthentication ("cad2cc9b-a3d5-3c32-8709-23279b7247f9");
        tmapview.setTileType(TILETYPE_HDTILE);
        tmapview.setSKPMapApiKey("cad2cc9b-a3d5-3c32-8709-23279b7247f9");
        tmapview.setCompassMode(true);
        tmapview.setIconVisibility(true); //현재위치 파랑마커표시
//        tmapview.setZoomLevel(11);
        setMapView(totalDistance);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        tmapview.setTrackingMode(true); //화면중심을 단말의 현재위치로 이동시켜주는 트래킹 모드
        tmapview.setPathRotate(true); //나침반 회전 시 출,도착 아이콘을 같이 회전시킬지 여부를 결정합니다.

        //tpolyline = tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, tpoint1, tpoint2, passList, 0);
        tmapview.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {
            }
        });
        for(int i=0; i<passList.size(); i++)
        {
            TMapPoint passpoint = new TMapPoint(passList.get(i).getLatitude(), passList.get(i).getLongitude());

            TMapMarkerItem item = new TMapMarkerItem();
            item.setCanShowCallout(true); //풍선뷰 사용여부
            if(des.get(i) != null) {
                item.setCalloutTitle(des.get(i));
            }
            item.setTMapPoint(passpoint);
            tmapview.addMarkerItem(String.valueOf(i), item);

        }

        String LineID = tpolyline.getID();
        tmapview.addTMapPolyLine(LineID, tpolyline);
//        mapview.addView(tmapview);
//        setContentView(mapview);
//        relativeLayout.addView(tmapview);
//        setContentView(relativeLayout);
        double distance = totalDistance/1000;

//        Toast.makeText(this, String.valueOf(distance)+"km"+String.valueOf(totalTime)+"초", Toast.LENGTH_SHORT).show();

        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, tpoint1, tpoint2,  new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
//폴리 라인을 그리고 시작, 끝지점의 거리를 산출하여 로그에 표시한다
                tmapview.addTMapPath(polyLine);
                double wayDistance = polyLine.getDistance();
                Log.d(TAG, "Distance: " + wayDistance + "M");
            }
        });
        mapview.addView(tmapview);
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

    public ArrayList<TMapPoint> getJsonData(final TMapPoint startPoint, final TMapPoint endPoint)
    {
        Thread thread = new Thread() {
            @Override
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();

                String urlString = "https://apis.skplanetx.com/tmap/routes?version=1&format=json&appKey=cad2cc9b-a3d5-3c32-8709-23279b7247f9";
                //String urlString = "https://apis.skplanetx.com/tmap/routes/pedestrian?callback=&bizAppId=&version=1&format=json&appKey=e2a7df79-5bc7-3f7f-8bca-2d335a0526e7";

                TMapPolyLine jsonPolyline = new TMapPolyLine();
                jsonPolyline.setLineColor(Color.RED);
                jsonPolyline.setLineWidth(2);

                // &format={xml 또는 json}
                try{
                    URI uri = new URI(urlString);

                    HttpPost httpPost = new HttpPost();
                    httpPost.setURI(uri);

                    List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("startX", Double.toString(startPoint.getLongitude())));
                    nameValuePairs.add(new BasicNameValuePair("startY", Double.toString(startPoint.getLatitude())));

                    nameValuePairs.add(new BasicNameValuePair("endX", Double.toString(endPoint.getLongitude())));
                    nameValuePairs.add(new BasicNameValuePair("endY", Double.toString(endPoint.getLatitude())));

                    nameValuePairs.add(new BasicNameValuePair("startName", "출발지"));
                    nameValuePairs.add(new BasicNameValuePair("endName", "도착지"));

                    nameValuePairs.add(new BasicNameValuePair("reqCoordType", "WGS84GEO"));
                    nameValuePairs.add(new BasicNameValuePair("resCoordType", "WGS84GEO"));

                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    int code = response.getStatusLine().getStatusCode();
                    String message = response.getStatusLine().getReasonPhrase();
                    Log.d(TAG, "run: " + message);
                    String responseString;
                    if(response.getEntity() != null)
                        responseString = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
                    else
                        return;
                    String strData = "";

                    Log.d(TAG, "0\n");
                    JSONObject jAr = new JSONObject(responseString);
                    //JSONArray jAr2 = new JSONArray(responseString);
                    Log.d(TAG, "1\n");
                    JSONArray features = jAr.getJSONArray("features");
                    passList = new ArrayList<>();

                    for(int i=0; i<features.length(); i++)
                    {
                        JSONObject root = features.getJSONObject(i);
                        JSONObject properties = root.getJSONObject("properties");
                        if(i == 0){
                            //JSONObject properties = root.getJSONObject("properties");
                            totalDistance += properties.getInt("totalDistance");
                            totalTime += properties.getInt("totalTime");
                        }
                        else
                        {
                            String description = properties.getString("description");
                            des.add(description);
                        }
                        JSONObject geometry = root.getJSONObject("geometry");
                        JSONArray coordinates = geometry.getJSONArray("coordinates");

                        String geoType = geometry.getString("type");
                        if(geoType.equals("Point"))
                        {
                            double lonJson = coordinates.getDouble(0);
                            double latJson = coordinates.getDouble(1);

                            Log.d(TAG, "-");
                            Log.d(TAG, lonJson+","+latJson+"\n");
                            TMapPoint point = new TMapPoint(latJson, lonJson);
                            passList.add(point);

                        }
                        if(geoType.equals("LineString"))
                        {
                            if(i==0) {
                                for (int j = 0; j < coordinates.length(); j++) {
                                    JSONArray JLinePoint = coordinates.getJSONArray(j);
                                    double lonJson = JLinePoint.getDouble(0);
                                    double latJson = JLinePoint.getDouble(1);

                                    Log.d(TAG, "-");
                                    Log.d(TAG, lonJson + "," + latJson + "\n");
                                    TMapPoint point = new TMapPoint(latJson, lonJson);
                                    passList.add(point);
                                }
                            }
                        }
                    }

                    //DashPathEffect dashPath2 = new DashPathEffect(new float[]{0,0}, 0); //실선

                    /*
                    JSONObject test = features.getJSONObject(0);
                    JSONObject properties = test.getJSONObject("properties");
                    Log.d(TAG, "3\n");
                    //JSONObject index = properties.getJSONObject("index");
                    String nodeType = properties.getString("nodeType");
                    Log.d(TAG, "4 " + nodeType+"\n");
                    if(nodeType.equals("POINT")){
                        String turnType = properties.getString("turnType");
                        Log.d(TAG, "5 " + turnType+"\n");
                    }
                    */

                    // 하위 객체에서 데이터를 추출
                    //strData += features.getString("name") + "\n";


                } catch (URISyntaxException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    e.printStackTrace();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        };
        thread.start();

        try{
            thread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return passList;
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

    private void alertCheckGPS() { //gps 꺼져있으면 켤 껀지 체크
//        Intent intent = new Intent(NoticeActivity.this, gpsCheck.class);
//        startActivityForResult(intent, 1);
        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Handlear을 이용하시려면 \n[위치] 권한을 허용해 주세요")
                    .setCancelable(false)
                    .setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    moveConfigGPS();
                                }
                            });
//                    .setNegativeButton("취소",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
//                                }
//                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"GPS 연결완료",Toast.LENGTH_LONG).show();
        }
    }

    // GPS 설정화면으로 이동
    private void moveConfigGPS() {
        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionsIntent);
    }
}
