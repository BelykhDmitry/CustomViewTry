package dmitrybelykh.study.customviewtry;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import androidx.annotation.Nullable;

public class LinearGraph extends View {

    private static final String LOG_TAG = LinearGraph.class.getName();

    private int mColor;
    private Paint paint;
    private DataHelper dataHelper;

    private Path path;

    public LinearGraph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.d(LOG_TAG, "Constructor");
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LinearGraph);
        mColor = typedArray.getColor(R.styleable.LinearGraph_piv_color, R.color.colorPrimaryDark);
        typedArray.recycle();
        setupPaint(mColor);
        path = new Path();
        dataHelper = new DataHelper();
    }

    public void setData(ArrayList<Pair<Float, Float>> dataList) {
        animate().cancel();
        hideWithAnimation();
        dataHelper.setData(dataList);
        Log.d(LOG_TAG, "onSetData");
        invalidate();
    }

    public void setGraphColor(int color) {
        this.mColor = color;
        setupPaint(mColor);
        invalidate();
    }

    public void setInterpolationOn(boolean isInterpolationOn) {
        dataHelper.setInterpolation(isInterpolationOn);
        invalidate();
    }

    private void setupPaint(int color) {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        int shadowColor = Color.argb(Color.alpha(color) / 2, Color.red(color), Color.green(color), Color.blue(color));
        paint.setColor(color);
        paint.setStrokeWidth(6f);
        paint.setAntiAlias(true);
        paint.setShadowLayer(8, 4, 4, shadowColor);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(LOG_TAG, "onMeasure");
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(LOG_TAG, "onSizeChanged");
        super.onSizeChanged(w, h, oldw, oldh);
        dataHelper.setPaddings(getPaddingLeft(), getPaddingRight(),
                getPaddingTop(), getPaddingBottom())
                .setViewSize(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(LOG_TAG, "onDraw");
        dataHelper.fillPath(path);
        canvas.drawPath(path, paint);
    }

    private void hideWithAnimation() {
        animate().cancel();
        animate().setDuration(500)
                .alpha(0f)
                .setInterpolator(new AccelerateInterpolator());
    }

    private void showWithAnimation() {
        animate().cancel();
        animate().setDuration(500)
                .alpha(1f)
                .setInterpolator(new AccelerateInterpolator());
    }

    /**
     * По хорошему должен выполять следующие действия:
     * Пересчёт данных для отрисовки и их хранение.
     * Построение доп точек для интерполяции?
     */
    private class DataHelper {
        // View Size Params
        private float mHigh = 0f;
        private float mWidth = 0f;
        private int mPaddingLeft = 0;
        private int mPaddingRight = 0;
        private int mPaddingTop = 0;
        private int mPaddingBottom = 0;
        // Data
        WeakReference<ArrayList<Pair<Float, Float>>> rawDataWeakReference;
        private volatile boolean isValid = false;
        private boolean interpolationOn = false;
        private LinkedList<Float> mXCoordinatesList = new LinkedList<>();
        private LinkedList<Float> mYCoordinatesList = new LinkedList<>();
        // Data params
        private float mMinimumY;
        private float mMaximumDeltaY;
        private float mMinimumX;
        private float mMaximumDeltaX;

        /**
         * Set view paddings.
         *
         * @param left
         * @param right
         * @param top
         * @param bottom
         * @return self
         */
        DataHelper setPaddings(int left, int right, int top, int bottom) {
            mPaddingLeft = left;
            mPaddingRight = right;
            mPaddingTop = top;
            mPaddingBottom = bottom;
            return this;
        }

        /**
         * Set view size
         *
         * @param width
         * @param high
         * @return self
         */
        DataHelper setViewSize(int width, int high) {
            mHigh = high - (mPaddingTop + mPaddingBottom);
            mWidth = width - (mPaddingLeft + mPaddingRight);
            return this;
        }

        /**
         * Set interpolation for graph
         *
         * @param on If true sets interpolation On
         * @return self
         */
        DataHelper setInterpolation(boolean on) {
            interpolationOn = on;
            if (isValid)
                interpolate();
            return this;
        }

        /**
         * Sets the graph Data
         *
         * @param dataList ArrayList<Pair<Float, Float>>
         * @return self
         */
        DataHelper setData(ArrayList<Pair<Float, Float>> dataList) {
            rawDataWeakReference = new WeakReference<>(dataList);
            final ArrayList<Pair<Float, Float>> localDataList = rawDataWeakReference.get();
            mXCoordinatesList.clear();
            mYCoordinatesList.clear();
            for (Pair<Float, Float> dot : localDataList) {
                mXCoordinatesList.add(new Float(dot.first));
                mYCoordinatesList.add(new Float(dot.second));
            }
            calculateDataParams();
            isValid = calculatePoints();
            if (interpolationOn)
                interpolate();
            return this;
        }

        /**
         * Calculate points. Call to calculate scalable data.
         *
         * @return True if data is Valid
         */
        boolean calculatePoints() {
            for (int i = 0; i < mXCoordinatesList.size(); i++) {
                mXCoordinatesList.set(i, getXPos(mXCoordinatesList.get(i)));
                mYCoordinatesList.set(i, getYPos(mYCoordinatesList.get(i)));
            }
            return true;
        }


        /**
         * Call when view resize
         *
         * @param width new View width.
         * @param heigh new View heigh.
         */
//        void onSizeChanged(int width, int heigh) {
//            float graphHigh = calcGraphSize(mHigh, mPaddingTop, mPaddingBottom);
//            float graphWidth = calcGraphSize(mWidth, mPaddingLeft, mPaddingRight);
//            recalculateData(graphHigh, graphWidth);
//        }

        /**
         * Metod when resize View. Coming soon
         * @param graphHigh
         * @param graphWidth
         */
//        private void recalculateData(float graphHigh, float graphWidth) {
//
//        }

        /**
         * Calculates minimum X, Y, maximum delta X, Y
         */
        private void calculateDataParams() {
            mMinimumY = getMin(mYCoordinatesList);
            mMaximumDeltaY = getMax(mYCoordinatesList) - mMinimumY;
            mMinimumX = getMin(mXCoordinatesList);
            mMaximumDeltaX = getMax(mXCoordinatesList) - mMinimumX;
        }

        /**
         * Calculate Graph size without paddings
         *
         * @param overallSize  Size of the View (hiegh or width)
         * @param paddingBegin Padding in the begin of the View (Top or Left)
         * @param paddingEnd   Padding in the end of the View (Bottom or Right)
         * @return Graph size
         */
        private float calcGraphSize(float overallSize, int paddingBegin, int paddingEnd) {
            return overallSize - paddingBegin - paddingEnd;
        }

        /**
         * Calculate Number given by the scale
         *
         * @param number Y coordinate in raw data
         * @return Y coordinate given by the scale
         */
        private float getYPos(float number) {
            return mHigh + mPaddingTop - (number - mMinimumY) * (mHigh / mMaximumDeltaY); // inverse + padding
        }

        /**
         * Calculate Number given by the scale
         *
         * @param number X coordinate in raw data
         * @return X coordinate given by the scale
         */
        private float getXPos(float number) {
            return mPaddingLeft + (number - mMinimumX) * (mWidth / mMaximumDeltaX);
        }

        /**
         * Calculate Max Value in list
         *
         * @param list LinkedList<Float> - data List
         * @return max value
         */
        private float getMax(LinkedList<Float> list) {
            return Collections.max(list);
        }

        /**
         * Calculate Min Value in list
         *
         * @param list LinkedList<Float> - data List
         * @return min value
         */
        private float getMin(LinkedList<Float> list) {
            return Collections.min(list);
        }

        public void fillPath(Path path) {
            path.reset();
            if (isValid) {
                if (interpolationOn) {
                    interpolatePath(path);
                } else {
                    linearPath(path);
                }
            }
        }

        private void linearPath(Path path) {
            path.moveTo(mXCoordinatesList.getFirst(), mYCoordinatesList.getFirst());
            Iterator<Float> iterX = mXCoordinatesList.iterator();
            Iterator<Float> iterY = mYCoordinatesList.iterator();
            while (iterX.hasNext() && iterY.hasNext()) {
                path.lineTo(iterX.next(), iterY.next());
            }
        }

        private LinkedList<Float> interpolationXCoord = new LinkedList<>();
        private LinkedList<Float> interpolationYCoord = new LinkedList<>();

        private void clearInterpolatePoints() {
            interpolationXCoord.clear();
            interpolationYCoord.clear();
        }

        private void interpolatePath(Path path) {
            path.moveTo(mXCoordinatesList.getFirst(), mYCoordinatesList.getFirst());
            ListIterator<Float> iterX = mXCoordinatesList.listIterator(1);
            ListIterator<Float> iterY = mYCoordinatesList.listIterator(1);
            Iterator<Float> iterInterpolateX = interpolationXCoord.iterator();
            Iterator<Float> iterInterpolateY = interpolationYCoord.iterator();
            while (iterInterpolateX.hasNext() && iterInterpolateY.hasNext()) {
                path.cubicTo(iterX.previous(), iterY.previous(),
                        iterInterpolateX.next(), iterInterpolateY.next(),
                        iterX.next(), iterY.next());
            }
        }

        /**
         * Интерполяция кривыми Безье с помощью дополнительных точек. Плохо работает для
         * небольшого количества точек.
         */
        private void interpolate() {
            clearInterpolatePoints();
            float prevX = mXCoordinatesList.getFirst();
            float prevY = mYCoordinatesList.getFirst();
            float actualX = mXCoordinatesList.get(1);
            float actualY = mYCoordinatesList.get(1);
            float nextX = mXCoordinatesList.get(2);
            float nextY = mYCoordinatesList.get(2);
            float ACx = nextX - prevX;
            float ACy = nextY - prevY;
            float module = (float) Math.sqrt(ACx * ACx + ACy * ACy);
            float tempX = actualX - ACx * (actualX - prevX) / (2f * module);
            float tempY = actualY - ACy * (actualX - prevX) / (2f * module);
            interpolationXCoord.add(tempX);
            interpolationYCoord.add(tempY);
            for (int i = 2; i < mXCoordinatesList.size(); i++) {
                nextX = mXCoordinatesList.get(i);
                nextY = mYCoordinatesList.get(i);
                ACx = nextX - prevX;
                ACy = nextY - prevY;
                module = (float) Math.sqrt(ACx * ACx + ACy * ACy);
                tempX = actualX + ACx * (actualX - prevX) / (2f * module);
                tempY = actualY + ACy * (actualX - prevX) / (2f * module);
                interpolationXCoord.add(tempX);
                interpolationYCoord.add(tempY);
                prevX = actualX;
                prevY = actualY;
                actualX = nextX;
                actualY = nextY;
            }
        }
    }
}
