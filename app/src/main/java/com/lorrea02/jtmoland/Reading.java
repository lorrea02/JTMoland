package com.lorrea02.jtmoland;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Reading extends AppCompatActivity {
    TextView tvName, tvAddress, tvSubd, tvMeter, tvAccNum, tvPrevious, tvUnpaid, tvConsumption, tvAmtDue;
    Button btnPrev, btnNext, btnSearch, btnProcess, btnPrint;
    EditText etPresent;
    ArrayList<Record> records = new ArrayList<Record>();
    Record currentSelected;
    int current = 0;
    int i = 0;
    int consumption = 0;
    String billMonth = "";
    Messenger mMessenger = null;
    boolean isBind = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);


        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvSubd = findViewById(R.id.tvSubd);
        tvMeter = findViewById(R.id.tvMeter);
        tvAccNum = findViewById(R.id.tvAccNum);
        tvPrevious = findViewById(R.id.tvPrevious);
        tvUnpaid = findViewById(R.id.tvUnpaid);
        tvConsumption = findViewById(R.id.tvConsumption);
        tvAmtDue = findViewById(R.id.tvAmtDue);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnSearch = findViewById(R.id.btnSearch);
        btnProcess = findViewById(R.id.btnProcess);
        btnPrint = findViewById(R.id.btnPrint);
        etPresent = findViewById(R.id.etPresent);

        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        records = b.getParcelableArrayList("records");
        billMonth = b.getString("billMonth");
        currentSelected = records.get(i);
        bindService();

        refreshData();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i != records.size() - 1) {
                    int target = i;
                    do {
                        if (target + 1 >= records.size())
                            break;
                        target++;
                    } while (records.get(target) == null);
                    if (records.get(target) != null) {
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
                if (i != 0) {
                    int target = i;
                    do {
                        if (target - 1 < 0)
                            break;
                        target--;
                    } while (records.get(target) == null);
                    if (records.get(target) != null) {
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
                if (!etPresent.getText().toString().trim().equals("")) {
                    current = Integer.parseInt(etPresent.getText().toString());
                }

                if (current < currentSelected.getPrevious()) {
                    Toast.makeText(Reading.this, "Please enter correct present reading", Toast.LENGTH_LONG).show();
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(Reading.this);
                    builder.setCancelable(true);
                    builder.setTitle("JTMoland");
                    builder.setMessage("Confirm processing?");
                    builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    consumption = current - currentSelected.getPrevious();
                                    float charges = 0f;
                                    if (consumption == 0) {
                                        charges = 0;
                                    } else {
                                        charges = currentSelected.getCharges();
                                    }

                                    tvConsumption.setText("" + consumption);
                                    float amt = (ComputeAmtDue(consumption) + currentSelected.getUnpaid() + charges);
                                    tvAmtDue.setText("" + amt);

                                    currentSelected.setPresent(Integer.parseInt(etPresent.getText().toString().trim()));
                                    currentSelected.setAmtDue(amt);
                                    currentSelected.setRead(1);

                                    float water = (ComputeAmtDue(current - currentSelected.getPrevious()) + currentSelected.getUnpaid() + charges);
                                    float monthly = currentSelected.getMonthlyBalance() + currentSelected.getMonthlyCharge();
                                    currentSelected.setNetAmt(water + monthly);



                                    records.set(i, currentSelected);

                                    String text = "";
                                    for (int i = 0; i < records.size(); i++) {
                                        text += records.get(i).toString() + "\n";
                                    }
                                    text = text.trim();

                                    File dir = new File(Environment.getExternalStorageDirectory(), "JTMoland");
                                    File dir2 = new File(dir, "Export");
                                    String export = "export_" + billMonth + ".csv";
                                    File file = new File(dir2, export);
                                    try {
                                        FileWriter f2 = new FileWriter(file, false);
                                        f2.write(text);
                                        f2.close();
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Reading.this);
                builder.setTitle("Search via Meter Number:");

                // Set up the input
                final EditText input = new EditText(Reading.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        boolean found = false;
                        for (int j = 0; j < records.size(); j++) {
                            if (records.get(j).getMeterNumber().equalsIgnoreCase(m_Text.trim())) {
                                currentSelected = records.get(j);
                                i = j;
                                refreshData();
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            Toast.makeText(Reading.this, "Meter Number not found!", Toast.LENGTH_LONG).show();
                        } else {
                            dialog.dismiss();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvAmtDue.getText().toString().trim().equals("")) {
                    Toast.makeText(Reading.this, "Please process the record first.", Toast.LENGTH_LONG).show();
                } else {
                    float charges = 0f;
                    Date today = Calendar.getInstance().getTime();
                    SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.US);
                    String dateNow = formatter.format(today);
                    String judith = ConvertToReadable(currentSelected.getDueDate());
                    String headerTxt = centerTxt(dateNow) + "\n\n" +
                            centerTxt("JT Moland Realty") + "\n" +
                            centerTxt("Development Corporation") + "\n\n" +
                            centerTxt("STATEMENT OF ACCOUNT") + "\n\n" +
                            centerTxt("________________________________") + "\n\n" +
                            "" + currentSelected.getName() + "\n" +
                            "Meter Number: " + currentSelected.getMeterNumber() + "\n" +
                            printAdd("Address: " + currentSelected.getAddress()) + "\n" +
                            printAdd("Subdivision: " + currentSelected.getSubd()) + "\n" +
                            printAdd("Period Covered: " + currentSelected.getStartDate()) + "-" + currentSelected.getEndDate() + "\n" +
                            printAdd("Due Date: " + judith) + "\n\n" +
                            centerTxt("WATER BILLING") + "\n" +
                            centerTxt("________________________________") + "\n\n";

                    if (current == 0 && etPresent.getText().toString().trim().length() > 0) {
                        current = Integer.parseInt(etPresent.getText().toString().trim());
                    }

                    if (consumption <= 0) {
                        charges = 0;
                    } else {
                        charges = currentSelected.getCharges();
                    }

                    String ballot = ("Present Reading: ") + current + "\n"
                            + "Previous Reading: " + currentSelected.getPrevious() + "\n"
                            + "Consumption in cu. m: " + (current - currentSelected.getPrevious()) + "\n\n"
                            + "Balance from last: " + currentSelected.getUnpaid() + "\n"
                            + "Current charge: " + ComputeAmtDue(current - currentSelected.getPrevious()) + "\n"
                            + "Other charges: " + charges + "\n"
                            + "Total charges: " + (ComputeAmtDue(current - currentSelected.getPrevious()) + currentSelected.getUnpaid() + charges) + "\n\n";
                    if (!currentSelected.getWaterRemarks().trim().equals(""))
                        ballot += "NOTE: " + currentSelected.getWaterRemarks().toUpperCase() + "\n\n";
                    ballot += centerTxt("MONTHLY DUES") + "\n"
                            + centerTxt("________________________________") + "\n\n"
                            + "Balance from last: Php " + currentSelected.getMonthlyBalance() + "\n"
                            + "Current Charge: Php " + currentSelected.getMonthlyCharge() + "\n"
                            + "Total Charge: Php " + (currentSelected.getMonthlyCharge() + currentSelected.getMonthlyBalance()) + "\n\n";
                    if (!currentSelected.getMonthlyRemarks().trim().equals(""))
                        ballot += "NOTE: " + currentSelected.getMonthlyRemarks().toUpperCase() + "\n\n";
                    ballot += centerTxt("Note: If payment has been made,") + "\n"
                            + centerTxt("Please disregard this notice.") + "\n\n"
                            + centerTxt("THANK YOU") + "\n";

                    String footerTxt = centerTxt("Updated last: " + ConvertToReadable(currentSelected.getLastUpdate()) + "\n\n\n\n");

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

        if (txt.length() < 48) {
            int spaces = 48 - txt.length();
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
        if (txt.length() > 48) {
            int rows = txt.length() / 48;
            if (txt.length() % 48 != 0)
                rows++;
            String ret = "";

            for (int i = 0, ind = 0; i < rows; i++, ind += 48) {
                if (ind + 48 > txt.length())
                    ret += txt.substring(ind, txt.length()) + "\n";
                else
                    ret += txt.substring(ind, ind + 48) + "\n";
                Log.d("ret", ret);
            }
            return ret;
        } else
            return txt;

        //aerrol
        //0,4
        //
    }

    public void refreshData() {
        tvName.setText(currentSelected.getName().toUpperCase());
        tvAddress.setText(currentSelected.getAddress());
        tvSubd.setText(currentSelected.getSubd());
        tvMeter.setText("Meter Number : " + currentSelected.getMeterNumber());
        tvAccNum.setText("Account Number : " + currentSelected.getAccountNum());
        tvPrevious.setText("Previous Reading : " + currentSelected.getPrevious());
        tvUnpaid.setText("Unpaid Balance : " + currentSelected.getUnpaid());
        if (currentSelected.getRead() == 1) {
            etPresent.setText(currentSelected.getPresent() + "");
            int consump = currentSelected.getPresent() - currentSelected.getPrevious();
            tvConsumption.setText("" + consump);
            tvAmtDue.setText(currentSelected.getAmtDue() + "");
            etPresent.setEnabled(false);
            btnProcess.setEnabled(false);

            AlertDialog.Builder builder = new AlertDialog.Builder(Reading.this);
            builder.setTitle("Meter Number already read!");
            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.setIcon(R.drawable.iconmsg);

            builder.show();
        } else {
            etPresent.setEnabled(true);
            btnProcess.setEnabled(true);
            etPresent.setText("");
            tvConsumption.setText("");
            tvAmtDue.setText("");
        }
    }

    public float ComputeAmtDue(int consumption) {
        if (consumption == 0) {
            return 0;
        } else if (consumption <= 10)
            return 150.0f;
        else if (consumption > 10 && consumption <= 20) {
            int temp = consumption - 10;
            return 150.0f + (temp * 17);
        } else {
            int temp = consumption - 20;
            return 320.0f + (temp * 19.26f);
        }
    }

    public String ConvertToReadable(String dateInString) {
        String readable = "";
        String day = dateInString.substring(6, 8);
        String year = dateInString.substring(0, 4);
        String month = dateInString.substring(4, 6);
        String tempMonth = "";
        if (month.equals("01")) {
            tempMonth = "January";
        } else if (month.equals("02")) {
            tempMonth = "February";
        } else if (month.equals("03")) {
            tempMonth = "March";
        } else if (month.equals("04")) {
            tempMonth = "April";
        } else if (month.equals("05")) {
            tempMonth = "May";
        } else if (month.equals("06")) {
            tempMonth = "June";
        } else if (month.equals("07")) {
            tempMonth = "July";
        } else if (month.equals("08")) {
            tempMonth = "August";
        } else if (month.equals("09")) {
            tempMonth = "September";
        } else if (month.equals("10")) {
            tempMonth = "October";
        } else if (month.equals("11")) {
            tempMonth = "November";
        } else if (month.equals("12")) {
            tempMonth = "December";
        }

        readable = tempMonth + " " + day + ", " + year;

        return readable;
    }
}