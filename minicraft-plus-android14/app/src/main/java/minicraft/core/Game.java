package minicraft.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import minicraft.core.io.InputHandler;
import minicraft.entity.mob.Player;
import minicraft.level.Level;
import minicraft.saveload.Version;
import minicraft.screen.Display;
import org.jetbrains.annotations.Nullable;

/** Shared game state retained from the PC edition. AndroidGameRunner owns the platform loop. */
public class Game {
    protected Game() {}
    public static final String NAME = "Minicraft Plus";
    public static final Version VERSION = new Version("2.3.0-infdev3");
    public static InputHandler input;
    public static Player player;
    public static List<String> notifications = new ArrayList<>();
    public static int MAX_FPS = 60;
    static Display currentDisplay;
    static final ArrayDeque<Display> displayQuery = new ArrayDeque<>();
    public static Level[] levels = new Level[6];
    public static int currentLevel = 3;
    public static String gameDir;
    static boolean gameOver;
    static volatile boolean running;
    public static void setDisplay(@Nullable Display display) { if (display == null) displayQuery.clear(); else displayQuery.add(display); }
    public static void exitDisplay() { exitDisplay(1); }
    public static void exitDisplay(int depth) {
        if (depth < 1 || displayQuery.isEmpty()) return;
        for (int i = 0; i < depth && !displayQuery.isEmpty(); i++) { Display parent = displayQuery.peekLast().getParent(); if (parent == null) displayQuery.clear(); else displayQuery.add(parent); }
    }
    @Nullable public static Display getDisplay() { return displayQuery.isEmpty() ? null : displayQuery.peekLast(); }
    public static boolean isMode(String mode) { return ((String) minicraft.core.io.Settings.get("mode")).equalsIgnoreCase(mode); }
    public static void quit() { running = false; }
    /** Desktop launch is deliberately unavailable in the Android package. */
    public static void main(String[] args) { }
}
