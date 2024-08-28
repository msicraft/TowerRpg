package me.msicraft.towerRpg.Party.Menu;

import me.msicraft.towerRpg.API.Data.CustomGui;
import me.msicraft.towerRpg.TowerRpg;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
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
    }

    private final NamespacedKey searchPartyKey;
    private final NamespacedKey createPartyKey;

    private void setSearchParty(){
        ItemStack itemStack;
    }

    private void setCreateParty() {
    }

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }

}
