package com.example.kyungjoo.maestro.main.component;

import android.app.Application;
import android.util.Log;

import com.example.kyungjoo.maestro.main.network.NetworkService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by KyungJoo on 2016-07-13.
 */
public class ApplicationController extends Application {
    // TODO: 2. ApplicationController의 instance 선언 및 getter 생성
    private static ApplicationController instance;
    public static ApplicationController getInstance(){return instance;}

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationController.instance = this;
    }

    // TODO: 3. Activity에서 사용할 NetworkService 선언 및 getter 생성
    private NetworkService networkService;
    public NetworkService getNetworkService() {
        return networkService;
    }

    private String baseUrl;

    public void buildNetworkService(String ip, int port) {
        synchronized (ApplicationController.class) {
            if (networkService == null) {
                baseUrl = String.format("http://%s:%d/", ip, port);
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                        .create();

                GsonConverterFactory factory = GsonConverterFactory.create(gson);

                // TODO: 4. Retrofit.Builder()를 이용해 Retrofit 객체를 생성한 후 이를 이용해 networkService 정의
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(factory)
                        .build();

                networkService = retrofit.create(NetworkService.class);
            }
        }
    }
}
