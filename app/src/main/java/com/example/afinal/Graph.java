package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.afinal.Adapter.SpinnerAdapter;
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

public class Graph extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Spinner spinner = findViewById(R.id.spinner);
        Spinner timeFrameSpinner = findViewById(R.id.spinner_timeframe);
        LineChart lineChart = findViewById(R.id.chart);


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

}