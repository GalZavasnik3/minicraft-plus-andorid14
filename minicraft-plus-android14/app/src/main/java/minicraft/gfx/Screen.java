package minicraft.gfx;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import minicraft.core.Renderer;
import minicraft.core.Updater;
import minicraft.gfx.SpriteLinker.LinkedSprite;
import minicraft.gfx.SpriteLinker.SpriteType;

/** Software pixel renderer for the original 288x192 Minicraft+ viewport. */
public class Screen {
    public static final int w = Renderer.WIDTH;
    public static final int h = Renderer.HEIGHT;
    public static final Point center = new Point(w / 2, h / 2);
    private static final int BIT_MIRROR_X = 1, BIT_MIRROR_Y = 2, MAX_DARK = 128;
    private final int[] pixels = new int[w * h];
    private final HashMap<Point, Integer> lights = new HashMap<>();
    private int xOffset, yOffset;
    public int[] getPixels() { return pixels; }
    public void flush() { }
    public void clear(int color) { Arrays.fill(pixels, argb(color)); }
    private static int argb(int color) { return 0xff000000 | (color & 0x00ffffff); }
    private void setPixel(int x, int y, int color) { if (x >= 0 && y >= 0 && x < w && y < h) pixels[x + y * w] = argb(color); }

    public void render(int xp, int yp, int xt, int yt, int bits, MinicraftImage sheet) { render(xp, yp, xt, yt, bits, sheet, -1); }
    public void render(int xp, int yp, int xt, int yt, int bits, MinicraftImage sheet, int whiteTint) { render(xp, yp, xt, yt, bits, sheet, whiteTint, false); }
    public void render(int xp, int yp, int xt, int yt, int bits, MinicraftImage sheet, int whiteTint, boolean fullbright) { render(xp, yp, xt, yt, bits, sheet, whiteTint, fullbright, 0); }
    public void render(int xp, int yp, LinkedSprite sprite) { render(xp, yp, sprite.getSprite()); }
    public void render(int xp, int yp, Sprite sprite) { render(xp, yp, sprite, false); }
    public void render(int xp, int yp, Sprite sprite, boolean fullbright) { render(xp, yp, sprite, 0, fullbright, 0); }
    public void render(int xp, int yp, Sprite sprite, int mirror, boolean fullbright) { render(xp, yp, sprite, mirror, fullbright, 0); }
    public void render(int xp, int yp, Sprite sprite, int mirror, boolean fullbright, int color) {
        if (sprite == null) return;
        boolean mirrorX = (mirror & BIT_MIRROR_X) != 0, mirrorY = (mirror & BIT_MIRROR_Y) != 0;
        for (int row = 0; row < sprite.spritePixels.length; row++) {
            int sourceRow = mirrorY ? sprite.spritePixels.length - 1 - row : row;
            for (int column = 0; column < sprite.spritePixels[sourceRow].length; column++) {
                int sourceColumn = mirrorX ? sprite.spritePixels[sourceRow].length - 1 - column : column;
                render(xp + column * 8, yp + row * 8, sprite.spritePixels[sourceRow][sourceColumn], mirror, sprite.color, fullbright, color);
            }
        }
    }
    public void render(int xp, int yp, Sprite.Px pixel) { render(xp, yp, pixel, -1); }
    public void render(int xp, int yp, Sprite.Px pixel, int whiteTint) { render(xp, yp, pixel, 0, whiteTint); }
    public void render(int xp, int yp, Sprite.Px pixel, int mirror, int whiteTint) { render(xp, yp, pixel, mirror, whiteTint, false); }
    public void render(int xp, int yp, Sprite.Px pixel, int mirror, int whiteTint, boolean fullbright) { render(xp, yp, pixel, mirror, whiteTint, fullbright, 0); }
    public void render(int xp, int yp, Sprite.Px pixel, int mirror, int whiteTint, boolean fullbright, int color) {
        render(xp, yp, pixel.x, pixel.y, pixel.mirror ^ mirror, pixel.sheet, whiteTint, fullbright, color);
    }
    public void render(int xp, int yp, int xt, int yt, int bits, MinicraftImage sheet, int whiteTint, boolean fullbright, int color) {
        if (sheet != null) render(xp - xOffset, yp - yOffset, xt * 8, yt * 8, 8, 8, sheet, bits, whiteTint, fullbright, color);
    }
    public void render(int xp, int yp, int xt, int yt, int tileWidth, int tileHeight, MinicraftImage sheet) { render(xp, yp, xt, yt, tileWidth, tileHeight, sheet, 0); }
    public void render(int xp, int yp, int xt, int yt, int tileWidth, int tileHeight, MinicraftImage sheet, int mirrors) { render(xp, yp, xt, yt, tileWidth, tileHeight, sheet, mirrors, -1); }
    public void render(int xp, int yp, int xt, int yt, int tileWidth, int tileHeight, MinicraftImage sheet, int mirrors, int whiteTint) { render(xp, yp, xt, yt, tileWidth, tileHeight, sheet, mirrors, whiteTint, false); }
    public void render(int xp, int yp, int xt, int yt, int tileWidth, int tileHeight, MinicraftImage sheet, int mirrors, int whiteTint, boolean fullbright) { render(xp, yp, xt, yt, tileWidth, tileHeight, sheet, mirrors, whiteTint, fullbright, 0); }
    public void render(int xp, int yp, int xt, int yt, int tileWidth, int tileHeight, MinicraftImage sheet, int mirrors, int whiteTint, boolean fullbright, int color) {
        if (sheet == null || xt < 0 || yt < 0 || xt + tileWidth > sheet.width || yt + tileHeight > sheet.height) return;
        boolean mirrorX = (mirrors & BIT_MIRROR_X) != 0, mirrorY = (mirrors & BIT_MIRROR_Y) != 0;
        for (int y = 0; y < tileHeight; y++) {
            int sy = mirrorY ? tileHeight - 1 - y : y;
            for (int x = 0; x < tileWidth; x++) {
                int sx = mirrorX ? tileWidth - 1 - x : x;
                int source = sheet.pixels[xt + sx + (yt + sy) * sheet.width];
                if ((source >>> 24) == 0) continue;
                int output = fullbright ? Color.WHITE : color != 0 ? color : (whiteTint != -1 && source == 0x01ffffff ? whiteTint : source);
                setPixel(xp + x, yp + y, output);
            }
        }
    }
    public void fillRect(int xp, int yp, int width, int height, int color) { for (int y = yp; y < yp + height; y++) for (int x = xp; x < xp + width; x++) setPixel(x, y, color); }
    public void drawRect(int xp, int yp, int width, int height, int color) { drawLine(xp, yp, xp + width, yp, color); drawLine(xp, yp, xp, yp + height, color); drawLine(xp + width, yp, xp + width, yp + height, color); drawLine(xp, yp + height, xp + width, yp + height, color); }
    public void drawAxisLine(int xp, int yp, int axis, int length, int color) { if (axis == 0) drawLine(xp, yp, xp + length, yp, color); else drawLine(xp, yp, xp, yp + length, color); }
    public void drawLine(int x0, int y0, int x1, int y1, int color) {
        int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1, dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1, error = dx + dy;
        while (true) { setPixel(x0, y0, color); if (x0 == x1 && y0 == y1) return; int twice = error * 2; if (twice >= dy) { error += dy; x0 += sx; } if (twice <= dx) { error += dx; y0 += sy; } }
    }
    public void drawLineSpecial(int xp, int yp, int axis, int length) {
        for (int i = 0; i < length; i++) { int x = axis == 0 ? xp + i : xp, y = axis == 0 ? yp : yp + i; if (x >= 0 && y >= 0 && x < w && y < h) pixels[x + y * w] = Color.getLightnessFromRGB(pixels[x + y * w]) >= .5 ? argb(Color.BLACK) : argb(Color.WHITE); }
    }
    public void setOffset(int xOffset, int yOffset) { this.xOffset = xOffset; this.yOffset = yOffset; }
    public void renderLight(int x, int y, int radius) { lights.put(new Point(x - xOffset, y - yOffset), radius); }
    public void overlay(int currentLevel, int xa, int ya) {
        double darkness = 0;
        if (currentLevel < 3 || currentLevel >= 5) darkness = 1;
        else {
            int segment = Updater.dayLength / 4; double progress = (Updater.tickCount % segment) / (double) segment;
            switch (Updater.getTime()) { case Morning: darkness = Updater.pastDay1 ? (1 - progress) * .8 : 0; break; case Evening: darkness = progress * .8; break; case Night: darkness = .8; break; default: darkness = 0; }
        }
        if (darkness > 0) {
            for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
                double lit = 0;
                for (Map.Entry<Point, Integer> entry : lights.entrySet()) { int dx = x - entry.getKey().x, dy = y - entry.getKey().y, radius = entry.getValue(); double d = Math.sqrt(dx * dx + dy * dy); if (d < radius) lit = Math.max(lit, 1 - d / radius); }
                double shade = darkness * (1 - lit);
                int source = pixels[x + y * w]; int red = (int)(((source >> 16) & 255) * (1 - shade)); int green = (int)(((source >> 8) & 255) * (1 - shade)); int blue = (int)((source & 255) * (1 - shade));
                pixels[x + y * w] = 0xff000000 | red << 16 | green << 8 | blue;
            }
        }
        lights.clear();
    }
}
