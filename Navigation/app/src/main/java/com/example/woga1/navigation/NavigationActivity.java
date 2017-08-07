package com.example.woga1.navigation;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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

public class NavigationActivity extends Activity {
    //Navigation화면으로 나오는 Activity
    ImageButton stopButton;
    TextView destinationText;
    RelativeLayout relativeLayout = null;
    TMapView tmapview;
    public TMapPolyLine tpolyline;
    ArrayList<TMapPoint> passList = new ArrayList<TMapPoint>();
    ArrayList<Integer> trunTypeList = new ArrayList<Integer>();
    ArrayList<String> des = new ArrayList<String>();
    int totalDistance;
    int totalTime;
    RelativeLayout mapview = null;
    String longtitude;
    String  latitude;
    //    String description;
    private static final String TAG = "RoadTracker";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Intent intent = getIntent();
        final String name = intent.getExtras().getString("destination");
        longtitude  = intent.getExtras().getString("longtitude");
        latitude = intent.getExtras().getString("latitude");

        Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();

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

        relativeLayout = new RelativeLayout(this);
        mapview = (RelativeLayout) findViewById(R.id.mapview);
        //sendBroadcast(new Intent("com.skt.intent.action.GPS_TURN_ON")); //GPS를 켜놓지 않아도 현재위치를 받아와서 출발지로 인식한다.
        //alertCheckGPS();
//        execute();
        execute(Double.parseDouble(longtitude), Double.parseDouble(latitude));
    }

    public void execute(double longtitude, double latitude) {
        //sendBroadcast(new Intent("com.skt.intent.action.GPS_TURN_ON"));
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        tmapview = new TMapView(this);
        TMapGpsManager tmapgps = new TMapGpsManager(this);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);
        tmapgps.OpenGps();
        TMapPoint point = tmapgps.getLocation();
        TMapPoint tpoint1 = new TMapPoint(37.550447, 127.073118);
        TMapPoint tpoint2 = new TMapPoint(latitude, longtitude);
        tpolyline = new TMapPolyLine();
        TMapMarkerItem tItem = new TMapMarkerItem();
        TMapData tmapdata = new TMapData();
        tmapview.setLocationPoint(tpoint1.getLongitude(),tpoint1.getLatitude());


        tItem.setTMapPoint(tpoint1);
        tItem.setName("뚝섬유원지");

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
        tmapview.setZoomLevel(15);
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
        Toast.makeText(this, String.valueOf(distance)+"km"+String.valueOf(totalTime)+"초", Toast.LENGTH_SHORT).show();

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
}
