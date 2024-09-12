package me.msicraft.towerRpg.Prefix.Data;

import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Prefix {

    private final String id;
    private String displayName = "Unknown";

    private final Set<PrefixStat> stats = new HashSet<>();

    public Prefix(String id, String displayName, List<String> statList) {
        this.id = id;
        update(displayName, statList);
    }

    public void update(String displayName, List<String> statFormatList) {
        stats.clear();
        if (displayName != null) {
            this.displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        } else {
            this.displayName = ChatColor.GRAY + "Unknown";
        }
        for (String format : statFormatList) {
            String[] a = format.split(":");
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

    public enum StatValueType {
        FLAT, MULTIPLY
    }

}
