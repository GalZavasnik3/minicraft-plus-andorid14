package minicraft.screen;

import minicraft.core.io.InputHandler;
import minicraft.gfx.Screen;
import minicraft.util.TutorialElement;
import org.json.JSONObject;

/** Touch controls are self-explanatory; desktop keyboard tutorials are omitted. */
public final class TutorialDisplayHandler {
    private TutorialDisplayHandler() {
    }

    public static void updateCompletedElement(TutorialElement element) {
    }

    public static void skipCurrent() {
    }

    public static boolean inTutorial() {
        return false;
    }

    public static boolean inQuests() {
        return false;
    }

    public static void turnOffTutorials() {
    }

    public static void tick(InputHandler input) {
    }

    public static void render(Screen screen) {
    }

    public static void reset(boolean initial) {
    }

    public static void load(JSONObject json) {
    }

    public static void save(JSONObject json) {
    }
}
