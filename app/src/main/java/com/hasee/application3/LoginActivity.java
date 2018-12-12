package com.hasee.application3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hasee.application3.bean.ResponseInfo;
import com.hasee.application3.dialog.CustomProgress;
import com.hasee.application3.util.HandlerData;
import com.hasee.application3.util.HttpRequestClient;
import com.hasee.application3.util.MyHandler;
import com.hasee.application3.util.MyThread;
import com.hasee.application3.util.ToastUtil;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private Context mContext;
    private EditText userNameEt;//用户名输入库
    private EditText passWordEt;//密码输入框
    private EditText ipAddressEt;//ip输入框
    private Button loginButton;//登陆按钮
    private CustomProgress customProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        mContext = getApplicationContext();
        userNameEt = (EditText)findViewById(R.id.loginLayout_userName_et);
        passWordEt = (EditText)findViewById(R.id.loginLayout_passWord_et);
        //默认账号
        userNameEt.setText("123");
        passWordEt.setText("1");

        ipAddressEt = (EditText)findViewById(R.id.loginLayout_ipAddress_et);
        ipAddressEt.setText(App.getInstance().getPreferences().trim());
        loginButton = (Button)findViewById(R.id.loginLayout_login_button);
        loginButton.setOnClickListener(onClickListener);
    }

    /**
     * 点击事件监听器
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.loginLayout_login_button:
                    if(isEmpty()){
                        customProgress = CustomProgress.show(LoginActivity.this,
                                getResources().getString(R.string.loading),false,null);
                        String userName = userNameEt.getText().toString().trim();
                        String passWord = passWordEt.getText().toString().trim();
                        String ipAddress = ipAddressEt.getText().toString().trim();
                        HttpRequestClient.refresh(ipAddress);//更新ip地址
                        HandlerData.login(handler,userName,passWord);
                    }else{
                        ToastUtil.getInstance(mContext).showShortToast(getResources().getString(R.string.login_isEmpty));
                    }
                    break;
            }
        }
    };

    /**
     * 判断输入框是否为空
     * @return
     */
    private boolean isEmpty(){
        if("".equalsIgnoreCase(userNameEt.getText().toString().trim())
                ||"".equalsIgnoreCase(passWordEt.getText().toString().trim())
                ||"".equalsIgnoreCase(ipAddressEt.getText().toString().trim())){
            return false;
        }
        return true;
    }

    /**
     * 处理登陆结果
     */
    public MyHandler handler = new MyHandler(LoginActivity.this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            customProgress.dismiss();
            ResponseInfo response = (ResponseInfo) msg.obj;
            switch (msg.what){
                case MyThread.RESPONSE_FAILED://失败
                    ToastUtil.getInstance(mContext).
                            showShortToast(response.getMessage());
                    break;
                case MyThread.RESPONSE_SUCCESS://登陆成功
                    ToastUtil.getInstance(mContext).
                            showShortToast(response.getMessage());
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };
}
