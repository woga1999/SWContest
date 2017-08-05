package com.example.woga1.navigation;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class ReadyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable());

        Intent intent = getIntent();
        final String name = intent.getExtras().getString("destination");
        //Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();

        setContentView(R.layout.activity_ready);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_readybar);

        Button startNavigation = (Button) findViewById(R.id.startNavigation);
        ImageButton backImageButton = (ImageButton) findViewById(R.id.backimageButton);
        startNavigation.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(ReadyActivity.this, NavigationActivity.class));
                Intent intent = new Intent(ReadyActivity.this, NavigationActivity.class);
                intent.putExtra("destination", name);
                startActivityForResult(intent, 1);
            }

        });




        backImageButton.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReadyActivity.this, MenuActivity.class));
            }

        });
    }
}
