package mvmr.mvmr;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;

import mvmr.mvmr.models.UsageModel;

public class ScreenOnReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context,final Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        final PendingResult pendingResult = goAsync();
        AsyncTask<String, Integer, String> asyncTask = new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {

                String id = java.util.UUID.randomUUID().toString();
                String userId = context.getSharedPreferences("MVMR", 0).getString("user_id", null);
                String lit = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
                SharedPreferences.Editor settingsEditor = context.getSharedPreferences("MVMR", 0).edit();

                settingsEditor.putString("row", id);
                settingsEditor.commit();

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("usage").child(id).setValue(new UsageModel(userId, lit, null));

                // Must call finish() so the BroadcastReceiver can be recycled.
                pendingResult.finish();
                return "success";
            }
        };
        asyncTask.execute();
    }
}
