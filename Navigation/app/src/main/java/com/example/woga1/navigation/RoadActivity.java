package com.example.woga1.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

import static android.content.ContentValues.TAG;
import static com.skp.Tmap.TMapView.TILETYPE_HDTILE;

/**
 * Created by woga1 on 2017-07-30.
 */

public class RoadActivity extends Activity implements TMapGpsManager.onLocationChangedCallback {
    TextView textview = null;
    TMapGpsManager tmapgps = null;
    RelativeLayout relativeLayout = null;
    ArrayList<TMapPoint> passList = new ArrayList<TMapPoint>();
    ArrayList<Integer> meterList = new ArrayList<>();
    ArrayList<Integer> turnTypeList = new ArrayList<Integer>();
    ArrayList<String> des = new ArrayList<String>();
    int totalDistance;
    int totalTime;
    int type = -1;
    int count = 0;
//    PathData pathData = new PathData(this);
    public double wayDistance = 0;
    public Location nowPlace = null;
    private TMapView mMapView = null;
    boolean stopCheck=false;
    private String tmapAPI = "cad2cc9b-a3d5-3c32-8709-23279b7247f9";
    private static final double EARTH_RADIOUS = 3958.75; //지구 반경;
    private static final int METER_CONVERSION = 1609;
    @Override
    public void onLocationChange(Location location) {

        nowPlace = location;
        mMapView.setLocationPoint(nowPlace.getLongitude(), nowPlace.getLatitude());
        mMapView.setTrackingMode(true);
        double speed = location.getSpeed();
        mMapView.setCompassMode(true);
        mMapView.setMarkerRotate(true);
        textview.setText(String.valueOf(speed));
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
            else if(stopCheck == true){count++; mMapView.setCompassMode(true); mMapView.setMarkerRotate(true); stopCheck = false;}
        }
        else if(type == 11 ){
            Toast.makeText(getApplicationContext(), "직진", Toast.LENGTH_SHORT).show();
            if(stopCheck == true){count++; mMapView.setCompassMode(true); mMapView.setMarkerRotate(true); stopCheck = false;}
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
        setContentView(R.layout.activity_map);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        relativeLayout = (RelativeLayout) findViewById(R.id.map_view);
        textview = (TextView) findViewById(R.id.textView3);
        mMapView = new TMapView(this);

        TMapData tmapdata = new TMapData();
        TMapPolyLine polyLine = new TMapPolyLine();

        nowPlace = nowLocation();
        TMapPoint startpoint = new TMapPoint(nowPlace.getLatitude(), nowPlace.getLongitude());
        TMapPoint endpoint = new TMapPoint(37.540662, 127.069235);
        mMapView.setLocationPoint(nowPlace.getLongitude(), nowPlace.getLatitude());
        mMapView.setTileType(TILETYPE_HDTILE);
        mMapView.setSKPMapApiKey(tmapAPI);
        mMapView.setCompassMode(true);
        mMapView.setIconVisibility(true); //현재위치 파랑마커표시
        mMapView.setZoomLevel(19);
        mMapView.setMapType(mMapView.MAPTYPE_TRAFFIC);
        mMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        mMapView.setTrackingMode(true);
        mMapView.setMarkerRotate(true);

        double dis = calDistance(nowPlace.getLatitude(), nowPlace.getLongitude(),37.540662, 127.069235);
        passList = getJsonData(startpoint,endpoint);
        dis = totalDistance;

        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, startpoint, endpoint,  new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
//폴리 라인을 그리고 시작, 끝지점의 거리를 산출하여 로그에 표시한다
                Bitmap start = BitmapFactory.decodeResource(getResources(),R.drawable.startpurple_resized);
                Bitmap end = BitmapFactory.decodeResource(getResources(),R.drawable.endpurple_resized);
                mMapView.setTMapPathIcon(start, end);
                polyLine.setLineColor(Color.GRAY);
                polyLine.setLineWidth(25);
                mMapView.addTMapPath(polyLine);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.guide_arrow_blue);
                mMapView.setIcon(bitmap);
                Log.i(TAG, "Distance: " + wayDistance + "M");
            }
        });
        for(int i=0; i<passList.size()-1; i++)
        {
            TMapMarkerItem item = new TMapMarkerItem();
            TMapPoint passpoint = new TMapPoint(passList.get(i).getLatitude(), passList.get(i).getLongitude());
            Bitmap mark = null;
//            item.setCanShowCallout(true);
//            //풍선뷰 사용여부
//            if(des.get(i) != null) {
//                item.setCalloutTitle(String.valueOf(turnTypeList.get(i))+" "+des.get(i));
//            }
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
            mMapView.bringMarkerToFront(item);
            mMapView.addMarkerItem(String.valueOf(i), item);
        }
        relativeLayout.addView(mMapView);

    }
    public double calDistance(double lat1, double lon1, double lat2, double lon2){

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

    public Location nowLocation() {
        Location myLocation = null;
        //현재위치 받아오기
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            tmapgps = new TMapGpsManager(this);
            tmapgps.setMinDistance(0);
            tmapgps.setMinTime(100);
            tmapgps.OpenGps();
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (myLocation == null) {
                myLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(myLocation != null)
                {
                    Log.d("myLocation: ", String.valueOf(myLocation.getLatitude()));
                }
            }
            else if(myLocation != null){
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                String provider = lm.getBestProvider(criteria, true);
                myLocation = lm.getLastKnownLocation(provider);
                Log.d("myLocation: ", String.valueOf(myLocation.getLatitude()));
            }
        }
        return myLocation;
    }

    public void btnNow(View v)
    {
        mMapView.setLocationPoint(nowPlace.getLongitude(), nowPlace.getLatitude());
        mMapView.setTrackingMode(true);
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
//                        else
//                        {
//                            String description = properties.getString("description");
//                            des.add(description);
//                        }
                        JSONObject geometry = root.getJSONObject("geometry");
                        JSONArray coordinates = geometry.getJSONArray("coordinates");

                        String geoType = geometry.getString("type");
                        if(geoType.equals("Point"))
                        {
                            double lonJson = coordinates.getDouble(0);
                            double latJson = coordinates.getDouble(1);
                            int turnType = properties.getInt("turnType");
                            int meter = properties.getInt("distance");
                            String description = properties.getString("description");
                            Log.e(TAG, "Point-");
                            Log.e(TAG, lonJson+","+latJson+"\n");
                            TMapPoint point = new TMapPoint(latJson, lonJson);

                            turnTypeList.add(turnType);
                            meterList.add(meter);
                            des.add(description);
                            passList.add(point);

                        }
//                        if(geoType.equals("LineString"))
//                        {
//                            for (int j = 0; j < coordinates.length(); j++) {
//                                if(j >=1 && i < coordinates.length()-1) {
//                                    JSONArray JLinePoint = coordinates.getJSONArray(j);
//                                    double lonJson = JLinePoint.getDouble(0);
//                                    double latJson = JLinePoint.getDouble(1);
//
//                                    Log.e(TAG, "LineString-");
//                                    Log.e(TAG, lonJson + "," + latJson + "\n");
//                                    TMapPoint point = new TMapPoint(latJson, lonJson);
//                                    LineList.add(point);
//                                }
//                            }
//
//                        }
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

    public int checkArea(Location location, int cnt){ //40m 반경 체크
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


    public int Radius(double pointLat, double pointLon, double myLat, double myLon)
    {
        double lat, lon;
        int lon_dist, lat_dist;
        int DIV_VALUE = 10000000;
        lat = pointLat > myLat  ? (pointLat - myLat)/(double)DIV_VALUE   : (myLat-pointLat)/(double)DIV_VALUE;
        lon =  pointLon > myLon  ? (pointLon - myLon)/(double)DIV_VALUE   : (myLon-pointLon)/(double)DIV_VALUE;
        int rad = (int)lon;
        double min = (lon-rad)*60;
        double sec = ((lon-rad)*60 - min)*60;
        lon_dist = (int)((rad * 88.8) + (min*1.48) + (sec*0.025)) * 1000; // m단위

/*
위도에 대한 도분초및 거리 계산
*/
        rad = (int)lat;
        min = (lat-rad)*60;
        sec = ((lat-rad)*60 - min)*60;
        lat_dist = (int)((rad * 111) + (min*1.85) + (sec*0.031)) * 1000; // m단위

//        if( nCmpLat == 0 ){ // 원 형태의 구역반경
//            // 직선거리만을 조건으로 한다.
            int realDist = (int)Math.sqrt((lon_dist*lon_dist)+(lat_dist*lat_dist));

            return realDist;
    }

    public double distanceFrom(double lat1, double lng1, double lat2, double lng2)
    {
        // Return distance between 2 points, stored as 2 pair location;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = EARTH_RADIOUS * c;
        return new Double(dist * METER_CONVERSION).floatValue();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        tmapgps.OpenGps();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // GPS중단
        tmapgps.CloseGps();
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
    //거리 구하는 부분
    public double distance(double P1_latitude, double P1_longitude,
                           double P2_latitude, double P2_longitude) {
        if ((P1_latitude == P2_latitude) && (P1_longitude == P2_longitude)) {
            return 0;
        }
        double e10 = P1_latitude * Math.PI / 180;
        double e11 = P1_longitude * Math.PI / 180;
        double e12 = P2_latitude * Math.PI / 180;
        double e13 = P2_longitude * Math.PI / 180;
    /* 타원체 GRS80 */
        double c16 = 6356752.314140910;
        double c15 = 6378137.000000000;
        double c17 = 0.0033528107;
        double f15 = c17 + c17 * c17;
        double f16 = f15 / 2;
        double f17 = c17 * c17 / 2;
        double f18 = c17 * c17 / 8;
        double f19 = c17 * c17 / 16;
        double c18 = e13 - e11;
        double c20 = (1 - c17) * Math.tan(e10);
        double c21 = Math.atan(c20);
        double c22 = Math.sin(c21);
        double c23 = Math.cos(c21);
        double c24 = (1 - c17) * Math.tan(e12);
        double c25 = Math.atan(c24);
        double c26 = Math.sin(c25);
        double c27 = Math.cos(c25);
        double c29 = c18;
        double c31 = (c27 * Math.sin(c29) * c27 * Math.sin(c29))
                + (c23 * c26 - c22 * c27 * Math.cos(c29))
                * (c23 * c26 - c22 * c27 * Math.cos(c29));
        double c33 = (c22 * c26) + (c23 * c27 * Math.cos(c29));
        double c35 = Math.sqrt(c31) / c33;
        double c36 = Math.atan(c35);
        double c38 = 0;
        if (c31 == 0) {
            c38 = 0;
        } else {
            c38 = c23 * c27 * Math.sin(c29) / Math.sqrt(c31);
        }
        double c40 = 0;
        if ((Math.cos(Math.asin(c38)) * Math.cos(Math.asin(c38))) == 0) {
            c40 = 0;
        } else {
            c40 = c33 - 2 * c22 * c26
                    / (Math.cos(Math.asin(c38)) * Math.cos(Math.asin(c38)));
        }
        double c41 = Math.cos(Math.asin(c38)) * Math.cos(Math.asin(c38))
                * (c15 * c15 - c16 * c16) / (c16 * c16);
        double c43 = 1 + c41 / 16384
                * (4096 + c41 * (-768 + c41 * (320 - 175 * c41)));
        double c45 = c41 / 1024 * (256 + c41 * (-128 + c41 * (74 - 47 * c41)));
        double c47 = c45
                * Math.sqrt(c31)
                * (c40 + c45
                / 4
                * (c33 * (-1 + 2 * c40 * c40) - c45 / 6 * c40
                * (-3 + 4 * c31) * (-3 + 4 * c40 * c40)));
        double c50 = c17
                / 16
                * Math.cos(Math.asin(c38))
                * Math.cos(Math.asin(c38))
                * (4 + c17
                * (4 - 3 * Math.cos(Math.asin(c38))
                * Math.cos(Math.asin(c38))));
        double c52 = c18
                + (1 - c50)
                * c17
                * c38
                * (Math.acos(c33) + c50 * Math.sin(Math.acos(c33))
                * (c40 + c50 * c33 * (-1 + 2 * c40 * c40)));
        double c54 = c16 * c43 * (Math.atan(c35) - c47);
        // return distance in meter
        return c54;
    }
    //방위각 구하는 부분
    public short bearingP1toP2(double P1_latitude, double P1_longitude,
                               double P2_latitude, double P2_longitude) {
        // 현재 위치 : 위도나 경도는 지구 중심을 기반으로 하는 각도이기 때문에
        //라디안 각도로 변환한다.
        double Cur_Lat_radian = P1_latitude * (3.141592 / 180);
        double Cur_Lon_radian = P1_longitude * (3.141592 / 180);
        // 목표 위치 : 위도나 경도는 지구 중심을 기반으로 하는 각도이기 때문에
        // 라디안 각도로 변환한다.
        double Dest_Lat_radian = P2_latitude * (3.141592 / 180);
        double Dest_Lon_radian = P2_longitude * (3.141592 / 180);
        // radian distance
        double radian_distance = 0;
        radian_distance = Math.acos(Math.sin(Cur_Lat_radian)
                * Math.sin(Dest_Lat_radian) + Math.cos(Cur_Lat_radian)
                * Math.cos(Dest_Lat_radian)
                * Math.cos(Cur_Lon_radian - Dest_Lon_radian));
        // 목적지 이동 방향을 구한다.(현재 좌표에서 다음 좌표로 이동하기 위해서는
        //방향을 설정해야 한다. 라디안값이다.
        double radian_bearing = Math.acos((Math.sin(Dest_Lat_radian) - Math
                .sin(Cur_Lat_radian)
                * Math.cos(radian_distance))
                / (Math.cos(Cur_Lat_radian) * Math.sin(radian_distance)));
        // acos의 인수로 주어지는 x는 360분법의 각도가 아닌 radian(호도)값이다.
        double true_bearing = 0;
        if (Math.sin(Dest_Lon_radian - Cur_Lon_radian) < 0) {
            true_bearing = radian_bearing * (180 / 3.141592);
            true_bearing = 360 - true_bearing;
        } else {
            true_bearing = radian_bearing * (180 / 3.141592);
        }
        return (short) true_bearing;
    }
}

