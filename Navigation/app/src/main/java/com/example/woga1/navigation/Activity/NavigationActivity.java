package com.example.woga1.navigation.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.woga1.navigation.R;
import com.example.woga1.navigation.StopNavigation;
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
    TMapView tmapview;
    ArrayList<TMapPoint> passList = new ArrayList<TMapPoint>();
    ArrayList<Integer> turnTypeList = new ArrayList<Integer>();
    ArrayList<Integer> meterList = new ArrayList<>();
    ArrayList<String> des = new ArrayList<String>();
    RelativeLayout mapView = null;
    String longitude;
    String  latitude;
    String startLat, startLon;
    double gpsLat, gpsLon;
    TextView totalDis;
    TextView time;
    int totalDistance = 0;
    int totalTime = 0;
    int type = -1;
    int index = 0;
    int count = 0;
    boolean signalStopCheck = false;
    boolean oneMoreAlarm = false;
    public double startPlaceLat, startPlaceLon;
    public Location nowPlace = null;
    private String tmapAPI = "cad2cc9b-a3d5-3c32-8709-23279b7247f9";
    private static final String TAG = "NavigationActivity";


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
        time = (TextView)findViewById(R.id.min);
        mapView = (RelativeLayout) findViewById(R.id.mapview);
        totalDis = (TextView)findViewById(R.id.km);
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
//        gpsLat = Double.parseDouble(startLat);
//        gpsLon = Double.parseDouble(startLon);
        nowPlace = nowLocation();
//        startLatitiude = 37.517278;
//        startLongitude = 127.040598;

        TMapPoint startPoint = new TMapPoint(nowPlace.getLatitude(), nowPlace.getLongitude());
        TMapPoint endPoint = new TMapPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));
//        TMapPoint startPoint = new TMapPoint(37.517278,127.040598);
//        TMapPoint endPoint = new TMapPoint(37.5407625,127.07934279999995);
        TMapData tmapdata = new TMapData();
//        relativeLayout = new RelativeLayout(this);

        tmapview = new TMapView(this);

        tmapview.setLocationPoint(startPoint.getLongitude(),startPoint.getLatitude());

        tmapview.setTileType(TILETYPE_HDTILE);
        tmapview.setSKPMapApiKey(tmapAPI);
        tmapview.setCompassMode(true);
        tmapview.setIconVisibility(true); //현재위치 파랑마커표시
        tmapview.setZoomLevel(17);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        tmapview.setTrackingMode(true); //화면중심을 단말의 현재위치로 이동시켜주는 트래킹 모드
        tmapview.setPathRotate(true); //나침반 회전 시 출,도착 아이콘을 같이 회전시킬지 여부를 결정합니다.
        passList = getJsonData(startPoint, endPoint);
        time.setText(String.valueOf(totalTime/60)+"분");
        Log.e("totalTime",String.valueOf(totalTime/60)+"분");
        totalDis.setText(String.valueOf((double)totalDistance/1000)+"km");
        Log.e("totalDis",String.valueOf(totalDistance/1000)+"km");
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
        execute(startPoint, endPoint);
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

    @Override
    public void onLocationChange(Location location) {

        nowPlace = location;
        gpsLat = nowPlace.getLatitude();
        gpsLon = nowPlace.getLongitude();
        int m = totalDistance - (int)currentDistance(startPlaceLat,startPlaceLon,nowPlace.getLatitude(),nowPlace.getLongitude());
        double speed = location.getSpeed();
        double hour = (m/1000)/location.getSpeed();
        tmapview.setLocationPoint(nowPlace.getLongitude(), nowPlace.getLatitude());
        tmapview.setTrackingMode(true);
        int min = (totalTime/60) - (int)hour * 60;
        totalDis.setText(String.valueOf(m/1000)+"km");
        time.setText(String.valueOf(min)+"분");
        tmapview.setCompassMode(true);
        tmapview.setMarkerRotate(true);
        //textview.setText(String.valueOf(speed));
        //boolean isIn100M = check100M(nowPlace, index);
        double x = currentToPointDistance(nowPlace, index);
        type = checkArea(nowPlace, index, x);
        if(signalStopCheck == false)
        {

            if(x>100)
            {
                oneMoreAlarm = true;
                signalTurnType(type);
            }
            else if(x<=100)
            {
                signalTurnType(type);
            }
        }
        else if(x <= 100 && oneMoreAlarm == true)
        {
            signalStopCheck = false;
            if(signalStopCheck == false)
            {
                signalTurnType(type);
                count = 1;
            }
        }
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
            tmapgps.setProvider(tmapgps.GPS_PROVIDER );
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

    public int checkArea(Location location, int cnt, double distanceInMeters) {
        int turnType = 0;
        int r;
        //double distanceInMeters = currentToPointDistance(location, cnt);
        for(int i= 0; i<passList.size(); i++) {
            if (cnt == passList.size() - 1) {
                //도착지 범위는 좀 더 작게

                //r = Radius(passList.get(i).getLatitude(), passList.get(i).getLongitude(), nowPlace.getLatitude(), nowPlace.getLongitude());
                if (distanceInMeters <= 10) {
                    turnType = turnTypeList.get(i);
                    signalStopCheck = false;
                }
            }
            else if(i == 0)
            {
                if (distanceInMeters <= 20) {
                    turnType = turnTypeList.get(0);
                }
            }
            else
            {
                if (distanceInMeters <= 100 && distanceInMeters > 10) {
                    turnType = turnTypeList.get(cnt);
                    if(count == 1 ){
                        oneMoreAlarm = false;}
                }
                if (distanceInMeters > 100) {
                    turnType = turnTypeList.get(cnt);
                    oneMoreAlarm = true;
                }
                else if (distanceInMeters <= 10) {
                    signalStopCheck = false;
                    oneMoreAlarm = false;
                    index++;
                    count = 0;
                }
            }
        }
        return turnType;
    }

    public double currentToPointDistance(Location myLocation, int cnt)
    {
        double distance = 0;
        for(int i =0; i<passList.size(); i++) {
            if(i == cnt) {
                distance = currentDistance(passList.get(i).getLatitude(), passList.get(i).getLongitude(), myLocation.getLatitude(), myLocation.getLongitude());
            }
        }
        return distance;
    }

    public void btnNowLocation(View v) {

        tmapview.setLocationPoint(gpsLon, gpsLat);
        tmapview.setTrackingMode(true);
        Log.e("현재 위도 값", "성공");
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
        tmapview.setZoomLevel(18);
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
                mark = BitmapFactory.decodeResource(getResources(),R.drawable.go);
            }
            else if(turnTypeList.get(i) == 12)
            {
                mark = BitmapFactory.decodeResource(getResources(),R.drawable.left);
            }
            else if(turnTypeList.get(i) == 13)
            {
                mark = BitmapFactory.decodeResource(getResources(),R.drawable.right);
            }
            else if(turnTypeList.get(i) == 14)
            {
                mark = BitmapFactory.decodeResource(getResources(),R.drawable.uturn);
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


    public void signalTurnType(int type) {
        if (signalStopCheck == false || oneMoreAlarm == true) {
            switch (type) {
                case 200:
                    Toast.makeText(getApplicationContext(), "출발", Toast.LENGTH_SHORT).show();
                    break;
                case 201:
                    Toast.makeText(getApplicationContext(), "도착", Toast.LENGTH_SHORT).show();
                    break;
                case 11:
                    Toast.makeText(getApplicationContext(), "직진", Toast.LENGTH_SHORT).show();
                    break;
                case 12:
                    Toast.makeText(getApplicationContext(), "좌회전", Toast.LENGTH_SHORT).show();
                    break;
                case 13:
                    Toast.makeText(getApplicationContext(), "우회전", Toast.LENGTH_SHORT).show();
                    break;
                case 14:
                    Toast.makeText(getApplicationContext(), "U턴", Toast.LENGTH_SHORT).show();
                    break;
                case 15:
                    Toast.makeText(getApplicationContext(), "P턴", Toast.LENGTH_SHORT).show();
                    break;
                case 16:
                    Toast.makeText(getApplicationContext(), "8시방향 좌회", Toast.LENGTH_SHORT).show();
                    break;
                case 17:
                    Toast.makeText(getApplicationContext(), "10시방향 좌회", Toast.LENGTH_SHORT).show();
                    break;
                case 18:
                    Toast.makeText(getApplicationContext(), "2시방향 우회", Toast.LENGTH_SHORT).show();
                    break;
                case 19:
                    Toast.makeText(getApplicationContext(), "4시방향 우회", Toast.LENGTH_SHORT).show();
                    break;
                case 43:
                    Toast.makeText(getApplicationContext(), "오른쪽", Toast.LENGTH_SHORT).show();
                    break;
                case 44:
                    Toast.makeText(getApplicationContext(), "왼쪽", Toast.LENGTH_SHORT).show();
                    break;
                case 51:
                    Toast.makeText(getApplicationContext(), "직진 방향", Toast.LENGTH_SHORT).show();
                    break;
            }
            signalStopCheck = true;
        }
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
                            Log.e("totalDistance", String.valueOf(totalDistance));
                            totalTime += properties.getInt("totalTime");

                        }
                        JSONObject geometry = root.getJSONObject("geometry");
                        Log.e("geometry","파싱성공");
                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                        Log.e("coordinates","파싱성공");
                        String geoType = geometry.getString("type");
                        Log.e("type","파싱성공");
                        if (geoType.equals("Point")) {
                            Log.e("Point","파싱성공");
                            double lonJson = coordinates.getDouble(0);
                            double latJson = coordinates.getDouble(1);
                            int turnType = properties.getInt("turnType");
                            String description = properties.getString("description");
                            Log.e(TAG, "Point-");
                            Log.e(TAG, lonJson + "," + latJson + "\n");
                            if(i == 0){ startPlaceLat = latJson; startPlaceLon = lonJson;}
                            TMapPoint point = new TMapPoint(latJson, lonJson);

                            turnTypeList.add(turnType);

                            passList.add(point);
                        }
                        if(geoType.equals("LineString")){
                            int meter = properties.getInt("distance");
                            meterList.add(meter);
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
    public double currentDistance(double lat1, double lon1, double lat2, double lon2){

        double theta, dist;
        theta = lon1 - lon2;
        dist = Math.sin(degToRad(lat1)) * Math.sin(degToRad(lat2)) + Math.cos(degToRad(lat1))
                * Math.cos(degToRad(lat2)) * Math.cos(degToRad(theta));
        dist = Math.acos(dist);
        dist = radToDeg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

        return dist;
    }

    // 주어진 도(degree) 값을 라디언으로 변환
    private double degToRad(double deg){
        return (double)(deg * Math.PI / (double)180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double radToDeg(double rad){
        return (double)(rad * (double)180d / Math.PI);
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