package me.msicraft.towerRpg.Party.Menu.Event;

import me.msicraft.towerRpg.Party.Data.Party;
import me.msicraft.towerRpg.Party.Menu.PartyGui;
import me.msicraft.towerRpg.Party.PartyManager;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class PartyMenuEvent implements Listener {

    private final TowerRpg plugin;

    public PartyMenuEvent(TowerRpg plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void partyMenuClickEvent(InventoryClickEvent e) {
        Inventory topInventory = e.getView().getTopInventory();
        if (topInventory.getHolder(false) instanceof PartyGui partyGui) {
            ClickType type = e.getClick();
            if (type == ClickType.NUMBER_KEY || type == ClickType.SWAP_OFFHAND
                    || type == ClickType.SHIFT_LEFT || type == ClickType.SHIFT_RIGHT) {
                e.setCancelled(true);
                return;
            }
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            ItemStack itemStack = e.getCurrentItem();
            if (itemStack == null) {
                return;
            }
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) {
                return;
            }
            PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
            PartyManager partyManager = plugin.getPartyManager();
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            NamespacedKey searchKey = partyGui.getSearchPartyKey();
            if (dataContainer.has(searchKey)) {
                String data = dataContainer.get(searchKey, PersistentDataType.STRING);
                if (data != null) {
                    int maxPage = partyManager.getPartyIdKeySet().size() / 45;
                    int current = (int) playerData.getTempData("Party_SearchPage", 1);
                    switch (data) {
                        case "Next" -> {
                            int next = current + 1;
                            if (next > maxPage) {
                                next = 0;
                            }
                            playerData.setTempData("Party_SearchPage", next);
                            partyManager.openPartyInventory(player, 0);
                        }
                        case "Previous" -> {
                            int previous = current - 1;
                            if (previous < 0) {
                                previous = maxPage;
                            }
                            playerData.setTempData("Party_SearchPage", previous);
                            partyManager.openPartyInventory(player, 0);
                        }
                        case "Create" -> {
                            partyManager.openPartyInventory(player, 2);
                        }
                        default -> {
                            if (e.isLeftClick()) {
                                if (playerData.hasParty()) {
                                    player.sendMessage(ChatColor.RED + "이미 파티가 있습니다.");
                                    return;
                                }
                                UUID partyId = UUID.fromString(data);
                                Party party = partyManager.getParty(partyId);
                                if (party != null) {
                                    boolean isPublicParty = (boolean) party.getPartyOptionValue(Party.PartyOptions.PUBLIC_PARTY);
                                    if (isPublicParty) {
                                        int size = party.getMembers().size();
                                        int maxSize = (int) party.getPartyOptionValue(Party.PartyOptions.MAX_PLAYER);
                                        if (size >= maxSize) {
                                            player.sendMessage(ChatColor.RED + "파티 인원이 최대입니다.");
                                        } else {
                                            party.addPlayer(player);
                                            partyManager.openPartyInventory(player, 1);
                                        }
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "해당 파티가 존재하지 않습니다.");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
