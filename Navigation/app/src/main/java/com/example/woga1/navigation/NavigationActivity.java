package com.example.woga1.navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class NavigationActivity extends Activity {
    //Navigation화면으로 나오는 Activity
    ImageButton stopButton;
    TextView destinationText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Intent intent = getIntent();
        final String name = intent.getExtras().getString("destination");
        Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();

        destinationText = (TextView) findViewById(R.id.destination);
        destinationText.setText(name);
        stopButton = (ImageButton) findViewById(R.id.imageButton2);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigationActivity.this, StopNavigation.class);
//                intent.putExtra("data", "Test Popup");
                startActivityForResult(intent, 1);

            }
        });



    }
}
