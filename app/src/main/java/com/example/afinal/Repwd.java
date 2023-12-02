package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.afinal.api.ApiClient;
import com.example.afinal.api.CallToken;
import com.example.afinal.model.TokenResponse;
import com.example.afinal.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repwd extends AppCompatActivity {
    WebView webview;
    ImageButton back_btn, reset_btn;
    EditText user_editText, pwd_editText, rePwd_editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repwd);

        webview = findViewById(R.id.webView);
        user_editText = findViewById(R.id.edit_text_name);
        pwd_editText = findViewById(R.id.edit_text_password);
        rePwd_editText = findViewById(R.id.edit_text_confirm_password);
        back_btn = findViewById(R.id.backBtn);
        reset_btn = findViewById(R.id.resetBtn);
        webview.setVisibility(View.GONE);

        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Repwd.this, Home.class);
                startActivity(intent);
            }
        });

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}