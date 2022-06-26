package com.example.aircraftwar;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aircraftwar.data.account.User;
import com.example.aircraftwar.data.account.UserManager;


public class LoginActivity extends AppCompatActivity {

    /**管理当前用户*/
    public static User currentUser;
    public static UserManager userManager;
    private EditText etName;
    private EditText etPwd;
    public static String name_ = "yhp";
    private ProgressDialog progressDialog;
    //设置用户名和密码,可以自动登录等功能
    private String username;
    private String password;
    private Button btLogin;
    private Button ibTOSignUp;
    /**实现自动登录*/
    private SharedPreferences sp;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent = getIntent();
        userManager = new UserManager();
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        etName = (EditText)findViewById(R.id.et_name_);
        etPwd = (EditText)findViewById(R.id.et_pwd_);
        btLogin = findViewById(R.id.bt_login);
        ibTOSignUp = findViewById(R.id.ib_toSignUp);

        ibTOSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        if(sp.getBoolean("ISCHECK", false))
        {
            etName.setText(sp.getString("USER_NAME", ""));
            etPwd.setText(sp.getString("PASSWORD", ""));
            name_ = sp.getString("USER_NAME", "");
            //判断自动登陆多选框状态
            if(sp.getBoolean("AUTO_ISCHECK", false))
            {
                //跳转界面
                login();
            }
        }

        if(intent.getExtras() != null){
            username = intent.getExtras().getString("username");
            password = intent.getExtras().getString("password");
            etName.setText(username);
            etPwd.setText(password);
        }

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });


    }

    private void login(){
        /**登录校验*/
        username = etName.getText().toString().trim();
        password = etPwd.getText().toString().trim();

        //创建本地数据

        //加载数据进本地
        try {
            userManager.load();
        }catch (Exception e){}

        User user = userManager.find(username);
        if(user == null){
            Toast.makeText(LoginActivity.this,"无此用户,请注册", Toast.LENGTH_SHORT).show();
        }

        else if(!user.getPassword().equals(password)){
            Toast.makeText(LoginActivity.this,"用户名或密码错误!", Toast.LENGTH_SHORT).show();
        }else {
            currentUser = user;
            success();
        }

    }

    private void success(){
        /**登录成功*/
        Toast.makeText(LoginActivity.this,"登录成功,正在进入游戏...",
                Toast.LENGTH_SHORT).show();
        //记住用户名、密码、
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("USER_NAME", username);
        editor.putString("PASSWORD",password);
        editor.commit();
        name_ = currentUser.getUserName();

        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, ModeSelActivity.class);
        startActivity(intent);
    }


}

