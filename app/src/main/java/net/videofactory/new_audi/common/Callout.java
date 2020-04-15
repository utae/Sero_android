package net.videofactory.new_audi.common;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by Utae on 2016-02-02.
 */
public class Callout extends ClickableSpan {
    private Context context;
    private OnCalloutClickListener onCalloutClickListener;

    public Callout(Context context) {
        super();
        this.context = context;
    }

    public void setOnCalloutClickListener(OnCalloutClickListener onCalloutClickListener) {
        this.onCalloutClickListener = onCalloutClickListener;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setARGB(255, 51, 51, 51);
        ds.setTypeface(Typeface.DEFAULT_BOLD);

    }

    @Override
    public void onClick(View widget) {
        onCalloutClickListener.onCalloutClick(widget);

//        TextView tv = (TextView) widget;
//        Spanned s = (Spanned) tv.getText();
//        int start = s.getSpanStart(this);
//        int end = s.getSpanEnd(this);
//        String theWord = s.subSequence(start + 1, end).toString();
//        Toast.makeText(context, String.format("Here's a cool person: %s", theWord), 10).show();
    }

    public interface OnCalloutClickListener{
        void onCalloutClick(View widget);
    }
}
