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
import com.example.afinal.api.CallResetPwd;
import com.example.afinal.api.CallToken;
import com.example.afinal.api.CallUserId;
import com.example.afinal.api.CallWeather;
import com.example.afinal.model.MapResponse;
import com.example.afinal.model.TokenResponse;
import com.example.afinal.model.User;
import com.example.afinal.model.WeatherResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard extends AppCompatActivity {
    Button btnHome, btnLogout, btnGraph, btnMap;
    SharedPreferences sharedPreferences;
    TextView helloText, wind_value, rainfall_value, humidity_value, temperature_value, time_value;
    User userCallApi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        sharedPreferences = getSharedPreferences("dataSignin",MODE_PRIVATE);
        btnLogout = findViewById(R.id.btn_logout);
        btnGraph = findViewById(R.id.btn_graph);
        btnMap = findViewById(R.id.btn_map);
        helloText = findViewById(R.id.account);
        wind_value = findViewById(R.id.wind_speed_value);
        rainfall_value = findViewById(R.id.rain_fall_value);
        humidity_value = findViewById(R.id.humidity_value);
        temperature_value = findViewById(R.id.temperature_value);
        time_value = findViewById(R.id.time);

        userCallApi = new User("user", "123", "");

        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");

        CallToken callToken = ApiClient.CallToken();
        CallWeather callWeather = ApiClient.CallWeather();

        Call<TokenResponse> userCallToken = callToken.sendRequest(
            "password",
            "openremote",
            userCallApi.getUsername(),
            userCallApi.getPassword()
        );
        userCallToken.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.body() != null) {
                    String accessToken = response.body().getAccessToken();
                    userCallApi.setToken(accessToken);
                    Log.d("response call", userCallApi.getToken());

                    Call<WeatherResponse> userCallWeather = callWeather.callWeather(
                        "5zI6XqkQVSfdgOrZ1MyWEf",
                        "Bearer " + userCallApi.getToken()
                    );
                    userCallWeather.enqueue(new Callback<WeatherResponse>() {
                        @Override
                        public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                            if (response.body() != null) {
                                WeatherResponse weatherResponse = response.body();
                                setAttributeValues(weatherResponse);
                            } else {
                                Log.d("response call", "lỗi");
                            }
                        }
                        @Override
                        public void onFailure(Call<WeatherResponse> call, Throwable t) {
                            Log.d("response call", t.getMessage().toString());
                        }
                    });
                } else {
                    Log.d("response call", "lỗi");
                }
            }
            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Log.d("response call", t.getMessage().toString());
            }
        });

        helloText.setText("Hello, " + user.getUsername());

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, MapScreen.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
        btnGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Graph.class);
                intent.putExtra("user", user);
                intent.putExtra("userAPI", userCallApi);
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
    private void setAttributeValues(WeatherResponse weatherResponse) {
        if (weatherResponse != null) {

            WeatherResponse.Attributes attributes = weatherResponse.getAttributes();
            if (attributes != null) {
                WeatherResponse.Attribute humidity = attributes.getHumidity();
                WeatherResponse.Attribute temperature = attributes.getTemperature();
                WeatherResponse.Attribute rainfall = attributes.getRainfall();
                WeatherResponse.Attribute windSpeed = attributes.getWindSpeed();

                if(humidity != null) {
                    String value = humidity.getValue();
                    humidity_value.setText(value + " %");
                }
                if(temperature != null) {
                    String value = temperature.getValue();
                    long timestamp = temperature.getTimestamp();
                    String formattedTime = convertTimestampToTime(timestamp);
                    time_value.setText(formattedTime);
                    temperature_value.setText(value + " C");
                }
                if(rainfall != null) {
                    String value = rainfall.getValue();
                    rainfall_value.setText(value + " mm");
                }
                if(windSpeed != null) {
                    String value = windSpeed.getValue();
                    wind_value.setText(value + " km/h");
                }
            }
        }
    }
    public static String convertTimestampToTime(long timestamp) {
        // Chuyển đổi timestamp thành Date
        Date date = new Date(timestamp);

        // Định dạng ngày để chỉ hiển thị giờ và phút
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        // Trả về chuỗi thời gian đã định dạng
        return sdf.format(date);
    }
}



