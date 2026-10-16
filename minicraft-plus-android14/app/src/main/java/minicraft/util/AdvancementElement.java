package minicraft.util;

import minicraft.item.Inventory;
import minicraft.item.Item;
import minicraft.item.Recipe;
import minicraft.level.tile.Tile;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Mobile-compatible progress facade. Core play is independent of desktop
 * advancement-data parsing; recipe lists are available directly on Android.
 */
public class AdvancementElement {
    public final String key;
    public final String description;
    protected final HashMap<String, ElementCriterion> criteria = new HashMap<>();
    protected final HashSet<HashSet<String>> requirements = new HashSet<>();
    protected final HashMap<String, ElementCriterion> unlockingCriteria = new HashMap<>();
    protected final HashSet<HashSet<String>> unlockingRequirements = new HashSet<>();
    protected final @Nullable ElementRewards rewards;
    protected boolean completed;
    protected boolean unlocked = true;

    public AdvancementElement(String key, String description, Map<String, ElementCriterion> criteria,
                              @Nullable ElementRewards rewards, Set<HashSet<String>> requirements,
                              Map<String, ElementCriterion> unlockingCriteria,
                              Set<HashSet<String>> unlockingRequirements) {
        this.key = key;
        this.description = description;
        this.criteria.putAll(criteria);
        this.rewards = rewards;
        this.requirements.addAll(requirements);
        this.unlockingCriteria.putAll(unlockingCriteria);
        this.unlockingRequirements.addAll(unlockingRequirements);
    }

    public static HashSet<AdvancementElement> loadAdvancementFile(String filename, boolean displayable) throws IOException {
        return new HashSet<>();
    }

    public static void loadAdvancementElement(Collection<AdvancementElement> elements, String key, JSONObject json, boolean displayable) {
    }

    public static ElementRewards loadRewards(JSONObject json) {
        return new ElementRewards(new ArrayList<>(), new ArrayList<>());
    }

    public static void resetRecipeUnlockingElements() {
    }

    public static void loadRecipeUnlockingElements(JSONObject json) {
    }

    public static void saveRecipeUnlockingElements(JSONObject json) {
    }

    public void markAsCompleted(boolean inLoad) {
        completed = true;
    }

    public void deregisterCriteria() {
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public boolean isDisplayableAtStatus() {
        return unlocked && !completed;
    }

    public int getNumCriteriaCompleted() {
        return completed ? criteria.size() : 0;
    }

    public int getTotalNumCriteria() {
        return criteria.size();
    }

    public boolean shouldAllCriteriaBeCompleted() {
        return requirements.isEmpty();
    }

    protected boolean isUnlockable() {
        return true;
    }

    protected boolean checkIsCompleted() {
        return completed;
    }

    public void update() {
    }

    public void reset() {
        completed = false;
        unlocked = true;
    }

    public void load(JSONObject json) {
    }

    public void save(JSONObject json) {
    }

    public static class ElementCriterion {
        public ElementCriterion(String trigger, JSONObject conditions) {
        }
    }

    public static class ElementRewards {
        private final ArrayList<Item> items;
        private final ArrayList<Recipe> recipes;

        public ElementRewards(ArrayList<Item> items, ArrayList<Recipe> recipes) {
            this.items = new ArrayList<>(items);
            this.recipes = new ArrayList<>(recipes);
        }

        public ArrayList<Item> getItems() {
            return new ArrayList<>(items);
        }

        public ArrayList<Recipe> getRecipe() {
            return new ArrayList<>(recipes);
        }
    }

    public static class AdvancementTrigger {
        public static void tick() {
        }

        public static class InventoryChangedTrigger {
            public static final InventoryChangedTrigger INSTANCE = new InventoryChangedTrigger();
            public void trigger(Object conditions) {
            }
            public static class InventoryChangedTriggerConditionHandler {
                public static class InventoryChangedTriggerConditions {
                    public InventoryChangedTriggerConditions(Inventory inventory) {
                    }
                }
            }
        }

        public static class PlacedTileTrigger {
            public static final PlacedTileTrigger INSTANCE = new PlacedTileTrigger();
            public void trigger(Object conditions) {
            }
            public static class PlacedTileTriggerConditionHandler {
                public static class PlacedTileTriggerConditions {
                    public PlacedTileTriggerConditions(Item item, Tile tile, int data, int x, int y, int level) {
                    }
                }
            }
        }

        public static class ItemUsedOnTileTrigger {
            public static final ItemUsedOnTileTrigger INSTANCE = new ItemUsedOnTileTrigger();
            public void trigger(Object conditions) {
            }
            public static class ItemUsedOnTileTriggerConditionHandler {
                public static class ItemUsedOnTileTriggerConditions {
                    public ItemUsedOnTileTriggerConditions(Item item, Tile tile, int data, int x, int y, int level) {
                    }
                }
            }
        }
    }
}
