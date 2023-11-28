package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.afinal.model.User;

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

        textView.setText(user.getUsername());
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Home.class);
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
}



