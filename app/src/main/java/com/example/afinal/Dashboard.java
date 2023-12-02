package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.afinal.api.ApiClient;
import com.example.afinal.api.CallToken;
import com.example.afinal.model.TokenResponse;
import com.example.afinal.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard extends AppCompatActivity {
    Button btnBack, btnLogout;
    SharedPreferences sharedPreferences;
    TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        sharedPreferences = getSharedPreferences("dataSignin",MODE_PRIVATE);
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btn_logOut);
        textView = findViewById(R.id.text);

        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");

        callToken(user);

        textView.setText(user.getUsername());
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Home.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", "");
                editor.putString("password", "");
                editor.commit();
                Intent intent = new Intent(Dashboard.this, Home.class);

                startActivity(intent);
            }

        });
    }

    private void callToken(User user) {
        CallToken apiService = ApiClient.CreateCallToken();
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
                    String accessToken = response.body().getAccessToken();
                    Log.d("response call", accessToken);
                } else {
                    Log.d("response call", "lỗi");
                }
            }
            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Log.d("response call", t.getMessage().toString());
            }
        });
    }
}



