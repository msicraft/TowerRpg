package me.msicraft.towerRpg.Prefix;

import me.msicraft.towerRpg.Prefix.Data.StatValueType;

public class PrefixStat {

    private final String statName;
    private final StatValueType statValueType;
    private final double value;

    public PrefixStat(String statName, StatValueType statValueType, double value) {
        this.statName = statName;
        this.statValueType = statValueType;
        this.value = value;
    }

    public String getStatName() {
        return statName;
    }

    public double getValue() {
        return value;
    }

    public StatValueType getStatValueType() {
        return statValueType;
    }

}
