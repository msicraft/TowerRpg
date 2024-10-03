package me.msicraft.towerRpg.DisableItem;

import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DisableItemManger {

    private final TowerRpg plugin;

    public DisableItemManger(TowerRpg plugin) {
        this.plugin = plugin;
    }

    private final Set<Material> disableMaterialList = new HashSet<>();

    public void reloadVariables() {
        disableMaterialList.clear();
        List<String> materialS = plugin.getConfig().getStringList("DisableItem");
        for (String s : materialS) {
            Material material = Material.getMaterial(s);
            if (material != null) {
                disableMaterialList.add(material);
            } else {
                Bukkit.getConsoleSender().sendMessage(TowerRpg.PREFIX + ChatColor.RED + "Invalid material: " + s);
            }
        }
    }

    public boolean isDisableMaterial(ItemStack itemStack) {
        if (itemStack != null) {
            return isDisableMaterial(itemStack.getType());
        }
        return false;
    }

    public boolean isDisableMaterial(Material material) {
        return disableMaterialList.contains(material);
    }

}
