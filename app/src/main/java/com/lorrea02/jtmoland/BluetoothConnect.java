package com.lorrea02.jtmoland;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class BluetoothConnect extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter;
    Button btnConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connect);

        //turning off bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
            Intent enableBluetooth = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }
        else{
            Intent enableBluetooth = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        btnConnect = findViewById(R.id.btnConnect);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(BluetoothConnect.this,"Connecting...", Toast.LENGTH_LONG).show();
                try{
                    stopService(new Intent(BluetoothConnect.this, MyService.class));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                startService(new Intent(BluetoothConnect.this, MyService.class));
            }
        });
    }



    private BroadcastReceiver mReceiver;
    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter("com.lorrea02.jtmoland");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg_for_me = intent.getStringExtra("some_msg");
                //log our message value
                if(msg_for_me.equalsIgnoreCase("connected")) {
                    Toast.makeText(BluetoothConnect.this, "Printer successfully connected", Toast.LENGTH_LONG).show();
                    Intent goToMain = new Intent(BluetoothConnect.this,MainActivity.class);
                    startActivity(goToMain);
                }
            }
        };
        this.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.mReceiver);
    }
}
