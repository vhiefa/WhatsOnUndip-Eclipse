package com.vhiefa.whatsonundip.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WhatsOnUndipSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static WhatsOnUndipSyncAdapter sWhastonundipSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("WhastonundipSyncService", "onCreate - WhastonundipSyncService");
        synchronized (sSyncAdapterLock) {
            if (sWhastonundipSyncAdapter == null) {
                sWhastonundipSyncAdapter = new WhatsOnUndipSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sWhastonundipSyncAdapter.getSyncAdapterBinder();
    }
}