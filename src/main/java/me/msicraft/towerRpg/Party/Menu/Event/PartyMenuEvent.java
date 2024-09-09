package me.msicraft.towerRpg.Party.Menu.Event;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.msicraft.towerRpg.Menu.GuiType;
import me.msicraft.towerRpg.Menu.MenuGui;
import me.msicraft.towerRpg.Party.Data.Party;
import me.msicraft.towerRpg.Party.Data.TempPartyInfo;
import me.msicraft.towerRpg.Party.Menu.PartyGui;
import me.msicraft.towerRpg.Party.PartyManager;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.TowerRpg;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
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
    public void partyInventoryChatEdit(AsyncChatEvent e) {
        Player player = e.getPlayer();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData.hasTempData("TempPartyOption")) {
            String optionName = (String) playerData.getTempData("TempPartyOption", null);
            Party.PartyOptions option = Party.PartyOptions.valueOf(optionName);
            e.setCancelled(true);
            String message = PlainTextComponentSerializer.plainText().serialize(e.message());
            if (message.equalsIgnoreCase("cancel")) {
                playerData.removeTempData("TempPartyOption");
                Bukkit.getScheduler().runTask(plugin, ()-> {
                    plugin.getPartyManager().openPartyInventory(player, PartyGui.Type.CREATE);
                });
                return;
            }
            TempPartyInfo tempPartyInfo = playerData.getTempPartyInfo();
            switch (option) {
                case DISPLAY_NAME -> {
                    int length = message.length();
                    if (length > 10) {
                        player.sendMessage(ChatColor.RED + "파티 이름은 10자리 이하여야합니다.");
                    } else {
                        tempPartyInfo.setPartyOption(Party.PartyOptions.DISPLAY_NAME, message);
                    }
                }
                case PASSWORD -> {
                    int length = message.length();
                    if (length > 10) {
                        player.sendMessage(ChatColor.RED + "비밀번호는 10자리 이하여야합니다.");
                    } else {
                        tempPartyInfo.setPartyOption(Party.PartyOptions.PASSWORD, message);
                    }
                }
            }
            playerData.removeTempData("TempPartyOption");
            Bukkit.getScheduler().runTask(plugin, ()-> {
                plugin.getPartyManager().openPartyInventory(player, PartyGui.Type.CREATE);
            });
        } else if (playerData.hasTempData("PartyOption")) {
            String optionName = (String) playerData.getTempData("PartyOption", null);
            Party.PartyOptions option = Party.PartyOptions.valueOf(optionName);
            e.setCancelled(true);
            String message = PlainTextComponentSerializer.plainText().serialize(e.message());
            if (message.equalsIgnoreCase("cancel")) {
                playerData.removeTempData("PartyOption");
                Bukkit.getScheduler().runTask(plugin, ()-> {
                    plugin.getPartyManager().openPartyInventory(player, PartyGui.Type.EDIT);
                });
                return;
            }
            Party party = playerData.getParty();
            if (party == null) {
                playerData.removeTempData("PartyOption");
                player.sendMessage(ChatColor.RED + "파티가 존재하지 않습니다.");
                return;
            }
            switch (option) {
                case DISPLAY_NAME -> {
                    int length = message.length();
                    if (length > 10) {
                        player.sendMessage(ChatColor.RED + "파티 이름은 10자리 이하여야합니다.");
                    } else {
                        party.setPartyOption(Party.PartyOptions.DISPLAY_NAME, message);
                    }
                }
                case PASSWORD -> {
                    int length = message.length();
                    if (length > 10) {
                        player.sendMessage(ChatColor.RED + "비밀번호는 10자리 이하여야합니다.");
                    } else {
                        party.setPartyOption(Party.PartyOptions.PASSWORD, message);
                    }
                }
            }
            playerData.removeTempData("PartyOption");
            Bukkit.getScheduler().runTask(plugin, ()-> {
                plugin.getPartyManager().openPartyInventory(player, PartyGui.Type.EDIT);
            });
        } else if (playerData.hasTempData("Party_PasswordCheck")) {
            String partyUUIDS = (String) playerData.getTempData("Party_PasswordCheck");
            e.setCancelled(true);
            Party party = plugin.getPartyManager().getParty(UUID.fromString(partyUUIDS));
            if (party == null) {
                player.sendMessage(ChatColor.RED + "해당 파티가 존재하지않습니다.");
                playerData.removeTempData("Party_PasswordCheck");
                Bukkit.getScheduler().runTask(plugin, ()-> {
                    plugin.getPartyManager().openPartyInventory(player, PartyGui.Type.SEARCH);
                });
                return;
            }
            String message = PlainTextComponentSerializer.plainText().serialize(e.message());
            if (message.equalsIgnoreCase("cancel")) {
                playerData.removeTempData("Party_PasswordCheck");
                Bukkit.getScheduler().runTask(plugin, ()-> {
                    plugin.getPartyManager().openPartyInventory(player, PartyGui.Type.SEARCH);
                });
                return;
            }
            String partyPassword = (String) party.getPartyOptionValue(Party.PartyOptions.PASSWORD);
            if (partyPassword.equals(message)) {
                int size = party.getMembers().size();
                int maxSize = (int) party.getPartyOptionValue(Party.PartyOptions.MAX_PLAYER);
                if (size >= maxSize) {
                    player.sendMessage(ChatColor.RED + "파티 인원이 최대입니다.");
                    playerData.removeTempData("Party_PasswordCheck");
                    Bukkit.getScheduler().runTask(plugin, ()-> {
                        plugin.getPartyManager().openPartyInventory(player, PartyGui.Type.SEARCH);
                    });
                } else {
                    party.addPlayer(player);
                    player.sendMessage(ChatColor.GREEN + "파티에 가입되었습니다.");
                    playerData.removeTempData("Party_PasswordCheck");
                    Bukkit.getScheduler().runTask(plugin, ()-> {
                        plugin.getPartyManager().openPartyInventory(player, PartyGui.Type.INFO);
                    });
                }
            } else {
                player.sendMessage(ChatColor.RED + "비밀번호가 일치하지 않습니다.");
                playerData.removeTempData("Party_PasswordCheck");
                Bukkit.getScheduler().runTask(plugin, ()-> {
                    plugin.getPartyManager().openPartyInventory(player, PartyGui.Type.SEARCH);
                });
            }
        }
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
            NamespacedKey infoKey = partyGui.getPartyInfoKey();
            NamespacedKey createKey = partyGui.getCreatePartyKey();
            NamespacedKey editKey = partyGui.getEditPartyOptionsKey();
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
                            partyManager.openPartyInventory(player, PartyGui.Type.SEARCH);
                        }
                        case "Previous" -> {
                            int previous = current - 1;
                            if (previous < 0) {
                                previous = maxPage;
                            }
                            playerData.setTempData("Party_SearchPage", previous);
                            partyManager.openPartyInventory(player, PartyGui.Type.SEARCH);
                        }
                        case "Create" -> {
                            if (playerData.hasParty()) {
                                player.sendMessage(ChatColor.RED + "이미 파티에 속해있습니다.");
                                return;
                            }
                            partyManager.openPartyInventory(player, PartyGui.Type.CREATE);
                        }
                        case "Page" -> {
                            return;
                        }
                        case "Back" -> {
                            MenuGui menuGui = (MenuGui) playerData.getCustomGui(GuiType.MAIN);
                            player.openInventory(menuGui.getInventory());
                        }
                        default -> {
                            if (e.isLeftClick()) {
                                if (playerData.hasParty()) {
                                    player.sendMessage(ChatColor.RED + "이미 파티가 있습니다.");
                                    return;
                                }
                                UUID partyId = UUID.fromString(data);
                                Party party = partyManager.getParty(partyId);
                                if (party == null) {
                                    player.sendMessage(ChatColor.RED + "해당 파티가 존재하지 않습니다.");
                                    return;
                                }
                                boolean isPublicParty = (boolean) party.getPartyOptionValue(Party.PartyOptions.PUBLIC_PARTY);
                                if (isPublicParty) {
                                    int size = party.getMembers().size();
                                    int maxSize = (int) party.getPartyOptionValue(Party.PartyOptions.MAX_PLAYER);
                                    if (size >= maxSize) {
                                        player.sendMessage(ChatColor.RED + "파티 인원이 최대입니다.");
                                    } else {
                                        party.addPlayer(player);
                                        partyManager.openPartyInventory(player, PartyGui.Type.INFO);
                                        player.sendMessage(ChatColor.GREEN + "파티에 가입되었습니다.");
                                    }
                                } else {
                                    boolean usePassword = (boolean) party.getPartyOptionValue(Party.PartyOptions.USE_PASSWORD);
                                    if (usePassword) {
                                        player.sendMessage(ChatColor.GRAY + "========================================");
                                        player.sendMessage(ChatColor.GRAY + "파티 비밀번호를 입력해주세요.");
                                        player.sendMessage(ChatColor.GRAY + "'cancel' 입력시 취소");
                                        player.sendMessage(ChatColor.GRAY + "========================================");
                                        playerData.setTempData("Party_PasswordCheck", data);
                                        player.closeInventory();
                                    } else {
                                        player.sendMessage(ChatColor.RED + "해당 파티는 초대로만 참여가능합니다.");
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (dataContainer.has(infoKey)) {
                String data = dataContainer.get(infoKey, PersistentDataType.STRING);
                if (data != null) {
                    switch (data) {
                        case "BackAndLeave" -> {
                            if (e.isLeftClick()) {
                                partyManager.openPartyInventory(player, PartyGui.Type.SEARCH);
                            } else if (e.isRightClick()) {
                                Party party = playerData.getParty();
                                if (party != null) {
                                    party.removePlayer(player);
                                    player.closeInventory();
                                    player.sendMessage(ChatColor.RED + "파티를 탈퇴했습니다.");
                                } else {
                                    player.sendMessage(ChatColor.RED + "파티가 존재하지 않습니다.");
                                }
                            }
                        }
                        case "PartyInfo" -> {
                            if (e.isLeftClick()) {
                                Party party = playerData.getParty();
                                if (party != null) {
                                    if (party.getLeaderUUID().equals(player.getUniqueId())) {
                                        partyManager.openPartyInventory(player, PartyGui.Type.EDIT);
                                    } else {
                                        player.sendMessage(ChatColor.RED + "파티장만 이용가능합니다.");
                                    }
                                }
                            } else if (e.isRightClick()) {
                                Party party = playerData.getParty();
                                if (party == null) {
                                    player.sendMessage(ChatColor.RED + "파티가 존재하지 않습니다.");
                                    return;
                                }
                                party.disband();
                                player.closeInventory();
                                player.sendMessage(ChatColor.GREEN + "파티를 해체하였습니다.");
                            }
                        }
                        default -> {
                            Party party = playerData.getParty();
                            if (party == null) {
                                player.sendMessage(ChatColor.RED + "파티가 존재하지않습니다.");
                                player.closeInventory();
                                return;
                            }
                            if (!party.getLeaderUUID().equals(player.getUniqueId())) {
                                player.sendMessage(ChatColor.RED + "파티장만 사용가능한 기능입니다.");
                                return;
                            }
                            UUID uuid = UUID.fromString(data);
                            Player targetPlayer = Bukkit.getPlayer(uuid);
                            if (targetPlayer == null) {
                                player.sendMessage(ChatColor.RED + "플레이어가 존재하지 않습니다.");
                                return;
                            }
                            if (e.isLeftClick()) {
                                if (targetPlayer.getUniqueId().equals(party.getLeaderUUID())) {
                                    player.sendMessage(ChatColor.RED + "자기 자신을 추방시킬 수 없습니다.");
                                    return;
                                }
                                party.removePlayer(targetPlayer);
                                targetPlayer.sendMessage(ChatColor.RED + "파티에서 추방당하였습니다.");
                                player.sendMessage(ChatColor.GREEN + "해당 플레이어를 추방하였습니다.");
                            } else if (e.isRightClick()) {
                                if (targetPlayer.getUniqueId().equals(party.getLeaderUUID())) {
                                    player.sendMessage(ChatColor.RED + "자기 자신에게 파티장을 위임할 수 없습니다.");
                                    return;
                                }
                                player.closeInventory();
                                party.setLeader(uuid);
                                targetPlayer.sendMessage(ChatColor.GREEN + "파티장이 되었습니다.");
                            }
                        }
                    }
                }
            } else if (dataContainer.has(createKey)) {
                String data = dataContainer.get(createKey, PersistentDataType.STRING);
                if (data != null) {
                    switch (data) {
                        case "Back" -> {
                            partyManager.openPartyInventory(player, PartyGui.Type.SEARCH);
                        }
                        case "Create" -> {
                            partyManager.createParty(playerData);
                        }
                        default -> {
                            TempPartyInfo tempPartyInfo = playerData.getTempPartyInfo();
                            Party.PartyOptions partyOption = Party.PartyOptions.valueOf(data);
                            boolean u = true;
                            switch (partyOption) {
                                case DISPLAY_NAME -> {
                                    if (e.isLeftClick()) {
                                        player.sendMessage(ChatColor.GRAY + "========================================");
                                        player.sendMessage(ChatColor.GRAY + "파티 이름을 입력해주세요. (최대 10자리)");
                                        player.sendMessage(ChatColor.GRAY + "'cancel' 입력시 취소");
                                        player.sendMessage(ChatColor.GRAY + "========================================");
                                        playerData.setTempData("TempPartyOption", partyOption.name());
                                        player.closeInventory();
                                        u = false;
                                    }
                                }
                                case MAX_PLAYER -> {
                                    int maxPlayer = (int) tempPartyInfo.getPartyOptionValue(Party.PartyOptions.MAX_PLAYER);
                                    if (e.isLeftClick()) {
                                        int cal = maxPlayer + 1;
                                        if (cal > 4) {
                                            cal = 4;
                                        }
                                        tempPartyInfo.setPartyOption(Party.PartyOptions.MAX_PLAYER, cal);
                                    } else if (e.isRightClick()) {
                                        int cal = maxPlayer - 1;
                                        if (cal < 1) {
                                            cal = 1;
                                        }
                                        tempPartyInfo.setPartyOption(Party.PartyOptions.MAX_PLAYER, cal);
                                    }
                                }
                                case FRIENDLY_FIRE -> {
                                    if (e.isLeftClick()) {
                                        if ((boolean) tempPartyInfo.getPartyOptionValue(Party.PartyOptions.FRIENDLY_FIRE)) {
                                            tempPartyInfo.setPartyOption(Party.PartyOptions.FRIENDLY_FIRE, false);
                                        } else {
                                            tempPartyInfo.setPartyOption(Party.PartyOptions.FRIENDLY_FIRE, true);
                                        }
                                    }
                                }
                                case PUBLIC_PARTY -> {
                                    if (e.isLeftClick()) {
                                        if ((boolean) tempPartyInfo.getPartyOptionValue(Party.PartyOptions.PUBLIC_PARTY)) {
                                            tempPartyInfo.setPartyOption(Party.PartyOptions.PUBLIC_PARTY, false);
                                        } else {
                                            tempPartyInfo.setPartyOption(Party.PartyOptions.PUBLIC_PARTY, true);
                                        }
                                    }
                                }
                                case USE_PASSWORD -> {
                                    if (e.isLeftClick()) {
                                        if ((boolean) tempPartyInfo.getPartyOptionValue(Party.PartyOptions.USE_PASSWORD)) {
                                            tempPartyInfo.setPartyOption(Party.PartyOptions.USE_PASSWORD, false);
                                        } else {
                                            tempPartyInfo.setPartyOption(Party.PartyOptions.USE_PASSWORD, true);
                                        }
                                    }
                                }
                                case PASSWORD -> {
                                    if (e.isLeftClick()) {
                                        player.sendMessage(ChatColor.GRAY + "========================================");
                                        player.sendMessage(ChatColor.GRAY + "파티 비밀번호를 입력해주세요. (최대 10자리)");
                                        player.sendMessage(ChatColor.GRAY + "'cancel' 입력시 취소");
                                        player.sendMessage(ChatColor.GRAY + "========================================");
                                        playerData.setTempData("TempPartyOption", partyOption.name());
                                        player.closeInventory();
                                        u = false;
                                    }
                                }
                            }
                            if (u) {
                                partyManager.openPartyInventory(player, PartyGui.Type.CREATE);
                            }
                        }
                    }
                }
            } else if (dataContainer.has(editKey)) {
                String data = dataContainer.get(editKey, PersistentDataType.STRING);
                if (data != null) {
                    switch (data) {
                        case "Back" -> {
                            partyManager.openPartyInventory(player, PartyGui.Type.INFO);
                        }
                        default -> {
                            Party party = playerData.getParty();
                            Party.PartyOptions partyOption = Party.PartyOptions.valueOf(data);
                            boolean u = true;
                            switch (partyOption) {
                                case DISPLAY_NAME -> {
                                    if (e.isLeftClick()) {
                                        player.sendMessage(ChatColor.GRAY + "========================================");
                                        player.sendMessage(ChatColor.GRAY + "파티 이름을 입력해주세요. (최대 10자리)");
                                        player.sendMessage(ChatColor.GRAY + "'cancel' 입력시 취소");
                                        player.sendMessage(ChatColor.GRAY + "========================================");
                                        playerData.setTempData("PartyOption", partyOption.name());
                                        player.closeInventory();
                                        u = false;
                                    }
                                }
                                case MAX_PLAYER -> {
                                    int maxPlayer = (int) party.getPartyOptionValue(Party.PartyOptions.MAX_PLAYER);
                                    if (e.isLeftClick()) {
                                        int cal = maxPlayer + 1;
                                        if (cal > 4) {
                                            cal = 4;
                                        }
                                        party.setPartyOption(Party.PartyOptions.MAX_PLAYER, cal);
                                    } else if (e.isRightClick()) {
                                        int cal = maxPlayer - 1;
                                        if (cal < 1) {
                                            cal = 1;
                                        }
                                        party.setPartyOption(Party.PartyOptions.MAX_PLAYER, cal);
                                    }
                                }
                                case FRIENDLY_FIRE -> {
                                    if (e.isLeftClick()) {
                                        if ((boolean) party.getPartyOptionValue(Party.PartyOptions.FRIENDLY_FIRE)) {
                                            party.setPartyOption(Party.PartyOptions.FRIENDLY_FIRE, false);
                                        } else {
                                            party.setPartyOption(Party.PartyOptions.FRIENDLY_FIRE, true);
                                        }
                                    }
                                }
                                case PUBLIC_PARTY -> {
                                    if (e.isLeftClick()) {
                                        if ((boolean) party.getPartyOptionValue(Party.PartyOptions.PUBLIC_PARTY)) {
                                            party.setPartyOption(Party.PartyOptions.PUBLIC_PARTY, false);
                                        } else {
                                            party.setPartyOption(Party.PartyOptions.PUBLIC_PARTY, true);
                                        }
                                    }
                                }
                                case USE_PASSWORD -> {
                                    if (e.isLeftClick()) {
                                        if ((boolean) party.getPartyOptionValue(Party.PartyOptions.USE_PASSWORD)) {
                                            party.setPartyOption(Party.PartyOptions.USE_PASSWORD, false);
                                        } else {
                                            party.setPartyOption(Party.PartyOptions.USE_PASSWORD, true);
                                        }
                                    }
                                }
                                case PASSWORD -> {
                                    if (e.isLeftClick()) {
                                        player.sendMessage(ChatColor.GRAY + "========================================");
                                        player.sendMessage(ChatColor.GRAY + "파티 비밀번호를 입력해주세요. (최대 10자리)");
                                        player.sendMessage(ChatColor.GRAY + "'cancel' 입력시 취소");
                                        player.sendMessage(ChatColor.GRAY + "========================================");
                                        playerData.setTempData("PartyOption", partyOption.name());
                                        player.closeInventory();
                                        u = false;
                                    }
                                }
                            }
                            if (u) {
                                partyManager.openPartyInventory(player, PartyGui.Type.EDIT);
                            }
                        }
                    }
                }
            }
        }
    }

}
