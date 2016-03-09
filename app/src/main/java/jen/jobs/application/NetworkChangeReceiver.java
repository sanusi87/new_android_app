package jen.jobs.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
        if (isConnected) {
            // TODO - start data synchronization
        }
    }
}
