package minicraft.screen;

import minicraft.util.Quest;
import org.json.JSONObject;

import java.util.HashSet;

/** Android placeholder for the desktop quest data-pack screen. */
public class QuestsDisplay extends Display {
    public QuestsDisplay() {
        super(true, true);
    }

    public static void refreshDisplayableQuests() {
    }

    public static HashSet<Quest> getDisplayableQuests() {
        return new HashSet<>();
    }

    public static void resetGameQuests() {
    }

    public static void load(JSONObject json) {
    }

    public static void save(JSONObject json) {
    }
}
