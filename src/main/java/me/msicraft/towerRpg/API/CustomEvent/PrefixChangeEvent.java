package me.msicraft.towerRpg.API.CustomEvent;

import me.msicraft.towerRpg.Prefix.Data.Prefix;
import org.bukkit.entity.Player;

public class PrefixChangeEvent extends TowerRpgEvent {

    private final Player player;
    private final Prefix prefix;

    public PrefixChangeEvent(Player player, Prefix prefix) {
        this.player = player;
        this.prefix = prefix;
    }

    public Player getPlayer() {
        return player;
    }

    public Prefix getPrefix() {
        return prefix;
    }

}
