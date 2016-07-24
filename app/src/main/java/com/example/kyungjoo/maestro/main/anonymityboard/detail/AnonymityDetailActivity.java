package com.example.kyungjoo.maestro.main.anonymityboard.detail;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kyungjoo.maestro.R;
import com.example.kyungjoo.maestro.main.component.ApplicationController;
import com.example.kyungjoo.maestro.main.network.NetworkService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by KyungJoo on 2016-07-13.
 */
public class AnonymityDetailActivity extends Activity {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    private NetworkService networkService;
    private TextView textViewTitle;
    private TextView textViewTimestamp;
    private TextView textViewContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail);
        initNetworkService();
        initView();

    }

    private void initNetworkService(){
        // TODO: 13. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }

    private void initView() {
        textViewTitle = (TextView) findViewById(R.id.txtTitle_detail);
        textViewTimestamp = (TextView) findViewById(R.id.txtTimestamp_detail);
        textViewContent = (TextView) findViewById(R.id.txtContent_detail);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        long article_id = intent.getLongExtra("article_id", -1);
        if (article_id != -1) {
            // TODO: 14. article_id로 식별하여 Content를 GET 방식으로 요청
            Call<AnonymityContent> contentCall = networkService.getAnonyContent(article_id);
            contentCall.enqueue(new Callback<AnonymityContent>() {
                @Override
                public void onResponse(Call<AnonymityContent> call, Response<AnonymityContent> response) {
                    if(response.isSuccessful()){
                        AnonymityContent content_temp = response.body();
                        textViewTitle.setText(content_temp.title);
                        textViewTimestamp.setText("작성일 : "+FORMAT.format(content_temp.timestamp));
                        textViewContent.setText(content_temp.content);
                        //Log.i("TEST","title : "+ content_temp.content);

                    }else{
                        int statusCode = response.code();
                        //Log.i("TEST", "response : " + statusCode);
                    }
                }

                @Override
                public void onFailure(Call<AnonymityContent> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Failed to load", Toast.LENGTH_LONG).show();
                    Log.i("TEST","err : " + t.getMessage());
                    finish();
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Invalid article",
                    Toast.LENGTH_LONG).show();
        }
    }
}
