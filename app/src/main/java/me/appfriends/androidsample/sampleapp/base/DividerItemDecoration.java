package me.appfriends.androidsample.sampleapp.base;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Mike Dai Wang on 2016-11-02.
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    public static final String TAG = DividerItemDecoration.class.getSimpleName();

    private Drawable divider;
    private boolean includeLastItem;

    public DividerItemDecoration(Drawable divider) {
        this(divider, false);
    }

    public DividerItemDecoration(Drawable divider, boolean includeLastItem) {
        this.divider = divider;
        this.includeLastItem = includeLastItem;
    }

    @Override
    public void getItemOffsets(Rect outRect,
                               View view,
                               RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) == 0) {
            return;
        }

        outRect.top = divider.getIntrinsicHeight();
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        int dividerLeft = parent.getPaddingLeft();
        int dividerRight = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - (includeLastItem ? 0 : 1); i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int dividerTop = child.getBottom() + params.bottomMargin;
            int dividerBottom = dividerTop + divider.getIntrinsicHeight();

            divider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
            divider.draw(canvas);
        }
    }

}
