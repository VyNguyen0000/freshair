package com.example.afinal;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.afinal.model.User;

import java.util.Locale;

public class Home extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    User user = new User();
    String a;
    Button btn_resetPwd, btnSignin, btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_home);
        btn_resetPwd = findViewById(R.id.btnReset);
        btnSignin = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        sharedPreferences = getSharedPreferences("dataSignin",MODE_PRIVATE);
        a = sharedPreferences.getString("username","");

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a.equals(""))
                {
                    Intent intent = new Intent(Home.this, Signin.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(Home.this, Dashboard.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Signup.class);
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
        btn_resetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Repwd.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    private void showChangeLanguageDialog() {
        //array of language to display in alert dialog
        final String[] listItems = {getString(R.string.lang_vietnamese), getString(R.string.lang_english)};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Home.this);
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
