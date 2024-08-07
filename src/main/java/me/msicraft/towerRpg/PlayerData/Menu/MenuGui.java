package me.msicraft.towerRpg.PlayerData.Menu;

import me.msicraft.towerRpg.PlayerData.Data.CustomGui;
import me.msicraft.towerRpg.Utils.GuiUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
                dataTag, "RpgInventory", new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
        gui.setItem(11, itemStack);

        itemStack = GuiUtil.createItemStack(Material.IRON_SWORD, "스탯", GuiUtil.EMPTY_LORE, -1,
                dataTag, "RpgStat", new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
        gui.setItem(12, itemStack);

        itemStack = GuiUtil.createItemStack(Material.BOOK, "스킬", GuiUtil.EMPTY_LORE, -1,
                dataTag, "RpgSkill");
        gui.setItem(13, itemStack);
    }


    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }

}
