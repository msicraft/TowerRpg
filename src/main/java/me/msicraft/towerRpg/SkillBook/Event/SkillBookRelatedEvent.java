package me.msicraft.towerRpg.SkillBook.Event;

import me.msicraft.towerRpg.SkillBook.Data.SkillBook;
import me.msicraft.towerRpg.SkillBook.SkillBookManager;
import me.msicraft.towerRpg.TowerRpg;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.Indyuce.mmocore.skill.RegisteredSkill;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SkillBookRelatedEvent implements Listener {

    private final TowerRpg plugin;

    public SkillBookRelatedEvent(TowerRpg plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void skillBookInteractEvent(PlayerInteractEvent e) {
        SkillBookManager skillBookManager = plugin.getSkillBookManager();
        ItemStack itemStack = e.getItem();
        if (skillBookManager.isSkillBook(itemStack)) {
            Action action = e.getAction();
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                Player player = e.getPlayer();
                String skillBookId = skillBookManager.getSkillBookId(itemStack);
                SkillBook skillBook = skillBookManager.getSkillBook(skillBookId);
                PlayerData mPlayerData = PlayerData.get(player.getUniqueId());
                PlayerClass playerClass = mPlayerData.getProfess();
                RegisteredSkill registeredSkill = skillBook.getRegisteredSkill();
                if (playerClass.hasSkill(registeredSkill)) {
                    ClassSkill classSkill = playerClass.getSkill(registeredSkill);
                    int currentLevel = mPlayerData.getSkillLevel(registeredSkill);
                    int maxLevel = classSkill.getMaxLevel();
                    if (currentLevel >= maxLevel) {
                        player.sendMessage(ChatColor.RED + "해당 스킬은 더이상 레벨을 올릴 수 없습니다.");
                        return;
                    }
                    int cal = currentLevel + 1;
                    mPlayerData.setSkillLevel(registeredSkill, cal);
                    player.sendMessage(ChatColor.YELLOW + registeredSkill.getName() + ChatColor.GREEN + " 의 스킬레벨이 상승하였습니다.");
                } else {
                    mPlayerData.setSkillLevel(registeredSkill, 1);
                    player.sendMessage(ChatColor.YELLOW + registeredSkill.getName() + ChatColor.GREEN + " 스킬을 배웠습니다.");
                }
                itemStack.setAmount(0);
            }
        }
    }

}
