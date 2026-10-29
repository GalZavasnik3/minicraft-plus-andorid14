package minicraft.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import minicraft.core.Game;

/** Four visible directional buttons for touch movement, including diagonal input. */
public final class VirtualJoystick extends View {
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint outline = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean up, down, left, right;
    public VirtualJoystick(Context context) {
        super(context); setFocusable(true);
        fill.setStyle(Paint.Style.FILL);
        outline.setColor(Color.WHITE); outline.setStyle(Paint.Style.STROKE); outline.setStrokeWidth(dp(2));
    }
    @Override protected void onDraw(Canvas canvas) {
        float s = Math.min(getWidth(), getHeight());
        drawArrow(canvas, s, 0, up); drawArrow(canvas, s, 1, right);
        drawArrow(canvas, s, 2, down); drawArrow(canvas, s, 3, left);
    }
    @Override public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_CANCEL) { setDirections(false, false, false, false); return true; }
        boolean nextUp = false, nextDown = false, nextLeft = false, nextRight = false;
        for (int i = 0; i < event.getPointerCount(); i++) {
            if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP && i == event.getActionIndex()) continue;
            float x = event.getX(i) / getWidth(), y = event.getY(i) / getHeight();
            nextUp |= y < .42f && x > .23f && x < .77f;
            nextDown |= y > .58f && x > .23f && x < .77f;
            nextLeft |= x < .42f && y > .23f && y < .77f;
            nextRight |= x > .58f && y > .23f && y < .77f;
        }
        setDirections(nextUp, nextDown, nextLeft, nextRight); return true;
    }
    private void setDirections(boolean newUp, boolean newDown, boolean newLeft, boolean newRight) {
        up = newUp; down = newDown; left = newLeft; right = newRight;
        if (Game.input == null) return;
        Game.input.setAction("MOVE-UP", up); Game.input.setAction("MOVE-DOWN", down);
        Game.input.setAction("MOVE-LEFT", left); Game.input.setAction("MOVE-RIGHT", right); invalidate();
    }
    private void drawArrow(Canvas canvas, float s, int direction, boolean pressed) {
        // Keep each triangle inside its own side of the pad: the previous width
        // made the four white outlines cross over one another in the middle.
        float c = s / 2f, a = s * .15f, b = s * .17f, tip = s * .08f;
        Path path = new Path();
        if (direction == 0) { path.moveTo(c, tip); path.lineTo(c - b, a + b); path.lineTo(c + b, a + b); }
        if (direction == 1) { path.moveTo(s - tip, c); path.lineTo(s - a - b, c - b); path.lineTo(s - a - b, c + b); }
        if (direction == 2) { path.moveTo(c, s - tip); path.lineTo(c - b, s - a - b); path.lineTo(c + b, s - a - b); }
        if (direction == 3) { path.moveTo(tip, c); path.lineTo(a + b, c - b); path.lineTo(a + b, c + b); }
        path.close(); fill.setColor(pressed ? Color.rgb(105, 105, 105) : Color.rgb(52, 52, 52)); canvas.drawPath(path, fill); canvas.drawPath(path, outline);
    }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
