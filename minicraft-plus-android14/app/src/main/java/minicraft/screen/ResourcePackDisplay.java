package minicraft.screen;

import android.graphics.Bitmap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import minicraft.android.AndroidAssets;
import minicraft.core.Renderer;
import minicraft.core.io.Localization;
import minicraft.core.io.Sound;
import minicraft.gfx.MinicraftImage;
import minicraft.gfx.SpriteAnimation;
import minicraft.gfx.SpriteLinker;
import minicraft.gfx.SpriteLinker.SpriteMeta;
import minicraft.gfx.SpriteLinker.SpriteType;
import minicraft.util.BookData;
import org.json.JSONObject;

/** Loads the unmodified Minicraft+ PC asset pack packaged in the APK. */
public class ResourcePackDisplay extends Display {
    public ResourcePackDisplay() { super(true, true); }
    public static void initPacks() { }
    public static ArrayList<String> getLoadedPacks() { return new ArrayList<>(); }
    public static void loadResourcePacks(ArrayList<String> ignored) { }
    public static void loadResourcePacks(String[] ignored) { }
    public static void releaseUnloadedPacks() { }
    public static void reloadResources() {
        Renderer.spriteLinker.resetSprites();
        Localization.resetLocalizations();
        BookData.resetBooks();
        Sound.resetSounds();
        SpriteAnimation.resetMetadata();
        for (SpriteType type : SpriteType.values()) loadTextureType(type);
        loadLocalizations();
        loadBooks();
        SpriteAnimation.refreshAnimations();
        Renderer.spriteLinker.updateLinkedSheets();
        Localization.loadLanguage();
    }
    private static void loadTextureType(SpriteType type) {
        String folder;
        switch (type) { case Entity: folder = "entity"; break; case Gui: folder = "gui"; break; case Item: folder = "item"; break; default: folder = "tile"; }
        String root = "assets/textures/" + folder;
        for (String path : AndroidAssets.list(root)) {
            if (!path.endsWith(".png")) continue;
            try {
                Bitmap bitmap = AndroidAssets.decode(path);
                String key = path.substring(root.length() + 1, path.length() - 4);
                Renderer.spriteLinker.setSprite(type, key, new MinicraftImage(bitmap));
                if (type == SpriteType.Tile) loadTileMetadata(path, key, bitmap);
            } catch (RuntimeException ignored) { }
        }
    }
    private static void loadTileMetadata(String imagePath, String key, Bitmap bitmap) {
        try {
            String metadataPath = imagePath + ".json";
            String text = AndroidAssets.readText(metadataPath);
            JSONObject json = new JSONObject(text); SpriteMeta meta = new SpriteMeta();
            JSONObject animation = json.optJSONObject("animation");
            if (animation != null) { meta.frametime = animation.optInt("frametime", 0); meta.frames = Math.max(1, bitmap.getHeight() / 16); }
            JSONObject border = json.optJSONObject("border");
            if (border != null) { meta.border = emptyToNull(border.optString("key")); meta.corner = emptyToNull(border.optString("corner")); }
            SpriteAnimation.setMetadata(key, meta);
        } catch (IOException | org.json.JSONException ignored) { }
    }
    private static String emptyToNull(String value) { return value == null || value.isEmpty() ? null : value; }
    private static void loadLocalizations() {
        for (String path : AndroidAssets.list("assets/localization")) {
            if (!path.endsWith(".json")) continue;
            try {
                String tag = path.substring(path.lastIndexOf('/') + 1, path.length() - 5);
                Locale locale = Locale.forLanguageTag(tag);
                Localization.addLocale(locale, new Localization.LocaleInformation(locale, locale.getDisplayLanguage(Locale.US), locale.getDisplayCountry(Locale.US)));
                Localization.addLocalization(locale, AndroidAssets.readText(path));
            } catch (IOException ignored) { }
        }
    }
    private static void loadBooks() {
        BookData.about = () -> readBook("assets/books/about.txt");
        BookData.credits = () -> readBook("assets/books/credits.txt");
        BookData.instructions = () -> readBook("assets/books/instructions.txt");
        BookData.antVenomBook = () -> readBook("assets/books/antidous.txt");
        BookData.storylineGuide = () -> readBook("assets/books/game_guide.txt");
    }
    private static String readBook(String path) {
        try { return BookData.loadBook(AndroidAssets.readText(path)); }
        catch (IOException ignored) { return ""; }
    }
}
