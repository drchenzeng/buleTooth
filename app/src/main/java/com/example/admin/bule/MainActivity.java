package com.example.admin.bule;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.view.LayoutInflater;


import com.example.admin.bule.Adapter.MyPagerAdapter;
import com.example.admin.bule.fragment.CatFragment;
import com.example.admin.bule.fragment.HomeFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity {
    public static String BlueToothAddress;//蓝牙地址
     public static boolean isOpen = false;//
   public   static Type mType = Type.NONE;//类型
    Context context;
    //类型：
    public enum Type {
        NONE, SERVICE, CILENT
    };

    @InjectView(R.id.home_tab)
    TabLayout homeTab;
    @InjectView(R.id.home_vp)
    ViewPager homeVp;

    private LayoutInflater mInflater;
    private List<String> mTitleList = new ArrayList<>();//页卡标题集合
    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (context == null) {
            context = this;
        }
        ButterKnife.inject(this);
        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(this);
        mInflater = LayoutInflater.from(context);
        HomeFragment homef = new HomeFragment();
        CatFragment catf = new CatFragment();
        fragmentList.add(homef);
        fragmentList.add(catf);
        mTitleList.add("设备列表");
        mTitleList.add("会话列表");


        MyPagerAdapter mAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList, mTitleList);


        homeVp.setAdapter(mAdapter);//给ViewPager设置适配器
        homeTab.setupWithViewPager(homeVp);//将TabLayout和ViewPager关联起来。


    }


}
