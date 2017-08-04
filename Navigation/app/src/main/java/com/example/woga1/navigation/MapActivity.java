package com.example.woga1.navigation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.skp.Tmap.TMapView.TILETYPE_HDTILE;

/**
 * Created by woga1 on 2017-07-28.
 */
public class MapActivity extends Activity{
    RelativeLayout relativeLayout = null;
    TMapView tmapview;
    public TMapPolyLine tpolyline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        relativeLayout = new RelativeLayout(this);
        //sendBroadcast(new Intent("com.skt.intent.action.GPS_TURN_ON")); //GPS를 켜놓지 않아도 현재위치를 받아와서 출발지로 인식한다.
        //alertCheckGPS();
            execute();

    }

    public void btnNow(View v) {
        sendBroadcast(new Intent("com.skt.intent.action.GPS_TURN_ON")); //GPS를 켜놓지 않아도 현재위치를 받아와서 출발지로 인식한다.
    }


    public void execute() {
        //sendBroadcast(new Intent("com.skt.intent.action.GPS_TURN_ON"));

        tmapview = new TMapView(this);
        TMapGpsManager tmapgps = new TMapGpsManager(this);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);
        //tmapgps.OpenGps();
        TMapPoint point = tmapgps.getLocation();
        TMapPoint tpoint1 = new TMapPoint(37.540662, 127.069235);
        TMapPoint tpoint2 = new TMapPoint(37.550447, 127.073118);
        tpolyline = new TMapPolyLine();
        TMapMarkerItem tItem = new TMapMarkerItem();
        TMapData tmapdata = new TMapData();
        tmapview.setLocationPoint(point.getLongitude(),point.getLatitude());
        ArrayList<TMapPoint> passList = new ArrayList<TMapPoint>();

        tItem.setTMapPoint(tpoint1);
        tItem.setName("뚝섬유원지");
        tItem.setVisible(TMapMarkerItem.VISIBLE);
        TMapMarkerItem tItem2 = new TMapMarkerItem();
        tItem2.setTMapPoint(tpoint2);
        tItem2.setName("세종대학교");

        //tmapview.addMarkerItem("1", tItem);
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


        //tpolyline = tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH,tpoint1,tpoint2, passList, 0);
        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, tpoint1, tpoint2, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
//폴리 라인을 그리고 시작, 끝지점의 거리를 산출하여 로그에 표시한다
                tmapview.addTMapPath(polyLine);
                double wayDistance = polyLine.getDistance();
                Log.d(TAG, "Distance: " + wayDistance + "M");

            }
        });
            String LineID = tpolyline.getID();
            //tmapview.addTMapPolyLine(LineID, tpolyline);
            relativeLayout.addView(tmapview);
            setContentView(relativeLayout);
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

//        public void getJsonData(final TMapPoint startPoint, final TMapPoint endPoint)
//    {
//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//                HttpClient httpClient = new DefaultHttpClient();
//
//
//                //String urlString = "https://apis.skplanetx.com/tmap/routes/bicycle?callback=&bizAppId=&version=1";
//
//                //String urlString = "https://apis.skplanetx.com/tmap/routes/bicycle?callback=&bizAppId=&version=1&format=json&appKey=e2a7df79-5bc7-3f7f-8bca-2d335a0526e7";
//                //String urlString = "https://apis.skplanetx.com/tmap/routes/pedestrian?callback=&bizAppId=&version=1&format=json&appKey=e2a7df79-5bc7-3f7f-8bca-2d335a0526e7";
//
//                TMapPolyLine jsonPolyline = new TMapPolyLine();
//                jsonPolyline.setLineColor(Color.RED);
//                jsonPolyline.setLineWidth(2);
//
//                // &format={xml 또는 json}
//                try {
//                    URI url = new URI(urlString);
//
//                    HttpPost httpPost = new HttpPost();
//                    httpPost.setURI(url);
//                    HttpConnect h = new HttpConnect();
//
//                    List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
//                    //nameValuePairs.add(new BasicNameValuePair("startX", Double.toString(126.9786599)));
//                    //nameValuePairs.add(new BasicNameValuePair("startY", Double.toString(37.5657321)));
//                    nameValuePairs.add(new BasicNameValuePair("startX", Double.toString(startPoint.getLongitude())));
//                    nameValuePairs.add(new BasicNameValuePair("startY", Double.toString(startPoint.getLatitude())));
//
//
//                    //nameValuePairs.add(new BasicNameValuePair("endX", Double.toString(126.9516781)));
//                    //nameValuePairs.add(new BasicNameValuePair("endY", Double.toString(37.5426981)));
//                    nameValuePairs.add(new BasicNameValuePair("endX", Double.toString(endPoint.getLongitude())));
//                    nameValuePairs.add(new BasicNameValuePair("endY", Double.toString(endPoint.getLatitude())));
//
//
//
//                    nameValuePairs.add(new BasicNameValuePair("reqCoordType", "WGS84GEO"));
//                    nameValuePairs.add(new BasicNameValuePair("resCoordType", "WGS84GEO"));
//
//                    nameValuePairs.add(new BasicNameValuePair("startName", "서율역"));
//                    nameValuePairs.add(new BasicNameValuePair("endName", "공덕역"));
//
//                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//                    HttpResponse response = httpClient.execute(httpPost);
//                    String responseString = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
//
//                    //Log.d(TAG, responseString);
//
//                    String strData = "";
//                    saveRoutePoint.clear();
//                    saveRouteTurn.clear();
//
//                    Log.d(TAG, "0\n");
//                    JSONObject jAr = new JSONObject(responseString);
//
//                    Log.d(TAG, "1\n");
//                    // 개별 객체를 하나씩 추출
//                    JSONArray features = jAr.getJSONArray("features");
//
//                    Log.d(TAG, "2,"+ features.length() +"\n");
//                    // 객체에서 하위 객체를 추출
//
//
//                    for(int i=0; i<features.length(); i++)
//                    {
//                        JSONObject test2 = features.getJSONObject(i);
//                        JSONObject properties = test2.getJSONObject("properties");
//
//
//                        JSONObject geometry = test2.getJSONObject("geometry");
//                        JSONArray coordinates = geometry.getJSONArray("coordinates");
//
//                        String geoType = geometry.getString("type");
//                        if(geoType.equals("Point"))
//                        {
//                            double lonJson = coordinates.getDouble(0);
//                            double latJson = coordinates.getDouble(1);
//
//                            Log.d(TAG, "-");
//                            Log.d(TAG, lonJson+","+latJson+"\n");
//                            TMapPoint jsonPoint = new TMapPoint(latJson, lonJson);
//
//                            jsonPolyline.addLinePoint(jsonPoint);
//                            Log.d(TAG, jsonPoint.getLatitude()+"-"+jsonPoint.getLongitude());
//
//                            saveRoutePoint.add(jsonPoint);
//                        }
//                        if(geoType.equals("LineString"))
//                        {
//                            for(int j=0; j<coordinates.length(); j++)
//                            {
//                                JSONArray JLinePoint = coordinates.getJSONArray(j);
//                                double lonJson = JLinePoint.getDouble(0);
//                                double latJson = JLinePoint.getDouble(1);
//
//                                Log.d(TAG, "-");
//                                Log.d(TAG, lonJson+","+latJson+"\n");
//                                TMapPoint jsonPoint = new TMapPoint(latJson, lonJson);
//
//                                jsonPolyline.addLinePoint(jsonPoint);
//                                Log.d(TAG, jsonPoint.getLatitude()+"-"+jsonPoint.getLongitude());
//
//                                saveRoutePoint.add(jsonPoint);
//                                saveRouteTurn.add(11);
//                            }
//                        }
//
//                        String nodeType = properties.getString("nodeType");
//
//                        if(nodeType.equals("POINT")){
//                            int turnType = properties.getInt("turnType");
//
//                            saveRouteTurn.add(turnType);
//                            Log.d(TAG, "회전방향: "+turnType+"\n");
//                        }
//                        else if(nodeType.equals("LINE")){
//                            String description = properties.getString("description");
//                            Log.d(TAG, description+"\n");
//                        }
//
//
//
//                    }
//                    DashPathEffect dashPath = new DashPathEffect(new float[]{20,10}, 1); //점선
//                    //DashPathEffect dashPath2 = new DashPathEffect(new float[]{0,0}, 0); //실선
//
//                    jsonPolyline.setPathEffect(dashPath); //점선
//                    //jsonPolyline.setPathEffect(dashPath2); //실선
//                    jsonPolyline.setLineColor(Color.GREEN);
//                    jsonPolyline.setLineAlpha(0);
//                    mMapView.addTMapPolyLine("jsonPolyline",jsonPolyline);
//			        /*
//			        JSONObject test = features.getJSONObject(0);
//
//			        JSONObject properties = test.getJSONObject("properties");
//
//			        Log.d(TAG, "3\n");
//			        //JSONObject index = properties.getJSONObject("index");
//			        String nodeType = properties.getString("nodeType");
//
//
//			        Log.d(TAG, "4 " + nodeType+"\n");
//
//			        if(nodeType.equals("POINT")){
//			        	String turnType = properties.getString("turnType");
//
//			        	Log.d(TAG, "5 " + turnType+"\n");
//			        }
//			        */
//
//                    // 하위 객체에서 데이터를 추출
//                    //strData += features.getString("name") + "\n";
//
//
//                } catch (URISyntaxException e) {
//                    Log.e(TAG, e.getLocalizedMessage());
//                    e.printStackTrace();
//                } catch (ClientProtocolException e) {
//                    Log.e(TAG, e.getLocalizedMessage());
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    Log.e(TAG, e.getLocalizedMessage());
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//
//            }
//        };
//
//        thread.start();
//
//    }
}


