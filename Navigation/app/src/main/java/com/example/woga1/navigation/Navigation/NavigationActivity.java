package com.example.woga1.navigation.Navigation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.woga1.navigation.DisplayException;
import com.example.woga1.navigation.MainActivity;
import com.example.woga1.navigation.R;
import com.example.woga1.navigation.Search.POIActivity;
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

import static android.R.attr.x;
import static com.example.woga1.navigation.R.id.min;
import static com.skp.Tmap.TMapView.TILETYPE_HDTILE;

public class NavigationActivity extends Activity implements TMapGpsManager.onLocationChangedCallback{
    //Navigation화면으로 나오는 Activity
    ImageButton stopButton;
    ImageButton volumeControl;
    ImageButton resetButton;
    ImageButton poiButton;
    ImageView entireView;
    TextView destinationText;
    //----데이터파싱해서 분류별 리스트----//
    ArrayList<TMapPoint> passList = new ArrayList<TMapPoint>();
    ArrayList<Integer> turnTypeList = new ArrayList<Integer>();
    ArrayList<Integer> meterList = new ArrayList<>();
    ArrayList<String> des = new ArrayList<String>();
    //---클래스들----///
    RelativeLayout mapView = null;
    TMapGpsManager tmapgps = null;
    TMapView tmapview;
    DisplayException displayException;
    //---변수---//
    String longitude;
    String  latitude;
    double distance;
    String startLat, startLon;
    //-------------onLocation에서 쓰이는 변수//
    double gpsLat, gpsLon;
    TextView totalDis;
    TextView time;
    int totalDistance = 0;
    int totalTime = 0;
    int type = -1;
    int index = 0;
    boolean signalStopCheck = false;
    boolean oneMoreAlarm = false;
    boolean speakerIsOn = true;

    public double startPlaceLat, startPlaceLon;
    public Location nowPlace = null;
    private String tmapAPI = "cad2cc9b-a3d5-3c32-8709-23279b7247f9";
    private static final String TAG = "NavigationActivity";


    @Override
    public void onLocationChange(Location location) {

        //nowPlace = location;
        gpsLat = location.getLatitude();
        gpsLon = location.getLongitude();
        int m = totalDistance - (int)currentDistance(startPlaceLat,startPlaceLon,nowPlace.getLatitude(),nowPlace.getLongitude());
        double speed = location.getSpeed();
        tmapview.setLocationPoint(gpsLon, gpsLat);
        tmapview.setTrackingMode(true); //이거 방향변환지점오면 true하기로 할까
        totalDis.setText(displayException.strDistance(m));
        time.setText(displayException.strTime(totalTime,speed,m));
        tmapview.setCompassMode(true); //이것도 방향변환지점?
        tmapview.setMarkerRotate(true); //이거랑
        //boolean isIn100M = check100M(nowPlace, index);
        distance = currentToPointDistance(nowPlace, index);
        type = checkArea(nowPlace, distance);
//        if(signalStopCheck == false){
//            signalTurnType(type);
//        }

        if(signalStopCheck == false )
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
            signalTurnType(type);
            oneMoreAlarm = false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        ((MainActivity)MainActivity.mContext).sendMessage("100 14");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        displayException = new DisplayException();
        Intent intent = getIntent();
        final String name = intent.getExtras().getString("destination");
        longitude  = intent.getExtras().getString("endLongitude");
        latitude = intent.getExtras().getString("endLatitude");
        startLat = intent.getExtras().getString("startLatitude");
        startLon = intent.getExtras().getString("startLongitude");
        Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
        time = (TextView)findViewById(min);
        mapView = (RelativeLayout) findViewById(R.id.mapview);
        entireView = (ImageView) findViewById(R.id.entireView);
        totalDis = (TextView)findViewById(R.id.km);
        destinationText = (TextView) findViewById(R.id.destination);
        destinationText.setText(name);
        stopButton = (ImageButton) findViewById(R.id.stopButton);

        resetButton = (ImageButton) findViewById(R.id.resetButton);
        volumeControl = (ImageButton) findViewById(R.id.volumeControl);
        poiButton = (ImageButton) findViewById(R.id.poiButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigationActivity.this, StopNavigation.class);
                startActivityForResult(intent, 1);

            }
        });

        //데시벨 기준 조정
        volumeControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "volumeControl", Toast.LENGTH_SHORT).show();
                showdDesibelStandard();


            }
        });

        //resetButton 클릭이벤트
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "resetButton", Toast.LENGTH_SHORT).show();
//                        entireView.setBackgroundColor(Color.parseColor("#80FF0000"));
                        entireView.setBackgroundColor(Color.TRANSPARENT);
                    }
                }, 1000);
//                entireView.setBackgroundColor(Color.TRANSPARENT);
                entireView.setBackgroundColor(Color.parseColor("#80FF0000"));
            }
        });
        //주변 편의시설로 넘어가는
        poiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigationActivity.this, POIActivity.class);
                startActivityForResult(intent, 1);

            }
        });
        nowPlace = nowLocation();
        TMapPoint startPoint = new TMapPoint(nowPlace.getLatitude(), nowPlace.getLongitude());
        TMapPoint endPoint = new TMapPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));
        TMapData tmapdata = new TMapData();

        tmapview = new TMapView(this);

        tmapview.setLocationPoint(startPoint.getLongitude(),startPoint.getLatitude());
        initTMap();

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

        execute(startPoint, endPoint);


//        ((MainActivity)MainActivity.mContext).sendMessage("100 14");
    }

    public void showdDesibelStandard()
    {
        final Dialog dialog = new Dialog(NavigationActivity.this);
        dialog.setTitle("마이크 민감도 설정");
        dialog.setContentView(R.layout.dialog);
        Button b1 = (Button) dialog.findViewById(R.id.okButton);
        Button b2 = (Button) dialog.findViewById(R.id.cancelButton);
        NumberPicker np = (NumberPicker) dialog.findViewById(R.id.numberPicker);
        np.setMaxValue(10);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);
//        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
            }
        });
        b2.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public  void initTMap(){
        tmapview.setTileType(TILETYPE_HDTILE);
        tmapview.setSKPMapApiKey(tmapAPI);
        tmapview.setCompassMode(true);
        tmapview.setIconVisibility(true); //현재위치 파랑마커표시
        tmapview.setZoomLevel(19);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        tmapview.setTrackingMode(true); //화면중심을 단말의 현재위치로 이동시켜주는 트래킹 모드
        tmapview.setPathRotate(true); //나침반 회전 시 출,도착 아이콘을 같이 회전시킬지 여부를 결정합니다.
    }
    public void initGPSSetting(){
        tmapgps = new TMapGpsManager(this);
        tmapgps.setMinDistance(0);
        tmapgps.setMinTime(100);
        tmapgps.OpenGps();
        tmapgps.setProvider(tmapgps.GPS_PROVIDER );
    }




    public Location nowLocation() {
        Location myLocation = null;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            initGPSSetting();
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            myLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

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

    public int checkArea(Location location, double distanceInMeters) {
        int turnType = 0;
        int r;
        //double distanceInMeters = currentToPointDistance(location, cnt);
        for(int i= 0; i<passList.size(); i++) {
            if (index == passList.size() - 1) {
                //도착지 범위는 좀 더 작게

                //r = Radius(passList.get(i).getLatitude(), passList.get(i).getLongitude(), nowPlace.getLatitude(), nowPlace.getLongitude());
                if (distanceInMeters <= 10) {
                    turnType = turnTypeList.get(i);
                    signalStopCheck = false;
                }
            }
            else if(i == index && index == 0)
            {
                if (distanceInMeters <= 20) {
                    turnType = turnTypeList.get(0);
                    signalTurnType(200);
                    signalStopCheck = false;
                    index++;
                }
            }
            else
            {
                if (distanceInMeters <= 100 && distanceInMeters > 10) {
                    turnType = turnTypeList.get(index);
                }
                if (distanceInMeters > 100) {
                    turnType = turnTypeList.get(index);
                    oneMoreAlarm = true;
                }
                else if (distanceInMeters <= 5) {
                    signalStopCheck = false;
                    oneMoreAlarm = false;
                    index++;
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
                    //sendMessage(String.valueOf(distance)+" 200.");
                    break;
                case 201:
                    Toast.makeText(getApplicationContext(), "도착", Toast.LENGTH_SHORT).show();
                    //sendMessage(String.valueOf(distance)+" 201.");
                    break;
                case 11:
                    Toast.makeText(getApplicationContext(), "직진", Toast.LENGTH_SHORT).show();
                    //sendMessage(String.valueOf(distance)+" 11.");
                    break;
                case 12:
                    Toast.makeText(getApplicationContext(), "좌회전", Toast.LENGTH_SHORT).show();
                    //sendMessage(String.valueOf(distance)+" 12.");
                    break;
                case 13:
                    Toast.makeText(getApplicationContext(), "우회전", Toast.LENGTH_SHORT).show();
                    //sendMessage(String.valueOf(distance)+" 13.");
                    break;
                case 14:
                    Toast.makeText(getApplicationContext(), "U턴", Toast.LENGTH_SHORT).show();
                    //sendMessage(String.valueOf(distance)+" 14.");
                    break;
                case 15:
                    Toast.makeText(getApplicationContext(), "P턴", Toast.LENGTH_SHORT).show();
                    //sendMessage(String.valueOf(distance)+" 15.");
                    break;
                case 16:
                    Toast.makeText(getApplicationContext(), "8시방향 좌회", Toast.LENGTH_SHORT).show();
                    //sendMessage(String.valueOf(distance)+" 16.");
                    break;
                case 17:
                    Toast.makeText(getApplicationContext(), "10시방향 좌회", Toast.LENGTH_SHORT).show();
                    //sendMessage(String.valueOf(distance)+" 17.");
                    break;
                case 18:
                    Toast.makeText(getApplicationContext(), "2시방향 우회", Toast.LENGTH_SHORT).show();
                    //sendMessage(String.valueOf(distance)+" 18.");
                    break;
                case 19:
                    Toast.makeText(getApplicationContext(), "4시방향 우회", Toast.LENGTH_SHORT).show();
                    //sendMessage(String.valueOf(distance)+" 19.");
                    break;
                case 43:
                    Toast.makeText(getApplicationContext(), "오른쪽", Toast.LENGTH_SHORT).show();
                    //sendMessage(String.valueOf(distance)+" 43.");
                    break;
                case 44:
                    Toast.makeText(getApplicationContext(), "왼쪽", Toast.LENGTH_SHORT).show();
                    //sendMessage(String.valueOf(distance)+" 44.");
                    break;
                case 51:
                    Toast.makeText(getApplicationContext(), "직진 방향", Toast.LENGTH_SHORT).show();
                    //sendMessage(String.valueOf(distance)+" 51.");
                    break;
            }
            signalStopCheck = true;
        }
    }




    public ArrayList<TMapPoint> getJsonData(final TMapPoint startPoint, final TMapPoint endPoint)
    {
        Thread thread = new Thread() {
            //내가 본 예시에서는 Thread를 따로 파서 넣었는데 json파싱할때 어싱크태스크를 이용한 코드들도 봤음!
            //Thread가 돌아가면 바로 이 안으로 들어오지않고 밑에 보면 Thread.start 있는데 그거 실행해야 안으로 들어옴!
            @Override
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();

                String urlString = "https://apis.skplanetx.com/tmap/routes?version=1&format=json&appKey=cad2cc9b-a3d5-3c32-8709-23279b7247f9";
                //String urlString = "https://apis.skplanetx.com/tmap/routes/pedestrian?callback=&bizAppId=&version=1&format=json&appKey=e2a7df79-5bc7-3f7f-8bca-2d335a0526e7";


                // &format={xml 또는 json}
                try{
                    URI uri = new URI(urlString);

                    HttpPost httpPost = new HttpPost();
                    httpPost.setURI(uri);
                    //만약 Http 관련해서 빨간 줄 뜨면서 안되면 gradle에  provided 'org.jbundle.util.osgi.wrapped:org.jbundle.util.osgi.wrapped.org.apache.http.client:4.1.2' 를 추가해봐
                    List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("startX", Double.toString(startPoint.getLongitude())));
                    nameValuePairs.add(new BasicNameValuePair("startY", Double.toString(startPoint.getLatitude())));

                    nameValuePairs.add(new BasicNameValuePair("endX", Double.toString(endPoint.getLongitude())));
                    nameValuePairs.add(new BasicNameValuePair("endY", Double.toString(endPoint.getLatitude())));

                    nameValuePairs.add(new BasicNameValuePair("startName", "출발지"));
                    nameValuePairs.add(new BasicNameValuePair("endName", "도착지"));

                    nameValuePairs.add(new BasicNameValuePair("reqCoordType", "WGS84GEO"));
                    nameValuePairs.add(new BasicNameValuePair("resCoordType", "WGS84GEO"));
//출발지 위도 경도, 목적지에 위도 경도나 위에 있는 것들은 티맵 포럼 보면 알겠지만 기본적으로 넣어야하는 정보야! 그냥 출발지 위도경도와 목적지 위도경도만 맞춰주면 됨
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);//여기서 인터넷이랑 연결해서 불러오는데 매니페스트에  퍼미션 ㄱ인터넷은 추가했겠지,,

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
                    JSONArray features = jAr.getJSONArray("features"); //그리고 이것도 티맵 포럼 보면 알 수 있는데 feature라는 태그에 담긴 정보를 가지고온다고 생각하면 이해하기 쉬움
                    passList = new ArrayList<>();

                    for(int i=0; i<features.length(); i++) {
                        JSONObject root = features.getJSONObject(i);
                        JSONObject properties = root.getJSONObject("properties");
                        if (i == 0) { //총거리 총시간이 for문 돌리기 맨 처음에 있으니 여기서 뽑아주고

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
                        if (geoType.equals("Point")) { //여기서 경로에서 방향변환지점들이 있을거아냐 그 지점들 위도경도를 받아옴
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
                        if(geoType.equals("LineString")){ //이 태그도 위랑 비슷한데 경로 중간중간 위도경도 받을 수 있어!
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

//        }
    }


}