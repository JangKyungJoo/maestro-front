package com.example.kyungjoo.maestro.main.board;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.example.kyungjoo.maestro.R;
import com.example.kyungjoo.maestro.main.anonymityboard.thumbnail.AnonymityBoardFragment;
import com.example.kyungjoo.maestro.main.board.thumbnail.BoardFragment;
import com.example.kyungjoo.maestro.main.setting.SettingFragment;

/**
 * Created by KyungJoo on 2016-07-14.
 */
public class BoardActivity extends FragmentActivity {
    FragmentPagerAdapter fragmentPagerAdapter;
    PagerTabStrip pagerTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_board);
        //Log.d("TEST", getIntent().getStringExtra("ID"));
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_header);
        ((ViewPager.LayoutParams)pagerTabStrip.getLayoutParams()).isDecor = true;
        fragmentPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentPagerAdapter);
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter{
        private String title[] = new String[]{"자유게시판", "익명게시판", "설정"};
        public static int NUM_ITEM = 3;

        public MyPagerAdapter(FragmentManager fm){
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return BoardFragment.newInstance();
                case 1:
                    return AnonymityBoardFragment.newInstance();
                case 2:
                    return SettingFragment.newInstance();
                default:
                    return BoardFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return NUM_ITEM;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }

    }
}
