package com.example.kyungjoo.maestro.main.board.thumbnail;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kyungjoo.maestro.R;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by KyungJoo on 2016-07-15.
 */
public class ThumbnailAdapter extends BaseAdapter {
    private static ThumbnailAdapter instance;
    private static final SimpleDateFormat FORMAT =
            new SimpleDateFormat("yyyy-MM-dd hh:mm");

    private List<Thumbnail> thumbnails; //리스트뷰에 사용되는 모델

    public ThumbnailAdapter(){
        ThumbnailAdapter.instance = this;
    }

    public static ThumbnailAdapter getInstance(){
        return instance;
    }

    public void append(Thumbnail thumbnail) {
        this.thumbnails.add(0, thumbnail );
        this.notifyDataSetChanged();
    }

    public void setSource(List<Thumbnail> thumbnail) {
        this.thumbnails = thumbnail;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() { return thumbnails != null ? thumbnails.size() : 0; }

    @Override
    public Object getItem(int position) {
        return (thumbnails != null && (0 <= position && position < thumbnails.size()))
                ? thumbnails.get(position) : null;
    }

    @Override
    public long getItemId(int position) { return 0; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = View.inflate(parent.getContext(),
                R.layout.layout_item, null);
        TextView textViewTitle = (TextView) convertView.findViewById(R.id.txtTitle_detail);
        TextView textViewTimestamp = (TextView) convertView.findViewById(R.id.txtTimestamp_detail);

        Thumbnail thumbnail = thumbnails.get(position);

        textViewTitle.setText(thumbnail.title);
        textViewTimestamp.setText("작성자 : "+thumbnail.name+"\t\t\t작성일 : "+FORMAT.format(thumbnail.timestamp));

        return convertView;
    }
}
