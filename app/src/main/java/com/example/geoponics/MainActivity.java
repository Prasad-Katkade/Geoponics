package com.example.geoponics;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    Button btn;
    Animation circleIN,fadein,circleOut;
    ImageView circle,logo;
    TextView txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circle=findViewById(R.id.circle);
        logo=findViewById(R.id.logo);
        circleIN= AnimationUtils.loadAnimation(this,R.anim.circle_scale_in);
        fadein= AnimationUtils.loadAnimation(this,R.anim.logo_fade_in);
        circleOut=AnimationUtils.loadAnimation(this,R.anim.circle_scale_out);
        circle.setAnimation(circleIN);
        logo.setVisibility(View.INVISIBLE);
        txt=findViewById(R.id.title);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                logo.setVisibility(View.VISIBLE);
                logo.setAnimation(fadein);
            }
        },2000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

              Intent intent = new Intent(MainActivity.this, Login.class);
              Pair[] pairs = new Pair[2];
              pairs[0] = new Pair<View,String>(logo,"logo_anim");
              pairs[1] = new Pair<View,String>(txt,"title_anim");
              ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
              startActivity(intent,options.toBundle());

            }
        },4000);





    }
}