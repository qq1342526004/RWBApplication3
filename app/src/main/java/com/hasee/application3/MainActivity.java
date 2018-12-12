package com.hasee.application3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hasee.application3.adapter.ListViewAdapter;
import com.hasee.application3.bean.GoodInfo;
import com.hasee.application3.bean.ResponseInfo;
import com.hasee.application3.util.HandlerData;
import com.hasee.application3.util.MyHandler;
import com.hasee.application3.util.MyThread;
import com.hasee.application3.util.ToastUtil;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Context mContext;
    private CheckBox checkBox;
    private EditText editText;//输入框
    private TextView queryButton;//查询按钮
    private TextView sendButton;//发送按钮
    private ListView listView;//列表
    private List<GoodInfo> list = new ArrayList<>();
    private ListViewAdapter adapter;//适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        checkBox = (CheckBox)findViewById(R.id.main_checkBox);
        checkBox.setOnClickListener(onClickListener);
        editText = (EditText) findViewById(R.id.mainActivity_et);
        editText.addTextChangedListener(textWatcher);
        queryButton = (TextView) findViewById(R.id.mainActivity_query_button);
        queryButton.setOnClickListener(onClickListener);
        sendButton = (TextView) findViewById(R.id.mainActivity_send_button);
        sendButton.setOnClickListener(onClickListener);
        listView = (ListView)findViewById(R.id.listView);
//        init();
        adapter = new ListViewAdapter(MainActivity.this,list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClickListener);
    }

    private void init() {
        for (int i = 0; i < 10; i++) {
            GoodInfo goodInfo = new GoodInfo();
            goodInfo.setDcpsPlanID("1");
            goodInfo.setSpsPSJID("1");
            goodInfo.setSpsRoute("1");
            list.add(goodInfo);
        }
        checkBox.setClickable(true);
    }

    /**
     * 编辑框
     */
    public TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            adapter.getMyFilter().filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    /**
     * 事件监听器
     */
    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.main_checkBox://复选框
                    setCheckedAll(checkBox.isChecked());
                    listNotifyDataSetChanged();
                    break;
                case R.id.mainActivity_query_button://查询所有
                    HandlerData.queryAll(handler);
                    break;
                case R.id.mainActivity_send_button://发送
                    JSONArray sendArray = new JSONArray();
                    for (int i = 0; i < list.size(); i++) {
                        if(list.get(i).isFlag()){
                            GoodInfo goodInfo = list.get(i);
                            JSONObject object = new JSONObject();
                            object.put("dcpsAssemblingFlowInfoRemark","APP传入");
                            object.put("spsPSJID",goodInfo.getSpsPSJID());
                            object.put("dcpsPlanID",goodInfo.getDcpsPlanID());
                            sendArray.add(object);
                        }
                    }
//                    Log.d(TAG, "onClick: "+sendArray.toString());
                    HandlerData.send(handler,sendArray);
                    break;
            }
        }
    };

    /**
     * 改变所有复选框状态
     */
    public synchronized void setCheckedAll(boolean isChecked){
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setFlag(isChecked);
        }
    }

    /**
     * listView事件监听
     */
    public AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListViewAdapter.ViewHolder viewHolder = (ListViewAdapter.ViewHolder) view.getTag();
            viewHolder.checkBox.toggle();//改变CheckBox的状态
            list.get(position).setFlag(viewHolder.checkBox.isChecked());//记录状态
        }
    };

    /**
     * 处理服务器返回的数据
     */
    @SuppressLint("HandlerLeak")
    public MyHandler handler = new MyHandler(MainActivity.this) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ResponseInfo responseInfo = (ResponseInfo) msg.obj;
            String message = responseInfo.getMessage();
//            Log.d(TAG, "handleMessage: "+responseInfo.getData());
            switch (msg.what) {
                case MyThread.RESPONSE_SUCCESS: {
                    if ("".equalsIgnoreCase(message)) {//查询显示
                        selectShow(responseInfo.getData());//改变数据集
                        checkBox.setClickable(true);
                        listNotifyDataSetChanged();
                    }else if(getResources().getString(R.string.add_success).equalsIgnoreCase(message)){//删除发送的东西
                        Iterator<GoodInfo> iterator = list.iterator();
                        while (iterator.hasNext()){
                            GoodInfo goodInfo = iterator.next();
                            if(goodInfo.isFlag()){
                                iterator.remove();
                            }
                        }
                        listNotifyDataSetChanged();
                    }
                    ToastUtil.getInstance(mContext).showShortToast(message);
                    break;
                }
                case MyThread.RESPONSE_FAILED: {
                    ToastUtil.getInstance(mContext).showShortToast(message);
                    break;
                }
            }
        }
    };

    /**
     * 更新ListView
     */
    public void listNotifyDataSetChanged(){
        if(list.size() == 0){
            checkBox.setClickable(false);
        }
        adapter.notifyDataSetChanged();
    }


    /**
     * 将所有数据添加至list
     *
     * @param message
     */
    public void selectShow(String message) {
        list.clear();
        JSONArray jsonArray = JSONArray.parseArray(message);
        Log.d(TAG, "selectShow: "+jsonArray.size());
        GoodInfo goodInfo = null;
        for (int i = 0; i < jsonArray.size(); i++) {
            goodInfo = JSONObject.parseObject(jsonArray.getJSONObject(i).toJSONString(),GoodInfo.class);
            list.add(goodInfo);
        }
    }
}
