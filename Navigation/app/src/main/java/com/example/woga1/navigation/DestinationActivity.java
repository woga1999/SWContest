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
import android.widget.Toast;

public class DestinationActivity extends AppCompatActivity {
//최근목적지를 나타내는 Activity

//    static final String[] LIST_MENU = {"홍대", "건대", "세종대학교","어린이대공원역"} ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        Intent intent = getIntent();
        String[] myStrings = intent.getStringArrayExtra("destination");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_destinationbar);

        ImageButton backImageButton = (ImageButton) findViewById(R.id.backimageButton);
        backImageButton.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DestinationActivity.this, MenuActivity.class));
            }

        });

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, myStrings) ;

        ListView listview = (ListView) findViewById(R.id.listview1) ;
        listview.setAdapter(adapter) ;

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                // get TextView's Text.
                String strText = (String) parent.getItemAtPosition(position) ;

                Intent intents = new Intent(DestinationActivity.this, ReadyActivity.class);
                intents.putExtra("destination", strText);
                //Toast.makeText(getApplicationContext(),names[position], Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),strText, Toast.LENGTH_SHORT).show();
                startActivityForResult(intents, 1);
                // TODO : use strText
            }
        }) ;

    }
}
