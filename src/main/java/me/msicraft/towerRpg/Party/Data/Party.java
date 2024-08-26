package me.msicraft.towerRpg.Party.Data;

import org.bukkit.entity.Player;

import java.util.*;

public class Party {

    private final UUID id;

    private UUID leader;
    private final Set<UUID> members = new HashSet<>();
    private final Map<PartyOptions, Object> optionMap = new HashMap<>();

    private Party(UUID id, UUID leader) {
        this.id = id;
        this.leader = leader;
        for (PartyOptions option : PartyOptions.values()) {
            optionMap.put(option, option.getBaseValue());
        }
    }

    public static Party createParty(UUID leader) {
        return new Party(UUID.randomUUID(), leader);
    }

    public static Party createParty(Player leader) {
        return createParty(leader.getUniqueId());
    }

    public Object getPartyOptionValue(PartyOptions option) {
        return optionMap.getOrDefault(option, option.getBaseValue());
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

    public enum PartyOptions {
        DISPLAY_NAME("파티 이름", "Unknown"),
        MAX_PLAYER("최대 플레이어", 4),
        FRIENDLY_FIRE("아군 오사", false),
        PARTY_TYPE("공개 여부", true),
        PASSWORD("비밀번호", "12345");

        private final String displayName;
        private final Object baseValue;

        PartyOptions(String displayName, Object baseValue) {
            this.displayName = displayName;
            this.baseValue = baseValue;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Object getBaseValue() {
            return baseValue;
        }
    }

}
