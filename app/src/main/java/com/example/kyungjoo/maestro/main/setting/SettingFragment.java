package com.example.kyungjoo.maestro.main.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kyungjoo.maestro.R;
import com.example.kyungjoo.maestro.main.LoginActivity;
import com.example.kyungjoo.maestro.main.Response;
import com.example.kyungjoo.maestro.main.board.BoardActivity;
import com.example.kyungjoo.maestro.main.component.ApplicationController;
import com.example.kyungjoo.maestro.main.network.NetworkService;
import com.example.kyungjoo.maestro.main.people.UserProfile;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by KyungJoo on 2016-07-21.
 */
public class SettingFragment extends Fragment {
    Button logoutBtn;
    NetworkService networkService;
    CallbackManager callbackManager;
    ImageView imageView;
    TextView textView;

    public static SettingFragment newInstance(){
        SettingFragment settingFragment = new SettingFragment();
        return settingFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_setting, container, false);
        initView(view);
        return view;
    }

    public void initView(View view){
        logoutBtn = (Button) view.findViewById(R.id.facebook_logout_btn);
        callbackManager = CallbackManager.Factory.create();
        imageView = (ImageView) view.findViewById(R.id.profile_img);
        textView = (TextView) view.findViewById(R.id.profile_name);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        Intent intent = getActivity().getIntent();
        networkService = ApplicationController.getInstance().getNetworkService();
        Call<UserProfile> call = networkService.getUserProfile(intent.getStringExtra("ID"));
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, retrofit2.Response<UserProfile> response) {
                if(response.isSuccessful()){
                    UserProfile userProfile = response.body();
                    Picasso.with(getContext()).load(userProfile.profile).resize(1000, 1000).into(imageView);
                    textView.setText(userProfile.name);
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {

            }
        });

    }
}
