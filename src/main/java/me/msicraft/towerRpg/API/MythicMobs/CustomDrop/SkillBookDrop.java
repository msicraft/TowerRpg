package me.msicraft.towerRpg.API.MythicMobs.CustomDrop;

import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import me.msicraft.towerRpg.Utils.GuiUtil;

public class SkillBookDrop implements IItemDrop {

    public SkillBookDrop(MythicLineConfig mlc) {
    }

    @Override
    public AbstractItemStack getDrop(DropMetadata dropMetadata, double v) {
        return new BukkitItemStack(GuiUtil.AIR_STACK);
    }

}
