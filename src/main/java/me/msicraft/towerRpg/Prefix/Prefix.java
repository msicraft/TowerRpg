package me.msicraft.towerRpg.Prefix;

import me.msicraft.towerRpg.Prefix.Data.StatValueType;
import me.msicraft.towerRpg.Prefix.File.PrefixDataFile;
import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Prefix {

    private final String id;
    private String displayName = "[Unknown]";

    private final Set<PrefixStat> stats = new HashSet<>();

    public Prefix(String id, String displayName, List<String> statList) {
        this.id = id;
        if (displayName != null) {
            this.displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        }

        for (String statFormat : statList) {
            String[] a = statFormat.split(":");
            String statName = a[0];
            StatValueType statValueType = StatValueType.valueOf(a[1].toUpperCase());
            double value = Double.parseDouble(a[2]);

            PrefixStat prefixStat = new PrefixStat(statName, statValueType, value);
            stats.add(prefixStat);
        }
    }

    public void updatePrefixStat(PrefixDataFile prefixDataFile) {
        List<String> statList = prefixDataFile.getConfig().getStringList("Prefix." + id + ".Stat");
        stats.clear();

        for (String statFormat : statList) {
            String[] a = statFormat.split(":");
            String statName = a[0];
            StatValueType statValueType = StatValueType.valueOf(a[1].toUpperCase());
            double value = Double.parseDouble(a[2]);

            PrefixStat prefixStat = new PrefixStat(statName, statValueType, value);
            stats.add(prefixStat);
        }
    }

    public String getId() {
        return id;
    }

    public Set<PrefixStat> getStats() {
        return stats;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
