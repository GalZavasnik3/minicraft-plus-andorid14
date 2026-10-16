package minicraft.core.io;

import java.util.HashMap;
import minicraft.screen.entry.ArrayEntry;
import minicraft.screen.entry.BooleanEntry;
import minicraft.screen.entry.RangeEntry;

/** Platform-neutral settings; Android uses a stable 60 Hz default. */
public final class Settings {
    private static final HashMap<String, ArrayEntry<?>> options = new HashMap<>();
    static {
        options.put("fps", new RangeEntry("minicraft.settings.fps", 10, 120, 60));
        options.put("diff", new ArrayEntry<>("minicraft.settings.difficulty", "minicraft.settings.difficulty.easy", "minicraft.settings.difficulty.normal", "minicraft.settings.difficulty.hard"));
        options.get("diff").setSelection(1);
        options.put("mode", new ArrayEntry<>("minicraft.settings.mode", "minicraft.settings.mode.survival", "minicraft.settings.mode.creative", "minicraft.settings.mode.hardcore", "minicraft.settings.mode.score"));
        options.put("scoretime", new ArrayEntry<>("minicraft.settings.scoretime", 10, 20, 40, 60, 120));
        options.get("scoretime").setValueVisibility(10, false);
        options.get("scoretime").setValueVisibility(120, false);
        options.put("sound", new BooleanEntry("minicraft.settings.sound", true));
        options.put("autosave", new BooleanEntry("minicraft.settings.autosave", true));
        options.put("hwa", new BooleanEntry("minicraft.settings.opengl_hwa", true));
        options.put("size", new ArrayEntry<>("minicraft.settings.size", 128, 256, 512));
        options.put("theme", new ArrayEntry<>("minicraft.settings.theme", "minicraft.settings.theme.normal", "minicraft.settings.theme.forest", "minicraft.settings.theme.desert", "minicraft.settings.theme.plain", "minicraft.settings.theme.hell"));
        options.put("type", new ArrayEntry<>("minicraft.settings.type", "minicraft.settings.type.island", "minicraft.settings.type.box", "minicraft.settings.type.mountain", "minicraft.settings.type.irregular"));
        options.put("tutorials", new BooleanEntry("minicraft.settings.tutorials", false));
        options.put("quests", new BooleanEntry("minicraft.settings.quests", false));
        options.put("showquests", new BooleanEntry("minicraft.settings.show_quests", true));
        options.get("mode").setChangeAction(value -> options.get("scoretime").setVisible("minicraft.settings.mode.score".equals(value)));
    }
    private Settings() {}
    public static Object get(String option) { return options.get(option.toLowerCase()).getValue(); }
    public static int getIdx(String option) { return options.get(option.toLowerCase()).getSelection(); }
    public static ArrayEntry<?> getEntry(String option) { return options.get(option.toLowerCase()); }
    public static void set(String option, Object value) { options.get(option.toLowerCase()).setValue(value); }
    public static void setIdx(String option, int idx) { options.get(option.toLowerCase()).setSelection(idx); }
}
