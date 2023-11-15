package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Webview extends AppCompatActivity {
    public boolean temp = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        WebView webView = findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("https://uiot.ixxc.dev");
        signUpOnWebView(webView);
    }

    private void signUpOnWebView (WebView webview) {
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if(url.contains("openid-connect/auth")) {
                    Log.d("finished - yess", "sign up");
                    view.loadUrl("javascript:(function() { " +
                            "document.getElementsByTagName('a')[0].click();" +
                            "})()");
                }

                if (url.contains("login-actions/registration")) {
                    temp = true;
                    String javascript = "document.getElementById('username').value = 'abcdefghii';" +
                                        "document.getElementById('email').value = 'abcdefghii@gmail.com';" +
                                        "document.getElementById('password').value = 'YourPassword';" +
                                        "document.getElementById('password-confirm').value = 'YourPassword';" +
                                        "document.getElementById('kc-register-form').submit();";
                    view.evaluateJavascript(javascript, null);
                }
                if (url.contains("manager") && temp == true) {
                    Log.d("finished - yess", "complete");
                    Intent intent = new Intent(Webview.this, Dashboard.class);
                    startActivity(intent);
                }
            }
        });

    }
}

