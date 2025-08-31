package io.github.NoOne.nMLQuivers;

import io.github.NoOne.nMLItems.ItemRarity;
import io.github.NoOne.nMLItems.ItemStat;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static io.github.NoOne.nMLItems.ItemRarity.COMMON;
import static io.github.NoOne.nMLItems.ItemStat.*;
import static io.github.NoOne.nMLItems.ItemType.*;

public class QuiverGenerator {
    private NMLQuivers nmlQuivers;

    public QuiverGenerator(NMLQuivers nmlQuivers) {
        this.nmlQuivers = nmlQuivers;
    }

    public ItemStack generateQuiver(Player receiver, ItemRarity rarity, int level) {
        ItemStack quiver = new ItemStack(ItemType.getItemTypeMaterial(QUIVER));
        ItemMeta meta = quiver.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        List<String> lore = new ArrayList<>();

        pdc.set(ItemSystem.makeItemTypeKey(QUIVER), PersistentDataType.INTEGER, 1);
        pdc.set(ItemSystem.makeItemRarityKey(rarity), PersistentDataType.INTEGER, 1);
        pdc.set(ItemSystem.getLevelKey(), PersistentDataType.INTEGER, level);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        quiver.setItemMeta(meta);

        String name = ItemSystem.generateItemName(QUIVER, null, rarity);
        meta.setDisplayName(name);
        pdc.set(ItemSystem.getOriginalNameKey(), PersistentDataType.STRING, name);

        lore.add("§o§fLv. " + level + "§r " + ItemRarity.getItemRarityColor(rarity) + ChatColor.BOLD + ItemRarity.getItemRarityString(rarity).toUpperCase() + " " + ItemType.getItemTypeString(QUIVER).toUpperCase());
        lore.add("");
        meta.setLore(lore);
        quiver.setItemMeta(meta);

        generateQuiverStats(quiver, rarity, level);
        ItemSystem.updateUnusableItemName(quiver, ItemSystem.isItemUsable(quiver, receiver));

        return quiver;
    }

    public void generateQuiverStats(ItemStack quiver, ItemRarity rarity, int level) {
        generateDamage(quiver, rarity, level);
        generateSecondaryStats(quiver, rarity, level);
    }

    private void generateDamage(ItemStack weapon, ItemRarity rarity, int level) {
        List<ItemStat> possibleFirstStats = new ArrayList<>(List.of(PHYSICALDAMAGE, FIREDAMAGE, COLDDAMAGE, EARTHDAMAGE, LIGHTNINGDAMAGE, AIRDAMAGE, LIGHTDAMAGE,
                                                                    DARKDAMAGE, PUREDAMAGE, CRITCHANCE, CRITDAMAGE));
        List<ItemStat> possibleSecondStats = new ArrayList<>(List.of(PHYSICALDAMAGE, FIREDAMAGE, COLDDAMAGE, EARTHDAMAGE, LIGHTNINGDAMAGE, AIRDAMAGE, LIGHTDAMAGE,
                                                                    DARKDAMAGE, PUREDAMAGE, CRITCHANCE, CRITDAMAGE));

        ItemStat firstStat = possibleFirstStats.get(ThreadLocalRandom.current().nextInt(possibleFirstStats.size()));
        int firstStatValue = level * 2;
        ItemStat secondStat = possibleSecondStats.get(ThreadLocalRandom.current().nextInt(possibleSecondStats.size()));
        int secondStatValue = level;

        switch (rarity) {
            case COMMON -> {
                ItemSystem.setStat(weapon, firstStat, firstStatValue);
            }
            case UNCOMMON, RARE -> {
                if (firstStat == secondStat) {
                    ItemSystem.setStat(weapon, firstStat, firstStatValue + secondStatValue);
                } else {
                    ItemSystem.setStat(weapon, firstStat, firstStatValue);
                    ItemSystem.setStat(weapon, secondStat, secondStatValue);
                }
            }
            case MYTHICAL -> {
                firstStatValue = level * 3;

                if (firstStat == secondStat) {
                    ItemSystem.setStat(weapon, firstStat, firstStatValue + secondStatValue);
                } else {
                    ItemSystem.setStat(weapon, firstStat, firstStatValue);
                    ItemSystem.setStat(weapon, secondStat, secondStatValue);
                }
            }
        }

        ItemSystem.updateLoreWithStats(weapon);
    }

    private void generateSecondaryStats(ItemStack quiver, ItemRarity rarity, int level) {
        HashMap<ItemStat, Integer> statMap = new HashMap<>();
        statMap.put(CRITCHANCE, level * 2);
        statMap.put(CRITDAMAGE, level * 10);

        // divider
        if (rarity != COMMON) {
            ItemMeta meta = quiver.getItemMeta();
            List<String> addedLore = meta.getLore();

            addedLore.add("§7─────────────");
            meta.setLore(addedLore);
            quiver.setItemMeta(meta);
        }

        // generate stat rolls
        List<Map.Entry<ItemStat, Integer>> statEntries = new ArrayList<>(statMap.entrySet());
        HashMap<ItemStat, Integer> selectedStats = new HashMap<>();
        int rolls = 0;

        switch (rarity) {
            case UNCOMMON -> rolls = 1;
            case RARE -> rolls = 2;
            case MYTHICAL -> rolls = 4;
        }

        for (int i = 0; i < rolls; i++) {
            Map.Entry<ItemStat, Integer> randomEntry = statEntries.get(new Random().nextInt(statEntries.size()));
            ItemStat randomItemStat = randomEntry.getKey();
            int randomStatValue = randomEntry.getValue();

            selectedStats.merge(randomItemStat, randomStatValue, Integer::sum);
        }

        // update stats
        for (Map.Entry<ItemStat, Integer> selectedStatEntry : selectedStats.entrySet()) {
            ItemSystem.setStat(quiver, selectedStatEntry.getKey(), selectedStatEntry.getValue());
            ItemSystem.updateLoreWithStat(quiver, selectedStatEntry.getKey(), selectedStatEntry.getValue());
        }
    }
}
