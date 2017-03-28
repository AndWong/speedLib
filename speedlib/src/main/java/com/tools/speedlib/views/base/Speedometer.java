package com.tools.speedlib.views.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.tools.speedlib.R;
import com.tools.speedlib.views.components.Indicators.ImageIndicator;
import com.tools.speedlib.views.components.Indicators.Indicator;
import com.tools.speedlib.views.components.Indicators.NoIndicator;
import com.tools.speedlib.views.components.note.Note;

import java.util.ArrayList;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
@SuppressWarnings("unused")
public abstract class Speedometer extends Gauge {

    private Indicator indicator;
    private Paint circleBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float speedometerWidth = dpTOpx(30f);

    private int markColor = Color.WHITE
            , lowSpeedColor = Color.GREEN
            , mediumSpeedColor = Color.YELLOW
            , highSpeedColor = Color.RED
            , backgroundCircleColor = Color.WHITE;

    private int startDegree = 135, endDegree = 135+270;
    /** to rotate indicator */
    private float degree = startDegree;

    /** array to contain all notes that will be draw */
    private ArrayList<Note> notes = new ArrayList<>();

    private Mode speedometerMode = Mode.NORMAL;

    private int cutPadding = 0;

    public Speedometer(Context context) {
        this(context, null);
    }

    public Speedometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Speedometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
        initAttributeValue();
    }

    private void init() {
        indicator = new NoIndicator(getContext());
        SpeedometerDefault speedometerDefault = getSpeedometerDefault();
        if (speedometerDefault != null) {
            if (speedometerDefault.speedometerMode != null)
                this.setSpeedometerMode(speedometerDefault.speedometerMode);
            if (speedometerDefault.indicator != null)
                indicator = speedometerDefault.indicator;
            if (speedometerDefault.speedometerWidth >= 0)
                speedometerWidth = speedometerDefault.speedometerWidth;
            markColor = speedometerDefault.markColor;
            lowSpeedColor = speedometerDefault.lowSpeedColor;
            mediumSpeedColor = speedometerDefault.mediumSpeedColor;
            highSpeedColor = speedometerDefault.highSpeedColor;
            backgroundCircleColor = speedometerDefault.backgroundCircleColor;
            startDegree = speedometerDefault.startDegree;
            endDegree = speedometerDefault.endDegree;
        }
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null)
            return;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Speedometer, 0, 0);

        int mode = a.getInt(R.styleable.Speedometer_sv_speedometerMode, -1);
        if (mode != -1 && mode != 0)
            setSpeedometerMode(Mode.values()[mode]);
        markColor = a.getColor(R.styleable.Speedometer_sv_markColor, markColor);
        lowSpeedColor = a.getColor(R.styleable.Speedometer_sv_lowSpeedColor, lowSpeedColor);
        mediumSpeedColor = a.getColor(R.styleable.Speedometer_sv_mediumSpeedColor, mediumSpeedColor);
        highSpeedColor = a.getColor(R.styleable.Speedometer_sv_highSpeedColor, highSpeedColor);
        backgroundCircleColor = a.getColor(R.styleable.Speedometer_sv_backgroundCircleColor, backgroundCircleColor);
        speedometerWidth = a.getDimension(R.styleable.Speedometer_sv_speedometerWidth, speedometerWidth);
        startDegree = a.getInt(R.styleable.Speedometer_sv_startDegree, startDegree);
        endDegree = a.getInt(R.styleable.Speedometer_sv_endDegree, endDegree);
        setIndicatorWidth(a.getDimension(R.styleable.Speedometer_sv_indicatorWidth, indicator.getIndicatorWidth()));
        cutPadding = (int) a.getDimension(R.styleable.Speedometer_sv_cutPadding, cutPadding);
        int ind = a.getInt(R.styleable.Speedometer_sv_indicator, -1);
        if (ind != -1)
            setIndicator(Indicator.Indicators.values()[ind]);
        setIndicatorColor(a.getColor(R.styleable.Speedometer_sv_indicatorColor, indicator.getIndicatorColor()));
        degree = startDegree;
        a.recycle();
        checkStartAndEndDegree();
    }

    private void initAttributeValue() {
        circleBackPaint.setColor(backgroundCircleColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        int newW = size / speedometerMode.divWidth;
        int newH = size / speedometerMode.divHeight;
        if (speedometerMode.isHalf) {
            if (speedometerMode.divWidth == 2)
                newW += cutPadding;
            else
                newH += cutPadding;
        }
        setMeasuredDimension(newW, newH);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        indicator.onSizeChange(this);
    }

    private void checkStartAndEndDegree() {
        if (startDegree < 0)
            throw new IllegalArgumentException("StartDegree can\'t be Negative");
        if (endDegree < 0)
            throw new IllegalArgumentException("EndDegree can\'t be Negative");
        if (startDegree >= endDegree)
            throw new IllegalArgumentException("EndDegree must be bigger than StartDegree !");
        if (endDegree - startDegree > 360)
            throw new IllegalArgumentException("(EndDegree - StartDegree) must be smaller than 360 !");
        if (startDegree < speedometerMode.minDegree)
            throw new IllegalArgumentException("StartDegree must be bigger than " + speedometerMode.minDegree
                    + " in " + speedometerMode + " Mode !");
        if (endDegree > speedometerMode.maxDegree)
            throw new IllegalArgumentException("EndDegree must be smaller than " + speedometerMode.maxDegree
                    + " in " + speedometerMode + " Mode !");
    }

    protected abstract SpeedometerDefault getSpeedometerDefault();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        degree = getDegreeAtSpeed(getCorrectSpeed());
    }

    /**
     * draw indicator at correct {@link #degree},
     * this method must call in subSpeedometer's {@code onDraw} method.
     * @param canvas view canvas to draw.
     */
    protected void drawIndicator(Canvas canvas) {
        indicator.draw(canvas, degree);
    }

    /**
     * draw Notes,
     * every Speedometer must call this method at End of it's {@code onDraw()} method.
     * @param canvas view canvas to draw notes.
     */
    protected void drawNotes(Canvas canvas) {
        for (Note note : notes) {
            if (note.getPosition() == Note.Position.CenterSpeedometer)
                note.draw(canvas, getWidth() *.5f, getHeight() *.5f);
            else {
                float y = 0f;
                if (note.getPosition() == Note.Position.CenterIndicator)
                    y = getHeightPa() *.25f + getPadding();
                else if (note.getPosition() == Note.Position.TopIndicator)
                    y = getPadding();
                canvas.save();
                canvas.rotate(90f + getDegree(), getWidth() *.5f, getHeight() *.5f);
                canvas.rotate(-(90f + getDegree()), getWidth() *.5f, y);
                note.draw(canvas, getWidth() *.5f, y);
                canvas.restore();
            }
        }
    }

    /**
     * create canvas to draw {@link #backgroundBitmap}.
     * @return {@link #backgroundBitmap}'s canvas.
     */
    @Override
    protected final Canvas createBackgroundBitmapCanvas() {
        if (getWidth() == 0 || getHeight() == 0)
            return new Canvas();
        backgroundBitmap = Bitmap.createBitmap(getSize(), getSize(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(backgroundBitmap);
        canvas.drawCircle(getSize() *.5f, getSize() *.5f, getSize() *.5f - getPadding(), circleBackPaint);
        return canvas;
    }

    /**
     * @return correct degree where indicator must be.
     */
    protected float getDegree() {
        return degree;
    }

    /**
     * @param speed to know the degree at it.
     * @return correct Degree at that speed.
     */
    protected float getDegreeAtSpeed (float speed) {
        return (speed - getMinSpeed()) * (endDegree - startDegree) /(getMaxSpeed() - getMinSpeed()) + startDegree;
    }

    /**
     * @param degree to know the speed at it.
     * @return correct speed at that degree.
     */
    protected float getSpeedAtDegree (float degree) {
        return (degree - startDegree) * (getMaxSpeed() - getMinSpeed()) /(endDegree - startDegree) + getMinSpeed();
    }

    public int getIndicatorColor() {
        return indicator.getIndicatorColor();
    }

    /**
     * change indicator's color,
     * this option will ignore when using {@link ImageIndicator}.
     * @param indicatorColor new color.
     */
    public void setIndicatorColor(int indicatorColor) {
        indicator.noticeIndicatorColorChange(indicatorColor);
        if (!isAttachedToWindow())
            return;
        invalidate();
    }

    public int getMarkColor() {
        return markColor;
    }

    /**
     * change the color of all marks (if exist),
     * <b>this option is not available for all Speedometers</b>.
     * @param markColor new color.
     */
    public void setMarkColor(int markColor) {
        this.markColor = markColor;
        if (!isAttachedToWindow())
            return;
        invalidate();
    }

    public int getLowSpeedColor() {
        return lowSpeedColor;
    }

    /**
     * change the color of Low Section.
     * @param lowSpeedColor new color.
     */
    public void setLowSpeedColor(int lowSpeedColor) {
        this.lowSpeedColor = lowSpeedColor;
        if (!isAttachedToWindow())
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public int getMediumSpeedColor() {
        return mediumSpeedColor;
    }

    /**
     * change the color of Medium Section.
     * @param mediumSpeedColor new color.
     */
    public void setMediumSpeedColor(int mediumSpeedColor) {
        this.mediumSpeedColor = mediumSpeedColor;
        if (!isAttachedToWindow())
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public int getHighSpeedColor() {
        return highSpeedColor;
    }

    /**
     * change the color of High Section.
     * @param highSpeedColor new color.
     */
    public void setHighSpeedColor(int highSpeedColor) {
        this.highSpeedColor = highSpeedColor;
        if (!isAttachedToWindow())
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public int getBackgroundCircleColor() {
        return backgroundCircleColor;
    }

    /**
     * Circle Background Color,
     * you can set it {@code Color.TRANSPARENT}
     * to remove circle background.
     * @param backgroundCircleColor new Circle Background Color.
     */
    public void setBackgroundCircleColor(int backgroundCircleColor) {
        this.backgroundCircleColor = backgroundCircleColor;
        circleBackPaint.setColor(backgroundCircleColor);
        if (!isAttachedToWindow())
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public float getSpeedometerWidth() {
        return speedometerWidth;
    }

    /**
     * change the width of speedometer's bar.
     * @param speedometerWidth new width in pixel.
     */
    public void setSpeedometerWidth(float speedometerWidth) {
        this.speedometerWidth = speedometerWidth;
        if (!isAttachedToWindow())
            return;
        indicator.noticeSpeedometerWidthChange(speedometerWidth);
        updateBackgroundBitmap();
        invalidate();
    }

    protected int getStartDegree() {
        return startDegree;
    }

    /**
     * change the start of speedometer (at {@link #minSpeed}).
     * @param startDegree the start of speedometer.
     * @throws IllegalArgumentException if {@code startDegree} negative.
     * @throws IllegalArgumentException if {@code startDegree >= endDegree}.
     * @throws IllegalArgumentException if the difference between {@code endDegree and startDegree} bigger than 360.
     */
    public void setStartDegree(int startDegree) {
        setStartEndDegree(startDegree, endDegree);
    }

    protected int getEndDegree() {
        return endDegree;
    }

    /**
     * change the end of speedometer (at {@link #maxSpeed}).
     * @param endDegree the end of speedometer.
     * @throws IllegalArgumentException if {@code endDegree} negative.
     * @throws IllegalArgumentException if {@code endDegree <= startDegree}.
     * @throws IllegalArgumentException if the difference between {@code endDegree and startDegree} bigger than 360.
     */
    public void setEndDegree(int endDegree) {
        setStartEndDegree(startDegree, endDegree);
    }

    /**
     * change start and end of speedometer.
     * @param startDegree the start of speedometer.
     * @param endDegree the end of speedometer.
     * @throws IllegalArgumentException if {@code startDegree OR endDegree} negative.
     * @throws IllegalArgumentException if {@code startDegree >= endDegree}.
     * @throws IllegalArgumentException if the difference between {@code endDegree and startDegree} bigger than 360.
     */
    public void setStartEndDegree (int startDegree, int endDegree) {
        this.startDegree = startDegree;
        this.endDegree = endDegree;
        checkStartAndEndDegree();
        cancelSpeedAnimator();
        degree = getDegreeAtSpeed(getSpeed());
        if (!isAttachedToWindow())
            return;
        updateBackgroundBitmap();
        tremble();
        invalidate();
    }

    /**
     * @return size of speedometer.
     */
    public int getSize() {
        if (speedometerMode == Mode.NORMAL)
            return getWidth();
        if (speedometerMode.isHalf)
            return Math.max(getWidth(), getHeight());
        return Math.max(getWidth(), getHeight())*2 - cutPadding*2;
    }

    /**
     * @return size of speedometer without padding.
     */
    public int getSizePa() {
        return getSize() - (getPadding()*2);
    }

    /**
     * Display new <a href="https://github.com/anastr/SpeedView/wiki/Notes">Note</a>
     * for 3 seconds.
     * @param note to display.
     */
    public void addNote(Note note) {
        addNote(note, 3000);
    }

    /**
     * Display new <a href="https://github.com/anastr/SpeedView/wiki/Notes">Note</a>
     * for custom seconds.
     * @param note to display.
     * @param showTimeMillisecond time to remove Note.
     */
    public void addNote(final Note note, long showTimeMillisecond) {
        note.build(getWidth());
        notes.add(note);
        if (showTimeMillisecond == Note.INFINITE)
            return;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAttachedToWindow()) {
                    notes.remove(note);
                    postInvalidate();
                }
            }
        }, showTimeMillisecond);
        invalidate();
    }

    /**
     * remove All <a href="https://github.com/anastr/SpeedView/wiki/Notes">Notes</a>.
     */
    public void removeAllNotes() {
        notes.clear();
        invalidate();
    }

    /**
     * draw minSpeedText and maxSpeedText at default Position.
     * @param c canvas to draw.
     */
    protected void drawDefMinMaxSpeedPosition(Canvas c) {
        if (getStartDegree()%360 <= 90)
            textPaint.setTextAlign(Paint.Align.RIGHT);
        else if (getStartDegree()%360 <= 180)
            textPaint.setTextAlign(Paint.Align.LEFT);
        else if (getStartDegree()%360 <= 270)
            textPaint.setTextAlign(Paint.Align.CENTER);
        else
            textPaint.setTextAlign(Paint.Align.RIGHT);
        c.save();
        c.rotate(getStartDegree() + 90f, getSize() *.5f, getSize() *.5f);
        c.rotate(-(getStartDegree() + 90f)
                , getSizePa() *.5f - textPaint.getTextSize() + getPadding(), textPaint.getTextSize() + getPadding());
        c.drawText(getMinSpeedText(), getSizePa() *.5f - textPaint.getTextSize() + getPadding()
                , textPaint.getTextSize() + getPadding(), textPaint);
        c.restore();
        if (getEndDegree()%360 <= 90)
            textPaint.setTextAlign(Paint.Align.RIGHT);
        else if (getEndDegree()%360 <= 180)
            textPaint.setTextAlign(Paint.Align.LEFT);
        else if (getEndDegree()%360 <= 270)
            textPaint.setTextAlign(Paint.Align.CENTER);
        else
            textPaint.setTextAlign(Paint.Align.RIGHT);
        c.save();
        c.rotate(getEndDegree() + 90f, getSize() *.5f, getSize() *.5f);
        c.rotate(-(getEndDegree() + 90f)
                , getSizePa() *.5f + textPaint.getTextSize() + getPadding(), textPaint.getTextSize() + getPadding());
        c.drawText(getMaxSpeedText(), getSizePa() *.5f + textPaint.getTextSize() + getPadding()
                , textPaint.getTextSize() + getPadding(), textPaint);
        c.restore();
    }

    public float getIndicatorWidth() {
        return indicator.getIndicatorWidth();
    }

    /**
     * change indicator width, this value have several meaning
     * between {@link Indicator.Indicators}, it will be ignore
     * if using {@link ImageIndicator}.
     * @param indicatorWidth new width in pixel.
     */
    public void setIndicatorWidth(float indicatorWidth) {
        indicator.noticeIndicatorWidthChange(indicatorWidth);
        if (!isAttachedToWindow())
            return;
        invalidate();
    }

    /**
     * call this method to apply/remove blur effect for indicator.
     * @param withEffects effect.
     */
    protected void indicatorEffects(boolean withEffects) {
        indicator.withEffects(withEffects);
    }

    /**
     * change <a href="https://github.com/anastr/SpeedView/wiki/Indicators">indicator shape</a>.<br>
     * this method will get bach indicatorColor and indicatorWidth to default.
     * @param indicator new indicator (Enum value).
     */
    public void setIndicator (Indicator.Indicators indicator) {
        this.indicator = Indicator.createIndicator(getContext(), indicator);
        if(!isAttachedToWindow())
            return;
        this.indicator.setTargetSpeedometer(this);
        invalidate();
    }

    /**
     * add custom <a href="https://github.com/anastr/SpeedView/wiki/Indicators">indicator</a>.
     * @param indicator new indicator.
     */
    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
        if(!isAttachedToWindow())
            return;
        this.indicator.setTargetSpeedometer(this);
        invalidate();
    }

    /**
     * @return correct position of center X to use in drawing.
     */
    protected final float getViewCenterX() {
        switch (speedometerMode) {
            case LEFT:
            case TOP_LEFT:
            case BOTTOM_LEFT:
                return getSize() *.5f - (getWidth() *.5f);
            case RIGHT:
            case TOP_RIGHT:
            case BOTTOM_RIGHT:
                return getSize() *.5f + (getWidth() *.5f);
            default:
                return getSize() *.5f;
        }
    }

    /**
     * @return correct position of center Y to use in drawing.
     */
    protected final float getViewCenterY() {
        switch (speedometerMode) {
            case TOP:
            case TOP_LEFT:
            case TOP_RIGHT:
                return getSize() *.5f - (getHeight() *.5f);
            case BOTTOM:
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                return getSize() *.5f + (getHeight() *.5f);
            default:
                return getSize() *.5f;
        }
    }

    protected final float getViewLeft() {
        return getViewCenterX() - getWidth() *.5f;
    }

    protected final float getViewTop() {
        return getViewCenterY() - getHeight() *.5f;
    }

    protected final float getViewRight() {
        return getViewCenterX() + getWidth() *.5f;
    }

    protected final float getViewBottom() {
        return getViewCenterY() + getHeight() *.5f;
    }

    /**
     * change speedometer shape, style and indicator position.<br>
     * this option will return {@link #startDegree} to the <b>minimum</b> value,
     * and {@link #endDegree} to the <b>maximum</b> value
     * if the speedometerMode doesn't equal to {@code Mode.NORMAL}.
     * @param speedometerMode enum value.
     */
    public void setSpeedometerMode (Mode speedometerMode) {
        this.speedometerMode = speedometerMode;
        if (speedometerMode != Mode.NORMAL) {
            startDegree = speedometerMode.minDegree;
            endDegree = speedometerMode.maxDegree;
        }
        translatedDx = speedometerMode.isRight()  ? - getSize() *.5f + cutPadding : 0;
        translatedDy = speedometerMode.isBottom() ? - getSize() *.5f + cutPadding : 0;
        cancelSpeedAnimator();
        degree = getDegreeAtSpeed(getSpeed());
        indicator.onSizeChange(this);
        if(!isAttachedToWindow())
            return;
        requestLayout();
        updateBackgroundBitmap();
        tremble();
        invalidate();
    }

    public enum Mode {
        NORMAL         (0 ,360*2, false, 1, 1)
        , LEFT         (90 , 270, true , 2, 1)
        , TOP          (180, 360, true , 1, 2)
        , RIGHT        (270, 450, true , 2, 1)
        , BOTTOM       (0  , 180, true , 1, 2)
        , TOP_LEFT     (180, 270, false, 1, 1)
        , TOP_RIGHT    (270, 360, false, 1, 1)
        , BOTTOM_RIGHT (0  , 90 , false, 1, 1)
        , BOTTOM_LEFT  (90 , 180, false, 1, 1);

        final int minDegree;
        final int maxDegree;
        final boolean isHalf;
        final int divWidth;
        final int divHeight;
        Mode (int minDegree, int maxDegree, boolean isHalf, int divWidth, int divHeight) {
            this.minDegree = minDegree;
            this.maxDegree = maxDegree;
            this.isHalf = isHalf;
            this.divWidth = divWidth;
            this.divHeight = divHeight;
        }

        public boolean isLeft(){
            return this == LEFT || this == TOP_LEFT || this == BOTTOM_LEFT;
        }

        public boolean isTop(){
            return this == TOP || this == TOP_LEFT || this == TOP_RIGHT;
        }

        public boolean isRight(){
            return this == RIGHT || this == TOP_RIGHT || this == BOTTOM_RIGHT;
        }

        public boolean isBottom(){
            return this == BOTTOM || this == BOTTOM_LEFT || this == BOTTOM_RIGHT;
        }
    }
}

