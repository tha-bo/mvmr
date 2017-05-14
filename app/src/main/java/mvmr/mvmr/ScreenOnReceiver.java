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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import mvmr.mvmr.models.UsageModel;
import mvmr.mvmr.models.UserModel;

import static android.content.ContentValues.TAG;

public class ScreenOnReceiver extends BroadcastReceiver {

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
                sb.append("URI: " + "I turned on" + "\n");
                Log.d("brrbrbrb", sb.toString());

                String id = java.util.UUID.randomUUID().toString();
                String userId = context.getSharedPreferences("MVMR", 0).getString("user_id", null);
                String lit = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
                SharedPreferences.Editor settingsEditor = context.getSharedPreferences("MVMR", 0).edit();
                SharedPreferences.Editor rowEditor = context.getSharedPreferences("MVMR_lit", 0).edit();

                rowEditor.putString(id, lit);
                settingsEditor.putString("row", id);
                settingsEditor.commit();
                rowEditor.commit();

                ((ListennerService)context).mDatabase = FirebaseDatabase.getInstance().getReference();
                ((ListennerService)context).mDatabase.child("usage").child(id).setValue(new UsageModel(userId, lit, null));

                // Must call finish() so the BroadcastReceiver can be recycled.
                pendingResult.finish();
                return sb.toString();
            }
        };
        asyncTask.execute();
    }
}
