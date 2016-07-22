package com.example.kyungjoo.maestro.main.anonymityboard.thumbnail;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kyungjoo.maestro.R;
import com.example.kyungjoo.maestro.main.component.ApplicationController;
import com.example.kyungjoo.maestro.main.anonymityboard.detail.AnonymityContent;
import com.example.kyungjoo.maestro.main.network.NetworkService;
import com.facebook.AccessTokenTracker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by KyungJoo on 2016-07-13.
 */
public class PostAnoymity extends Activity{
    NetworkService networkService;
    AnonyThumbnailAdapter adapter;
    EditText editTitle, editContent;
    TextView sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_post_anoymity);
        networkService = ApplicationController.getInstance().getNetworkService();
        adapter = AnonyThumbnailAdapter.getInstance();
        initView();
    }

    private void initView(){
        editTitle = (EditText) findViewById(R.id.anoymity_title);
        editContent = (EditText) findViewById(R.id.anoymity_content);
        sendButton = (TextView) findViewById(R.id.send_btn);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTitle.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "fill title please", Toast.LENGTH_SHORT).show();
                }else {
                    AnonymityContent content = new AnonymityContent();
                    content.title = editTitle.getText().toString();
                    content.content = editContent.getText().toString();
                    // TODO: 9. NetworkService를 이용하여 POST 방식의 요청 구현
                    Call<AnonyThumbnail> thumbnailCall = networkService.newAnony(getIntent().getStringExtra("ID"), content);
                    thumbnailCall.enqueue(new Callback<AnonyThumbnail>() {
                        @Override
                        public void onResponse(Call<AnonyThumbnail> call, Response<AnonyThumbnail> response) {
                            if (response.isSuccessful()) {
                                AnonyThumbnail anonyThumbnail_temp = response.body();
                                adapter.append(anonyThumbnail_temp);
                                editTitle.setText("");
                                editContent.setText("");
                                Log.i("TEST", "쎔네일 제목 : " + anonyThumbnail_temp.title);
                                finish();
                            } else {
                                int statusCode = response.code();
                                Log.i("TEST", "응답코드 : " + statusCode);
                            }
                        }

                        @Override
                        public void onFailure(Call<AnonyThumbnail> call, Throwable t) {

                        }
                    });
                }
            }
        });
    }
}
