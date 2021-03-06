package reporting.mvmr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.Toast;

import org.joda.time.LocalDateTime;

public class ScreenOnReceiver extends BroadcastReceiver {

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

                SharedPreferences settings = context.getSharedPreferences("MVMR", Context.MODE_MULTI_PROCESS);
                SharedPreferences.Editor settingsEditor = settings.edit();
                TrayModule tray = new TrayModule(context);

                String lit = dt.toString("yyyy:MM:dd HH:mm:ss");
                settingsEditor.putBoolean("onSave", true);
                settingsEditor.putLong("onTime", time);
                settingsEditor.putString("onTimeStamp", lit);

                //start a new day if we must
                int today = dt.getDayOfWeek();
                int lastOnDay = settings.getInt("onDay", 0);

                boolean sameDay = (lastOnDay == today);
                if(!sameDay) {
                    settingsEditor.putLong("dayTotal", 0L);
                    settingsEditor.putInt("onDay", today);
                    tray.put("dayTotal", 0L);
                }

                //start a new week if we must
                int dayOfTheYear = dt.getDayOfYear();
                int weekEnding = settings.getInt("weekEnding", -1);

                if(dayOfTheYear > weekEnding || weekEnding == -1){
                    dt = dt.withDayOfWeek(7);
                    int weekEndingDayOffTheYear = dt.getDayOfYear();
                    settingsEditor.putString("weekEndingDate", dt.toString("yyyy/MM/dd"));
                    settingsEditor.putInt("weekEnding", weekEndingDayOffTheYear);
                    settingsEditor.putLong("weekTotal", 0L);
                    tray.put("weekEndingDate", dt.toString("yyyy/MM/dd"));
                    tray.put("weekTotal", 0L);
                }
                // Must call finish() so the BroadcastReceiver can be recycled.
                settingsEditor.apply();
                pendingResult.finish();
                return "success";
            }
        };
        asyncTask.execute();
    }
}
