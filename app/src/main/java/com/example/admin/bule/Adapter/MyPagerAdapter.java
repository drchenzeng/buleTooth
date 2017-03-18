package com.example.admin.bule.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by admin on 2017/3/11.
 */

public class MyPagerAdapter extends FragmentStatePagerAdapter{
    List<Fragment> list;//ViewPager要填充的fragment列表  
    List<String>title;//tab中的title文字列表  
    //使用构造方法来将数据传进去  
            public MyPagerAdapter(FragmentManager fm, List<Fragment> list,List<String>title) {
         super(fm);
         this.list = list;
         this.title = title;
        }


             @Override
             public   Fragment getItem(int position) {//获得position中的fragment来填充  
         return list.get(position);
         }

             @Override
             public int getCount() {//返回FragmentPager的个数  
         return list.size();
         }

            //FragmentPager的标题,如果重写这个方法就显示不出tab的标题内容  
             @Override
             public CharSequence getPageTitle(int position) {
        return title.get(position);
         }
}
