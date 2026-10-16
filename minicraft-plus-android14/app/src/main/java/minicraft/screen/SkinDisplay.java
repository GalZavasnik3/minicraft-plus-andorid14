package minicraft.screen;

import minicraft.core.Game;
import minicraft.core.Renderer;
import minicraft.core.io.InputHandler;
import minicraft.entity.mob.Mob;
import minicraft.gfx.Color;
import minicraft.gfx.Font;
import minicraft.gfx.MinicraftImage;
import minicraft.gfx.Point;
import minicraft.gfx.Screen;
import minicraft.gfx.SpriteLinker.LinkedSprite;
import minicraft.screen.entry.ListEntry;
import minicraft.screen.entry.SelectEntry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Android skin selector. It keeps the four stock Minicraft+ skins from the
 * original sprite sheet; desktop folder watching is intentionally omitted.
 */
public class SkinDisplay extends Display {
    private static final LinkedHashMap<String, LinkedSprite[][]> skins = new LinkedHashMap<>();
    private static final ArrayList<String> defaultSkins = new ArrayList<>();
    private static String selectedSkin = "minicraft.skin.paul";
    private int step;

    static {
        defaultSkins.add("minicraft.skin.paul");
        defaultSkins.add("minicraft.skin.paul_cape");
        defaultSkins.add("minicraft.skin.minecraft_steve");
        defaultSkins.add("minicraft.skin.minecraft_alex");
    }

    public SkinDisplay() {
        super(true, true,
            new Menu.Builder(false, 2, RelPos.CENTER)
                .setDisplayLength(8)
                .setSelectable(true)
                .setPositioning(new Point(Screen.w / 2, Screen.h * 3 / 5), RelPos.CENTER)
                .createMenu());
        refreshSkins();
        List<ListEntry> entries = new ArrayList<>();
        for (String skin : skins.keySet()) entries.add(new SelectEntry(skin, this::confirmExit));
        menus[0].setEntries(entries);
        menus[0].setSelection(Math.max(0, new ArrayList<>(skins.keySet()).indexOf(selectedSkin)));
    }

    public static synchronized void refreshSkins() {
        Renderer.spriteLinker.clearSkins();
        skins.clear();
        MinicraftImage sheet = Renderer.loadDefaultSkinSheet();
        for (int i = 0; i < defaultSkins.size(); i++) {
            String skin = defaultSkins.get(i);
            Renderer.spriteLinker.setSkin("skin." + skin, sheet);
            skins.put(skin, Mob.compileMobSpriteAnimations(0, i * 4, "skin." + skin));
        }
    }

    /** Retained for save/load callers; all mobile skins are built in. */
    public static void releaseSkins() {
    }

    private void confirmExit() {
        selectedSkin = new ArrayList<>(skins.keySet()).get(menus[0].getSelection());
        if (Game.player != null) Game.player.updateSprites();
        Game.exitDisplay();
    }

    @Override
    public void tick(InputHandler input) {
        super.tick(input);
    }

    @Override
    public void render(Screen screen) {
        super.render(screen);
        step++;
        Font.drawCentered("Skin", screen, 16, Color.WHITE);
        if (!skins.isEmpty()) {
            LinkedSprite[][] frames = new ArrayList<>(skins.values()).get(menus[0].getSelection());
            LinkedSprite sprite = frames[(step / 40) % 4][(step / 20) % 2];
            screen.render(Screen.w / 2 - 8, 40, sprite);
        }
    }

    public static String getSelectedSkin() {
        return selectedSkin;
    }

    public static void setSelectedSkin(String skin) {
        if (defaultSkins.contains(skin)) selectedSkin = skin;
    }

    @NotNull
    public static LinkedSprite[][][] getSkinAsMobSprite() {
        refreshSkins();
        int index = Math.max(0, defaultSkins.indexOf(selectedSkin));
        return new LinkedSprite[][][] {
            Mob.compileMobSpriteAnimations(0, index * 4, "skin." + defaultSkins.get(index)),
            Mob.compileMobSpriteAnimations(0, index * 4 + 2, "skin." + defaultSkins.get(index))
        };
    }
}
