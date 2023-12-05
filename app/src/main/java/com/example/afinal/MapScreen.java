package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.afinal.api.ApiClient;
import com.example.afinal.api.CallMap;
import com.example.afinal.api.CallToken;
import com.example.afinal.model.MapResponse;
import com.example.afinal.model.TokenResponse;
import com.example.afinal.model.User;
import com.example.afinal.model.UserResponse;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapScreen extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    SupportMapFragment mapFragment;
    User userCallApi, user;

    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        Log.d("user", user.getUsername());
        Log.d("user", user.getPassword());

        userCallApi = new User("user", "123", "");
        btn = findViewById(R.id.cometoDashboard_btn);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapScreen.this, Dashboard.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        fetchDataFromApi();
    }
    private void fetchDataFromApi() {
        CallToken callToken = ApiClient.CallToken();
        Call<TokenResponse> usercallToken = callToken.sendRequest(
                "password",
                "openremote",
                userCallApi.getUsername(),
                userCallApi.getPassword()
        );
        usercallToken.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.body() != null) {
                    String accessToken = response.body().getAccessToken();
                    userCallApi.setToken(accessToken);

                    Log.d("Failure on call map", userCallApi.getToken());

                    CallMap callMap = ApiClient.CallMap();
                    Call<MapResponse> userCallMap = callMap.getMapData(
                        "Bearer " + userCallApi.getToken()
                    );

                    userCallMap.enqueue(new Callback<MapResponse>() {
                        @Override
                        public void onResponse(Call<MapResponse> call, Response<MapResponse> response) {
                            if (response.isSuccessful()) {
                                MapResponse mapResponse = response.body();
                                setMapValues(mapResponse);
                            } else {
                                // Xử lý khi có lỗi trong response
                            }
                        }
                        @Override
                        public void onFailure(Call<MapResponse> call, Throwable t) {
                            Log.d("Failure on call map", "onFailure");
                        }
                    });
                }
                else {
                    Log.d("Failure on call map", "Không có dữ liệu");
                }
            }
            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Log.d("Failure on call map", "onFailure");
            }
        });

    }
    private void setMapValues(MapResponse mapResponse) {
        if (mapResponse != null) {
            MapResponse.Options options = mapResponse.getOptions();
            if (options != null) {
                MapResponse.DefaultOptions defaultOptions = options.getDefaultOptions();
                if (defaultOptions != null) {
                    double[] center = defaultOptions.getCenter();
                    double[] bounds = defaultOptions.getBounds();
                    float zoom = defaultOptions.getZoom();
                    float minZoom = defaultOptions.getMinZoom();
                    float maxZoom = defaultOptions.getMaxZoom();
                    boolean boxZoom = defaultOptions.getBoxZoom();

                    // Thiết lập trung tâm của bản đồ
                    LatLng mapCenter = new LatLng(center[1], center[0]);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mapCenter));

                    // Thiết lập giới hạn của bản đồ
                    LatLngBounds mapBounds = new LatLngBounds(
                            new LatLng(bounds[1], bounds[0]),  // Tọa độ tối thiểu (dưới cùng bên trái)
                            new LatLng(bounds[3], bounds[2])   // Tọa độ tối đa (trên cùng bên phải)
                    );
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBounds, 0));

                    // Thiết lập cấp độ thu phóng
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));

                    // Tắt chức năng box zoom
                    mMap.getUiSettings().setCompassEnabled(boxZoom);

                    // Thiết lập minZoom và maxZoom
                    mMap.setMinZoomPreference(minZoom); // Giá trị minZoom
                    mMap.setMaxZoomPreference(maxZoom); // Giá trị maxZoom
                }
            }
        }
    }
}