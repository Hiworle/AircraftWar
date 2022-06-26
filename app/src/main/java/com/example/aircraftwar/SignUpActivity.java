package com.example.aircraftwar;

import static com.example.aircraftwar.LoginActivity.userManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aircraftwar.data.account.User;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    //用户名
    EditText userNameTxt;
    //密码
    EditText passwordTxt;
    //确认密码
    EditText passwordTxtAgain;

    //注册按钮
    Button registerBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        System.out.println("===================");
        System.out.println(getFilesDir().toString());
//        Toast.makeText(this, getFilesDir().toString(), Toast.LENGTH_LONG).show();
//        System.out.println("===================");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initView();

    }

    private void initView() {
        //初始化各个组件
        userNameTxt =(EditText)findViewById(R.id.et_name);
        passwordTxt = (EditText) findViewById(R.id.et_pwd);
        registerBtn = (Button) findViewById(R.id.bt_signUp);
        passwordTxtAgain = (EditText) findViewById(R.id.et_confirmPwd);
        registerBtn.setOnClickListener(this);
    }

    public void onClick(View view) {
        String username = userNameTxt.getText().toString().trim();
        String password = passwordTxt.getText().toString().trim();
        String confPwd = passwordTxtAgain.getText().toString().trim();
        //注册验证
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confPwd)) {
            //用TextUtils.equals（）不用string的equals，TextUtils.equals（）多了非空判断
            if (TextUtils.equals(confPwd,password)) {

                //初始化userManager

                //生成输入,输出数据流
                //加载用户数据
                try {
                    userManager.load();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //判断输入数据是否合格
                if(userManager.find(username) != null){
                    Toast.makeText(this,  "用户名已存在", Toast.LENGTH_SHORT).show();
                    //三个输入栏置空
                    userNameTxt.setText("");
                    passwordTxt.setText("");
                    passwordTxtAgain.setText("");
                }else{
                    User user = new User(username, password);
                    userManager.add(user);
                    Toast.makeText(this,  "注册成功", Toast.LENGTH_SHORT).show();
                    //将新用户数据保存至本地
                    try {
                        userManager.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //返回登录界面
                    Intent intent = new Intent(this, LoginActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("password", password);
                    bundle.putString("username", username);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            } else {
                Toast.makeText(this, "两次输入的密码不相同,请检查", Toast.LENGTH_SHORT).show();
                //确认密码栏置空
                passwordTxtAgain.setText("");
            }
        }else {
            Toast.makeText(this, "请输入完整", Toast.LENGTH_SHORT).show();
        }

    }
}