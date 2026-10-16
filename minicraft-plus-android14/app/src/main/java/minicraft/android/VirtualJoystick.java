package minicraft.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import minicraft.core.Game;

/** The circular joystick styling and motion behaviour retained from Pigeoncraft's control pad. */
public final class VirtualJoystick extends View {
    private final Paint base = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint handle = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float dx, dy;
    public VirtualJoystick(Context context) {
        super(context); setFocusable(true);
        base.setColor(Color.WHITE); base.setStyle(Paint.Style.FILL_AND_STROKE); base.setAlpha(150);
        handle.setColor(Color.rgb(68, 68, 68)); handle.setStyle(Paint.Style.FILL_AND_STROKE); handle.setAlpha(210);
    }
    @Override protected void onDraw(Canvas canvas) {
        float size = Math.min(getWidth(), getHeight()), center = size / 2f, radius = size / 2f - dp(8), knob = size * .25f;
        canvas.drawCircle(center, center, radius, base); canvas.drawCircle(center + dx, center + dy, knob, handle);
    }
    @Override public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_CANCEL) { dx = dy = 0; updateActions(); invalidate(); return true; }
        float size = Math.min(getWidth(), getHeight()), center = size / 2f, limit = size / 2f - size * .25f;
        dx = event.getX() - center; dy = event.getY() - center; float distance = (float) Math.sqrt(dx * dx + dy * dy);
        if (distance > limit && distance > 0) { dx = dx / distance * limit; dy = dy / distance * limit; }
        updateActions(); invalidate(); return true;
    }
    private void updateActions() {
        if (Game.input == null) return;
        float limit = Math.min(getWidth(), getHeight()) * .18f;
        Game.input.setAction("MOVE-UP", dy < -limit); Game.input.setAction("MOVE-DOWN", dy > limit);
        Game.input.setAction("MOVE-LEFT", dx < -limit); Game.input.setAction("MOVE-RIGHT", dx > limit);
    }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
