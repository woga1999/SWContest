package com.example.woga1.navigation.Navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.woga1.navigation.MainActivity;
import com.example.woga1.navigation.R;

public class StopNavigation extends Activity {
    //네비게이션 화면에서 사용종료시키는 팝업창

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_stop_navigation);
//        Button noButton = (Button)findViewById(R.id.noButton);
        Button finishButton = (Button)findViewById(R.id.finishButton);

//        noButton.setOnClickListener(new Button.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//
//        });

        finishButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StopNavigation.this, MainActivity.class);
                startActivityForResult(intent, 1);
            }

        });
    }


}
