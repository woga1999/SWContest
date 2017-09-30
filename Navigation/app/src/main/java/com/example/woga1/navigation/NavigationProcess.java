package com.example.woga1.navigation;

/**
 * Created by woga1 on 2017-09-27.
 */

//public class NavigationProcess {
//    public int checkArea2(Location location, int cnt){ //40m 반경 체크
//        int signalTurnType = 0;
//        double lon = location.getLongitude();
//        double lat = location.getLatitude();
//        int r ;
//        for(int i=0; i<passList.size(); i++)
//        {
//            float[] results = new float[1];
//
////            boolean isWithin10km = distanceInMeters < 10000; //10km 넘는지 넘지 않는지
//            if(i == passList.size() && i == cnt){
//                //도착지 범위는 좀 더 작게
//                Location.distanceBetween(passList.get(i).getLatitude(), passList.get(i).getLongitude(), lat, lon, results);
//                float distanceInMeters = results[0];
//                //r = Radius(passList.get(i).getLatitude(), passList.get(i).getLongitude(), nowPlace.getLatitude(), nowPlace.getLongitude());
//                if(distanceInMeters<=10){
//                    signalTurnType = turnTypeList.get(i);}
//            }
//            else if(i == cnt)
//            {
//                double distanceInMeters = currentDistance(passList.get(i).getLatitude(), passList.get(i).getLongitude(), lat, lon);
////                Location.distanceBetween(passList.get(i).getLatitude(), passList.get(i).getLongitude(), lat, lon, results);
////                float distanceInMeters = results[0];
//                // r = Radius(passList.get(i).getLatitude(), passList.get(i).getLongitude(), nowPlace.getLatitude(), nowPlace.getLongitude());
//                //보통 포인트 범위 알림은 40m이고
//                if(distanceInMeters<=50 && distanceInMeters>10){
//                    signalTurnType = turnTypeList.get(i);}
//                else if(distanceInMeters<=10){
//                    signalStopCheck = false;
//                    index++;
//                }
//            }
//        }
//
//        return signalTurnType;
//    }
//public void execute(TMapPoint startpoint, TMapPoint endpoint) {
//
//
//        tmapview = new TMapView(this);
//
//        tmapview.setLocationPoint(startpoint.getLongitude(),startpoint.getLatitude());
//
//
//        tmapview.setTileType(TILETYPE_HDTILE);
//        tmapview.setSKPMapApiKey(tmapAPI);
//        tmapview.setCompassMode(true);
//        tmapview.setIconVisibility(true); //현재위치 파랑마커표시
//        tmapview.setZoomLevel(19);
//        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
//        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
//        tmapview.setTrackingMode(true); //화면중심을 단말의 현재위치로 이동시켜주는 트래킹 모드
//        tmapview.setPathRotate(true); //나침반 회전 시 출,도착 아이콘을 같이 회전시킬지 여부를 결정합니다.
//        passList = getJsonData(startpoint, endpoint);
//
//
//        for(int i=0; i<passList.size(); i++)
//        {
//        TMapMarkerItem item = new TMapMarkerItem();
//        TMapPoint passpoint = new TMapPoint(passList.get(i).getLatitude(), passList.get(i).getLongitude());
//        Bitmap mark = null;
//        if(turnTypeList.get(i) == 11)
//        {
//        mark = BitmapFactory.decodeResource(getResources(),R.drawable.direction_11_resized);
//        }
//        else if(turnTypeList.get(i) == 12)
//        {
//        mark = BitmapFactory.decodeResource(getResources(),R.drawable.direction_12_resized);
//        }
//        else if(turnTypeList.get(i) == 13)
//        {
//        mark = BitmapFactory.decodeResource(getResources(),R.drawable.direction_13_resized);
//        }
//        else if(turnTypeList.get(i) == 14)
//        {
//        mark = BitmapFactory.decodeResource(getResources(),R.drawable.direction_14_resized);
//        }
//        else{
//        mark = BitmapFactory.decodeResource(getResources(), R.drawable.tranparent);
//        }
//
//        item.setTMapPoint(passpoint);
//        item.setIcon(mark);
//        tmapview.bringMarkerToFront(item);
//        tmapview.addMarkerItem(String.valueOf(i), item);
//        }
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.guide_arrow_blue);
//        tmapview.setIcon(bitmap);
//        mapView.addView(tmapview);
//        }
//  Log.d( TAG, "Initalizing Bluetooth adapter...");
//          //1.블루투스 사용 가능한지 검사합니다.
//          mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
////        if (mBluetoothAdapter == null) {
////            Log.e("aaa","1");
////            showErrorDialog("This device is not implement Bluetooth.");
////            return;
////        }
//
//          if (!mBluetoothAdapter.isEnabled()) {
//          //블루투스 허용되지 않았을 때
//          Log.e("aaa","2");
//          Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//          startActivityForResult(intent, REQUEST_BLUETOOTH_ENABLE);
//          }
//          else {
//          //블루투스 허용되어 있을 때
//          Log.e("aaa","3");
//          Log.d(TAG, "Initialisation successful.");
//
//          //2. 페어링 되어 있는 블루투스 장치들의 목록을 보여줍니다.
//          //3. 목록에서 블루투스 장치를 선택하면 선택한 디바이스를 인자로 하여
//          //   doConnect 함수가 호출됩니다.
//          //showPairedDevicesListDialog();
//          }
//}

