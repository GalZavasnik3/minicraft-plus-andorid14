package minicraft.core.io;

import android.view.KeyEvent;
import com.studiohartman.jamepad.ControllerButton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;

/** Shared action map driven by Android hardware keys and the three on-screen controls. */
public class InputHandler {
    public String keyToChange;
    private String keyChanged;
    private boolean overwrite;
    private final Map<String, String> keymap = new LinkedHashMap<>();
    private final Map<String, PhysicalKey> keyboard = new HashMap<>();
    private String lastKeyTyped = "";
    private String keyTypedBuffer = "";
    private @Nullable Predicate<String> inputMask;

    public InputHandler() {
        resetKeyBindings();
        keyboard.put("SHIFT", new PhysicalKey());
        keyboard.put("CTRL", new PhysicalKey());
        keyboard.put("ALT", new PhysicalKey());
    }

    private void initKeyMap() {
        keymap.put("MOVE-UP", "UP|W"); keymap.put("MOVE-DOWN", "DOWN|S");
        keymap.put("MOVE-LEFT", "LEFT|A"); keymap.put("MOVE-RIGHT", "RIGHT|D");
        keymap.put("CURSOR-UP", "UP"); keymap.put("CURSOR-DOWN", "DOWN");
        keymap.put("CURSOR-LEFT", "LEFT"); keymap.put("CURSOR-RIGHT", "RIGHT");
        keymap.put("SELECT", "ENTER"); keymap.put("EXIT", "ESCAPE");
        keymap.put("QUICKSAVE", "R"); keymap.put("ATTACK", "C|SPACE|ENTER");
        keymap.put("MENU", "X|E"); keymap.put("CRAFT", "Z|SHIFT-E");
        keymap.put("PICKUP", "V|P"); keymap.put("DROP-ONE", "Q");
        keymap.put("DROP-STACK", "SHIFT-Q"); keymap.put("SEARCHER-BAR", "SHIFT-F");
        keymap.put("PAGE-UP", "PAGE_UP"); keymap.put("PAGE-DOWN", "PAGE_DOWN");
        keymap.put("PAUSE", "ESCAPE"); keymap.put("POTIONEFFECTS", "P");
        keymap.put("SIMPPOTIONEFFECTS", "O"); keymap.put("EXPANDQUESTDISPLAY", "L");
        keymap.put("TOGGLEHUD", "F1"); keymap.put("SCREENSHOT", "F2");
        keymap.put("INFO", "SHIFT-I"); keymap.put("FULLSCREEN", "F11");
    }

    public void resetKeyBindings() { keymap.clear(); initKeyMap(); }
    public void tick() {
        lastKeyTyped = keyTypedBuffer;
        keyTypedBuffer = "";
        inputMask = null;
        for (PhysicalKey key : keyboard.values()) key.tick();
    }
    public void releaseAll() { for (PhysicalKey key : keyboard.values()) key.release(); }
    public void setKey(String action, String physical) { if (action != null && keymap.containsKey(action.toUpperCase())) keymap.put(action.toUpperCase(), physical); }
    public String getChangedKey() { String out = keyChanged + ";" + keymap.get(keyChanged); keyChanged = null; return out; }
    public String getMapping(String action) { return keymap.getOrDefault(action.toUpperCase(), "NO_KEY").replace("|", "/"); }
    public String selectMapping(String keys, String buttons) { return keys; }
    public int getLastInputType() { return 0; }
    public void maskInput(@Nullable Predicate<String> filter) { inputMask = filter == null ? key -> true : filter; }

    public Key getMappedKey(String mapping) {
        String text = mapping == null ? "" : mapping.toUpperCase(java.util.Locale.ENGLISH);
        String resolved = keymap.getOrDefault(text, text);
        if (resolved.contains("|")) {
            ArrayList<Key> keys = new ArrayList<>();
            for (String item : resolved.split("\\|")) keys.add(getMappedKey(item));
            return new OrKey(keys);
        }
        HashSet<Key> keys = new HashSet<>();
        for (String item : resolved.split("-")) keys.add(getKey(item));
        return keys.size() == 1 ? keys.iterator().next() : new CompoundKey(keys);
    }

    private PhysicalKey getKey(String name) {
        String key = name == null ? "" : name.toUpperCase(java.util.Locale.ENGLISH);
        if (key.isEmpty()) return PhysicalKey.NONE;
        if (inputMask != null && inputMask.test(key)) return PhysicalKey.NONE;
        PhysicalKey existing = keyboard.get(key);
        if (existing == null) { existing = new PhysicalKey(); keyboard.put(key, existing); }
        return existing;
    }

    /** Called by the visual joystick, backpack and sword controls. */
    public void setAction(String action, boolean down) {
        String resolved = keymap.getOrDefault(action.toUpperCase(java.util.Locale.ENGLISH), action);
        for (String alternative : resolved.split("\\|")) {
            String[] parts = alternative.split("-");
            for (String part : parts) getKey(part).toggle(down);
        }
    }

    public void onAndroidKey(int keyCode, boolean down) {
        String name;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP: name = "UP"; break;
            case KeyEvent.KEYCODE_DPAD_DOWN: name = "DOWN"; break;
            case KeyEvent.KEYCODE_DPAD_LEFT: name = "LEFT"; break;
            case KeyEvent.KEYCODE_DPAD_RIGHT: name = "RIGHT"; break;
            case KeyEvent.KEYCODE_DPAD_CENTER: case KeyEvent.KEYCODE_ENTER: name = "ENTER"; break;
            case KeyEvent.KEYCODE_ESCAPE: case KeyEvent.KEYCODE_BACK: name = "ESCAPE"; break;
            case KeyEvent.KEYCODE_SPACE: name = "SPACE"; break;
            case KeyEvent.KEYCODE_DEL: name = "BACKSPACE"; break;
            default:
                name = KeyEvent.keyCodeToString(keyCode).replace("KEYCODE_", "");
                if (name.startsWith("NUMPAD_")) name = name.substring(7);
        }
        if (down && keyToChange != null && !isModifier(name)) {
            keymap.put(keyToChange, (overwrite ? "" : keymap.get(keyToChange) + "|") + name);
            keyChanged = keyToChange; keyToChange = null; return;
        }
        getKey(name).toggle(down);
    }
    public void onCharacter(char character) { keyTypedBuffer += character; }
    public void changeKeyBinding(String action) { keyToChange = action.toUpperCase(); overwrite = true; }
    public void addKeyBinding(String action) { keyToChange = action.toUpperCase(); overwrite = false; }
    public String[] getKeyPrefs() {
        ArrayList<String> prefs = new ArrayList<>();
        for (Map.Entry<String, String> entry : keymap.entrySet()) prefs.add(entry.getKey() + ";" + entry.getValue());
        return prefs.toArray(new String[0]);
    }
    public ArrayList<String> getAllPressedKeys() {
        ArrayList<String> keys = new ArrayList<>();
        for (Map.Entry<String, PhysicalKey> entry : keyboard.entrySet()) if (entry.getValue().isDown()) keys.add(entry.getKey());
        return keys;
    }
    public String addKeyTyped(String typing, @Nullable String pattern) { return handleBackspaceChars(getKeysTyped(typing, pattern)); }
    public String getKeysTyped(@Nullable String pattern) { return getKeysTyped(null, pattern, true); }
    public String getKeysTyped(@Nullable String typing, @Nullable String pattern) { return getKeysTyped(typing, pattern, false); }
    public String getKeysTyped(@Nullable String typing, @Nullable String pattern, boolean multiline) {
        StringBuilder result = new StringBuilder(typing == null ? "" : typing);
        for (char c : lastKeyTyped.toCharArray()) {
            String value = String.valueOf(c);
            if (c == '\b' || (c >= ' ' && (multiline || c != '\n') && (pattern == null || value.matches(pattern)))) result.append(c);
        }
        lastKeyTyped = "";
        return result.toString();
    }
    public static String handleBackspaceChars(String typing) { return handleBackspaceChars(typing, false); }
    public static String handleBackspaceChars(String typing, boolean keepUnevaluated) {
        Stack<Character> stack = new Stack<>();
        for (char c : typing.toCharArray()) {
            if (c == '\b' && !stack.empty() && stack.peek() != '\b') stack.pop();
            else if (c != '\b' || keepUnevaluated) stack.push(c);
        }
        StringBuilder result = new StringBuilder(); for (Character c : stack) result.append(c); return result.toString();
    }
    public boolean anyControllerConnected() { return false; }
    public boolean buttonPressed(ControllerButton button) { return false; }
    public boolean buttonDown(ControllerButton button) { return false; }
    public ArrayList<ControllerButton> getAllPressedButtons() { return new ArrayList<>(); }
    public boolean inputPressed(String mapping) { return getMappedKey(mapping).isClicked(); }
    public boolean inputDown(String mapping) { return getMappedKey(mapping).isDown(); }
    public boolean controllerVibration(float leftMagnitude, float rightMagnitude, int durationMs) { return false; }
    public boolean leftTriggerPressed() { return false; }
    public boolean rightTriggerPressed() { return false; }

    private static boolean isModifier(String key) { return "SHIFT".equals(key) || "CTRL".equals(key) || "ALT".equals(key); }
    public abstract static class Key { public abstract boolean isDown(); public abstract boolean isClicked(); }
    private static final class PhysicalKey extends Key {
        static final PhysicalKey NONE = new PhysicalKey();
        private boolean down, clicked, pending;
        void toggle(boolean value) { if (value && !down) pending = true; down = value; }
        void tick() { clicked = pending; pending = false; }
        void release() { down = false; clicked = false; pending = false; }
        @Override public boolean isDown() { return down; }
        @Override public boolean isClicked() { return clicked; }
    }
    private static final class CompoundKey extends Key {
        private final Collection<Key> keys; CompoundKey(Collection<Key> keys) { this.keys = keys; }
        @Override public boolean isDown() { for (Key key : keys) if (!key.isDown()) return false; return true; }
        @Override public boolean isClicked() { if (!isDown()) return false; for (Key key : keys) if (key.isClicked()) return true; return false; }
    }
    private static final class OrKey extends Key {
        private final Collection<Key> keys; OrKey(Collection<Key> keys) { this.keys = keys; }
        @Override public boolean isDown() { for (Key key : keys) if (key.isDown()) return true; return false; }
        @Override public boolean isClicked() { for (Key key : keys) if (key.isClicked()) return true; return false; }
    }
}
