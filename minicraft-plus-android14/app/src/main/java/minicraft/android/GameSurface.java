package minicraft.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import minicraft.core.Renderer;

/** Nearest-neighbour presentation of the original PC frame buffer. */
public final class GameSurface extends View {
    private final Bitmap frame = Bitmap.createBitmap(Renderer.WIDTH, Renderer.HEIGHT, Bitmap.Config.ARGB_8888);
    private final Paint paint = new Paint();
    private final Rect source = new Rect(0, 0, Renderer.WIDTH, Renderer.HEIGHT);
    private final Rect destination = new Rect();
    public GameSurface(Context context) { super(context); paint.setFilterBitmap(false); paint.setAntiAlias(false); setFocusable(true); }
    public void present(int[] pixels) { frame.setPixels(pixels, 0, Renderer.WIDTH, 0, 0, Renderer.WIDTH, Renderer.HEIGHT); postInvalidateOnAnimation(); }
    @Override protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        float scale = Math.min(getWidth() / (float) Renderer.WIDTH, getHeight() / (float) Renderer.HEIGHT);
        int width = Math.round(Renderer.WIDTH * scale), height = Math.round(Renderer.HEIGHT * scale);
        int left = (getWidth() - width) / 2, top = (getHeight() - height) / 2;
        destination.set(left, top, left + width, top + height);
        canvas.drawBitmap(frame, source, destination, paint);
    }
}
