package mvmr.mvmr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenOffReceiver extends BroadcastReceiver {

    private SQLiteDatabase _dbMvmr;
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



                //_dbMvmr = context.openOrCreateDatabase("MVMR", context.MODE_PRIVATE, null);
                //Cursor resultSet = _dbMvmr.rawQuery("SELECT MAX(ID) FROM table;", null);
                //resultSet.moveToFirst();
                //0_dbMvmr.execSQL("UPDATE Source SET UnLit = datetime('now') where ID = " + resultSet.getInt(0));

                SharedPreferences settings = context.getSharedPreferences("MVMR", 0);
                SharedPreferences.Editor rowEditor = context.getSharedPreferences("MVMR_unlit", 0).edit();
                rowEditor.putString(settings.getString("row", null), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                rowEditor.commit();

                // Must call finish() so the BroadcastReceiver can be recycled.
                pendingResult.finish();
                return sb.toString();
            }
        };
        asyncTask.execute();
    }
}
