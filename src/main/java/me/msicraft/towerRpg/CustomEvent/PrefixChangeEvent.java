package me.msicraft.towerRpg.CustomEvent;

import me.msicraft.towerRpg.Prefix.Data.Prefix;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PrefixChangeEvent extends Event {

    private final static HandlerList handlers = new HandlerList();

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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
