package com.example.kyungjoo.maestro.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.kyungjoo.maestro.R;
import com.example.kyungjoo.maestro.main.board.BoardActivity;
import com.example.kyungjoo.maestro.main.component.ApplicationController;
import com.example.kyungjoo.maestro.main.gcm.RegistrationIntentService;
import com.example.kyungjoo.maestro.main.network.NetworkService;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by KyungJoo on 2016-07-12.
 */
public class LoginActivity extends Activity{
    public static final String SERVER_IP = "52.41.201.121";
    public static final int SERVER_PORT = 3000;
    private String TEXT = "text/plain";
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    Button FBLoginBtn;
    ApplicationController applicationController;
    NetworkService networkService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplicationContext());
        setContentView(R.layout.layout_login);
        FBLoginBtn = (Button) findViewById(R.id.facebook_login_btn);

        applicationController = new ApplicationController();
        applicationController.onCreate();
        applicationController = ApplicationController.getInstance();
        applicationController.buildNetworkService(SERVER_IP, SERVER_PORT);
/*
        if(checkPlayServices()){
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);    //서비스 실행
        }
*/
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                checkUser(accessToken);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("TEST", "err : "+ error.toString());
                Toast.makeText(getApplicationContext(), "error with login facebook", Toast.LENGTH_SHORT).show();
            }
        });
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null) {
                    LoginManager.getInstance().logOut();
                }
            }
        };
    }
/*
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.d("TEST", "can use play services");
        }else{
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
            } else {
                Log.d("TEST", "This device is not supported.");
                apiAvailability.showErrorNotification(this, resultCode);
                finish();
            }
            return false;
        }
        return true;
    }
*/
    public void checkUser(final AccessToken accessToken){
        networkService = applicationController.getNetworkService();
        Log.d("TEST", "id : "+accessToken.getUserId());
        Call<Response> call = networkService.checkPeople(accessToken.getUserId());
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if(response.isSuccessful()){
                    if(response.code() == 200){
                        Profile profile = Profile.getCurrentProfile();
                        enroll(profile);
                    }else if(response.code() == 201){
                        Intent intent = new Intent(getApplicationContext(), BoardActivity.class);
                        intent.putExtra("ID", accessToken.getUserId());
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

            }
        });
    }

    public void enroll(final Profile profile){
        networkService = applicationController.getNetworkService();
        RequestBody requestID = RequestBody.create(MediaType.parse(TEXT), profile.getId());
        RequestBody requestName = RequestBody.create(MediaType.parse(TEXT), profile.getName());
        RequestBody requestProfile = RequestBody.create(MediaType.parse(TEXT), "https://graph.facebook.com/" + profile.getId() + "/picture?type=large");
        Call<Response> call = networkService.newPeople(requestID, requestName,requestProfile);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if(response.isSuccessful()){
                    Intent intent = new Intent(getApplicationContext(), BoardActivity.class);
                    intent.putExtra("ID", profile.getId());
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.d("TEST", "err : "+t.getMessage());
                Toast.makeText(getApplicationContext(), "fail to enroll", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AccessToken.getCurrentAccessToken() != null) {
            Intent intent = new Intent(getApplicationContext(), BoardActivity.class);
            intent.putExtra("ID", AccessToken.getCurrentAccessToken().getUserId());
            startActivity(intent);
            finish();
        }else{
            LoginManager.getInstance().logOut();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
