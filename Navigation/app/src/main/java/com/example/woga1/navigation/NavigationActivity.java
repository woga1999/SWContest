package com.example.woga1.navigation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class NavigationActivity extends Activity implements TMapGpsManager.onLocationChangedCallback{
    //Navigation화면으로 나오는 Activity
    TMapGpsManager tmapgps = null;
    ImageButton stopButton;
    TextView destinationText;
    RelativeLayout relativeLayout = null;
    TMapView tmapview;
    public TMapPolyLine tpolyline;
    ArrayList<TMapPoint> passList = new ArrayList<TMapPoint>();
    ArrayList<Integer> turnTypeList = new ArrayList<Integer>();
    ArrayList<Integer> meterList = new ArrayList<>();
    ArrayList<String> des = new ArrayList<String>();
    RelativeLayout mapView = null;
    String longitude;
    String  latitude;
    String startLat, startLon;
    int totalDistance;
    int totalTime;
    int type = -1;
    int count = 0;
    public Location nowPlace = null;
    boolean stopCheck=false;
    private String tmapAPI = "cad2cc9b-a3d5-3c32-8709-23279b7247f9";
    private static final String TAG = "NavigationActivity";
    @Override
    public void onLocationChange(Location location) {

        nowPlace = location;
        tmapview.setLocationPoint(nowPlace.getLongitude(), nowPlace.getLatitude());
        tmapview.setTrackingMode(true);
        double speed = location.getSpeed();
        tmapview.setCompassMode(true);
        tmapview.setMarkerRotate(true);
        //textview.setText(String.valueOf(speed));
        type = checkArea(nowPlace, count);
        if(type == 13 )
        {
            if(stopCheck == false){
                Toast.makeText(getApplicationContext(), "우회", Toast.LENGTH_SHORT).show();}
            //count++; 카운트 수 추가는 반경 3m내에서 추가
            else if(stopCheck == true){count++;  stopCheck = false;}
        }
        else if(type == 12)
        {
            if(stopCheck == false){
                Toast.makeText(getApplicationContext(), "좌회", Toast.LENGTH_SHORT).show(); }

            else if(stopCheck == true){count++; stopCheck = false;}
        }
        else if(type == 14 ){
            if(stopCheck == false){
                Toast.makeText(getApplicationContext(), "유턴", Toast.LENGTH_SHORT).show();}
            else if(stopCheck == true){count++; tmapview.setCompassMode(true); tmapview.setMarkerRotate(true); stopCheck = false;}
        }
        else if(type == 11 ){
            Toast.makeText(getApplicationContext(), "직진", Toast.LENGTH_SHORT).show();
            if(stopCheck == true){count++; tmapview.setCompassMode(true); tmapview.setMarkerRotate(true); stopCheck = false;}
        }
        else if(type == 200 )
        {
            if(stopCheck == false){
                Toast.makeText(getApplicationContext(), "출발", Toast.LENGTH_SHORT).show();}
            else if(stopCheck == true){count++;  stopCheck = false;}
        }
        else if(type == 201 )
        {
            if(stopCheck == false){
                Toast.makeText(getApplicationContext(), "도착", Toast.LENGTH_SHORT).show();}
            else if(stopCheck == true){count++; stopCheck = false;}
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Intent intent = getIntent();
        final String name = intent.getExtras().getString("destination");
        longitude  = intent.getExtras().getString("endLongitude");
        latitude = intent.getExtras().getString("endLatitude");
        startLat = intent.getExtras().getString("startLatitude");
        startLon = intent.getExtras().getString("startLongitude");
        Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
        mapView = (RelativeLayout) findViewById(R.id.mapview);
        destinationText = (TextView) findViewById(R.id.destination);
        destinationText.setText(name);
        stopButton = (ImageButton) findViewById(R.id.imageButton2);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigationActivity.this, StopNavigation.class);
//                intent.putExtra("data", "Test Popup");
                startActivityForResult(intent, 1);

            }
        });
        double instantLat = Double.parseDouble(startLat);
        double instantLon = Double.parseDouble(startLon);
        TMapPoint startPoint = new TMapPoint(Double.parseDouble(startLat),Double.parseDouble(startLon));
        TMapPoint endPoint = new TMapPoint(Double.parseDouble(longitude),Double.parseDouble(latitude));
        TMapData tmapdata = new TMapData();
//        relativeLayout = new RelativeLayout(this);


        tmapview = new TMapView(this);

        tmapview.setLocationPoint(startPoint.getLongitude(),startPoint.getLatitude());


        tmapview.setTileType(TILETYPE_HDTILE);
        tmapview.setSKPMapApiKey(tmapAPI);
        tmapview.setCompassMode(true);
        tmapview.setIconVisibility(true); //현재위치 파랑마커표시
        tmapview.setZoomLevel(19);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        tmapview.setTrackingMode(true); //화면중심을 단말의 현재위치로 이동시켜주는 트래킹 모드
        tmapview.setPathRotate(true); //나침반 회전 시 출,도착 아이콘을 같이 회전시킬지 여부를 결정합니다.
        passList = getJsonData(startPoint, endPoint);

        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, startPoint, endPoint,  new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
//폴리 라인을 그리고 시작, 끝지점의 거리를 산출하여 로그에 표시한다
                Bitmap start = BitmapFactory.decodeResource(getResources(),R.drawable.startpurple_resized);
                Bitmap end = BitmapFactory.decodeResource(getResources(),R.drawable.endpurple_resized);
                tmapview.setTMapPathIcon(start, end);
                polyLine.setLineColor(Color.GRAY);
                polyLine.setLineWidth(25);

                tmapview.addTMapPath(polyLine);
                double wayDistance = polyLine.getDistance();
                Log.d(TAG, "Distance: " + wayDistance + "M");
            }
        });

        for(int i=0; i<passList.size(); i++)
        {
            TMapMarkerItem item = new TMapMarkerItem();
            TMapPoint passpoint = new TMapPoint(passList.get(i).getLatitude(), passList.get(i).getLongitude());
            Bitmap mark = null;
            if(turnTypeList.get(i) == 11)
            {
                mark = BitmapFactory.decodeResource(getResources(),R.drawable.direction_11_resized);
            }
            else if(turnTypeList.get(i) == 12)
            {
                mark = BitmapFactory.decodeResource(getResources(),R.drawable.direction_12_resized);
            }
            else if(turnTypeList.get(i) == 13)
            {
                mark = BitmapFactory.decodeResource(getResources(),R.drawable.direction_13_resized);
            }
            else if(turnTypeList.get(i) == 14)
            {
                mark = BitmapFactory.decodeResource(getResources(),R.drawable.direction_14_resized);
            }
            else{
                mark = BitmapFactory.decodeResource(getResources(), R.drawable.tranparent);
            }

            item.setTMapPoint(passpoint);
            item.setIcon(mark);
            tmapview.bringMarkerToFront(item);
            tmapview.addMarkerItem(String.valueOf(i), item);
        }
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.guide_arrow_blue);
        tmapview.setIcon(bitmap);
        mapView.addView(tmapview);
        //execute(startPoint, endPoint);
//        alertCheckGPS();
//        if( !isNetworkConnected(this) ){
//            Toast.makeText(getApplicationContext(),"YES",Toast.LENGTH_LONG).show();
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
//            Toast.makeText(getApplicationContext(),"네트워크 연결완료",Toast.LENGTH_LONG).show();
//        }
    }

    public void btnNowLocation(View v) {
        
        tmapview.setLocationPoint(nowPlace.getLongitude(), nowPlace.getLatitude());
        tmapview.setTrackingMode(true);
    }

    public void btnZoomIn(View v) {
        if (tmapview.getZoomLevel() != 19) {
            tmapview.MapZoomIn();
        }
    }

    public void btnZoomOut(View v) {
        if (tmapview.getZoomLevel() != 7) {
            tmapview.MapZoomOut();
        }
}
    public boolean isNetworkConnected(Context context){
        boolean isConnected = false;

        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
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
        return isConnected;
    }
    public void execute(TMapPoint startpoint, TMapPoint endpoint) {


        tmapview = new TMapView(this);

        tmapview.setLocationPoint(startpoint.getLongitude(),startpoint.getLatitude());


        tmapview.setTileType(TILETYPE_HDTILE);
        tmapview.setSKPMapApiKey(tmapAPI);
        tmapview.setCompassMode(true);
        tmapview.setIconVisibility(true); //현재위치 파랑마커표시
        tmapview.setZoomLevel(19);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        tmapview.setTrackingMode(true); //화면중심을 단말의 현재위치로 이동시켜주는 트래킹 모드
        tmapview.setPathRotate(true); //나침반 회전 시 출,도착 아이콘을 같이 회전시킬지 여부를 결정합니다.
        passList = getJsonData(startpoint, endpoint);


        for(int i=0; i<passList.size(); i++)
        {
            TMapMarkerItem item = new TMapMarkerItem();
            TMapPoint passpoint = new TMapPoint(passList.get(i).getLatitude(), passList.get(i).getLongitude());
            Bitmap mark = null;
            if(turnTypeList.get(i) == 11)
            {
                mark = BitmapFactory.decodeResource(getResources(),R.drawable.direction_11_resized);
            }
            else if(turnTypeList.get(i) == 12)
            {
                mark = BitmapFactory.decodeResource(getResources(),R.drawable.direction_12_resized);
            }
            else if(turnTypeList.get(i) == 13)
            {
                mark = BitmapFactory.decodeResource(getResources(),R.drawable.direction_13_resized);
            }
            else if(turnTypeList.get(i) == 14)
            {
                mark = BitmapFactory.decodeResource(getResources(),R.drawable.direction_14_resized);
            }
            else{
                mark = BitmapFactory.decodeResource(getResources(), R.drawable.tranparent);
            }

            item.setTMapPoint(passpoint);
            item.setIcon(mark);
            tmapview.bringMarkerToFront(item);
            tmapview.addMarkerItem(String.valueOf(i), item);
        }
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.guide_arrow_blue);
        tmapview.setIcon(bitmap);
        mapView.addView(tmapview);
    }

    public int checkArea(Location location, int cnt){ //거리의 1/3 반경 체크
        int signalTurnType = 0;
        double lon = location.getLongitude();
        double lat = location.getLatitude();
        int r ;
        for(int i=0; i<passList.size(); i++)
        {
            float[] results = new float[1];

//            boolean isWithin10km = distanceInMeters < 10000; //10km 넘는지 넘지 않는지
            if(i == passList.size() && i == cnt){
                //도착지 범위는 좀 더 작게
                Location.distanceBetween(passList.get(i).getLatitude(), passList.get(i).getLongitude(), lat, lon, results);
                float distanceInMeters = results[0];
                //r = Radius(passList.get(i).getLatitude(), passList.get(i).getLongitude(), nowPlace.getLatitude(), nowPlace.getLongitude());
                if(distanceInMeters<=10){
                    signalTurnType = turnTypeList.get(i);}
            }
            else if(i == cnt)
            {
                Location.distanceBetween(passList.get(i).getLatitude(), passList.get(i).getLongitude(), lat, lon, results);
                float distanceInMeters = results[0];
                // r = Radius(passList.get(i).getLatitude(), passList.get(i).getLongitude(), nowPlace.getLatitude(), nowPlace.getLongitude());
                //보통 포인트 범위 알림은 40m이고
                if(distanceInMeters<=(meterList.get(i)/3) && distanceInMeters>10){
                    signalTurnType = turnTypeList.get(i);}
                else if(distanceInMeters<=10){
                    stopCheck = true;
                }
            }
        }

        return signalTurnType;
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

                    for(int i=0; i<features.length(); i++) {
                        JSONObject root = features.getJSONObject(i);
                        JSONObject properties = root.getJSONObject("properties");
                        if (i == 0) {
                            //JSONObject properties = root.getJSONObject("properties");
                            totalDistance += properties.getInt("totalDistance");
                            totalTime += properties.getInt("totalTime");

                        }
                        JSONObject geometry = root.getJSONObject("geometry");
                        JSONArray coordinates = geometry.getJSONArray("coordinates");

                        String geoType = geometry.getString("type");
                        if (geoType.equals("Point")) {
                            double lonJson = coordinates.getDouble(0);
                            double latJson = coordinates.getDouble(1);
                            int turnType = properties.getInt("turnType");
                            int meter = properties.getInt("distance");
                            String description = properties.getString("description");
                            Log.e(TAG, "Point-");
                            Log.e(TAG, lonJson + "," + latJson + "\n");
                            TMapPoint point = new TMapPoint(latJson, lonJson);

                            turnTypeList.add(turnType);
                            meterList.add(meter);
                            passList.add(point);
                        }
                    }

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
    @Override
    protected void onStart() {
        super.onStart();
        Log.e("onStart","true");
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        Log.e("onRestart","true");
        tmapgps.OpenGps();
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
    @Override
    protected void onPause() {
        super.onPause();
        // GPS중단
//        tmapgps.CloseGps();
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