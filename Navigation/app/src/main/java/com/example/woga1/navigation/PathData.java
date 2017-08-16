package com.example.woga1.navigation;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;

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

/**
 * Created by woga1 on 2017-08-15.
 */

public class PathData {

    ArrayList<TMapPoint> passList = new ArrayList<TMapPoint>();
    ArrayList<TMapPoint> LineList = new ArrayList<TMapPoint>();
    ArrayList<Integer> turnTypeList = new ArrayList<Integer>();
    ArrayList<String> des = new ArrayList<String>();
    int totalDistance = 0;
    int totalTime = 0;
    private Context context;

    public PathData(Context context){
        this.context = context;
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

                            turnTypeList.add(turnType);

                            des.add(description);
                            passList.add(point);

                        }
                        if(geoType.equals("LineString"))
                        {
                            for (int j = 0; j < coordinates.length(); j++) {
                                if(j >=1 && i < coordinates.length()-1) {
                                    JSONArray JLinePoint = coordinates.getJSONArray(j);
                                    double lonJson = JLinePoint.getDouble(0);
                                    double latJson = JLinePoint.getDouble(1);

                                    Log.e(TAG, "LineString-");
                                    Log.e(TAG, lonJson + "," + latJson + "\n");
                                    TMapPoint point = new TMapPoint(latJson, lonJson);
                                    LineList.add(point);
                                }
                            }

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

    public ArrayList<Integer> getTurnType()
    {
        return turnTypeList;
    }

    public ArrayList<String> getDes() {
        return des;
    }
    public int getTotalDistance(){
        return totalDistance;
    }
}
