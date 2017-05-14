package mvmr.mvmr;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

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
                Thread.sleep(5000);
                Log.d("brrbrbrb", "start");
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
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
        on = new ScreenOnReceiver();
        off = new ScreenOffReceiver();

        IntentFilter onfilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        IntentFilter offfilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        this.registerReceiver(on, onfilter);
        this.registerReceiver(off, offfilter);

        /*_dbMvmr = openOrCreateDatabase("MVMR", MODE_PRIVATE, null);

        _dbMvmr.execSQL("CREATE TABLE IF NOT EXISTS Source(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Lit DATETIME NULL," +
                "UnLit DATETIME NULL);");
        _dbMvmr.execSQL("INSERT INTO Source (Lit, UnLit) VALUES(NULL,NULL);");

        _dbMvmr.execSQL("CREATE TABLE IF NOT EXISTS Version(" +
                "Version VARCHAR NOT NULL);");*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
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
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}
