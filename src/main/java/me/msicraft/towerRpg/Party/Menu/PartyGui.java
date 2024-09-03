package me.msicraft.towerRpg.Party.Menu;

import me.msicraft.towerRpg.API.Data.CustomGui;
import me.msicraft.towerRpg.Party.Data.Party;
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
    }

    private final NamespacedKey searchPartyKey;
    private final NamespacedKey createPartyKey;
    private final NamespacedKey partyInfoKey;

    public void setGui(Player player, int type) { // 0 = 파티 찾기, 1 =  파티 정보, 2 = 파티 생성
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
            case 2-> {
                player.openInventory(getInventory());
                setCreateParty(player);
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

    private void setCreateParty(Player player) {
        ItemStack itemStack;
        itemStack = GuiUtil.createItemStack(Material.BARRIER, "뒤로", GuiUtil.EMPTY_LORE, -1,
                createPartyKey, "Back");
        gui.setItem(45, itemStack);
        itemStack = GuiUtil.createItemStack(Material.ARROW, "파티 생성", GuiUtil.EMPTY_LORE, -1,
                createPartyKey, "Create");
        gui.setItem(54, itemStack);
    }

    private void setPartyInfo(Player player) {
        ItemStack itemStack;
        itemStack = GuiUtil.createItemStack(Material.ARROW, "다음 페이지", GuiUtil.EMPTY_LORE, -1,
                partyInfoKey, "Next");
        gui.setItem(48, itemStack);
        itemStack = GuiUtil.createItemStack(Material.ARROW, "이전 페이지", GuiUtil.EMPTY_LORE, -1,
                partyInfoKey, "Previous");
        gui.setItem(50, itemStack);
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

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }

}
