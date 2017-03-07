package com.drdc1.medic;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

import com.drdc1.medic.R;

public class LineChartWithBackground extends LineChart {
    private static float axisThickness = 4f;

    protected Paint mUpperRedZone;
    protected Paint mUpperYellowZone;
    protected Paint mGreenZone;
    protected Paint mLowerYellowZone;
    protected Paint mLowerRedZone;

    public LineChartWithBackground(Context context) {
        super(context);
    }

    public LineChartWithBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineChartWithBackground(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        Resources res = getContext().getResources();

        //going to stack rectangles, so only need 3
        mUpperRedZone = new Paint();
        mUpperRedZone.setStyle(Paint.Style.FILL);
        mUpperRedZone.setColor(res.getColor(R.color.redGraphTint));
        mUpperYellowZone = new Paint();
        mUpperYellowZone.setStyle(Paint.Style.FILL);
        mUpperYellowZone.setColor(res.getColor(R.color.yellowGraphTint));
        mGreenZone = new Paint();
        mGreenZone.setStyle(Paint.Style.FILL);
        mGreenZone.setColor(res.getColor(R.color.greenGraphTint));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        List<LimitLine> limitLines = mAxisLeft.getLimitLines();

        if (limitLines != null && limitLines.size() == 4) {

            float[] pts = new float[8];
            LimitLine l0 = limitLines.get(0);
            LimitLine l1 = limitLines.get(1);
            LimitLine l2 = limitLines.get(2);
            LimitLine l3 = limitLines.get(3);

            pts[1] = l0.getLimit();
            pts[3] = l1.getLimit();
            pts[5] = l2.getLimit();
            pts[7] = l3.getLimit();

            mLeftAxisTransformer.pointValuesToPixel(pts);
            float left = mViewPortHandler.contentLeft() + mXAxis.getXOffset();
            float right = mViewPortHandler.contentRight();
            float bottom = mViewPortHandler.contentBottom();
            float top = mViewPortHandler.contentTop();

            canvas.drawRect(left, top, right, bottom, mUpperRedZone);
            canvas.drawRect(left, pts[1], right, pts[7], mUpperYellowZone);
            canvas.drawRect(left, pts[3], right, pts[5], mGreenZone);
        }
        super.onDraw(canvas);
    }

    public static void formatUpdateLineChart(Resources resources, LineChartWithBackground lineChart,
                                             List<Entry> entries, float yMin, float yMax,
                                             float[] zoneLimits) {
        //get colours
        int white = resources.getColor(R.color.white);

        //format dataset
        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(white);
        dataSet.setCircleColor(white);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawValues(false);

        //format chart
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.setDrawBorders(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setExtraBottomOffset(10f);
        lineChart.getDescription().setEnabled(false);

        //get rid of extra axes
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawAxisLine(false);

        //format xAxis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(14f);
        xAxis.setTextColor(white);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineWidth(axisThickness);
        xAxis.setAxisLineColor(white);

        //format yAxis
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setTextSize(14f); // set the text size
        yAxis.setAxisMinimum(yMin);
        yAxis.setAxisMaximum(yMax);
        yAxis.setTextColor(white);
        yAxis.setDrawGridLines(false);
        yAxis.setAxisLineWidth(axisThickness);
        yAxis.setAxisLineColor(white);

        //create limitLines
        yAxis.getLimitLines().clear();
        for (int i = 0; i < zoneLimits.length; i++) {
            LimitLine ll = new LimitLine(zoneLimits[i]);
            ll.setEnabled(false);
            yAxis.addLimitLine(ll);
        }

        lineChart.refreshDrawableState();


        lineChart.invalidate();
    }
}
