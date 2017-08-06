package com.example.woga1.navigation;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class StopNavigation extends Activity {
    //네비게이션 화면에서 사용종료시키는 팝업창
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_stop_navigation);
    }
}
