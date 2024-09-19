package me.msicraft.towerRpg.API.MythicMobs.CustomDrop;

import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import me.msicraft.towerRpg.SkillBook.Data.SkillBook;
import me.msicraft.towerRpg.SkillBook.SkillBookManager;
import me.msicraft.towerRpg.TowerRpg;
import me.msicraft.towerRpg.Utils.GuiUtil;

public class SkillBookDrop implements IItemDrop {

    private final String skillBookId;

    public SkillBookDrop(MythicLineConfig mlc) {
        this.skillBookId = mlc.getString(new String[]{"skill", "s"}, null);
    }

    @Override
    public AbstractItemStack getDrop(DropMetadata dropMetadata, double v) {
        if (skillBookId != null) {
            SkillBookManager skillBookManager = TowerRpg.getPlugin().getSkillBookManager();
            SkillBook skillBook = skillBookManager.getSkillBook(skillBookId.toUpperCase());
            if (skillBook != null) {
                return new BukkitItemStack(skillBook.getItemStack());
            }
        }
        return new BukkitItemStack(GuiUtil.AIR_STACK);
    }

}
