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

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {
    static final String[] names = {"신도림역", "수목아트빌", "초지역", "휴먼타운","","","","","","","","","","",""} ;
    static final int[] images={R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,
            R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,
            R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,};
    private GridView gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        EditText search = (EditText) findViewById(R.id.search);
//        imageGridView = (GridView) findViewById(R.id.gridView);

        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button) findViewById(R.id.button4);
        Button button5 = (Button) findViewById(R.id.button5);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);

         search.setMovementMethod(null);

        gv = (GridView) findViewById(R.id.gridView);

        //Adapter
        ImageGridViewCustomAdapter adapter = new ImageGridViewCustomAdapter(this, getImageandText());
        gv.setAdapter(adapter);

//        ImageGridViewCustomAdapter customAdapter = new ImageGridViewCustomAdapter(this,imageList);
//        imageGridView.setAdapter(customAdapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Toast.makeText(getApplicationContext(),names[position],Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(MenuActivity.this, ReadyActivity.class));
                Intent intent = new Intent(MenuActivity.this, ReadyActivity.class);
                intent.putExtra("destination", names[position]);
                startActivityForResult(intent, 1);
            }
        });

//        ArrayList<Integer> imageList = new ArrayList<>();
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);

//        ImageGridViewCustomAdapter customAdapter = new ImageGridViewCustomAdapter(this,imageList);
//        imageGridView.setAdapter(customAdapter);
//
//        imageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                Toast.makeText(MenuActivity.this,""+id,Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(MenuActivity.this, ReadyActivity.class));
//            }
//        });

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
                Intent intent = new Intent(MenuActivity.this, DestinationActivity.class);
                intent.putExtra("destination", names);
                startActivityForResult(intent, 1);
            }

        });
    }


    private ArrayList<Player> getImageandText()
    {
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(new Player(names[0],images[0]));
        players.add(new Player(names[1],images[1]));
        players.add(new Player(names[2],images[2]));
        players.add(new Player(names[3],images[3]));
        players.add(new Player(names[4],images[4]));
        players.add(new Player(names[5],images[5]));
        players.add(new Player(names[6],images[6]));
        players.add(new Player(names[7],images[7]));
        players.add(new Player(names[8],images[8]));
        players.add(new Player(names[9],images[9]));
        players.add(new Player(names[10],images[10]));
        players.add(new Player(names[11],images[11]));
        players.add(new Player(names[12],images[12]));
        players.add(new Player(names[13],images[13]));
        players.add(new Player(names[14],images[14]));

        return players;
    }

}
