package me.msicraft.towerRpg.SkillBook;

import me.msicraft.towerRpg.SkillBook.Data.SkillBook;
import me.msicraft.towerRpg.TowerRpg;
import net.Indyuce.mmocore.MMOCore;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SkillBookManager {

    private final TowerRpg plugin;

    public SkillBookManager(TowerRpg plugin) {
        this.plugin = plugin;
    }

    private final Map<String, SkillBook> skillBookMap = new HashMap<>();

    public void reloadVariables() {
        MMOCore.plugin.skillManager.getAll().forEach(registeredSkill -> {
            String id = registeredSkill.getHandler().getId();
            SkillBook skillBook;
            if (skillBookMap.containsKey(id)) {
                skillBook = skillBookMap.get(id);
                skillBook.update(registeredSkill);
            } else {
                skillBook = new SkillBook(registeredSkill);
            }
            skillBookMap.put(id, skillBook);
        });
    }

    public SkillBook getSkillBook(String id) {
        if (skillBookMap.containsKey(id)) {
            return skillBookMap.get(id);
        }
        return null;
    }

    public boolean isSkillBook(ItemStack itemStack) {
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            return dataContainer.has(SkillBook.SKILL_BOOK_ID_KEY, PersistentDataType.STRING);
        }
        return false;
    }

    public String getSkillBookId(ItemStack itemStack) {
        if (isSkillBook(itemStack)) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            return dataContainer.get(SkillBook.SKILL_BOOK_ID_KEY, PersistentDataType.STRING);
        }
        return null;
    }

    public List<String> getSkillIdsToList() {
        return List.copyOf(skillBookMap.keySet());
    }

    public Set<String> getSkillIdsToSet() {
        return skillBookMap.keySet();
    }

}
