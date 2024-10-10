package me.msicraft.towerRpg.Dungeon.Menu.Event;

import me.msicraft.towerRpg.Dungeon.Data.DungeonType;
import me.msicraft.towerRpg.Dungeon.DungeonManager;
import me.msicraft.towerRpg.Dungeon.Menu.DungeonGui;
import me.msicraft.towerRpg.Party.Data.Party;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

public class DungeonMenuEvent implements Listener {

    private final TowerRpg plugin;

    public DungeonMenuEvent(TowerRpg plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void DungeonInventoryClick(InventoryClickEvent e) {
        Inventory topInventory = e.getView().getTopInventory();
        if (topInventory.getHolder(false) instanceof DungeonGui dungeonGui) {
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
            DungeonManager dungeonManager = plugin.getDungeonManager();
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            NamespacedKey selectFloorKey = dungeonGui.getSelectKey();
            ItemStack dungeonTypeStack = topInventory.getItem(49);
            if (dungeonTypeStack == null) {
                return;
            }
            if (dungeonTypeStack.getType() == Material.BOOK) {
                ItemMeta dungeonItemMeta = dungeonTypeStack.getItemMeta();
                PersistentDataContainer dungeonDataContainer = dungeonItemMeta.getPersistentDataContainer();
                if (!dungeonDataContainer.has(dungeonGui.getDungeonTypeKey())) {
                    return;
                }
                String dungeonTypeS = dungeonDataContainer.get(dungeonGui.getDungeonTypeKey(), PersistentDataType.STRING);
                DungeonType dungeonType = null;
                try {
                    dungeonType = DungeonType.valueOf(dungeonTypeS);
                } catch (IllegalArgumentException ex) {
                    player.sendMessage(ChatColor.RED + "존재하지 않는 던전입니다.");
                    player.closeInventory();
                    return;
                }
                if (dataContainer.has(selectFloorKey)) {
                    String data = dataContainer.get(selectFloorKey, PersistentDataType.STRING);
                    if (data != null) {
                        String pageKey = dungeonType.getKey() + "_page";
                        int maxPage = dungeonType.getTotalFloor() / 45;
                        int current = (int) playerData.getTempData(pageKey, 1);
                        switch (data) {
                            case "Next" -> {
                                int next = current + 1;
                                if (next > maxPage) {
                                    next = 0;
                                }
                                playerData.setTempData(pageKey, next);
                                dungeonManager.openDungeonInventory(dungeonType, player);
                            }
                            case "Previous" -> {
                                int previous = current - 1;
                                if (previous < 0) {
                                    previous = maxPage;
                                }
                                playerData.setTempData(pageKey, previous);
                                dungeonManager.openDungeonInventory(dungeonType, player);
                            }
                            default -> {
                                if (playerData.hasParty()) {
                                    Party party = playerData.getParty();
                                    if (!party.getLeaderUUID().equals(player.getUniqueId())) {
                                        player.sendMessage(ChatColor.RED + "파티장만 입장신청이 가능합니다.");
                                        return;
                                    }
                                    int maxPlayerSize = dungeonType.getMaxPlayer();
                                    int partySize = party.getMembers().size();
                                    if (partySize > maxPlayerSize) {
                                        player.sendMessage(ChatColor.RED + "파티인원수가 최대 입장가능 인원수보다 많습니다.");
                                        return;
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "파티상태로 입장해주시기 바랍니다.");
                                    return;
                                }
                                int floor = Integer.parseInt(data);
                                String dungeonName = dungeonType.getKey() + "_" + floor;
                                if (floor == 1) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "md play " + dungeonName + " " + player.getName());
                                } else {
                                    String previousDungeonFloorKey = dungeonType.getKey() + "_" + (floor - 1);
                                    if (playerData.canEnterDungeon(previousDungeonFloorKey)) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "md play " + dungeonName + " " + player.getName());
                                    } else {
                                        player.sendMessage(ChatColor.RED + "이전 층을 클리어해야 입장가능합니다.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
