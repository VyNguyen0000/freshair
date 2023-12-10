package com.example.afinal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.example.afinal.Adapter.SpinnerAdapter;
import com.example.afinal.model.User;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class Graph extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    Button btn_home, btn_map, btn_logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Spinner spinner = findViewById(R.id.spinner);
        Spinner timeFrameSpinner = findViewById(R.id.spinner_timeframe);
        LineChart lineChart = findViewById(R.id.chart);

        btn_home = findViewById(R.id.btn_home);
        btn_map = findViewById(R.id.btn_map);
        btn_logout = findViewById(R.id.btn_logout);

        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");
        User userCallApi = (User) intent.getSerializableExtra("userAPI");

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Graph.this, MapScreen.class);
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
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Graph.this, Dashboard.class);
                intent.putExtra("user", user);
                intent.putExtra("userAPI", userCallApi);
                startActivity(intent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", "");
                editor.putString("password", "");
                editor.commit();
                Intent intent = new Intent(Graph.this, Home.class);
                startActivity(intent);
            }
        });

        List<String> data = Arrays.asList("Item 1", "Item 2", "Item 3");
        List<String> timeFrameData = Arrays.asList("Day", "Month", "Year");

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, data);
        spinner.setAdapter(spinnerAdapter);
        spinner.setDropDownVerticalOffset(120);

        SpinnerAdapter timeFrameAdapter = new SpinnerAdapter(this, timeFrameData);
        timeFrameSpinner.setAdapter(timeFrameAdapter);
        timeFrameSpinner.setDropDownVerticalOffset(120);

        // Dữ liệu cho biểu đồ đường
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 20f));
        entries.add(new Entry(2, 35f));
        entries.add(new Entry(3, 50f));
        entries.add(new Entry(4, 15f));
        entries.add(new Entry(5, 30f));
        entries.add(new Entry(6, 20f));
        entries.add(new Entry(7, 35f));
        entries.add(new Entry(8, 50f));
        entries.add(new Entry(9, 15f));
        entries.add(new Entry(10, 30f));
        entries.add(new Entry(11, 20f));
        entries.add(new Entry(12, 35f));
        entries.add(new Entry(13, 50f));
        entries.add(new Entry(14, 15f));
        entries.add(new Entry(15, 30f));
        entries.add(new Entry(16, 20f));
        entries.add(new Entry(17, 35f));
        entries.add(new Entry(18, 50f));
        entries.add(new Entry(19, 15f));
        entries.add(new Entry(20, 30f));

        LineDataSet dataSet = new LineDataSet(entries, null);
        dataSet.setColor(Color.RED);
        dataSet.setDrawValues(false);
        dataSet.setLineWidth(3f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Cấu hình trục X và trục Y
        XAxis xAxis = lineChart.getXAxis();
        YAxis yAxisLeft = lineChart.getAxisLeft();
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);

//        Cỡ chữ của trục
        xAxis.setTextSize(16f);
        yAxisLeft.setTextSize(16f);

//        Màu chữ của text trục
        xAxis.setTextColor(Color.WHITE);
        yAxisLeft.setTextColor(Color.WHITE);

//        Màu của trục
        xAxis.setAxisLineColor(Color.WHITE);
        yAxisLeft.setAxisLineColor(Color.WHITE);

        xAxis.setAxisLineWidth(2f);
        yAxisLeft.setAxisLineWidth(2f);

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        yAxisLeft.setGranularity(10f);
    }

    private void showChangeLanguageDialog() {
        //array of language to display in alert dialog
        final String[] listItems = {getString(R.string.lang_vietnamese), getString(R.string.lang_english)};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Graph.this);
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
