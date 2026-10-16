package minicraft.core.io;

import java.io.InputStream;
import java.util.HashMap;
import org.jetbrains.annotations.Nullable;

/** Safe Android audio facade. Asset sound loading can be expanded without affecting game logic. */
public class Sound {
    private static final HashMap<String, Sound> sounds = new HashMap<>();
    public static void resetSounds() { sounds.clear(); }
    public static void loadSound(String key, InputStream input, String pack) { sounds.put(key, new Sound()); }
    @Nullable public static Sound getSound(String key) { return sounds.get(key); }
    public static void play(String key) { Sound sound = sounds.get(key); if (sound != null) sound.play(); }
    public static void loop(String key, int count) { Sound sound = sounds.get(key); if (sound != null) sound.loop(count); }
    public static void tick() { }
    public void play() { }
    @Deprecated public void loop(int count) { }
}
