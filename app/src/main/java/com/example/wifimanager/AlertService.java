package com.example.wifimanager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AlertService extends Service {

    private static final String TAG = "AlertService";
    boolean isVib = true;

    WifiManager wifiManager;
    List<ScanResult> scanList;
    int maxIndex = -1;

    String[] placeName = new String[3];
    ArrayList<String> top10APId[] = new ArrayList[3];

    Timer timer = new Timer();
    TimerTask timerTask = null;

    Vibrator vib;

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
                checkProximity();
        }
    };

    // 등록 장소 근접 여부를 판단하고 그에 따라 알림을 주기 위한 메소드
    private void checkProximity() {
        scanList = wifiManager.getScanResults();
        boolean isProximate = false;
        double maxValue = 0.0;
        int[] proximity_value = new int[3];
        Arrays.fill(proximity_value, -100);
        ArrayList<String> nowAPId = new ArrayList<String>();

        String str;
        for(int i = 1; i < scanList.size(); i++) {
            ScanResult result = scanList.get(i);
            str = result.SSID + result.BSSID;
            nowAPId.add(str); // 현재 위치 정보 저장
        }
        double[] jaccard_index = new double[3];
        Arrays.fill(jaccard_index, 0.0);
        for(int i=0; i<3; i++) {
            if(top10APId[i] == null) continue; //저장되지 않은 장소 생략 과정
            int length = top10APId[i].size();
            int count = 0;
            for(int j=0; j<length; j++)
                if(nowAPId.contains(top10APId[i].get(j))) count++; //두 장소를 비교하며 교집합 갯수를 구한다.
            jaccard_index[i] = count / (double)(nowAPId.size() + top10APId[i].size() - count); // 교집합 / 합집합 (합집합은 전체 사이즈 - 교집합 사이즈)
        }
        for(int i=0;i<3; i++) //주기적으로 scan하고 있는지 확인하기 위한 출력력
            System.out.println("jaccard_index[" + i +"]:"+jaccard_index[i]);
        for(int i=0; i<3; i++){
            if(jaccard_index[i] > 0.5){
                isProximate = true; // 0.5보다 큰 값들이 존재하면 저장된 장소 근처에 있는 것으로 간주
            }
        }
        if((isProximate && isVib) || (isProximate && maxIndex == -1)) { //저장장소이면서 현재 저장된 장소가 없으면 가장 가까운 위치가 어디인지 측정
            maxValue = 0.0;
            maxIndex = -1;
            for (int i = 0; i < 3; i++) {
                if (jaccard_index[i] > maxValue) {
                    maxValue = jaccard_index[i];
                    maxIndex = i;
                }
            }
        }

        if(isProximate) { //가까운 위치가 있는 경우
            // 진동 패턴
            // 0초 후에 시작 => 바로 시작, 200ms 동안 진동, 100ms 동안 쉼, 200ms 동안 진동, 100ms 동안 쉼, 200ms 동안 진동
            long[] pattern = {0, 200, 100, 200, 100, 200};
            // pattern 변수로 지정된 방식으로 진동한다, -1: 반복 없음. 한번의 진동 패턴 수행 후 완료
            if(isVib) { //isVib 변수로 인해 지속적으로 alert가 울리지 않는다.
                vib.vibrate(pattern, -1);
                isVib = false;
                Toast.makeText(this, "** " + placeName[maxIndex] + "에 있거나 그 근처에 있습니다 **", Toast.LENGTH_SHORT).show();
            }
        } else { //가까운 위치가 없는 경우
            // 동작 확인용
            if(!isVib) { //isVib 변수로 인해 지속적으로 alert가 울리지 않는다.
                vib.vibrate(200);
                isVib = true;
                if(maxIndex != -1) Toast.makeText(this, "** " + placeName[maxIndex] + " 근처에 있지 않습니다 **", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");

        vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);

        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // intent: startService() 호출 시 넘기는 intent 객체
        // flags: service start 요청에 대한 부가 정보. 0, START_FLAG_REDELIVERY, START_FLAG_RETRY
        // startId: start 요청을 나타내는 unique integer id

        Toast.makeText(this, "AlertService 시작", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onStartCommand()");

        // 넘어온 intent에서 등록 장소 및 AP 정보 추출
        for(int i=0; i<3; i++)
            top10APId[i] = new ArrayList<String>();
        placeName = intent.getStringArrayExtra("place");
        top10APId[0] = intent.getStringArrayListExtra("ap1id");
        top10APId[1] = intent.getStringArrayListExtra("ap2id");
        top10APId[2] = intent.getStringArrayListExtra("ap3id");

        // 주기적으로 wifi scan 수행하기 위한 timer 가동
        startTimerTask();

        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        Toast.makeText(this, "AlertService 중지", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onDestroy()");

        stopTimerTask();
        unregisterReceiver(mReceiver);
    }

    private void startTimerTask() {
        // TimerTask 생성한다
        timerTask = new TimerTask() {
            @Override
            public void run() {
                wifiManager.startScan();
            }
        };

        // TimerTask를 Timer를 통해 실행시킨다
        timer.schedule(timerTask, 1000, 5000); // 1초 후에 타이머를 구동하고 10초마다 반복한다
        //*** Timer 클래스 메소드 이용법 참고 ***//
        //     schedule(TimerTask task, long delay, long period)
        // http://developer.android.com/intl/ko/reference/java/util/Timer.html
        //***********************************//
    }

    private void stopTimerTask() {
        // 1. 모든 태스크를 중단한다
        if(timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }
}