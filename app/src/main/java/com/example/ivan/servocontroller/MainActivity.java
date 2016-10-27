package com.example.ivan.servocontroller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends Activity {

    int x, servo1;
    Button ledbutton, servobutton, connectbutton, servoleft, servoright, servodown, servoup, servolift, servolower;
    EditText servovalue;
    Spinner DeviceSelection;
    TextView text1;

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice ArduinoBoard;
    BluetoothSocket BTsocket;
    boolean stopWorker = false;
    byte[] readBuffer = new byte[1024];
    private final static int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private OutputStream outStream = null;
    private InputStream inStream = null;
    Handler handler = new Handler();
    String datatosend, Reading, Compile, Reading2;
    String[] DeviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        servo1=0;

        //Widget Declaration
        ledbutton = (Button) findViewById(R.id.ledbutton);
        servobutton = (Button) findViewById(R.id.servobutton);
        servovalue = (EditText) findViewById(R.id.servovalue);
        DeviceSelection = (Spinner) findViewById(R.id.DeviceSelection);
        connectbutton = (Button) findViewById(R.id.connectbutton);
        servoleft = (Button)findViewById(R.id.servoleft);
        servoright = (Button)findViewById(R.id.servoright);
        servodown = (Button)findViewById(R.id.servodown);
        servoup = (Button)findViewById(R.id.servoup);
        servolift = (Button)findViewById(R.id.servolift);
        servolower = (Button)findViewById(R.id.servolower);
        text1 = (TextView)findViewById(R.id.text1);

        ArrayList<String> ScannedItems;
        ScannedItems = new ArrayList<String>();
        ArrayAdapter<String> aa;
        aa = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                ScannedItems);
        DeviceSelection.setAdapter(aa);

        //Bluetooth Declaration
        DeviceAddress = new String[15];


        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //QUERY FOR PAIRED DEVICES
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            x = 0;

            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                ScannedItems.add(x, device.getName().toString());
                DeviceAddress[x] = device.getAddress().toString();
                x = x + 1;
                aa.notifyDataSetChanged();
            }
        }

        //SPINNER SELECTION
        DeviceSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArduinoBoard = mBluetoothAdapter.getRemoteDevice(DeviceAddress[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //CONNECTION BUTTON
        connectbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectThread(ArduinoBoard);
            }
        });

        //Servo specific
        servobutton.setOnClickListener(new View.OnClickListener() {
            private String servoposition;
            @Override
            public void onClick(View v) {
                servoposition= "a" + servovalue.getText().toString();
                servo1 = Integer.parseInt(servovalue.getText().toString());
                writeData(servoposition);
                text1.setText(servoposition);
            }
        });

        //DATA SENDING BUTTON
        ledbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datatosend = "3";
                writeData(datatosend);
            }

        });



        //ORIGINAL CODE (10/9/15)
        /*
        servoright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datatosend = "r";
                writeData(datatosend);
            }
        });
        */

        /*
        servoleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datatosend = "l";
                writeData(datatosend);
            }
        });

        servoup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datatosend = "d";
                writeData(datatosend);
            }
        });

        servodown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datatosend = "u";
                writeData(datatosend);
            }
        });
        */



        //SERVO RIGHT
        servoright.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    datatosend = "1";
                    writeData(datatosend);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    datatosend = "8";
                    writeData(datatosend);
                }

                return (true);
            }
        });

        servoleft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    datatosend = "2";
                    writeData(datatosend);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    datatosend = "8";
                    writeData(datatosend);
                }

                return (true);
            }
        });

        servoup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    datatosend = "3";
                    writeData(datatosend);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    datatosend = "8";
                    writeData(datatosend);
                }

                return (true);
            }
        });

        servodown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    datatosend = "4";
                    writeData(datatosend);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    datatosend = "8";
                    writeData(datatosend);
                }

                return (true);
            }
        });

        servolift.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    datatosend = "5";
                    writeData(datatosend);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    datatosend = "8";
                    writeData(datatosend);
                }

                return (true);
            }
        });

        servolower.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    datatosend = "6";
                    writeData(datatosend);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    datatosend = "8";
                    writeData(datatosend);
                }

                return (true);
            }
        });


    }

    //CONNECTING TO BT DEVICE
    private void ConnectThread(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            BTsocket = device.createRfcommSocketToServiceRecord(MY_UUID);

        } catch (IOException e) { Toast.makeText(getApplicationContext(), "Device Failed To Connect", Toast.LENGTH_LONG).show(); }


        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            BTsocket.connect();
            Toast.makeText(getApplicationContext(),"Device Connected",Toast.LENGTH_LONG).show();

            //CALL DATA LISTENER THREAD
            //beginListenForData();

        } catch (IOException connectException) {Toast.makeText(getApplicationContext(),"Device Failed To Connect",Toast.LENGTH_LONG).show();
            // Unable to connect; close the socket and get out
            try {
                BTsocket.close();
                Toast.makeText(getApplicationContext(),"Device Failed To Connect",Toast.LENGTH_LONG).show();
            } catch (IOException closeException) { Toast.makeText(getApplicationContext(),"Device Failed To Connect",Toast.LENGTH_LONG).show();}

        }

    }

    private void writeData(String data) {
        try {
            outStream = BTsocket.getOutputStream();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Bug BEFORE sending data",Toast.LENGTH_LONG).show();
        }

        String message = data;
        byte[] msgBuffer = message.getBytes();

        try {
            outStream.write(msgBuffer);
            Toast.makeText(getApplicationContext(),"Data Sent",Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Bug AFTER sending data",Toast.LENGTH_LONG).show();
        }
    }


    }

