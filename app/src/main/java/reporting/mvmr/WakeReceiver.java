package reporting.mvmr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WakeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent i = new Intent(context, ListennerService.class);
        context.startService(i);
    }
}
