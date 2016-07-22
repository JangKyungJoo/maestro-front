package com.example.kyungjoo.maestro.main.gcm;

import android.content.Intent;

/**
 * Created by KyungJoo on 2016-07-22.
 */
public class InstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
