package com.example.woga1.navigation;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;

/**
 * Created by woga1 on 2017-08-04.
 */

//public class GpsActivity extends AppCompatActivity
//        implements TMapGpsManager.onLocationChangedCallback {
public class GpsActivity extends AppCompatActivity
        implements TMapGpsManager.onLocationChangedCallback {

    private Context mContext = null;
    private boolean m_bTrackingMode = true;

    private TMapGpsManager tmapgps = null;
    private TMapView tmapview = null;
    private static String mApiKey = "cad2cc9b-a3d5-3c32-8709-23279b7247f9"; // 발급받은 appKey
    private static int mMarkerID;

    private ArrayList<TMapPoint> m_tmapPoint = new ArrayList<TMapPoint>();
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>();
    private ArrayList<TMapPoint> m_mapPoint = new ArrayList<TMapPoint>();

    @Override
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mapview);
        tmapview = new TMapView(this);
        linearLayout.addView(tmapview);
        tmapview.setSKPMapApiKey(mApiKey);

        //addPoint();
        //showMarkerPoint();

        /* 현재 보는 방향 */
        tmapview.setCompassMode(true);

        /* 현위치 아이콘표시 */
        tmapview.setIconVisibility(true);

        /* 줌레벨 */
        tmapview.setZoomLevel(15);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        tmapgps = new TMapGpsManager(GpsActivity.this);


        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER); //연결된 인터넷으로 현 위치를 받습니다.
        //실내일 때 유용합니다.
        tmapgps.OpenGps();
        //tmapgps.setProvider(tmapgps.GPS_PROVIDER); //gps로 현 위치를 잡습니다.


        /*  화면중심을 단말의 현재위치로 이동 */
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);

        // 풍선에서 우측 버튼 클릭시 할 행동입니다
//        tmapview.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback()
//        {
//            @Override
//            public void onCalloutRightButton(TMapMarkerItem markerItem) {
//                Toast.makeText(MainActivity.this, "클릭", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

//    public void addPoint() { //여기에 핀을 꼽을 포인트들을 배열에 add해주세요!
//        // 강남 //
//        m_mapPoint.add(new MapPoint("강남", 37.510350, 127.066847));
//    }
//
//
//    public void showMarkerPoint() {// 마커 찍는거 빨간색 포인트.
//        for (int i = 0; i < m_mapPoint.size(); i++) {
//            TMapPoint point = new TMapPoint(m_mapPoint.get(i).getLatitude(),
//                    m_mapPoint.get(i).getLongitude());
//            TMapMarkerItem item1 = new TMapMarkerItem();
//            Bitmap bitmap = null;
//            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.poi_dot);
//
//            //poi_dot은 지도에 꼽을 빨간 핀 이미지입니다
//
//            item1.setTMapPoint(point);
//            item1.setName(m_mapPoint.get(i).getName());
//            item1.setVisible(item1.VISIBLE);
//
//            item1.setIcon(bitmap);
//
//            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.poi_dot);
//
//            // 풍선뷰 안의 항목에 글을 지정합니다.
//            item1.setCalloutTitle(m_mapPoint.get(i).getName());
//            item1.setCalloutSubTitle("서울");
//            item1.setCanShowCallout(true);
//            item1.setAutoCalloutVisible(true);
//
//            Bitmap bitmap_i = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.i_go);
//
//            item1.setCalloutRightButtonImage(bitmap_i);
//
//            String strID = String.format("pmarker%d", mMarkerID++);
//
//            tmapview.addMarkerItem(strID, item1);
//            mArrayMarkerID.add(strID);
//        }
//    }

}
