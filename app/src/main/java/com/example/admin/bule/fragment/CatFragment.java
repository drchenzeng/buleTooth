package com.example.admin.bule.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin.bule.Adapter.CatAdapter;
import com.example.admin.bule.Adapter.HomeAdapter;
import com.example.admin.bule.Bean;
import com.example.admin.bule.MainActivity;
import com.example.admin.bule.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by admin on 2017/3/11.
 */

public class CatFragment extends Fragment {
    @InjectView(R.id.Catft_rl)
    RecyclerView CatftRl;
    @InjectView(R.id.catft_bn_cut)
    Button catftBnCut;
    @InjectView(R.id.catft_et)
    EditText catftEt;
    @InjectView(R.id.catft_bn_send)
    Button catftBnSend;
CatAdapter catAdapter;
    List<Bean> mDatas = new ArrayList<>();
    // 蓝牙服务端socket
    private BluetoothServerSocket mServerSocket;
    // 蓝牙客户端socket
    private BluetoothSocket mSocket;
    // 设备
    private BluetoothDevice mDevice;
    private BluetoothAdapter mBluetoothAdapter;

    // --线程类-----------------
    private ServerThread mServerThread;
    private ClientThread mClientThread;
    private ReadThread mReadThread;

    /* 一些常量，代表服务器的名称 */

    public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";

    private static final int STATUS_CONNECT = 0x11;
    /**
     * 信息处理
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String info = (String) msg.obj;
            switch (msg.what) {
                case STATUS_CONNECT:
                    Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT).show();
                    break;
            }

            if (msg.what == 1) {
                mDatas.add(new Bean(info, true));
                catAdapter.notifyDataSetChanged();

            }else {
                mDatas.add(new Bean(info, false));
                catAdapter.notifyDataSetChanged();

        }

    }};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cat_fragment, container, false);
        ButterKnife.inject(this, view);


        init();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (MainActivity.isOpen) {
                Toast.makeText(getActivity(), "连接已经打开，可以通信。如果要再建立连接，请先断开", Toast.LENGTH_SHORT).show();
                return;
            }
            if (MainActivity.mType == MainActivity.Type.CILENT) {
                String address = MainActivity.BlueToothAddress;
                if (!"".equals(address)) {
                    //拿到那个连接的设备
                    mDevice = mBluetoothAdapter.getRemoteDevice(address);
                    mClientThread = new ClientThread();
                    mClientThread.start();
                    MainActivity.isOpen = true;
                } else {
                    Toast.makeText(getActivity(), "address is null !", Toast.LENGTH_SHORT).show();
                }
            } else if (MainActivity.mType == MainActivity.Type.SERVICE) {
                mServerThread = new ServerThread();
                mServerThread.start();
                MainActivity.isOpen = true;
            }
        } else {

        }

    }



    // 客户端线程
    private class ClientThread extends Thread {
        public void run() {
            try {
                mSocket = mDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                Message msg = new Message();
                msg.obj = "请稍候，正在连接服务器:" + MainActivity.BlueToothAddress;
                msg.what = STATUS_CONNECT;
                mHandler.sendMessage(msg);

                mSocket.connect();

                msg = new Message();
                msg.obj = "已经连接上服务端！可以发送信息。";
                msg.what = STATUS_CONNECT;
                mHandler.sendMessage(msg);
                // 启动接受数据
                mReadThread = new ReadThread();
                mReadThread.start();
            } catch (IOException e) {
                Message msg = new Message();
                msg.obj = "连接服务端异常！断开连接重新试一试。";
                msg.what = STATUS_CONNECT;
                mHandler.sendMessage(msg);
            }
        }
    };
    // 开启服务器
    private class ServerThread extends Thread {
        public void run() {
            try {
                // 创建一个蓝牙服务器 参数分别：服务器名称、UUID
                mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(PROTOCOL_SCHEME_RFCOMM,
                        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

                Message msg = new Message();
                msg.obj = "请稍候，正在等待客户端的连接...";
                msg.what = STATUS_CONNECT;
                mHandler.sendMessage(msg);

				/* 接受客户端的连接请求 */
                mSocket = mServerSocket.accept();

                msg = new Message();
                msg.obj = "客户端已经连接上！可以发送信息。";
                msg.what = STATUS_CONNECT;
                mHandler.sendMessage(msg);
                // 启动接受数据
                mReadThread = new ReadThread();
                mReadThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    /* 停止服务器 */
    private void shutdownServer() {
        new Thread() {
            public void run() {
                if (mServerThread != null) {
                    mServerThread.interrupt();
                    mServerThread = null;
                }
                if (mReadThread != null) {
                    mReadThread.interrupt();
                    mReadThread = null;
                }
                try {
                    if (mSocket != null) {
                        mSocket.close();
                        mSocket = null;
                    }
                    if (mServerSocket != null) {
                        mServerSocket.close();
                        mServerSocket = null;
                    }
                } catch (IOException e) {
                    Log.e("server", "mserverSocket.close()", e);
                }
            };
        }.start();
    }
    /* ͣ停止客户端连接 */
    private void shutdownClient() {
        new Thread() {
            public void run() {
                if (mClientThread != null) {
                    mClientThread.interrupt();
                    mClientThread = null;
                }
                if (mReadThread != null) {
                    mReadThread.interrupt();
                    mReadThread = null;
                }
                if (mSocket != null) {
                    try {
                        mSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mSocket = null;
                }
            };
        }.start();
    }
    // 读取数据
    private class ReadThread extends Thread {
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream is = null;
            try {
                is = mSocket.getInputStream();
                while (true) {
                    if ((bytes = is.read(buffer)) > 0) {
                        byte[] buf_data = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            buf_data[i] = buffer[i];
                        }
                        String s = new String(buf_data);
                        Message msg = new Message();
                        msg.obj = s;
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }
    }
    private void init() {
        catftEt.clearFocus();
        mDatas.add(new Bean("会话窗口",false));
        catAdapter = new CatAdapter(getActivity(), mDatas);

        RecyclerView.LayoutManager lr = new LinearLayoutManager(getActivity());
        CatftRl.setLayoutManager(lr);
        CatftRl.setAdapter(catAdapter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//得到蓝牙适配器

    }
    @OnClick({R.id.catft_bn_cut,R.id.catft_bn_send})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.catft_bn_cut:

                if (MainActivity.mType == MainActivity.Type.CILENT) {
                    shutdownClient();
                } else if (MainActivity.mType == MainActivity.Type.SERVICE) {
                    shutdownServer();
                }
                MainActivity.isOpen = false;
                MainActivity.mType = MainActivity.Type.NONE;
                Toast.makeText(getActivity(), "已断开连接！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.catft_bn_send:
                String text = catftEt.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    // 发送信息
                    sendMessageHandle(text);

                    catftEt.setText("");
                    catftEt.clearFocus();
                    // 隐藏软键盘
                    InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(catftEt.getWindowToken(), 0);
                } else
                    Toast.makeText(getActivity(), "发送内容不能为空！", Toast.LENGTH_SHORT).show();

                break;
        }
    }
    // 发送数据
    private void sendMessageHandle(String msg) {
        if (mSocket == null) {
            Toast.makeText(getActivity(), "没有连接", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            OutputStream os = mSocket.getOutputStream();
            os.write(msg.getBytes());

            mDatas.add(new Bean(msg, false));
            catAdapter.notifyDataSetChanged();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (MainActivity.mType == MainActivity.Type.CILENT) {
            shutdownClient();
        } else if (MainActivity.mType == MainActivity.Type.SERVICE) {
            shutdownServer();
        }
        MainActivity.isOpen = false;
        MainActivity.mType = MainActivity.Type.NONE;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
