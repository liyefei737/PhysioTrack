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

/**
 * Created by Grace on 2017-02-18.
 */

public class LineChartWithBackground extends LineChart{

    protected Paint mRedZone;
    protected Paint mYellowZone;
    protected Paint mGreenZone;

    float[] pts = new float[8];

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
        mRedZone = new Paint();
        mRedZone.setStyle(Paint.Style.FILL);
        mRedZone.setColor(res.getColor(R.color.redGraphTint));
        mYellowZone= new Paint();
        mYellowZone.setStyle(Paint.Style.FILL);
        mYellowZone.setColor(res.getColor(R.color.yellowGraphTint));
        mGreenZone = new Paint();
        mGreenZone.setStyle(Paint.Style.FILL);
        mGreenZone.setColor(res.getColor(R.color.greenGraphTint));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        setNoDataText("");
        List<LimitLine> limitLines = mAxisLeft.getLimitLines();
        int numLimLines = limitLines.size();
        if ( numLimLines == 2 || numLimLines == 4) {

            for (int i = 0; i < numLimLines; i++){
                pts[2*i+1] = limitLines.get(i).getLimit();
            }

            mLeftAxisTransformer.pointValuesToPixel(pts);
            float left = mViewPortHandler.contentLeft();
            float right = mViewPortHandler.contentRight();
            float bottom = mViewPortHandler.contentBottom();
            float top = mViewPortHandler.contentTop();

            canvas.drawRect(left, top, right, bottom, mRedZone);

            if (numLimLines == 2){
                canvas.drawRect(left, pts[3], right, pts[1], mGreenZone);
            }
            else {
                canvas.drawRect(left, pts[7], right, pts[1], mYellowZone);
                canvas.drawRect(left, pts[5], right, pts[3], mGreenZone);
            }
        }
        super.onDraw(canvas);
    }

    public LineData formatUpdateLineChart(Resources resources, List<Entry> entries,
                                             float yMin, float yMax, List<Object> zoneLimits){
        //get colours
        int white = resources.getColor(R.color.white);

        //format dataset
        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset

        dataSet.setCircleColor(R.color.black);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleHoleRadius(3f);
        dataSet.setDrawCircleHole(true);
        dataSet.setCircleColorHole(white);
        dataSet.setHighlightEnabled(false);
        dataSet.setColor(R.color.black, 255);
        dataSet.setDrawValues(false);

        //format chart
        LineData lineData = new LineData(dataSet);
        setData(lineData);
        setDrawBorders(false);
        setDrawGridBackground(false);
        getLegend().setEnabled(false);
        getDescription().setEnabled(false);
        setNoDataText("");

        setPinchZoom(false);
        setTouchEnabled(false);
        setScaleEnabled(false);
        setExtraBottomOffset(10f);

        //get rid of extra axes
        getAxisRight().setDrawGridLines(false);
        getAxisRight().setDrawAxisLine(false);

        //format xAxis
        XAxis xAxis = getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(14f);
        xAxis.setTextColor(white);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        float axisThickness = 4f;
        xAxis.setAxisLineWidth(axisThickness);
        xAxis.setAxisLineColor(white);

        //format yAxis
        YAxis yAxis = getAxisLeft();
        yAxis.setTextSize(14f); // set the text size
        yAxis.setAxisMinimum(yMin);
        yAxis.setAxisMaximum(yMax);
        yAxis.setTextColor(white);
        yAxis.setDrawGridLines(false);
        yAxis.setAxisLineWidth(axisThickness);
        yAxis.setAxisLineColor(white);
        yAxis.setLabelCount(5, true);

        //create limitLines
        yAxis.getLimitLines().clear();
        int numLimits = zoneLimits.size();
        LimitLine ll;
        for (int i = 0; i < numLimits; i++){

            try {
                ll = new LimitLine((float) zoneLimits.get(i));
            } catch (Exception e) {
                ll = new LimitLine(((Integer) zoneLimits.get(i)).floatValue());
            }
            ll.setEnabled(false);
            yAxis.addLimitLine(ll);
        }
        setVisibility(VISIBLE);
        refreshDrawableState();
        invalidate();
        return lineData;
    }
}
