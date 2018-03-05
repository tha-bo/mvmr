package reporting.mvmr;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import reporting.mvmr.models.UsageItem;
import reporting.mvmr.models.UserModel;
import org.joda.time.LocalDateTime;

public class ScreenOffReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context,final Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        final PendingResult pendingResult = goAsync();
        AsyncTask<String, Integer, String> asyncTask = new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {
                LocalDateTime dt = new LocalDateTime();
                long time = SystemClock.elapsedRealtime()/1000L;
                SharedPreferences settings = context.getSharedPreferences("MVMR", 0);
                SharedPreferences.Editor settingsEditor = settings.edit();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                TrayModule tray = new TrayModule(context);

                boolean onSave = settings.getBoolean("onSave", false);
                String userId = FirebaseRepository.GetUser(context, mDatabase);
                String unlit = dt.toString("yyyy:MM:dd HH:mm:ss");
                String Id = unlit.trim();

                if(!onSave)
                {
                    mDatabase.child("usage").child(userId).child(Id).setValue(new UsageItem("",0, unlit, time));
                }

                else
                {
                    //save total for today;
                    long onTime = settings.getLong("onTime", 0L);
                    long dayTotal = settings.getLong("dayTotal", 0L);
                    long duration = time - onTime;
                    settingsEditor.putLong("dayTotal", dayTotal + duration);
                    tray.put("dayTotal", dayTotal + duration);

                    //save total for week;
                    long weekTotal = settings.getLong("weekTotal", 0L);
                    settingsEditor.putLong("weekTotal", weekTotal + duration);
                    tray.put("weekTotal", weekTotal + duration);

                    //Toast.makeText(context, onTime"", Toast.LENGTH_SHORT).show();
                    String lit = settings.getString("onTimeStamp", null);
                    settingsEditor.putBoolean("onSave", false);
                    settingsEditor.apply();
                    mDatabase.child("usage").child(userId).child(Id).setValue(new UsageItem(lit,onTime, unlit, time));
                }

                // Must call finish() so the BroadcastReceiver can be recycled.
                pendingResult.finish();
                return "success";
            }
        };
        asyncTask.execute();
    }
}
