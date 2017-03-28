package com.tools.speedlib.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;

import com.tools.speedlib.R;
import com.tools.speedlib.views.base.Speedometer;
import com.tools.speedlib.views.base.SpeedometerDefault;
import com.tools.speedlib.views.components.Indicators.Indicator;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class RaySpeedometer extends Speedometer {

    private Path markPath = new Path(),
            ray1Path = new Path(),
            ray2Path = new Path();
    private Paint markPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            speedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            rayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean withEffects = true;

    private int degreeBetweenMark = 5;

    public RaySpeedometer(Context context) {
        this(context, null);
    }

    public RaySpeedometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RaySpeedometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
    }

    @Override
    protected void defaultValues() {
        super.setTextColor(Color.WHITE);
    }

    @Override
    protected SpeedometerDefault getSpeedometerDefault() {
        SpeedometerDefault speedometerDefault = new SpeedometerDefault();
        speedometerDefault.backgroundCircleColor = Color.parseColor("#212121");
        speedometerDefault.markColor = Color.BLACK;
        return speedometerDefault;
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null)
            return;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RaySpeedometer, 0, 0);

        rayPaint.setColor(a.getColor(R.styleable.RaySpeedometer_sv_rayColor, rayPaint.getColor()));
        int degreeBetweenMark = a.getInt(R.styleable.RaySpeedometer_sv_degreeBetweenMark, this.degreeBetweenMark);
        markPaint.setStrokeWidth(a.getDimension(R.styleable.RaySpeedometer_sv_markWidth, markPaint.getStrokeWidth()));
        speedBackgroundPaint.setColor(a.getColor(R.styleable.RaySpeedometer_sv_speedBackgroundColor
                , speedBackgroundPaint.getColor()));
        withEffects = a.getBoolean(R.styleable.RaySpeedometer_sv_withEffects, withEffects);
        a.recycle();
        setWithEffects(withEffects);
        if (degreeBetweenMark > 0 && degreeBetweenMark <= 20)
            this.degreeBetweenMark = degreeBetweenMark;
    }

    private void init() {
        markPaint.setStyle(Paint.Style.STROKE);
        rayPaint.setStyle(Paint.Style.STROKE);
        rayPaint.setStrokeWidth(dpTOpx(1.8f));
        rayPaint.setColor(Color.WHITE);
        speedBackgroundPaint.setColor(Color.WHITE);
        markPaint.setStrokeWidth(dpTOpx(3f));

        if (Build.VERSION.SDK_INT >= 11)
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        setWithEffects(withEffects);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        updateMarkPath();
        updateBackgroundBitmap();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.rotate(getStartDegree()+90f, getSize() *.5f, getSize() *.5f);
        for (int i = getStartDegree(); i < getEndDegree(); i+=degreeBetweenMark) {
            if (getDegree() <= i) {
                markPaint.setColor(getMarkColor());
                canvas.drawPath(markPath, markPaint);
                canvas.rotate(degreeBetweenMark, getSize() *.5f, getSize() *.5f);
                continue;
            }
            if (i > (getEndDegree()- getStartDegree())*getMediumSpeedOffset() + getStartDegree())
                markPaint.setColor(getHighSpeedColor());
            else if (i > (getEndDegree()- getStartDegree())*getLowSpeedOffset() + getStartDegree())
                markPaint.setColor(getMediumSpeedColor());
            else
                markPaint.setColor(getLowSpeedColor());
            canvas.drawPath(markPath, markPaint);
            canvas.rotate(degreeBetweenMark, getSize() *.5f, getSize()/2f);
        }
        canvas.restore();

        RectF speedBackgroundRect = getSpeedUnitTextBounds();
        speedBackgroundRect.left -= 2;
        speedBackgroundRect.right += 2;
        speedBackgroundRect.bottom += 2;
        canvas.drawRect(speedBackgroundRect, speedBackgroundPaint);

        drawSpeedUnitText(canvas);
        drawIndicator(canvas);
        drawNotes(canvas);
    }

    @Override
    protected void updateBackgroundBitmap() {
        Canvas c = createBackgroundBitmapCanvas();

        updateMarkPath();

        ray1Path.reset();
        ray1Path.moveTo(getSize()/2f, getSize()/2f);
        ray1Path.lineTo(getSize()/2f, getHeightPa()/3.2f + getPadding());
        ray1Path.moveTo(getSize()/2f, getHeightPa()/3.2f + getPadding());
        ray1Path.lineTo(getSize()/2.2f, getHeightPa()/3f + getPadding());
        ray1Path.moveTo(getSize()/2.2f, getHeightPa()/3f + getPadding());
        ray1Path.lineTo(getSize()/2.1f, getHeightPa()/4.5f + getPadding());

        ray2Path.reset();
        ray2Path.moveTo(getSize()/2f, getSize()/2f);
        ray2Path.lineTo(getSize()/2f, getHeightPa()/3.2f + getPadding());
        ray2Path.moveTo(getSize()/2f, getHeightPa()/3.2f + getPadding());
        ray2Path.lineTo(getSize()/2.2f, getHeightPa()/3.8f + getPadding());
        ray2Path.moveTo(getSize()/2f, getHeightPa()/3.2f + getPadding());
        ray2Path.lineTo(getSize()/1.8f, getHeightPa()/3.8f + getPadding());

        c.save();
        for (int i=0; i<6; i++) {
            c.rotate(58f, getSize() *.5f, getSize() *.5f);
            if (i % 2 == 0)
                c.drawPath(ray1Path, rayPaint);
            else
                c.drawPath(ray2Path, rayPaint);
        }
        c.restore();

        drawDefMinMaxSpeedPosition(c);
    }

    private void updateMarkPath() {
        markPath.reset();
        markPath.moveTo(getSize() *.5f, getPadding());
        markPath.lineTo(getSize() *.5f, getSpeedometerWidth() + getPadding());
    }

    public boolean isWithEffects() {
        return withEffects;
    }

    public void setWithEffects(boolean withEffects) {
        this.withEffects = withEffects;
        indicatorEffects(withEffects);
        if (withEffects && !isInEditMode()) {
            rayPaint.setMaskFilter(new BlurMaskFilter(3, BlurMaskFilter.Blur.SOLID));
            markPaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.SOLID));
            speedBackgroundPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.SOLID));
        }
        else {
            rayPaint.setMaskFilter(null);
            markPaint.setMaskFilter(null);
            speedBackgroundPaint.setMaskFilter(null);
        }
        updateBackgroundBitmap();
        invalidate();
    }

    @Override
    public void setIndicator(Indicator.Indicators indicator) {
        super.setIndicator(indicator);
        indicatorEffects(withEffects);
    }

    public int getSpeedBackgroundColor() {
        return speedBackgroundPaint.getColor();
    }

    public void setSpeedBackgroundColor(int speedBackgroundColor) {
        speedBackgroundPaint.setColor(speedBackgroundColor);
        updateBackgroundBitmap();
        invalidate();
    }

    public int getDegreeBetweenMark() {
        return degreeBetweenMark;
    }

    /**
     * The spacing between the marks
     * <p>
     *     it should be between (0-20] ,else well be ignore.
     * </p>
     * @param degreeBetweenMark degree between two marks.
     */
    public void setDegreeBetweenMark(int degreeBetweenMark) {
        if (degreeBetweenMark <= 0 || degreeBetweenMark > 20)
            return;
        this.degreeBetweenMark = degreeBetweenMark;
        invalidate();
    }

    public float getMarkWidth() {
        return markPaint.getStrokeWidth();
    }

    public void setMarkWidth(float markWidth) {
        markPaint.setStrokeWidth(markWidth);
        invalidate();
    }

    public int getRayColor() {
        return rayPaint.getColor();
    }

    public void setRayColor(int rayColor) {
        rayPaint.setColor(rayColor);
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * this Speedometer doesn't use this method.
     * @return {@code Color.TRANSPARENT} always.
     */
    @Deprecated
    @Override
    public int getIndicatorColor() {
        return Color.TRANSPARENT;
    }

    /**
     * this Speedometer doesn't use this method.
     * @param indicatorColor nothing.
     */
    @Deprecated
    @Override
    public void setIndicatorColor(int indicatorColor) {
    }
}
