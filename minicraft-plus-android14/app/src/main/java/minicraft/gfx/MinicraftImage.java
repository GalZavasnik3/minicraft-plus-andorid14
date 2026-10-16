package minicraft.gfx;

import android.graphics.Bitmap;

/** Pixel-sheet representation backed by Android's Bitmap decoder. */
public class MinicraftImage {
    public static final int boxWidth = 8;
    public final int width, height;
    public final int[] pixels;
    public MinicraftImage(int width, int height) {
        if (width < 1 || height < 1) throw new IllegalArgumentException("Image dimensions must be positive");
        this.width = width; this.height = height; this.pixels = new int[width * height];
    }
    public MinicraftImage(Bitmap bitmap) { this(bitmap, bitmap.getWidth(), bitmap.getHeight()); }
    public MinicraftImage(Bitmap bitmap, int requestedWidth, int requestedHeight) {
        if (bitmap == null || requestedWidth < 1 || requestedHeight < 1) throw new IllegalArgumentException("Invalid bitmap");
        this.width = Math.min(requestedWidth, bitmap.getWidth());
        this.height = Math.min(requestedHeight, bitmap.getHeight());
        this.pixels = new int[this.width * this.height];
        bitmap.getPixels(this.pixels, 0, this.width, 0, 0, this.width, this.height);
        for (int i = 0; i < pixels.length; i++) {
            int argb = pixels[i];
            pixels[i] = ((argb >>> 24) == 0 ? 0 : 0x01000000) | (argb & 0x00ffffff);
        }
    }
    public static MinicraftImage createDefaultCompatible(Bitmap bitmap) throws MinicraftImageDimensionIncompatibleException {
        validateImageDimension(bitmap); return new MinicraftImage(bitmap);
    }
    public static void validateImageDimension(Bitmap bitmap) throws MinicraftImageDimensionIncompatibleException {
        if (bitmap.getWidth() % 8 != 0 || bitmap.getHeight() % 8 != 0) throw new MinicraftImageDimensionIncompatibleException(bitmap.getWidth(), bitmap.getHeight());
    }
    public static void validateImageDimension(Bitmap bitmap, int width, int height) throws MinicraftImageRequestOutOfBoundsException {
        if (bitmap.getWidth() < width || bitmap.getHeight() < height) throw new MinicraftImageRequestOutOfBoundsException(bitmap.getWidth(), bitmap.getHeight(), width, height);
    }
    public static class MinicraftImageDimensionIncompatibleException extends Exception {
        private final int width, height;
        public MinicraftImageDimensionIncompatibleException(int width, int height) { this.width = width; this.height = height; }
        public int getWidth() { return width; } public int getHeight() { return height; }
    }
    public static class MinicraftImageRequestOutOfBoundsException extends Exception {
        private final int sourceWidth, sourceHeight, requestedWidth, requestedHeight;
        public MinicraftImageRequestOutOfBoundsException(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            this.sourceWidth = sourceWidth; this.sourceHeight = sourceHeight; this.requestedWidth = requestedWidth; this.requestedHeight = requestedHeight;
        }
        public int getSourceWidth() { return sourceWidth; } public int getSourceHeight() { return sourceHeight; }
        public int getRequestedWidth() { return requestedWidth; } public int getRequestedHeight() { return requestedHeight; }
    }
}
