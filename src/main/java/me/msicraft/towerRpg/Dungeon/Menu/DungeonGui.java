package me.msicraft.towerRpg.Dungeon.Menu;

import me.msicraft.towerRpg.API.Data.CustomGui;
import me.msicraft.towerRpg.Dungeon.Data.DungeonType;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.TowerRpg;
import me.msicraft.towerRpg.Utils.GuiUtil;
import me.msicraft.towerRpg.Utils.TimeUtil;
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
    private final NamespacedKey dungeonTypeKey;

    public DungeonGui(TowerRpg plugin) {
        this.plugin = plugin;
        this.gui = Bukkit.createInventory(this, 54, Component.text("던전"));

        this.selectKey = new NamespacedKey(plugin, "DungeonGui_Select");
        this.dungeonTypeKey = new NamespacedKey(plugin, "DungeonGui_DungeonType");
    }

    public void setFloorSelectMenu(DungeonType dungeonType, Player player) {
        gui.clear();
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
        int page = (int) playerData.getTempData(dungeonType.getKey() + "_page", 0);
        int guiCount = 0;
        int lastCount = page * 45;

        String pageS = "페이지: " + (page + 1) + "/" + ((maxSize / 45) + 1);
        itemStack = GuiUtil.createItemStack(Material.BOOK, pageS, GuiUtil.EMPTY_LORE, -1,
                dungeonTypeKey, dungeonType.name());
        gui.setItem(49, itemStack);

        for (int i = lastCount; i < maxSize; i++) {
            lore.clear();
            int floor = i + 1;
            String key = dungeonType.getKey() + "_" + floor;
            lore.add(ChatColor.YELLOW + "입장 가능인원수: " + ChatColor.GREEN + dungeonType.getMaxPlayer());
            lore.add("");
            if (playerData.hasData(key)) {
                long lastClearTime = (long) playerData.getData(key);
                lore.add(ChatColor.YELLOW + "클리어 상태: " + ChatColor.GREEN + "O");
                lore.add(ChatColor.YELLOW + "마지막 클리어 날짜: " + ChatColor.GREEN + TimeUtil.getTimeToFormat(lastClearTime));
            } else {
                lore.add(ChatColor.YELLOW + "클리어 상태: " + ChatColor.RED + "X");
                lore.add(ChatColor.YELLOW + "마지막 클리어 날짜: " + ChatColor.GREEN + "X");
            }
            String displayName = dungeonType.getDisplayName() + "-" + floor + "층";
            itemStack = GuiUtil.createItemStack(Material.PAPER, ChatColor.GREEN + displayName,lore, -1,
                    selectKey, String.valueOf(floor));

            gui.setItem(guiCount, itemStack);

            guiCount++;
            if (guiCount >= 45) {
                break;
            }
        }
    }

    public NamespacedKey getSelectKey() {
        return selectKey;
    }

    public NamespacedKey getDungeonTypeKey() {
        return dungeonTypeKey;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }

}
