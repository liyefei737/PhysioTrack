package capstone.client;

import android.content.res.Resources;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

/**
 * Created by Grace on 2017-02-18.
 */

public class ViewUtils {
    private static float axisThickness = 4f;

    public static void formatUpdateLineChart(Resources resources, LineChart lineChart, List<Entry> entries, float yMin, float yMax){
        //get colours
        int white = resources.getColor(R.color.white);



        //the following code is playing around with the graph for heart rate, feel free to play around
        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(white);
        dataSet.setCircleColor(white);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        //dataSet.setDrawFilled(true
        //dataSet.setFillDrawable(gradientDrawable);
        lineChart.setData(lineData);
        lineChart.setDrawBorders(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getLegend().setEnabled(false);

        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawAxisLine(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(14f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineWidth(axisThickness);
        xAxis.setAxisLineColor(white);
        xAxis.setDrawLabels(true);
        xAxis.setGranularity(1f);



        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setTextSize(14f); // set the text size
        yAxis.setAxisMinimum(yMin);
        yAxis.setAxisMaximum(yMax);
        yAxis.setTextColor(Color.WHITE);
        yAxis.setDrawGridLines(false);
        yAxis.setAxisLineWidth(axisThickness);
        xAxis.setAxisLineColor(white);
        //yAxis.setValueFormatter(new MyValueFormatter());
        //yAxis.setGranularity(1f); // interval 1
        yAxis.setLabelCount(5, true); // force 6 labels
        lineChart.setNoDataText("Loading...");
        lineChart.refreshDrawableState();
        lineChart.invalidate();
    }
}
