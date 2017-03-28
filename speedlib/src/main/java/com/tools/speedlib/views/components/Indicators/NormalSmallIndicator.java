package com.tools.speedlib.views.components.Indicators;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class NormalSmallIndicator extends Indicator<NormalSmallIndicator> {

    private Path indicatorPath = new Path();

    public NormalSmallIndicator(Context context) {
        super(context);
        updateIndicator();
    }

    @Override
    protected float getDefaultIndicatorWidth() {
        return dpTOpx(12f);
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
        indicatorPath.reset();
        indicatorPath.moveTo(getCenterX(), getViewSize()/5f + getPadding());
        float indicatorBottom = getViewSize()*3f/5f + getPadding();
        indicatorPath.lineTo(getCenterX() - getIndicatorWidth(), indicatorBottom);
        indicatorPath.lineTo(getCenterX() + getIndicatorWidth(), indicatorBottom);
        RectF rectF = new RectF(getCenterX() - getIndicatorWidth(), indicatorBottom - getIndicatorWidth()
                , getCenterX() + getIndicatorWidth(), indicatorBottom + getIndicatorWidth());
        indicatorPath.addArc(rectF, 0f, 180f);

        indicatorPaint.setColor(getIndicatorColor());
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
