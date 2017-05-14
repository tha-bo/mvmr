package mvmr.mvmr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
                StringBuilder sb = new StringBuilder();
                sb.append("Action: " + intent.getAction() + "\n");
                sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
                sb.append("URI: " + "I turned off" + "\n");
                Log.d("brrbrbrb", sb.toString());

                SharedPreferences settings = context.getSharedPreferences("MVMR", 0);
                SharedPreferences litSettings = context.getSharedPreferences("MVMR_lit", 0);
                SharedPreferences.Editor rowEditor = context.getSharedPreferences("MVMR_unlit", 0).edit();

                String id = settings.getString("row", null);
                String userId = context.getSharedPreferences("MVMR", 0).getString("user_id", null);
                String unlit = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());


                rowEditor.putString(id, unlit);
                rowEditor.commit();

                ((ListennerService)context).mDatabase = FirebaseDatabase.getInstance().getReference();
                if(id == null)
                {
                    id = java.util.UUID.randomUUID().toString();
                    ((ListennerService)context).mDatabase = FirebaseDatabase.getInstance().getReference();
                    ((ListennerService)context).mDatabase.child("usage").child(id).setValue(new UsageModel(userId, null, unlit));
                }

                else
                {
                    Map<String, Object> childUpdates = new HashMap<>();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("Unlit", unlit);
                    result.put("User", userId);
                    result.put("Lit", litSettings.getString(id, null));
                    childUpdates.put("/usage/" + id, result);
                    ((ListennerService)context).mDatabase.updateChildren(childUpdates);
                }

                // Must call finish() so the BroadcastReceiver can be recycled.
                pendingResult.finish();
                return sb.toString();
            }
        };
        asyncTask.execute();
    }
}
