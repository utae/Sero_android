package net.videofactory.new_audi.custom_view.clipping_bar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Utae on 2016-01-28.
 */
public class Bar {

    private Paint barPaint;

    private float leftX, rightX, y;

    public Bar(Context context, float x, float y, float length, float barHeight, int barColor) {
        this.leftX = x;
        this.rightX = x + length;
        this.y = y;

        barPaint = new Paint();
        barPaint.setColor(barColor);
        barPaint.setStrokeWidth(barHeight);
        barPaint.setAntiAlias(true);
    }

    public void draw(Canvas canvas){
        canvas.drawLine(leftX, y, rightX, y, barPaint);
    }

    public float getLeftX() {
        return leftX;
    }

    public float getRightX() {
        return rightX;
    }


}
