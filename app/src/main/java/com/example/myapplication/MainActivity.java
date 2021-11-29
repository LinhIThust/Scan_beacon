package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "xxx";
    BluetoothLeScanner scanner;
    TextView tvMac, tvGpsLat, tvGpsLon, tvRaw;
    Long tsLong_old = 0L;
    Long ts_add_gps =0L;
    File myExternalFile;
    String fileContent;
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMac =findViewById(R.id.tvMac);
        tvGpsLat =findViewById(R.id.tvLat);
        tvGpsLon =findViewById(R.id.tvLon);
        tvRaw =findViewById(R.id.tvRaw);
        Permission.askForPermissions(this);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        scanner= adapter.getBluetoothLeScanner();
        if (scanner != null) {
            startScanning();
            Log.d(TAG, "scan started");
        }  else {
            Log.e(TAG, "could not get scanner object");
        }

    }
    private final ScanCallback scanCallback = new ScanCallback() {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        BluetoothDevice device = result.getDevice();
        Log.d(TAG, "Device: " + device.getName());
        tvMac.setText(device.getAddress());

        byte[] advertiseData = result.getScanRecord().getBytes();
        boolean valid = checkBasicDiscoverCondition(advertiseData);
        if (valid){
            tvRaw.setText(bytesToHex(advertiseData));
            parsingGps(advertiseData);
        }else
        {
            return;
        }

    }

        private void parsingGps(byte[] advertiseData) {
            long _lon = ((advertiseData[16]& 0xff) <<24) + ((advertiseData[15]& 0xff) <<16) + ((advertiseData[14]& 0xff) <<8) + ((advertiseData[13]& 0xff));
            float p_lon = (float) (105+((_lon*1.0/10000)-10500)/60);

            long _lat = ((advertiseData[12]& 0xff) <<24) + ((advertiseData[11]& 0xff) <<16) + ((advertiseData[10]& 0xff) <<8) + ((advertiseData[9]& 0xff));
            float p_lat = (float) (21+((_lat*1.0/10000)-2100)/60);
            if(p_lon <0 || p_lat <0) {
                tvGpsLat.setText("0.000000");
                tvGpsLon.setText("0.000000");
                writeFileOnInternalStorage(p_lat,p_lon);
            }else{
                tvGpsLat.setText(Float.toString(p_lat));
                tvGpsLon.setText(Float.toString(p_lon));
                writeFileOnInternalStorage(p_lat,p_lon);
                writeFileOnInternalStorage2(p_lat,p_lon);
            }
        }

        private void writeFileOnInternalStorage2(float p_lat, float p_lon) {
            try {
                FileWriter fout = new FileWriter("filename.txt", true);
                fout.append("Hello"+p_lat);
                fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void writeFileOnInternalStorage(float p_lat, float p_lon){
            Long tsLong = System.currentTimeMillis()/1000;
            Date cDate = new Date();
            if(tsLong -ts_add_gps >5){
                fileContent += new SimpleDateFormat("hhmmss").format(cDate)+","+p_lat+","+p_lon+"\n";
                ts_add_gps =tsLong;
            }

            if(tsLong -tsLong_old >300){
                myExternalFile = new File(getExternalFilesDir("gps"), new SimpleDateFormat("hhmmss").format(cDate).concat(".csv"));
                tsLong_old =tsLong;
                if(isStoragePermissionGranted()) {
                    FileOutputStream fos = null;
                    try {
                        // Instantiate the FileOutputStream object and pass myExternalFile in constructor
                        fos = new FileOutputStream(myExternalFile);
                        // Write to the file
                        fos.write(fileContent.getBytes());
                        // Close the stream
                        fos.close();
                        fileContent ="";
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Show a Toast message to inform the user that the operation has been successfully completed.
//                Toast.makeText(MainActivity.this, "Information saved to SD card.", Toast.LENGTH_SHORT).show();
                } else {
                    // If the Text field is empty show corresponding Toast message
                    Toast.makeText(MainActivity.this, "Text field can not be empty.", Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

    private void startScanning() {
        Log.d(TAG, "im here 111");
        String[] peripheralAddresses = new String[]{"D9:74:A1:28:59:28"};
        // Build filters list
        List<ScanFilter> filters = null;
        if (peripheralAddresses != null) {
            filters = new ArrayList<>();
            for (String address : peripheralAddresses) {
                ScanFilter filter = new ScanFilter.Builder()
                        .setDeviceAddress(address)
                        .build();
                filters.add(filter);
            }
        }
        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        scanner.startScan(filters, settings, scanCallback);
    }
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    private boolean checkBasicDiscoverCondition(byte[] advertiseData) {
        return true;
    }
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //Permission is granted
                return true;
            } else {
                //Permission is revoked
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            //permission is automatically granted on sdk<23 upon installation
            //Permission is granted
            return true;
        }
    }
}