package com.example.admin.bule.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.bule.Bean;
import com.example.admin.bule.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by admin on 2017/3/11.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
    Context context;
    List<Bean> list;
OnItemClickListener onItemClickListener;

    public HomeAdapter(Context context, List<Bean> list) {
        this.context = context;
        this.list = list;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.home_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
     holder.homeitemTv.setText(list.get(position).message);
        if(onItemClickListener!= null){//回调
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     onItemClickListener.onItemClick(v,position);
                }
            });
        }
    }

    public List<Bean> getList() {
        return list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.homeitem_tv)
        TextView homeitemTv;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;

    }

    public interface OnItemClickListener
    {
         void onItemClick(View view,int position);
        void onItemLongClick(View view ,int position);
    }
}
