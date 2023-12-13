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
import java.util.Locale;

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

        String hello = getResources().getString(R.string.hello);


        helloText.setText(hello +" " + sharedPreferences.getString("username",""));

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, MapScreen.class);
                intent.putExtra("user", user);
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
    private void showChangeLanguageDialog() {
        //array of language to display in alert dialog
        final String[] listItems = {getString(R.string.lang_vietnamese), getString(R.string.lang_english)};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Dashboard.this);
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




