package me.msicraft.towerRpg.API.MMOItems;

import net.Indyuce.mmoitems.stat.type.DoubleStat;
import org.bukkit.Material;

public class AllAttributes extends DoubleStat {

    public AllAttributes() {
        super("All_Attributes", Material.ENCHANTED_GOLDEN_APPLE, "모든 속성",
                new String[]{"모든 속성 증가"},
                new String[]{"accessory", "off_catalyst", "gem_stone"});
    }

}
