package com.example.woga1.navigation;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class AddressSearchActivity extends Activity {

    RelativeLayout mapview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_search);


//        relativeLayout = new RelativeLayout(this);
        mapview = (RelativeLayout) findViewById(R.id.mapview);
//        execute();
        EditText search = (EditText) findViewById(R.id.search);
        search.setMovementMethod(null);
    }


}
