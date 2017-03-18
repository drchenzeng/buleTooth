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
 * Created by Administrator on 2017/3/12.
 */

public class CatAdapter extends RecyclerView.Adapter<CatAdapter.MyViewHolder> {
    Context context;
    List<Bean> list;


    public CatAdapter(Context context, List<Bean> list) {
        this.context = context;
        this.list = list;
    }

    public List<Bean> getList() {
        return list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.homeitemTv.setText(list.get(position).message);
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
            ButterKnife.inject(this,itemView);
        }
    }
}
