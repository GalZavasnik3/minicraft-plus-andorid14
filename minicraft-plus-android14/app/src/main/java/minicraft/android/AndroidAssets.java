package minicraft.android;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Central access to the original PC assets bundled into the Android application. */
public final class AndroidAssets {
    private static Context context;
    private AndroidAssets() {}
    public static void initialize(Context value) { context = value.getApplicationContext(); }
    public static Context context() { if (context == null) throw new IllegalStateException("Assets were requested before Android initialization"); return context; }
    public static InputStream open(String path) throws IOException { return context().getAssets().open(path, AssetManager.ACCESS_STREAMING); }
    public static Bitmap decode(String path) {
        try (InputStream input = open(path)) { Bitmap bitmap = BitmapFactory.decodeStream(input); if (bitmap == null) throw new IllegalArgumentException("Unable to decode " + path); return bitmap; }
        catch (IOException exception) { throw new IllegalArgumentException("Missing asset " + path, exception); }
    }
    public static String readText(String path) throws IOException {
        try (InputStream input = open(path); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096]; int read; while ((read = input.read(buffer)) != -1) output.write(buffer, 0, read);
            return output.toString(StandardCharsets.UTF_8.name());
        }
    }
    public static List<String> list(String root) {
        ArrayList<String> output = new ArrayList<>(); list(root, output); return output;
    }
    private static void list(String root, List<String> output) {
        try {
            String[] names = context().getAssets().list(root); if (names == null) return;
            for (String name : names) { String child = root.isEmpty() ? name : root + "/" + name; String[] nested = context().getAssets().list(child); if (nested != null && nested.length > 0) list(child, output); else output.add(child); }
        } catch (IOException ignored) { }
    }
}
