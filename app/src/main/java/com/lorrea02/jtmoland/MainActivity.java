package com.lorrea02.jtmoland;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    Button btnCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCode = findViewById(R.id.btnPin);

        File dir = new File(Environment.getExternalStorageDirectory(), "JTMoland");
        if (!dir.exists() || !dir.isDirectory())
            dir.mkdirs();
        File expDir = new File(dir, "Export");
        if (!expDir.exists() || !expDir.isDirectory())
            expDir.mkdirs();


        btnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent billMonthIntent = new Intent(MainActivity.this, BillMonth.class);
                startActivity(billMonthIntent);
            }
        });
    }
}
