package com.example.woga1.navigation.Navigation;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by woga1 on 2017-09-27.
 */

public class DisplayException {
    public static final String TAG = "DisplayException";

    public static double STATIC_CURRENT_LONGITUDE = 0.0;
    public static double STATIC_CURRENT_LATITUDE = 0.0;
    public static boolean STATIC_CURRENT_GPS_CHECK = false;
    Context mContext;
    public DisplayException(Context context){
        mContext = context;
    }
    //네비액티비티에서 쓸 남은 시간
    public int remainTime(int totalTime, double speed, int meter) {
        int result = 0;
        double min = (meter / speed) * 60;
        int min2 = (totalTime / 60) - (int) min;
        return result;
    }
    public String strRemainTime(int totalTime, double speed, int meter) {
        String result = "";
        double min = (meter / speed) / 60;
        int min2 = (totalTime / 60) - (int) min;
        HashMap<String, String> previewTime = new HashMap();

        if (min2 > 0) {
            result = String.valueOf(min2) + "분";
            previewTime.put("Time", String.valueOf(min2));
        } else if (min2 < 0) {
            result = previewTime.get("Time") + "분";
        }

//        hour = totalTime / 3600;
//        minute = totalTime % 3600 / 60;
//
//        if( hour == 0 ) result = minute + "분";
//        else result = hour+"시간 " + minute +"분";

        return result;
    }

    public String strTime(int totalTime)
    {
        String result = "";
        int hour = totalTime / 3600;
        int minute = totalTime % 3600 / 60;

        if( hour == 0 ) result = minute + "분";
        else result = hour+"시간 " + minute +"분";

        return result;
    }

    // 거리
    public String strDistance(int distance){
        String result = "";

        if( distance >= 1000 ) result = distance / 1000 + "." + (distance % 1000 ) / 100 + " km";
        else result = distance + " m";

        return result;
    }   // strDistance

    // 총 남은거리
    public String strRemainDistance(int total_distance, int remain_distance){
        String result = "";
        String strTotal = "";
        String strRemain = "";

        int value = total_distance - remain_distance;

        if(value>= 1000) {
            strRemain = value/1000+ "." + (value % 1000) / 100 +"km";
        }
        else {
            strRemain = value + "m";
        }
//        if( total_distance >= 1000 ) strTotal = total_distance / 1000 + "." + (total_distance % 1000 ) / 100;
//        else strTotal = "0." + total_distance/100;
//
//        if( remain_distance >= 1000 ) strRemain = remain_distance / 1000 + "." + (remain_distance % 1000 ) / 100;
//        else if( remain_distance <= 0 ) strRemain = "0.0";
//        else strRemain = "0." + remain_distance/100;

        return strRemain;
    }


    public String nowPlaceAdress(double lat, double lon){
        String latlongString;
        String addressString = "No address found";
        Geocoder gc = new Geocoder(mContext, Locale.KOREAN);
        try {

            List<Address> addresses = gc.getFromLocation(lat,lon, 1);
            StringBuilder sb = new StringBuilder();
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                    sb.append(address.getAddressLine(i)).append("\n");
				/*

				 * 우편번호는 나오지 않음

				 */
                sb.append(address.getAdminArea()).append(" ");		// 시
                //address.getLocality()
                sb.append("광진구" + " ");  		// 구
                sb.append(address.getThoroughfare()).append(" ");	// 동
                sb.append(address.getFeatureName()).append(" ");	// 번지
                addressString = sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addressString;
    }

    public String poiName(String name){
        String result = "";
        if( name.length() > 15 ) result = name.substring(0, 15) + "..";
        else result = name;

        return result;
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
}

