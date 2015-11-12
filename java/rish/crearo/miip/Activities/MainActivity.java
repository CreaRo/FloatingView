package rish.crearo.miip.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import rish.crearo.miip.R;
import rish.crearo.miip.Service.ServiceFloating;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button launch = (Button) findViewById(R.id.button1);
        launch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, ServiceFloating.class));
            }
        });

        Button stop = (Button) findViewById(R.id.button2);
        stop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, ServiceFloating.class));
            }
        });

        Button config = (Button) findViewById(R.id.button_config);
        config.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, Configurations.class);
                startActivity(intent);
                stopService(new Intent(MainActivity.this, ServiceFloating.class));
            }
        });
    }
}