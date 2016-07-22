package com.example.kyungjoo.maestro.main.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.kyungjoo.maestro.R;
import com.example.kyungjoo.maestro.main.LoginActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by KyungJoo on 2016-07-22.
 */
public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegistrationIntentService";

    public RegistrationIntentService(){
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            //app폴더 밑에 저장한 google-services.json 파일에 있는 클라이언트 키값으로 토큰을 만든다.
            InstanceID instanceID = InstanceID.getInstance(this);
            String sender_id = getString(R.string.gcm_defaultSenderId);
            String scope = GoogleCloudMessaging.INSTANCE_ID_SCOPE;
            String token = instanceID.getToken(sender_id, scope, null);

            Log.d("TSET", "!token: " + token);
            // [END get_token]
            //TODO server로 token 전송
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
