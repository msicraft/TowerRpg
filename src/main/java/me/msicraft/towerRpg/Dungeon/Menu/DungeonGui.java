package me.msicraft.towerRpg.Dungeon.Menu;

import me.msicraft.towerRpg.Dungeon.Data.DungeonType;
import me.msicraft.towerRpg.Menu.Data.CustomGui;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.TowerRpg;
import me.msicraft.towerRpg.Utils.GuiUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DungeonGui extends CustomGui {

    private final Inventory gui;
    private final TowerRpg plugin;

    public DungeonGui(TowerRpg plugin) {
        this.plugin = plugin;
        this.gui = Bukkit.createInventory(this, 54, Component.text("던전"));
    }

    public void setFloorSelectMenu(DungeonType dungeonType, Player player) {
        String dataTag = "DungeonGui_Select";
        ItemStack itemStack;
        itemStack = GuiUtil.createItemStack(Material.ARROW, "다음 페이지", GuiUtil.EMPTY_LORE, -1, dataTag, "Next");
        gui.setItem(48, itemStack);
        itemStack = GuiUtil.createItemStack(Material.ARROW, "이전 페이지", GuiUtil.EMPTY_LORE, -1, dataTag, "Previous");
        gui.setItem(50, itemStack);

        PlayerData playerData = TowerRpg.getPlugin().getPlayerDataManager().getPlayerData(player);
        switch (dungeonType) {
            case BEGINNING_TOWER -> {
            }
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }

}
