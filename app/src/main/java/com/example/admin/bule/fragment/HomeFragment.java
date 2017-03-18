package com.example.admin.bule.fragment;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.admin.bule.Adapter.HomeAdapter;
import com.example.admin.bule.Bean;
import com.example.admin.bule.MainActivity;
import com.example.admin.bule.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by admin on 2017/3/11.
 */

public class HomeFragment extends Fragment {


    List<Bean> deviceList = new ArrayList<>();
    HomeAdapter homeAdapter;
    @InjectView(R.id.home_rl)
    RecyclerView homeRl;
    @InjectView(R.id.home_bn_service)
    Button homeBnService;
    @InjectView(R.id.home_bn_search)
    Button homeBnSearch;
    //蓝牙适配
    private BluetoothAdapter mBtAdapter;

    final static int SEARCH_FINISH = 0;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEARCH_FINISH:
                    homeBnSearch .setText("重新搜索");
                    break;
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        ButterKnife.inject(this, view);
        initView();
        registerBroadcast();
        initDatas();
        return view;
    }

    private void initDatas() {
        Log.i("tag", "mBtAdapter=="+ mBtAdapter);
        //根据适配器得到所有的设备信息
        Set<BluetoothDevice> deviceSet = mBtAdapter.getBondedDevices();
        if (deviceSet.size() > 0) {
            for (BluetoothDevice device : deviceSet) {
                deviceList.add(new Bean(device.getName() + "\n" + device.getAddress(), true));
                homeAdapter.notifyDataSetChanged();
//                mListView.setSelection(mDatas.size() - 1);
            }
        } else {
            deviceList.add(new Bean("没有配对的设备", true));
            homeAdapter.notifyDataSetChanged();
//            mListView.setSelection(mDatas.size() - 1);
        }
    }

    /**
     * 注册广播
     */
    private void registerBroadcast() {
        //设备被发现广播
        IntentFilter discoveryFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, discoveryFilter);

        // 设备发现完成
        IntentFilter foundFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(mReceiver, foundFilter);
    }

    /**
     * 发现设备广播
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 获得设备信息
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 如果绑定的状态不一样
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {

                    deviceList.add(new Bean(device.getName() + "\n" + device.getAddress(), false));
                    homeAdapter.notifyDataSetChanged();
//                    mListView.setSelection(mDatas.size() - 1);
                }
                // 如果搜索完成了
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                setProgressBarIndeterminateVisibility(false);
                if (deviceList.size() == 0) {
                    deviceList.add(new Bean("û没有发现蓝牙设备", false));
                    homeAdapter.notifyDataSetChanged();

                }
                handler.sendEmptyMessage(SEARCH_FINISH);
            }
        }
    };
    private void initView() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();//拿到蓝牙适配器

        homeAdapter = new HomeAdapter(getActivity(), deviceList);
        homeAdapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Bean bean = deviceList.get(position);
                String info = bean.message;
                String address = info.substring(info.length() - 17);
                Toast.makeText(getActivity(),address,Toast.LENGTH_SHORT).show();
                MainActivity.BlueToothAddress = address;

                AlertDialog.Builder stopDialog = new AlertDialog.Builder(getActivity());
                stopDialog.setTitle("连接");//标题
                stopDialog.setMessage(bean.message);
                stopDialog.setPositiveButton("连接", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mBtAdapter.cancelDiscovery();
                        homeBnService.setText("重新搜索");

                        MainActivity.mType = MainActivity.Type.CILENT;
//                        BluetoothActivity.mTabHost.setCurrentTab(1);
                        //// TODO: 2017/3/12  切换到会话页面

                        dialog.cancel();
                    }
                });
                stopDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.BlueToothAddress = null;
                        dialog.cancel();
                    }
                });
                stopDialog.show();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        RecyclerView.LayoutManager lr = new LinearLayoutManager(getActivity());
        homeRl.setLayoutManager(lr);
        homeRl.setAdapter(homeAdapter);


    }

@OnClick({R.id.home_bn_search,R.id.home_bn_service})
public void onClick(View view){
    switch (view.getId()){
        case R.id.home_bn_service:
            MainActivity.mType = MainActivity.Type.SERVICE;
            Toast.makeText(getActivity(),"服务器正在开启",Toast.LENGTH_SHORT).show();
            //// TODO: 2017/3/12 切换
            break;
        case R.id.home_bn_search:
           homeBnSearch.setText("搜索中");
            onclickSearch();
            break;
    }

}

    /**
     * 搜索监听
     */
    private void onclickSearch(){
        //蓝牙可见
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
            homeBnSearch.setText("重新搜索");
        } else {
            deviceList.clear();
            homeAdapter.notifyDataSetChanged();

            initDatas();

				/* 开始搜索 */
            mBtAdapter.startDiscovery();
            homeBnSearch.setText("ֹͣ停止搜索");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        //激活蓝牙
        if (!mBtAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 3);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}