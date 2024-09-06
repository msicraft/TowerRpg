package me.msicraft.towerRpg.Party.Data;

import me.msicraft.towerRpg.API.Data.Pair;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.TowerRpg;
import net.playavalon.mythicdungeons.api.party.IDungeonParty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Party implements IDungeonParty {

    private final UUID partyID;

    private UUID leader;
    private final List<UUID> members = new ArrayList<>();
    private final Map<PartyOptions, Object> optionMap = new HashMap<>();

    public Party(Player player, TempPartyInfo tempPartyInfo) {
        this.partyID = UUID.randomUUID();
        this.leader = player.getUniqueId();

        for (Pair<PartyOptions, Object> pair : tempPartyInfo.getPartyOptionValueList()) {
            optionMap.put(pair.getV1(), pair.getV2());
        }

        initDungeonParty(TowerRpg.getPlugin());
    }

    public Party(UUID leader, TempPartyInfo tempPartyInfo) {
        this.partyID = UUID.randomUUID();
        this.leader = leader;

        for (Pair<PartyOptions, Object> pair : tempPartyInfo.getPartyOptionValueList()) {
            optionMap.put(pair.getV1(), pair.getV2());
        }

        initDungeonParty(TowerRpg.getPlugin());
    }

    public void setPartyOption(PartyOptions option, Object value) {
        optionMap.put(option, value);
    }

    public Object getPartyOptionValue(PartyOptions option) {
        return optionMap.get(option);
    }

    @Override
    public void addPlayer(Player player) {
        members.add(player.getUniqueId());

        PlayerData playerData = TowerRpg.getPlugin().getPlayerDataManager().getPlayerData(player);
        playerData.setParty(this);
    }

    @Override
    public void removePlayer(Player player) {
        members.remove(player.getUniqueId());

        PlayerData playerData = TowerRpg.getPlugin().getPlayerDataManager().getPlayerData(player);
        playerData.setParty(null);
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for (UUID uuid : members) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            players.add(player);
        }
        return players;

    }

    @Override
    public @NotNull OfflinePlayer getLeader() {
        return Bukkit.getOfflinePlayer(leader);
    }

    public void disband() {
        for (UUID uuid : members) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                PlayerData playerData = TowerRpg.getPlugin().getPlayerDataManager().getPlayerData(player);
                playerData.setParty(null);
            }
        }
        TowerRpg.getPlugin().getPartyManager().removeParty(getPartyID());
    }

    public UUID getPartyID() {
        return partyID;
    }

    public UUID getLeaderUUID() {
        return leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public List<String> partyOptionsToLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "파티 이름: " + ChatColor.GRAY + getPartyOptionValue(PartyOptions.DISPLAY_NAME));
        lore.add(ChatColor.GREEN + "최대 플레이어: " + ChatColor.GRAY + getPartyOptionValue(PartyOptions.MAX_PLAYER));
        if ((boolean) getPartyOptionValue(PartyOptions.FRIENDLY_FIRE)) {
            lore.add(ChatColor.GREEN + "아군 오사: " + ChatColor.GRAY + "O");
        } else {
            lore.add(ChatColor.GREEN + "아군 오사: " + ChatColor.GRAY + "X");
        }
        if ((boolean) getPartyOptionValue(PartyOptions.PUBLIC_PARTY)) {
            lore.add(ChatColor.GREEN + "공개 파티: " + ChatColor.GRAY + "O");
        } else {
            lore.add(ChatColor.GREEN + "공개 파티: " + ChatColor.GRAY + "X");
        }
        if ((boolean) getPartyOptionValue(PartyOptions.USE_PASSWORD)) {
            lore.add(ChatColor.GREEN + "비밀번호 사용여부: " + ChatColor.GRAY + "O");
        } else {
            lore.add(ChatColor.GREEN + "비밀번호 사용여부: " + ChatColor.GRAY + "X");
        }
        String password = (String) getPartyOptionValue(PartyOptions.PASSWORD);
        int size = password.length();
        lore.add(ChatColor.GREEN + "비밀번호: " + ChatColor.GRAY + ("*".repeat(size)) + " (비공개 됨)");

        return lore;
    }

    public enum PartyOptions {
        DISPLAY_NAME("파티 이름", "Unknown"),
        MAX_PLAYER("최대 플레이어", 4),
        FRIENDLY_FIRE("아군 오사", false),
        PUBLIC_PARTY("공개 파티", true),
        USE_PASSWORD("비밀번호 사용여부", false),
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
