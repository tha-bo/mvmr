package reporting.mvmr;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import reporting.mvmr.models.UserModel;

/**
 * Created by PeerlessGate on 8/3/2017.
 */

public class FirebaseRepository {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabaseInstance() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

    public static String GetUser(Context context, DatabaseReference database)
    {
        TrayModule tray = new TrayModule(context);

        //String userId = settings.getString("user_id", null);
        String userId = tray.getString("user_id", null);
        if(userId != null && userId != "")
            return userId;

        userId = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss_").format(new Date()) + java.util.UUID.randomUUID().toString();
        tray.put("user_id", userId);
        /*SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putString("user_id", userId);
        settingsEditor.apply();*/


        UserModel user = new UserModel(
                Build.MANUFACTURER,
                Build.MODEL,
                Build.VERSION.RELEASE + " | " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName()
        );

        String socialMedia = tray.getString("socialMedia", null);
        if(socialMedia == null || socialMedia =="") {
            String[] socialMediaArray = context.getResources().getStringArray(R.array.known_sm);

            for (String sm : socialMediaArray) {
                if (appInstalledOrNot(sm.split(",")[0], context)) {
                    user.SocialMedia += sm.split(",")[1] + " ";
                }
            }
            tray.put("socialMedia", user.SocialMedia);
        }
        database.child("users").child(userId).setValue(user);
        return userId;
    }

    private static boolean appInstalledOrNot(String uri, Context context)
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
