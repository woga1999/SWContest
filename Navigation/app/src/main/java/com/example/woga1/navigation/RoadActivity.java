package com.example.woga1.navigation;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Created by woga1 on 2017-07-30.
 */

public class RoadActivity extends Activity {


    private TMapView mMapView = null;
    public String mApiKey = "cad2cc9b-a3d5-3c32-8709-23279b7247f9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mMapView = new TMapView(this);
        mMapView.setSKPMapApiKey(mApiKey);
        TMapData tmapdata = new TMapData();
//        String data1 = "SKT타워";
//        String data2 = "서울 용산구 이태원동";
//        String data3 = "SKT타워";
//        try {
//            ArrayList<TMapPOIItem> arTMapPOIItem_1 = tmapdata.findTitlePOI("SKT타워");
//            ArrayList<TMapPOIItem> arTMapPOIItem_2 = tmapdata.findAddressPOI("서울 용산구 이태원동");
//            TMapPoint tpoint = new TMapPoint(37.570841, 126.985302);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        }TMapPolyLine tpolyline = new TMapPolyLine();
//
        TMapPoint startpoint = new TMapPoint(37.570841, 126.985302);
        TMapPoint endpoint = new TMapPoint(37.570841, 126.985302);
        RelativeLayout relativeLayout = new RelativeLayout(this);
       TMapData tMapData = new TMapData();
        TMapPoint tMapPointStart = new TMapPoint(37.557607, 126.926252);
       TMapPoint tMapPointEnd = new TMapPoint(37.556352, 126.927643);
        tmapdata.findPathDataAll(startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
            @Override
            public void onFindPathDataAll(Document document) {
                Element root = document.getDocumentElement();
                Log.d("debug", "tmap:totalDistance :" + root.getElementsByTagName("tmap:totalDistance").item(0).getTextContent());

                NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");
                for (int i = 0; i < nodeListPlacemark.getLength(); i++) {
                    NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                    for (int j = 0; j < nodeListPlacemarkItem.getLength(); j++) {
                        if (nodeListPlacemarkItem.item(j).getNodeName().equals("name")) {
                            Log.d("debug", nodeListPlacemarkItem.item(j).getTextContent().trim());
                        }
                    }
                }
            }
        });


    }
}

