package me.appfriends.androidsample.sampleapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by haowang on 11/10/16.
 */

public class RoundCornerImageView extends ImageView {


    public static float radius = 18.0f;

    public RoundCornerImageView(Context context) {
        super(context);
    }

    public RoundCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundCornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path clipPath = new Path();
        int width = this.getWidth();
        int height = this.getHeight();
        RectF rect = new RectF(0, 0, width, height);
        clipPath.addRoundRect(rect, width / 2, width / 2, Path.Direction.CW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }
}
