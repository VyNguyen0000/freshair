package com.example.afinal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.afinal.api.ApiClient;
import com.example.afinal.api.ApiService;
import com.example.afinal.model.RequestModel;
import com.example.afinal.model.ResponseModel;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Signup extends AppCompatActivity {
    ImageButton backHomeBtn, signUpBtn;
    EditText user, email, pwd, rePwd;
    WebView webview;
    RelativeLayout loading;
    public boolean temp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        backHomeBtn = findViewById(R.id.homeBtn);
        user = findViewById(R.id.edit_text_name);
        email = findViewById(R.id.edit_text_gmail);
        pwd = findViewById(R.id.edit_text_password);
        rePwd = findViewById(R.id.edit_text_confirm_password);
        signUpBtn = findViewById(R.id.signUp);
        webview = findViewById(R.id.webView_signup);
        loading = findViewById(R.id.loading);
        webview.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
        backHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, Home.class);
                startActivity(intent);
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                call khi đúng cú pháp
                if (canRegister() == true) {
                    ApiService apiService = ApiClient.createService();
                    RequestModel requestModel = new RequestModel(user.getText().toString(), pwd.getText().toString());
                    Call<ResponseModel> call = apiService.sendRequest(
                            "password",
                            "openremote",
                            requestModel.getUsername(),
                            requestModel.getPassword()
                    );
                    call.enqueue(new Callback<ResponseModel>() {
                        @Override
                        public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                            if (response.body() != null) {
                                showAlert("Username already exists.");
                            } else {
                                loading.setVisibility(View.VISIBLE);
                                webview.setVisibility(View.VISIBLE);
                                WebSettings webSettings = webview.getSettings();
                                webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
                                CookieManager.getInstance().removeAllCookies(null);
                                CookieManager.getInstance().flush();
                                webSettings.setJavaScriptEnabled(true);
                                webview.loadUrl("https://uiot.ixxc.dev");
                                signUpOnWebView(webview);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseModel> call, Throwable t) {
                            Log.d("response call", t.getMessage().toString());
                        }
                    });
                } else {
                    if (checkUser() == false) {
                        showAlert("Please fill username");
                    } else if (checkUser() == false) {
                        showAlert("Please fill email");
                    } else if (isValidEmail() == false) {
                        showAlert("Invalid email address");
                    } else if (checkPwd() == false) {
                        showAlert("Invalid email address");
                    } else if (checkRePwd() == false) {
                        showAlert("Password confirmation doesn't match");
                    }
                }
            }
        });

    }

    private boolean checkPwd() {
        if (pwd.getText().toString().equals(rePwd.getText().toString())) return true;
        return false;
    }

    private boolean checkRePwd() {
        if (pwd.getText().toString().equals(rePwd.getText().toString())) return true;
        return false;
    }

    private boolean checkUser() {
        if (user.getText().toString().equals("")) return false;
        return true;
    }

    boolean checkEmail() {
        if (email.getText().toString().equals("")) return false;
        return true;
    }

    public boolean isValidEmail() {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email.getText().toString());
        return matcher.matches();
    }

    private boolean canRegister() {
        if (
                checkRePwd() == true
                        && checkUser() == true
                        && checkEmail() == true
                        && isValidEmail() == true
                        && checkPwd() == true
        ) return true;
        return false;
    }

    private void showAlert(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
        builder.setTitle("Error")
                .setMessage(text)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void signUpOnWebView(WebView webview) {
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("openid-connect/auth")) {
                    Log.d("finished - yess", "sign up");
                    view.loadUrl("javascript:(function() { " +
                            "document.getElementsByTagName('a')[0].click();" +
                            "})()");
                }

                if (url.contains("login-actions/registration")) {
                    temp = true;
                    String javascript =
                            "document.getElementById('username').value ='" + user.getText().toString() + "';" +
                                    "document.getElementById('email').value ='" + email.getText().toString() + "';" +
                                    "document.getElementById('password').value ='" + pwd.getText().toString() + "';" +
                                    "document.getElementById('password-confirm').value ='" + pwd.getText().toString() + "';" +
                                    "document.getElementById('kc-register-form').submit();";
                    view.evaluateJavascript(javascript, null);
                }
                if (url.contains("manager") && temp == true) {
                    Log.d("finished - yess", "complete");
                    Intent intent = new Intent(Signup.this, Dashboard.class);
                    startActivity(intent);
                }
            }
        });

    }
}
