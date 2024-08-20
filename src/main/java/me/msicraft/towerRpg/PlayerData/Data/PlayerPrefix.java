package me.msicraft.towerRpg.PlayerData.Data;

import me.msicraft.towerRpg.Prefix.Data.Prefix;
import me.msicraft.towerRpg.Prefix.PrefixManager;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class PlayerPrefix {

    private final PlayerData playerData;

    private Prefix applyPrefix = null;

    private final List<Prefix> prefixList = new ArrayList<>();

    public PlayerPrefix(PlayerData playerData) {
        this.playerData = playerData;
    }

    public void loadData() {
        FileConfiguration config = playerData.getPlayerDataFile().getConfig();
        PrefixManager prefixManager = TowerRpg.getPlugin().getPrefixManager();

        String applyPrefixId = config.getString("Prefix.ApplyPrefix");
        Prefix prefix = prefixManager.getPrefix(applyPrefixId);
        prefixManager.applyPrefix(playerData.getPlayer(), prefix);

        List<String> prefixList = config.getStringList("Prefix.List");
        for (String id : prefixList) {
            Prefix prefix1 = prefixManager.getPrefix(id);
            if (prefix1 != null) {
                addPrefix(prefix1);
            }
        }
    }

    public void saveData() {
        FileConfiguration config = playerData.getPlayerDataFile().getConfig();

        Prefix applyPrefix = getPrefix();
        if (applyPrefix != null) {
            config.set("Prefix.ApplyPrefix", applyPrefix.getId());
        } else {
            config.set("Prefix.ApplyPrefix", null);
        }

        List<String> prefixIdList = new ArrayList<>();
        for (Prefix prefix : getPrefixList()) {
            prefixIdList.add(prefix.getId());
        }
        config.set("Prefix.List", prefixIdList);
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public Prefix getPrefix() {
        return applyPrefix;
    }

    public void setPrefix(Prefix prefix) {
        this.applyPrefix = prefix;
    }

    public void addPrefix(Prefix prefix) {
        prefixList.add(prefix);
    }

    public void removePrefix(Prefix prefix) {
        prefixList.remove(prefix);
    }

    public List<Prefix> getPrefixList() {
        return prefixList;
    }

}
