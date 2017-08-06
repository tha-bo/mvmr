package mvmr.mvmr;
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

import mvmr.mvmr.models.UsageItem;
import mvmr.mvmr.models.UsageModel;
import mvmr.mvmr.models.UserModel;

public class ScreenOnReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context,final Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        final PendingResult pendingResult = goAsync();
        AsyncTask<String, Integer, String> asyncTask = new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {

                String id = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss").format(new Date());
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                String userId = GetUser(context.getSharedPreferences("MVMR", 0), context, mDatabase);
                long time = SystemClock.elapsedRealtime();
                String lit = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
                SharedPreferences.Editor settingsEditor = context.getSharedPreferences("MVMR", 0).edit();

                settingsEditor.putString("row", id);
                settingsEditor.commit();


                mDatabase.child("usage").child(userId).child(id).setValue(new UsageItem(lit, time, null, 0));

                // Must call finish() so the BroadcastReceiver can be recycled.
                pendingResult.finish();
                return "success";
            }
        };
        asyncTask.execute();
    }

    private String GetUser(SharedPreferences settings, Context context, DatabaseReference database)
    {
        String userId = context.getSharedPreferences("MVMR", 0).getString("user_id", null);
        if(userId == null || userId == "")
        {
            userId = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss_").format(new Date()) + java.util.UUID.randomUUID().toString();
            SharedPreferences.Editor settingsEditor = settings.edit();
            settingsEditor.putString("user_id", userId);

            UserModel user = new UserModel(
                    Build.MANUFACTURER,
                    Build.MODEL,
                    Build.VERSION.RELEASE + " | " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName()
            );

            String[] socialMedia = context.getResources().getStringArray(R.array.known_sm);

            for (String sm : socialMedia) {
                if (appInstalledOrNot(sm.split(",")[0], context)) {
                    user.SocialMedia += sm.split(",")[1] + " ";
                }
            }

            database.child("users").child(userId).setValue(user);
        }
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
