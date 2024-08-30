package me.msicraft.towerRpg.Party.Menu;

import me.msicraft.towerRpg.API.Data.CustomGui;
import me.msicraft.towerRpg.TowerRpg;
import me.msicraft.towerRpg.Utils.GuiUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PartyGui extends CustomGui {

    private final Inventory gui;
    private final TowerRpg plugin;

    public PartyGui(TowerRpg plugin) {
        this.plugin = plugin;
        this.gui = Bukkit.createInventory(this, 54, Component.text("파티"));

        this.searchPartyKey = new NamespacedKey(plugin, "SearchParty");
        this.createPartyKey = new NamespacedKey(plugin, "CreateParty");
        this.partyInfoKey = new NamespacedKey(plugin, "PartyInfo");
    }

    private final NamespacedKey searchPartyKey;
    private final NamespacedKey createPartyKey;
    private final NamespacedKey partyInfoKey;

    public void setGui(Player player, int type) { // 0 = 파티 찾기, 1 =  파티 정보, 2 = 파티 생성
        gui.clear();
        switch (type) {
            case 0 -> {
                player.openInventory(getInventory());
                setSearchParty();
            }
            case 1 -> {
                player.openInventory(getInventory());
                setPartyInfo(player);
            }
            case 2-> {
                player.openInventory(getInventory());
                setCreateParty(player);
            }
        }
    }

    private void setSearchParty(){
        ItemStack itemStack;
        itemStack = GuiUtil.createItemStack(Material.ARROW, "다음 페이지", GuiUtil.EMPTY_LORE, -1,
                searchPartyKey, "Next");
        gui.setItem(48, itemStack);
        itemStack = GuiUtil.createItemStack(Material.ARROW, "이전 페이지", GuiUtil.EMPTY_LORE, -1,
                searchPartyKey, "Previous");
        gui.setItem(50, itemStack);
    }

    private void setCreateParty(Player player) {
        ItemStack itemStack;
        itemStack = GuiUtil.createItemStack(Material.ARROW, "다음 페이지", GuiUtil.EMPTY_LORE, -1,
                createPartyKey, "Next");
        gui.setItem(48, itemStack);
        itemStack = GuiUtil.createItemStack(Material.ARROW, "이전 페이지", GuiUtil.EMPTY_LORE, -1,
                createPartyKey, "Previous");
        gui.setItem(50, itemStack);
    }

    private void setPartyInfo(Player player) {
        ItemStack itemStack;
        itemStack = GuiUtil.createItemStack(Material.ARROW, "다음 페이지", GuiUtil.EMPTY_LORE, -1,
                partyInfoKey, "Next");
        gui.setItem(48, itemStack);
        itemStack = GuiUtil.createItemStack(Material.ARROW, "이전 페이지", GuiUtil.EMPTY_LORE, -1,
                partyInfoKey, "Previous");
        gui.setItem(50, itemStack);
    }

    public NamespacedKey getSearchPartyKey() {
        return searchPartyKey;
    }

    public NamespacedKey getCreatePartyKey() {
        return createPartyKey;
    }

    public NamespacedKey getPartyInfoKey() {
        return partyInfoKey;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }

}
