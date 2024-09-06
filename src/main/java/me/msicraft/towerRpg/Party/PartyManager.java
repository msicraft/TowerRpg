package me.msicraft.towerRpg.Party;

import me.msicraft.towerRpg.API.Data.CustomGuiManager;
import me.msicraft.towerRpg.Menu.GuiType;
import me.msicraft.towerRpg.Party.Data.Party;
import me.msicraft.towerRpg.Party.Menu.PartyGui;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.entity.Player;

import java.util.*;

public class PartyManager extends CustomGuiManager {

    private final TowerRpg plugin;

    public PartyManager(TowerRpg plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, Party> partyMap = new LinkedHashMap<>();

    public void openPartyInventory(Player player, int type) { // 0 = 파티 찾기, 1 =  파티 정보, 2 = 파티 생성, 3 = 파티 옵션 변경
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        PartyGui partyGui = (PartyGui) playerData.getCustomGui(GuiType.PARTY);
        player.openInventory(partyGui.getInventory());
        partyGui.setGui(player, type);
    }

    public Party getParty(UUID partyId) {
        return partyMap.getOrDefault(partyId, null);
    }

    public void addParty(UUID partyId, Party party) {
        partyMap.put(partyId, party);
    }

    public void addParty(Party party) {
        addParty(party.getPartyID(), party);
    }

    public void removeParty(UUID partyId) {
        partyMap.remove(partyId);
    }

    public Set<UUID> getPartyIdKeySet() {
        return partyMap.keySet();
    }

    public List<UUID> getPartyIdsToList() {
        return List.copyOf(getPartyIdKeySet());
    }

}
