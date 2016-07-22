package com.example.kyungjoo.maestro.main.anonymityboard.thumbnail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kyungjoo.maestro.R;
import com.example.kyungjoo.maestro.main.anonymityboard.detail.AnonymityDetailActivity;
import com.example.kyungjoo.maestro.main.component.ApplicationController;
import com.example.kyungjoo.maestro.main.network.NetworkService;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by KyungJoo on 2016-07-15.
 */
public class AnonymityBoardFragment extends Fragment{
    private NetworkService networkService;
    private ListView listViewThumbnails;
    private AnonyThumbnailAdapter adapter;
    private ImageView imageView;

    public static AnonymityBoardFragment newInstance(){
        AnonymityBoardFragment anonymityBoardFragment = new AnonymityBoardFragment();
        return anonymityBoardFragment;
    }
        @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_anonymity_fragment, container, false);
            initNetworkService();
            initView(view);
            initAdapter();
            itemClick();
        return view;
    }

    private void itemClick() {
        // TODO: 10. ListView의 아이템 클릭 시 이벤트
        listViewThumbnails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AnonyThumbnail anonyThumbnail = (AnonyThumbnail) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), AnonymityDetailActivity.class);
                intent.putExtra("article_id", anonyThumbnail.id);
                startActivity(intent);
            }
        });

        listViewThumbnails.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                AnonyThumbnail anonyThumbnail = (AnonyThumbnail) adapter.getItem(position);
                Call<List<AnonyThumbnail>> updateCall = networkService.deleteAnony(getActivity().getIntent().getStringExtra("ID"), ""+anonyThumbnail.id);
                updateCall.enqueue(new Callback<List<AnonyThumbnail>>() {
                    @Override
                    public void onResponse(Call<List<AnonyThumbnail>> call, Response<List<AnonyThumbnail>> response) {
                        if(response.isSuccessful()){
                            List<AnonyThumbnail> anonyThumbnails = response.body();
                            adapter.setSource(anonyThumbnails);
                            Toast.makeText(getContext(), "Delete Success", Toast.LENGTH_SHORT).show();
                        }else{
                            int statusCode = response.code();
                            if(statusCode == 503){
                                Toast.makeText(getContext(), "no auth to delete", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getContext(), "error : fail to delete", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AnonyThumbnail>> call, Throwable t) {

                    }
                });

                return true;
            }
        });

    }
    private void initAdapter() {
        adapter = new AnonyThumbnailAdapter();
        listViewThumbnails.setAdapter(adapter);
    }

    private void initView(View view) {
        listViewThumbnails = (ListView) view.findViewById(R.id.anony_list_thumbnails);
        imageView = (ImageView) view.findViewById(R.id.anony_post_btn);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PostAnoymity.class);
                intent.putExtra("ID", getActivity().getIntent().getStringExtra("ID"));
                Log.d("TEST", "anony post id "+ getActivity().getIntent().getStringExtra("ID"));
                startActivity(intent);
            }
        });
    }

    private void initNetworkService() {
        // TODO: 8. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }


    @Override
    public void onResume() {
        super.onResume();

        // TODO: 11. 서버에서 Thumbnail을 리스트로 받아오기 위한 GET 방식의 요청 구현

        Call<List<AnonyThumbnail>> thumbnailsCall = networkService.getAnonyThumbnail();

        thumbnailsCall.enqueue(new Callback<List<AnonyThumbnail>>() {
            @Override
            public void onResponse(Call<List<AnonyThumbnail>> call, Response<List<AnonyThumbnail>> response) {
                if(response.isSuccessful()){
                    List<AnonyThumbnail> anonyThumbnails = response.body();
                    adapter.setSource(anonyThumbnails);
                }else{
                    int statusCode = response.code();
                    Log.i("TEST","응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<List<AnonyThumbnail>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load thumbnails", Toast.LENGTH_LONG).show();
                Log.i("TEST","에러내용 : "+ t.getMessage());
            }
        });
    }
}
