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
import com.example.afinal.api.CallToken;
import com.example.afinal.model.TokenResponse;
import com.example.afinal.model.User;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Signup extends AppCompatActivity {
    ImageButton backHomeBtn, signUpBtn;
    EditText user_editText, email, pwd, rePwd;
    WebView webview;
    RelativeLayout loading;
    public boolean temp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        backHomeBtn = findViewById(R.id.homeBtn);
        user_editText = findViewById(R.id.edit_text_name);
        email = findViewById(R.id.edit_text_gmail);
        pwd = findViewById(R.id.edit_text_password);
        rePwd = findViewById(R.id.edit_text_confirm_password);
        signUpBtn = findViewById(R.id.signUp);
        webview = findViewById(R.id.webView_signup);
        loading = findViewById(R.id.loading);
        webview.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);

        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");
        backHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, Home.class);
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
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                call khi đúng cú pháp
                if (canRegister() == true) {
                    CallToken apiService = ApiClient.CallToken();
                    user.setUsername(user_editText.getText().toString());
                    user.setPassword(pwd.getText().toString());
                    user.setEmail(email.getText().toString());
                    Call<TokenResponse> call = apiService.sendRequest(
                        "password",
                        "openremote",
                        user.getUsername(),
                        user.getPassword()
                    );
                    call.enqueue(new Callback<TokenResponse>() {
                        @Override
                        public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                            if (response.body() != null) {

                                showAlert(getResources().getString(R.string.exist));
                            } else {
                                loading.setVisibility(View.VISIBLE);
                                webview.setVisibility(View.VISIBLE);
                                WebSettings webSettings = webview.getSettings();
                                webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
                                CookieManager.getInstance().removeAllCookies(null);
                                CookieManager.getInstance().flush();
                                webSettings.setJavaScriptEnabled(true);
                                webview.loadUrl("https://uiot.ixxc.dev");
                                signUpOnWebView(webview, user);
                            }
                        }

                        @Override
                        public void onFailure(Call<TokenResponse> call, Throwable t) {
                            Log.d("response call", t.getMessage().toString());
                        }
                    });
                } else {
                    if (checkUser() == false) {

                        showAlert(getResources().getString(R.string.fill1));

                    } else if (checkUser() == false) {
                        showAlert(getResources().getString(R.string.fill2));
                    } else if (isValidEmail() == false) {
                        showAlert(getResources().getString(R.string.e));
                    } else if (checkPwd() == false) {
                        showAlert(getResources().getString(R.string.pwd));
                    } else if (checkRePwd() == false) {
                        showAlert(getResources().getString(R.string.fill3));
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
        if (user_editText.getText().toString().equals("")) return false;
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

    private void signUpOnWebView(WebView webview, User user) {
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
                        "document.getElementById('username').value ='" + user_editText.getText().toString() + "';" +
                            "document.getElementById('email').value ='" + email.getText().toString() + "';" +
                            "document.getElementById('password').value ='" + pwd.getText().toString() + "';" +
                            "document.getElementById('password-confirm').value ='" + pwd.getText().toString() + "';" +
                            "document.getElementById('kc-register-form').submit();";
                    view.evaluateJavascript(javascript, null);
                }
                if (url.contains("manager") && temp == true) {
                    Log.d("finished - yess", "complete");
                    Intent intent = new Intent(Signup.this, MapScreen.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            }
        });
    }
    private void showChangeLanguageDialog() {
        //array of language to display in alert dialog
        final String[] listItems = {getString(R.string.lang_vietnamese), getString(R.string.lang_english)};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Signup.this);
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

