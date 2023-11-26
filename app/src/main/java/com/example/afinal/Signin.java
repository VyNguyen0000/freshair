package com.example.afinal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.afinal.api.ApiClient;
import com.example.afinal.api.CallToken;
import com.example.afinal.model.RequestModel;
import com.example.afinal.model.ResponseModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signin extends AppCompatActivity {
    ImageButton backHomeBtn, signInBtn;
    EditText user, pwd;
    CheckBox cbRemember;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        sharedPreferences = getSharedPreferences("dataSignin",MODE_PRIVATE);
        backHomeBtn = findViewById(R.id.homeBtn);
        signInBtn = findViewById(R.id.sign_in_btn);
        user = findViewById(R.id.edit_text_name);
        pwd = findViewById(R.id.edit_text_password);
        cbRemember = findViewById(R.id.cbRemember);
        user.setText(sharedPreferences.getString("username",""));
        pwd.setText(sharedPreferences.getString("password",""));
        cbRemember.setChecked(sharedPreferences.getBoolean("checked",false));
        backHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signin.this, Home.class);
                startActivity(intent);
            }
        });
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(canSignIn() == true) {
                    CallToken apiService= ApiClient.CreateCallToken();
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
                            String username=user.getText().toString().trim();
                            String password=pwd.getText().toString().trim();
                            if (response.body() != null) {
                                Intent intent = new Intent(Signin.this, Dashboard.class);
                                startActivity(intent);
                                if (cbRemember.isChecked()) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("username", username);
                                    editor.putString("password", password);
                                    editor.putBoolean("checked",true);
                                    editor.commit();
                                }
                            }
                            else {
                                showAlert("Invalid username or password");
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseModel> call, Throwable t) {
                            Log.d("response call", t.getMessage().toString());
                        }
                    });
                }else {
                    if(checkUser() == false) {
                        showAlert("Please fill username");
                    }
                    else if(checkPwd() == false) {
                        showAlert("Please fill password");
                    }
                }
            }
        });
    }

    private boolean checkUser() {
        if(user.getText().toString().equals("")) return false;
        return true;
    }
    private boolean checkPwd() {
        if(pwd.getText().toString().equals("")) return false;
        return true;
    }
    private boolean canSignIn() {
        if(checkUser() == true && checkPwd() == true) return true;
        return false;
    }

    private void showAlert(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Signin.this);
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
}