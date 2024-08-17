package me.msicraft.towerRpg.PlayerData.Data;

import me.msicraft.towerRpg.Prefix.Data.Prefix;

import java.util.ArrayList;
import java.util.List;

public class PlayerPrefix {

    private Prefix applyPrefix = null;

    private final List<Prefix> prefixList = new ArrayList<>();

    public PlayerPrefix() {
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
