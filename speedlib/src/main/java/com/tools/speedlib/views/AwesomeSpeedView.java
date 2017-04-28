package com.tools.speedlib.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.tools.speedlib.R;
import com.tools.speedlib.views.base.Speedometer;
import com.tools.speedlib.views.base.SpeedometerDefault;
import com.tools.speedlib.views.components.Indicators.TriangleIndicator;


/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class AwesomeSpeedView extends Speedometer {

    private Path markPath = new Path(),
            trianglesPath = new Path();
    private Paint markPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            trianglesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF speedometerRect = new RectF();

    private int speedometerColor = Color.parseColor("#007AFF");

    public AwesomeSpeedView(Context context) {
        this(context, null);
    }

    public AwesomeSpeedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AwesomeSpeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
    }

    @Override
    protected void defaultValues() {

        super.setTextColor(Color.parseColor("#ffc260"));
        super.setSpeedTextColor(Color.WHITE);
        super.setUnitTextColor(Color.WHITE);
        super.setTextTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        super.setSpeedTextPosition(Position.CENTER);
        super.setUnitUnderSpeedText(true);
    }

    @Override
    protected SpeedometerDefault getSpeedometerDefault() {
        SpeedometerDefault speedometerDefault = new SpeedometerDefault();
        speedometerDefault.indicator = new TriangleIndicator(getContext())
                .setIndicatorWidth(dpTOpx(25f))
                .setIndicatorColor(Color.parseColor("#007AFF"));
        speedometerDefault.startDegree = 135;
        speedometerDefault.endDegree = 135+320;
        speedometerDefault.speedometerWidth = dpTOpx(60);
        speedometerDefault.backgroundCircleColor = Color.parseColor("#212121");
        speedometerDefault.backgroundCircleColor = Color.parseColor("#212121");
        return speedometerDefault;
    }

    private void init() {
        markPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        ringPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(dpTOpx(10));
        trianglesPaint.setColor(Color.parseColor("#3949ab"));
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null)
            return;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AwesomeSpeedView, 0, 0);

        speedometerColor = a.getColor(R.styleable.AwesomeSpeedView_sv_speedometerColor, speedometerColor);
        trianglesPaint.setColor(a.getColor(R.styleable.AwesomeSpeedView_sv_trianglesColor, trianglesPaint.getColor()));
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        updateGradient();
        updateBackgroundBitmap();
    }

    private void updateGradient() {
        float stop = (getWidthPa() *.5f - getSpeedometerWidth()) / (getWidthPa() *.5f);
        float stop2 = stop+((1f-stop)*.1f);
        float stop3 = stop+((1f-stop)*.36f);
        float stop4 = stop+((1f-stop)*.64f);
        float stop5 = stop+((1f-stop)*.9f);
        int []colors = new int[]{getBackgroundCircleColor(), speedometerColor, getBackgroundCircleColor()
                , getBackgroundCircleColor(), speedometerColor, speedometerColor};
        Shader radialGradient = new RadialGradient(getSize() *.5f, getSize() *.5f, getWidthPa() *.5f
                , colors, new float[]{stop, stop2, stop3, stop4, stop5, 1f}, Shader.TileMode.CLAMP);
        ringPaint.setShader(radialGradient);
    }

    private void initDraw() {
        ringPaint.setStrokeWidth(getSpeedometerWidth());
        markPaint.setColor(getMarkColor());
        textPaint.setColor(getTextColor());
        textPaint.setTextSize(getTextSize());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDraw();

        drawSpeedUnitText(canvas);
        drawIndicator(canvas);
        drawNotes(canvas);
    }

    @Override
    protected void updateBackgroundBitmap() {
        Canvas c = createBackgroundBitmapCanvas();
        initDraw();

        float markH = getHeightPa()/22f;
        markPath.reset();
        markPath.moveTo(getSize() *.5f, getPadding());
        markPath.lineTo(getSize() *.5f, markH + getPadding());
        markPaint.setStrokeWidth(markH/5f);

        trianglesPath.reset();
        trianglesPath.moveTo(getSize() *.5f, getPadding() + getHeightPa()/20f);
        trianglesPath.lineTo(getSize() *.5f -(getSize()/40f), getPadding());
        trianglesPath.lineTo(getSize() *.5f +(getSize()/40f), getPadding());

        float risk = getSpeedometerWidth() *.5f + getPadding();
        speedometerRect.set(risk, risk, getSize() -risk, getSize() -risk);
        c.drawArc(speedometerRect, 0f, 360f, false, ringPaint);

        c.save();
        c.rotate(getStartDegree()+90f, getSize() *.5f, getSize() *.5f);
        for (float i = 0; i <= getEndDegree() - getStartDegree(); i+=4f) {
            c.rotate(4f, getSize() *.5f, getSize() *.5f);
            if (i % 40 == 0) {
                c.drawPath(trianglesPath, trianglesPaint);
                c.drawText(String.format(getLocale(), "%d", (int)getSpeedAtDegree(i + getStartDegree()))
                        , getSize() *.5f, getHeightPa()/20f +textPaint.getTextSize() + getPadding(), textPaint);
            }
            else {
                if (i % 20 == 0)
                    markPaint.setStrokeWidth(getSize()/22f/5);
                else
                    markPaint.setStrokeWidth(getSize()/22f/9);
                c.drawPath(markPath, markPaint);
            }
        }
        c.restore();
    }

    @Override
    public void setSpeedometerWidth(float speedometerWidth) {
        super.setSpeedometerWidth(speedometerWidth);
        float risk = speedometerWidth *.5f;
        speedometerRect.set(risk, risk, getSize() -risk, getSize() -risk);
        updateGradient();
        updateBackgroundBitmap();
        invalidate();
    }

    public int getSpeedometerColor() {
        return speedometerColor;
    }

    public void setSpeedometerColor(int speedometerColor) {
        this.speedometerColor = speedometerColor;
        updateGradient();
        updateBackgroundBitmap();
        invalidate();
    }

    public int getTrianglesColor() {
        return trianglesPaint.getColor();
    }

    public void setTrianglesColor(int trianglesColor) {
        trianglesPaint.setColor(trianglesColor);
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * this Speedometer doesn't use this method.
     * @return {@code Color.TRANSPARENT} always.
     */
    @Deprecated
    @Override
    public int getLowSpeedColor() {
        return Color.TRANSPARENT;
    }

    /**
     * this Speedometer doesn't use this method.
     * @param lowSpeedColor nothing.
     */
    @Deprecated
    @Override
    public void setLowSpeedColor(int lowSpeedColor) {
    }

    /**
     * this Speedometer doesn't use this method.
     * @return {@code Color.TRANSPARENT} always.
     */
    @Deprecated
    @Override
    public int getMediumSpeedColor() {
        return Color.TRANSPARENT;
    }

    /**
     * this Speedometer doesn't use this method.
     * @param mediumSpeedColor nothing.
     */
    @Deprecated
    @Override
    public void setMediumSpeedColor(int mediumSpeedColor) {
    }

    /**
     * this Speedometer doesn't use this method.
     * @return {@code Color.TRANSPARENT} always.
     */
    @Deprecated
    @Override
    public int getHighSpeedColor() {
        return Color.TRANSPARENT;
    }

    /**
     * this Speedometer doesn't use this method.
     * @param highSpeedColor nothing.
     */
    @Deprecated
    @Override
    public void setHighSpeedColor(int highSpeedColor) {
    }
}
