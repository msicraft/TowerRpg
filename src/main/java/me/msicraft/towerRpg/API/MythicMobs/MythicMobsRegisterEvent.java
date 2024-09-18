package me.msicraft.towerRpg.API.MythicMobs;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.events.MythicDropLoadEvent;
import me.msicraft.towerRpg.API.MythicMobs.CustomDrop.SkillBookDrop;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MythicMobsRegisterEvent implements Listener {

    private final TowerRpg plugin;

    public MythicMobsRegisterEvent(TowerRpg plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void registerDrop(MythicDropLoadEvent e) {
        MythicLineConfig mlc = e.getConfig();
        if (e.getDropName().equalsIgnoreCase("skillbook")) {
            e.register(new SkillBookDrop(mlc));
        }
    }

}
