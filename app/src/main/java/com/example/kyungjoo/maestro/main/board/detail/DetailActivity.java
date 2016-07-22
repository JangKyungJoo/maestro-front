package com.example.kyungjoo.maestro.main.board.detail;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kyungjoo.maestro.R;
import com.example.kyungjoo.maestro.main.component.ApplicationController;
import com.example.kyungjoo.maestro.main.network.NetworkService;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by KyungJoo on 2016-07-15.
 */
public class DetailActivity extends Activity{
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    private final String host = "http://ec2-52-41-201-121.us-west-2.compute.amazonaws.com:3000/board";
    private NetworkService networkService;
    private TextView textViewTitle;
    private TextView textViewTimestamp;
    private TextView textViewContent;
    LinearLayout linearLayout;
    ImageView imageView;

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
        linearLayout = (LinearLayout) findViewById(R.id.detail_linear);
        imageView = (ImageView) findViewById(R.id.detail_img);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        long article_id = intent.getLongExtra("article_id", -1);
            if (article_id != -1) {
            // TODO: 14. article_id로 식별하여 Content를 GET 방식으로 요청
            Call<Content> contentCall = networkService.getContent(article_id);
            contentCall.enqueue(new Callback<Content>() {
                @Override
                public void onResponse(Call<Content> call, Response<Content> response) {
                    if(response.isSuccessful()){
                        Content content_temp = response.body();
                        textViewTitle.setText(content_temp.title);
                        textViewTimestamp.setText("작성자 : "+content_temp.writer+"\t\t\t작성일 : "+FORMAT.format(content_temp.timestamp));
                        textViewContent.setText(content_temp.content);
                        if(content_temp.photopath != null){
                            //ImageView imageView = new ImageView(getApplicationContext());
                            //linearLayout.addView(imageView);
                            imageView.setVisibility(View.VISIBLE);
                            Picasso.with(getApplicationContext()).load(host+content_temp.photopath).into(imageView);
                            Log.d("TEST", "req : "+host+content_temp.photopath);
                        }else{
                            imageView.setVisibility(View.GONE);
                        }
                    }else{
                        int statusCode = response.code();
                        Log.i("TEST", "response : " + statusCode);
                    }
                }

                @Override
                public void onFailure(Call<Content> call, Throwable t) {
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
