package me.msicraft.towerRpg.Party;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Party {

    private final UUID id;
    private String displayName = "Unknown";

    private UUID leader;
    private final Set<UUID> members = new HashSet<>();

    public Party(UUID id, UUID leader, String displayName) {
        this.id = id;
        this.leader = leader;
        this.displayName = displayName;
    }

    public Party(UUID id, UUID leader) {
        this.id = id;
        this.leader = leader;
    }

    public UUID getId() {
        return id;
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(Player player) {
        setLeader(player.getUniqueId());
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void addPlayer(Player player) {
        addPlayer(player.getUniqueId());
    }

    public void addPlayer(UUID uuid) {
        members.add(uuid);
    }

    public void removePlayer(Player player) {
        removePlayer(player.getUniqueId());
    }

    public void removePlayer(UUID uuid) {
        members.remove(uuid);
    }

    public boolean isPartyMember(Player player) {
        return isPartyMember(player.getUniqueId());
    }

    public boolean isPartyMember(UUID uuid) {
        return members.contains(uuid);
    }

    public Set<UUID> getMemberUUIDs() {
        return members;
    }

}
