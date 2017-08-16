package com.example.woga1.navigation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.RelativeLayout;
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

/**
 * Created by woga1 on 2017-07-28.
 */
public class MapActivity extends Activity implements TMapGpsManager.onLocationChangedCallback {

    RelativeLayout relativeLayout = null;
    TMapView tmapview;
    int totalDistance;
    int totalTime;
    TMapGpsManager tmapgps = null;
    ArrayList<TMapPoint> passList = new ArrayList<TMapPoint>();
    private String tmapAPI = "cad2cc9b-a3d5-3c32-8709-23279b7247f9";
    private static final String TAG = "MapActivity";
    @Override
    public void onLocationChange(Location location){
        if(true){
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }
//    @Override
//    public void onCalloutRightButton(TMapMarkerItem markerItem) {
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        relativeLayout = (RelativeLayout) findViewById(R.id.map_view);
       // ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        //sendBroadcast(new Intent("com.skt.intent.action.GPS_TURN_ON")); //GPS를 켜놓지 않아도 현재위치를 받아와서 출발지로 인식한다.
        //alertCheckGPS();
            execute();
    }

    public void execute() {
        //sendBroadcast(new Intent("com.skt.intent.action.GPS_TURN_ON"));

        tmapview = new TMapView(this);
        Location start= nowLocation();
        TMapPoint startPoint = new TMapPoint(start.getLatitude(), start.getLongitude());
        TMapPoint endPoint = new TMapPoint(37.560447, 127.074118);
        TMapMarkerItem tItem = new TMapMarkerItem();
        TMapData tmapdata = new TMapData();
        TMapData tmapdata2 = new TMapData();
        tmapview.setLocationPoint(startPoint.getLongitude(),startPoint.getLatitude());
        tmapview.setTileType(TILETYPE_HDTILE);
        tmapview.setSKPMapApiKey(tmapAPI);
        tmapview.setCompassMode(true);
        float[] results = new float[1];
        Location.distanceBetween(startPoint.getLatitude(), startPoint.getLongitude(), endPoint.getLatitude(), endPoint.getLongitude(), results);
        float distanceInMeters = results[0];
        totalDistance = (int)distanceInMeters;
        setMapView(totalDistance);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        tmapview.setTrackingMode(true); //화면중심을 단말의 현재위치로 이동시켜주는 트래킹 모드
        tmapview.setPathRotate(true); //나침반 회전 시 출,도착 아이콘을 같이 회전시킬지 여부를 결정합니다.

        //tpolyline = tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, tpoint1, tpoint2, passList, 0);

        relativeLayout.addView(tmapview);
        double distance = totalDistance/1000;
        Toast.makeText(this, String.valueOf(distance)+"km"+String.valueOf(totalTime)+"초", Toast.LENGTH_SHORT).show();
//        passList = getJsonData(startPoint, endPoint);
//        for(int i=0; i<passList.size()-1; i++)
//        {
//            TMapMarkerItem item = new TMapMarkerItem();
//            TMapPoint passpoint = new TMapPoint(passList.get(i).getLatitude(), passList.get(i).getLongitude());
//            Bitmap mark = null;
////            item.setCanShowCallout(true);
////            //풍선뷰 사용여부
////            if(des.get(i) != null) {
////                item.setCalloutTitle(String.valueOf(turnTypeList.get(i))+" "+des.get(i));
////            }
//
//            item.setTMapPoint(passpoint);
//            tmapview.addMarkerItem(String.valueOf(i), item);
//        }
        ArrayList<TMapPoint> r = new ArrayList<>();
        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, startPoint, endPoint, r, 12
        ,new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                polyLine.setLineColor(Color.GREEN);
                tmapview.addTMapPath(polyLine);
            }
        });
//        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, startPoint, endPoint, r, 2
//                ,new TMapData.FindPathDataListenerCallback() {
//                    @Override
//                    public void onFindPathData(TMapPolyLine polyLine) {
//                        polyLine.setLineColor(Color.BLUE);
//                        tmapview.addTMapPath(polyLine);
//                    }
//                });
//        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, startPoint, endPoint,  new TMapData.FindPathDataListenerCallback() {
//            @Override
//            public void onFindPathData(TMapPolyLine polyLine) {
////폴리 라인을 그리고 시작, 끝지점의 거리를 산출하여 로그에 표시한다
//                tmapview.addTMapPath(polyLine);
//                double wayDistance = polyLine.getDistance();
//                Log.d(TAG, "Distance: " + wayDistance + "M");
//            }
//        });
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

    private void alertCheckGPS() { //gps 꺼져있으면 켤 껀지 체크
        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("현재 GPS가 꺼져있습니다.켜시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    moveConfigGPS();
                                }
                            })
                    .setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    // GPS 설정화면으로 이동
    private void moveConfigGPS() {
        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionsIntent);
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
                            String description = properties.getString("description");
                            Log.e(TAG, "Point-");
                            Log.e(TAG, lonJson+","+latJson+"\n");
                            TMapPoint point = new TMapPoint(latJson, lonJson);

//                            turnTypeList.add(turnType);
//
//                            des.add(description);
                            passList.add(point);

                        }
                        if(geoType.equals("LineString"))
                        {
//                                for (int j = 0; j < coordinates.length(); j++) {
//                                    if(j >=1 && i < coordinates.length()-1) {
//                                        JSONArray JLinePoint = coordinates.getJSONArray(j);
//                                        double lonJson = JLinePoint.getDouble(0);
//                                        double latJson = JLinePoint.getDouble(1);
//
//                                        Log.e(TAG, "LineString-");
//                                        Log.e(TAG, lonJson + "," + latJson + "\n");
//                                        TMapPoint point = new TMapPoint(latJson, lonJson);
//                                        LineList.add(point);
//                                    }
//                                }

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
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
}


