package minicraft.core;

import android.content.Context;
import minicraft.android.AndroidAssets;
import minicraft.android.GameSurface;
import minicraft.core.io.FileHandler;
import minicraft.core.io.InputHandler;
import minicraft.level.biome.Biomes;
import minicraft.level.tile.Tiles;
import minicraft.saveload.Load;
import minicraft.screen.ResourcePackDisplay;
import minicraft.screen.TitleDisplay;

/** Android-owned 60 Hz loop that calls the original Minicraft+ simulation unchanged. */
public final class AndroidGameRunner {
    private static final int MAX_CATCH_UP_TICKS = 4;
    private static final long FRAME_NANOS = 1_000_000_000L / 60;
    private AndroidGameRunner() {}
    public static void start(Context context, GameSurface surface) {
        AndroidAssets.initialize(context); Renderer.bindSurface(surface); FileHandler.determineGameDir(null);
        Game.input = new InputHandler(); Game.MAX_FPS = 60;
        ResourcePackDisplay.initPacks(); ResourcePackDisplay.reloadResources();
        new Load(true, false); // Restores size and other preferences before worlds are opened.
        Tiles.initTileList(); Biomes.initBiomeList(); Renderer.initScreen();
        World.resetGame(); if (Game.player != null) Game.player.eid = 0;
        Game.setDisplay(new TitleDisplay()); Game.running = true;
        Thread loop = new Thread(AndroidGameRunner::runLoop, "Minicraft game loop"); loop.setDaemon(true); loop.start();
    }
    private static void runLoop() {
        long previous = System.nanoTime(), timer = System.currentTimeMillis(); double carry = 0; int frames = 0, ticks = 0;
        while (Game.running) {
            long frameStart = System.nanoTime();
            long now = System.nanoTime(); double ticksPerSecond = Updater.normSpeed * (Game.currentDisplay == null ? Updater.gamespeed : 1);
            carry += (now - previous) * ticksPerSecond / 1_000_000_000d; previous = now;
            int catchUpTicks = 0;
            while (carry >= 1 && catchUpTicks < MAX_CATCH_UP_TICKS) { Updater.tick(); carry--; ticks++; catchUpTicks++; }
            // Avoid a long recovery loop after a slow frame. Dropping excess catch-up work
            // keeps a brief hiccup from turning into several seconds of unresponsive gameplay.
            if (carry >= 1) carry = 0;
            Renderer.render(); frames++;
            if (System.currentTimeMillis() - timer >= 1000) { Initializer.fra = frames; Initializer.tik = ticks; frames = ticks = 0; timer += 1000; }
            long remainingMillis = (FRAME_NANOS - (System.nanoTime() - frameStart)) / 1_000_000L;
            try {
                if (remainingMillis > 0) Thread.sleep(remainingMillis);
                else Thread.yield();
            } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); Game.quit(); }
        }
    }
}
