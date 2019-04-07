package com.lorrea02.jtmoland;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Reading extends AppCompatActivity {
    TextView tvName, tvAddress, tvSubd, tvMeter, tvAccNum, tvPrevious, tvUnpaid, tvConsumption, tvAmtDue;
    Button btnPrev, btnNext, btnSearch, btnProcess, btnPrint;
    EditText etPresent;
    ArrayList<Record> records = new ArrayList<Record>();
    Record currentSelected;
    int current = 0;
    int i = 0;
    Messenger mMessenger = null;
    boolean isBind = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        tvName = findViewById(R.id.tvName);
        tvAddress =findViewById(R.id.tvAddress);
        tvSubd = findViewById(R.id.tvSubd);
        tvMeter = findViewById(R.id.tvMeter);
        tvAccNum = findViewById(R.id.tvAccNum);
        tvPrevious = findViewById(R.id.tvPrevious);
        tvUnpaid = findViewById(R.id.tvUnpaid);
        tvConsumption = findViewById(R.id.tvConsumption);
        tvAmtDue = findViewById(R.id.tvAmtDue);
        btnPrev  = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnSearch = findViewById(R.id.btnSearch);
        btnProcess = findViewById(R.id.btnProcess);
        btnPrint = findViewById(R.id.btnPrint);
        etPresent = findViewById(R.id.etPresent);

        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        records = b.getParcelableArrayList("records");
        currentSelected = records.get(i);
        bindService();

        refreshData();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i != records.size()-1)
                {
                    int target = i;
                    do{
                        if(target + 1 >= records.size())
                            break;
                        target++;
                    }while(records.get(target) == null);
                    if(records.get(target) != null) {
                        i = target;
                        currentSelected = records.get(target);
                        refreshData();
                    }
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i != 0)
                {
                    int target = i;
                    do{
                        if(target - 1 < 0)
                            break;
                        target--;
                    }while(records.get(target) == null);
                    if(records.get(target) != null) {
                        i = target;
                        currentSelected = records.get(target);
                        refreshData();
                    }
                }
            }
        });

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etPresent.getText().toString().trim().equals(""))
                {
                    current = Integer.parseInt(etPresent.getText().toString());
                }

                if(current < currentSelected.getPrevious())
                {
                    Toast.makeText(Reading.this, "Please enter correct present reading", Toast.LENGTH_LONG).show();
                }
                else
                {
                    int consumption = current - currentSelected.getPrevious();
                    tvConsumption.setText("" + consumption);
                    tvAmtDue.setText("" + (ComputeAmtDue(consumption) + currentSelected.getUnpaid() + currentSelected.getCharges()));
                }

            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
                String dateNow = formatter.format(today);
                String headerTxt = centerTxt("JT Moland Realty") + "\n" +
                        centerTxt("Development Corporation") + "\n\n" +
                        centerTxt("STATEMENT OF ACCOUNT") + "\n\n" +
                        centerTxt("Water Billing") + "\n" +
                        "________________________________" + "\n\n" +
                        "" + currentSelected.getName() + "\n" +
                        "Meter Number: " + currentSelected.getMeterNumber() + "\n" +
                        printAdd("Address: " + currentSelected.getAddress()) + "\n" +
                        printAdd("Period Covered: " + currentSelected.getStartDate()) + "-" + currentSelected.getEndDate()  +"\n" +
                        printAdd("Due Date: " + currentSelected.getDueDate()) + "\n" +
                        "________________________________" + "\n\n";

                String ballot = ("Present Reading: ") + current + "\n"
                        + "Previous Reading: " + currentSelected.getPrevious() + "\n"
                        + "Consumption in cu. m: " + (current - currentSelected.getPrevious()) + "\n\n"
                        + "Balance from last: " + currentSelected.getUnpaid() + "\n"
                        + "Current charge: " + ComputeAmtDue(current - currentSelected.getPrevious()) + "\n"
                        + "Other charges: " + currentSelected.getCharges() + "\n"
                        + "Total charges: " + (ComputeAmtDue(current - currentSelected.getPrevious()) + currentSelected.getUnpaid() + currentSelected.getCharges()) + "\n"
                        + "\n\n"
                        + "Note: If payment has been made,\n"
                        + "please disregard this notice. \n"
                        + "Thank you\n";

                String footerTxt = centerTxt("________________________________") + "\n Updated last: " + dateNow + "\n\n\n\n";

                if (isBind) {
                    ArrayList message = new ArrayList<>();
                    message.add("" + headerTxt + ballot + footerTxt);
                    Message msg = Message.obtain();
                    msg.obj = message;
                    try {
                        mMessenger.send(msg);
                        Toast.makeText(Reading.this, "Printing...", Toast.LENGTH_LONG).show();
                        refreshData();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Reading.this, "Bind muna", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessenger = new Messenger(service);
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMessenger = null;
            isBind = false;
        }
    };

    @Override
    protected void onStop() {
        unbindService(mConnection);
        isBind = false;
        mMessenger = null;
        super.onStop();
    }

    public void bindService() {
        Intent intent = new Intent(Reading.this, MyService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public String centerTxt(String txt) {

        if (txt.length() < 32) {
            int spaces = 32 - txt.length();
            int firstHalf = spaces / 2;
            int secondHalf = spaces - firstHalf;
            for (int i = 0; i < firstHalf; i++) {
                txt = " " + txt;
            }
            for (int i = 0; i < secondHalf; i++) {
                txt = txt + " ";
            }
        }
        return txt;
    }

    public String printAdd(String txt) {
        Log.d("address", txt);
        if (txt.length() > 32) {
            int rows = txt.length() / 32;
            if(txt.length() % 32 != 0)
                rows++;
            String ret = "";

            for (int i = 0, ind = 0; i < rows; i++, ind += 32) {
                if (ind + 32 > txt.length())
                    ret += txt.substring(ind, txt.length()) + "\n";
                else
                    ret += txt.substring(ind, ind + 32) + "\n";
                Log.d("ret", ret);
            }
            return ret;
        } else
            return txt;

        //aerrol
        //0,4
        //
    }

    public void refreshData()
    {
        tvName.setText(currentSelected.getName());
        tvAddress.setText(currentSelected.getAddress());
        tvSubd.setText(currentSelected.getSubd());
        tvMeter.setText("Meter Number : " + currentSelected.getMeterNumber());
        tvAccNum.setText("Account Number : " + currentSelected.getAccountNum());
        tvPrevious.setText("Previous Reading : " + currentSelected.getPrevious());
        tvUnpaid.setText("Unpaid Balance : " + currentSelected.getUnpaid());
    }

    public float ComputeAmtDue(int consumption)
    {
        if(consumption == 0)
        {
            return 0;
        }
        else if(consumption <= 10)
            return 150.0f;
        else if (consumption > 10 && consumption <= 20)
        {
            int temp = consumption - 10;
            return 150.0f + (temp * 17);
        }else
        {
            int temp = consumption - 20;
            return 320.0f + (temp * 19.26f);
        }
    }
}
