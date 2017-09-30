package com.example.woga1.navigation;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.skp.Tmap.TMapTapi;

import java.util.ArrayList;

public class NearActivity extends AppCompatActivity {
    //주변검색 할 때 나오는 Activity(주유소 등등) poi
    TMapData tmapData;
    ListView listviewPOI;
    ArrayAdapter adapter;
    TMapGpsManager tMapGpsManager;
    ArrayList<String> POIName = new ArrayList<>();
    ArrayList<TMapPOIItem> poiItems = new ArrayList<>();
    int count = 0;
    String API_KEY = "cad2cc9b-a3d5-3c32-8709-23279b7247f9";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near);
        tmapData = new TMapData();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_nearbar);

        TMapTapi tmaptapi = new TMapTapi(this);
        tmaptapi.setSKPMapAuthentication(API_KEY);
        Button LPG = (Button) findViewById(R.id.btnLPG);
        Button Food = (Button) findViewById(R.id.btnFoodStore);
        Button Mart = (Button) findViewById(R.id.btnMart);
        ImageButton backImageButton = (ImageButton) findViewById(R.id.backimageButton);
        listviewPOI = (ListView) findViewById(R.id.poiListView);
        listviewPOI.setAdapter(adapter);
        listviewPOI.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               TMapPoint point = poiItems.get(position).getPOIPoint();
                Toast.makeText(getApplicationContext(), "lat: "+point.getLatitude()+" lon: "+point.getLongitude(), Toast.LENGTH_SHORT).show();
            }
        });
        backImageButton.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NearActivity.this, MenuActivity.class));
            }

        });
        LPG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAroundPOI("주유소");
            }

        });

        Food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAroundPOI("음식");
            }

        });
        Mart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAroundPOI("편의점");
            }
        });
    }

    public void searchAroundPOI(final String category)
    {
        TMapPoint tmappoint = new TMapPoint(37.550498, 127.073182);
        POIName.clear();
        poiItems.clear();
        if(count != 0) {
            adapter.clear();
        }
        tmapData.findAroundNamePOI(tmappoint, category, new TMapData.FindAroundNamePOIListenerCallback()
        {
            @Override
            public void onFindAroundNamePOI(ArrayList<TMapPOIItem> poiItem) {
                for(int i = 0; i < poiItem.size(); i++) {
                    TMapPOIItem  item = poiItem.get(i);
                    POIName.add(i, item.getPOIName().toString());
                    poiItems.add(poiItem.get(i));
                    Log.e("NearActivity"+category,"POI Name: " + item.getPOIName().toString() + ", " +
                            "Address: " + item.getPOIAddress().replace("null", "")  +
                            ", " + "Point: " + item.getPOIPoint().toString());
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, POIName) ;

                                listviewPOI.setAdapter(adapter);
                                count++;
                                adapter.notifyDataSetChanged();
                                // 해당 작업을 처리함
                            }
                        });
                    }
                }).start();

            }
        });

    }
    public class ParsePOI extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... params) {
            return null;
        }
    }
}
