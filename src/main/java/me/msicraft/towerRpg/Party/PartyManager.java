package me.msicraft.towerRpg.Party;

import me.msicraft.towerRpg.TowerRpg;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PartyManager {

    private final TowerRpg plugin;

    public PartyManager(TowerRpg plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, Party> partyMap = new HashMap<>();

}
