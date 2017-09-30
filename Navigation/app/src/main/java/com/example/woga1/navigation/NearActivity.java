package com.example.woga1.navigation;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

public class NearActivity extends AppCompatActivity {
    //주변검색 할 때 나오는 Activity(주유소 등등) poi
    TMapData tmapData;
    ListView listviewPOI;
    ArrayAdapter adapter;
    TMapGpsManager tMapGpsManager;
    ArrayList<String> POIName = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near);
        tmapData = new TMapData();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_nearbar);
        tMapGpsManager = new TMapGpsManager(this);
        tMapGpsManager.setProvider(TMapGpsManager.NETWORK_PROVIDER);
        tMapGpsManager.OpenGps();

        Button LPG = (Button) findViewById(R.id.btnLPG);
        Button Food = (Button) findViewById(R.id.btnFoodStore);
        Button Mart = (Button) findViewById(R.id.btnMart);
        ImageButton backImageButton = (ImageButton) findViewById(R.id.backimageButton);
        listviewPOI = (ListView) findViewById(R.id.poiListView);
        listviewPOI.setAdapter(adapter);
        backImageButton.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NearActivity.this, MenuActivity.class));
            }

        });
        LPG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                searchAroundPOI();
                                // 해당 작업을 처리함
                            }
                        });
                    }
                }).start();
            }

        });

        Food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TMapPoint tmappoint = new TMapPoint(37.550498, 127.073182);
//                tmapData.findAroundNamePOI(tmappoint, "편의점", new TMapData.FindAroundNamePOIListenerCallback()
//                {
//                    @Override
//                    public void onFindAroundNamePOI(ArrayList<TMapPOIItem> poiItem) {
//                        for(int i = 0; i < poiItem.size(); i++) {
//                            TMapPOIItem  item = poiItem.get(i);
//                            POIName.add(i, item.getPOIName().toString());
//                            Log.e("NearActivity POIName","POI Name: " + item.getPOIName().toString() + ", " +
//                                    "Address: " + item.getPOIAddress().replace("null", "")  +
//                                    ", " + "Point: " + item.getPOIPoint().toString());
//                        }
//                    }
//                });
//
//                    adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, POIName) ;
//                    listviewPOI.setAdapter(adapter);

            }

        });
        Mart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"클릭", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void searchAroundPOI()
    {
        TMapPoint tmappoint = new TMapPoint(37.550498, 127.073182);
        tmapData.findAroundNamePOI(tmappoint, "주유소", new TMapData.FindAroundNamePOIListenerCallback()
        {
            @Override
            public void onFindAroundNamePOI(ArrayList<TMapPOIItem> poiItem) {
                for(int i = 0; i < poiItem.size(); i++) {
                    TMapPOIItem  item = poiItem.get(i);
                    POIName.add(i, item.getPOIName().toString());
                    Log.e("NearActivity POIName","POI Name: " + item.getPOIName().toString() + ", " +
                            "Address: " + item.getPOIAddress().replace("null", "")  +
                            ", " + "Point: " + item.getPOIPoint().toString());
                    //adapter.add(POIName);
                }

            }
        });

    }

    public void btnLPG(View v){
        TMapPoint tmappoint = new TMapPoint(37.550498, 127.073182);
        try {
            ArrayList<TMapPOIItem> tMapPOIItems =  tmapData.findAroundKeywordPOI (tmappoint, "주유소", 1, 20);
            for(int i=0; i< tMapPOIItems.size(); i++)
            {
                POIName.add(i, tMapPOIItems.toString());
            }
            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, POIName) ;
            listviewPOI.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public void btnFoodStore(View v){
        TMapPoint tmappoint = new TMapPoint(37.550498, 127.073182);
        try {
            ArrayList<TMapPOIItem> tMapPOIItems =  tmapData.findAroundKeywordPOI (tmappoint, "음식점", 1, 20);
            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, tMapPOIItems) ;
            listviewPOI.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public void btnMart(View v){
        Toast.makeText(getApplicationContext(),"클릭", Toast.LENGTH_SHORT).show();
    }
}
