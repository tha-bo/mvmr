package mvmr.mvmr;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mvmr.mvmr.models.TimeStamp;
import mvmr.mvmr.models.UsageModel;

public class ScreenOffReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context,final Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        final PendingResult pendingResult = goAsync();
        AsyncTask<String, Integer, String> asyncTask = new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {

                SharedPreferences settings = context.getSharedPreferences("MVMR", 0);

                String id = settings.getString("row", null);
                String userId = context.getSharedPreferences("MVMR", 0).getString("user_id", null);
                long time = SystemClock.elapsedRealtime();
                String unlit = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(time);

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                if(id == null)
                {
                    id = java.util.UUID.randomUUID().toString();
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("usage").child(id).setValue(new UsageModel(userId, null, unlit));
                }

                else
                {
                    mDatabase.child("usage").child(id).child("Unlit").setValue(new TimeStamp(unlit));
                }
                // Must call finish() so the BroadcastReceiver can be recycled.
                pendingResult.finish();
                return "success";
            }
        };
        asyncTask.execute();
    }
}
