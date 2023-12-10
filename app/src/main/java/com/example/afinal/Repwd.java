package com.example.afinal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.afinal.api.ApiClient;
import com.example.afinal.api.CallResetPwd;
import com.example.afinal.api.CallToken;
import com.example.afinal.api.CallUserId;
import com.example.afinal.model.PasswordResetRequest;
import com.example.afinal.model.TokenResponse;
import com.example.afinal.model.User;
import com.example.afinal.model.UserResponse;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repwd extends AppCompatActivity {
    WebView webview;
    RelativeLayout loading;
    ImageButton back_btn, reset_btn;
    EditText user_name, old_pwd, new_pwd, confirm_pwd;

    User user, admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repwd);

        webview = findViewById(R.id.webView);
        loading = findViewById(R.id.loading);
        user_name = findViewById(R.id.edit_text_name);
        old_pwd = findViewById(R.id.edit_oldPwd);
        new_pwd = findViewById(R.id.edit_new_pwd);
        confirm_pwd = findViewById(R.id.edit_confirm);
        back_btn = findViewById(R.id.backBtn);
        reset_btn = findViewById(R.id.resetBtn);

        webview.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);


        user = new User();
        admin = new User("admin", "1", "");

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Repwd.this, Home.class);
                startActivity(intent);
            }
        });
        Button changeLang = findViewById(R.id.changeMyLang);
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show AlertDialog to display list of language, one can be selected
                showChangeLanguageDialog();
            }
        });

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setUsername(user_name.getText().toString());
                user.setPassword(old_pwd.getText().toString());
                CallToken callToken = ApiClient.CallToken();
                CallUserId callUserId = ApiClient.CallUserId();
                CallResetPwd callResetPwd = ApiClient.CallRePwd();

                Call<TokenResponse> userCallToken = callToken.sendRequest(
                    "password",
                    "openremote",
                    user.getUsername(),
                    user.getPassword()
                );
                userCallToken.enqueue(new Callback<TokenResponse>() {
                    @Override
                    public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                        if (response.body() != null) {
                            String accessToken = response.body().getAccessToken();
                            user.setToken(accessToken);
                            Log.d("responsecall usertoken" ,user.getToken());

//                          user nhận được token đến admin call
                            Call<TokenResponse> adminCallToken = callToken.sendRequest(
                                "password",
                                "openremote",
                                admin.getUsername(),
                                admin.getPassword()
                            );
                            adminCallToken.enqueue(new Callback<TokenResponse>() {
                                @Override
                                public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                                    if (response.body() != null) {
                                        String accessToken = response.body().getAccessToken();
                                        admin.setToken(accessToken);
                                        Log.d("responsecall admin token" ,admin.getToken());
//                                      Nhận được 2 token của user và admin, call user id của user
                                        Call<UserResponse> userCallId = callUserId.getUserId("Bearer " + user.getToken());
                                        userCallId.enqueue(new Callback<UserResponse>() {
                                            @Override
                                            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                                                if (response.body() != null) {
                                                    String userid = response.body().getId();
                                                    user.setId(userid);
                                                    Log.d("responsecall" ,user.getId());
                                                    PasswordResetRequest passwordResetRequest = new PasswordResetRequest(
                                                            "String",
                                                            new_pwd.getText().toString(),
                                                            true
                                                    );
//                                                  Nhận được userid bắt đầu đổi pass
                                                    Call<Void> admincallRepwd = callResetPwd.resetPassword(
                                                        user.getId(),
                                                        "Bearer " + admin.getToken(),
                                                        passwordResetRequest
                                                    );
                                                    admincallRepwd.enqueue(new Callback<Void>() {
                                                        @Override
                                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                                            Log.d("responsecall", "yêu cầu đổi pass thành công");
                                                            user.setPassword(new_pwd.getText().toString());
//                                                           Thành công và bắt đầu nhúng web view
                                                            loading.setVisibility((View.VISIBLE));
                                                            webview.setVisibility(View.VISIBLE);
                                                            WebSettings webSettings = webview.getSettings();
                                                            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
                                                            CookieManager.getInstance().removeAllCookies(null);
                                                            CookieManager.getInstance().flush();
                                                            webSettings.setJavaScriptEnabled(true);
                                                            webview.loadUrl("https://uiot.ixxc.dev");
                                                            webview.setWebViewClient(new WebViewClient() {
                                                                boolean canIntent = false;
                                                                @Override
                                                                public void onPageFinished(WebView view, String url) {
                                                                    if (url.contains("openid-connect/auth")) {
                                                                        canIntent = true;
                                                                        String javascript =
                                                                            "document.forms[0].elements['username'].value = '" + user.getUsername() + "';" +
                                                                            "document.forms[0].elements['password'].value = '" + user.getPassword() + "';" +
                                                                            "document.forms[0].submit();";
                                                                        view.evaluateJavascript(javascript, null);
                                                                    }
                                                                    if (url.contains("login-actions/required-action")) {
                                                                        String javascript =
                                                                            "document.forms[0].elements['password-new'].value = '" + user.getPassword() + "';" +
                                                                            "document.forms[0].elements['password-confirm'].value = '" + user.getPassword() + "';" +
                                                                            "document.forms[0].submit();";
                                                                        view.evaluateJavascript(javascript, null);
                                                                    }
                                                                    if (url.contains("manager") && canIntent == true) {
                                                                        Intent intent = new Intent(Repwd.this, Home.class);
                                                                        startActivity(intent);
                                                                    }
                                                                }
                                                            });
                                                        }
                                                        @Override
                                                        public void onFailure(Call<Void> call, Throwable t) {
                                                            Log.d("responsecall", "yêu cầu đổi pass thất bại");
                                                        }
                                                    });
                                                }
                                                else {
                                                    Log.d("responsecall" ,"body rỗng");
                                                }
                                            }
                                            @Override
                                            public void onFailure(Call<UserResponse> call, Throwable t) {
                                                Log.d("responsecall", t.getMessage().toString());
                                            }
                                        });
                                    }
                                    else {
                                        Log.d("responsecall" ,"body rỗng");
                                    }
                                }
                                @Override
                                public void onFailure(Call<TokenResponse> call, Throwable t) {
                                    Log.d("responsecall", t.getMessage().toString());
                                }
                            });
                        }
                        else {
                            Log.d("responsecall" ,"body rỗng");
                        }
                    }
                    @Override
                    public void onFailure(Call<TokenResponse> call, Throwable t) {
                        Log.d("responsecall", t.getMessage().toString());
                    }
                });
            }
        });
    }
    private void showChangeLanguageDialog() {
        //array of language to display in alert dialog
        final String[] listItems = {getString(R.string.lang_vietnamese), getString(R.string.lang_english)};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Repwd.this);
        mBuilder.setTitle("Choose Language");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    //English
                    setLocale("VI");
                    recreate();
                } else if (i == 1) {
                    setLocale("EN");
                    recreate();
                }

            }

        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        // Lưu ngôn ngữ vào SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    //load language saved in shared prpubliceferences
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings",MODE_PRIVATE);
        String language = prefs.getString("My_Lang"," ");
        setLocale(language);
    }

}