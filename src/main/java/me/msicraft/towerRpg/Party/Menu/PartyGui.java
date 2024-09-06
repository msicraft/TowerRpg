package me.msicraft.towerRpg.Party.Menu;

import me.msicraft.towerRpg.API.Data.CustomGui;
import me.msicraft.towerRpg.Party.Data.Party;
import me.msicraft.towerRpg.Party.Data.TempPartyInfo;
import me.msicraft.towerRpg.Party.PartyManager;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.TowerRpg;
import me.msicraft.towerRpg.Utils.GuiUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyGui extends CustomGui {

    private final Inventory gui;
    private final TowerRpg plugin;

    public PartyGui(TowerRpg plugin) {
        this.plugin = plugin;
        this.gui = Bukkit.createInventory(this, 54, Component.text("파티"));

        this.searchPartyKey = new NamespacedKey(plugin, "SearchParty");
        this.createPartyKey = new NamespacedKey(plugin, "CreateParty");
        this.partyInfoKey = new NamespacedKey(plugin, "PartyInfo");
        this.editPartyOptionsKey = new NamespacedKey(plugin, "PartyOptionsEdit");
    }

    private final NamespacedKey searchPartyKey;
    private final NamespacedKey createPartyKey;
    private final NamespacedKey partyInfoKey;
    private final NamespacedKey editPartyOptionsKey;

    public void setGui(Player player, int type) { // 0 = 파티 찾기, 1 =  파티 정보, 2 = 파티 생성, 3 = 파티 옵션 변경
        gui.clear();
        switch (type) {
            case 0 -> {
                player.openInventory(getInventory());
                setSearchParty(player);
            }
            case 1 -> {
                player.openInventory(getInventory());
                setPartyInfo(player);
            }
            case 2 -> {
                player.openInventory(getInventory());
                setCreateParty(player);
            }
            case 3 -> {
                player.openInventory(getInventory());
                setEditPartyOption(player);
            }
        }
    }

    private void setSearchParty(Player player){
        ItemStack itemStack;
        itemStack = GuiUtil.createItemStack(Material.ARROW, "다음 페이지", GuiUtil.EMPTY_LORE, -1,
                searchPartyKey, "Next");
        gui.setItem(48, itemStack);
        itemStack = GuiUtil.createItemStack(Material.ARROW, "이전 페이지", GuiUtil.EMPTY_LORE, -1,
                searchPartyKey, "Previous");
        gui.setItem(50, itemStack);
        itemStack = GuiUtil.createItemStack(Material.WRITTEN_BOOK, "파티 생성", GuiUtil.EMPTY_LORE, -1,
                searchPartyKey, "Create");
        gui.setItem(45, itemStack);

        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        PartyManager partyManager = plugin.getPartyManager();
        List<UUID> partyIdList = partyManager.getPartyIdsToList();
        int maxSize = partyIdList.size();
        int page = (int) playerData.getTempData("Party_SearchPage",0);
        int guiCount = 0;
        int lastCount = page * 45;

        String pageS = "페이지: " + (page + 1) + "/" + ((maxSize / 45) + 1);
        itemStack = GuiUtil.createItemStack(Material.BOOK, pageS, GuiUtil.EMPTY_LORE, -1,
                searchPartyKey, "Page");
        gui.setItem(49, itemStack);

        List<Component> lore = new ArrayList<>();
        for (int a = lastCount; a <maxSize; a++) {
            UUID partyId = partyIdList.get(a);
            Party party = partyManager.getParty(partyId);
            if (party != null) {
                lore.clear();
                itemStack = new ItemStack(Material.PAPER);
                ItemMeta itemMeta = itemStack.getItemMeta();
                PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
                itemMeta.displayName(Component.text(ChatColor.GREEN + (String) party.getPartyOptionValue(Party.PartyOptions.DISPLAY_NAME)));
                lore.add(Component.text(ChatColor.YELLOW + "좌 클릭: 파티 참가(신청)"));
                boolean isPublicParty = (boolean) party.getPartyOptionValue(Party.PartyOptions.PUBLIC_PARTY);
                if (isPublicParty) {
                    lore.add(Component.text(ChatColor.GRAY + "공개 파티"));
                } else {
                    lore.add(Component.text(ChatColor.GRAY + "비공개 파티"));
                }
                lore.add(Component.text(""));
                lore.add(Component.text(ChatColor.GRAY + "파티장: " + Bukkit.getPlayer(party.getLeaderUUID())));
                lore.add(Component.text(ChatColor.GRAY + "파티 인원: "
                        + party.getMembers().size() + "/" + party.getPartyOptionValue(Party.PartyOptions.MAX_PLAYER)));
                itemMeta.lore(lore);

                dataContainer.set(searchPartyKey, PersistentDataType.STRING, partyId.toString());

                itemStack.setItemMeta(itemMeta);
                gui.setItem(guiCount, itemStack);
                guiCount++;
                if (guiCount >= 45) {
                    break;
                }
            }
        }
    }

    private final int[] optionSlots = new int[]{19,20,21,22,23,24,25, 28,29,30,31,32,33,34, 37,38,39,40,41,42,43};

    private void setCreateParty(Player player) {
        ItemStack itemStack;
        itemStack = GuiUtil.createItemStack(Material.BARRIER, "뒤로", GuiUtil.EMPTY_LORE, -1,
                createPartyKey, "Back");
        gui.setItem(45, itemStack);
        itemStack = GuiUtil.createItemStack(Material.ARROW, "파티 생성", GuiUtil.EMPTY_LORE, -1,
                createPartyKey, "Create");
        gui.setItem(54, itemStack);

        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        TempPartyInfo tempPartyInfo = playerData.getTempPartyInfo();
        Party.PartyOptions[] partyOptions = Party.PartyOptions.values();

        int count = 0;
        List<Component> lore = new ArrayList<>();
        for (Party.PartyOptions options : partyOptions) {
            lore.clear();
            ItemStack optionStack = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = optionStack.getItemMeta();
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            itemMeta.displayName(Component.text(options.getDisplayName()));
            Object value = tempPartyInfo.getPartyOptionValue(options);
            switch (options) {
                case DISPLAY_NAME -> {
                    lore.add(Component.text(ChatColor.YELLOW + "좌 클릭: 값 변경"));
                    lore.add(Component.text(""));
                    lore.add(Component.text(ChatColor.GRAY + "파티 이름: " + value));
                }
                case MAX_PLAYER -> {
                    lore.add(Component.text(ChatColor.YELLOW + "좌 클릭 : +1"));
                    lore.add(Component.text(ChatColor.YELLOW + "우 클릭 : -1"));
                    lore.add(Component.text(""));
                    lore.add(Component.text(ChatColor.GRAY + "최대 플레이어: " + value));
                }
                case FRIENDLY_FIRE -> {
                    lore.add(Component.text(ChatColor.YELLOW + "좌 클릭: 값 변경"));
                    lore.add(Component.text(""));
                    if ((boolean) value) {
                        lore.add(Component.text(ChatColor.GRAY + "아군 오사: O"));
                    } else {
                        lore.add(Component.text(ChatColor.GRAY + "아군 오사: X"));
                    }
                }
                case PUBLIC_PARTY -> {
                    lore.add(Component.text(ChatColor.YELLOW + "좌 클릭: 값 변경"));
                    lore.add(Component.text(""));
                    if ((boolean) value) {
                        lore.add(Component.text(ChatColor.GRAY + "공개 파티: O"));
                    } else {
                        lore.add(Component.text(ChatColor.GRAY + "공개 파티: X"));
                    }
                }
                case USE_PASSWORD -> {
                    lore.add(Component.text(ChatColor.YELLOW + "좌 클릭: 값 변경"));
                    lore.add(Component.text(""));
                    if ((boolean) value) {
                        lore.add(Component.text(ChatColor.GRAY + "비밀번호 사용여부: O"));
                    } else {
                        lore.add(Component.text(ChatColor.GRAY + "비밀번호 사용여부: X"));
                    }
                }
                case PASSWORD -> {
                    lore.add(Component.text(ChatColor.YELLOW + "좌 클릭: 값 변경"));
                    lore.add(Component.text(""));
                    lore.add(Component.text(ChatColor.GRAY + "비밀번호: " + value));
                }
            }
            itemMeta.lore(lore);
            dataContainer.set(createPartyKey, PersistentDataType.STRING, options.name());

            itemStack.setItemMeta(itemMeta);
            gui.setItem(optionSlots[count], itemStack);
            count++;
        }
    }

    private final int[] playerSlots = new int[]{
                                                19,20,21,22,23,24,25,
                                                28,29,30,31,32,33,34,
                                                37,38,39,40,41,42,43}; //10,11,12,13,14,15,16,

    private void setPartyInfo(Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        Party party = playerData.getParty();
        if (party == null) {
            player.sendMessage(ChatColor.RED + "파티에 속해있지않습니다.");
            plugin.getPartyManager().openPartyInventory(player, 0);
            return;
        }
        ItemStack itemStack;
        itemStack = GuiUtil.createItemStack(Material.BARRIER, "뒤로(좌) | 탈퇴(우)",
                List.of(ChatColor.YELLOW + "좌 클릭: 뒤로", ChatColor.YELLOW + "우 클릭: 탈퇴"), -1,
                partyInfoKey, "BackAndLeave");
        gui.setItem(45, itemStack);

        List<String> infoLore = new ArrayList<>();
        infoLore.add(ChatColor.YELLOW + "좌 클릭: 파티 옵션 변경 (파티장 기능)");
        infoLore.add("");
        infoLore.addAll(party.partyOptionsToLore());
        itemStack = GuiUtil.createItemStack(Material.BOOK, "파티 정보", infoLore, -1, partyInfoKey, "PartyInfo");
        gui.setItem(5, itemStack);

        int maxSize = playerSlots.length;
        int count = 0;
        List<UUID> members = party.getMembers();
        List<Component> lore = new ArrayList<>();
        for (UUID member : members) {
            Player partyPlayer = Bukkit.getPlayer(member);
            if (partyPlayer != null) {
                lore.clear();
                ItemStack partySlot = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skullMeta = (SkullMeta) partySlot.getItemMeta();
                PersistentDataContainer dataContainer = skullMeta.getPersistentDataContainer();
                lore.add(Component.text(ChatColor.YELLOW + "좌 클릭: 파티원 추방 (파티장 기능)"));
                lore.add(Component.text(ChatColor.YELLOW + "우 클릭: 파티장 위임 (파티장 가능)"));
                lore.add(Component.text(""));
                if (party.getLeaderUUID() == member) {
                    lore.add(Component.text(ChatColor.GREEN + "파티장"));
                } else {
                    lore.add(Component.text(ChatColor.GREEN + "파티원"));
                }
                skullMeta.displayName(Component.text(ChatColor.GREEN + partyPlayer.getName()));
                skullMeta.lore(lore);
                skullMeta.setOwningPlayer(partyPlayer);
                dataContainer.set(partyInfoKey, PersistentDataType.STRING, member.toString());

                partySlot.setItemMeta(skullMeta);
                gui.setItem(playerSlots[count], partySlot);
                count++;
                if (count >= maxSize) {
                    break;
                }
            }
        }
    }

    private void setEditPartyOption(Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        Party party = playerData.getParty();
        if (party == null) {
            player.sendMessage(ChatColor.RED + "파티에 속해있지않습니다.");
            plugin.getPartyManager().openPartyInventory(player, 0);
            return;
        }
        ItemStack itemStack;
        itemStack = GuiUtil.createItemStack(Material.BARRIER, "뒤로", GuiUtil.EMPTY_LORE, -1, editPartyOptionsKey, "Back");
        gui.setItem(45, itemStack);

        int count = 0;
        Party.PartyOptions[] partyOptions = Party.PartyOptions.values();
        List<Component> lore = new ArrayList<>();
        for (Party.PartyOptions options : partyOptions) {
            lore.clear();
            itemStack = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = itemStack.getItemMeta();
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            itemMeta.displayName(Component.text(options.getDisplayName()));
            Object value = party.getPartyOptionValue(options);
            switch (options) {
                case DISPLAY_NAME -> {
                    lore.add(Component.text(ChatColor.YELLOW + "좌 클릭: 값 변경"));
                    lore.add(Component.text(""));
                    lore.add(Component.text(ChatColor.GRAY + "파티 이름: " + value));
                }
                case MAX_PLAYER -> {
                    lore.add(Component.text(ChatColor.YELLOW + "좌 클릭 : +1"));
                    lore.add(Component.text(ChatColor.YELLOW + "우 클릭 : -1"));
                    lore.add(Component.text(""));
                    lore.add(Component.text(ChatColor.GRAY + "최대 플레이어: " + value));
                }
                case FRIENDLY_FIRE -> {
                    lore.add(Component.text(ChatColor.YELLOW + "좌 클릭: 값 변경"));
                    lore.add(Component.text(""));
                    if ((boolean) value) {
                        lore.add(Component.text(ChatColor.GRAY + "아군 오사: O"));
                    } else {
                        lore.add(Component.text(ChatColor.GRAY + "아군 오사: X"));
                    }
                }
                case PUBLIC_PARTY -> {
                    lore.add(Component.text(ChatColor.YELLOW + "좌 클릭: 값 변경"));
                    lore.add(Component.text(""));
                    if ((boolean) value) {
                        lore.add(Component.text(ChatColor.GRAY + "공개 파티: O"));
                    } else {
                        lore.add(Component.text(ChatColor.GRAY + "공개 파티: X"));
                    }
                }
                case USE_PASSWORD -> {
                    lore.add(Component.text(ChatColor.YELLOW + "좌 클릭: 값 변경"));
                    lore.add(Component.text(""));
                    if ((boolean) value) {
                        lore.add(Component.text(ChatColor.GRAY + "비밀번호 사용여부: O"));
                    } else {
                        lore.add(Component.text(ChatColor.GRAY + "비밀번호 사용여부: X"));
                    }
                }
                case PASSWORD -> {
                    lore.add(Component.text(ChatColor.YELLOW + "좌 클릭: 값 변경"));
                    lore.add(Component.text(""));
                    lore.add(Component.text(ChatColor.GRAY + "비밀번호: " + value));
                }
            }
            itemMeta.lore(lore);
            dataContainer.set(editPartyOptionsKey, PersistentDataType.STRING, options.name());

            itemStack.setItemMeta(itemMeta);
            gui.setItem(optionSlots[count], itemStack);
            count++;
        }
    }

    public NamespacedKey getSearchPartyKey() {
        return searchPartyKey;
    }

    public NamespacedKey getCreatePartyKey() {
        return createPartyKey;
    }

    public NamespacedKey getPartyInfoKey() {
        return partyInfoKey;
    }

    public NamespacedKey getEditPartyOptionsKey() {
        return editPartyOptionsKey;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }

}
