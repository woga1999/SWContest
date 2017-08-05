package com.example.woga1.navigation;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    private GridView imageGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        EditText search = (EditText) findViewById(R.id.search);
        imageGridView = (GridView) findViewById(R.id.gridView);

        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button) findViewById(R.id.button4);
        Button button5 = (Button) findViewById(R.id.button5);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);



        search.setMovementMethod(null);

        ArrayList<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.mapholder);
        imageList.add(R.drawable.mapholder);
        imageList.add(R.drawable.mapholder);
        imageList.add(R.drawable.mapholder);
        imageList.add(R.drawable.mapholder);
        imageList.add(R.drawable.mapholder);
        imageList.add(R.drawable.mapholder);
        imageList.add(R.drawable.mapholder);
        imageList.add(R.drawable.mapholder);
        imageList.add(R.drawable.mapholder);
        imageList.add(R.drawable.mapholder);
        imageList.add(R.drawable.mapholder);
        imageList.add(R.drawable.mapholder);
        imageList.add(R.drawable.mapholder);
        imageList.add(R.drawable.mapholder);

        ImageGridViewCustomAdapter customAdapter = new ImageGridViewCustomAdapter(this,imageList);
        imageGridView.setAdapter(customAdapter);

        imageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(MenuActivity.this,""+id,Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MenuActivity.this, ReadyActivity.class));
            }
        });

        search.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, SearchActivity.class));
            }

        });

        button1.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, FavoriteActivity.class));
            }

        });

        button2.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, NearActivity.class));
            }

        });

        button3.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, DestinationActivity.class));
            }

        });
    }



}
