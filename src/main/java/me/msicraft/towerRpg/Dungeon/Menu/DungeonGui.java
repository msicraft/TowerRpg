package me.msicraft.towerRpg.Dungeon.Menu;

import me.msicraft.towerRpg.API.Data.CustomGui;
import me.msicraft.towerRpg.Dungeon.Data.DungeonType;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.TowerRpg;
import me.msicraft.towerRpg.Utils.GuiUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DungeonGui extends CustomGui {

    private final Inventory gui;
    private final TowerRpg plugin;

    private final NamespacedKey selectKey;

    public DungeonGui(TowerRpg plugin) {
        this.plugin = plugin;
        this.gui = Bukkit.createInventory(this, 54, Component.text("던전"));

        this.selectKey = new NamespacedKey(plugin, "DungeonGui_Select");
    }

    public void setFloorSelectMenu(DungeonType dungeonType, Player player) {
        ItemStack itemStack;
        itemStack = GuiUtil.createItemStack(Material.ARROW, "다음 페이지", GuiUtil.EMPTY_LORE, -1,
                selectKey, "Next");
        gui.setItem(48, itemStack);
        itemStack = GuiUtil.createItemStack(Material.ARROW, "이전 페이지", GuiUtil.EMPTY_LORE, -1,
                selectKey, "Previous");
        gui.setItem(50, itemStack);

        PlayerData playerData = TowerRpg.getPlugin().getPlayerDataManager().getPlayerData(player);

        List<String> lore = new ArrayList<>();
        int maxSize = dungeonType.getTotalFloor();
        int page = (int) playerData.getTempData(dungeonType.getKey() + "_page", 1);
        int guiCount = 0;
        int lastCount = page * 45;
        for (int i = 0; i < maxSize; i++) {
            if (i < lastCount) {
                continue;
            }
            lore.clear();
            int floor = i + 1;
            String key = dungeonType.getKey() + "_" + floor;
            boolean isClear = playerData.hasTag(key);
            if (isClear) {
                lore.add(ChatColor.GREEN + "클리어 상태: O");
            } else {
                lore.add(ChatColor.GREEN + "클리어 상태: " + ChatColor.RED + " X");
            }
            String displayName = dungeonType.getDisplayName() + " - " + floor + " 층";
            itemStack = GuiUtil.createItemStack(Material.PAPER, ChatColor.GREEN + displayName,lore, -1,
                    selectKey, String.valueOf(floor));
            gui.setItem(guiCount, itemStack);

            guiCount++;
            if (guiCount >= 45) {
                break;
            }
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }

}
