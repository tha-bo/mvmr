package mvmr.mvmr;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import mvmr.mvmr.models.UsageItem;
import mvmr.mvmr.models.UserModel;
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

                boolean onSave = settings.getBoolean("onSave", false);
                String userId = GetUser(settings, context, mDatabase);
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
                    //save total for week;
                    long weekTotal = settings.getLong("weekTotal", 0L);
                    settingsEditor.putLong("weekTotal", weekTotal + duration);

                    //Toast.makeText(context, onTime"", Toast.LENGTH_SHORT).show();
                    String lit = settings.getString("onTimeStamp", null);
                    settingsEditor.putBoolean("onSave", false);
                    settingsEditor.commit();
                    mDatabase.child("usage").child(userId).child(Id).setValue(new UsageItem(lit,onTime, unlit, time));
                }

                // Must call finish() so the BroadcastReceiver can be recycled.
                pendingResult.finish();
                return "success";
            }
        };
        asyncTask.execute();
    }

    private String GetUser(SharedPreferences settings, Context context, DatabaseReference database)
    {
        String userId = settings.getString("user_id", null);

        if(userId != null && userId != "")
            return userId;

        userId = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss_").format(new Date()) + java.util.UUID.randomUUID().toString();
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putString("user_id", userId);
        settingsEditor.commit();

        UserModel user = new UserModel(
                Build.MANUFACTURER,
                Build.MODEL,
                Build.VERSION.RELEASE + " | " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName()
        );

        String socialMedia = settings.getString("socialMedia", null);
        if(socialMedia == null || socialMedia =="") {
            String[] socialMediaArray = context.getResources().getStringArray(R.array.known_sm);

            for (String sm : socialMediaArray) {
                if (appInstalledOrNot(sm.split(",")[0], context)) {
                    user.SocialMedia += sm.split(",")[1] + " ";
                }
            }
            settingsEditor.putString("socialMedia", user.SocialMedia);
        }
        database.child("users").child(userId).setValue(user);
        return userId;
    }

    private boolean appInstalledOrNot(String uri, Context context)
    {
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try
        {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            app_installed = false;
        }
        return app_installed ;
    }
}
