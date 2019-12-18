package kr.ac.ssu.edugochi.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import kr.ac.ssu.edugochi.IMyTimerService;

public class TimerService extends Service {
    private static final String TAG = TimerService.class.getSimpleName();

    private int status = init;
    private final static int init = 0;  // 정지
    private final static int run = 1;   // 측정 중
    private final static int pause = 2; // 일시정지

    private long count;

    public TimerService() {
    }


    public void setStatus(int status) {
        this.status = status;
    }

    public long getCount() {
        return count;
    }

    public class LocalBinder extends Binder {
        public TimerService getTimerService() {
            return TimerService.this;
        }
    }

    private final Binder binder = new LocalBinder();


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();
        status = run;
        Thread counter = new Thread(new Counter());
        counter.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand()");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        status = init;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        status = init;
        return super.onUnbind(intent);
    }

    private class Counter implements Runnable {

        //private long count;
        private Handler handler = new Handler();

        @Override
        public void run() {
            while (true) {
                switch (status) {
                    case init:
                    case pause:
                        break;
                    case run:
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(status != run) break;
                        count++;

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "count: " + count);
                            }
                        });
                        break;
                }
            }
        }
    }
}
