package com.example.afinal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.afinal.Adapter.SpinnerAdapter;
import com.example.afinal.api.ApiClient;
import com.example.afinal.api.CallGraph;
import com.example.afinal.model.PointWeather;
import com.example.afinal.model.User;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.sql.Struct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Graph extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    Button btn_home, btn_map, btn_logout, changeLang, btnShow;
    EditText editTextDateTime;
    TextView textGraph;
    private Calendar cal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Spinner attributeSpinner = findViewById(R.id.spinner);
        Spinner timeFrameSpinner = findViewById(R.id.spinner_timeframe);
        LineChart lineChart = findViewById(R.id.chart);

        btn_home = findViewById(R.id.btn_home);
        btn_map = findViewById(R.id.btn_map);
        btn_logout = findViewById(R.id.btn_logout);
        changeLang = findViewById(R.id.changeMyLang);
        btnShow = findViewById(R.id.btnShow);
        textGraph = findViewById(R.id.name_graph);

        String attribute, timeframe, date;

        editTextDateTime = findViewById(R.id.editTextDateTime);
        cal = Calendar.getInstance();

        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");
        User userCallApi = (User) intent.getSerializableExtra("userAPI");

        setSpiner(attributeSpinner, timeFrameSpinner);



        editTextDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateTime();
            }
        });

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Graph.this, MapScreen.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
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
                startActivity(intent);
            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String attribute = attributeSpinner.getSelectedItem().toString().substring(0, 1).toLowerCase() + attributeSpinner.getSelectedItem().toString().substring(1);
                String timeframe = timeFrameSpinner.getSelectedItem().toString();
                Date endTime = formatStringtoDate(editTextDateTime.getText().toString());
                Date beginTime = handleBeginTime(endTime, timeframe);

                textGraph.setText(attributeSpinner.getSelectedItem().toString() + " graph of the " + timeframe);

                String jsonBody = createJson(convertToISOString(beginTime), convertToISOString(endTime));
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody);

                CallGraph callGraph = ApiClient.CallGraph();
                Call<List<PointWeather>> call = callGraph.callGraph(
                    "5zI6XqkQVSfdgOrZ1MyWEf",
                        attribute,
                    "Bearer " + userCallApi.getToken(),
                    requestBody
                );

                call.enqueue(new Callback<List<PointWeather>>() {
                    @Override
                    public void onResponse(Call<List<PointWeather>> call, Response<List<PointWeather>> response) {
                        // Xử lý thành công
                        if (response.isSuccessful()) {
                            List<PointWeather> listPoint = response.body();
                            Collections.reverse(listPoint);
                            setGraph(lineChart, listPoint, timeframe);
                        } else {
                            Log.d("callGraph", "null");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PointWeather>> call, Throwable t) {
                        // Xử lý thất bại
                        Log.d("callGraph", "onFailure: " + t.getMessage());
                    }
                });
            }
        });
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
    private void setDateTime () {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(Graph.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog timePickerDialog = new TimePickerDialog(Graph.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cal.set(Calendar.MINUTE, minute);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                        String formatDateTime = dateFormat.format(cal.getTime());

                        editTextDateTime.setText(formatDateTime);
                    }
                }, hour, minute, true
                );
                timePickerDialog.show();
            }
        },year, month, day
        );
        datePickerDialog.show();
    }

    private void setSpiner(Spinner spinner, Spinner timeFrameSpinner) {
        List<String> attributes = Arrays.asList("Temperature", "WindSpeed", "Humidity");
        List<String> timeFrameData = Arrays.asList("Day", "Month");

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, attributes);
        spinner.setAdapter(spinnerAdapter);
        spinner.setDropDownVerticalOffset(120);

        SpinnerAdapter timeFrameAdapter = new SpinnerAdapter(this, timeFrameData);
        timeFrameSpinner.setAdapter(timeFrameAdapter);
        timeFrameSpinner.setDropDownVerticalOffset(120);
    }

    private void setGraph(LineChart lineChart, List<PointWeather> pointWeatherList, String timeframe) {
        List<Entry> entries = new ArrayList<>();
        List<String> xAxisLabels = new ArrayList<>();
        xAxisLabels.add("");
        if(timeframe.equalsIgnoreCase("month")) {
            for(int i = 0; i < 30; i++) {
                List<Float> values = new ArrayList<>();
                for (int j = 0; j < 24; j++) {
                    int point = i * 24 + j;
                    values.add(pointWeatherList.get(point).getValue());
                }
                xAxisLabels.add(getDayFromTimestamp(pointWeatherList.get(i * 24).getTimestamp()));
                entries.add(new Entry(i, calculateAverage(values)));
            }
        }else {
            for (int i = 0; i < pointWeatherList.size(); i++) {
                xAxisLabels.add(getHourFromTimestamp(pointWeatherList.get(i).getTimestamp()));
                entries.add(new Entry(i, (float) pointWeatherList.get(i).getValue()));
            }
        }


        LineDataSet dataSet = new LineDataSet(entries, null);
        dataSet.setColor(Color.RED);
        dataSet.setDrawValues(true);
        dataSet.setLineWidth(3f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Cấu hình trục X và trục Y
        XAxis xAxis = lineChart.getXAxis();
        YAxis yAxisLeft = lineChart.getAxisLeft();
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);

        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));

        yAxisLeft.setAxisMinimum(0);

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


        lineChart.invalidate();
    }
    public static String getHourFromTimestamp(long timestampInMillis) {
        // Tạo đối tượng Date từ timestamp
        Date date = new Date(timestampInMillis);

        // Sử dụng SimpleDateFormat để lấy giá trị giờ
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH");

        return dateFormat.format(date);
    }
    public static String getDayFromTimestamp(long timestampInMillis) {
        // Tạo đối tượng Date từ timestamp
        Date date = new Date(timestampInMillis);

        // Sử dụng SimpleDateFormat để lấy giá trị giờ
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");

        return dateFormat.format(date);
    }
    public static Date formatStringtoDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        try {
            Date date = dateFormat.parse(dateString);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            // Trả về null hoặc xử lý ngoại lệ tùy theo yêu cầu
            return null;
        }
    }
    public Date handleBeginTime(Date endTime, String timeframe) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);

        if(timeframe.equalsIgnoreCase("day")) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }else {
            calendar.add(Calendar.DAY_OF_MONTH, -31);
        }

        return calendar.getTime();
    }
    public String convertToISOString(Date inputDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Đặt múi giờ thành UTC
        return dateFormat.format(inputDate);
    }
    public static String createJson(String fromTime, String toTime) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("fromTime", new JsonPrimitive(fromTime));
        jsonObject.add("toTime", new JsonPrimitive(toTime));

        return jsonObject.toString();
    }
    private float calculateAverage(List<Float> floatList) {
        if (floatList == null || floatList.isEmpty()) {
            throw new IllegalArgumentException("Danh sách không được là null hoặc rỗng.");
        }
        float sum = 0;
        for (float value : floatList) {
            sum += value;
        }
        return sum / floatList.size();
    }

}
