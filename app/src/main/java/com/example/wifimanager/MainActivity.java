package com.example.wifimanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    boolean isPermitted = false;
    boolean isWifiScan = false;
    boolean doneWifiScan = true;
    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    TextView ap1;
    TextView ap2;
    TextView ap3;
    ArrayList<String> top10APId[];

    WifiManager wifiManager;
    List<ScanResult> scanResultList;

    private Spinner placeName;
    private String temp_str;
    private String[] str_placeName;
    ArrayList<String[]> arrayList;
    ArrayAdapter<String> arrayAdapter;

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
                getWifiInfo();
        }
    };

    String[] dataset;

    private void getWifiInfo() {
        if (!doneWifiScan) { // wifiScan을 한 경우에만 getScanResult를 사용하도록 flag 변수 구현
//            int num = -1;
//            // num에 선택된 spinner를 체크해서 넣어준다.
//            if (temp_str.equals("AP1")) num = 0;
//            else if (temp_str.equals("AP2")) num = 1;
//            else num = 2;
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            scanResultList = wifiManager.getScanResults();

            ScanResult result = scanResultList.get(0);
            dataset[0] = result.SSID;
            dataset[1] = result.BSSID;
            dataset[2] = String.valueOf(result.level);


//            String str;
//            //중복되지 않게 해당 num번째 배열에 AP정보들을 집어넣는다.
//            for (int i = 1; i < scanResultList.size(); i++) {
//                ScanResult result = scanResultList.get(i);
//                // 화면의 TextView에 SSID와 BSSID를 이어붙여서 텍스트로 표시
//                //Log.d("RESULT", "RESULT OK");
//                //Log.d("RESULT", result.BSSID);
//                str = "SSID : " + result.SSID + ", BSSID : " + result.BSSID + "\n RSSI : " + result.level;
//                if(!top10APId[num].contains(str)) top10APId[num].add(str);
//            }
//
            //모두 처리되고 나면 가장 처음 들어온 AP의 SSID+BSSID를 text로 뿌려준다.
//            if (num == 0) {
//                ap1.setText(top10APId[num].get(0));
//            } else if (num == 1) {
//                ap2.setText(top10APId[num].get(0));
//            } else {
//                ap3.setText(top10APId[num].get(0));
//            }
            doneWifiScan = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editText = findViewById(R.id.placename_et);
        editText.getText().toString();
        dataset[4] = String.valueOf(editText);

        recyclerView = findViewById(R.id.wifi_sensor_rv);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new WifiRVAdapter(arrayList);
        recyclerView.setAdapter(mAdapter);

        //초기 셋팅 과정
        requestRuntimePermission();
//        temp_str = "temp_str";
//        str_placeName = new String[3];
//        top10APId = new ArrayList[3];
//        for(int i=0; i<3; i++)
//            top10APId[i] = new ArrayList<String>();

//        ap1 = (TextView)findViewById(R.id.ap1);
//        ap2 = (TextView)findViewById(R.id.ap2);
//        ap3 = (TextView)findViewById(R.id.ap3);
//        placeName = (Spinner)findViewById(R.id.placeName);

//        arrayList = new ArrayList<>();
//        arrayList.add("AP1");
//        arrayList.add("AP2");
//        arrayList.add("AP3");

        //Spinner 연결 설정
//        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrayList);
//        placeName.setAdapter(arrayAdapter);
//        placeName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                str_placeName[i] = arrayList.get(i);
//                temp_str = str_placeName[i];
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        placeName.setSelection(0);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        if(wifiManager.isWifiEnabled() == false)
            wifiManager.setWifiEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // wifi scan 결과 수신을 위한 BroadcastReceiver 등록
        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // wifi scan 결과 수신용 BroadcastReceiver 등록 해제
        unregisterReceiver(mReceiver);
    }

    public void onClick(View view) {
        if(view.getId() == R.id.start) {
            // Start wifi scan 버튼이 눌렸을 때
            doneWifiScan = false;
            Toast.makeText(this, "WiFi scan start!!", Toast.LENGTH_LONG).show();

            if(isPermitted) {
                // wifi 스캔 시작
                wifiManager.startScan();
                isWifiScan = true;
                arrayList.add(dataset);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Location access 권한이 없습니다..", Toast.LENGTH_LONG).show();
            }
        }
//        else if(view.getId() == R.id.startAlert) {
//            // Start proximity alert 버튼이 눌렸을 때
//
//            // placeName이라는 변수로 참조하는 EditText에 쓰여진 장소 이름으로 proximity alert을 등록한다
//
//            // proximity alert을 주는 것은 Service로 구현
//            // Service를 AlertService라는 이름의 클래스로 구현하고 startService 메소드를 호출하여 이 Service를 시작
//
//            if(str_placeName.equals("")) { //Spinner 선택 했는지 검사
//                Toast.makeText(this, "Please select place name first.", Toast.LENGTH_LONG).show();
//            }
//            else if(!isWifiScan){ //WIfiScan 했는지 검사
//                Toast.makeText(this, "Wifi-Scan first!!", Toast.LENGTH_LONG).show();
//            }
//            else { // 결과값들을 Service로 보낸다.
//                Intent intent = new Intent(this, AlertService.class);
//                intent.putExtra("place", str_placeName);
//                intent.putStringArrayListExtra("ap1id", top10APId[0]);
//                intent.putStringArrayListExtra("ap2id", top10APId[1]);
//                intent.putStringArrayListExtra("ap3id", top10APId[2]);
//                // 위에서 key 값으로 쓰인 String 값은 여러 곳에서 반복해서 사용된다면
//                // String 상수로 정의해 놓고 사용하는 것이 좋음
//                // 이 예제에서는 AlertService에서 쓰임
//
//                startService(intent);
//            }
//        }
//        else if(view.getId() == R.id.stopAlert) {
//            // Stop proximity alert 버튼이 눌렸을 때
//
//            // AlertService 동작을 중단
//            stopService(new Intent(this, AlertService.class));
//        }
    }

    private void requestRuntimePermission() {
        //*******************************************************************
        // Runtime permission check
        //*******************************************************************
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            // ACCESS_FINE_LOCATION 권한이 있는 것
            isPermitted = true;
        }
        //*********************************************************************
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // read_external_storage-related task you need to do.

                    // ACCESS_FINE_LOCATION 권한을 얻음
                    isPermitted = true;

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    // 권한을 얻지 못 하였으므로 location 요청 작업을 수행할 수 없다
                    // 적절히 대처한다
                    isPermitted = false;

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}