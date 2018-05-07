package com.example.amitj.roboarm;

import java.io.IOException;

import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

public class ArduinoMain extends Activity {
    int i = 2;
    Button functionTwo, functionOne;



    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;


    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module
    public String newAddress = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino_main);

//        addKeyListener();
        SeekBar baseBar = (SeekBar)findViewById(R.id.seekBar1);
        SeekBar onbaseBar = (SeekBar)findViewById(R.id.seekBar6);
        SeekBar armBar = (SeekBar)findViewById(R.id.seekBar7);


        baseBar.setMax(120);
        onbaseBar.setMax(120);
        armBar.setMax(120);


        baseBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sendData(Integer.toString(progress) + "b");
                Log.i("baseBar", Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        onbaseBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sendData(Integer.toString(progress) + "o");
                Log.i("onbaseBar", Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        armBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sendData(Integer.toString(progress) + "a");
                Log.i("armBar", Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        functionTwo = (Button) findViewById(R.id.functionTwo);
        functionOne = (Button) findViewById(R.id.reset);


        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();


        functionOne.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
               sendData("reset");

                Toast.makeText(getBaseContext(), "reset", Toast.LENGTH_SHORT).show();
            }
        });


        functionTwo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(i % 2 == 0){
                    sendData("2");
                    i = 3;
                    functionTwo.setText("Disengage");
                }
                else if(i % 2 != 0){
                    sendData("1");
                    i = 2;
                    functionTwo.setText("Engage");
                }
                //Toast.makeText(getBaseContext(), "Down", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();



        Intent intent = getIntent();
        newAddress = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);


        BluetoothDevice device = btAdapter.getRemoteDevice(newAddress);


        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e1) {
            Toast.makeText(getBaseContext(), "ERROR - Could not create Bluetooth socket", Toast.LENGTH_SHORT).show();
        }

        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                Toast.makeText(getBaseContext(), "ERROR - Could not close Bluetooth socket", Toast.LENGTH_SHORT).show();
            }
        }

        // Create a data stream so we can connect to the device
        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "ERROR - Could not create bluetooth outstream", Toast.LENGTH_SHORT).show();
        }
        sendData("X");
    }

    @Override
    public void onPause() {
        super.onPause();

        //Close BT socket to device
        try     {
            btSocket.close();
        } catch (IOException e2) {
            Toast.makeText(getBaseContext(), "ERROR - Failed to close Bluetooth socket", Toast.LENGTH_SHORT).show();
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }


    private void checkBTState() {
        // Check device has Bluetooth and that it is turned on
        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "ERROR - Device does not support bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    // Method to send data
    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "ERROR - Device not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    /*public void addKeyListener() {

        // get edittext component
        editText = (EditText) findViewById(R.id.editText1);

        // add a keylistener to keep track user input
        editText.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                // if keydown and send is pressed implement the sendData method
                if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                    //I have put the * in automatically so it is no longer needed when entering text
                    sendData('*' + editText.getText().toString());
                    Toast.makeText(getBaseContext(), "Sending text", Toast.LENGTH_SHORT).show();

                    return true;
                }

                return false;
            }
        });
    }*/

}