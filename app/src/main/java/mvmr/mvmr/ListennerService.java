package mvmr.mvmr;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
public class ListennerService extends Service {
    public ListennerService() {
    }

    protected DatabaseReference mDatabase;
    protected FirebaseAuth mAuth;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private ScreenOnReceiver on;
    private ScreenOffReceiver off;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                FirebaseApp.initializeApp(ListennerService.this);

                on = new ScreenOnReceiver();
                off = new ScreenOffReceiver();

                IntentFilter onfilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
                IntentFilter offfilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
                ListennerService.this.registerReceiver(on, onfilter);
                ListennerService.this.registerReceiver(off, offfilter);

                mAuth = FirebaseAuth.getInstance();
                mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            Toast.makeText(ListennerService.this, "login success", Toast.LENGTH_SHORT).show();
                        }
                        else{
//                            Toast.makeText(ListennerService.this, "login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (Exception e) {
//                Toast.makeText(ListennerService.this, "login error", Toast.LENGTH_SHORT).show();
                throw e;
                // Restore interrupt status.
                //Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            //stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        // setup handler for uncaught exception
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        int start = getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.ECLAIR ?
                START_STICKY_COMPATIBILITY : START_STICKY;
        return start;

    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(on);
        this.unregisterReceiver(off);
        stopSelf();
//        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    //restart a killed service
    //https://stackoverflow.com/questions/8943288/how-to-implement-uncaughtexception-android
    @Override
    public void onTaskRemoved(Intent rootIntent) {
            /*Intent restartServiceTask = new Intent(getApplicationContext(),this.getClass());
            restartServiceTask.setPackage(getPackageName());
            PendingIntent servicex = PendingIntent.getService(
                    this,
                    1,
                    restartServiceTask,
                    0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()
                    + 1000, servicex);
            super.onTaskRemoved(rootIntent);*/
    }

    private Thread.UncaughtExceptionHandler defaultUEH;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            Intent restartServiceTask = new Intent(getApplicationContext(),this.getClass());
            restartServiceTask.setPackage(getPackageName());
            PendingIntent servicex = PendingIntent.getService(
                    getApplicationContext(),
                    1,
                    restartServiceTask,
                    0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()
                    + 1000, servicex);
            System.exit(2);
        }
    };
}
