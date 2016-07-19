package com.csy.wechatred;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_start;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_start = (Button)this.findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.btn_start:
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                break;

            default:
                break;
        }
    }
}
