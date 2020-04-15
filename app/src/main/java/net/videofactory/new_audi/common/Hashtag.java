package net.videofactory.new_audi.common;

import android.content.Context;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Utae on 2016-02-02.
 */
public class Hashtag extends ClickableSpan {

    private Context context;
    private OnHashtagClickListener onHashtagClickListener;

    public Hashtag(Context context) {
        super();
        this.context = context;
    }

    public void setOnHashtagClickListener(OnHashtagClickListener onHashtagClickListener) {
        this.onHashtagClickListener = onHashtagClickListener;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(ds.linkColor);
        ds.setARGB(255, 30, 144, 255);
    }

    @Override
    public void onClick(View widget) {
        onHashtagClickListener.onHashtagClick(widget);
//        TextView tv = (TextView) widget;
//        Spanned s = (Spanned) tv.getText();
//        int start = s.getSpanStart(this);
//        int end = s.getSpanEnd(this);
//        String theWord = s.subSequence(start + 1, end).toString();
//        Toast.makeText(context, String.format("Tags for tags: %s", theWord), 10 ).show();
    }

    public interface OnHashtagClickListener{
        void onHashtagClick(View widget);
    }
}
