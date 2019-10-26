package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class Signin extends AppCompatActivity implements View.OnClickListener {

    private EditText etPhoneNumber;    // 电话号码
    private Button sendVerificationCode;  // 发送验证码
    private EditText etVerificationCode;  // 验证码
    private Button nextStep;        // 下一步

    private String phoneNumber;     // 电话号码
    private String verificationCode;  // 验证码

    private boolean flag;  // 操作是否成功
    public static Signin instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_signin);


        init();

        EventHandler eventHandler = new EventHandler(){    // 操作回调
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);   // 注册回调接口
    }
    @SuppressLint("WrongViewCast")
    private void init() {
        etPhoneNumber = (EditText) findViewById(R.id.editText);
        sendVerificationCode = (Button) findViewById(R.id.button2);
        etVerificationCode = (EditText) findViewById(R.id.editText2);
        sendVerificationCode.setOnClickListener((View.OnClickListener) this);
        nextStep = (Button) findViewById(R.id.submit);
        nextStep.setOnClickListener((View.OnClickListener) this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button2:
                if (!TextUtils.isEmpty(etPhoneNumber.getText())) {
                    if (etPhoneNumber.getText().length() == 11) {
                        phoneNumber = etPhoneNumber.getText().toString();
                        SMSSDK.getVerificationCode("86", phoneNumber); // 发送验证码给号码的 phoneNumber 的手机
                        etVerificationCode.requestFocus();
                    }
                    else {
                        Toast.makeText(this, "请输入完整的电话号码", Toast.LENGTH_SHORT).show();
                        etPhoneNumber.requestFocus();
                    }
                } else {
                    Toast.makeText(this, "请输入电话号码", Toast.LENGTH_SHORT).show();
                    etPhoneNumber.requestFocus();
                }
                break;

            case R.id.submit:
                if (!TextUtils.isEmpty(etVerificationCode.getText())) {
                    if (etVerificationCode.getText().length() == 6) {
                        verificationCode = etVerificationCode.getText().toString();
                        SMSSDK.submitVerificationCode("86", phoneNumber, verificationCode);
                        flag = false;
                    } else {
                        Toast.makeText(this, "请输入完整的验证码", Toast.LENGTH_SHORT).show();
                        etVerificationCode.requestFocus();
                    }
                } else {
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    etVerificationCode.requestFocus();
                }
                break;

            default:
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;

            if (result == SMSSDK.RESULT_COMPLETE) {
                // 如果操作成功
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    // 校验验证码，返回校验的手机和国家代码
                    Toast.makeText(getApplicationContext(), "验证成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Map.class);
                    EditText ed1 = (EditText) findViewById(R.id.editText);
                    String phone = ed1.getText().toString();
                    MyApplication application = (MyApplication) getApplicationContext();
                    application.setPhone(phone);

                    SharedPreferences sp = getSharedPreferences("login", getApplicationContext().MODE_PRIVATE);
                    sp.edit()
                            .putString("phone", phone)
                            .apply();

                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    // 获取验证码成功，true为智能验证，false为普通下发短信
                    Toast.makeText(getApplicationContext(), "验证码已发送", Toast.LENGTH_SHORT).show();
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    // 返回支持发送验证码的国家列表
                }
            } else {
                // 如果操作失败
                if (flag) {
                    Toast.makeText(getApplicationContext(), "验证码获取失败，请重新获取", Toast.LENGTH_SHORT).show();
                    etPhoneNumber.requestFocus();
                } else {
                    ((Throwable) data).printStackTrace();
                    Toast.makeText(getApplicationContext(), "手机号格式错误", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler(); // 注销回调接口
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}
