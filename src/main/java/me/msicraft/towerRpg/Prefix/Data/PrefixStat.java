package me.msicraft.towerRpg.Prefix.Data;

public class PrefixStat {

    private final String statName;
    private final Prefix.StatValueType statValueType;
    private final double value;

    public PrefixStat(String statName, Prefix.StatValueType statValueType, double value) {
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

    public Prefix.StatValueType getStatValueType() {
        return statValueType;
    }

}
