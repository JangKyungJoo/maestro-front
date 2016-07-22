package com.example.kyungjoo.maestro.main.board.thumbnail;

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
import com.example.kyungjoo.maestro.main.anonymityboard.thumbnail.AnonyThumbnail;
import com.example.kyungjoo.maestro.main.anonymityboard.thumbnail.AnonyThumbnailAdapter;
import com.example.kyungjoo.maestro.main.board.detail.DetailActivity;
import com.example.kyungjoo.maestro.main.component.ApplicationController;
import com.example.kyungjoo.maestro.main.network.NetworkService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by KyungJoo on 2016-07-15.
 */
public class BoardFragment extends Fragment {
    private NetworkService networkService;
    private ListView listViewThumbnails;
    private ThumbnailAdapter adapter;
    private ImageView imageView;

    public static BoardFragment newInstance(){
        BoardFragment boardFragment = new BoardFragment();
        return boardFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.layout_board_fragment, container, false);
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
                Thumbnail thumbnail = (Thumbnail) adapter.getItem(position);
                Intent intent = new Intent((getActivity()), DetailActivity.class);
                intent.putExtra("article_id", thumbnail.id);
                startActivity(intent);
            }
        });

        listViewThumbnails.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Thumbnail Thumbnail = (Thumbnail) adapter.getItem(position);
                Call<List<Thumbnail>> updateCall = networkService.deleteContent(getActivity().getIntent().getStringExtra("ID"), ""+Thumbnail.id);
                updateCall.enqueue(new Callback<List<Thumbnail>>() {
                    @Override
                    public void onResponse(Call<List<Thumbnail>> call, Response<List<Thumbnail>> response) {
                        if(response.isSuccessful()){
                            List<Thumbnail> Thumbnails = response.body();
                            adapter.setSource(Thumbnails);
                            Toast.makeText(getContext(), "delete success", Toast.LENGTH_SHORT).show();
                        }else{
                            int statusCode = response.code();
                            if(statusCode == 500){
                                Toast.makeText(getContext(), "fail to load list", Toast.LENGTH_SHORT).show();
                            }else if(statusCode == 502){
                                Toast.makeText(getContext(), "no auth to delete", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getContext(), "fail to delete", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Thumbnail>> call, Throwable t) {

                    }
                });

                return true;
            }
        });

    }
    private void initAdapter() {
        adapter = new ThumbnailAdapter();
        listViewThumbnails.setAdapter(adapter);
    }

    private void initView(View view) {
        listViewThumbnails = (ListView) view.findViewById(R.id.list_thumbnails);
        imageView = (ImageView) view.findViewById(R.id.post_btn);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PostBoard.class);
                intent.putExtra("ID", getActivity().getIntent().getStringExtra("ID"));
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

        Call<List<Thumbnail>> thumbnailsCall = networkService.getThumbnail();

        thumbnailsCall.enqueue(new Callback<List<Thumbnail>>() {
            @Override
            public void onResponse(Call<List<Thumbnail>> call, Response<List<Thumbnail>> response) {
                if(response.isSuccessful()){
                    List<Thumbnail> Thumbnails = response.body();
                    adapter.setSource(Thumbnails);
                }else{
                    int statusCode = response.code();
                    Log.i("TEST","response : " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<List<Thumbnail>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load thumbnails", Toast.LENGTH_LONG).show();
                Log.i("TEST","err : "+ t.getMessage());
            }
        });
    }
}
