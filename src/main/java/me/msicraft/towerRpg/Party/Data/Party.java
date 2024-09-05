package me.msicraft.towerRpg.Party.Data;

import me.msicraft.towerRpg.API.Data.Pair;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.TowerRpg;
import net.playavalon.mythicdungeons.api.party.IDungeonParty;
import org.bukkit.Bukkit;
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

    public Object getPartyOptionValue(PartyOptions option) {
        return optionMap.getOrDefault(option, option.getBaseValue());
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

    public enum PartyOptions {
        DISPLAY_NAME("파티 이름", "Unknown", "DisplayName"),
        MAX_PLAYER("최대 플레이어", 4, "MaxPlayer"),
        FRIENDLY_FIRE("아군 오사", false, "FriendlyFire"),
        PUBLIC_PARTY("공개 파티", true, "PublicParty"),
        USE_PASSWORD("비밀번호 사용 여부", false, "UsePassword"),
        PASSWORD("비밀번호", "12345", "Password");

        private final String displayName;
        private final Object baseValue;
        private final String key;

        PartyOptions(String displayName, Object baseValue, String key) {
            this.displayName = displayName;
            this.baseValue = baseValue;
            this.key = key;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Object getBaseValue() {
            return baseValue;
        }

        public String getKey() {
            return key;
        }

    }

}
