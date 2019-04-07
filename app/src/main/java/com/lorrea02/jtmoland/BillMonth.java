package com.lorrea02.jtmoland;

import android.content.Intent;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class BillMonth extends AppCompatActivity {

    Button btnReading, btnImport, btnExport;
    EditText etBillMonth;
    ArrayList<Record> records = new ArrayList<Record>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_month);

        btnReading = findViewById(R.id.btnReading);
        btnImport = findViewById(R.id.btnImport);
        btnExport = findViewById(R.id.btnExport);
        etBillMonth = findViewById(R.id.etBillMonth);


        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String fileName = Environment.getExternalStorageDirectory().getPath() + "/JTMoland/JTM" + etBillMonth.getText().toString() + ".csv";
                    Toast.makeText(BillMonth.this, fileName, Toast.LENGTH_SHORT).show();
                    BufferedReader br = new BufferedReader(new FileReader(fileName));
                    String line = "";
                    while((line=br.readLine())!=null)
                    {
                        records.add(convertToRecord(line));
                    }
                }
                catch (FileNotFoundException e) {
                    Toast.makeText(BillMonth.this, "File not found", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(records.size() > 0)
                {
                    Intent readingIntent = new Intent(BillMonth.this, Reading.class);
                    readingIntent.putParcelableArrayListExtra("records", records);
                    startActivity(readingIntent);
                }
                else
                {
                    Toast.makeText(BillMonth.this, "Select a valid bill month first.", Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    public Record convertToRecord(String line)
    {
        String arr[] = line.trim().split(",");
        if(arr.length != 11) {
            return null;
        }
        Record record = new Record(arr[0],arr[1],arr[2],arr[3],arr[4],Integer.parseInt(arr[5]),Float.parseFloat(arr[6]),Float.parseFloat(arr[7]),arr[8],arr[9],arr[10]);
        return record;
    }
}
