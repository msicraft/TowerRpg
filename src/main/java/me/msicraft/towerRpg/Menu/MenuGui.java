package me.msicraft.towerRpg.Menu;

import me.msicraft.towerRpg.API.Data.CustomGui;
import me.msicraft.towerRpg.Utils.GuiUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuGui extends CustomGui {

    private final Inventory gui;

    public MenuGui() {
        this.gui = Bukkit.createInventory(this, 54, Component.text("메인 메뉴"));
        setMain();
    }

    public void setMain() {
        String dataTag = "MenuGui_Main";
        ItemStack itemStack;
        itemStack = GuiUtil.createItemStack(Material.IRON_CHESTPLATE, "장비 인벤토리", GuiUtil.EMPTY_LORE, -1,
                dataTag, "RpgInventory");
        gui.setItem(10, itemStack);

        itemStack = GuiUtil.createItemStack(Material.IRON_SWORD, "스탯", GuiUtil.EMPTY_LORE, -1,
                dataTag, "RpgStat");
        gui.setItem(11, itemStack);

        itemStack = GuiUtil.createItemStack(Material.BOOK, "스킬", GuiUtil.EMPTY_LORE, -1,
                dataTag, "RpgSkill");
        gui.setItem(12, itemStack);

        itemStack = GuiUtil.createItemStack(Material.CHEST, "상점", GuiUtil.EMPTY_LORE, -1,
                dataTag, "ShopInventory");
        gui.setItem(19, itemStack);
        itemStack = GuiUtil.createItemStack(Material.PLAYER_HEAD, ChatColor.WHITE + "파티",
                List.of(ChatColor.YELLOW + "좌 클릭: 피티 찾기", ChatColor.YELLOW + "우 클릭: 파티 정보"), -1,
                dataTag, "Party");
        gui.setItem(20, itemStack);
        itemStack = GuiUtil.createItemStack(Material.BARRIER, ChatColor.WHITE + "임시 (던전)", GuiUtil.EMPTY_LORE, -1,
                dataTag, "Dungeon");
        gui.setItem(21, itemStack);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }

}
