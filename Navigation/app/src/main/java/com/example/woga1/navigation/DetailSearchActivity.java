package com.example.woga1.navigation;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class DetailSearchActivity extends AppCompatActivity {
    //주소검색할 때 역시/도 , 시/군/구 , 도로/읍/면/동  으로 구분해서 검색하게 하는 Activity
    static final String[] LIST_MENU2 = {"서울특별시", "경기도", "인천광역시","강원도","경상남도","경상북도","광주광역시"
            ,"대구광역시","대전광역시","부산광역시","세종특별자치시","울산광역시","전라남도","전라북도","제주특별자치도","충청남도","충청북도"} ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_search);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_searchbar);

        ImageButton backImageButton = (ImageButton) findViewById(R.id.backimageButton);

        backImageButton.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailSearchActivity.this, SearchActivity.class));
            }

        });

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, LIST_MENU2) ;

        ListView listview = (ListView) findViewById(R.id.listview1) ;
        listview.setAdapter(adapter) ;

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                // get TextView's Text.
                String strText = (String) parent.getItemAtPosition(position) ;

                // TODO : use strText
            }
        }) ;
    }
}
