package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScanBeacon extends AppCompatActivity {
    StringBuffer buffer;
    private static final String TAG = "xxx";
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner scanner;
    Long tsLong_old = 0L;
    Long ts_add_gps =0L;
    File myExternalFile;
    String fileContent;
    private List<BeaconInfo> beaconInfoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private BeaconInfoAdapter mAdapter;
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Permission.askForPermissions(this);
        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
            });
            builder.show();
        }

        if (scanner != null) {
            startScanning();
            Log.d(TAG, "scan started");
        }  else {
            Log.e(TAG, "could not get scanner object");
        }
        setContentView(R.layout.activity_scan_beacon);
        recyclerView =findViewById(R.id.rcvBeacon);
        mAdapter = new BeaconInfoAdapter(getApplicationContext(), beaconInfoList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }
    private final ScanCallback scanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            Log.d(TAG, "Device: " + device.getName());
            Log.d(TAG, "onScanResult: "+result.getRssi());

            byte[] advertiseData = result.getScanRecord().getBytes();
            boolean valid = checkBasicDiscoverCondition(advertiseData);
            if (valid){
                parsingGps(advertiseData);
                String _s_advertise = bytesToHex(advertiseData);
                parsingData(advertiseData);
            }else
            {
                return;
            }

        }

        private void parsingData(byte[] advertiseData) {

//            tvCompany.setText("0x"+int2Hex(advertiseData[6]) + int2Hex(advertiseData[5]));
//            tvLength.setText("0x"+int2Hex(advertiseData[8]));
//            tvType.setText("0x"+int2Hex(advertiseData[7]));
            String uuid ="";
            for (int i =9;i<25;i++){
                uuid+= int2Hex(advertiseData[i]);
                if(i %4 ==0 && i<24) uuid+="-";
            }
//            tvUUID.setText(uuid);
//            tvMajor.setText("0x"+int2Hex(advertiseData[25]) + int2Hex(advertiseData[26]));
//            tvMinor.setText("0x"+int2Hex(advertiseData[27]) + int2Hex(advertiseData[28]));
//            tvLength.setText();

        }
        private String int2Hex(int i){
            i = i&0xFF;
            if(i<16) return "0"+Integer.toString(i, 16);
            return ""+Integer.toString(i, 16);
        }
        private void parsingGps(byte[] advertiseData) {
            long _lon = ((advertiseData[16]& 0xff) <<24) + ((advertiseData[15]& 0xff) <<16) + ((advertiseData[14]& 0xff) <<8) + ((advertiseData[13]& 0xff));
            float p_lon = (float) (105+((_lon*1.0/10000)-10500)/60);

            long _lat = ((advertiseData[12]& 0xff) <<24) + ((advertiseData[11]& 0xff) <<16) + ((advertiseData[10]& 0xff) <<8) + ((advertiseData[9]& 0xff));
            float p_lat = (float) (21+((_lat*1.0/10000)-2100)/60);
            if(p_lon <0 || p_lat <0) {
//                tvGpsLat.setText("0.000000");
//                tvGpsLon.setText("0.000000");
                writeFileOnInternalStorage(0,0);
            }else{
//                tvGpsLat.setText(Float.toString(p_lat));
//                tvGpsLon.setText(Float.toString(p_lon));
                writeFileOnInternalStorage(p_lat,p_lon);
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
                    Toast.makeText(ScanBeacon.this, "Text field can not be empty.", Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startScanning() {
        Log.d(TAG, "im here 111");
        String[] peripheralAddresses = new String[]{"D9:74:A1:28:59:28"}; //"D9:74:A1:28:59:28" : devkit2 , "ED:BF:36:11:3A:19",: beacon
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
//        return advertiseData[5] == 0x59 && advertiseData[6] == 0x00 && advertiseData[7] == (byte) 0x02;
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