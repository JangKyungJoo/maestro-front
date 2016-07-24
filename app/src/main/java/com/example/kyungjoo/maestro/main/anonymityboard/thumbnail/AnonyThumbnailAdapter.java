package com.example.kyungjoo.maestro.main.anonymityboard.thumbnail;

import android.icu.text.SimpleDateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kyungjoo.maestro.R;

import java.util.List;

/**
 * Created by KyungJoo on 2016-07-13.
 */
public class AnonyThumbnailAdapter extends BaseAdapter{
    private static AnonyThumbnailAdapter instance;
    private static final SimpleDateFormat FORMAT =
            new SimpleDateFormat("yyyy-MM-dd hh:mm");

    private List<AnonyThumbnail> anonyThumbnails; //리스트뷰에 사용되는 모델

    public AnonyThumbnailAdapter(){
        AnonyThumbnailAdapter.instance = this;
    }

    public static AnonyThumbnailAdapter getInstance(){
        return instance;
    }
    public void append(AnonyThumbnail anonyThumbnail) {
        this.anonyThumbnails.add(0, anonyThumbnail);
        this.notifyDataSetChanged();
    }

    public void setSource(List<AnonyThumbnail> anonyThumbnails) {
        this.anonyThumbnails = anonyThumbnails;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() { return anonyThumbnails != null ? anonyThumbnails.size() : 0; }
    @Override
    public Object getItem(int position) {
        return (anonyThumbnails != null && (0 <= position && position < anonyThumbnails.size()))
                ? anonyThumbnails.get(position) : null;
    }
    @Override
    public long getItemId(int position) { return 0; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = View.inflate(parent.getContext(),
                R.layout.layout_item, null);
        TextView textViewTitle = (TextView) convertView.findViewById(R.id.txtTitle_detail);
        TextView textViewTimestamp = (TextView) convertView.findViewById(R.id.txtTimestamp_detail);

        AnonyThumbnail anonyThumbnail = anonyThumbnails.get(position);

        textViewTitle.setText(anonyThumbnail.title);
        textViewTimestamp.setText("작성일 : "+FORMAT.format(anonyThumbnail.timestamp));

        return convertView;
    }
}
