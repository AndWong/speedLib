package com.tools.speedlib.views.components.Indicators;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Path;
import android.graphics.Shader;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class TriangleIndicator extends Indicator<TriangleIndicator> {

    private Path indicatorPath = new Path();

    public TriangleIndicator(Context context) {
        super(context);
        updateIndicator();
    }

    @Override
    protected float getDefaultIndicatorWidth() {
        return dpTOpx(25f);
    }

    @Override
    public void draw(Canvas canvas, float degree) {
        canvas.save();
        canvas.rotate(90f + degree, getCenterX(), getCenterY());
        canvas.drawPath(indicatorPath, indicatorPaint);
        canvas.restore();
    }

    @Override
    protected void updateIndicator() {
        indicatorPath = new Path();
        float indicatorH = getPadding() + getSpeedometerWidth() + dpTOpx(5);
        indicatorPath.moveTo(getCenterX(), indicatorH);
        indicatorPath.lineTo(getCenterX() - getIndicatorWidth(), indicatorH + getIndicatorWidth());
        indicatorPath.lineTo(getCenterX() + getIndicatorWidth(), indicatorH + getIndicatorWidth());
        indicatorPath.moveTo(0f, 0f);

        int endColor = Color.argb(0, Color.red(getIndicatorColor()), Color.green(getIndicatorColor())
                , Color.blue(getIndicatorColor()));
        Shader linearGradient = new LinearGradient(getCenterX(), indicatorH, getCenterX(), indicatorH + getIndicatorWidth()
                , getIndicatorColor(), endColor, Shader.TileMode.CLAMP);
        indicatorPaint.setShader(linearGradient);
    }

    @Override
    protected void setWithEffects(boolean withEffects) {
        if (withEffects && !isInEditMode()) {
            indicatorPaint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.SOLID));
        }
        else {
            indicatorPaint.setMaskFilter(null);
        }
    }
}
