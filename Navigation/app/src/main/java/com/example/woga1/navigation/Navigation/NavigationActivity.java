package com.example.woga1.navigation.Navigation;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.example.woga1.navigation.MainActivity;
import com.example.woga1.navigation.R;
import com.example.woga1.navigation.Search.POIActivity;
import com.example.woga1.navigation.SoundAnalyze.AnalyzerGraphic;
import com.example.woga1.navigation.SoundAnalyze.AnalyzerParameters;
import com.example.woga1.navigation.SoundAnalyze.AnalyzerViews;
import com.example.woga1.navigation.SoundAnalyze.SamplingLoop;
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

import static com.example.woga1.navigation.R.id.min;
import static com.skp.Tmap.TMapView.TILETYPE_HDTILE;

public class NavigationActivity extends Activity implements TMapGpsManager.onLocationChangedCallback, AnalyzerGraphic.Ready {
    //Navigation화면으로 나오는 Activity
    ImageButton stopButton;
    ImageButton volumeControl;
    ImageButton resetButton;
    ImageButton poiButton;
    static ImageView entireView;
    TextView destinationText;
    TextView speedView;
    ImageView directionImg;
    ImageView nextDirectionImg;
    TextView directionDistance;
    TextView nextdirectionDistance;
    TextView displayRoad;
    //----데이터파싱해서 분류별 리스트----//
    ArrayList<TMapPoint> passList = new ArrayList<TMapPoint>();
    ArrayList<Integer> turnTypeList = new ArrayList<Integer>();
    ArrayList<Integer> pointDistanceList = new ArrayList<>();
    ArrayList<String> roadName = new ArrayList<String>();
    //---클래스들----///
    RelativeLayout mapView = null;
    TMapGpsManager tmapgps = null;
    TMapView tmapview;
    DisplayException displayException;
    //---변수---//
    int distance;
    String startLat, startLon;
    String longitude, latitude;
    //-------------onLocation에서 쓰이는 변수//
    TextView totalDis;
    TextView time;
    int totalDistance = 0;
    int totalTime = 0;
    int type = -1;
    int index = 0;
    int meter = 0;
    int oneMoreAlarmCount = 0;
    boolean signalStopCheck = false;
    boolean oneMoreAlarm = false;
    boolean oneMoreAlarmSignalStopCheck = false;
    boolean speakerIsOn = true;
    double speed = 0;
    public double startPlaceLat, startPlaceLon;
    public Location currentLocation = null;

    private String tmapAPI = "cad2cc9b-a3d5-3c32-8709-23279b7247f9";
    private static final String TAG = "NavigationActivity";

    //SoundAnalyze
    public AnalyzerViews analyzerViews;
    SamplingLoop samplingThread = null;
    private AnalyzerParameters analyzerParam = null;
    public double dtRMS = 0;
    public double dtRMSFromFT = 0;
    public double maxAmpDB;
    public double maxAmpFreq;
    double[] viewRangeArray = null;
    private boolean isLockViewRange = false;
    public volatile boolean bSaveWav = false;
    private final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;  // just a number
    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    public Thread graphInit;
    private boolean bSamplingPreparation = false;
    // For call requestPermissions() after each showPermissionExplanation()
    private int count_permission_explanation = 0;
    // For preventing infinity loop: onResume() -> requestPermissions() -> onRequestPermissionsResult() -> onResume()
    private int count_permission_request = 0;


    @Override
    public void onLocationChange(Location location) {

        currentLocation = location;
        meter = totalDistance - (int) currentDistance(startPlaceLat, startPlaceLon, currentLocation.getLatitude(), currentLocation.getLongitude());
        speed = location.getSpeed();
        distance = (int)currentToPointDistance(currentLocation, index);
        type = checkArea(currentLocation, distance);
        changeMarker();
        
        if (type == 201) {
            totalDis.setText(displayException.strDistance(distance));
            ((MainActivity) MainActivity.mContext).sendMessage(String.valueOf(distance) + " 100.");
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(NavigationActivity.this, StopNavigation.class);
                    startActivityForResult(intent, 1);
                    finish();
                }
            }, 1500); // 1000ms
        }

        if (speed > 0) {
            speedView.setText(String.valueOf((int)(location.getSpeed() * 3600 / 1000)));
        } else {
            speedView.setText("0");
        }

        if (!signalStopCheck) {
            if (oneMoreAlarm) {
                oneMoreAlarmCount++;
                if(oneMoreAlarmCount == 1) {
                    oneMoreSignalTurnType(type);
                }
                oneMoreAlarm = false;
            } else if (oneMoreAlarmSignalStopCheck) {
                signalTurnType(type);
                signalStopCheck = true;
            }
        }
    }

    public void initValue(){
        index = 0;
        meter = 0;
        oneMoreAlarmCount = 0;
    }
    public void initProcess(){
        signalStopCheck = false;
        oneMoreAlarm = false;
        oneMoreAlarmSignalStopCheck = false;
        oneMoreAlarmCount = 0;
    }

    private void changeMarker() {
        tmapview.setTrackingMode(true);
        tmapview.setLocationPoint(currentLocation.getLongitude(), currentLocation.getLatitude());
        //남은거리 변경
        totalDis.setText(displayException.strDistance(meter));
        //strCurrentDistance = exception.strDistance((int) driveInfoDistance);
        int textTime = displayException.remainTime(totalTime, speed, meter);
        if (textTime > 0) {
            time.setText(String.valueOf(textTime) + "분");
        }
        //tv_distance.setText(strCurrentDistance);
        //각 구간 m 줄어들기
        directionDistance.setText(displayException.strDistance(distance));
        Log.i(TAG, "[[[[[ 남은 거리와 시간 ]]]]] : " + displayException.strDistance(meter) + displayException.remainTime(totalTime, speed, meter));
    }

//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            Log.i(TAG, "[ Handler ] : " + startLon + " & " + startLat);
//
//        }
//    };   // [ End Handler ]


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        //SoundAnalyze
        Resources res = getResources();
        analyzerParam = new AnalyzerParameters(res);
        analyzerViews = new AnalyzerViews(this);
        analyzerViews.graphView.switch2Spectrogram();


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        displayException = new DisplayException(getApplicationContext());
        Intent intent = getIntent();
        //final String name = "스타벅스";
        ((MainActivity) MainActivity.mContext).sendMessage("100 200.");
        final String name = intent.getExtras().getString("destination");
        longitude = intent.getExtras().getString("endLongitude");
        latitude = intent.getExtras().getString("endLatitude");
//        startLat = intent.getExtras().getString("startLatitude");
//        startLon = intent.getExtras().getString("startLongitude");
        initValue();

        time = (TextView) findViewById(min);
        mapView = (RelativeLayout) findViewById(R.id.mapview);
        entireView = (ImageView) findViewById(R.id.entireView);
        totalDis = (TextView) findViewById(R.id.km);
        destinationText = (TextView) findViewById(R.id.destination);
        speedView = (TextView) findViewById(R.id.speedView);
        destinationText.setText(name);
        stopButton = (ImageButton) findViewById(R.id.stopButton);
        resetButton = (ImageButton) findViewById(R.id.resetButton);
        volumeControl = (ImageButton) findViewById(R.id.volumeControl);
        displayRoad = (TextView) findViewById(R.id.roadText);

        directionDistance = (TextView) findViewById(R.id.distanceText);
        directionImg = (ImageView) findViewById(R.id.directionSign);
        nextdirectionDistance = (TextView) findViewById(R.id.nextDistanceText);
        nextDirectionImg = (ImageView) findViewById(R.id.nextDirectionSign);
        stopButton = (ImageButton) findViewById(R.id.stopButton);
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
//                Toast.makeText(getApplicationContext(), "volumeControl", Toast.LENGTH_SHORT).show();
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
        currentLocation = nowLocation();
        startPlaceLat = 37.551451;
        startPlaceLon = 127.073621;
        //currentLocation.getLatitude(), currentLocation.getLongitude()
        //37.551451, 127.073621
        //세종대학교 용덕관
        TMapPoint startPoint = new TMapPoint(37.551451, 127.073621);
        //37.540542, 127.069236
        //Double.parseDouble(latitude), Double.parseDouble(longitude)
        //광진소방서
        TMapPoint endPoint = new TMapPoint(37.545169, 127.082834);
        TMapData tmapdata = new TMapData();
        tmapview = new TMapView(this);

        tmapview.setLocationPoint(startPoint.getLongitude(), startPoint.getLatitude());

        passList = getJsonData(startPoint, endPoint);
        time.setText(displayException.strTime(totalTime));
        Log.e("totalTime", displayException.strTime(totalTime));
        totalDis.setText(displayException.strDistance(totalDistance));
        Log.e("totalDis", String.valueOf(totalDistance / 1000) + "km");
        displayRoad.setText(roadName.get(index));
        setTMap();

        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, startPoint, endPoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
//폴리 라인을 그리고 시작, 끝지점의 거리를 산출하여 로그에 표시한다
                Bitmap start = BitmapFactory.decodeResource(getResources(), R.drawable.startpurple_resized);
                Bitmap end = BitmapFactory.decodeResource(getResources(), R.drawable.endpurple_resized);
                tmapview.setTMapPathIcon(start, end);
                polyLine.setLineColor(Color.GRAY);
                polyLine.setLineWidth(25);

                tmapview.addTMapPath(polyLine);
                double wayDistance = polyLine.getDistance();
                Log.d(TAG, "Distance: " + wayDistance + "M");
            }
        });

        execute(startPoint, endPoint);
        changeTurnTypeDirection(turnTypeList.get(index + 1), turnTypeList.get(index + 2));
//        ((MainActivity)MainActivity.mContext).sendMessage("100 14");
    }

    public void showdDesibelStandard() {
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
        b1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
            }
        });
        b2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void setTMap() {
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

    public void setGPS() {
        tmapgps = new TMapGpsManager(this);
        tmapgps.setMinDistance(0);
        tmapgps.setMinTime(0);
        tmapgps.OpenGps();
        tmapgps.setProvider(tmapgps.GPS_PROVIDER);
    }

    public Location nowLocation() {
        Location myLocation = null;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            setGPS();
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.e("myLocation: ", "nowLocation함수 들림");
            myLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (myLocation == null) {
                myLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (myLocation != null) {
                    Log.e("myLocation: ", String.valueOf(myLocation.getLatitude()));
                }
            } else if (myLocation != null) {
                //네트워크 ,gps, 둘다 받으면 그 둘중에 뭐가 더 최적의  프로바이더 인지 뽑아내가지고, location 안에다가 데이터 집어넣는 것
//                Criteria criteria = new Criteria();
//                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//                String provider = lm.getBestProvider(criteria, true);
//                myLocation = lm.getLastKnownLocation(provider);
//                Log.d("myLocation: ", String.valueOf(myLocation.getLatitude()));
            }
        }
        return myLocation;
    }

    public int checkArea(Location location, int distanceInMeters) {
        int turnType = 0;
        int r;
        //double distanceInMeters = currentToPointDistance(location, cnt);
//        for (int i = 0; i < passList.size(); i++) {
            if (index == passList.size() - 1) {
                //도착지 범위는 좀 더 작게
                //r = Radius(passList.get(i).getLatitude(), passList.get(i).getLongitude(), nowPlace.getLatitude(), nowPlace.getLongitude());
                if (distanceInMeters <= 30) {
                    turnType = turnTypeList.get(passList.size()-1);
                    //Toast.makeText(getApplicationContext(), "도착", Toast.LENGTH_SHORT).show();
                    Log.e("checkarea", "도착");
                }
            }
            else if (index == 0) {
                if (distanceInMeters <= 30) {
                    signalStopCheck = false;
                    oneMoreAlarm = false;
                    oneMoreAlarmSignalStopCheck = false;
                    index++;
                    //Toast.makeText(getApplicationContext(),"index == 0 ->"+String.valueOf(index),Toast.LENGTH_SHORT).show();
                }
            }
            else if(index > 0 && index < passList.size()-1) {

                if (distanceInMeters <= 100 && distanceInMeters > 10) {
                    turnType = turnTypeList.get(index);
                    oneMoreAlarmSignalStopCheck = true;
                    oneMoreAlarm = false;
                    //Toast.makeText(getApplicationContext(),"index: "+String.valueOf(index)+" 100미터 미만",Toast.LENGTH_SHORT).show();
                }
                else if (distanceInMeters > 100) {
                    turnType = turnTypeList.get(index);
                    oneMoreAlarm = true;
                    //Toast.makeText(getApplicationContext(),"index: "+String.valueOf(index)+" 100미터 이상",Toast.LENGTH_SHORT).show();
                }
                else if (distanceInMeters <= 10) {
                    Log.e("checkarea", "다음인덱스로 넘어갈 단계");
                    initProcess();
                    index++;
                    Toast.makeText(getApplicationContext(), "다음 인덱스로 넘어감", Toast.LENGTH_SHORT).show();
                    changeTopUIHandler.sendEmptyMessage(0);
                    tmapview.setCompassMode(true);
                    tmapview.setMarkerRotate(true);
                }
            }
//        }
        return turnType;
    }

    public double currentToPointDistance(Location myLocation, int cnt) {
        double distance = 0;
        distance = currentDistance(passList.get(cnt).getLatitude(), passList.get(cnt).getLongitude(), myLocation.getLatitude(), myLocation.getLongitude());

        return distance;
    }

    public void execute(TMapPoint startpoint, TMapPoint endpoint) {

        Log.e("excecute함수", "실행");
        //tmapview = new TMapView(this);

        tmapview.setLocationPoint(startpoint.getLongitude(), startpoint.getLatitude());
        setTMap();
        //passList = getJsonData(startpoint, endpoint);

        for (int i = 0; i < passList.size(); i++) {
            TMapMarkerItem item = new TMapMarkerItem();
            TMapPoint passpoint = new TMapPoint(passList.get(i).getLatitude(), passList.get(i).getLongitude());
            Bitmap mark = null;
            if (turnTypeList.get(i) == 11) {
                mark = BitmapFactory.decodeResource(getResources(), R.drawable.go);
            } else if (turnTypeList.get(i) == 12) {
                mark = BitmapFactory.decodeResource(getResources(), R.drawable.left);
            } else if (turnTypeList.get(i) == 13) {
                mark = BitmapFactory.decodeResource(getResources(), R.drawable.right);
            } else if (turnTypeList.get(i) == 14) {
                mark = BitmapFactory.decodeResource(getResources(), R.drawable.uturn);
            } else {
                mark = BitmapFactory.decodeResource(getResources(), R.drawable.tranparent);
            }

            item.setTMapPoint(passpoint);
            item.setIcon(mark);
            tmapview.bringMarkerToFront(item);
            tmapview.addMarkerItem(String.valueOf(i), item);
        }
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.guide_arrow_blue);
        tmapview.setIcon(bitmap);
        mapView.addView(tmapview);
    }

    private Handler changeTopUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e("[ TopUI구간과 방향Handler ] ", String.valueOf(index + 1));
            if (index < turnTypeList.size() - 1) {
                changeTurnTypeDirection(turnTypeList.get(index), turnTypeList.get(index + 1));
            } else if (index == turnTypeList.size() - 1) {
                changeTurnTypeDirection(turnTypeList.get(index), 0);
            }
        }
    };

    private Handler intentHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(NavigationActivity.this, StopNavigation.class);
            startActivityForResult(intent, 1);
            finish();
        }
    };

    public void changeTurnTypeDirection(int turntype, int nextturntype) {
        Log.e(TAG, String.valueOf(turntype) + " " + String.valueOf(nextturntype));
        switch (turntype) {
            case 11:
                directionImg.setImageResource(R.drawable.straight);
                break;
            case 12:
                directionImg.setImageResource(R.drawable.turnleft);
                break;
            case 13:
                directionImg.setImageResource(R.drawable.turnright);
                break;
            case 14:
                directionImg.setImageResource(R.drawable.uturn);
                break;
            case 201:
                directionImg.setImageResource(R.drawable.endpurple_resized);
                break;
        }
        switch (nextturntype) {
            case 0:
                nextDirectionImg.setImageResource(R.drawable.tranparent);
                break;
            case 11:
                nextDirectionImg.setImageResource(R.drawable.straight);
                break;
            case 12:
                nextDirectionImg.setImageResource(R.drawable.turnleft);
                break;
            case 13:
                nextDirectionImg.setImageResource(R.drawable.turnright);
                break;
            case 14:
                nextDirectionImg.setImageResource(R.drawable.uturn);
                break;
            case 201:
                nextDirectionImg.setImageResource(R.drawable.endpurple_resized);
                break;
        }

        if (index < pointDistanceList.size()) {
            directionDistance.setText(displayException.strDistance(pointDistanceList.get(index)));
            displayRoad.setText(roadName.get(index));
        }
        if (index + 1 < pointDistanceList.size()-1) {
            nextdirectionDistance.setText(displayException.strDistance(pointDistanceList.get(index + 1)));
        }
        if(turntype == 201)
        {
            nextdirectionDistance.setText(" ");
        }
    }

    public void signalTurnType(int type) {
        //Toast.makeText(getApplicationContext(), "알림", Toast.LENGTH_SHORT).show();
        switch (type) {
//            case 201:
//                Toast.makeText(getApplicationContext(), "도착", Toast.LENGTH_SHORT).show();
//                ((MainActivity) MainActivity.mContext).sendMessage(String.valueOf(distance) + " 201.");
//                break;
            case 11:
                //Toast.makeText(getApplicationContext(), "직진", Toast.LENGTH_SHORT).show();
                ((MainActivity) MainActivity.mContext).sendMessage(String.valueOf(distance) + " 11.");
                break;
            case 12:
                //Toast.makeText(getApplicationContext(), "좌회전", Toast.LENGTH_SHORT).show();
                ((MainActivity) MainActivity.mContext).sendMessage(String.valueOf(distance) + " 12.");
                break;
            case 13:
                //Toast.makeText(getApplicationContext(), "우회전", Toast.LENGTH_SHORT).show();
                ((MainActivity) MainActivity.mContext).sendMessage(String.valueOf(distance) + " 13.");
                break;
            case 14:
                //Toast.makeText(getApplicationContext(), "U턴", Toast.LENGTH_SHORT).show();
                ((MainActivity) MainActivity.mContext).sendMessage(String.valueOf(distance) + " 14.");
                break;
        }
    }

    public void oneMoreSignalTurnType(int type) {
        //Toast.makeText(getApplicationContext(), "예비 알림", Toast.LENGTH_SHORT).show();
            switch (type) {
                case 11:
                    //Toast.makeText(getApplicationContext(), "직진", Toast.LENGTH_SHORT).show();
                    ((MainActivity) MainActivity.mContext).sendMessage(String.valueOf(distance) + " 104.");
                    break;
                case 12:
                    //Toast.makeText(getApplicationContext(), "좌회전", Toast.LENGTH_SHORT).show();
                    ((MainActivity) MainActivity.mContext).sendMessage(String.valueOf(distance) + " 102.");
                    break;
                case 13:
                    //Toast.makeText(getApplicationContext(), "우회전", Toast.LENGTH_SHORT).show();
                    ((MainActivity) MainActivity.mContext).sendMessage(String.valueOf(distance) + " 103.");
                    break;
                case 14:
                    //Toast.makeText(getApplicationContext(), "U턴", Toast.LENGTH_SHORT).show();
                    ((MainActivity) MainActivity.mContext).sendMessage(String.valueOf(distance) + " 105.");
                    break;
            }
        }



    public ArrayList<TMapPoint> getJsonData(final TMapPoint startPoint, final TMapPoint endPoint) {
        Thread thread = new Thread() {
            //내가 본 예시에서는 Thread를 따로 파서 넣었는데 json파싱할때 어싱크태스크를 이용한 코드들도 봤음!
            //Thread가 돌아가면 바로 이 안으로 들어오지않고 밑에 보면 Thread.start 있는데 그거 실행해야 안으로 들어옴!
            @Override
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();

                String urlString = "https://apis.skplanetx.com/tmap/routes?version=1&format=json&appKey=cad2cc9b-a3d5-3c32-8709-23279b7247f9";
                //String urlString = "https://apis.skplanetx.com/tmap/routes/pedestrian?callback=&bizAppId=&version=1&format=json&appKey=e2a7df79-5bc7-3f7f-8bca-2d335a0526e7";

                // &format={xml 또는 json}
                try {
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
                    if (response.getEntity() != null)
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

                    for (int i = 0; i < features.length(); i++) {
                        JSONObject root = features.getJSONObject(i);
                        JSONObject properties = root.getJSONObject("properties");
                        if (i == 0) { //총거리 총시간이 for문 돌리기 맨 처음에 있으니 여기서 뽑아주고

                            totalDistance += properties.getInt("totalDistance");
                            Log.e("totalDistance", String.valueOf(totalDistance));
                            totalTime += properties.getInt("totalTime");
                        }
                        JSONObject geometry = root.getJSONObject("geometry");
                        Log.e("geometry", "파싱성공");
                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                        Log.e("coordinates", "파싱성공");
                        String geoType = geometry.getString("type");
                        Log.e("type", "파싱성공");
                        if (geoType.equals("Point")) { //여기서 경로에서 방향변환지점들이 있을거아냐 그 지점들 위도경도를 받아옴
                            Log.e("Point", "파싱성공");
                            double lonJson = coordinates.getDouble(0);
                            double latJson = coordinates.getDouble(1);
                            int turnType = properties.getInt("turnType");
                            String description = properties.getString("description");

                            Log.e(TAG, "Point-");
                            Log.e(TAG, lonJson + "," + latJson + "\n");
                            //if(i == 0){ startPlaceLat = latJson; startPlaceLon = lonJson;}
                            TMapPoint point = new TMapPoint(latJson, lonJson);

                            turnTypeList.add(turnType);

                            passList.add(point);

                        }
                        if (geoType.equals("LineString")) { //이 태그도 위랑 비슷한데 경로 중간중간 위도경도 받을 수 있어!
                            int pointDis = properties.getInt("distance");
                            String roadname = properties.getString("name");
                            roadName.add(roadname);

                            pointDistanceList.add(pointDis);

                        }
                    }
                    Log.e("json turn", String.valueOf(turnTypeList.size()));
                    Log.e("json pass", String.valueOf(passList.size()));
                    Log.e("json road", String.valueOf(roadName.size()));
                    Log.e("json distance", String.valueOf(pointDistanceList.size()));
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

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return passList;
    }

    public double currentDistance(double lat1, double lon1, double lat2, double lon2) {

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
    private double degToRad(double deg) {
        return (double) (deg * Math.PI / (double) 180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double radToDeg(double rad) {
        return (double) (rad * (double) 180d / Math.PI);
    }

    public void btnNowLocation(View v) {

        tmapview.setLocationPoint(currentLocation.getLongitude(), currentLocation.getLatitude());
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

    //SoundAnalyze horn alert Handler
    public static Handler changeEntireView = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    entireView.setBackgroundColor(Color.TRANSPARENT);
                }
            }, 2000);
            entireView.setBackgroundColor(Color.parseColor("#80FF0000"));
        }
    };

    //SoundAnalyze
    private void LoadPreferences() {
        // Load preferences for recorder and views, beside loadPreferenceForView()
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        boolean keepScreenOn = sharedPref.getBoolean("keepScreenOn", true);
        if (keepScreenOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        analyzerParam.audioSourceId = Integer.parseInt(sharedPref.getString("audioSource", Integer.toString(analyzerParam.RECORDER_AGC_OFF)));
        analyzerParam.wndFuncName = sharedPref.getString("windowFunction", "Hanning");
        analyzerParam.spectrogramDuration = Double.parseDouble(sharedPref.getString("spectrogramDuration",
                Double.toString(6.0)));
        analyzerParam.overlapPercent = Double.parseDouble(sharedPref.getString("fft_overlap_percent", "50.0"));
        analyzerParam.hopLen = (int) (analyzerParam.fftLen * (1 - analyzerParam.overlapPercent / 100) + 0.5);

        analyzerParam.sampleRate = sharedPref.getInt("button_sample_rate", 8000);
        analyzerParam.fftLen = sharedPref.getInt("button_fftlen", 1024);
        analyzerParam.nFFTAverage = sharedPref.getInt("button_average", 1);

        // spectrogram
        analyzerViews.graphView.setSpectrogramModeShifting(sharedPref.getBoolean("spectrogramShifting", false));
        analyzerViews.graphView.setShowTimeAxis(sharedPref.getBoolean("spectrogramTimeAxis", true));
        analyzerViews.graphView.setShowFreqAlongX(sharedPref.getBoolean("spectrogramShowFreqAlongX", true));
        analyzerViews.graphView.setSmoothRender(sharedPref.getBoolean("spectrogramSmoothRender", false));
        analyzerViews.graphView.setColorMap(sharedPref.getString("spectrogramColorMap", "Hot"));
        // set spectrogram show range
        analyzerViews.graphView.setSpectrogramDBLowerBound(Float.parseFloat(
                sharedPref.getString("spectrogramRange", Double.toString(analyzerViews.graphView.spectrogramPlot.spectrogramBMP.dBLowerBound))));

        analyzerViews.bWarnOverrun = sharedPref.getBoolean("warnOverrun", false);
        analyzerViews.setFpsLimit(Double.parseDouble(
                sharedPref.getString("spectrogramFPS", getString(R.string.spectrogram_fps_default))));
    }

    private void restartSampling(final AnalyzerParameters _analyzerParam) {
        // Stop previous sampler if any.
        if (samplingThread != null) {
            samplingThread.finish();
            try {
                samplingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            samplingThread = null;
        }

        if (viewRangeArray != null) {
            analyzerViews.graphView.setupAxes(analyzerParam);
            double[] rangeDefault = analyzerViews.graphView.getViewPhysicalRange();
            Log.i(TAG, "restartSampling(): setViewRange: " + viewRangeArray[0] + " ~ " + viewRangeArray[1]);
            analyzerViews.graphView.setViewRange(viewRangeArray, rangeDefault);
            if (!isLockViewRange) viewRangeArray = null;  // do not conserve
        }

        // Set the view for incoming data
        graphInit = new Thread(new Runnable() {
            public void run() {
                analyzerViews.setupView(_analyzerParam);
            }
        });
        graphInit.start();

        // Check and request permissions
        if (!checkAndRequestPermissions())
            return;

        if (!bSamplingPreparation)
            return;

        // Start sampling
        samplingThread = new SamplingLoop(this, _analyzerParam);
        samplingThread.start();
    }

    private boolean checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(NavigationActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Permission RECORD_AUDIO denied. Trying  to request...");
            if (ActivityCompat.shouldShowRequestPermissionRationale(NavigationActivity.this, Manifest.permission.RECORD_AUDIO) &&
                    count_permission_explanation < 1) {
                Log.w(TAG, "  Show explanation here....");
                count_permission_explanation++;
            } else {
                Log.w(TAG, "  Requesting...");
                if (count_permission_request < 3) {
                    ActivityCompat.requestPermissions(NavigationActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                    count_permission_explanation = 0;
                    count_permission_request++;
                } else {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Context context = getApplicationContext();
                            String text = "Permission denied.";
                            Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                }
            }
            return false;
        }
        if (bSaveWav &&
                ContextCompat.checkSelfPermission(NavigationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Permission WRITE_EXTERNAL_STORAGE denied. Trying  to request...");
            bSaveWav = false;
            ActivityCompat.requestPermissions(NavigationActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.w(TAG, "RECORD_AUDIO Permission granted by user.");
                } else {
                    Log.w(TAG, "RECORD_AUDIO Permission denied by user.");
                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.w(TAG, "WRITE_EXTERNAL_STORAGE Permission granted by user.");
                    if (!bSaveWav) {
                        Log.w(TAG, "... bSaveWav == true");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bSaveWav = true;
                            }
                        });
                    } else {
                        Log.w(TAG, "... bSaveWav == false");
                    }
                } else {
                    Log.w(TAG, "WRITE_EXTERNAL_STORAGE Permission denied by user.");
                }
                break;
            }
        }
        // Then onResume() will be called.
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putDouble("dtRMS", dtRMS);
        savedInstanceState.putDouble("dtRMSFromFT", dtRMSFromFT);
        savedInstanceState.putDouble("maxAmpDB", maxAmpDB);
        savedInstanceState.putDouble("maxAmpFreq", maxAmpFreq);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // will be called after the onStart()
        super.onRestoreInstanceState(savedInstanceState);

        dtRMS = savedInstanceState.getDouble("dtRMS");
        dtRMSFromFT = savedInstanceState.getDouble("dtRMSFromFT");
        maxAmpDB = savedInstanceState.getDouble("maxAmpDB");
        maxAmpFreq = savedInstanceState.getDouble("maxAmpFreq");
    }

    @Override
    protected void onResume() {
        super.onResume();

        LoadPreferences();
        analyzerViews.graphView.setReady(this);  // TODO: move this earlier?

        // Used to prevent extra calling to restartSampling() (e.g. in LoadPreferences())
        bSamplingPreparation = true;

        // Start sampling
        restartSampling(analyzerParam);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("onStart", "true");
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        Log.e("onRestart", "true");
    }

    @Override
    protected void onPause() {
        bSamplingPreparation = false;
        if (samplingThread != null) {
            samplingThread.finish();
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void ready() {
        // put code here for the moment that graph size just changed
        Log.v(TAG, "ready()");
        analyzerViews.invalidateGraphView();
    }

}