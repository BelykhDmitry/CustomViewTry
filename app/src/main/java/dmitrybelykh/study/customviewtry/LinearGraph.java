package dmitrybelykh.study.customviewtry;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;

public class LinearGraph extends View {

    private int mColor;
    private Paint paint;
    private Paint axisPaint;
    private ArrayList<Float> mXList;
    private ArrayList<Float> mYList;
    private float minY;
    private float maxdY;
    private float minX;
    private float maxdX;

    private boolean isInterpolationOn = false;

    private Path path;

    public LinearGraph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LinearGraph);
        mColor = typedArray.getColor(R.styleable.LinearGraph_piv_color, R.color.colorPrimaryDark);
        int mInt = typedArray.getInt(R.styleable.LinearGraph_piv_integer, -1);
        typedArray.recycle();
        setupPaint(mColor);
        path = new Path();
        mXList = new ArrayList<>();
        mYList = new ArrayList<>();
    }

    public void setData(ArrayList<Pair<Float, Float>> dataList) {
        mXList.clear();
        mYList.clear();
        for(Pair<Float, Float> dot:dataList) {
            mXList.add(new Float(dot.first));
            mYList.add(new Float(dot.second)); // Как скажется на памяти?
        }
        minY = getMin(mYList);
        maxdY = getMax(mYList) - minY;
        minX = getMin(mXList);
        maxdX = getMax(mXList) - minX;
        invalidate();
    }

    private float getYPos(float number, float minNumberY, float maxDeltaY, float high) {
        return high + getPaddingTop() - (number - minNumberY) * (high / maxDeltaY); // inverse + padding
    }

    private float getXPos(float number, float minNumberX, float maxDeltaX, float high) {
        return getPaddingTop() + (number - minNumberX) * (high / maxDeltaX);
    }

    private float getMax(ArrayList<Float> list) {
        float maxValue = Float.MIN_VALUE;
        for(Float coord:list) {
            maxValue = Math.max(coord, maxValue);
        }
        return maxValue;
    }

    private float getMin(ArrayList<Float> list) {
        float minValue = Float.MAX_VALUE;
        for(Float coord:list) {
            minValue = Math.min(coord, minValue);
        }
        return minValue;
    }

    public void setGraphColor(int color) {
        this.mColor = color;
        setupPaint(mColor);
        invalidate();
    }

    public void setInterpolationOn(boolean isInterpolationOn) {
        this.isInterpolationOn = isInterpolationOn;
        invalidate();
    }

    private void setupPaint(int color) {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        int shadowColor = Color.argb(Color.alpha(color)/2, Color.red(color), Color.green(color), Color.blue(color));
        paint.setColor(color);
        paint.setStrokeWidth(6f);
        paint.setAntiAlias(true);
        paint.setShadowLayer(8, 4, 4, shadowColor);

//        axisPaint = new Paint();
//        axisPaint.setStyle(Paint.Style.STROKE);
//        axisPaint.setColor(Color.DKGRAY);
//        axisPaint.setStrokeWidth(4f);
//        axisPaint.setAntiAlias(true);
//        axisPaint.setShadowLayer(8, 4, 4, Color.LTGRAY);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float high = getHeight() - getPaddingTop() - getPaddingBottom();
        float width = getWidth() - getPaddingLeft() - getPaddingRight();
        path.reset();
        // TODO: Axis
        //canvas.drawLine(getPaddingLeft(), getPaddingTop(), getPaddingLeft(), getHeight() - getPaddingBottom(), axisPaint);
        //canvas.drawLine(getPaddingLeft(), getHeight() / 2f, getWidth() - getPaddingRight(), getHeight() / 2f, axisPaint);
        if (isInterpolationOn) {
            if (mXList.size() > 0 && mYList.size() > 0) {
                float prevX = getXPos(mXList.get(0), minX, maxdX, width);
                float prevY = getYPos(mYList.get(0), minY, maxdY, high);
                path.moveTo(prevX, prevY);
                float actualX = getXPos(mXList.get(1), minX, maxdX, width);
                float actualY = getYPos(mYList.get(1), minY, maxdY, high);
                float nextX;
                float nextY;
                for (int i = 1; i < mXList.size()-1; i++) {
                    nextX = getXPos(mXList.get(i+1), minX, maxdX, width);
                    nextY = getYPos(mYList.get(i+1), minY, maxdY, high);
                    path.cubicTo(prevX, prevY, actualX, actualY, nextX, nextY);
                    prevX = actualX;
                    prevY = actualY;
                    actualX = nextX;
                    actualY = nextY;
                }
                canvas.drawPath(path, paint);
            }
        } else {
            if (mXList.size() > 0 && mYList.size() > 0) {
                path.moveTo(getXPos(mXList.get(0), minX, maxdX, width),
                        getYPos(mYList.get(0), minY, maxdY, high));
                for (int i = 1; i < mXList.size(); i++) {
                    path.lineTo(getXPos(mXList.get(i), minX, maxdX, width),
                            getYPos(mYList.get(i), minY, maxdY, high));
                }
                canvas.drawPath(path, paint);
            }
        }
    }
}
