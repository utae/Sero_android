package net.videofactory.new_audi.custom_view.clipping_bar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.util.TypedValue;

/**
 * Created by Utae on 2015-12-07.
 */
public class ConnectingLine {

    private Paint paint;

    private float y;

    public ConnectingLine(Context context, float y, float connectingLineWeight,
                          int connectingLineColor) {

        Resources res = context.getResources();

        float connectingLineWeight1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                connectingLineWeight,
                res.getDisplayMetrics());

        // Initialize the paint, set values
        paint = new Paint();
        paint.setColor(connectingLineColor);
        paint.setStrokeWidth(connectingLineWeight1);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setAntiAlias(true);

        y = y;
    }
}
