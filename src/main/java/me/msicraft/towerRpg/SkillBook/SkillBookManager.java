package me.msicraft.towerRpg.SkillBook;

import me.msicraft.towerRpg.SkillBook.Data.SkillBook;
import me.msicraft.towerRpg.TowerRpg;
import net.Indyuce.mmocore.MMOCore;

import java.util.HashMap;
import java.util.Map;

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

}
